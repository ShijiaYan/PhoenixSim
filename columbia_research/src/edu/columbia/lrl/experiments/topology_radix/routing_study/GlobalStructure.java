package edu.columbia.lrl.experiments.topology_radix.routing_study;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Map;


































import java.util.Map.Entry;

import ch.epfl.general_libraries.clazzes.ConstructorDef;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.graphics.pcolor.PcolorGUI;
import ch.epfl.general_libraries.math.MoreMaths;
import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.general_libraries.statistics.StatisticalDistribution;
import ch.epfl.general_libraries.utils.BoxedIntArray;
import ch.epfl.general_libraries.utils.Matrix;
import ch.epfl.general_libraries.utils.MoreArrays;
import ch.epfl.general_libraries.utils.MultiDigitCounter;
import ch.epfl.general_libraries.utils.Pair;
import ch.epfl.general_libraries.utils.SimpleMap;
import edu.columbia.lrl.experiments.topology_radix.locality.AbstractTrafficMatrix;
import edu.columbia.lrl.experiments.topology_radix.locality.LocalityModelBasedTrafficMatrix;
import edu.columbia.lrl.experiments.topology_radix.moorestudy.MooreBoundExperiment;
import edu.columbia.lrl.experiments.topology_radix.routing_study.structures.AbstractAxisStructure;

public class GlobalStructure {
	
	public enum MODE {
		MIN, ONE, MAX, DIM;
	}
	
	private int dimensionsLength;
	
	private int[] dimensions;
	private Class[] types;
	private AbstractAxisStructure[] templates; // the future - not fully implemented
	
	private AbstractAxisStructure[][] axes;
	private int[][] dimensionSizes;
	
	private int[] dimBase;
	
	private long nodeTarget;
	
	// consider placing this in a subclass to mark the specificity
	private int usedSwitches = -1;
	private int nodeMultiplicity = 1;
	
	// specific setting
	private boolean compactOnStructure = false;
	
	// state
	private boolean constructed = false;
	
	public GlobalStructure(int[] dimensions, 
			               @ParamName(name="Class struct", abstractClass=AbstractAxisStructure.class) Class type) {
		this.dimensions = dimensions;
		this.types = new Class[dimensions.length];
		for (int i = 0 ; i < dimensions.length ; i++) {
			types[i] = type;
		}
		init();
	}
	
	public GlobalStructure(int dimension, 
			@ParamName(name="Class struct", abstractClass=AbstractAxisStructure.class) Class type) {
		this.dimensions = new int[]{dimension};
		this.types = new Class[]{type};
		init();
	}	
	
	public GlobalStructure(@ParamName(name="Node per dim")int nbN, 
			@ParamName(name="Nb dimensions")int nbD, 
			@ParamName(name="Class struct", abstractClass=AbstractAxisStructure.class) Class type) {
		this.dimensions = new int[nbD];
		this.types = new Class[nbD];
		for (int i = 0 ; i < nbD ; i++) {
			dimensions[i] = nbN;
			types[i] = type;
		}
		init();
	}
	
	public GlobalStructure(
			int[] dimensions, 
			@ParamName(name="", abstractClass=AbstractAxisStructure.class)  Class[] types, 
			int nodeMult) {
		this.dimensions = dimensions;
		this.types = types;
		this.nodeMultiplicity = nodeMult;
		init();
	}
	
	
	public GlobalStructure(int dimension, AbstractAxisStructure template) {
		this.dimensions = new int[]{dimension};
		types = new Class[dimensions.length];
		templates = new AbstractAxisStructure[dimensions.length];
		for (int i = 0 ; i < dimensions.length ; i++) {
			types[i] = template.getClass();
			templates[i] = template;
		}
		init();
	}
	
	public GlobalStructure(int[] dimensions, AbstractAxisStructure[] templates) {
		this.dimensions = dimensions;
		types = new Class[dimensions.length];
		this.templates = new AbstractAxisStructure[dimensions.length];
		for (int i = 0 ; i < dimensions.length ; i++) {
			types[i] = templates[i].getClass();
			this.templates[i] = templates[i];
		}
		init();		
	}
	
