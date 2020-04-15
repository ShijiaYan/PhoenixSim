package edu.columbia.lrl.LWSim.analysers;

import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.general_libraries.results.Execution;

import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.LWSim.TrafficDestination;
import edu.columbia.lrl.general.Message;

public abstract class AbstractLWSimAnalyser {
	
	LWSIMExperiment lwSimExp;
	
	public void init(LWSIMExperiment exp) {
		this.lwSimExp = exp;
	}
	
	public abstract void addInfo(DataPoint basic, DataPoint globals, Execution e, double simTime);	
	
	public abstract void packetQuenched(Message m, String where);
	public abstract void packetContented(Message m, String where, TrafficDestination swi, int type);
	public abstract void packetTransmitted(Message m);	
	public abstract void packetEmitted(Message m);
	public abstract void packetReceived(Message m, int origin, int dest, double timeEmitted, double timeReceived);	
	public abstract void packetRetransmitted(Message m);
	public abstract void timeElapsed(double time);

	public abstract void notifyEnd(double clock, int status);
	
}
