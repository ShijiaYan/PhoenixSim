package edu.columbia.lrl.LWSim.traffic;

import java.util.Map;

import edu.columbia.lrl.LWSim.AbstractTrafficGenerator;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.LWSim.traffic.deadlines.AbstractTTLGenerator;
import edu.columbia.lrl.LWSim.traffic.sizes.AbstractPacketSizeGenerator;

public class HotspotTrafficGenerator extends DefaultPoissonTrafficGenerator {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double alpha = 0;

	public HotspotTrafficGenerator(AbstractLoadScheme scheme, int defaultPacketSize, double alpha) {
		super(scheme, defaultPacketSize);
		this.alpha = alpha;
	}

	public HotspotTrafficGenerator(AbstractLoadScheme scheme, AbstractPacketSizeGenerator sizeGen, double alpha) {
		super(scheme, sizeGen);
		this.alpha = alpha;
	}

	public HotspotTrafficGenerator(AbstractLoadScheme scheme, 
			AbstractTTLGenerator ttlGen,
			AbstractPacketSizeGenerator sizeGen,
			double alpha) {
		super(scheme, ttlGen, sizeGen);
		this.alpha = alpha;
	}
	
	@Override
	protected int getDestination() {
		if (Math.random() < alpha)
			return 0;
		/* remember to change i->0 in
		 * gens[i].setPossibleDestinationIndexesExcludingOne(destsIndexes, i); 
		 * in FromGeneratorSpinetTopologyBuilder.java
		 */
		else
			return super.getDestination();
	}
	
	@Override
	public AbstractTrafficGenerator getCopy(double loadCoeff, int index) {
		if (this.getClass() != HotspotTrafficGenerator.class) 
			throw new IllegalStateException("getCopy() must be implemented in under classes (in this case, " + this.getClass() +")");

		HotspotTrafficGenerator gen =  new HotspotTrafficGenerator(loadScheme, ttlGen, sizeGen, alpha);
		setDefaultInCopy(loadCoeff, gen);
		return gen;
	}
	
	@Override
	public Map<String, String> getAllParameters(LWSIMExperiment lwSimExp) {
		Map<String, String> m = super.getAllParameters(lwSimExp);
		m.put("alpha", this.alpha +"");
		return m;
	}

}
