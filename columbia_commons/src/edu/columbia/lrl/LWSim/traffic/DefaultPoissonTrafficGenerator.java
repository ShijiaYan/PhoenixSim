package edu.columbia.lrl.LWSim.traffic;

import ch.epfl.general_libraries.clazzes.ParamName;
import umontreal.iro.lecuyer.probdist.ExponentialDist;
import edu.columbia.lrl.LWSim.AbstractTrafficGenerator;
import edu.columbia.lrl.LWSim.traffic.deadlines.AbstractTTLGenerator;
import edu.columbia.lrl.LWSim.traffic.sizes.AbstractPacketSizeGenerator;

public class DefaultPoissonTrafficGenerator extends AbstractRandomTrafficGenerator {
	
	public DefaultPoissonTrafficGenerator(AbstractLoadScheme scheme,
			@ParamName(name="Packet size in bits", default_="8192") int defaultPacketSize) {
		super(scheme, defaultPacketSize);
	}
	
	public DefaultPoissonTrafficGenerator(AbstractLoadScheme scheme,
			AbstractPacketSizeGenerator sizeGen) {
		super(scheme, null, sizeGen);
	}
	
	public DefaultPoissonTrafficGenerator(AbstractLoadScheme scheme,
			AbstractTTLGenerator ttlGen, AbstractPacketSizeGenerator sizeGen) {
		super(scheme, ttlGen, sizeGen);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected ExponentialDist dist;
	
	@Override
	public AbstractTrafficGenerator getCopy(double loadCoeff, int index) {
		if (this.getClass() != DefaultPoissonTrafficGenerator.class) 
			throw new IllegalStateException("getCopy() must be implemented in under classes (in this case, " + this.getClass() +")");

		DefaultPoissonTrafficGenerator gen =  new DefaultPoissonTrafficGenerator(loadScheme, ttlGen, sizeGen);
		setDefaultInCopy(loadCoeff, gen);
		return gen;
	}
	
	protected void updateRate() {	
		double bitPerNs = r.getRate()/1000;
		double packetsPerNs = bitPerNs/sizeGen.getAverageSize();
		dist = new ExponentialDist(packetsPerNs);		
	}
	
	@Override
	protected double getInterTime() {
		return dist.inverseF(randomStream.nextDouble());
	}

	@Override
	public void notifyEnd(double ref, double status) {	}

	@Override
	public int getNumberOfClients() {
		throw new IllegalStateException("The default generator cannot determine a number of clients");
	}	
	
}
