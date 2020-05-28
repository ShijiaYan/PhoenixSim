package edu.columbia.ke.circuit_oriented;

import java.util.TreeMap;

import edu.columbia.lrl.LWSim.EventOrigin;
import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.LWSim.TrafficDestination;
import edu.columbia.lrl.LWSim.TrafficTransitPoint;
import edu.columbia.lrl.general.Evt;

public class Dst2VoqMapper implements TrafficTransitPoint {
	
	TreeMap<Integer, TrafficDestination> destMap = new TreeMap<>();
	private LWSIMExperiment lwSimExperiment;
	
	public void addDestination(TrafficDestination dest, int dstId) {
		destMap.put(dstId, dest);
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

	@Override
	public void processEvent(Evt e) {
		int msgDest = e.getMessage().dest;
		double now = e.getTimeNS();
		Evt next = new Evt(now, this, destMap.get(msgDest), 0, e);		//type = 0, as a producer event for next dest (VOQ) 
		lwSimExperiment.manager.queueEvent(next);
	}

	@Override
	public void setTrafficDestination(TrafficDestination sw) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setTrafficOrigin(EventOrigin origin) {
		// TODO Auto-generated method stub

	}

}
