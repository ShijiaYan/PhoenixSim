package edu.columbia.lrl.experiments.topo_scalability;

import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.general_libraries.results.Execution;

public class RingScalability extends Scalability {
	
	public RingScalability() {}
	
	public boolean compute(DataPoint dp, Execution e) {
		if (clientsPerStar == 10 && numberOfNodes == 50) {
		//	int g = 0;
		}
		
		int[] exchangesWithLeftRight = getExchangesWithLeftAndRight();
		
		double[] loads = getLoadWithLeftAndRight();

		int[] requiredCN = getRequiredConnectingNodesLeftAndRight();
		
		int requiredConnectingNodesLeft = requiredCN[0];
		int requiredConnectingNodesRight = requiredCN[1];
		
		int unusedWavelengths = numberOfWavelengths - requiredConnectingNodesLeft - requiredConnectingNodesRight - clientsPerStar;

		boolean toRet = false;

		if (unusedWavelengths >= 0) {
			dp.addResultProperty("Unused wavelengths", unusedWavelengths);
			dp.addResultProperty("Connecting node left", requiredConnectingNodesLeft);
			dp.addResultProperty("Connecting nodes right", requiredConnectingNodesRight);
			dp.addResultProperty("Total connecting nodes", (requiredConnectingNodesLeft + requiredConnectingNodesRight));
			dp.addResultProperty("Load with left", loads[0]);
			dp.addResultProperty("Load with rights", loads[1]);	
			dp.addResultProperty("Exhange with left", exchangesWithLeftRight[0]);
			dp.addResultProperty("Exhange with right", exchangesWithLeftRight[1]);
					
			dp.addResultProperty("Total number of stars", getNumberOfStars());
			toRet = true;
		}
		e.addDataPoint(dp);
		return toRet;
	}
	
	public int getTotalNumberOfStars() {
		return getNumberOfStars();
	}
	
	public int getTotalNumberOfConnectingNodes() {
		return getRequiredConnectingNodes();
	}	
	
	public int getNumberOfStars() {
		return (int)Math.ceil((double)numberOfNodes/(double)clientsPerStar);
	}
	
	public int[] getRequiredConnectingNodesLeftAndRight() {
		double[] loads = getLoadWithLeftAndRight();
		return new int[]{ (int)Math.ceil(loads[0]), (int)Math.ceil(loads[1])};
	}
	
	public int getRequiredConnectingNodes() {
		double[] loads = getLoadWithLeftAndRight();
		return (int)Math.ceil(loads[0]) + (int)Math.ceil(loads[1]);
	}
	
	public double[] getLoadWithLeftAndRight() {
		int[] exchanges = getExchangesWithLeftAndRight();
		
		double loadWithLeft = exchanges[0] * clientsPerStar *clientsPerStar * srcDestLoad;
		double loadWithRight = exchanges[1] * clientsPerStar *clientsPerStar * srcDestLoad;
		return new double[]{loadWithLeft, loadWithRight};		
	}
	
	public int[] getExchangesWithLeftAndRight() {
		int nbStars = getNumberOfStars();
		int[] trafficFromLeft = new int[nbStars];
		int[] trafficFromRight = new int[nbStars];		
		for (int i = 0 ; i < nbStars ; i++) {
			int connectionsRights = (int)Math.floor(nbStars/2d);
			int connectionsLeft;
			if (nbStars % 2 == 0) {
				connectionsLeft = (nbStars/2)-1;
			} else {
				connectionsLeft = (int)Math.floor(nbStars/2d);
			}
			for (int j = 0 ; j < connectionsRights ; j++) {
				trafficFromLeft[(i+j+1)%nbStars] += (connectionsRights - j);
			}
			for (int j = 0 ; j < connectionsLeft ; j++) {
				trafficFromRight[(i-j-1+nbStars)%nbStars] += (connectionsLeft -j);
			}
		}

		
		for (int i = 1 ; i < nbStars ; i++) {
			if (trafficFromLeft[i] != trafficFromLeft[i-1]) throw new IllegalStateException();
			if (trafficFromRight[i] != trafficFromRight[i-1]) throw new IllegalStateException();			
		}		
		
		return new int[]{trafficFromLeft[0], trafficFromRight[0]};		
	}
}
