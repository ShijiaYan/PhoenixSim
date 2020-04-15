package edu.columbia.lrl.experiments.spinet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ch.epfl.general_libraries.traffic.Rate;
import ch.epfl.general_libraries.utils.MoreCollections;
import ch.epfl.general_libraries.utils.SimpleMap;
import edu.columbia.lrl.LWSim.AbstractTrafficGenerator;
import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.LWSim.LWSimComponent;
import edu.columbia.lrl.LWSim.components.Receiver;
import edu.columbia.lrl.LWSim.components.TransmissionLink;
import edu.columbia.lrl.experiments.spinet.link_model.AbstractLinkDistanceModel;
import edu.columbia.lrl.experiments.spinet.variants.SpinnetVariant;

public class FourFourSpinetBuilder extends AbstractSpinetBuilder {
	

	SpinnetVariant variant;
	
	public FourFourSpinetBuilder(AbstractLinkDistanceModel linkDistMod, SpinnetVariant variant, boolean trafToSelf, Rate refBW) {
		super(linkDistMod, trafToSelf, refBW);
		this.linkDistMod = linkDistMod;
		this.variant = variant;
	}
	
	@Override
	public int getMaxPacketSizeInBits() {
		return lwSimExp.getReferenceBandwidth().getSizeBitsNS(variant.getMaxPacketDurationNS());
	}	
	
	
	public int getNumberOfClients() {
		return 4;
	}
	
	public int getMaxNumberOf2by2SwitchStages() {
		return 2;
	}
	
	public Map<String, String> getAllParameters() {
		Map<String, String> map = SimpleMap.getMap(
													"clients", 4+"", 
												   "extra stages", 0 +"",
												   "sending traffic to self", trafToSelf + "");
		map.putAll(linkDistMod.getAllParameters());
		map.putAll(variant.getAllParameters());
		return map;
	}
	
	@Override
	public InitFeedback buildSpinet(LWSIMExperiment lwSimExperiment, ArrayList<LWSimComponent> dests) {
		AbstractTrafficGenerator[] gens = new AbstractTrafficGenerator[4];
		SpinnetBuffer[] bufs = new SpinnetBuffer[4];
		TransmissionLink[][] links = new TransmissionLink[2][4];
		TwoTwoSwitch[][] swis = new TwoTwoSwitch[2][2];
		Receiver[] recs = new Receiver[4];

		for (int i = 0 ; i < 4 ; i++) {
			gens[i] = lwSimExperiment.getTrafficGenerator().getCopy(1,i);
			List<Integer> destsIndexes = MoreCollections.subsetOfN(0, 3);
			if (trafToSelf) {
				gens[i].setPossibleDestinationIndexes__(destsIndexes);
			} else {
				gens[i].setPossibleDestinationIndexesExcludingOne(destsIndexes, i);
			}			
			dests.add(gens[i]);
			bufs[i] = variant.getExampleBuffer(i);
			dests.add(bufs[i]);
			for (int j = 0 ; j < 2 ; j++) {
				links[j][i] = new TransmissionLink(linkDistMod.getLinkLatency(i,4));
				dests.add(links[j][i]);
			}
			recs[i] = getReceiver(i, variant.getExampleReceiver());
			dests.add(recs[i]);
		}
		for (int i = 0 ; i < 2 ; i++) {
			for (int j = 0 ; j < 2 ; j++) {
				swis[i][j] = variant.getExampleSwitch(j+"-"+i);
				dests.add(swis[i][j]);
			}				
		} 
		for (int i = 0 ; i < 4 ; i++) {
			gens[i].setTrafficDestination(bufs[i]);
			bufs[i].setTrafficDestination(links[0][i]);
			links[1][i].setTrafficDestination(recs[i]);
		}
		for (int i = 0 ; i < 2 ; i++) {
			links[0][i*2].setTrafficDestination(swis[0][i]); // l_00->sw_00, l_02->sw_01
			links[0][(i*2)+1].setTrafficDestination(swis[0][i]); // l_01->sw_00, l_03->sw_01
			swis[0][i].setEventOrigin(links[0][i*2], 0);
			swis[0][i].setEventOrigin(links[0][(i*2)+1], 1);

			swis[1][i].setTrafficDestination(links[1][i*2], 0/* for up*/);
			swis[1][i].setTrafficDestination(links[1][(i*2)+1], 1 /* 1 for down*/);
		}
		
		swis[0][0].setTrafficDestination(swis[1][0], 0); 
		swis[0][0].setTrafficDestination(swis[1][1], 1); 
		swis[0][1].setTrafficDestination(swis[1][0], 0); 
		swis[0][1].setTrafficDestination(swis[1][1], 1);
		swis[1][0].setEventOrigin(swis[0][0], 0);
		swis[1][0].setEventOrigin(swis[0][1], 1);
		swis[1][1].setEventOrigin(swis[0][0], 0);
		swis[1][1].setEventOrigin(swis[0][1], 1);
		
		swis[0][0].setRoutingDecisions(new int[]{0,0,1,1});
		swis[0][1].setRoutingDecisions(new int[]{0,0,1,1});	
		swis[1][0].setRoutingDecisions(new int[]{0,1,-1,-1});
		swis[1][1].setRoutingDecisions(new int[]{-1,-1,0,1});	
			
	/*	for (int i = 0 ; i < 2 ; i++) {
			for (int j = 0 ; j < 2 ; j++) {
				System.out.println(swis[i][j].getMapping());
			}
		}*/															

		return null;
	}

	@Override
	public int[][] getNeighborhood(int fromAnode) {
		return new int[][]{{0,1,2,3}};
	}
	
	@Override
	public double getTotalInjectionBandwidthRatio() {
		return 4;
	}		

	@Override
	public void notifyEnd(double clock, int status) {	
	}		
}
