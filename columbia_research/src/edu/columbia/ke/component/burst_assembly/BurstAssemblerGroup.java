package edu.columbia.ke.component.burst_assembly;

import java.util.ArrayList;

import edu.columbia.lrl.LWSim.AbstractTrafficOrigin;
import edu.columbia.lrl.LWSim.EventOrigin;
import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.LWSim.LWSimComponent;
import edu.columbia.lrl.LWSim.TrafficDestination;
import edu.columbia.lrl.LWSim.TrafficTransitPoint;
import edu.columbia.lrl.general.Evt;

public class BurstAssemblerGroup extends AbstractTrafficOrigin implements TrafficTransitPoint {
	
	private AbstractBurstAssembler[] BA;
	private int destinations;
	private LWSIMExperiment lwSimExperiment;

	public BurstAssemblerGroup(int destinations, AbstractBurstAssembler exampleBA, ArrayList<LWSimComponent> dests) {
		this.destinations = destinations;
		BA = new AbstractBurstAssembler[destinations];
		for (int i = 0; i < destinations; i++) {
			// add burst assembler between gen and buf
			try {
				BA[i] = (AbstractBurstAssembler) exampleBA.clone();
				dests.add(BA[i]);
			} catch (CloneNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public InitFeedback initComponent(LWSIMExperiment lwSimExperiment) {
		this.lwSimExperiment = lwSimExperiment;
		return null;
	}

	@Override
	public void notifyEnd(double ref, double status) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String toShortString() {
		// TODO Auto-generated method stub
		return null;
	}

	public void processEvent(Evt e) {
		int dest = e.getMessage().dest;
		BA[dest].addMessage(e);
	}

	@Override
	public void setTrafficOrigin(EventOrigin origin) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setTrafficDestination(TrafficDestination nextDest) {
		for (int i = 0; i < destinations; i++) 
			BA[i].setTrafficDestination(nextDest);
	}
}
