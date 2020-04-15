package edu.columbia.lrl.LWSim.traffic.sizes;


import ch.epfl.general_libraries.random.PRNStream;

public class ConstantSizeGenerator extends AbstractPacketSizeGenerator {
	
	private int packetSize;
	
	public ConstantSizeGenerator(int packetSize) {
		this.packetSize = packetSize;
	}
	

	@Override
	public int getSize(PRNStream stream) {
		return packetSize;
	}


	@Override
	public int getAverageSize() {
		return packetSize;
	}

}