	public GlobalStructure(int[] dimensions, AbstractAxisStructure template) {
		this.dimensions = dimensions;
		types = new Class[dimensions.length];
		templates = new AbstractAxisStructure[dimensions.length];
		for (int i = 0 ; i < dimensions.length ; i++) {
			types[i] = template.getClass();
			templates[i] = template;
		}
		init();
	}	
	
	public GlobalStructure(int[] dimensions, Class[] types, int usedSwitches, boolean compact) {
		this.dimensions = dimensions;
		this.types = types;
		this.usedSwitches = usedSwitches;
		this.compactOnStructure = compact;
		init();
	}
	
	public GlobalStructure(int[] dimensions, Class[] types, int switches,
			int nodeMult, boolean compact) {
		this(dimensions, types, switches, compact);
		this.nodeMultiplicity = nodeMult;
	}
	
	public GlobalStructure(int[] dimensions, String s) {
		this.dimensions = dimensions;
		this.types = new Class[s.length()];
		this.nodeMultiplicity = 1;
		for (int i = 0 ; i < s.length() ; i++) {
			types[i] = AbstractAxisStructure.getCorrespondingAxis(s.charAt(i));
		}
		init();
	}
	
	public GlobalStructure(int[] dimensions, String s, int nodeMult) {
		this.dimensions = dimensions;
		this.types = new Class[s.length()];
		this.nodeMultiplicity = nodeMult;
		for (int i = 0 ; i < s.length() ; i++) {
			types[i] = AbstractAxisStructure.getCorrespondingAxis(s.charAt(i));
		}
		init();
	}	
	
	@ConstructorDef(def="With total nodes target")
	public GlobalStructure(int[] dimensions, String s, long target) {
		this.dimensions = dimensions;
		this.nodeTarget = target;
		this.types = new Class[s.length()];
		this.nodeMultiplicity = MoreMaths.ceilDiv((int)target, (double)MoreMaths.product(dimensions));
		for (int i = 0 ; i < s.length() ; i++) {
			types[i] = AbstractAxisStructure.getCorrespondingAxis(s.charAt(i));
		}
		init();
	}	

	private void init() {
		this.dimensionsLength = dimensions.length;
		if (templates == null) {
			templates = new AbstractAxisStructure[types.length];
		}
		// here we construct the an instance of each axis to adapt the size
		
		MultiDigitCounter counter = new MultiDigitCounter(dimensions);
		dimBase = counter.getBases();

		if (usedSwitches < 0) usedSwitches = getTotalNumberOfNodes(); // this is unsafe, consider change that in the future
	}
	
	public void setNodeMultiplicity(int nn) {
		nodeMultiplicity = nn;
	}
	
