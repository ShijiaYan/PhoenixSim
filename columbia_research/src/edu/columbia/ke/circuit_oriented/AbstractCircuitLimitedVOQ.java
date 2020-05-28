package edu.columbia.ke.circuit_oriented;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import ch.epfl.general_libraries.graphics.ColorMap;
import ch.epfl.general_libraries.graphics.timeline.TimeLine;
import ch.epfl.general_libraries.utils.SimpleMap;
import edu.columbia.ke.DataStructure.CircuitReuseInfo;
import edu.columbia.ke.DataStructure.CircuitReuseInfo_TransitionTracked;
import edu.columbia.ke.generic.dataStucture.AvlTreeRm;
import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.LWSim.LWSimComponent;
import edu.columbia.lrl.LWSim.components.Buffer;
import edu.columbia.lrl.LWSim.components.Receiver;
import edu.columbia.lrl.general.Evt;
import edu.columbia.lrl.general.Message;

public abstract class AbstractCircuitLimitedVOQ extends AbstractVOQ {
	
	/*
	 * require: VOQ index being parent node index
	 */
	private static class CircuitInfo {
		int destIndex;
		int circuitId;
		double cacheStartTime;
		double vacantStartTime;
		boolean isVacant;
		boolean isOn;
	}
	
	//protected Map<Integer, CircuitInfo> circuitInfos;
	CircuitInfo[] circuitInfos;
	
	static int bufIndex = 0;

	protected int maxNumCircuits;
	protected double circuitSetupLatency;
	protected double maxVacantTime = -1;	
	
	protected TreeMap<Integer, CircuitReuseInfo> csi;
	
	// log controls
	protected int trackingNode = -1;
	private boolean microLog = false;
	
	protected int lastDest = -1;
	protected int circuitUseCount = 0;

	TimeLine[] srcTimelines;
	private boolean sdTimeline;
	private boolean srcTimelineFlag;	
	
	protected boolean coalescMode = false;
	
	String CIRCUIT_DEADLINE = "CIRCUIT_DEADLINE";
	private boolean avlTreeEnabled = false;
	
	private boolean dontSendTraffic2Myself = true;
	
	// avl tree for tracking unique reuse distance
	private AvlTreeRm avlTree = new AvlTreeRm();
	
	public abstract AbstractCircuitLimitedVOQ getCopy(int parentID, int nDestPerVoq, int circuitPerVoq);
	public abstract Receiver getAssociatedReceiver(int nClient, int circuitPerNode);

	/**
	 * This constructor is used to create the template (cockpit)
	 * @param circuitSetupLatency
	 * @param maxNumCircuits
	 * @param maxVacantTime
	 */
	protected AbstractCircuitLimitedVOQ() {
		super();
	}
	
	public AbstractCircuitLimitedVOQ(int index, int nDest,
			double circuitSetupLatency, int maxNumCircuits, double maxVacantTime) {
		super(index, nDest);
		this.maxNumCircuits = maxNumCircuits;
		
		this.circuitSetupLatency = circuitSetupLatency;
		
		circuitInfos = new CircuitInfo[maxNumCircuits];
		for (int i = 0 ; i < maxNumCircuits ; i++) {
			circuitInfos[i] = new CircuitInfo();
			circuitInfos[i].circuitId = i;
			circuitInfos[i].destIndex = -1;
		}
		
	//	inCacheStartTime = new TreeMap<Integer, Double>();
	//	vacantStartTime = new TreeMap<Integer, Double>();
		this.maxVacantTime = maxVacantTime;
	}
	
	@Override
	public InitFeedback initComponent(LWSIMExperiment lwSimExperiment) {
		InitFeedback fb = super.initComponent(lwSimExperiment);	
		if (srcTimelineFlag) {
			this.srcTimelines = new TimeLine[maxNumCircuits];
			for (int i = 0 ; i < maxNumCircuits ; i++) {
				srcTimelines[i] = new TimeLine(index, "VOQ", "From " + index + " #"+i);
			}
		}
		return fb;
	}	
	
	public Map<String, String> getAllParameters() {
		return SimpleMap.getMap();
	}
	
	public void setMaxVacantTime(double maxVacantTime) {
		this.maxVacantTime = maxVacantTime;
	}
	
