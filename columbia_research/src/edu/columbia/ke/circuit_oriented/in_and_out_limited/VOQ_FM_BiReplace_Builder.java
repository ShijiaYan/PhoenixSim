package edu.columbia.ke.circuit_oriented.in_and_out_limited;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.traffic.Rate;
import edu.columbia.ke.circuit_oriented.AbstractVOQFullyMeshedBuilder;
import edu.columbia.lrl.CrossLayer.physical_models.layout.AbstractPhysicalLayout;
import edu.columbia.lrl.CrossLayer.physical_models.layout.TunableLaserMuxDemuxLayout;
import edu.columbia.lrl.CrossLayer.physical_models.util.AbstractLinkFormat;
import edu.columbia.lrl.CrossLayer.simulator.phy_builders.PhyWrapper;

public class VOQ_FM_BiReplace_Builder extends AbstractVOQFullyMeshedBuilder implements PhyWrapper {
	
	private AbstractPhysicalLayout phyLayout;

	public VOQ_FM_BiReplace_Builder(
			@ParamName(name="A VOQ template") AbstractDstTerminateVOQ voqTemplate,	
			@ParamName(name = "Max number of circuits per node", default_ = "10") int circuitPerNode,
			@ParamName(name = "Circuit setup latency", default_ = "10000") double circuitSetupLatency,
			@ParamName(name = "Max vacant time", default_ = "-1") double maxVacantTime,
			@ParamName(name = "Include self as traffic dest?", default_ = "true") boolean self,
			@ParamName(name="Add src-dest time line?", default_="false") boolean sdTimeline,
			@ParamName(name="Add src time line?", default_="true") boolean srcTimeline) {
		super(voqTemplate, circuitPerNode, circuitSetupLatency, maxVacantTime, self, sdTimeline, srcTimeline);
	}
	
	public VOQ_FM_BiReplace_Builder(
			@ParamName(name="A VOQ template") AbstractDstTerminateVOQ voqTemplate,	
			@ParamName(name = "Max number of circuits per node", default_ = "10") int circuitPerNode,
			@ParamName(name = "Max vacant time", default_ = "-1") double maxVacantTime,
			@ParamName(name = "Include self as traffic dest?", default_ = "true") boolean self, 
			@ParamName(name="Add src-dest time line?", default_="false") boolean sdTimeline,
			@ParamName(name="Add src time line?", default_="true") boolean srcTimeline,
			@ParamName(name="Physical layout") TunableLaserMuxDemuxLayout phyLayout) {
		super(voqTemplate, circuitPerNode, phyLayout.getUnavailabilityTime(), maxVacantTime, self, sdTimeline, srcTimeline);
		this.phyLayout = phyLayout;
	}
	
	@Override
	public Map<String, String> getAllParameters() {
		Map<String, String> m = super.getAllParameters();
		if (phyLayout != null)
			m.putAll(phyLayout.getAllParameters());
		return m;
	}


	protected void instantiateVoq(int i, int parentID, int nDestPerVoq, int circuitPerVoq){
		
		voq[i] = voqTemplate.getCopy(parentID, nDestPerVoq, circuitPerVoq);
		voq[i].setTimeline(sdTimeline, srcTimeline);
		// populate the VOQ
	/*	if (this.voqType.equals("LRU_CL_VOQ")) {
			voq[i] = new LRU_BI_CL_VOQ(i, nDestPerVoq,
					this.circuitSetupLatency, circuitPerNode, maxVacantTime,
					dests);
		} else if (this.voqType.equals("LRU2_CL_VOQ")) {
			voq[i] = new LRU2_BI_CL_VOQ(i, nDestPerVoq,
					this.circuitSetupLatency, circuitPerNode, maxVacantTime,
					dests);
		} else if (this.voqType.equals("ReuseScore_CL_VOQ")) {
			voq[i] = new ReuseScore_BI_CL_VOQ(i, nDestPerVoq,
					this.circuitSetupLatency, circuitPerNode, maxVacantTime,
					dests);
		} else if (this.voqType.equals("NC_ReusePredicted_CL_VOQ")) {
			voq[i] = new NC_ReusePredicted_BI_CL_VOQ(i, nDestPerVoq,
					this.circuitSetupLatency, circuitPerNode, maxVacantTime,
					dests);
		} else if (this.voqType.equals("WC_ReusePredicted_CL_VOQ")) {
			voq[i] = new WC_ReusePredicted_BI_CL_VOQ(i, nDestPerVoq,
					this.circuitSetupLatency, circuitPerNode, maxVacantTime,
					dests);
		} else {
			throw new IllegalStateException("No corresponding VOQ type found!");
		}*/

		AbstractDstTerminateVOQ v = (AbstractDstTerminateVOQ) voq[i];
		v.setRecv(recs);
		
	}

/*	@Override
	public Receiver getCorrespondingReceiver() {
		
		
		ReuseAwareReceiver a;
		if (this.voqType.equals("LRU_CL_VOQ")){
			a = new LRU_Receiver(index, this.nClient, circuitPerNode);
		}
		else if (this.voqType.equals("ReuseScore_CL_VOQ")){
			a = new ReuseScore_Receiver(index, this.nClient, circuitPerNode);
		}
		else if (this.voqType.equals("WC_ReusePredicted_CL_VOQ")){
			a = new WC_ReusePredicted_Receiver(index, this.nClient, circuitPerNode);
		}
		else{
			throw new IllegalStateException("No corresponding VOQ type found!");
		}
		
		ReuseAwareReceiver a = voqTemplate.getAssociatedReceiver(this.nClient, this.circuitPerNode);
		a.setVOQ(voq);
		return a;
		
	}*/
	
	@Override
	public double getTotalInjectionBandwidthRatio() {
		return circuitPerNode * this.nClient;
	}		


	@Override
	public int getNumberOfOpticalInterfacesPerClient() {
		return circuitPerNode;
	}


	@Override
	public AbstractPhysicalLayout getPhysicalLayoutImpl(int clients) {
		return phyLayout;
	}

	@Override
	public void potentiallyImposeFormat(int nbClients,
			AbstractLinkFormat linkFormat) {
		if (linkFormat.isNumberOfChannelFixed()) {
			if (nbClients > linkFormat.getNumberOfChannels()) {
				throw new WrongExperimentException("Cannot support " + nbClients + " with only " + linkFormat.getNumberOfChannels() + " channels");
			} else {
				System.out.println("WARNING: number of channels not directly related to number of clients");
			}
		} else {
			linkFormat.setNumberOfWavelengths(nbClients);
		}	
	}
	
	@Override
	public double getTotalpowerMW() {
		return -1; // let power be calculated in the standard way
	}	
	
	@Override
	public Rate getSrcToDestRate(AbstractLinkFormat linkFormat) {
		return linkFormat.getWavelengthRATE();
	}	

	
}
