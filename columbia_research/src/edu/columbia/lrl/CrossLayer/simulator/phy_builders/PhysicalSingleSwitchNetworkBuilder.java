package edu.columbia.lrl.CrossLayer.simulator.phy_builders;

import java.util.Map;

import ch.epfl.general_libraries.traffic.Rate;
import edu.columbia.lrl.CrossLayer.physical_models.layout.AbstractSwitchFabric;
import edu.columbia.lrl.CrossLayer.physical_models.layout.P2PLayout;
import edu.columbia.lrl.CrossLayer.physical_models.layout.PhysicalLayout;
import edu.columbia.lrl.CrossLayer.physical_models.layout.SingleSwitchNetworkLayout;
import edu.columbia.lrl.CrossLayer.physical_models.util.AbstractLinkFormat;
import edu.columbia.lrl.CrossLayer.simulator.components.Arbiter;
import edu.columbia.lrl.LWSim.builders.HubNetworkBuilder;

public class PhysicalSingleSwitchNetworkBuilder extends HubNetworkBuilder implements PhyWrapper {
	
	public PhysicalSingleSwitchNetworkBuilder(boolean sep, boolean self,
			int linkLength, double switchLatency, double bufferLatency) {
		super(sep, self, linkLength, switchLatency, bufferLatency);
		// TODO Auto-generated constructor stub
	}

	private AbstractSwitchFabric switchFabric;
	private Arbiter arbiter;
	

	
/*	public PhysicalSingleSwitchNetworkBuilder(
			@ParamName(name="Arbiter", defaultClass_ = RoundRobinArbiter.class) Arbiter arbiter,
			@ParamName(name = "The switch fabric") AbstractSwitchFabric fabric
			) {
		this.arbiter = arbiter;
		this.switchFabric = fabric;
	}*/

/*	@Override
	public InitFeedback buildPhysicalLayerBasedBuilder(
			AbstractNumberOfClientConfigurableBuilder stub,
			ArrayList<LWSimComponent> dests, int numClients, double latencyNS) {

		Transmitter[] transmitters = new Transmitter[numClients];
		TransmissionLink[] links = new TransmissionLink[numClients];
		Receiver[] receivers = new Receiver[numClients];
		
		ElecSwitch sw = new ElecSwitch(numClients, 0, 1, false, 0);
		dests.add(sw);
		
		dests.add(arbiter);
		
		//clear arbiter
		arbiter.clear();
		arbiter.setNumOutputs(numClients); //for building virtual output queues
		
		//instantiate components
		for( int i = 0; i < numClients; i++ ) {
			AbstractTrafficGenerator traffic = lwSimExperiment.getTrafficGenerator().getCopy(1, i);
			Collection<Integer> destinations = MoreCollections.subsetOfN(0, numClients -1);
			traffic.setPossibleDestinationIndexesExcludingOne(destinations, i);
			transmitters[i] = arbiter.getTransmitter(i, destinations);
			traffic.setTrafficDestination(transmitters[i]);

			links[i] = new TransmissionLink(latencyNS);
			
			receivers[i] = stub.getReceiver(i);
			
			arbiter.registerInput(transmitters[i], i);
			//arbiter.registerOutput(receivers[i], i);
			
			dests.add(traffic);
			dests.add(transmitters[i]);
			dests.add(links[i]);
			dests.add(receivers[i]);
		//	dests.add(transmitters[i].getSerializationTimer());
		}
		
		//connect components
		for( int i = 0; i < numClients; i++) {
			transmitters[i].setTrafficDestination(links[i]);
			links[i].setTrafficDestination(sw);
			sw.addDestination(receivers[i], i);
		}
		
		return null;		
	}	*/

	@Override
	public PhysicalLayout getPhysicalLayoutImpl(int clients) {
		// TODO : have this class calling somehow an 'arbiter.setFabric()' statement
		// perhaps should be done through a "connectElement hierarchical command similar to initComponent
		switchFabric.setSwitchRadix(clients);
		return new SingleSwitchNetworkLayout(switchFabric, new P2PLayout(2));
	}

	@Override
	public Map<String, String> getAllParameters() {
		Map<String, String> m = super.getAllParameters();
		m.putAll(arbiter.getAllParameters());
		m.put("Switch fabric", switchFabric+"");
		m.putAll(switchFabric.getAllParameters());
		return m;
	}

	
	@Override
	public int getNumberOfOpticalInterfacesPerClient() {
		return 1;
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