	public void setCircuitSetupLatency(double circuitSetupLatency) {
		this.circuitSetupLatency = circuitSetupLatency;
	}	
	
	@Override
	public void addDestinations(ArrayList<Integer> destList,
			ArrayList<LWSimComponent> dests) {
		this.csi = new TreeMap<>();
		this.destList.addAll(destList);
		
		for (int dstId: destList) {
			// create buffer
			bufs.put(dstId, new CircuitAvailLimitedBuffer(1000, 0, bufIndex++, 1, prior, this.index, dstId, this));
			dests.add(bufs.get(dstId));
			
			// create csi record
			csi.put(dstId, new CircuitReuseInfo_TransitionTracked(6));
		}
	}
	
	@Override
	public void addDestination(int dstId, ArrayList<LWSimComponent> dests) {
		this.destList.add(dstId);
		
		// create buffer
		bufs.put(dstId, new CircuitAvailLimitedBuffer(10000, 0, bufIndex++, 1, prior, this.index, dstId, this));
		dests.add(bufs.get(dstId));

		// create csi record
		csi.put(dstId, new CircuitReuseInfo_TransitionTracked(6));
		
	}
	
	protected Set<Integer> findReplaceCandidates(int dest) {
		Set<Integer> replaceCandidates = new TreeSet<>();
	//	replaceCandidates.addAll(vacantCircuitSet);
        for (CircuitInfo circuitInfo : circuitInfos) {
            if (circuitInfo.isVacant) {
                replaceCandidates.add(circuitInfo.destIndex);
            }
        }
		return replaceCandidates;
	}
	
	protected Set<Integer> getInCacheDest() {
		Set<Integer> dests = new TreeSet<>();
        for (CircuitInfo circuitInfo : circuitInfos) {
            if (circuitInfo.isVacant) {
                dests.add(circuitInfo.destIndex);
            }
        }
		return dests;
	}
	
	abstract protected int findReplacement(int dest, Set<Integer> replaceCandidates);
	
	protected void replaceCircuit(int oldCircuit, double time){
		this.removeFromCacheSet(oldCircuit, time);
	}
	
	public boolean isCircuitHit(int dest){
        for (CircuitInfo circuitInfo : circuitInfos) {
            if (circuitInfo.isOn && circuitInfo.destIndex == dest) {
                return true;
            }
        }
		return false;
	}
	
	public boolean isCircuitVacant(int dest){
        for (CircuitInfo circuitInfo : circuitInfos) {
            if (circuitInfo.isVacant && circuitInfo.destIndex == dest) return true;
        }
		return false;
	}
	
	public boolean isCacheFull(){
        for (CircuitInfo circuitInfo : circuitInfos) {
            if (!circuitInfo.isOn) return false;
        }
		return true;		
	}
	
	public boolean isCacheFullButWithVacant() {
        for (CircuitInfo circuitInfo : circuitInfos) {
            if (circuitInfo.isVacant) {
                return true;
            }
        }
		return false;		
	}
	
	public int getCacheSize() {
		int nb = 0;
        for (CircuitInfo circuitInfo : circuitInfos) {
            if (circuitInfo.isOn) nb++;
        }
		return nb;		
	}
	
	protected void handleCircuitHit(int msgDest, double now){
		if (!(coalescMode && lastDest == msgDest )) {
			
			// hit -- meaning the circuit has been cached (whether fully established or not)
			lwSimExperiment.logCircuitHit();
			
			if (bufs.get(msgDest).isCircuitAvail()) {
				// hit -- meaning the circuit has been cache and fully established
				lwSimExperiment.logHitAndReady();
			}
		}
	}
	
	protected void handleCacheAvailable(int msgDest, double now){	
		trySetupNewCircuit(msgDest, now);
	}
	
	protected int handleReplace(int msgDest, double now){
		Set<Integer> replaceCandidates = findReplaceCandidates(msgDest);
		int toBeReplaced = this.findReplacement(msgDest, replaceCandidates);

		replaceCircuit(toBeReplaced, now);
		
		/*
		 * The new circuit is assumed to be busy right after being brought into the cache
		 * so I didn't add it into the vacant set
		 */
		trySetupNewCircuit(msgDest, now);
		
		return toBeReplaced;
	}
	
