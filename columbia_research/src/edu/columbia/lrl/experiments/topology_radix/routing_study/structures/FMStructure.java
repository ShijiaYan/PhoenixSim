package edu.columbia.lrl.experiments.topology_radix.routing_study.structures;

import cern.colt.Arrays;

public class FMStructure extends AbstractAxisStructure {
	
	static {
		AbstractAxisStructure.registerSubClass('F', emptyA1, FMStructure.class);
	}
	
	public FMStructure(short size) {
		super(size);
	}

	@Override
	public int[][] getIndexesOfLinksUsed(int from, int to) {
		if (from == to) return emptyA;
		if (from < to) {
			return new int[][]{{getIndexOfLinkUsedLocal(from, to)},{1}};
		} else {
			return new int[][]{{getIndexOfLinkUsedLocal(from, to)},{-1}};
		}
	}
	
	private int getIndexOfLinkUsedLocal(int from, int to) {
		if (to < from) {
			int t = to;
			to = from;
			from = t;
		}
		int linestart = from*(size-1) - from*(from-1)/2;
		int lineel = linestart + to - from - 1;
		return lineel;
	}

	@Override
	public int getNumberOfLinksInStructure() {
		return size*(size-1)/2;
	}

	@Override
	public int[] getIndexesOfLinksConnectedTo(int nodeIndex) {
		int[] resp = new int[size-1];
		int index = 0;
		for (int i = 0 ; i < size ; i++) {
			if (i == nodeIndex) continue;
			resp[index] = getIndexOfLinkUsedLocal(nodeIndex, i);
			index++;
		}
		return resp;
	}
	
	public static void main(String[] args) {
		FMStructure rs = new FMStructure((short)6);
		for (int i = 0 ; i < rs.size ; i++) {
			System.out.println(Arrays.toString(rs.getIndexesOfLinksConnectedTo(i)));
			for (int j = 0 ; j < rs.size ; j++) {
				System.out.println("from " + i  + " to " + j + " " + Arrays.toString(rs.getIndexesOfLinksUsed(i, j)));
			}
		}
		for (int i = 0 ; i < rs.getNumberOfLinksInStructure() ; i++) {
			int[] gg = rs.getExtremitiesOfLink(i);
			System.out.println("Ext of " + i + ":" + gg[0] + "-" + gg[1]);
		}
	}

	@Override
	public int getRadix() {
		return size-1;
	}
	
	@Override
	public int getDiameter() {
		return 1;
	}
	
	@Override
	public AbstractAxisStructure getInstance(short s) {
		return new FMStructure(s);
	}	

	@Override
	public int[] getExtremitiesOfLink(int k) {
		int from = 0;
		int prevMarker = -1;
		int marker = size -2;
		while (marker < k) {
			from++;
			prevMarker = marker;
			marker += size - 1 - from;
		}
		int to = from + k - prevMarker;
		if (from < to){
			return new int[]{from, to};
		} else {
			return new int[]{to, from};
		}
	}	
}
