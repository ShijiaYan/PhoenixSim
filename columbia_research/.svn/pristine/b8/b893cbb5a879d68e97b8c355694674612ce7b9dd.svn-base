package edu.columbia.sebastien.link_util.models.trafficmodel;

import java.util.Map;

import umontreal.iro.lecuyer.probdist.ExponentialDist;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.random.PRNStream;
import ch.epfl.general_libraries.traffic.Rate;
import ch.epfl.general_libraries.utils.SimpleMap;

public class FixedSizePoissonGen extends AbstractMessageGenerator {

	private int averageMessageLengthInBits;
	private Rate injectionRatePerLink;	
	
	private ExponentialDist dist;
	
	public FixedSizePoissonGen(@ParamName(name="Transmission size in MB", default_="1") double averageMessageLengthInBits, 
				      @ParamName(name="Injection rate")  Rate injectionRatePerLink) {

		this.averageMessageLengthInBits = (int)(averageMessageLengthInBits * (double)8000000);
		this.injectionRatePerLink = injectionRatePerLink;
	}
	
	public void init() {
		double averagePacketPerSecond = injectionRatePerLink.getInBitsSeconds() / averageMessageLengthInBits;
		dist = new ExponentialDist(averagePacketPerSecond*1e-9);
	}
	
	public Map<String, String> getAllParameters() {
		return SimpleMap.getMap("transmission size (bits)", averageMessageLengthInBits+"", "Injection rate (Gb/s)", injectionRatePerLink.getInGbitSeconds()+"");
	}	
	
	public double nextExpNS(PRNStream stream) {
		return dist.inverseF(stream.nextDouble());
	}
	
	public int getMessageBits() {
		return averageMessageLengthInBits;
	}	

}
