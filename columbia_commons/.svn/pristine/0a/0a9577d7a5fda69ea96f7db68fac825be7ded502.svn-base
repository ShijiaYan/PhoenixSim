package edu.columbia.lrl.LWSim.analysers;

import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.general_libraries.results.Execution;
import ch.epfl.general_libraries.traffic.Rate;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.LWSim.TrafficDestination;
import edu.columbia.lrl.general.Message;

public class DeadLineAnalyser extends AbstractLWSimAnalyser {
	
	private long totalQuenched = 0;
	private long inTimeReceived = 0;
	private double totalDelay = 0;
	private long inTimeBitsReceived = 0;
	
	@Override
	public void init(LWSIMExperiment exp) {
		// TODO Auto-generated method stub
		super.init(exp);
		totalQuenched = 0;
		inTimeReceived = 0;
		totalDelay = 0;
		inTimeBitsReceived = 0;
	}
	
	@Override
	public void packetTransmitted(Message m) {
		// TODO Auto-generated method stub	
	}
	
	@Override
	public void notifyEnd(double clock, int status) {
		// TODO Auto-generated method stub
	}	

	@Override
	public void packetEmitted(Message m) {
		// TODO Auto-generated method stub
		
	}		
	
	protected double getMsgDeadline(Message m) {
		return m.getDeadline();
	}
	
	public void packetReceived(Message m, int origin, int dest, double timeEmitted, double timeReceived) {	
		double msgDeadline = getMsgDeadline(m);
		
		/*
		 * case for messages in a burst
		 */
		//msgDeadline = m.timeEmitted + m.getInitTimeToLive() + packetTransTime;
		
		// below are for common cases
		if(msgDeadline >= timeReceived) {
			this.inTimeReceived++;
			this.inTimeBitsReceived += m.sizeInBits;
		} else
			totalDelay += timeReceived - msgDeadline;
	}
	
	public void addInfo(DataPoint basic, DataPoint globals, Execution e, double simTime) {
		int clients = lwSimExp.getTopologyBuilder().getNumberOfClients();
		globals.addResultProperty("In-Time Delivery Rate (over total emitted)", (double)inTimeReceived/lwSimExp.totalEmitted);
		globals.addResultProperty("In-Time Delivery Rate (over total received)", (double)inTimeReceived/lwSimExp.totalReceived);
		globals.addResultProperty("Packet Quenched Rate", (double)totalQuenched/lwSimExp.totalEmitted);	
		globals.addResultProperty("Avg. Packet Delay", totalDelay/(double)lwSimExp.totalReceived);		
		
		long inTimeBitsReceivedPerClient = (long)Math.ceil(inTimeBitsReceived/(double)clients);
		Rate observedReceived = new Rate(inTimeBitsReceivedPerClient, simTime/1000d);
		observedReceived.setDataSizeUnit("gbit");
		observedReceived.setTimeUnit("s");
		globals.addResultProperty("Good Utilization", observedReceived.divide(this.lwSimExp.getReferenceBandwidth()));
	}


	@Override
	public void packetQuenched(Message m, String where) {
		totalQuenched++;
	}


	@Override
	public void packetContented(Message m, String where,TrafficDestination swi, int type) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void packetRetransmitted(Message m) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void timeElapsed(double time) {
		// TODO Auto-generated method stub
		
	}

}
