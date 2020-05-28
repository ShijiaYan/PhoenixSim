package edu.columbia.ke.component.burst_assembly;

import java.util.ArrayList;

import edu.columbia.lrl.experiments.spinet.SpinetMessage;
import edu.columbia.lrl.general.Message;

public class BurstMessage extends SpinetMessage {

	public BurstMessage() {
		this.sizeInBits = 0;
	}
	
	protected ArrayList<Message> msgList = new ArrayList<>() ;

	public int addMessage(Message m){
		if (this.msgList.isEmpty()){
			this.index = m.index;
			this.origin = m.origin;
			this.dest = m.dest;
		} else{
			if (this.dest!=m.dest)
				throw new IllegalStateException("Message destinations are not the same!");
		}
			
		msgList.add(m);
		this.sizeInBits += m.sizeInBits;	
		
		/*
		 * update burst deadline (emission deadline)
		 */
		double dl = m.timeEmitted + m.getInitTimeToLive();
		if (this.msgList.size()==1 || dl < this.getDeadline())  
			this.setDeadline(dl);
		
		return msgList.size();
	}
	
	public boolean isEmpty(){
		return this.msgList.isEmpty();
	}
	
	public int size(){
		return this.msgList.size();
	}

}
