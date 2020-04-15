package edu.columbia.lrl.experiments.topology_radix.locality;

import ch.epfl.general_libraries.math.MoreMaths;
import edu.columbia.lrl.experiments.topology_radix.routing_study.GlobalStructure;

public class TransposeMatrixTrafficMatrix extends AbstractTrafficMatrix {

	public TransposeMatrixTrafficMatrix(double normLoad) {
		super(normLoad);
		// TODO Auto-generated constructor stub
	}
	
	public TransposeMatrixTrafficMatrix() {	}	

	private int bits;
	
	@Override
	public void init(int clients, GlobalStructure gs) {
		super.init(clients, null);
		bits = (int)Math.ceil(MoreMaths.log2(clients));
	}
	
	private int transpose(int n) {
		int[] bitA = MoreMaths.getBits(n, bits);
		
		int[] res = new int[bitA.length];
		int half = bits/2;
		int halfAdd = MoreMaths.ceilDiv(bits,2);
		for (int i = 0 ; i < half ; i++) {
			res[halfAdd+i] = bitA[i];
		}
		for (int i = half ; i < bits ; i++) {
			res[i-half] = bitA[i];
		}
		return MoreMaths.bitsToInt(res);	
	}

	@Override
	public double getTraffic(int src, int dest) {
		if (dest == transpose(src) || src==transpose(dest)) {
			return normLoad/2;
		} else {
			return 0;
		}
	}

	@Override
	public double getTrafficFrom(int src) {
		// TODO Auto-generated method stub
		return 0;
	}

}