	public RoutingResult routeTraffic(AbstractTrafficMatrix trafMat, boolean extraData) {
		if (constructed == false)
			this.construct();
		trafMat.init(usedSwitches*nodeMultiplicity, this);
		long totalSegments = 0;
		double totalRoutedTraffic = 0;
		double totalRawTraffic = 0;
		int[] indexes = getCompactIndexList(usedSwitches);
		
		double[] trafFrom = new double[usedSwitches];
		double[] trafTo = new double[usedSwitches];
		
		StatisticalDistribution<Integer> distances = new StatisticalDistribution<>();
		
		
		for (int i = 0 ; i < usedSwitches ; i++) {
			for (int j = 0 ; j < usedSwitches ; j++) {
				if (i != j) {
				//	double traf = trafMat.getTraffic(i, j);
					int start = (compactOnStructure?indexes[i]:i)*nodeMultiplicity;
					int end = (compactOnStructure?indexes[j]:j)*nodeMultiplicity;
					double trafAggr = trafMat.getTraffic(start, nodeMultiplicity, end, nodeMultiplicity);
					trafFrom[i] += trafAggr;
					trafTo[j] += trafAggr;
					
					totalRawTraffic += trafAggr;
					int segments = this.routeTraffic(trafAggr, i, j);
					totalSegments += segments;
					totalRoutedTraffic += segments * trafAggr;
					if (extraData) {
						distances.add(segments);
					}
				}
			}
		}
		
		int totalLinks = this.getTotalNumberOfConnections();		
		
		Pair<Double[][], StatisticalDistribution<Double>> usages = this.getLinkUsages();		
		StatisticalDistribution<Double> dist = usages.getSecond();
		int requiredCapacityUnitsMin = 0;
		int requiredCapacityUnitsOne = 0;
		int requiredCapacityUnitsMax = totalLinks*2*(int)Math.ceil(dist.getMax());
		for (Map.Entry<Object, Integer> e : dist) {
			double rawReq = (Double)e.getKey();
			if (Math.ceil(rawReq)*e.getValue() == 0) {
				System.out.print(".");
			}
			requiredCapacityUnitsMin += Math.ceil(rawReq)*e.getValue();
			requiredCapacityUnitsOne += Math.max(1, Math.ceil(rawReq))*e.getValue();
		}
		
		Double[][] perDim = usages.getFirst();
		int requiredCapacityUnitsDim = 0;
		for (int i = 0; i < dimensionsLength ; i++) {
			requiredCapacityUnitsDim += Math.ceil(perDim[2][i]) * perDim[3][i];
		}
		
		int[][] distRad = this.getRouterDegrees();
		int[] maxConnectLinks = new int[distRad[0].length];
		MoreArrays.cleanUncert(trafFrom);
		MoreArrays.cleanUncert(trafTo);
		for (int i = 0 ; i < usedSwitches ; i++) {
			int minRadixInject = (int)Math.ceil(trafFrom[i]/(double)nodeMultiplicity)*nodeMultiplicity;
			int minRadixAbsord = (int)Math.ceil(trafTo[i]/(double)nodeMultiplicity)*nodeMultiplicity;
			int max = Math.max(minRadixInject, minRadixAbsord);
			distRad[0][i] += max;
			distRad[1][i] += max;
			maxConnectLinks[i] = max;
		}
		
		int[] topoRadix = new int[dimensionsLength];
		for (int i = 0 ; i < dimensionsLength ; i++) {
			topoRadix[i] = axes[i][0].getRadix();
		}

		DataPoint dp = new DataPoint();
		dp.addProperties(this.getAllParameters(nodeMultiplicity));
		dp.addProperties(trafMat.getAllParameters());
		dp.addProperty("node multiplicity", nodeMultiplicity);
		dp.addProperty("supported clients", nodeMultiplicity*usedSwitches);
		dp.addProperty("compact", compactOnStructure);
		dp.addProperty("node target", nodeTarget);
		
		double criticalLoad = dist.getMax();
		
		if (!extraData) {
			trafFrom = null;
			trafTo = null;
			dist = null;
		}
		
		MooreBoundExperiment.LocStruct ls = null;
		MooreBoundExperiment.LocStruct lsNotRound = null;
		MooreBoundExperiment.LocStruct optLsNotRound = null;	
		MooreBoundExperiment.LocStruct optLsRound = null;
		if (trafMat instanceof LocalityModelBasedTrafficMatrix) {
			LocalityModelBasedTrafficMatrix ltm = (LocalityModelBasedTrafficMatrix)trafMat;
			// obtain the current connectivity, which is the radix of each dimensions times its multiplicity
			int pRadix = 0;
			for (int i = 0 ; i < topoRadix.length ; i++) {
				pRadix += topoRadix[i]*Math.ceil(perDim[2][i]);
			}
			ls = ltm.getLocStruct(usedSwitches, pRadix, totalLinks);	
			lsNotRound = ltm.getLocStructNotRound(usedSwitches, pRadix, totalLinks);	
			optLsNotRound =ltm.getOptimalNotRoundLocStruct(usedSwitches);
			optLsRound = ltm.getOptimalRoundLocStruct(usedSwitches);
		}

		
		RoutingResult rr = new RoutingResult(dp, 
				nodeMultiplicity, 
				totalSegments, 
				totalRawTraffic, 
				totalRoutedTraffic, 
				totalLinks, 
				getTotalNumberOfNodes(), 
				usedSwitches, 
				requiredCapacityUnitsMin,
				requiredCapacityUnitsOne,
				requiredCapacityUnitsMax,
				requiredCapacityUnitsDim,
				perDim,
				maxConnectLinks,
				distRad,
				topoRadix, 
				dist, 
				criticalLoad,
				trafFrom,
				trafTo,
				ls,
				lsNotRound,
				optLsNotRound,
				optLsRound,
				distances);
		return rr;
	}
	