	protected void trySetupNewCircuit(int msgDest, double now){
		allowedSetupNewCircuit(msgDest, now);
	}
	
	protected void allowedSetupNewCircuit(int msgDest, double now){
		bufs.get(msgDest).handleCircuitSetup();
		bringInNewCircuit(msgDest, now);
		scheduleBufferAvailable(msgDest, now + circuitSetupLatency);
	}
	
	protected void handleNoVacant(int msgDest, double now) {
		/* missed, and all current circuits are busy; do nothing */
	}

	@Override
	protected void processConsumerEvent(Evt e) {
		if (this.evtQueue.isEmpty())
			return;
		
		Evt nextInLine = this.evtQueue.peek();
		Message m = nextInLine.getMessage();
		int msgDest = m.dest;
		double now = e.getTimeNS();
		
		if (dontSendTraffic2Myself && m.dest == this.index) {
			deque();
			bootStrap(now);
			return;		// pretending as if message is directly received
		}
		
		String handle;
		
		if (isCircuitHit(msgDest)){			
			handleCircuitHit(msgDest, now);
			this.forwardEvt(now);
			handle = "Hit";
		} 
		else if (!isCacheFull()) {
			handleCacheAvailable(msgDest, now);
			this.forwardEvt(now);
			handle = "New";
		}
		else if (isCacheFullButWithVacant()) { // missed, but there are some vacant circuits; need replacement
			int toBeReplaced = handleReplace(msgDest, now);
			this.forwardEvt(now);
			handle = "Replace " + toBeReplaced;
		}
		else {	
			handleNoVacant(msgDest, now);
			handle = "Wait";
		}
		
		if (microLog && index == trackingNode) {
			String s = m.index + "\t" + msgDest + "\t" + handle + "\t" + now;
			//List<Integer> inCache = new ArrayList<Integer>(this.inCacheCircuitSet);
			//List<Integer> vacant = new ArrayList<Integer>(this.vacantCircuitSet);
			List<Integer> vacant = new ArrayList<>();
			for (int i = 0 ; i < circuitInfos.length ; i++) {
				if (circuitInfos[i].isVacant) vacant.add(i);
			}			
			
			//s = s+ "\t" +" Cache:" + "\t" + inCache.toString();
			s = s+ "\t" + "Vacant:" + "\t" + vacant.toString();
			lwSimExperiment.logThread(s, voqName);
		}
		
		return;
	}
	
	protected void addInCacheSet(int dest, double time){
        for (CircuitInfo circuitInfo : circuitInfos) {
            if (!circuitInfo.isOn) {
                circuitInfo.cacheStartTime = time;
                circuitInfo.destIndex = dest;
                circuitInfo.isVacant = false;
                circuitInfo.isOn = true;
                break;
            }
        }
	}
	
	public boolean bufReportVacantAfterBusy(int dest, double time){
		boolean keepVacant = this.maxVacantTime < 0 || csi.get(dest).predictTimeReuseDistance() <= maxVacantTime;
		if (keepVacant) {
			addInVacantSet(dest, time);
		}
		else {
			/*
			 * next reuse is too far away, turn off the circuit
			 * if add into vacant set, booStrap is called;
			 * hence it should also be called here
			 */
			srcTearDownCircuit(dest, time);
		}
		bootStrap(time);
		return keepVacant;
	}
	
	private void srcTearDownCircuit(int dest, double time) {
		this.removeFromCacheSet(dest, time);
	}

	protected void addInVacantSet(int dest, double time){

        for (CircuitInfo circuitInfo : circuitInfos) {
            if (circuitInfo.destIndex == dest) {
                circuitInfo.isVacant = true;
                circuitInfo.vacantStartTime = time;
                break;
            }
        }
		
		if (microLog && index == trackingNode) {
			lwSimExperiment.logThread(dest + "\t" + "Vacant" + "\t" + time, voqName);
		}
		
	}
	
	protected void removeFromVacantSet(int dest, double time){
        for (CircuitInfo circuitInfo : circuitInfos) {
            if (circuitInfo.destIndex == dest && circuitInfo.isVacant) {
                circuitInfo.isVacant = false;
                lwSimExperiment.logVacantTimeSpan(time - circuitInfo.vacantStartTime);
                circuitInfo.vacantStartTime = -1;
                break;
            }
        }
	}
	
