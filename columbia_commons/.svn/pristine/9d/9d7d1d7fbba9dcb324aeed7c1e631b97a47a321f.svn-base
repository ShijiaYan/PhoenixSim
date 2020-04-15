package edu.columbia.lrl.LWSim.traffic.sizes;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.random.PRNStream;

public class ElephantsAndMicesGenerator extends AbstractPacketSizeGenerator {
	
	private int ele;
	private int mice;
	private double probMice;
	
	public ElephantsAndMicesGenerator(@ParamName(name="Size of elephants", default_="10000") int ele,
			@ParamName(name="Size of mices", default_="1") int mice,
			@ParamName(name="Probability of mice", default_="0.99") double probMice) {
		this.ele = ele;
		this.mice = mice;
		this.probMice = probMice;
	}

	@Override
	public int getSize(PRNStream stream) {
		if (stream.nextDouble() >= probMice) {
			return ele;
		} else {
			return mice;
		}
	}

	@Override
	public int getAverageSize() {
		return (int)(probMice*mice + (1-probMice)*ele);
	}

}