	private int[] getCompactIndexList(int usedNodes) {
		// idea of the algorithm
		// 1) select a hypercube with minimal differences across dimentions
		// and who has more than usedNodes integer coordinate
		// 2) evalute (usedNodes) flat index values in this hypercube
		// -- dimensions with less nodes are priviledged
		boolean isOk = true;
		ArrayList<Integer> imposedFactors = new ArrayList<>();
		int[] cubeDim = null;
		
		for (int k = 0 ; k < dimensionsLength ; k++) {
		
			int[] factors = MoreMaths.factorize(usedNodes, dimensionsLength, imposedFactors);
		
			int[] dim = MoreArrays.getIncreasinglySortedCopy(dimensions);
			for (int i = 0 ; i < dim.length ; i++) {
				if (dim[i] < factors[i]) {
					isOk = false;
					imposedFactors.add(dim[i]);
					break;
				}
			}
			if (isOk) {
				cubeDim = factors;
				break;
			}
			isOk = true;
		}
		
		int[] indexes = new int[usedNodes];
		int h = 0;
		MultiDigitCounter counter = new MultiDigitCounter(cubeDim);
		for (BoxedIntArray array : counter) {
			indexes[h] = getIdOfCoord(array.array);
			h++;
			if (h == usedNodes) break;
		}
		return indexes;	
	}

	@SuppressWarnings("unchecked")
	private void construct() {
		axes = new AbstractAxisStructure[dimensionsLength][];
		dimensionSizes = new int[dimensionsLength][];
		for (int i = 0 ; i < dimensionsLength ; i++) {
			int[] remainingDim = MoreArrays.exclude(dimensions, i);
			axes[i] = new AbstractAxisStructure[(int)MoreMaths.product(remainingDim)];
			populate(axes[i], i, remainingDim, types[i], templates[i]);
		}
		constructed = true;
	}
	
	private void populate(AbstractAxisStructure[] axes, int dimId,
			int[] remainingDim, Class<? extends AbstractAxisStructure> class_, AbstractAxisStructure template) {
		try {
			MultiDigitCounter counter = new MultiDigitCounter(remainingDim);
			if (template != null) {
				for (int i = 0 ; i <  axes.length ; i++) {
					axes[i] = template.getInstance((short)dimensions[dimId]);
					axes[i].resetUsages();
					axes[i].setCoord(dimId, counter.getStateShort());
					counter.increment();
				}				
			} else {
				Constructor<? extends AbstractAxisStructure> constru = class_.getConstructor(Short.TYPE);
				for (int i = 0 ; i <  axes.length ; i++) {
					axes[i] = constru.newInstance((short)dimensions[dimId]);
					axes[i].resetUsages();
					axes[i].setCoord(dimId, counter.getStateShort());
					counter.increment();
				}
			}
			dimensionSizes[dimId] = counter.getBases();			
		}
		catch (Exception e) {
			if (e.getCause() != null && e.getCause() instanceof IllegalStateException) {
				throw (IllegalStateException)e.getCause();
			}
			throw new IllegalStateException(e);
		}
	}
	
	private int[] getCoordOfNodeId(int id) {
		int[] coord = new int[dimensionsLength];
		for (int i = 0 ; i < dimensionsLength ; i++) {
			coord[i] = id / dimBase[i];
			id -= coord[i]*dimBase[i];
		}
		return coord;
	}
	
	private int getIdOfCoord(int[] coord) {
		if (coord.length != dimensionsLength) throw new IllegalStateException("Dimensions mismatch");
		int id = 0;
		for (int i = 0 ; i < dimensionsLength ; i++) {
			id += coord[i] * dimBase[i];
		}
		return id;
	}
	
	private int routeTraffic(double intensity, int from, int to) {
		if (intensity < 0) throw new IllegalStateException("Cannot have negative traffic");
		int totalDist = 0;
		int[] coordFrom = getCoordOfNodeId(from);
		int[] coordTo = getCoordOfNodeId(to);
		for (int i = 0 ; i < dimensionsLength ; i++) {
			int fromLoc = coordFrom[i];
			int toLoc = coordTo[i];
			if (fromLoc != toLoc) {
				AbstractAxisStructure axis = getAxis(MoreArrays.exclude(coordFrom, i), i);
				totalDist += axis.addTraffic((float)intensity, fromLoc, toLoc);
			}
			coordFrom[i] = toLoc;
		}
		return totalDist;
	}
	
