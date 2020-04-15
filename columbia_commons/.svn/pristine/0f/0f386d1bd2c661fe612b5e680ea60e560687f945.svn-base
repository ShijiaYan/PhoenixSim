package edu.columbia.lrl.LWSim.analysers;

import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.general_libraries.results.Execution;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.LWSim.LWSimComponent;
import edu.columbia.lrl.LWSim.TrafficDestination;
import edu.columbia.lrl.LWSim.components.Buffer;
import edu.columbia.lrl.general.Message;

public class BufferStateAnalyser extends AbstractLWSimAnalyser {
	
	int index = 0;
	int[] totalInBuf = new int[100];
	
	public void init(LWSIMExperiment exp) {
		super.init(exp);
		index = 0;
	}

	@Override
	public void addInfo(DataPoint basic, DataPoint globals, Execution e, double simTime) {
		
		for (int i = 0 ; i < totalInBuf.length ; i++) {
			DataPoint dp = globals.getDerivedDataPoint();
			
			dp.addProperty("index", i);
			dp.addResultProperty("Packets in buffer", totalInBuf[i]);
			e.addDataPoint(dp);
		}
		// TODO Auto-generated method stub

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
		if (totalInBuf.length <= index) {
			int[] novel = new int[totalInBuf.length *2];
			System.arraycopy(totalInBuf, 0, novel, 0, totalInBuf.length);
			totalInBuf = novel;
		}
		for (LWSimComponent comp : lwSimExp.getTopologyBuilder().getModelElements()) {
			if (comp instanceof Buffer) {
				totalInBuf[index] += ((Buffer) comp).getSize();
			}
		}
		index++;
	}

	@Override
	public void notifyEnd(double clock, int status) {
		// TODO Auto-generated method stub
		
	}

}