	protected void removeFromCacheSet(int dest, double time){
		removeFromVacantSet(dest, time);
		
		for (int i = 0 ; i < circuitInfos.length ; i++) {
			if (circuitInfos[i].destIndex == dest) {
				desactivateCircuit(i, time);
			}
		}
		
		bufs.get(dest).handleCircuitReplaced();
	}
	
	private void desactivateCircuit(int index, double time) {
		double startTime = circuitInfos[index].cacheStartTime;
		int cirDest = circuitInfos[index].destIndex;
		if (sdTimeline) {
			Color c = ColorMap.getDarkTone(this.index, 0.3f);
			c = ColorMap.getLighterTone(c, this.maxNumCircuits, index);			
			TimeLine tl = bufs.get(cirDest).getTimeLine();
			if (tl != null) {
				tl.addJobPhase(startTime, time, this.index + ":-# " + index, c);
			}
		}
		if (srcTimelines != null) {
			Color c = ColorMap.getDarkTone(cirDest, 0.3f);
			c = ColorMap.getLighterTone(c, this.maxNumCircuits, index);			
			srcTimelines[index].addJobPhase(startTime, time, "to " + cirDest, c);
		}
		
		circuitInfos[index].isOn = false;
		circuitInfos[index].cacheStartTime = -1;
		circuitInfos[index].destIndex = -1;		
		lwSimExperiment.logInCacheTimeSpan(time - startTime);		
	}
	
	public void logEmission(double time, double tranTime, int bits, int val, Message m) {
		if (srcTimelines != null) {
			for (CircuitInfo info : circuitInfos) {
				if (info.destIndex == m.dest) {
					srcTimelines[info.circuitId].addJobPhase(time, time+tranTime, /*"m:" + m.index + "\r\n->" + m.dest*/"", Buffer.PACKET_BLUE, 1);	
				}
			}			
		}
			
	}	
	
	protected void bringInNewCircuit(int dest, double time){
		if (isCacheFull())
			throw new IllegalStateException("Cache full!");
		this.addInCacheSet(dest, time);
	}
	
	private Evt instantFwdEvt(double time){
		Evt nextInLine = evtQueue.poll();
		if (nextInLine == null) 
			throw new IllegalStateException("Empty Evt Queue!");
		
		this.forwardedId++;
		int dest = nextInLine.getMessage().dest;
		Evt next = new Evt(time, this, bufs.get(dest), 0, nextInLine);
		bufs.get(dest).processProducerEvent(next);
		return nextInLine;
	}

//	private int lastFwdDest = -1;
	/*
	 * forwardEvt preserves packet order (as the arrival order),
	 * lastFwdDest and lastDest are the same
	 */
	
	@Override
	protected Evt forwardEvt(double time) {
		/* old forward
		 * */
		Evt nextInLine = super.forwardEvt(time);

		Message msg = nextInLine.getMessage();
		int dest = msg.dest;
	
		bufs.get(dest).lockFwd();
		removeFromVacantSet(dest, time);
		
		recordCircuitUse(dest, time, msg.sizeInBits/8);
		
		/*if (index == trackingIndex) {
			lwSimExperiment.logThread(index + " --> " + dest + "\t" + "Forwarded" + "\t" + time);
		}*/
	//	lastFwdDest = dest;
		return nextInLine;
	}
	
	protected void recordCircuitUse(int dest, double time, int size) {
		
		if (coalescMode && dest == lastDest) {
			// ignoring reocurring dests in coalescs mode
			return;
		}
		
		circuitUseCount++;
		lwSimExperiment.logCircuitUse();

		// find unqiue distance using avl tree
		// disabled to save simulation time
		// int uniqueDistance = findUniqueDistance(dest);

		if (csi.get(dest).addSrcUseInstance(size, time, this.circuitUseCount)) {
			lwSimExperiment.logAccuratePredict();
		}

		// insert this use into the tree
		if (avlTreeEnabled)
			avlTree.insert(circuitUseCount);
		
		lastDest = dest;
	}
	
	
	/*
	 * return how many uses after lastSeen
	 * return -1 if first time seeing this dest
	 */
	private int findUniqueDistance(int dest) {
		int lastSeen = csi.get(dest).lastSeenIndexInSrc;
		if (lastSeen < 0)
			return -1;		// no reuse distance yet

		int rv = avlTree.removeAndRank(lastSeen);
		if (rv < 0)
			throw new IllegalStateException("cannot find key!");
		return rv;
	}

