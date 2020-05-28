package edu.columbia.lrl.CrossLayer.simulator.phy_builders;

import java.util.ArrayList;
import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.traffic.Rate;
import ch.epfl.general_libraries.utils.MoreCollections;
import ch.epfl.general_libraries.utils.SimpleMap;
import edu.columbia.lrl.CrossLayer.physical_models.layout.P2PLayout;
import edu.columbia.lrl.CrossLayer.physical_models.layout.AbstractPhysicalLayout;
import edu.columbia.lrl.CrossLayer.physical_models.util.AbstractLinkFormat;
import edu.columbia.lrl.CrossLayer.simulator.components.Demultiplexor;
import edu.columbia.lrl.CrossLayer.simulator.components.Multiplexer;
import edu.columbia.lrl.LWSim.AbstractTrafficGenerator;
import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.LWSim.LWSimComponent;
import edu.columbia.lrl.LWSim.builders.AbstractNumberOfClientConfigurableBuilder;
import edu.columbia.lrl.LWSim.builders.FullyMeshedNetworkBuilder;
import edu.columbia.lrl.LWSim.components.Receiver;
import edu.columbia.lrl.LWSim.components.SerializingBuffer;
import edu.columbia.lrl.LWSim.components.TransmissionLink;

public class PhysicalFullMeshBuilder extends FullyMeshedNetworkBuilder implements PhyWrapper {


	
	public PhysicalFullMeshBuilder(
			@ParamName(name = "Separated traffic", default_ = "false") boolean separatedTraffic, 
			@ParamName(name = "Traffic to self", default_ = "true") boolean selfTraf,
			@ParamName(name = "Use priorities", default_ = "false") boolean usePriorities, 
			@ParamName(name = "Jitter Penalty (dB)", default_ = "LINK_BW") TYPE type, 
			@ParamName(name = "Jitter Penalty (dB)", default_ = "2") int linkLatency) {
		super(separatedTraffic, selfTraf, usePriorities, type, linkLatency);
	}

	//	@Override
	public AbstractPhysicalLayout getPhysicalLayoutImpl(int clients) {
		return new P2PLayout(/*Nb couplers*/2);
	}			

//	@Override
	public InitFeedback buildPhysicalLayerBasedBuilder(
			AbstractNumberOfClientConfigurableBuilder stub,
			ArrayList<LWSimComponent> dests, int nbClients, double latencyNS) {
		
		int numClients = lwSimExperiment.getNumberOfClients();
		
		AbstractTrafficGenerator[] generators = new AbstractTrafficGenerator[numClients];
		Multiplexer[] multiplexers = new Multiplexer[numClients];
		SerializingBuffer[][] buffers = new SerializingBuffer[numClients][numClients - 1];
		TransmissionLink[][] links = new TransmissionLink[numClients][numClients - 1];
		Demultiplexor[] demultiplexors = new Demultiplexor[numClients];
		Receiver[] receivers = new Receiver[numClients];

		int index = 0;

		// Construct components, add to dests
		for (int i = 0; i < numClients; i++, index++) {
			generators[i] = lwSimExperiment.getTrafficGenerator().getCopy(1, index);
			multiplexers[i] = new Multiplexer(index);
			demultiplexors[i] = new Demultiplexor(index);
			receivers[i] = stub.getReceiver(index);

			dests.add(generators[i]);
			dests.add(multiplexers[i]);
			dests.add(demultiplexors[i]);
			dests.add(receivers[i]);

			// Each generator will index its link to dest from 0 to the
			// number of clients, excluding self
			generators[i].setPossibleDestinationIndexesExcludingOne(MoreCollections.subsetOfN(0, numClients - 1), index);

			for (int j = 0; j < numClients - 1; j++) {
				// In this case, we inflate the nominal link rate in the
				// load scheme to accommodate for multiple links, so adjust buffer
				// output rate
				buffers[i][j] = new SerializingBuffer(10000, 0, /* Don't care */-1, 1.0, false);
				links[i][j] = new TransmissionLink(latencyNS);
				dests.add(buffers[i][j]);
				dests.add(links[i][j]);

			}
		}

		// Connect components
		for (int i = 0; i < numClients; i++) {

			generators[i].setTrafficDestination(multiplexers[i]);
			demultiplexors[i].setTrafficDestination(receivers[i]);

			int subIndex = 0;
			for (int j = 0; j < numClients; j++) {

				if (i != j) {
					multiplexers[i].addDest(j, buffers[i][subIndex]);

					buffers[i][subIndex].setTrafficDestination(links[i][subIndex]);
					links[i][subIndex].setTrafficDestination(demultiplexors[j]);
					subIndex++;
				}
			}
		}

		return null;
	}	
	
	@Override
	public Map<String, String> getAllParameters() {
		return new SimpleMap<>(0);
	}

	@Override
	public int getMaxPacketSizeInBits() {
		return Integer.MAX_VALUE;
	}

	@Override
	public double getTotalInjectionBandwidthRatio() {
		int nbClients = lwSimExperiment.getNumberOfClients();
		return nbClients * nbClients;
	}

	@Override
	public int[][] getNeighborhood(int fromAnode) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getNumberOfOpticalInterfacesPerClient() {
		return lwSimExperiment.getNumberOfClients()-1;

	}

	@Override
	public void potentiallyImposeFormat(int nbClients, AbstractLinkFormat linkFormat) {
		// Do nothing, does not apply here
	}

	@Override
	public double getTotalpowerMW() {
		return -1; // let power be calculated in the standard way
	}

	@Override
	public Rate getSrcToDestRate(AbstractLinkFormat linkFormat) {
		return linkFormat.getAggregateRate();
	}

}
