package edu.columbia.lrl.experiments.spinet.variants;

import edu.columbia.lrl.experiments.spinet.TwoTwoSwitch;
import edu.columbia.lrl.general.Message;


public class ACK_TwoTwoSwitch extends TwoTwoSwitch {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ACK_TwoTwoSwitch(String id, double switchingTime, boolean doubleWay) {
		super(id, switchingTime, doubleWay);
	}

	@Override
	protected void packetDropped(Message m, double time, int type) {
	//	log("switch_dropping", m.origin + (double)m.dest/10, time);			
		lwSimExperiment.packetDropped(m, toString(), this, type);
	}
}
