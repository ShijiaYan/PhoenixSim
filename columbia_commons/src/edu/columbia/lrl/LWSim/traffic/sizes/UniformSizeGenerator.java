package edu.columbia.lrl.LWSim.traffic.sizes;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.random.PRNStream;

public class UniformSizeGenerator extends AbstractPacketSizeGenerator {
	private int lowerBound;
	private int upperBound;
	private int range;

	public UniformSizeGenerator(
			@ParamName(name = "lower bound", default_ = "2") int lowerBound,
			@ParamName(name = "upper bound", default_ = "50") int upperBound) {
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
		this.range = upperBound - lowerBound;
	}

	@Override
	public int getSize(PRNStream stream) {
		int i = stream.nextInt(range + 1) + lowerBound;
		return i;
	}

	@Override
	public int getAverageSize() {
		// TODO Auto-generated method stub
		return (int) (lowerBound + upperBound)/2;
	}

}
