package edu.columbia.lrl.experiments.topology_radix.topogen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import edu.columbia.lrl.experiments.topology_radix.HPCtopologyAnalysis.HPCDesignPoint;
import edu.columbia.lrl.experiments.topology_radix.locality.AbstractNormalisedTrafficVectorGenerator;

import javancox.topogen.AbstractTopologyGenerator;

import ch.epfl.general_libraries.math.MoreMaths;
import ch.epfl.general_libraries.statistics.StatisticalDistribution;
import ch.epfl.general_libraries.utils.MoreArrays;
import ch.epfl.general_libraries.utils.SimpleMap;
import ch.epfl.javancox.inputs.topology.AbstractDeterministicGenerator;
import ch.epfl.javancox.inputs.topology.GeneralizedTorus;
import ch.epfl.javancox.inputs.topology.NDimFlattenedButterfly;

public class DimensionBasedGenerator extends
		AnalysableTopologyGenerator implements Iterable<TopologyDescription> {
	


	protected int totalDesiredClients = -1;
	protected int desiredSwitches;
	protected int radix;
	protected int dimensions;	
	protected int[] possibleDimensions;
	int[] width = null;	
	
	boolean init = false;
	protected AbstractTopologyGenerator generator;
	
	public enum TYPE {
		TORUS,
		FBUT
	}
	
	TYPE type;	
	
	public DimensionBasedGenerator(TYPE t, int[] possibleDimensions) {
		this.type = t;
		this.possibleDimensions = possibleDimensions;
	}
	
	protected void init() {
		width = GeneralizedTorus.getWidthVector(dimensions, desiredSwitches);
		if (type.equals(TYPE.TORUS)) {
			generator = new GeneralizedTorus(width, Integer.MAX_VALUE);
		} else {
			generator = new NDimFlattenedButterfly(width);
		}
	}
	
	private int getNodeDegree() {
		if (!init) {
			init();			
		}
		if (type.equals(TYPE.TORUS)) {
			return dimensions*2;
		} else {
			return MoreArrays.sum(width) - width.length;
		}		
	}	

	@Override
	public void setTotalDesiredClientsAndRadix(int totalDesiredClients, int radix) {
		this.totalDesiredClients = totalDesiredClients;
		this.radix = radix;
		init = false;
	}
	
	private void setDesiredSwitches(int desiredSwitches) {
		this.desiredSwitches = desiredSwitches;
	}
	
	protected boolean willSkip() {
		if (!init) {
			init();
		}
		return generator.willSkip();
	}	
	
	public String getString() {
		if (desiredSwitches == 1) {
			return "single switch";
		} else {
			return Arrays.toString(width);
		}
	}	
	
	@Override
	public Map<String, String> getAllParameters() {
		return SimpleMap.getMap("dimensions", dimensions+"");
	}
	
	public ArrayList<HPCDesignPoint> getDesignPoints(AbstractNormalisedTrafficVectorGenerator localityVec) {
		ArrayList<HPCDesignPoint> list = new ArrayList<HPCDesignPoint>();
		if (radix >= totalDesiredClients) {
			Map<String, String> map;
			if (type.equals(TYPE.FBUT)) {
				AbstractDeterministicGenerator gen = new NDimFlattenedButterfly(0, 0);
				map = gen.getAllParameters();
			} else{
				AbstractDeterministicGenerator gen = new GeneralizedTorus(0, 0);
				map = gen.getAllParameters();				
			}
			
			HPCDesignPoint point = new HPCDesignPoint(radix, 1, totalDesiredClients,1, totalDesiredClients, 1, 1, 1, 1, "central switch", map);
			list.add(point);
		} else {
			int increment = MoreMaths.ceilDiv(radix, 200);
			for (int M = 1 ; M <= radix; M = M + increment) {
				setDesiredSwitches(MoreMaths.ceilDiv(totalDesiredClients, M));
				for (TopologyDescription desc : this) {
					int D = desc.getNodeDegree();
					if (M > radix-D) continue;
	
					for (int m = 1 ; m <= (radix-M)/D ; m = m + increment) {				
						HPCDesignPoint point = getMaxLoad(m, M, desc, localityVec);
						list.add(point);
					}
				}
			}
		}
			return list;
	}
	
	protected HPCDesignPoint getMaxLoad(int m, int M, TopologyDescription desc, AbstractNormalisedTrafficVectorGenerator localityVec) {
		int D = desc.getNodeDegree();
		int[] d = desc.getSortedDistances();
		int busyDegree = m*D + M;
		if (busyDegree > radix) throw new IllegalStateException("Cannot exceed radix"); 
		int[] nodeDistances = extendDistances(d, M, totalDesiredClients);
		if (nodeDistances.length != totalDesiredClients) throw new IllegalStateException();
		
		StatisticalDistribution<Integer> statisticalDistribution = new StatisticalDistribution<Integer>(10, 0, 10);
		statisticalDistribution.add(MoreArrays.toIntegerArray(nodeDistances));
		
		double[] l = localityVec.getVector(0, nodeDistances.length);
		double alpha = 0;
		double totalHops = 0;
		for (int i = 0 ; i < nodeDistances.length ; i++) {
			alpha += l[i]*nodeDistances[i];
			totalHops += nodeDistances[i];
		}
		double loadSum = MoreArrays.sum(l);
		int linksPerCrossing = D*m;
		double maxLoad = Math.min(1, ((double)linksPerCrossing)/(alpha*M));
		double averageTopologicalHops = totalHops / (nodeDistances.length - 1);
		double averageTrafficHops = alpha / loadSum;
		if (desc.getDesc().equals("[2, 2, 2]") && averageTrafficHops < 4) {
			extendDistances(d, M, totalDesiredClients);
		}

		
		return new HPCDesignPoint(radix, d.length, nodeDistances.length, maxLoad, (linksPerCrossing*d.length/2) + nodeDistances.length, m, M, averageTrafficHops, averageTopologicalHops, desc.getDesc(), desc.getAllParameters());	
	}
	
	public static int[] extendDistances(int[] d2, int m, int totalSize) {
		return extendDistances(d2, m, totalSize,0);
	}
	
	public static int[] extendDistances(int[] d2, int m, int totalSize, int localIndex) {
		int[] x = new int[totalSize];
	/*	for (int i = 0 ; i < x.length ; i++) {
			x[i] = d2[i/m] +1;
			if ( i == 998) {
				int rui = 0;
			}
		}*/

		int extensionsPerSwitch = MoreMaths.ceilDiv(totalSize - m, d2.length - 1);
		int i = 0;
		for (int j = 0 ; j < d2.length ; j++) {
			if (i == totalSize) break;
			for (int k = 0 ; k < extensionsPerSwitch ; k++) {
				if (i == totalSize) {
					break;
				}
				x[i] = d2[j] +1;
				i++;
			}
		}
		x[localIndex] = 0;
		int groupStart = (localIndex / m)*m;
		for (i = groupStart ; i < groupStart + Math.min(m, totalSize) ; i++) {
			if (i != localIndex)
				x[i] = 1;
		}		
		
		return x;
	}	
	
	
	@Override
	public Iterator<TopologyDescription> iterator() {
		if (possibleDimensions == null) {
			return new Iterator<TopologyDescription>() {
				
				boolean used = false;
	
				@Override
				public boolean hasNext() {
					if (used) return false;
					return !willSkip();
				}
	
				@Override
				public TopologyDescription next() {
					TopologyDescription d = new TopologyDescription(getNodeDegree(), getString(), generator);
					used = true;
					return d;
				}
	
				@Override
				public void remove() {
					throw new IllegalStateException();				
				}
			};
		} else {
			return new Iterator<TopologyDescription>() {
				
				int index = 0;
				
				{
					prepareNext();
				}
				
				private void prepareNext() {
					while (index < possibleDimensions.length) {
						dimensions = possibleDimensions[index];
						init = false;
						if (willSkip()) {
							index++;
						} else {
							return;
						}
					}
				}

				@Override
				public boolean hasNext() {
					return (index < possibleDimensions.length);
				}

				@Override
				public TopologyDescription next() {
					TopologyDescription d = new TopologyDescription(getNodeDegree(), getString(), generator);
					index++;
					prepareNext();
					return d;
				}

				@Override
				public void remove() {
					throw new IllegalStateException();	
				}
			};
		}
	}
}
