package edu.columbia.lrl.LWSim.traffic;

import java.util.Map;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.statistics.ConstantDist;
import edu.columbia.lrl.LWSim.AbstractTrafficGenerator;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.LWSim.traffic.sizes.AbstractPacketSizeGenerator;
import umontreal.iro.lecuyer.probdist.ContinuousDistribution;
import umontreal.iro.lecuyer.probdist.ExponentialDist;


public class OnOffTrafficGenerator extends AbstractRandomTrafficGenerator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int burstLength;
	protected ContinuousDistribution dist;
	private boolean deterministic;

	private int state = 0;

	public OnOffTrafficGenerator(AbstractLoadScheme scheme,
			@ParamName(name = "Packet size in bits", default_ = "8192") int defaultPacketSize,
			@ParamName(name = "Number of packets in sequence", default_ = "50") int burstLength,
			@ParamName(name = "deterministic ? (false=expo)") boolean deterministic) {
		super(scheme, defaultPacketSize);
		this.burstLength = burstLength;
		this.deterministic = deterministic;
	}

	public OnOffTrafficGenerator(AbstractLoadScheme scheme, AbstractPacketSizeGenerator sizeGen,
			@ParamName(name = "Number of packets in sequence", default_ = "50") int burstLength,
			boolean deterministic) {
		super(scheme, sizeGen);
		this.burstLength = burstLength;
		this.deterministic = deterministic;
	}

	@Override
	public Map<String, String> getAllParameters(LWSIMExperiment lwSimExp) {
		Map<String, String> map = super.getAllParameters(lwSimExp);
		map.put("Burst length", burstLength + "");
		return map;
	}

	@Override
	public void notifyEnd(double ref, double status) {
	}

	@Override
	protected double getInterTime() {
		if (state == 0) {
			state = burstLength - 1;
			return dist.inverseF(randomStream.nextDouble());
		} else {
			state--;
			return 0;
		}

	}

	@Override
	protected void updateRate() {
		double bitPerNs = r.getRate() / 1000;
		double packetsPerNs = bitPerNs / (sizeGen.getAverageSize() * burstLength);
		if (deterministic) {
			dist = new ConstantDist(1d / packetsPerNs);
		} else {
			dist = new ExponentialDist(packetsPerNs);
		}

	}

	@Override
	public AbstractTrafficGenerator getCopy(double loadCoeff, int index) {
		if (this.getClass() != OnOffTrafficGenerator.class) throw new IllegalStateException(
				"getCopy() must be implemented in under classes (in this case, " + this.getClass() + ")");

		OnOffTrafficGenerator gen = new OnOffTrafficGenerator(loadScheme, sizeGen, burstLength, deterministic);
		setDefaultInCopy(loadCoeff, gen);
		return gen;
	}

	@Override
	public int getNumberOfClients() {
		throw new IllegalStateException("The default generator cannot determine a number of clients");
	}

}