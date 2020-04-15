package edu.columbia.lrl.LWSim.traffic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import edu.columbia.lrl.LWSim.AbstractTrafficGenerator;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.LWSim.traffic.deadlines.AbstractTTLGenerator;
import edu.columbia.lrl.LWSim.traffic.sizes.AbstractPacketSizeGenerator;


public class NeighborBiasedGenerator extends DefaultPoissonTrafficGenerator {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5674451036974879358L;
	private double alpha = 0;
	private int nbNeighbor = 0;

	public NeighborBiasedGenerator(AbstractLoadScheme scheme, int defaultPacketSize, double alpha, int nbNeighbor) {
		super(scheme, defaultPacketSize);
		this.alpha = alpha;
		this.nbNeighbor = nbNeighbor;
	}

	public NeighborBiasedGenerator(AbstractLoadScheme scheme, AbstractPacketSizeGenerator sizeGen, double alpha, int nbNeighbor) {
		super(scheme, sizeGen);
		this.alpha = alpha;
		this.nbNeighbor = nbNeighbor;
	}

	public NeighborBiasedGenerator(AbstractLoadScheme scheme, 
			AbstractTTLGenerator ttlGen,
			AbstractPacketSizeGenerator sizeGen,
			double alpha,
			int nbNeighbor) {
		super(scheme, ttlGen, sizeGen);
		this.alpha = alpha;
		this.nbNeighbor = nbNeighbor;
	}

	private int a = 0;
	private Collection<Integer> NewPossibleDestinationIndexes;
	private int nbClients;
	@Override
	protected int getDestination() {
		if (a==0) {
			this.nbClients = this.lwSimExperiment.getNumberOfClients();			
			this.NewPossibleDestinationIndexes = new ArrayList<Integer>();
			this.NewPossibleDestinationIndexes.addAll(possibleDestinationIndexes);
			for (int i = 0; i < nbNeighbor; i++){
				this.NewPossibleDestinationIndexes.remove((index+i+1) % nbClients);
			}
			a++;
		}
		if (randomStream.nextDouble() < alpha) {
			int i = randomStream.nextInt(nbNeighbor-1);
			return (this.index + i +1) % nbClients;
		}
		else {
			if (NewPossibleDestinationIndexes == null)
				throw new NullPointerException("Possible destinations has not been set for this traffic generator");
			return randomStream.pickIn(NewPossibleDestinationIndexes);	
		}
	}
	
	@Override
	public AbstractTrafficGenerator getCopy(double loadCoeff, int index) {
		if (this.getClass() != NeighborBiasedGenerator.class) 
			throw new IllegalStateException("getCopy() must be implemented in under classes (in this case, " + this.getClass() +")");

		NeighborBiasedGenerator gen =  new NeighborBiasedGenerator(loadScheme, ttlGen, sizeGen, alpha, nbNeighbor);
		gen.index = index;
		setDefaultInCopy(loadCoeff, gen);
		return gen;
	}
	
	@Override
	public Map<String, String> getAllParameters(LWSIMExperiment lwSimExp) {
		Map<String, String> m = super.getAllParameters(lwSimExp);
		m.put("alpha", this.alpha +"");
		m.put("Number of Neighbors", this.nbNeighbor +"");
		return m;
	}

	/*@Override
	public void setPossibleDestinationIndexes__(Collection<Integer> col) {
		super.setPossibleDestinationIndexes__(col);
		for (int i = 0; i < nbNeighbor; i++){
			this.possibleDestinationIndexes.remove(index+i);
		}
	}*/
	
	

}
