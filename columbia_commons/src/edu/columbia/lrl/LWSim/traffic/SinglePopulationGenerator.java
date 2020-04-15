package edu.columbia.lrl.LWSim.traffic;

import umontreal.iro.lecuyer.probdist.ExponentialDist;
import edu.columbia.lrl.LWSim.AbstractTrafficGenerator;
import edu.columbia.lrl.LWSim.traffic.sizes.AbstractPacketSizeGenerator;

public class SinglePopulationGenerator extends AbstractRandomTrafficGenerator {
	
	/**
	 * 
	 */
	protected ExponentialDist dist;	
	private static final long serialVersionUID = 1L;

	public SinglePopulationGenerator(AbstractLoadScheme scheme, int packetSize) {
		super(scheme, packetSize);
	}
	
	public SinglePopulationGenerator(AbstractLoadScheme scheme, AbstractPacketSizeGenerator packetSize) {
		super(scheme, null, packetSize);
	}
	
	@Override
	protected void updateRate() {	
		double load = r.divide(lwSimExperiment.getReferenceBandwidth());
		double averageIdleTime = ((1-load)/load)*lwSimExperiment.getReferenceBandwidth().getTime(sizeGen.getAverageSize()).getNanoseconds();
		try {
			dist = new ExponentialDist(1d/averageIdleTime);		
		}
		catch (IllegalArgumentException e) {
			throw new IllegalStateException("Trying to configure traffic generator with a load greater or equal than 1");
		}	
	}	
	
	@Override
	public AbstractTrafficGenerator getCopy(double loadCoeff, int index) {
		if (this.getClass() != SinglePopulationGenerator.class) 
			throw new IllegalStateException("getCopy() must be implemented in under classes (in this case, " + this.getClass() +")");

		SinglePopulationGenerator gen =  new SinglePopulationGenerator(loadScheme, sizeGen);
		setDefaultInCopy(loadCoeff, gen);
		return gen;
	}
	
	protected double addInterTime(double ns, int size) {
		return ns + lwSimExperiment.getReferenceBandwidth().getTime(size).getNanoseconds() + getInterTime();	
	}	
	
	@Override
	protected double getInterTime() {
		double d = dist.inverseF(randomStream.nextDouble());
	//	System.out.println(d);
		return d;
	}
	
	@Override
	public int getNumberOfClients() {
		throw new IllegalStateException("This traffic (" + this.getClass().getSimpleName() + ") generator cannot determine a number of clients");
	}

	@Override
	public void notifyEnd(double ref, double status) {
		// TODO Auto-generated method stub
		
	}
	
		
}
