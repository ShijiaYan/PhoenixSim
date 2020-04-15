package edu.columbia.lrl.LWSim.analysers;

import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.general_libraries.results.Execution;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.LWSim.TrafficDestination;
import edu.columbia.lrl.general.Message;

public class TrialsAnalyser extends AbstractLWSimAnalyser {
	
	private long totalTransmitTrials;
	private long firstTrialCounter;
	private long firstTimeSuccessCounter;
	private long totalQuenched;
	
	
	
	@Override
	public void init(LWSIMExperiment exp) {
		// TODO Auto-generated method stub
		super.init(exp);
		totalTransmitTrials = 0;
		firstTrialCounter = 0;
		firstTimeSuccessCounter = 0;
		totalQuenched = 0;
	}

	public void packetTransmitted(Message m) {
		this.totalTransmitTrials++;
		if (m.numTrans == 1)
			this.firstTrialCounter++;
	}
	
	@Override
	public void packetEmitted(Message m) {
		// TODO Auto-generated method stub	
	}
	
	
	@Override
	public void notifyEnd(double clock, int status) {
		// TODO Auto-generated method stub
	}	
	
	@Override
	public void packetReceived(Message m, int origin, int dest, double timeEmitted, double timeReceived) {
		if (m.numTrans == 1)
			this.firstTimeSuccessCounter++;
		
	}		
	
	public void addInfo(DataPoint basic, DataPoint globals, Execution e, double simTime) {
		long totalReceived = lwSimExp.totalReceived;
		long totalRetran = lwSimExp.totalRetran;
		
		double allTransTime = lwSimExp.totalTransTime;
		double allReTransTime = lwSimExp.totalReTransTime;
		
		double txPerDeliveredPacket = (double)totalTransmitTrials/totalReceived;
		
		globals.addResultProperty("Total Transmission Trials", totalTransmitTrials);
		globals.addResultProperty("TX Trials Per Delivered Packet", txPerDeliveredPacket);
		globals.addResultProperty("ReTrials Per Delivered Packet", (double)totalRetran/totalReceived);
		globals.addResultProperty("Ratio of ReTX / TX", (double)totalRetran/totalTransmitTrials);
		globals.addResultProperty("Ratio of ReTX / TX (Energy)", allReTransTime/allTransTime);
		globals.addResultProperty("Transmission Time Per Delivered Packet", allTransTime/totalReceived);
		globals.addResultProperty("Retransmission Time Per Delivered Packet", allReTransTime/totalReceived);
		globals.addResultProperty("Blocking Probability", totalRetran/(double)totalTransmitTrials);
		globals.addResultProperty("Blocking Probabiltiy for Retran", 1 - (totalReceived - this.firstTimeSuccessCounter)/(double)totalRetran);
		globals.addResultProperty("First-Time Successful Rate", this.firstTimeSuccessCounter/(double)(this.firstTrialCounter));
		globals.addResultProperty("packet quench rate", this.totalQuenched/(double)(this.lwSimExp.totalEmitted));
		
		double alpha = (double)allTransTime/lwSimExp.getNumberOfClients()/(double)(1e5);
		double eta = totalReceived * lwSimExp.getTrafficGenerator().getAveragePacketSize()/lwSimExp.getNumberOfClients()/(double)(1e5)/lwSimExp.getReferenceBandwidth().getInGbitSeconds();
		double Edyn = 2.35 * alpha / eta + 1;
		double Esta10 = 0.76 / eta;
		double Esta1 = 7.6 / eta;
		double Esta5 = 1.52 / eta;
		double Esta20 = 0.38 / eta;
		
		globals.addResultProperty("Energy Per Delivered Bit (10% WPE)", Edyn + Esta10);
		globals.addResultProperty("Energy Per Delivered Bit (1% WPE)", Edyn + Esta1);
		globals.addResultProperty("Energy Per Delivered Bit (5% WPE)", Edyn + Esta5);
		globals.addResultProperty("Energy Per Delivered Bit (20% WPE)", Edyn + Esta20);
		
		globals.addResultProperty("Static Energy", Esta10);
		globals.addResultProperty("Dynamic Energy", Edyn);
		
		// added for slotted systems
		// slot time of packet transmission time (not too much bigger than RTT)
		/*double slotPerDeliveredPacket = 1e6 / (double)2200 * lwSimExp.getNumberOfClients() / (double) lwSimExp.totalReceived;
		double Estat = EstoEd_WPE1 * slotPerDeliveredPacket;
		double Etot = Estat + txPerDeliveredPacket;
		globals.addResultProperty("Energy Per Delivered Packet (1% WPE)", Etot);
		
		Estat = Estat/6;
		Etot = Estat + txPerDeliveredPacket;
		globals.addResultProperty("Energy Per Delivered Packet (6% WPE)", Etot);
		
		Estat = EstoEd_WPE10 * slotPerDeliveredPacket;
		Etot = Estat + txPerDeliveredPacket;
		globals.addResultProperty("Energy Per Delivered Packet (10% WPE)", Etot);*/
	}

	@Override
	public void packetQuenched(Message m, String where) {
		totalQuenched++;
		
	}

	@Override
	public void packetContented(Message m, String where,
			TrafficDestination swi, int type) {
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
