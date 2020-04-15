package edu.columbia.lrl.LWSim.traffic;

import java.util.Map;

import edu.columbia.lrl.LWSim.AbstractTrafficGenerator;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import ch.epfl.general_libraries.random.PRNStream;
import ch.epfl.general_libraries.utils.SimpleMap;
import umontreal.iro.lecuyer.probdist.ExponentialDist;
import umontreal.iro.lecuyer.probdist.NormalDist;

public class RandomAverageLoadGenerator extends DefaultPoissonTrafficGenerator {
	
	private int packetSize;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RandomAverageLoadGenerator(AbstractLoadScheme scheme, int packetSize) {
		super(scheme, packetSize);
		this.packetSize = packetSize;
	}
	
	
	
	@Override
	protected void updateRate() {	
		double bitPerNs = r.getRate()/1000;
		NormalDist nd = new NormalDist(bitPerNs, bitPerNs);
		bitPerNs = Math.abs(nd.inverseF(PRNStream.getDefaultStream(index).nextDouble()));
		double packetsPerNs = bitPerNs/sizeGen.getAverageSize();
		dist = new ExponentialDist(packetsPerNs);		
	}
	
	@Override
	public AbstractTrafficGenerator getCopy(double loadCoeff, int index) {
		if (this.getClass() != RandomAverageLoadGenerator.class) 
			throw new IllegalStateException("getCopy() must be implemented in under classes (in this case, " + this.getClass() +")");

		RandomAverageLoadGenerator gen = new RandomAverageLoadGenerator(loadScheme, this.packetSize);
		setDefaultInCopy(loadCoeff, gen);
		return gen;
	}
	
	@Override
	public Map<String, String> getAllParameters(LWSIMExperiment lwSimExp) {
		return SimpleMap.getMap("traffic_gen", this.getClass().getSimpleName());
	}
	

}
