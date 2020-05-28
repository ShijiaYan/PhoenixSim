package edu.columbia.ke.circuit_oriented.in_limited_only.tail_recorded;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

import ch.epfl.general_libraries.utils.SimpleMap;
import edu.columbia.ke.DataStructure.MapSort;
import edu.columbia.ke.circuit_oriented.in_limited_only.InCircuitLimitedVOQ;

public abstract class AbstractTailRecordedVOQ extends InCircuitLimitedVOQ {
	
	protected Queue<Integer> precedentDests = new LinkedList<>();
	protected int tailLen = 2;
	protected boolean logTail = false;
	
	public AbstractTailRecordedVOQ(int tailLen) {
		super();
		this.tailLen = tailLen;
	}	

	public AbstractTailRecordedVOQ(int index, int nDest,
			double circuitSetupLatency, int maxNumCircuits,
			double maxVacantTime, int tailLen) {
		super(index, nDest, circuitSetupLatency, maxNumCircuits, maxVacantTime);
		writeName = "tail";
		this.tailLen = (maxNumCircuits+1)/2 < tailLen ? (maxNumCircuits+1)/2 : tailLen;
	}
	
	public Map<String, String> getAllParameters() {
		return SimpleMap.getMap("Tail length", tailLen+"");
	}	

	protected Set<Integer> getTail(int dest) {
		
		List list = MapSort.listSortByValue(csi.get(dest).tailList);
		Set<Integer> exemptSet = new TreeSet<>();
		int i = 0;
        for (Object o : list) {
            Map.Entry entry = (Map.Entry) o;
            exemptSet.add((Integer) entry.getKey());
            i++;
            if (i == tailLen) {
                break;
            }
        }
		
		return exemptSet;
	}

	/* (non-Javadoc)
	 * @see edu.columbia.ke.component.AbstractCircuitLimitedVOQ#recordReuse(int, double, int)
	 */
	@Override
	protected void recordCircuitUse(int dest, double time, int size) {
		recordTail(dest);
		
		// update recent dests after tail is recorded
		if(precedentDests.size() == tailLen) {
			precedentDests.poll();
		}
		precedentDests.add(dest);
		
		super.recordCircuitUse(dest, time, size);
	}
	
	
	protected void recordTail(int newDst) {
		int distance = precedentDests.size();
		for(Integer pre : precedentDests) {
			if (pre >= 0 && pre != newDst) {	// a dest will not add itself as tail
				csi.get(pre).addTail(newDst, distance--);
			}
		}
	}

	/* (non-Javadoc)
	 * @see edu.columbia.ke.component.AbstractCircuitLimitedVOQ#notifyEnd(double, double)
	 */
	@Override
	public void notifyEnd(double ref, double status) {
		
		if (index == trackingNode) {
			
			// dump per-dest statistics
			for (int i: this.destList){
			
			// dump tails learned
			if (logTail) {
				String tail = "Dst " + i;
				tail += '\t' + csi.get(i).tailList.toString();
				logMyRank(tail);				
			}
			}
		}
		super.notifyEnd(ref, status);
	}

	
}
