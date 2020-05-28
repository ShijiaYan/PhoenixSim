package edu.columbia.ke.DataStructure;

public class IntLog2 {
	private int distance;
	private int logDistance;
	public static double log2 = Math.log(2);

	public IntLog2(int d) {
		this.distance = d;
		this.logDistance = (int) (Math.log(distance)/log2);
	}

	@Override
	public boolean equals(Object obj) {
		return this.logDistance == Math.log((Integer)obj)/log2;
		//return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return logDistance;
		//return super.hashCode();
	}
	
	

}
