package edu.columbia.lrl.experiments.spinet.variants;

import java.util.Vector;

import ch.epfl.general_libraries.graphics.timeline.TimeLine;


import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.experiments.spinet.AbstractSpinetBuilder;
import edu.columbia.lrl.experiments.spinet.SpinnetBuffer;
import edu.columbia.lrl.general.Evt;

public class TDMSpinnetBuffer extends SpinnetBuffer {
	
	protected double tdmPeriod;
	protected double slotDuration;
	protected int slotNumber;

	protected Vector<Integer> slotForDestinations = new Vector<Integer>();
	protected Vector<Integer> destinationForSlot = new Vector<Integer>();
	protected double[] horizons;

	public TDMSpinnetBuffer(int maxSize, double bufferLatency,
			double spinetSwitchingTimeNS, double slotDuration, int index) {
		super(maxSize, bufferLatency, spinetSwitchingTimeNS, index);
		this.slotDuration = slotDuration;
	}
	
	public void setSlotIdForDestination(int id, int destination) {
		if (slotForDestinations.size() <= destination) {
			slotForDestinations.setSize(destination+1);
		}
		slotForDestinations.setElementAt(id, destination);
		
		if (destinationForSlot.size() <= id) {
			destinationForSlot.setSize(id+1);
		}
		destinationForSlot.setElementAt(destination, id);
	}
	
	public void setSlotNumber(int slotNumber) {
		this.slotNumber = slotNumber;
		this.tdmPeriod = slotNumber*slotDuration;
	}
	
	@Override 
	public InitFeedback initComponent(LWSIMExperiment lwSimExperiment) {
		if (slotDuration < 0) {
			int bits = lwSimExperiment.getTrafficGenerator().getAveragePacketSize();
			slotDuration = lwSimExperiment.getReferenceBandwidth().getTime(bits).getNanoseconds();
			AbstractSpinetBuilder builder = (AbstractSpinetBuilder)lwSimExperiment.getTopologyBuilder();
			slotDuration += spinetSwitchingTimeNS*builder.getMaxNumberOf2by2SwitchStages();
			this.tdmPeriod = slotNumber*slotDuration;
		}		
		horizons = new double[slotForDestinations.size()];
		for (int i = 0 ; i < horizons.length ; i++) {
			horizons[i] = getSlotOffset(i);

		}		
		return super.initComponent(lwSimExperiment);		
	}
	
	double packetDuration;
	
	protected boolean receiveEndOfTransmissionEvent(Evt e) {
		// report transmission time
		lwSimExperiment.reportTransTime(index, packetDuration, e.getMessage());
		return true;
	}
	
	@Override
	public void processEvent(Evt e) {
		
		if (e.getType() == 2){
			receiveEndOfTransmissionEvent(e);
			return;
		}
		
		defineSpinetMessage(e);
		int destination = e.getMessage().dest;
		
		/*if (this.index == 6 && destination == 1) {
			int iehieu = 0;
			double d = 0;
		}*/

		double horizonForDest = Math.max(horizons[destination], e.getTimeNS());
		
		double horizonRespectToPeriod = horizonForDest % tdmPeriod;
		double numberOfPeriods = (horizonForDest - horizonRespectToPeriod)/tdmPeriod;

		double horizonRespectToSlot = horizonRespectToPeriod - getSlotOffset(destination);
		
		packetDuration = lwSimExperiment.getReferenceBandwidth().getTime(e.getMessage().sizeInBits).getNanoseconds()+(builder.getMaxNumberOf2by2SwitchStages()*spinetSwitchingTimeNS);
		
		double departure;
		if (horizonRespectToSlot + packetDuration <= slotDuration) {
			// send in this slot			departure = horizonForDest;
			if (horizonRespectToSlot >= 0) {
				departure = horizonForDest;
			} else {
				departure = ((numberOfPeriods)*tdmPeriod) + getSlotOffset(destination);
			}
		} else {
			// schedule in next slot
			departure = ((1+numberOfPeriods)*tdmPeriod) + getSlotOffset(destination);	
		}
		
		Evt next = new Evt(departure, this, nextDest, e);
		lwSimExperiment.manager.queueEvent(next);
		
		Evt self = new Evt(departure + packetDuration, this, this, 2, e);	
		lwSimExperiment.manager.queueEvent(self);		
		
		// update horizon
		//for (int i = 0 ; i < horizons.length ; i++) {
			horizons[destination] = departure + packetDuration;
		//}
		
	//	System.out.println(index + " sending to " + e.getMessage().dest + " at time " + departure + "    new horizon : " + horizons[destination]);		
		
		if (lwSimExperiment.isWithTimeLine())			
			timeline.addJobPhase(departure, departure + packetDuration, TimeLine.EnumType.OK, "m:" + e.getMessage().index + "\n" + ((int)e.getTimeNS()) + "\nslot" + slotForDestinations.get(e.getMessage().dest));
		//	timeline.addJobPhase(departure, departure + packetDuration, 0, "m:" + e.getMessage().index  + "\r\n->" + e.getMessage().dest);
		
	}
	
	protected double getSlotOffset(int destination) {
		Integer d = slotForDestinations.get(destination);
		if (d != null)
			return d*slotDuration;
		return -1;
	}

}
