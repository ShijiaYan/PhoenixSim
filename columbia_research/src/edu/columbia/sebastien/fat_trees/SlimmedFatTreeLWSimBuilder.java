package edu.columbia.sebastien.fat_trees;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.epfl.general_libraries.utils.MoreCollections;
import ch.epfl.general_libraries.utils.SimpleMap;
import ch.epfl.general_libraries.utils.TypeParser;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.network.LinkContainer;
import ch.epfl.javanco.network.NodeContainer;
import edu.columbia.lrl.LWSim.AbstractTrafficGenerator;
import edu.columbia.lrl.LWSim.EventOrigin;
import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.LWSim.LWSimComponent;
import edu.columbia.lrl.LWSim.TrafficDestination;
import edu.columbia.lrl.LWSim.builders.AbstractNumberOfClientConfigurableBuilder;
import edu.columbia.lrl.LWSim.components.Buffer;
import edu.columbia.lrl.LWSim.components.Receiver;
import edu.columbia.lrl.LWSim.components.TransmissionLink;
import edu.columbia.lrl.experiments.topology_radix.locality.AbstractTrafficMatrix;
import edu.columbia.lrl.general.Evt;


public class SlimmedFatTreeLWSimBuilder extends 
	AbstractNumberOfClientConfigurableBuilder {
	
	private int radix;
	private AbstractTrafficMatrix trafMat;
	
	private double linkLength = 400;
	
	public SlimmedFatTreeLWSimBuilder(int radix, AbstractTrafficMatrix trafMat) {
		this.radix = radix;
		this.trafMat = trafMat;
	}

	@Override
	public InitFeedback buildSubBuilder(ArrayList<LWSimComponent> dests,
			int nbClients) {
		SlimmedFatTreeGenerator slgen = new SlimmedFatTreeGenerator(nbClients, radix, trafMat, false, true);

		AbstractGraphHandler agh = slgen.generate();
	//	agh.saveGraphImage(s, w, h)
		TransmissionLink[] down = new TransmissionLink[agh.getNumberOfNodes()];
		TransmissionLink[] up = new TransmissionLink[agh.getNumberOfNodes()];
		
		
		for (int i = 0 ; i < nbClients ; i++) {
			AbstractTrafficGenerator gen = lwSimExperiment.getTrafficGenerator().getCopy(1, i);
			gen.setPossibleDestinationIndexesExcludingOne(MoreCollections.subsetOfN(0, nbClients -1), i);
			dests.add(gen);
			Buffer b = new Buffer(10000, 0, i, 1, false);
			dests.add(b);
			TransmissionLink towardHub = new TransmissionLink(linkLength);
			dests.add(towardHub);
			TransmissionLink fromHub = new TransmissionLink(linkLength);
			dests.add(fromHub);
			Receiver rec = this.getReceiver(i);
			dests.add(rec);
			gen.setTrafficDestination(b);
			b.setTrafficOrigin(gen);			
			b.setTrafficDestination(towardHub);
			towardHub.setTrafficOrigin(b);
			fromHub.setTrafficDestination(rec);
			down[i] = fromHub;
			up[i] = towardHub;
		}
		
		HashMap<Integer, TreeSwitch> mm = new HashMap<Integer, TreeSwitch>();
		
		// check problem of larger bandwidth on next hop
		for (int i = nbClients ; i < agh.getNumberOfNodes() ; i++) {
			up[i] = new TransmissionLink(linkLength);
			down[i] = new TransmissionLink(linkLength);
			TreeSwitch ts = new TreeSwitch();
			dests.add(ts);
			dests.add(up[i]);
			dests.add(down[i]);
			ts.nc = agh.getNodeContainer(i);
			mm.put(i, ts);
		}
		
		for (int i = nbClients ; i < agh.getNumberOfNodes() ; i++) {
			TreeSwitch ts = mm.get(i);
			double band;
			if (i != agh.getHighestNodeIndex()) {
				LinkContainer upL = ts.nc.getOutgoingLinks().get(0);
				if (upL.attribute("label", false) != null) {
					band = Double.parseDouble(upL.attribute("label").getValue().replaceAll("x", ""));
				} else {
					band = 1;
				}
				ts.addUpLink(up[i], down[i], band);
				down[i].setTrafficDestination(ts);
			}
			List<LinkContainer> downList = ts.nc.getIncomingLinks();
			for (LinkContainer lc : downList) {
				if (lc.attribute("label", false) == null) {
					band = 1; 
				} else {
					band = Double.parseDouble(lc.attribute("label").getValue().replaceAll("x", ""));
				}
				Integer[] des = TypeParser.parseIntegerArray(lc.attribute("dests").getValue());
				int ext = lc.getOtherNodeIndex(i);
				ts.addDownLink(down[ext], up[ext], band, des);
			}
		}
		
		
		return null;
	}
	
	private class TreeSwitch implements TrafficDestination, EventOrigin {
		
		LWSIMExperiment exp;
		HashMap<Integer, Buffer> map = new HashMap<Integer, Buffer>();
		Buffer upBuf;
		NodeContainer nc;

		@Override
		public InitFeedback initComponent(LWSIMExperiment lwSimExperiment) {
			this.exp = lwSimExperiment;
			for (Buffer b : map.values()) {
				b.initComponent(lwSimExperiment);
			}
			if (upBuf != null) {
				upBuf.initComponent(lwSimExperiment);
			}
			return null;
		}

		public void addDownLink(TransmissionLink fromSwitchDown,
				TransmissionLink toSwitchUp, double band, Integer[] des) {
			Buffer down = new Buffer(10000, 0, -1, band, false);
			for (Integer i : des) {
				map.put(i, down);
			}
			down.setTrafficDestination(fromSwitchDown);
			fromSwitchDown.setTrafficOrigin(down);
			toSwitchUp.setTrafficDestination(this);
		}

		public void addUpLink(TransmissionLink fromSwitchUp,
				TransmissionLink toSwitchDown, double band) {
			upBuf = new Buffer(10000, 0, -1, band, false);
			upBuf.setTrafficDestination(fromSwitchUp);
			fromSwitchUp.setTrafficOrigin(upBuf);
			toSwitchDown.setTrafficDestination(this);
		}

		@Override
		public void notifyEnd(double ref, double status) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public String toShortString() {
			return "TreeSwitch";
		}

		@Override
		public void processEvent(Evt e) {
			Buffer b = map.get(e.getMessage().dest);
			Evt eNew;
			if (b == null) {
//				System.out.println(e.getMessage() + " forwarded up at " + nc.getIndex());
				eNew = new Evt(e.getTimeNS(), this, upBuf, e);
			} else {
//				System.out.println(e.getMessage() + " forwarded down at " + nc.getIndex());
				eNew = new Evt(e.getTimeNS(), this, b, e);
			}
			exp.manager.queueEvent(eNew);
			
		}
		
	}

	@Override
	public Map<String, String> getAllParameters() {
		Map<String, String> m = new SimpleMap<String, String>();
		m.put("radix", radix+"");
		m.putAll(trafMat.getAllParameters());
		return m;
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
	public int[][] getNeighborhood(int fromAnode) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	@Override	
	public void notifyEnd(double clock, int status) {
	}
}
