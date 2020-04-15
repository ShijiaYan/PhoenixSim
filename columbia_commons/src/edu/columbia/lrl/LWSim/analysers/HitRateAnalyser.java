package edu.columbia.lrl.LWSim.analysers;

import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.general_libraries.results.Execution;
import edu.columbia.lrl.LWSim.TrafficDestination;
import edu.columbia.lrl.general.Message;

public class HitRateAnalyser extends AbstractLWSimAnalyser {
	
	public void addInfo(DataPoint basic, DataPoint globals, Execution e, double simTime) {
		
		globals.addResultProperty("Hit Rate", (double)lwSimExp.nHit/lwSimExp.nCircuitUse);
		globals.addResultProperty("Prediction Accuracy", (double)lwSimExp.nAccuratePredict/lwSimExp.nCircuitUse);
		globals.addResultProperty("Energy Expense", lwSimExp.calcTotalPower());
		globals.addResultProperty("Circuit Utilization", lwSimExp.totalReceived/(lwSimExp.getReferenceBandwidth().getInGbitSeconds()*lwSimExp.totalCacheTime));
		globals.addResultProperty("Vacant Percentage", lwSimExp.totalVacantTime/lwSimExp.totalCacheTime);
		globals.addResultProperty("Vacant Time", lwSimExp.totalVacantTime);
		
		globals.addResultProperty("Prefetch Efficiency", (double) lwSimExp.nPrefetchHit/lwSimExp.nPrefetch);
		globals.addResultProperty("Effective Prefetch within Tail Length (%)", (double) lwSimExp.nPfHitWithinTail/lwSimExp.nPrefetch);
		globals.addResultProperty("Hit And Ready", (double)lwSimExp.nHitAndReady/lwSimExp.nCircuitUse);
	}

	@Override
	public void packetQuenched(Message m, String where) {
		// TODO Auto-generated method stub	
	}

	@Override
	public void packetContented(Message m, String where,
			TrafficDestination swi, int type) {
		// TODO Auto-generated method stub
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

	@Override
	public void packetReceived(Message m, int origin, int dest,
			double timeEmitted, double timeReceived) {
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
