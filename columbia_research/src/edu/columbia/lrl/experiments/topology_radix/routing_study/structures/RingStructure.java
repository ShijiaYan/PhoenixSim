package edu.columbia.lrl.experiments.topology_radix.routing_study.structures;

import java.util.Arrays;

import ch.epfl.general_libraries.utils.MoreArrays;

public class RingStructure extends AbstractAxisStructure {
	
	static {
		AbstractAxisStructure.registerSubClass('R', emptyA1, RingStructure.class);
	}
	
	public RingStructure(short size) {
		super(size);
	}

	@Override
	public int[][] getIndexesOfLinksUsed(int from, int to) {
		if (from == to) return emptyA;
		int diff = to - from;
		int[] array;
		if (size % 2 == 0) {
			int hs = size/2;
			if ((diff > 0 && diff <= hs) || diff <= -hs) {
				// clockwise
				if (to < from) 
					to += size;
				array = MoreArrays.range(from, to-1, size);
			} else {
				if (from < to)
					from += size;
				array = MoreArrays.range(to, from-1, size);
			}			
		} else {
			int hs = size/2;
			if ((diff > 0 && diff <= hs) || diff < -hs) {
				// clockwise
				if (to < from) 
					to += size;
				array = MoreArrays.range(from, to-1, size);
			} else {
				if (from < to)
					from += size;
				array = MoreArrays.range(to, from-1, size);
			}			
		}
		int[] dir = new int[array.length];
		if (from < to) {
			Arrays.fill(dir, 1);
		} else {
			Arrays.fill(dir, -1);
		}
		return new int[][]{array, dir};
	}
	
	public static void main(String[] args) {
		RingStructure rs = new RingStructure((short)8);
		for (int i = 0 ; i < rs.size ; i++) {
			for (int j = 0 ; j < rs.size ; j++) {
				System.out.println("from " + i  + " to " + j + " " + Arrays.toString(rs.getIndexesOfLinksUsed(i, j)));
			}
		}

	}

	@Override
	public int getNumberOfLinksInStructure() {
		return size;
	}

	@Override
	public int[] getIndexesOfLinksConnectedTo(int nodeIndex) {
		if (nodeIndex != 0) {
			return new int[]{nodeIndex-1,nodeIndex};
		} else {
			return new int[]{0,size-1};
		}
		
	}

	public static int getImposedSize() {
		return -1;
	}

	@Override
	public int getRadix() {
		return 2;
	}
	
	@Override
	public int getDiameter() {
		return size/2;
	}	

	@Override
	public int[] getExtremitiesOfLink(int k) {
		if (k >= size) throw new IllegalStateException("too large");
		if (k == size-1) { 
			return new int[]{0, size-1};
		} else {
			return new int[]{k, k+1};
		}
	}

	@Override
	public AbstractAxisStructure getInstance(short s) {
		return new RingStructure(s);
	}

}
