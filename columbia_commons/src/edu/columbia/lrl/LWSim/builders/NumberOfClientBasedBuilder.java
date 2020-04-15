package edu.columbia.lrl.LWSim.builders;

import java.util.ArrayList;
import java.util.Map;

import ch.epfl.general_libraries.clazzes.ConstructorDef;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.traffic.Rate;
import ch.epfl.general_libraries.utils.SimpleMap;
import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.LWSim.LWSimComponent;
import edu.columbia.lrl.general.Message;

public class NumberOfClientBasedBuilder extends AbstractTopologyBuilder {
	
	protected AbstractNumberOfClientConfigurableBuilder subBuilder;
	protected int nbClients;
	private Rate referenceBW;
	protected boolean nbClientsSet = false;
	protected boolean bandwidthSet = false;
	
	protected NumberOfClientBasedBuilder() {
	}
	
	@ConstructorDef(def="Default constructor involving a subBuilder, a template generator, and a number of clients")
	public NumberOfClientBasedBuilder(AbstractBandwidthSpecifiedNBClientBuilder subBuilder, 
									  @ParamName(name="Number of clients") int nbClients,
									  @ParamName(name="Reference bandwidth") Rate bandwidth) {
		super();
		this.referenceBW = bandwidth;
		bandwidthSet = true;
		this.subBuilder = subBuilder;
		subBuilder.setBuilder(this);
		this.nbClients = nbClients;
		nbClientsSet = true;
	}
	
	@ConstructorDef(def="Let the bandwidth determined by the subBuilder")
	public NumberOfClientBasedBuilder(AbstractBandwidthCalculatedNBClientBuilder subBuilder, 
									  @ParamName(name="Number of clients") int nbClients) {
		super();
		this.subBuilder = subBuilder;
		subBuilder.setBuilder(this);
		this.nbClients = nbClients;
		nbClientsSet = true;
	}	

	@ConstructorDef(def="Constructor to use if the traffic generator determines the number of clients")
	public NumberOfClientBasedBuilder(AbstractBandwidthSpecifiedNBClientBuilder subBuilder,
			@ParamName(name="Reference bandwidth") Rate bandwidth) {
		super();
		this.referenceBW = bandwidth;
		bandwidthSet = true;
		this.subBuilder = subBuilder;
		subBuilder.setBuilder(this);
	}		
	

	@Override
	public Map<String, String> getAllParameters() {
		Map<String, String> m = SimpleMap.getMap("Number of clients", nbClients + "");
		m.put("Simulation builder type", subBuilder.getClass().getSimpleName());
		// this was buggy when there is no "if" condition
		if (bandwidthSet) {
			m.put("Reference bandwidth (Gb/s)", referenceBW.getInGbitSeconds()+"");
		}
		m.putAll(subBuilder.getAllParameters());
		return m;
	}

	@Override
	public InitFeedback build(LWSIMExperiment lwSimExperiment,
			ArrayList<LWSimComponent> dests) {
		lwSimExperiment.setReferenceBandwidth(referenceBW);
		if (!nbClientsSet) {
			this.nbClients = lwSimExperiment.getTrafficGenerator().getNumberOfClients();
			if (nbClients < 0)
				throw new IllegalStateException("Seems that the number of clients is defined no where");
		} else {
			boolean throw_ = false;
			try {
				int cl = lwSimExperiment.getTrafficGenerator().getNumberOfClients();
				if (cl != nbClients && cl > 0) {
					throw_ = true;
				}
			}
			catch (Exception e) {}
			if (throw_) throw new IllegalStateException("Traffic generator and builders have different vision in terms of number of clients");
		}
		return subBuilder.build(lwSimExperiment, dests, nbClients);
	}
	
	@Override
	public int getMaxPacketSizeInBits() {
		return subBuilder.getMaxPacketSizeInBits();
	}	
	
	@Override
	public int[][] getNeighborhood(int fromAnode) {
		return subBuilder.getNeighborhood(fromAnode);
	}	
	
	@Override
	public int getNumberOfClients() {
		// TODO Auto-generated method stub
		return nbClients;
	}
	
	public AbstractNumberOfClientConfigurableBuilder getSubBuilder() {
		return subBuilder;
	}

	@Override
	public Message getMessageToUse() {
		return new Message();
	}
	
	@Override
	public double getTotalInjectionBandwidthRatio() {
		return subBuilder.getTotalInjectionBandwidthRatio();
	}	
	
	@Override
	public void notifyEnd(double clock, int status) {	
		subBuilder.notifyEnd(clock, status);
	}

}
