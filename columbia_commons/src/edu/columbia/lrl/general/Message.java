package edu.columbia.lrl.general;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import edu.columbia.lrl.LWSim.LWSimComponent;

public class Message implements Comparable<Message> { 
	public int index;
	public int origin;
	public int dest;
	public double timeEmitted;
	public int numTrans;
	public int sizeInBits;
	public int applicationPriority;
	public boolean partialData;
	public boolean lastPartial;
	public int partialIndex;
	public int totalSize;

	public Object object; // this object can be used by various networking schemes
	public Object carriedData;
	public double lastDuration;
	
	private double deadline;
	private int initTimeToLive = -1;
	public boolean deadlineSet = false;
	// this field is never used in the commons package so should not appear here. For deadline
	// application, specific message type should be created
	//private boolean dropAfterDeadlineEnabled = false;
	private double leastTimeToWait;
	
	protected ArrayList<LWSimComponent> occupiedResources;
	private boolean active = false;



	public Message() {
		this.deadline = -1;		// "-1" means no deadline requirement for this message
		this.occupiedResources = new ArrayList<>();
	}
	
	public Message(int i, int origin, int dest, double timeEmitted, int size) {
		this( i,  origin,  dest,  timeEmitted,  size, -1);
	}
	
	public Message(int i, int origin, int dest, double timeEmitted, int size, double deadline) {
		this.origin = origin;
		this.dest = dest;
		index = i;
		this.timeEmitted = timeEmitted;
		this.numTrans = 0;
		this.sizeInBits = size;
		this.deadline = deadline;
		this.occupiedResources = new ArrayList<>();
	}
	
	public Message getInstance(int i, int origin, int dest, double timeEmitted, int size) {
		try {
			return this.getClass().getConstructor(Integer.TYPE, Integer.TYPE, Integer.TYPE, Double.TYPE, Integer.TYPE).newInstance(i, origin, dest, timeEmitted, size);
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
	//	return new Message(i, origin, dest, timeEmitted, size);
	}
	
	public void incNumTrans(){
		this.numTrans++;
	}
	
	public double getDeadline() {
		return deadline;
	}

	public void setDeadline(double deadline) {
		//old 
		//if (!this.deadlineSet && this.deadlineEnabled) {
		//new
	
		this.deadline = deadline;
		this.deadlineSet = true;
	
	}
	
	public String toString() {
		return "m:"+index + "(" + origin + "-" + dest + ")";
	}

	public int getInitTimeToLive() {
		return initTimeToLive;
	}

	public void setInitTimeToLive(int initTimeToLive) {
		this.initTimeToLive = initTimeToLive;
	}
	
/*	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}*/
	
	public double getLeastTimeToWait() {
		return leastTimeToWait;
	}

	public void setLeastTimeToWait(double leastTimeToWait) {
		this.leastTimeToWait = leastTimeToWait;
	}

	@Override
	public int compareTo(Message arg0) {
		if (index != arg0.index) {
			return (int)Math.signum(this.index - arg0.index);
		} else {
			if (this.partialIndex != arg0.partialIndex) {
				return this.partialIndex - arg0.partialIndex;
			} else {
				return this.dest - arg0.dest;
			}
		}
	}
	
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
	public void enqueOccupyResource(LWSimComponent c){
		this.occupiedResources.add(c);
	}
	
	public void clearOccupyResource(){
		this.occupiedResources.clear();
	}

	public ArrayList<LWSimComponent> getOccupiedResources() {
		return occupiedResources;
	}
}