	public int getNumberOfHops(int from, int to) {
		int totalDist = 0;
		int[] coordFrom = getCoordOfNodeId(from);
		int[] coordTo = getCoordOfNodeId(to);
		for (int i = 0 ; i < dimensionsLength ; i++) {
			int fromLoc = coordFrom[i];
			int toLoc = coordTo[i];
			if (fromLoc != toLoc) {
				totalDist += getAxis(MoreArrays.exclude(coordFrom, i), i).getNumberOfHops(fromLoc, toLoc);
			}
		}
		return totalDist;		
	}
	
	private AbstractAxisStructure getAxis(int[] coords, int nullDimension) {
		if (constructed == false)
			construct();
		int index = 0;
		int total = 0;
		for (int i = 0 ; i < dimensionsLength ; i++) {
			if (i == nullDimension) continue;
			total += dimensionSizes[nullDimension][index] * coords[index];
			index++;
		}
		return axes[nullDimension][total];	
	}
	
	public int[] getSizes() {
		return dimensions;
	}

	public int getNumberOfDimensions() {
		return dimensions.length;
	}

	public int getTotalNumberOfNodes() {
		int totalNodes = (int)MoreMaths.product(dimensions);
		return totalNodes;
	}
	
	public int getUsedNodes() {
		return usedSwitches;
	}

	@SuppressWarnings("unchecked")
	public Map<String, String> getAllParameters(int nodemult) {
		StringBuilder sb = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();
		int dia = 0;
		for (int i = 0 ; i < types.length ; i++) {
			sb.append(AbstractAxisStructure.getCorrespondingChar(types[i]));
			sb2.append(AbstractAxisStructure.getCorrespondingChar(types[i]));
			sb.append(String.format("%02d", dimensions[i]));
			dia += axes[i][0].getDiameter();
		}
		Map<String, String> m = SimpleMap.getMap("Type", sb.toString());
		m.put("Type-mult", sb.toString() + "-m" + nodemult);
		m.put("General type", sb2.toString());
		m.put("Dimensions", types.length+"");
		m.put("diam", dia+"");
		// very beta version. Should add each propoerties of each dimension=?
		for (int i = 0 ; i < templates.length ; i++) {
			if (templates[i] != null) {
				for (Entry<? extends String, ? extends String> ent : templates[i].getParameters().entrySet()) {
					m.put("Dim_" + i + "_" + ent.getKey(), ent.getValue());
				}
			}
		}
		return m;
	}
	