	private void scheduleBufferAvailable(int dest, double time){
		Evt start = new Evt(time, this, bufs.get(dest), "CIRCUIT_AVAILABLE");
		lwSimExperiment.manager.queueEvent(start);
	}

	@Override
	public void notifyEnd(double ref, double status) {
		
		for (int i = 0 ; i < circuitInfos.length ; i++) {
			if (circuitInfos[i].isVacant) {
				lwSimExperiment.logVacantTimeSpan(ref - circuitInfos[i].vacantStartTime);
			}
			if (circuitInfos[i].isOn) {
				desactivateCircuit(i, ref);
			}
		}
		
	/*	for (int i: this.destList){
			if (inCacheStartTime.get(i) >= 0){
				lwSimExperiment.logInCacheTimeSpan(ref - inCacheStartTime.get(i));
				inCacheStartTime.put(i, -1.0);
			}
			if (vacantStartTime.get(i) >= 0){
				lwSimExperiment.logVacantTimeSpan(ref - vacantStartTime.get(i));
				vacantStartTime.put(i, -1.0);
			}
		}*/
		
		if (index == trackingNode) {
			System.out.println("Remaining Queue Size: " + this.evtQueue.size());
			System.out.println("Circuit Use: " + circuitUseCount);
					
			// dump per-dest statistics
			for (int i: this.destList){
			// dump reuse distance of each dest
			/*try {
				dumpReuseDistance(i, "SRC_RD", 0);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			}
		}
		
		// dump src (this node) statistics
		if (mywriter != null) {
			mywriter.close();
		}
		
	}
	
	private String appName = "";
	private String mode =  "UNIQUE";
	
	public boolean dumpReuseDistance(int dst, String type, int reuseThreshold) throws FileNotFoundException, UnsupportedEncodingException{
		if (csi.get(dst) == null)
			return false;
		if (csi.get(dst).srcURDList.size() == 0)
			return false;
		
		int src = this.index;
		PrintWriter writer = null;
		
		String fileName = appName + "_" + lwSimExperiment.getNumberOfClients();
		String path = "./matlab/"+fileName +"/"+mode+"/"+type+"/";
		String writeTo = path+src+"_"+dst+".txt";
		DumpType dt = DumpType.valueOf(type); // surround with try/catch

		switch(dt) {
		    case SRC_RD:
		    	if (csi.get(dst).srcURDList.size() > reuseThreshold) {
		    		printArray(csi.get(dst).srcURDList, writeTo);
		    	}
		        break;
		    case DST_RD:
		    	
		        break;
		    case TIME_RD:
		    	
		    	break;
		    default:
		    	throw new IllegalStateException("Wrong dump type!");
		}			
		return true;
	}
	
	private PrintWriter mywriter = null;
	protected String writeName = "basic";


	
	public void logMyRank(String s){
		if (mywriter == null) {
			try {
				mywriter = new PrintWriter("./log/rank-" + this.index + 
						"_cache-" + this.maxNumCircuits + "." + writeName, "UTF-8");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		mywriter.println(s);
	}
	
	private void printArray(ArrayList l, String writeTo) throws FileNotFoundException, UnsupportedEncodingException{
		PrintWriter writer = new PrintWriter(writeTo, "UTF-8");
		String formatedString = l.toString()
                .replace(",", "")  //remove the commas
                .replace("[", "")   //remove the right bracket
                .replace("]", ""); //remove the left bracket
		writer.println(formatedString);
		writer.close();		
	}
	
	private enum DumpType {
	    SRC_RD, DST_RD, TIME_RD;
	}

	public void bufReportBusy(int myDest, double timeNS) {
		if (isCircuitVacant(myDest)) {
			removeFromVacantSet(myDest, timeNS);
		}
	}
	public void setTimeline(boolean sdTimeline, boolean srcTimeline) {
		this.sdTimeline = sdTimeline;
		this.srcTimelineFlag = srcTimeline;

	}
	

}
