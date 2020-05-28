package edu.columbia.lrl.experiments.lwsim_generic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.MoreArrays;
import ch.epfl.general_libraries.utils.MoreCollections;
import ch.epfl.general_libraries.utils.SimpleMap;

import edu.columbia.lrl.LWSim.AbstractTrafficGenerator;
import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.LWSim.LWSimComponent;
import edu.columbia.lrl.LWSim.TrafficDestination;
import edu.columbia.lrl.LWSim.builders.AbstractNumberOfClientConfigurableBuilder;
import edu.columbia.lrl.LWSim.components.Buffer;
import edu.columbia.lrl.LWSim.components.ElecSwitch;
import edu.columbia.lrl.LWSim.components.Receiver;
import edu.columbia.lrl.LWSim.components.TransmissionLink;
import edu.columbia.lrl.general.Evt;

public class FatTreeOverProvisionnedNetworkBuilder extends
		AbstractNumberOfClientConfigurableBuilder {
	
	public class ElecSwitchWithUplink extends ElecSwitch {
		
		Buffer upLinkPort;
		
		TransmissionLink upLink = new TransmissionLink(linkLength);
		TransmissionLink downLink = new TransmissionLink(linkLength);
		
		ArrayList<Integer> connectedIndexes = new ArrayList<>();

		public ElecSwitchWithUplink(int nbPorts, double switchProcessDelay, int level, boolean usePriorities) {
			super(nbPorts, switchProcessDelay, usePriorities);
			upLinkPort = new Buffer(10000, 0, 3000, nbPorts*overPro, usePriorities);
			upLinkPort.setTrafficDestination(upLink);
		}
		
		public void addDestination(TrafficDestination dest, int index) {
			output[index%aggregationFactor].setTrafficDestination(dest);
			connectedIndexes.add(index);
		}
		
		@Override
		public void processEvent(Evt e) {
			int dest = e.getMessage().dest;
			if (connectedIndexes.contains(dest)) {
				lwSimExperiment.manager.queueEvent(new Evt(e.getTimeNS() + switchProcessDelay, this, output[dest%aggregationFactor] , e));
			} else {
				lwSimExperiment.manager.queueEvent(new Evt(e.getTimeNS() + switchProcessDelay, this, upLinkPort, e));				
			}
		}
		
		@Override
		public InitFeedback initComponent(LWSIMExperiment lwSimExperiment) {
			InitFeedback failure = super.initComponent(lwSimExperiment);
			if (failure != null) return failure;
			
			upLinkPort.initComponent(lwSimExperiment);
			upLink.initComponent(lwSimExperiment);
			downLink.initComponent(lwSimExperiment);
			
			return null;
		}		
		
	}
	
	public class MasterSwitch extends ElecSwitch {
		
		HashMap<Integer, TrafficDestination> dests = new HashMap<>();
		
		ArrayList<TrafficDestination> toBeSetExperiments = new ArrayList<>();

		public MasterSwitch(int nbPorts, double switchProcessDelay,
				double outputRate, boolean usePriorities) {
			super(nbPorts, switchProcessDelay, outputRate, usePriorities);
		}
		
		public void addDestination(TrafficDestination dest, ArrayList<Integer> indexes, int rate) {
			Buffer buf = new Buffer(10000,10, 309823, rate, usePriorities);
			toBeSetExperiments.add(buf);
			for (int in : indexes) {
				dests.put(in, buf);
			}
			buf.setTrafficDestination(dest);
		}
		
		@Override
		public void processEvent(Evt e) {
			int dest = e.getMessage().dest;
			TrafficDestination des = dests.get(dest);
			if (des == null) {
			}
			lwSimExperiment.manager.queueEvent(new Evt(e.getTimeNS() + switchProcessDelay, this, des , e));
		}
		
		@Override
		public InitFeedback initComponent(LWSIMExperiment lwSimExperiment) {
			InitFeedback failure = super.initComponent(lwSimExperiment);
			if (failure != null) return failure;
			
			for (TrafficDestination d : toBeSetExperiments) {
				d.initComponent(lwSimExperiment);
			}
			
			return null;
		}			
	}
	
	@Override
	public int[][] getNeighborhood(int fromAnode) {
		int clusterId = fromAnode / aggregationFactor;
		int[] closeN = MoreArrays.range(clusterId*aggregationFactor, clusterId*aggregationFactor + aggregationFactor-1);
		int[] before = MoreArrays.range(0, clusterId*aggregationFactor -1);
		int[] after = MoreArrays.range(clusterId*aggregationFactor + aggregationFactor, this.builder.getNumberOfClients()-1);
		
		int[] comp = MoreArrays.concat(before, after);
		return new int[][]{closeN, comp};
	}	
	
	private int aggregationFactor;
	private double overPro;
	private int linkLength;
	private double switchLatency;
	private boolean usePriorities;
	
	public FatTreeOverProvisionnedNetworkBuilder(
			@ParamName(name="Aggregation factor") int aggregationFactor, 
			@ParamName(name="Over-provisionning (0.5 means half the bandwidth for up)") double overPro,
			 @ParamName(name="Link lengths") int linkLength, 
			 @ParamName(name="Switch latency") double switchLatency,
			 @ParamName(name="Use priorities ?") boolean usePriorities) {
		this.aggregationFactor = aggregationFactor;
		this.overPro = overPro;
		this.linkLength = linkLength;
		this.switchLatency = switchLatency;		
		this.usePriorities = usePriorities;
	}

	@Override
	public InitFeedback buildSubBuilder(ArrayList<LWSimComponent> dests, int nbClients) {
		
		int clientDone = 0;
		int actualInHub = 0;		
		
		ArrayList<ElecSwitchWithUplink> hubs = new ArrayList<>();
		
		ElecSwitchWithUplink elec = new ElecSwitchWithUplink(aggregationFactor, switchLatency, 1, usePriorities);

		while (clientDone < nbClients) {
			AbstractTrafficGenerator gen = lwSimExperiment.getTrafficGenerator().getCopy(1, clientDone);
			gen.setPossibleDestinationIndexesExcludingOne(MoreCollections.subsetOfN(0, nbClients -1), clientDone);
			dests.add(gen);
			Buffer b = new Buffer(10000, 0, clientDone, 1, usePriorities);
			dests.add(b);
			TransmissionLink towardHub = new TransmissionLink(linkLength);
			dests.add(towardHub);
			TransmissionLink fromHub = new TransmissionLink(linkLength);
			dests.add(fromHub);
			Receiver rec = this.getReceiver(clientDone);
			dests.add(rec);
			gen.setTrafficDestination(b);
			b.setTrafficOrigin(gen);			
			b.setTrafficDestination(towardHub);
			towardHub.setTrafficOrigin(b);
			towardHub.setTrafficDestination(elec);
			elec.addDestination(fromHub, clientDone);
			fromHub.setTrafficOrigin(elec);
			fromHub.setTrafficDestination(rec);

			clientDone++;
			actualInHub++;
			if (actualInHub == aggregationFactor) {
				actualInHub = 0;
				hubs.add(elec);
				dests.add(elec);				
				elec = new ElecSwitchWithUplink(aggregationFactor, switchLatency, 1, usePriorities);
			}
		}
		if (actualInHub > 0) {
			hubs.add(elec);
			dests.add(elec);
		}
		
		MasterSwitch ms = new MasterSwitch(hubs.size(), switchLatency, overPro, usePriorities);
		dests.add(ms);
		
		for (ElecSwitchWithUplink hub : hubs) {
			hub.upLink.setTrafficDestination(ms);
			hub.downLink.setTrafficDestination(hub);
			ms.addDestination(hub.downLink, hub.connectedIndexes, hub.connectedIndexes.size());
		}
		
		return null;
	}

	@Override
	public Map<String, String> getAllParameters() {
		return SimpleMap.getMap("Fat tree aggr. factor", aggregationFactor+"", "fat tree over pro", overPro+"");
	}

	@Override
	public int getMaxPacketSizeInBits() {
		return Integer.MAX_VALUE;
	}
	

	@Override
	public double getTotalInjectionBandwidthRatio() {
		int nbCli = builder.getNumberOfClients();
		return nbCli;
	}
	
	@Override	
	public void notifyEnd(double clock, int status) {
	}
}