	@SuppressWarnings("unchecked")
	public String getGeneralTypeAndDimensions() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0 ; i < types.length ; i++) {
			sb.append(AbstractAxisStructure.getCorrespondingChar(types[i]));
			sb.append(String.format("%02d", dimensions[i]));
		}
		sb.append("-m"+ nodeMultiplicity);
		return sb.toString();
	}

	public Pair<Double[][], StatisticalDistribution<Double>> getLinkUsages() {
		StatisticalDistribution<Double> dist = new StatisticalDistribution<>();
		
		Double[][] perDim = new Double[4][dimensionsLength];	
		for (int i = 0 ; i < dimensionsLength ; i++) {
			double max = 0;
			double accumMean = 0;
			double min = Integer.MAX_VALUE;
			int divtot = 0;
			for (int j = 0 ; j < axes[i].length ; j++) {
				double[] front = axes[i][j].getUsagesFront();
				double[] back = axes[i][j].getUsagesBack();
				dist.add(MoreArrays.toDoubleArray(front));
				dist.add(MoreArrays.toDoubleArray(back));
				max = Math.max(max, MoreArrays.max(front));
				max = Math.max(max, MoreArrays.max(back));
				min = Math.min(min, MoreArrays.min(front));
				min = Math.min(min, MoreArrays.min(back));				
				accumMean += MoreArrays.sum(front);
				accumMean += MoreArrays.sum(back);
				divtot += back.length*2;
			}
			double mean = accumMean / (double)divtot;
			perDim[0][i] = min;
			perDim[1][i] = mean;
			perDim[2][i] = max;
			perDim[3][i] = (double)divtot;
		}	
		
		return new Pair<>(perDim, dist);
	}
	
	public int[][] getRouterDegrees() {
		int[][] ret = new int[2][getTotalNumberOfNodes()];
		MultiDigitCounter counter = new MultiDigitCounter(dimensions);
		int index = 0;
		for (BoxedIntArray coord : counter) {
			int[] coordOfOneNode = coord.array;
			int radixOfOneNodeMin = 0;
			int radixOfOneNodeOne = 0;
			for (int i = 0 ; i < coordOfOneNode.length ; i++) {
				int[] axisCoord = MoreArrays.exclude(coordOfOneNode, i);
				AbstractAxisStructure struct = getAxis(axisCoord, i);
				int[] indexes = struct.getIndexesOfLinksConnectedTo(coordOfOneNode[i]);
				double[] front = struct.getUsagesFront();
				double[] back = struct.getUsagesBack();
                for (int value : indexes) {
                    double radFront = front[value];
                    double radBack = back[value];
                    radixOfOneNodeMin += Math.ceil(Math.max(radFront, radBack));
                    radixOfOneNodeOne += Math.max(1, Math.ceil(Math.max(radFront, radBack)));
                }
			}
			ret[0][index] = radixOfOneNodeMin;
			ret[1][index] = radixOfOneNodeOne;
			index++;
		}
		return ret;
	}

	public int getTotalNumberOfConnections() {
		int total = 0;
		for (int i = 0 ; i < dimensionsLength ; i++) {
			for (int j = 0 ; j < axes[i].length ; j++) {
				total += axes[i][j].getNumberOfLinksInStructure();
			}
		}
		return total;
	}
	
	public double[][] getIncidenceMatrix() {
		int nodes = this.getTotalNumberOfNodes();
		double[][] inci = new double[nodes][nodes];
		for (int i = 0 ; i < nodes ; i++) {
			for (int j = 0 ; j < nodes ; j++) {
				if (i != j) {
					inci[i][j] = getNumberOfHops(i, j) == 1 ? 0 : Double.NaN;
				}
			}
		}
		return inci;
	}
	
	public double[][] getTrafficMatrix() {
		int nodes = this.getTotalNumberOfNodes();
		double[][] inci = new double[nodes][nodes];	
		for (int i = 0 ; i < nodes ; i++) {
			for (int j = 0 ; j < nodes ; j++) {
				if (i != j) {
					if (getNumberOfHops(i, j) != 1) {
						inci[i][j] = Double.NaN;
					}
				}
			}
		}
        for (AbstractAxisStructure[] axe : axes) {
            for (int j = 0; j < axe.length; j++) {
                AbstractAxisStructure struct = axe[j];
                for (int k = 0; k < struct.getNumberOfLinksInStructure(); k++) {
                    int[] srcDest = struct.getExtremitiesOfLink(k);
                    int[] coordFrom = struct.getCoordOfNodeIndex(srcDest[0]);
                    int[] coordTo = struct.getCoordOfNodeIndex(srcDest[1]);
                    int from = this.getIdOfCoord(coordFrom);
                    int to = this.getIdOfCoord(coordTo);
                    double[] front = struct.getUsagesFront();
                    double[] back = struct.getUsagesBack();
                    inci[from][to] = front[k];
                    inci[to][from] = back[k];
                }
            }
        }
		inci = Matrix.normalize(inci);
		return inci;
	}

	public void showPColor() {
		PcolorGUI gui = new PcolorGUI(getGeneralTypeAndDimensions(), getIncidenceMatrix());
		gui.showInFrame();
	}

	public void showTrafPColor() {
		PcolorGUI gui = new PcolorGUI(getGeneralTypeAndDimensions() + "result", getTrafficMatrix());
		gui.showInFrame();
	}

	public int getMinDimSize() {
		return MoreArrays.min(dimensions);
	}

	public int getMultiplicity() {
		return nodeMultiplicity;
	}
}
