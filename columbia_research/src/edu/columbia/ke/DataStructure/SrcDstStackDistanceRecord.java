package edu.columbia.ke.DataStructure;

import java.util.ArrayList;
import java.util.HashMap;

public class SrcDstStackDistanceRecord {
	
	public ArrayList<Integer> srcRDList;
	public ArrayList<Integer> dstRDList;
	public ArrayList<Double> RDinTimeList;	//same for src and dst
	public ArrayList<Integer> sizeList;
	public int lastSeenIndexInSrc;
	public int lastSeenIndexInDst;
	public double lastSeenTime;
	
	public int maxConsideredKey = 8;	// included
	
	public HashMap<Integer, Integer> srcMap;
	public HashMap<Integer, Integer> dstMap;
	public HashMap<Integer, Integer> timeMap;
	public HashMap<Integer, Integer> sizeMap;
	
	public SrcDstStackDistanceRecord() {
		
		srcRDList = new ArrayList<>();
		dstRDList = new ArrayList<>();
		RDinTimeList = new ArrayList<>();
		sizeList = new ArrayList<>();
		
		srcMap = new HashMap<>();
		dstMap = new HashMap<>();
		timeMap = new HashMap<>();
		sizeMap = new HashMap<>();
		
		lastSeenIndexInSrc = -1;
		lastSeenIndexInDst = -1;
		lastSeenTime = -1d;
	}
	
	public void addUseInstance(int size, double time, int srcCount, int dstCount){
		if (lastSeenTime >= 0) {
			this.RDinTimeList.add(time - lastSeenTime);
			this.srcRDList.add(srcCount - lastSeenIndexInSrc);
			this.dstRDList.add(dstCount - lastSeenIndexInDst);
		}
		this.sizeList.add(size);
		this.lastSeenTime = time;
		this.lastSeenIndexInSrc = srcCount;
		this.lastSeenIndexInDst = dstCount;
	}
	
	public void addSrcUseInstance(int size, double time, int srcCount){
		if (lastSeenTime >= 0) {
			this.RDinTimeList.add(time - lastSeenTime);
			this.srcRDList.add(srcCount - lastSeenIndexInSrc);
		}
		this.sizeList.add(size);
		this.lastSeenTime = time;
		this.lastSeenIndexInSrc = srcCount;
	}
	
	public void addSrcUseInstance(int size, double time, int srcCount, int uniqueDistance){
		if (lastSeenTime >= 0 && uniqueDistance >=0) {
			this.RDinTimeList.add(time - lastSeenTime);
			this.srcRDList.add(uniqueDistance);
		}
		this.sizeList.add(size);
		this.lastSeenTime = time;
		this.lastSeenIndexInSrc = srcCount;
	}
	
	/*
	 * Methods below are used for hash tables
	 */
	
	public void addUseInstanceHashed(int size, double time, int srcCount, int dstCount){
		/*
		 * update dst map first
		 */
		if (lastSeenTime >= 0) {
			int key = distanceHashFunc(dstCount - lastSeenIndexInDst);

			// add into the map only if (key < maxConsideredKey)
			if (key <= maxConsideredKey) {
				int value = 0;
				if (dstMap.containsKey(key))
					value = dstMap.get(key);
				dstMap.put(key, value + 1);
			}
		}
		this.lastSeenIndexInDst = dstCount;
		
		/*
		 * update src and time map
		 */
		this.addSrcUseInstanceHashed(size, time, srcCount);
		
	}
	
	/*
	 * used for nonunique distance
	 */
	public void addSrcUseInstanceHashed(int size, double time, int srcCount){
		if (lastSeenTime >= 0) {
			int key = distanceHashFunc(srcCount - lastSeenIndexInSrc);
			updateSDMap(key);
		}
		this.lastSeenIndexInSrc = srcCount;
		
		/*
		 * record inter time
		 */
		addTimeUseInstanceHashed(time);

	}
	
	public void addTimeUseInstanceHashed(double time){
		if (lastSeenTime >= 0) {
			double interTime = time - lastSeenTime;
			int key = distanceHashFunc(interTime);
			
			updateTDMap(key);
		}
		this.lastSeenTime = time;
	}
	
	public void updateSDMap(int key) {
		// add into the map only if (key < maxConsideredKey)
		if (key <= maxConsideredKey) {
			int value = 0;
			if (srcMap.containsKey(key))
				value = srcMap.get(key);
			value++;
			srcMap.put(key, value);
		}
	}
	
	private void updateTDMap(int key){
		/*
		 * no boundary set
		 */
		int value = 0;
		if (timeMap.containsKey(key))
			value = timeMap.get(key);
		value++;
		timeMap.put(key, value);
	}
	
	public int distanceHashFunc(double n){
		if (n == 0)
			return -1;
		else
			return (int) (Math.log(n)/log2);
	}
	
	public int sizeHashFunc(int n){
		return (int) (Math.log(n)/log2);
	}
	
	public static double log2 = Math.log(2);
}
