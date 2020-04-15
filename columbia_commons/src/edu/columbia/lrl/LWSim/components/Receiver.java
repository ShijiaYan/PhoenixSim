package edu.columbia.lrl.LWSim.components;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.TreeMap;

import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.LWSim.EventOrigin;
import edu.columbia.lrl.LWSim.TrafficDestination;
import edu.columbia.lrl.LWSim.application.Notifiable;
import edu.columbia.lrl.general.Evt;
import edu.columbia.lrl.general.Message;

public class Receiver implements TrafficDestination, EventOrigin {
	
	public static FileWriter fw;
	public static boolean logReceptions = false;
	
	static {
		if (logReceptions) {
			try {
				fw = new FileWriter("received.txt");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	

	protected LWSIMExperiment lwSimExperiment;
	protected int index;
	protected Notifiable notifiable;
	
	public TreeMap<Integer, Integer> lastReceived = new TreeMap<Integer, Integer>();
	public TreeMap<Integer, Integer> lastPartial = new TreeMap<Integer, Integer>();
	
	public Receiver(int index) {
		this.index = index;
	}
	
	// may be move this into a dedicated class that makes copies
	public Receiver getReceiverCopy(int index) {
		try {
			return this.getClass().getConstructor(Integer.TYPE).newInstance(index);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public int getIndex() {
		return index;
	}
	
	@Override
	public InitFeedback initComponent(LWSIMExperiment lwSimExperiment) {
		this.lwSimExperiment = lwSimExperiment;
		return null;
	}
	
	protected int getLatest(int origin) {
		Integer in = lastReceived.get(origin);
		if (in != null) return in;
		return -1;
	}
	
	protected int getLatestPartialIndex(int msgId) {
		Integer in = lastPartial.get(msgId);
		if (in != null) return in;
		return -1;		
	}
	
	protected void setLastPartial(int msgId, int partialIndex) {
		lastPartial.put(msgId, partialIndex);
	}
	
	protected void setLatest(int origin, int value) {
		lastReceived.put(origin, value);
	}
	
	@Override
	public void processEvent(Evt e) {
		Message m = e.getMessage();	
		//System.out.println("Received: \t" + m.origin + "\t -> " + m.dest + "\t" + m.timeEmitted + "\t" + e.getTimeNS());
		/// Event is 0 if received from somebody else, 1 if received from itself
		if (e.getType() == 0) {
		//	if (m.index == 6  && m.dest == 3) 
			//System.out.println("Receiver: " + index + " Pkt " + m.index + " received at " + e.getTimeNS()+ " destination: "+ m.dest);
			/*if (m.index <= getLatest(m.origin))
				throw new IllegalStateException("Message received twice or out-of-order");*/
			if (m.dest != index)
				throw new IllegalStateException("Wrong index (" + m.dest + ") at receiver (" + index + ")");
			
			lwSimExperiment.packetReceived(m, m.origin, m.dest, m.timeEmitted, e.getTimeNS());
			setLatest(m.origin, m.index);
			log(e);
			if (m.partialData == true && m.lastPartial == false) return;
			if (notifiable != null) {
				Evt newEv = new Evt(m.lastDuration + e.getTimeNS(), this, this, /* this is type */1, e);
				lwSimExperiment.manager.queueEvent(newEv);
			}
		} else {
			notifiable.objectReceived(m, m.carriedData, m.origin, e.getTimeNS());

		}
		
	}
	
	protected void log(Evt e) {
		if (!logReceptions) return;
		try {
			fw.append(e.getTimeNS() + "\r\n");
		}
		catch (Exception exx) {}		
	}
	

	public void setObjectToNotify(Notifiable n) {
		this.notifiable = n;
	}
	
	@Override
	public String toShortString() {
		return "receiver";
	}
	
	@Override
	public void notifyEnd(double ref, double status) {	}	
}
