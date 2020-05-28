package edu.columbia.lrl.experiments.spinet;

import edu.columbia.lrl.general.Message;

public class SpinetMessage extends Message {
	
	private boolean nbOffsetFixed = false;
	private boolean ttimeFixed = false;
	private boolean offsetDFixed = false;

	public int spinetPriority;	

	public boolean dropped = false;
	
	private int numberOfHeaderOffset;
	private int fullNumberOfHeaderOffset;
	private double transmissionTimeNS;
	private double offsetDurationNS;

	public SpinetMessage() {
	}

	public SpinetMessage(int i, int origin, int dest, double timeEmitted, int size) {
		this( i,  origin,  dest,  timeEmitted,  size, -1);
	//	this.numberOfHeaderOffset = numberOfHeaderOffset;
	//	this.transmissionTimeNS = transmissionTimeNS;
	//	this.offsetDurationNS = offsetDurationNS;
	}
	
	public SpinetMessage(int i, int origin, int dest, double timeEmitted, int size, double deadline) {
		super(i, origin, dest, timeEmitted, size, deadline);
	}
	
	public Message getInstance(int i, int origin, int dest, double timeEmitted, int size) {
		return new SpinetMessage(i, origin, dest, timeEmitted, size);
	}
	
	// used by EnhancedTDM
	public SpinetMessage getCopy() {
		SpinetMessage copy = new SpinetMessage();
		copy.index = this.index;
		copy.origin = this.origin;
		copy.dest = this.dest;
		copy.timeEmitted = this.timeEmitted;
		copy.numTrans = this.numTrans;
		copy.sizeInBits = this.sizeInBits;
		copy.spinetPriority = this.spinetPriority;
		
		copy.object = this.object; // this object can be used by various networking schemes
		copy.carriedData = this.carriedData;
		return copy;
	}
	
	public void setNumberOfHeaderOffset(int nu) {
		this.numberOfHeaderOffset = nu;
		this.fullNumberOfHeaderOffset = nu;
		nbOffsetFixed = true;
	}

	public void setOffsetDurationNS(double ti) {
		this.offsetDurationNS = ti;
		offsetDFixed = true;
	}
	
	public double getOffsetDurationNS() {
		return offsetDurationNS;
	}

	public int getNumberOfHeaderOffset() {
		return numberOfHeaderOffset;
	}

	public void setTransmissionTime(double ti) {
		this.transmissionTimeNS = ti;
		ttimeFixed = true;
	}
	
	public double getTransmissionTimeNS() {
		if (ttimeFixed == false || nbOffsetFixed == false || offsetDFixed == false) 
			throw new IllegalStateException("Spinet message must be provided with some info");
		
		if (numberOfHeaderOffset < 0) 
			throw new IllegalStateException("Header offset cannot be negative");
		
		return transmissionTimeNS + numberOfHeaderOffset*offsetDurationNS;
	}
	
	public void decrementOffset() {
		numberOfHeaderOffset--;
		/*if (numberOfHeaderOffset < 0) 
			throw new IllegalStateException("Header offset cannot be negative");*/
	}

	public int getSwitchPassed() {
		return this.fullNumberOfHeaderOffset - this.numberOfHeaderOffset;
	}

	public int getSpinetPriority() {
		return spinetPriority;
	}

	public void setSpinetPriority(int spinetPriority) {
		this.spinetPriority = spinetPriority;
	}
}
