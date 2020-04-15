package edu.columbia.lrl.LWSim.traffic.traces;

public class TraceEvent {

	private int from;
	private int to;
	private int sizeInByte;
	private double timeNS;
	
	public TraceEvent(int from, int to, int sizeInByte, double timeNS) {
		this.from = from;
		this.to = to;
		this.sizeInByte = sizeInByte;
		this.timeNS = timeNS;
	}

	public double getSendTime() {
		return timeNS;
	}

	public double check(double lastReadSendTime, int nodes) {
		if (timeNS < lastReadSendTime) 
			throw new IllegalStateException("sendTime = " + timeNS + ", " + "lastReadSendTime = " + lastReadSendTime);
		if (timeNS < 0)
			throw new IllegalStateException("sendTime is negative: = " + timeNS);
		if (timeNS > Double.MAX_VALUE)
			throw new IllegalStateException("sendTime larger than Double.Max: = " + timeNS);
		if (from < 0)
			throw new IllegalStateException("trace file with negative from");
		if (to < 0)
			throw new IllegalStateException("trace file with negative to");
		if (from >= nodes)
			throw new IllegalStateException("trace file with too large from (" + from + ")");
		if (to >= nodes)
			throw new IllegalStateException("trace file with too large to (" + to + ")");
		if (sizeInByte < 0)
			throw new IllegalStateException("trace file with negative size");
		return timeNS;
	}

	public int getDest() {
		return to;
	}

	public int getSource() {
		return from;
	}

	public int getByte() {
		return sizeInByte;
	}

	public int getBits() {
		return sizeInByte * 8;
	}
}
