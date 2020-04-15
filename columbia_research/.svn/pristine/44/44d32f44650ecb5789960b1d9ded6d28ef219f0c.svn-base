package edu.columbia.ke.DataStructure;

import java.util.ArrayList;
import java.util.HashMap;

public class CircuitReuseInfo {
	
	public HashMap<Integer, Integer> srcMap;
	public HashMap<Integer, Integer> dstMap;
	public HashMap<Integer, Integer> timeMap;
	public HashMap<Integer, Integer> sizeMap;
	public HashMap<Integer, Integer> tailList;
	public int lastSeenIndexInSrc;
	public int lastSeenIndexInSrc2;
	public int lastSeenIndexInDst;
	public double lastSeenTime;
	private int srcReuseScore;
	protected int maxConsideredKey = 5;	// included
	
	private int largestSrcMapValue;
	protected int keyofLargestSrcMapValue;
	private int largestTimeMapValue;
	private int keyofLargestTimeMapValue;
	public ArrayList<Integer> srcURDList;
	
	private boolean prefetched = false;
	private int prefetchedTime = -1;
	
	public int getSrcReuseScore() {
		return srcReuseScore;
	}

	public CircuitReuseInfo(int maxConsideredKey) {
		srcMap = new HashMap<Integer, Integer>();
		dstMap = new HashMap<Integer, Integer>();
		timeMap = new HashMap<Integer, Integer>();
		sizeMap = new HashMap<Integer, Integer>();
		tailList = new HashMap<Integer, Integer>();
		
		srcURDList = new ArrayList<Integer>();
		
		lastSeenIndexInSrc = -1;
		lastSeenIndexInDst = -1;
		lastSeenTime = -1d;
		if (maxConsideredKey > 0)
			this.maxConsideredKey = maxConsideredKey;
	}
	
	public void addUseInstance(int size, double time, int srcCount, int dstCount){
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
		this.addSrcUseInstance(size, time, srcCount);
	}
	
	public boolean isSDPredictionCorrect(int key){
		if (key == keyofLargestSrcMapValue)
			return true;
		else 
			return false;
	}
	
	public boolean isTDPredictionCorrect(int key){
		if (key == keyofLargestTimeMapValue)
			return true;
		else 
			return false;
	}
	
	public void updateSDMap(int key) {
		// add into the map only if (key < maxConsideredKey)
		if (key <= maxConsideredKey) {
			int value = 0;
			if (srcMap.containsKey(key))
				value = srcMap.get(key);
			value++;
			srcMap.put(key, value);
			if (value > this.largestSrcMapValue) {
				this.largestSrcMapValue = value;
				keyofLargestSrcMapValue = key;
			}
			updateSrcReuseScore(key);
		}
	}
	
	public void updateSizeMap(int size){
		int key = sizeHashFunc(size);
		int value = 0;
		if(sizeMap.containsKey(key))
			value = sizeMap.get(key);
		sizeMap.put(key, value + 1);
	}

	/*
	 * used for nonunique distance
	 */
	public boolean addSrcUseInstance(int size, double time, int srcCount){
		boolean correct = true;
		if (lastSeenTime >= 0) {
			int key = distanceHashFunc(srcCount - lastSeenIndexInSrc);
			
			// check accuracy of prediction
			correct = isSDPredictionCorrect(key);
			
			updateSDMap(key);
		}
		this.lastSeenIndexInSrc2 = this.lastSeenIndexInSrc;
		this.lastSeenIndexInSrc = srcCount;
		
		/*
		 * record inter time
		 */
		addTimeUseInstance(time);
		
		return correct;
	}
	
	/*
	 * used for unique distance
	 */
	public boolean addSrcUseInstance(int size, double time, int srcCount, int uniqueDistance){
		boolean correct = true;
		if (lastSeenTime >= 0 && uniqueDistance >=0 ) {
			int key = distanceHashFunc(uniqueDistance);
			
			// check accuracy of prediction
			correct = isSDPredictionCorrect(key);
			
			updateSDMap(key);
			
			// for matlab histogram
			this.srcURDList.add(uniqueDistance);
		}
		this.lastSeenIndexInSrc2 = this.lastSeenIndexInSrc;
		this.lastSeenIndexInSrc = srcCount;
		
		/*
		 * record inter time
		 */
		addTimeUseInstance(time);
		
		return correct;
	}

	public boolean addTimeUseInstance(double time){
		boolean correct = true;
		if (lastSeenTime >= 0) {
			double interTime = time - lastSeenTime;
			int key = distanceHashFunc(interTime);
			
			// check accuracy of prediction
			correct = isTDPredictionCorrect(key);
			
			updateTDMap(key);
		}
		this.lastSeenTime = time;
		return correct;
	}
	
	public void addTail1(int nextDst){
		int value = 0;
		if (tailList.containsKey(nextDst))
			value = tailList.get(nextDst);
		value++;
		tailList.put(nextDst, value);
	}
	
	public void addTail(int follower, int distance) {
		int value = 0;
		
		if (tailList.containsKey(follower)) {
			value = tailList.get(follower);
		}
		
		// assuming that within the distance of consideration
		// the tailing distance does not weight the incremental value
		value++;	
		tailList.put(follower, value);
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
		if (value > this.largestTimeMapValue) {
			this.largestTimeMapValue = value;
			keyofLargestTimeMapValue = key;
		}
	}
	
	private void updateSrcReuseScore(int key){
		if (key <= maxConsideredKey)
			this.srcReuseScore += (1 << (maxConsideredKey - key));
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
	
	public int predictedNextSrcUse_NoCredit(){
		return this.lastSeenIndexInSrc+(1 << this.keyofLargestSrcMapValue);
	}
	
	public int predictNextSrcUse_StackWithCredit(double threshold, int currentCount){
		int nextUse;
		if (this.largestSrcMapValue >= threshold)	//credible
			nextUse = this.lastSeenIndexInSrc+(1 << this.keyofLargestSrcMapValue);
		else	//not credible, hence cannot make a confident prediction
			//nextUse = this.lastSeenIndexInSrc+(1 << maxConsideredKey);
			nextUse = lastSeenIndexInSrc;
		
		int yetToWait = nextUse - currentCount;
		int timeElapsed = currentCount - lastSeenIndexInSrc;
		return yetToWait > timeElapsed? yetToWait : timeElapsed;
	}

	public int predictSrcReuseDistance(){
		return (int)(1 << this.keyofLargestSrcMapValue);
	}
	
	public double predictTimeReuseDistance(){
		return (int)(1 << this.largestTimeMapValue);
	}
	
	public void setPrefetch(int pfTime) {
		prefetched = true;
		prefetchedTime = pfTime;
	}
	
	public void resetPrefetch() {
		prefetched = false;
		prefetchedTime = -1;
	}
	
	public boolean isPrefetched() {
		return prefetched;
	}
	
	public int getPretetchedTime(){
		return prefetchedTime;
	}
}
