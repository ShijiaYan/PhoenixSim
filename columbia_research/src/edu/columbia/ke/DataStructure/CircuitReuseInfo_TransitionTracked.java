package edu.columbia.ke.DataStructure;

public class CircuitReuseInfo_TransitionTracked extends CircuitReuseInfo {
	
	/*
	 * transition matrix
	 */
	private int[][] p;
	private int dim;

	public CircuitReuseInfo_TransitionTracked(int maxConsideredKey) {
		super(maxConsideredKey);
		dim = maxConsideredKey + 1;
		p = new int [dim] [];
		for (int i = 0; i < dim; i++){
			p[i] = new int [dim];
		}
	}
	
	private int lastKey = -2;
	private int predictedKey = -2;

	@Override
	public boolean addSrcUseInstance(int size, double time, int srcCount) {
		boolean correct = true;
		if (lastSeenTime >= 0) {
			int key = distanceHashFunc(srcCount - lastSeenIndexInSrc);
			
			// check accuracy of prediction; do this before update
			correct = isSDPredictionCorrect(key);
			
			updateSDMap(key);
			
			// special for TM case
			if (lastKey>=0)
				updateTrnsMatrix(lastKey, key);
			
			lastKey = key;
			
			predictedKey = predictNextDistanceFromTM(key);
		}
		this.lastSeenIndexInSrc2 = this.lastSeenIndexInSrc;
		this.lastSeenIndexInSrc = srcCount;
		
		/*
		 * record inter time
		 */
		addTimeUseInstance(time);
		
		return correct;
	}

	private void updateTrnsMatrix(int lastKey2, int key) {
		if (key <= maxConsideredKey && lastKey2 >=0 && lastKey2 <= maxConsideredKey) 
			p[lastKey2][key]+=1;
	}
	
	private int predictNextDistanceFromTM(int key) {
		if (key < 0 || key > maxConsideredKey)
			return keyofLargestSrcMapValue;
		
		int maxV = -1;
		int f = -1;
		int maxKey = -1;
		for (int i = 0; i < dim; i++) {
			if (p[key][i] > maxV) {
				maxV = p[key][i];
				maxKey = i;
				if (srcMap.containsKey(maxKey))
					f = srcMap.get(maxKey);
			} else if (p[key][i] == maxV) {
				int f2 = -1;
				if (srcMap.containsKey(i))
					f2 = srcMap.get(i);
				if (f2 > f) {
					f = f2;
					maxKey = i;
				}
			}
		}
		return maxKey;
	}

	@Override
	public boolean isSDPredictionCorrect(int key) {
		if (lastKey == -2){
			return true;
		}

        return predictedKey == key;
	}

	@Override
	public int predictNextSrcUse_StackWithCredit(double threshold,
			int currentCount) {
		if (predictedKey==-2)
			return super.predictNextSrcUse_StackWithCredit(threshold, currentCount);
		
		int nextUse = this.lastSeenIndexInSrc+(1 << this.predictedKey);
		
		int yetToWait = nextUse - currentCount;
		int timeElapsed = currentCount - lastSeenIndexInSrc;
		return yetToWait > timeElapsed? yetToWait : timeElapsed;
		
	}
	
	

}
