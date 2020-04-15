package edu.columbia.lrl.experiments.spinet.variants;

import ch.epfl.general_libraries.utils.Pair;

import edu.columbia.lrl.general.Evt;
import edu.columbia.lrl.general.Message;

public class ACK_or_NACK_Sender {
	
	@SuppressWarnings("unchecked")
	public static double getElapsedTimeNS(Evt e) {
		Pair<Double, ACK_or_NACK_Receiver> pair = (Pair<Double, ACK_or_NACK_Receiver>)e.getMessage().object;		
		return e.getTimeNS() - pair.getFirst();
	}
	
	@SuppressWarnings("unchecked")
	public static double getElapsedTimeNS(Message m, double time) {
		Pair<Double, ACK_or_NACK_Receiver> pair = (Pair<Double, ACK_or_NACK_Receiver>)m.object;		
		return time - pair.getFirst();
	}	
	
	
	
	public static ACK_or_NACK_Receiver getFeedbackDestination(Evt e) {
		@SuppressWarnings("unchecked")
		Pair<Double, ACK_or_NACK_Receiver> pair = (Pair<Double, ACK_or_NACK_Receiver>)e.getMessage().object;		
		return pair.getSecond();		
	}
	
	public static ACK_or_NACK_Receiver getFeedbackDestination(Message m) {
		@SuppressWarnings("unchecked")
		Pair<Double, ACK_or_NACK_Receiver> pair = (Pair<Double, ACK_or_NACK_Receiver>)m.object;		
		return pair.getSecond();		
	}	
	
	
}
