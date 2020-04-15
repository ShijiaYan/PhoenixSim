package edu.columbia.lrl.LWSim.analysers;

import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.general_libraries.results.Execution;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.LWSim.TrafficDestination;
import edu.columbia.lrl.general.Message;

public class OvertimeTrafficAnalyser extends AbstractLWSimAnalyser {
	
	private double binLengthNS;

	
	private long[] emissionBins;
	private long[] receptionBins;
	
	
	
	public OvertimeTrafficAnalyser(double binLengthNS) {
		this.binLengthNS = binLengthNS;
	}
	
	@Override
	public void init(LWSIMExperiment exp) {
		super.init(exp);
		this.emissionBins = new long[200];	
		this.receptionBins = new long[200];			
	}	

	@Override
	public void addInfo(DataPoint basic, DataPoint globals, Execution e, double simTime) {
		
		int maxSize = Math.max(emissionBins.length, receptionBins.length);
		
		for (int i = 0 ; i < maxSize ; i++) {
			double timeSoFar = i*binLengthNS;
			if (timeSoFar > simTime) break;
			DataPoint dp = basic.getDerivedDataPoint();
			
			dp.addProperty("Time (ns)", timeSoFar);
			dp.addProperty("Bin length (ns)", binLengthNS);
			if (i < emissionBins.length) {
				dp.addResultProperty("Offered bits over time", emissionBins[i]);
				dp.addResultProperty("Offered load over time (Gb/s)", (double)emissionBins[i]/binLengthNS);	
			}
			if (i < receptionBins.length) {
				dp.addResultProperty("Delivered bits over time", receptionBins[i]);
				dp.addResultProperty("Delivered load over time (Gb/s)", (double)receptionBins[i]/binLengthNS);
			}
			e.addDataPoint(dp);
		}
		
		
		
	}

	@Override
	public void packetQuenched(Message m, String where) {}

	@Override
	public void packetContented(Message m, String where,
			TrafficDestination swi, int type) {}

	@Override
	public void packetTransmitted(Message m) {}
	
	
	@Override
	public void notifyEnd(double clock, int status) {
	}

	@Override
	public void packetEmitted(Message m) {
	//	try {
			int index = (int)Math.floor(m.timeEmitted / binLengthNS);
			if (emissionBins.length <= index) {
				long[] newBins = new long[Math.max(emissionBins.length*2, index+1)];
				System.arraycopy(emissionBins, 0, newBins, 0, emissionBins.length);
				emissionBins = newBins;
			}
			emissionBins[index] += m.sizeInBits;
	/*	}
		catch (Exception e) {
			int isw = 0;
		}*/
	}

	@Override
	public void packetReceived(Message m, int origin, int dest,
			double timeEmitted, double timeReceived) {

		double transTime = lwSimExp.getReferenceBandwidth().getTime(m.sizeInBits).getNanoseconds();
		int nbBins = (int)Math.ceil(transTime/binLengthNS);
		
		int index = (int)Math.floor(timeReceived / binLengthNS);
		if (receptionBins.length <= index + nbBins - 1) {
			long[] newBins = new long[Math.max(receptionBins.length*2, index + nbBins)];
			System.arraycopy(receptionBins, 0, newBins, 0, receptionBins.length);
			receptionBins = newBins;
		}
		// index is the first bin, now check if packet must be split into several bins

		if (nbBins == 0) {
			receptionBins[index] += m.sizeInBits;
		} else {
			int bits = m.sizeInBits;
			
			

			int bitsPerSlot = lwSimExp.getReferenceBandwidth().getSizeBitsNS(binLengthNS);
			for (int i = index ; i < index + nbBins - 1 ; i++) {
				receptionBins[i] += bitsPerSlot;
				bits -= bitsPerSlot;
			}
			receptionBins[index + nbBins -1] += bits;
		}		

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
