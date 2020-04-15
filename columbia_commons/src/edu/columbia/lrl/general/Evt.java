package edu.columbia.lrl.general;

import edu.columbia.lrl.LWSim.EventOrigin;

public class Evt implements Comparable {
	
	private EventOrigin orig;
	private int type;
	private String sType;
	private double deadlineNS;
	private EventTarget target;
	
	private Message carriedObject;
	
/*	public Evt(Time deadline, EventDestination dest) {
		this(deadline.getNanoseconds(), dest, 0);
	}
	
	public Evt(Time deadline, EventDestination dest, int type) {
		this(deadline.getNanoseconds(), dest, type);
	}*/
	
	public Evt(double deadlineNS, EventTarget dest) {
		this(deadlineNS, null, dest, 0, (Message)null);
	}
	
	public Evt(double deadlineNS, EventOrigin orig, EventTarget dest, Evt source) {
		this(deadlineNS, orig, dest, 0, source);
	}
	
	public Evt(double deadlineNS, EventOrigin orig,  EventTarget dest) {
		this(deadlineNS, orig, dest, 0);
	}
	
	public Evt(double deadlineNS, EventOrigin orig,  EventTarget dest, int type) {
		this(deadlineNS, orig, dest, type, (Message)null);
	}	
	
	public Evt(double deadlineNS, EventOrigin orig,  EventTarget dest, int type, Evt source) {
		this(deadlineNS, orig, dest, type, source != null ? source.getMessage() : null);		
	}
	
	public Evt(double deadlineNS, EventOrigin orig,  EventTarget dest, int type, Message carriedMessage) {
		if (dest == null) {
			throw new IllegalStateException();
		}		
		this.target = dest;
		this.deadlineNS = deadlineNS;
		this.orig = orig;
		this.type = type;
		this.carriedObject = carriedMessage;
	}
	
	public Evt(double deadlineNS, EventOrigin orig,  EventTarget dest, String sType) {
		this(deadlineNS, orig, dest, sType, (Message)null);
	}	
	
	public Evt(double deadlineNS, EventOrigin orig,  EventTarget dest, String sType, Evt source) {
		this(deadlineNS, orig, dest, sType, source != null ? source.getMessage() : null);		
	}
	
	public Evt(double deadlineNS, EventOrigin orig,  EventTarget dest, String sType, Message carriedMessage) {
		if (dest == null) {
			throw new IllegalStateException();
		}		
		this.target = dest;
		this.deadlineNS = deadlineNS;
		this.orig = orig;
		this.sType = sType;
		this.type = Integer.MIN_VALUE;
		this.carriedObject = carriedMessage;
	}
	
	public int compareTo(Object o) {
		Evt alt = (Evt)o;
		int signum = (int)Math.signum(this.deadlineNS - alt.deadlineNS);
		if (signum == 0) {
			
			if (carriedObject != null && alt.carriedObject != null) {
				return carriedObject.compareTo(alt.carriedObject);
			} else {
				return 0;
			}
		} else return signum;
	}
	
	public Message getMessage() {
		return carriedObject;
	}
	
	public void setMessage(Message o) {
		carriedObject = o;
	}	
	
	public double getTimeNS() {
		return deadlineNS;
	}
	
	public int getType() {
		return type;
	}
	
	public String getsType() {
		return sType;
	}
	
	public EventTarget getTarget() {
		return target;
	}
	
	public EventOrigin getOrigin() {
		return orig;
	}
	
	public void execute() {
		target.processEvent(this);
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("EV: ");
		sb.append(deadlineNS + "-->");
		sb.append(target.toShortString());
		sb.append("(");
		sb.append(type + ")");
		return sb.toString();
	}
}
