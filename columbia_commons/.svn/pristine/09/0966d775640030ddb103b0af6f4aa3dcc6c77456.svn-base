package edu.columbia.lrl.LWSim.traffic.sizes;


import ch.epfl.general_libraries.random.PRNStream;
import umontreal.iro.lecuyer.probdist.ExponentialDist;

public class ExponentialSizeGenerator extends AbstractPacketSizeGenerator {
	
	private int averageSizeInBits;
	private ExponentialDist dist;
	
	public ExponentialSizeGenerator(int averageSizeInBits) {
		this.averageSizeInBits = averageSizeInBits;
		dist = new ExponentialDist(1d/(double)averageSizeInBits);
	}

	public int getSize(PRNStream stream) {
		return (int)dist.inverseF(stream.nextDouble());
	}

	@Override
	public int getAverageSize() {
		return averageSizeInBits;
	}

}
