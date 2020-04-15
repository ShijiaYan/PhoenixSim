package edu.columbia.lrl.LWSim.analysers;

import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.general_libraries.results.Execution;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.LWSim.TrafficDestination;
import edu.columbia.lrl.general.Message;

public class WarmedUpLatencyAnalyser extends AbstractLWSimAnalyser {
	
	public WarmedUpLatencyAnalyser(int packetsPerGroup) {
		this(packetsPerGroup, 100);
	}
	
	public WarmedUpLatencyAnalyser(int packetsPerGroup, double interGroupToleranceInNs) {
		this(packetsPerGroup, interGroupToleranceInNs, true);
	}
	
	public WarmedUpLatencyAnalyser(int packetsPerGroup, double interGroupToleranceInNs, boolean store) {
		this.packetsPerGroup = packetsPerGroup;
		this.tolerance = interGroupToleranceInNs;
		this.store = store;
	}
	
	
	public void init(LWSIMExperiment exp) {
		super.init(exp);
		totalLatencies = new double[10];
	}
	
	double[] totalLatencies;
	int packetsPerGroup;
	double tolerance;
	boolean store;

	@Override
	public void addInfo(DataPoint basic, DataPoint globals, Execution e, double simTime) {
		double totalLatAccum = 0;
		int indexAccum = 0;
		for (int i = 0 ; i < totalLatencies.length-1 ; i++) {
			if (totalLatencies[i] == 0) break;
			if (store) {
				DataPoint dp = lwSimExp.getDerivedDatapoint();
				dp.addProperty("k-th packets group", i);
				dp.addResultProperty("groupep latency", totalLatencies[i]/(double)packetsPerGroup);
				dp.addResultProperty("latency diff", totalLatencies[i+1] - totalLatencies[i]);
				e.addDataPoint(dp);
			}
			if (Math.abs((totalLatencies[i+1] - totalLatencies[i])/(double)packetsPerGroup) < tolerance) {
				totalLatAccum += totalLatencies[i];
				indexAccum++;
			}
			
		}
		if (indexAccum > 0)
			globals.addResultProperty("Warmed up latency", totalLatAccum/((double)indexAccum*packetsPerGroup));

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
	public void notifyEnd(double clock, int status) {
		// TODO Auto-generated method stub
	}	

	@Override
	public void packetTransmitted(Message m) {
		// TODO Auto-generated method stub

	}

	@Override
	public void packetEmitted(Message m) {
		// TODO Auto-generated method stub

	}

	@Override
	public void packetReceived(Message m, int origin, int dest,
			double timeEmitted, double timeReceived) {
		int index = (int)Math.floor(lwSimExp.totalReceived/packetsPerGroup);
		if (totalLatencies.length <= index) {
			double[] newTab = new double[index+20];
			System.arraycopy(totalLatencies, 0, newTab, 0, totalLatencies.length);
			totalLatencies = newTab;
		}
		totalLatencies[index] += timeReceived - timeEmitted;
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
