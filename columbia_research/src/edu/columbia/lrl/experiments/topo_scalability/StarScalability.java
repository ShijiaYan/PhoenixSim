package edu.columbia.lrl.experiments.topo_scalability;

import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.general_libraries.results.Execution;

public class StarScalability extends Scalability {
	
	public StarScalability() {}
	
	public boolean compute(DataPoint dp, Execution e) {	
		/*if (numberOfNodes == 60 && targetLoad == 1 && clientsPerStar == 20) {
			int debug = 0;
		}*/
		boolean toRet = false;
		if (((getNumberOfStars()-1)*getRequiredCN() <= numberOfWavelengths) && 
			getRequiredCN() + clientsPerStar <= numberOfWavelengths) {
			dp.addResultProperty("Total connecting nodes", getNumberOfStars()*getRequiredCN());
			dp.addResultProperty("Connecting nodes per star", getRequiredCN());
			dp.addResultProperty("Total number of stars", getNumberOfStars()+"");
			dp.addResultProperty("Aggregation ratio", ((double)clientsPerStar/(double)getRequiredCN()));
		//	dp.addResultProperty("feasible", ((getRequiredCN() + clientsPerStar <= numberOfWavelengths)?1:0));
			toRet = true;
		}
		e.addDataPoint(dp);
		return toRet;
	}
	
	public int getTotalNumberOfStars(){
		return getNumberOfStars();
	}
	
	public int getTotalNumberOfConnectingNodes() {
		return getRequiredCN();
	}		
	
	public int getNumberOfStars() {
		return (int)Math.ceil((double)numberOfNodes/(double)clientsPerStar)+1;
	}
	
	public double getUplinkLoad() {
		return (numberOfNodes - clientsPerStar)*clientsPerStar*srcDestLoad;
	}
	
	public int getRequiredCN() {
		return (int)Math.ceil(getUplinkLoad());
	}
	
}
