package edu.columbia.lrl.experiments.topo_scalability;

import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.general_libraries.results.Execution;
import ch.epfl.javancox.results_manager.SmartDataPointCollector;
import ch.epfl.javancox.results_manager.gui.DefaultResultDisplayingGUI;

public class HybridScalability extends Scalability {
	
	public static void main(String[] args) {
		ScalabilityExperiment e = new ScalabilityExperiment(new HybridScalability(), 120, 32, 0.6);
		SmartDataPointCollector db = new SmartDataPointCollector();
		e.run(db, null);		
		DefaultResultDisplayingGUI.displayDefault(db);
	}
	

	
	public HybridScalability() {}
	
	public boolean compute(DataPoint dp, Execution e) {
		StarScalability starSc = new StarScalability();
		starSc.init(numberOfNodes, numberOfWavelengths, targetLoad, clientsPerStar);
		
		int numberOfL1Stars = starSc.getNumberOfStars(); 
		int requiredCN = starSc.getRequiredCN();
		
		if (requiredCN + clientsPerStar > numberOfWavelengths) return false;
		
		double loadPerCN = starSc.getUplinkLoad() / requiredCN;
		
		int totalNodesToInterConnect = numberOfL1Stars * requiredCN;
		
		dp.addResultProperty("Number of L1 stars", numberOfL1Stars);
		dp.addResultProperty("Number of L1 CN (per star)", requiredCN);
		e.addDataPoint(dp);
		
		boolean toRet = false;
		
		for (int i = 1 ; i < Math.min(numberOfWavelengths - 1, totalNodesToInterConnect) ; i++) {
			RingScalability ringSc = new RingScalability();
			ringSc.init(totalNodesToInterConnect, numberOfWavelengths, loadPerCN, i);
			
			int numberOfL2Stars = ringSc.getNumberOfStars();
			int requiredCNL2 = ringSc.getRequiredConnectingNodes();
			
			if (numberOfWavelengths - (i + requiredCNL2) >= 0) {
				DataPoint dp2 = dp.getDerivedDataPoint();
				dp2.addProperty("Clients per L2 star", i);	
				dp2.addResultProperty("L1 star per L2 star", (double)i/(double)requiredCN);	
				dp2.addResultProperty("Number of L2 stars", numberOfL2Stars);
				dp2.addResultProperty("Number of L2 CN (per star)", requiredCNL2);
				dp2.addResultProperty("Number of L2 CN left", ringSc.getRequiredConnectingNodesLeftAndRight()[0]);
				dp2.addResultProperty("Number of L2 CN right", ringSc.getRequiredConnectingNodesLeftAndRight()[1]);				
				dp2.addResultProperty("Unused wavelengths on L2", numberOfWavelengths - (i + requiredCNL2));
				dp2.addResultProperty("Total number of stars", numberOfL1Stars + numberOfL2Stars);
				e.addDataPoint(dp2);
				toRet = true;
			}
			
		//	ringSc.compute();	
		}
		return toRet;
	}
	
	public int getTotalNumberOfStars() {
		return -1;
	}
	
	public int getTotalNumberOfConnectingNodes() {
		return -1;
	}		
	
	
}
