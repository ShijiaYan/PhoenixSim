package edu.columbia.lrl.experiments.topology_radix.topogen;

import edu.columbia.lrl.experiments.topology_radix.locality.AbstractTrafficMatrix;
import edu.columbia.lrl.experiments.topology_radix.locality.IndexDistanceDecreasingTrafficMatrix;
import ch.epfl.general_libraries.graphics.pcolor.PcolorGUI;
import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.general_libraries.results.Execution;
import ch.epfl.general_libraries.statistics.StatisticalDistribution;
import ch.epfl.general_libraries.utils.Matrix;
import ch.epfl.general_libraries.utils.MoreArrays;
import ch.epfl.javanco.algorithms.BFS;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.base.Javanco;
import ch.epfl.javancox.inputs.topology.GeneralizedTorus;
import ch.epfl.javancox.results_manager.SmartDataPointCollector;
import ch.epfl.javancox.results_manager.gui.DefaultResultDisplayingGUI;

public class DistanceMatrix {
	
	public static void main(String[] args) {
	//	NDimFlattenedButterfly topo = new NDimFlattenedButterfly(5, 2);
		GeneralizedTorus topo = new GeneralizedTorus(3,3);
		
		int mult = 1;
		
		AbstractGraphHandler agh = Javanco.getDefaultGraphHandler(false);
		
		topo.generate(agh);
		
		int[][] mat = new int[agh.getNumberOfNodes()*mult][];
		int[] dist;
		int max = 0;
		for (int i = 0 ; i < agh.getNumberOfNodes() ; i++) {
			dist = BFS.getDistancesFromUndirected(agh, i);
			max = Math.max(max, MoreArrays.max(dist));
			for (int j = 0 ; j < mult ; j++) {
				mat[i*mult + j] = DimensionBasedGenerator.extendDistances(dist, mult, dist.length*mult, i*mult + j);
				max = Math.max(max, MoreArrays.max(mat[i*mult + j]));				
			}
			
		}
		
		/*int switchRadix = 8;
		int totalClients = 200;
		int maxClust = (int)Math.ceil(MoreMaths.logN(totalClients, switchRadix));
		int max = maxClust*2;
		
		int[][] mat = new int[totalClients][totalClients];
		for (int i = 0 ; i < totalClients ; i++) {
			Arrays.fill(mat[i], maxClust*2);
			
			// place this in a loop
			for (int j = maxClust - 1 ; j >= 0 ; j--) {
				int groupSize = (int)Math.pow(switchRadix, j);
				int groupId = (i/(groupSize));			
				int groupStart = groupId*groupSize;
				int groupEnd = groupStart+groupSize;
				int groupEndReal = Math.min(groupEnd,totalClients);
				Arrays.fill(mat[i], groupStart, groupEndReal, (j)*2);
			}
		}*/
		
		
		
		PcolorGUI gui = new PcolorGUI("distances", Matrix.mult(Matrix.toDouble(mat), 1d/(double)max));
		gui.showInFrame();
		
		AbstractTrafficMatrix trafMat = new IndexDistanceDecreasingTrafficMatrix(1, false, false);
		
		trafMat.init(mat.length, null);
		
		
		StatisticalDistribution<Double> distrib = new StatisticalDistribution<>(50, 2, 4);
		
		for (int i = 0 ; i < mat.length ; i++) {
		
			double[] from = trafMat.getTrafficVectorFrom(i);
		
			double totTraf = MoreArrays.scalarProduct(from, mat[i]);
			System.out.println("Total traffic " + totTraf);
			
			distrib.add(totTraf);
		}
		
		Execution e = new Execution();
		
		DataPoint dp = new DataPoint();
		
		distrib.storeDistribution("dis", e, dp);
		
		SmartDataPointCollector col = new SmartDataPointCollector();
		
		col.addExecution(e);
		
		DefaultResultDisplayingGUI.displayDefault_(col);
		
		trafMat.showPColor();
	}

}
