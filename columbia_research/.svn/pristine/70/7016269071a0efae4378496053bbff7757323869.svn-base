package edu.columbia.ke.component.burst_assembly;

import java.util.Map;

import ch.epfl.general_libraries.utils.SimpleMap;
import edu.columbia.lrl.LWSim.AbstractTrafficOrigin;
import edu.columbia.lrl.LWSim.EventOrigin;
import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.LWSim.TrafficTransitPoint;
import edu.columbia.lrl.general.Evt;

public abstract class AbstractBurstAssembler extends AbstractTrafficOrigin implements TrafficTransitPoint, Cloneable {
	protected LWSIMExperiment lwSimExperiment;
	protected BurstMessage burst;
	
	public BurstMessage getBurst() {
		return burst;
	}

	public AbstractBurstAssembler() {
	}
	
	public abstract String getBurstAssemblerName();	

	public void setTrafficOrigin(EventOrigin origin) {
	//	this.trafficOrigin = origin;
	}
	
	@Override
	public InitFeedback initComponent(LWSIMExperiment lwSimExperiment) {
		this.lwSimExperiment = lwSimExperiment;
		return null;
	}	
	
	/*
	 * For now, processEvent only handles the time-window-ending event
	 */
	@Override
	public void processEvent(Evt e) {
		double now = e.getTimeNS();

		/*
		 * check if the burst is still here
		 */
		if (burst == null)
			throw new IllegalStateException("burst == null");
		
		if (this.burst != e.getMessage())	// burst has been sent
			return;
		else {
			Evt next = new Evt(now, this, nextDest, e);
			next.setMessage(burst);
			lwSimExperiment.manager.queueEvent(next);
			burst = new BurstMessage();
			return;
		}

	}
	
	public abstract void addMessage(Evt e);
	
	public Object clone() throws CloneNotSupportedException {
        return super.clone();
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
	
	public Map<String, String> getAllParameters() {
		return SimpleMap.getMap("Burst Assembler", this.getClass().getSimpleName());
	}	
}
