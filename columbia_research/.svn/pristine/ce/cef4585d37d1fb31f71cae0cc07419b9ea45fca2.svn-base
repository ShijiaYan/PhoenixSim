package edu.columbia.sebastien;

import edu.columbia.lrl.LWSim.AbstractTrafficGenerator;
import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.general.Evt;
import edu.columbia.lrl.general.Message;

public class GeorgeWashingtonTrafficGenerator extends AbstractTrafficGenerator {
	
	public int[] rates = {1501, 1059,943,1160,2108,6080,11133,9805,8444,	8331,	7317,	6598,	6312,	6243,	6684,	7349,	7789,	8163,	8002,	6424,	5075,	4221,	3532,	2558};


	@Override
	public void notifyEnd(double ref, double status) {
		// TODO Auto-generated method stub

	}

	@Override
	public String toShortString() {
		return "geo bridge";
	}

	@Override
	public void processEvent(Evt e) {
		double time = e.getTimeNS();
		int hour = (int)(((time/1000000d)+6) % 24);
		double rate = rates[hour];
		double interTime = 10000000d/rate;
		Evt newE = new Evt(time, this.getTrafficDestination());
		Message m = lwSimExperiment.getTopologyBuilder().getMessageToUse();
		m.sizeInBits = 100;
		newE.setMessage(m);
		m.timeEmitted =time;
		lwSimExperiment.packetEmitted(m);
		lwSimExperiment.manager.queueEvent(newE);
		Evt self = new Evt(time + interTime, this);
		lwSimExperiment.manager.queueEvent(self);
		
	}
	
	@Override
	public InitFeedback initComponent(LWSIMExperiment lwSimExperiment) {
		super.initComponent(lwSimExperiment);
		Evt e = new Evt(0, this);
		this.lwSimExperiment.manager.queueEvent(e);
		return null;
	}

	@Override
	public int getAveragePacketSize() {
		return 1;
	}

	@Override
	public AbstractTrafficGenerator getCopy(double loadCoeff, int index) {
		return new GeorgeWashingtonTrafficGenerator();
	}

	@Override
	public int getNumberOfClients() {
		return 0;
	}

}
