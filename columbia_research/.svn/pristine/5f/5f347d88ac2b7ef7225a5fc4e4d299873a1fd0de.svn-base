package edu.columbia.sebastien.fat_trees;

import java.util.Map;

import edu.columbia.lrl.experiments.topology_radix.locality.AbstractTrafficMatrix;

import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javancox.inputs.topology.AbstractDeterministicGenerator;

public class UltimateSlimmedFatTreeGenerator extends AbstractDeterministicGenerator {

	final private int clients;
	final private int maxRadix;
	final private AbstractTrafficMatrix trafMat;
//	final private boolean unfat;
//	final private boolean mergeLast;	
	
	public UltimateSlimmedFatTreeGenerator(int clients, 
			int maxRadix, 
			AbstractTrafficMatrix trafMat, 
			boolean unfat, 
			boolean mergeLast) {
		if (maxRadix < 4) throw new IllegalStateException("Radix must be >= 4");
		this.clients = clients;
		this.maxRadix = maxRadix;
		this.trafMat = trafMat;
	//	this.unfat = unfat;
	//	this.mergeLast = mergeLast;
		trafMat.init(clients, null);
	}
	
	@Override
	public void generate(AbstractGraphHandler agh) {
		agh.clear();
		
		int startIndex = 0;
	//	int endIndex = 0;
		for (int i = 0 ; i < maxRadix ; i++) {
			double upTraf = 0;
			if (startIndex > 0) {
				upTraf += trafMat.getTraffic(startIndex, startIndex+i, 0, startIndex-1);
			}
			if (startIndex+i+1 < clients-1) {
				upTraf += trafMat.getTraffic(startIndex, startIndex+i, startIndex+i+1, clients-1);
			}
			if (upTraf + i <= maxRadix) {
		//		endIndex = startIndex+i;
			}
		}

}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, String> getGeneratorParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getNumberOfNodes() {
		// TODO Auto-generated method stub
		return 0;
	}
}
