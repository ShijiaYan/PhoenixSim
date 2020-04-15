package edu.columbia.lrl.LWSim;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.graphics.timeline.TimeLine;
import ch.epfl.general_libraries.graphics.timeline.TimeLineGUI;
import ch.epfl.general_libraries.graphics.timeline.TimeLineSet;
import ch.epfl.general_libraries.random.MersenneTwister;
import ch.epfl.general_libraries.random.PRNStream;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.general_libraries.results.Execution;
import ch.epfl.general_libraries.simulation.Time;
import ch.epfl.general_libraries.traffic.Rate;
import ch.epfl.general_libraries.utils.MoreArrays;
import ch.epfl.general_libraries.utils.Pair;
import ch.epfl.general_libraries.utils.SimpleMap;
import edu.columbia.lrl.LWSim.analysers.AbstractLWSimAnalyser;
import edu.columbia.lrl.LWSim.application.ActionManager;
import edu.columbia.lrl.LWSim.builders.AbstractTopologyBuilder;
import edu.columbia.lrl.general.Evt;
import edu.columbia.lrl.general.Message;
import edu.columbia.lrl.general.SimulationExperiment;


public class LWSIMExperiment extends SimulationExperiment implements Experiment {
	
	public final EventManager manager;
	private final MersenneTwister random;
	private final MersenneTwister randomForTraffic;
	private final SimulationEndCriterium criterium;
	int seed;
	protected AbstractTopologyBuilder builder;
	private AbstractTrafficGenerator trafGenerator;
	protected AbstractLWSimAnalyser[] analysers;
	public boolean withTimeLine;
	

	Rate referenceBandwidth;	
	
	// temporary, for dumping AMR traces
	public PrintWriter writer; 
	
	// Analysis part	
	public long totalEmitted;
	public long totalDrop;
	public long totalReceived;
	public long totalRetran;
	public double totalHeadToHeadLatency;	
	public double totalHeadToQueueLatency;
	public int[] perDestReceived;
	
	public long emittedBits;
	public long retransmittedBits;
	public long receivedBits;
	
	private TimeLineSet timeListSet = new TimeLineSet();

	public HashMap<Integer, DataPoint> sourceDataPoints;
	public DataPoint[][] srcDestDataPoints;
	final private DataPoint defaultDatapoint;
	final private DataPoint globalResultsDatapoint;
	protected Execution ex;	
	
/*	public LWSIMExperiment(@ParamName(name="Model builder") AbstractTopologyBuilder builder, 
			   @ParamName(name="The default traffic generator") AbstractTrafficGenerator trafGenerator,
			   @ParamName(name="PRNG Seed", default_="0") int seed, 
			   @ParamName(name="Simulate until?", defaultClass_=PerDestinationReceivedPacketEndCriterium.class) SimulationEndCriterium criterium			   ) {	
		this(builder, trafGenerator, seed, criterium, (AbstractLWSimAnalyser[])null, false);
	}*/
	
	public LWSIMExperiment(@ParamName(name="Model builder") AbstractTopologyBuilder builder, 
			@ParamName(name="The default traffic generator") AbstractTrafficGenerator trafGenerator,
			@ParamName(name="PRNG Seed", default_="0") int seed, 
			   @ParamName(name="Simulate until?", defaultClass_=PerDestinationReceivedPacketEndCriterium.class) SimulationEndCriterium criterium,
		//	   @ParamName(name="Load scheme", defaultClass_=RelativeToReferenceBWLoad.class) AbstractLoadScheme loadScheme,
			   @ParamName(name="Use additional analyzer?", default_="null") AbstractLWSimAnalyser analyser,
			   @ParamName(name="Popup time line?", default_="false") boolean withTimeLine
			   ) {	
		this(builder, trafGenerator, seed, criterium, new AbstractLWSimAnalyser[]{analyser}, withTimeLine);
	}
		
	public LWSIMExperiment(@ParamName(name="Model builder") AbstractTopologyBuilder builder,
			@ParamName(name="The default traffic generator") AbstractTrafficGenerator trafGenerator,
			 			   @ParamName(name="PRNG Seed", default_="0") int seed, 
			 			   @ParamName(name="Simulate until?", defaultClass_=PerDestinationReceivedPacketEndCriterium.class) SimulationEndCriterium criterium,
		//	 			   @ParamName(name="Load scheme", defaultClass_=RelativeToReferenceBWLoad.class) AbstractLoadScheme loadScheme,
						   AbstractLWSimAnalyser[] analysers,
			 			   @ParamName(name="Popup time line?", default_="false") boolean withTimeLine
						   ) {	
		super();
		random = new MersenneTwister(seed);	
		randomForTraffic = new MersenneTwister(random.nextInt());
		manager = new EventManager(this);				
		this.seed = seed;
		this.trafGenerator = trafGenerator;		
		this.builder = builder;
		this.criterium = criterium;
		this.withTimeLine = withTimeLine;
		
		defaultDatapoint = new DataPoint();
		this.globalResultsDatapoint = defaultDatapoint.getDerivedDataPoint();		
		
		this.analysers = MoreArrays.reorganize(analysers, AbstractLWSimAnalyser.class);
		
		ex = new Execution();
	}
	
	public Map<String, String> getAllParameters() {
		Map<String, String> map = SimpleMap.getMap(
			//	"Simulation time", simTimeNS+"",
				"Seed", seed+"");
		map.putAll(criterium.getAllParameters());
		map.putAll(builder.getAllParameters());
		map.putAll(trafGenerator.getAllParameters(this));
		return map;
	}
	
	public Execution getExecution() {
		return ex;
	}	
	
	public AbstractTopologyBuilder getTopologyBuilder() {
		return builder;
	}	
	
	public AbstractTrafficGenerator getTrafficGenerator() {
		return trafGenerator;
	}
	
	public PRNStream getRandomStreamForEverythingButTraffic() {
		return random;
	}
	
	public PRNStream getRandomStreamForTraffic() {
		return randomForTraffic;
	}
	
	public EventManager getEventManager() {
		return manager;
	}
	
	public Execution getExecutionObject() {
		return ex;
	}
	
	public void addPropertyToDefaultDataPoint(String s1, String s2) {
		defaultDatapoint.addProperty(s1, s2);
	}
	
	public boolean defaultDataPointHasProperty(String s) {
		return defaultDatapoint.hasProperty(s);
	}
	
	public void addGlobalResult(String s1, Object s2) {
		globalResultsDatapoint.addResultProperty(s1,  s2+"");
	}
		
	/**
	 * Added by Rob. Used when the defaultDataPoint has some stuff that is collected before/outside LWSim.
	 */
/*	ArrayList<LWSimComponent> comp;
	public void init() {
		comp = new ArrayList<LWSimComponent>();
		InitFeedback failure = builder.buildAbstract(this, comp);
		if (failure != null) {
			System.out.println(failure.failureReason);
			return;
		}
		int clients = builder.getNumberOfClients();
		perDestReceived = new int[clients];	
	}
	
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis, DataPoint defaultDataPoint, boolean recordResults) {
		this.defaultDatapoint = defaultDataPoint;  
		int status = launchSim(man);
		builder.notifySimulationEnd(this, comp, manager.getClock(), status);
		if( recordResults ) addInfo(man);	
		if (withTimeLine) {
			new TimeLineGUI(timeListSet);
		}
	}*/
	/**
	 * End Rob's stuff.
	 */
	protected InitFeedback init() {
		String threadName = Thread.currentThread().getName();
		try {
			Thread.currentThread().setName(threadName+ " - LWSim init");
			ActionManager.msgId = 0;
			trafGenerator.setMessageOriginal(builder.getMessageToUse());		
			InitFeedback failure;
			
			// Then traffic generator init
			failure = trafGenerator.initTrafficGeneratorTemplateFirstPass(this);
			if (failure != null) {
				return failure;
			}			
			Pair<InitFeedback, ArrayList<LWSimComponent>> pair = builder.buildAbstract(this);
			if (pair.getFirst() != null) {
				return pair.getFirst();
			}			
			
			// Second init, in case it required some stuff fixed by the builder
			failure = trafGenerator.initTrafficGeneratorTemplateSecondPass(this);
			if (failure != null) {
				return failure;
			}
			for (AbstractLWSimAnalyser a : this.analysers) {
				a.init(this);
			}				
			
			// Initialisation of all components must happend after the "negociation" between the builder and the traffic
			for (LWSimComponent dest : pair.getSecond()) {
				failure = dest.initComponent(this);
				if (failure != null) return failure;
			}
			
			
			int clients = builder.getNumberOfClients();
			perDestReceived = new int[clients];	
			
			// seb: way to go around rob changes
			defaultDatapoint.addProperties(getAllParameters());
			
			return null;
		}
		finally {
			Thread.currentThread().setName(threadName);
		}
	}
	
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis) {
		InitFeedback feedback = init();
		
		if (feedback != null) {
			System.out.println("Init of LWSIM experiment failed: " + feedback.failureReason);
			return;		
		}
		int status = launchSim(man);
		prepareSimulationEnd(status);

		addInfo(man, status);	
		if (withTimeLine) {
			new TimeLineGUI(timeListSet);
		}	

	}
	
	/**
	 * This method is put apart to ease extension of LWSimExp. By overloading this method (and calling super)
	 * another round of analyses can be conducted, for instance.
	 * @param status: -1 if an error occured, 0 if no more events, 1 if end criterium reached
	 */
	protected void prepareSimulationEnd(int status) {
		for (LWSimComponent dest : builder.getModelElements()) {
			dest.notifyEnd(manager.getClock(), status);
		}
		trafGenerator.notifyEnd(manager.getClock(), status);
		for (AbstractLWSimAnalyser analyzer : this.analysers) {
			analyzer.notifyEnd(manager.getClock(), status);
		}
		builder.notifyEnd(manager.getClock(), status);
	}
	
	/**
	 * This method is put part to ease extension of LWSimExp, for instance for Graphical Visualisation
	 * @param man
	 * @return -1 if an error occured, 0 if no more events, 1 if end criterium reached
	 */
	protected int launchSim(AbstractResultsManager man) {
		try {
			criterium.simulationStarted(this);			
			return manager.runSimulation();	
		}
		catch (WrongExperimentException e) {
			return -1;
		}
		catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}
	
	public boolean isWithTimeLine() {
		return withTimeLine;
	}
		
/*	public Rate getLoadPerClient() {
		if (loadScheme != null) {
			return loadScheme.getLoadPerClient(this);
		} else {
			return Rate.NULL_RATE;
		}
	}*/
	
	public Rate getTotalInjectionBandwidth() {
		return getReferenceBandwidth().multiply(builder.getTotalInjectionBandwidthRatio());
	}
	
	public Rate getReferenceBandwidth() {
		if (referenceBandwidth == null)
			throw new IllegalStateException("The reference bandwidth must be set in lwSimExp by one of the sub object, preferably the builder");
		return referenceBandwidth;
	/*	if (changed != null) {
			return changed;
		} else {
			return loadScheme.getNominalLinkRate();			
		}*/
	}
	
	public SimulationEndCriterium getEndCriterium() {
		return this.criterium;
	}
	
	public void setReferenceBandwidth(Rate r) {
		if (referenceBandwidth != null) {
			System.out.println("WARNING: Reference bandwidth replaced, was " + referenceBandwidth + ", is now " + r);
		}
		referenceBandwidth = r;
	}
	
	public int getNumberOfClients() {
		return builder.getNumberOfClients();
	}
	
	public DataPoint getDerivedDatapoint() {
		return defaultDatapoint.getDerivedDataPoint();
	}
	
	public DataPoint getSourceDestDataPoint(int src, int dest) {
		if (srcDestDataPoints == null) {
			int clients = builder.getNumberOfClients();
			srcDestDataPoints = new DataPoint[clients][clients];
		}
		if (srcDestDataPoints[src][dest] == null) {
			int clients = builder.getNumberOfClients();
			int zeros = (int)Math.max(Math.ceil(Math.log10(clients)),1);
			DataPoint perSrcDest = getDerivedDatapoint();

			perSrcDest.addProperty("src", String.format("%0"+zeros+"d", src));
			perSrcDest.addProperty("dest", String.format("%0"+zeros+"d", dest));
			perSrcDest.addProperty("src-dest",String.format("%0"+zeros+"d", src)+"-"+String.format("%0"+zeros+"d", dest));
			perSrcDest.addProperty("srcdest_offset",dest - src);
			
			srcDestDataPoints[src][dest] = perSrcDest;
		}
		return srcDestDataPoints[src][dest];
	}
	
	public DataPoint getSourceDataPoint(int src) {
		if (sourceDataPoints == null)
			sourceDataPoints = new HashMap<Integer, DataPoint>();
		DataPoint dp = sourceDataPoints.get(src);
		if (dp == null) {
			int clients = builder.getNumberOfClients();
			int zeros = (int)Math.max(Math.ceil(Math.log10(clients)),1);
			dp = getDerivedDatapoint();
			dp.addProperty("Source", String.format("%0"+zeros+"d", src));	
			sourceDataPoints.put(src,dp);
		}
		return dp;
	}	
	
	/*-/***********************************
	 *  EVENT METHODS
	 * ***********************************
	*/
	
	public void timeElapsed(double time) {
		for (int i = 0 ; i < analysers.length ; i++) {
			analysers[i].timeElapsed(time);
		}		
	}
	
	public void packetTransmitted(Message m) {
		for (int i = 0 ; i < analysers.length ; i++) {
			analysers[i].packetTransmitted(m);
		}
	}
	
	public double totalTransTime;
	public double totalReTransTime;
	public void reportTransTime(int index, double time, Message m){
		if (m.numTrans > 1)
			this.totalReTransTime += time;
		this.totalTransTime += time;
	}
	
	public void packetEmitted(Message m) {
		emittedBits += m.sizeInBits;
		totalEmitted++;	
		for (int i = 0 ; i < analysers.length ; i++) {
			analysers[i].packetEmitted(m);
		}	
	}
	
	/* Valid values for dropType:
	 * 2: dropped due to packet expiration
	 * others: dropped due to contention
	 */
	public void packetDropped(Message m, String where, TrafficDestination swi, int dropType) {
		totalDrop++;

		switch (dropType) {
		case 2:
			for (int i = 0 ; i < analysers.length ; i++) {
				analysers[i].packetQuenched(m, where);
			}
			break;
		default:
			for (int i = 0 ; i < analysers.length ; i++) {			
				analysers[i].packetContented(m, where, swi, dropType);	 
			}
		}
	}

	public void packetReceived(Message m, int origin, int dest, double timeEmitted, double timeReceived) {
		receivedBits += m.sizeInBits;
		totalHeadToHeadLatency += timeReceived - timeEmitted;
		totalHeadToQueueLatency += (timeReceived - timeEmitted) + m.lastDuration;

		totalReceived++;
		perDestReceived[dest]++;
		for (int i = 0 ; i < analysers.length ; i++) {
			analysers[i].packetReceived(m, origin, dest, timeEmitted, timeReceived);
		}		
	}
	
	public void packetRetransmitted(Message m) {
		retransmittedBits += m.sizeInBits;
		totalRetran++;
		for (int i = 0 ; i < analysers.length ; i++) {
			analysers[i].packetRetransmitted(m);
		}		
	}
	

	protected void addInfo(AbstractResultsManager man, int status) {
		
		double simTime = manager.getClock();
		double executionTime = manager.getLastWallClock();
		int clients = builder.getNumberOfClients();
		DataPoint globals = getDerivedDatapoint();
		globals.addResultProperty("peak memory use %", manager.getPeakMemoryUse());
		globals.addResultProperty("peak memory", manager.getPeakMemoryAbs());
		globalResultsDatapoint.addResultProperty("execution time (wall clock) (ms)", executionTime);	
		globalResultsDatapoint.addResultProperty("Simulated time (ns)", simTime);
		globals.addResultProperty("packet received", totalReceived);
		globals.addResultProperty("packet sent", totalEmitted);
		globals.addResultProperty("packet dropped", totalDrop);
		globals.addResultProperty("packet left in network", totalEmitted - totalReceived - totalDrop);
		globals.addResultProperty("packet retransmitted", totalRetran);
		long bitsSentPerClient = (long)Math.ceil((emittedBits + retransmittedBits)/(double)clients);
		System.out.println("clients:" + clients ); 
		long bitsReceivedPerClient = (long)Math.ceil(receivedBits/(double)clients);
		Rate observedSent = new Rate(bitsSentPerClient, simTime/1000d);
		Rate observedReceived = new Rate(bitsReceivedPerClient, simTime/1000d);
		Rate observedReceivedTotal = new Rate(receivedBits, simTime/1000d);
		observedSent.setDataSizeUnit("gbit");
		observedSent.setTimeUnit("s");
		observedReceived.setDataSizeUnit("gbit");
		observedReceived.setTimeUnit("s");
		globalResultsDatapoint.addResultProperty("Emitted bits", emittedBits);
		globalResultsDatapoint.addResultProperty("Received bits", receivedBits);		
		globalResultsDatapoint.addResultProperty("per client observed received rate (gbit/s)", observedReceived.getInGbitSeconds());
		globalResultsDatapoint.addResultProperty("per client observed sending rate (gbit/s)", observedSent.getInGbitSeconds());

		if (status >= 0) {
			globalResultsDatapoint.addResultProperty("Per client utilization (observed load relative to reference rate)", observedSent.divide(this.getReferenceBandwidth()));
			globalResultsDatapoint.addResultProperty("Network utilisation (observed relative load)", observedReceivedTotal.divide(this.getTotalInjectionBandwidth()));
			globalResultsDatapoint.addResultProperty("latency in ns", totalHeadToHeadLatency/(double)totalReceived);	
			globals.addResultProperty("drop rate", (double)totalDrop/(double)(totalReceived + totalDrop));
			globals.addResultProperty("acceptance rate", 1 - (double)totalDrop/(double)(totalReceived + totalDrop));
			globals.addResultProperty("average retransmissions", (double)totalRetran/(double)totalEmitted);
			globalResultsDatapoint.addResultProperty("Head to tail latency", totalHeadToQueueLatency/(double)totalReceived);
		}			
		
		for (int i = 0 ; i < analysers.length ; i++) {
			analysers[i].addInfo(getDerivedDatapoint(), globals, ex, simTime);
		}	
		
		ex.addDataPoint(globalResultsDatapoint);
		ex.addDataPoint(globals);
		man.addExecution(ex);
		
		if (writer!=null)
			this.writer.close();
	}

	
	public ArrayList<Evt> getRelatedEvents(TrafficDestination d) {
		ArrayList<Evt> list = new ArrayList<Evt>();
		for (Evt e : manager.getEventsRelatedTo(d)) {
			list.add(e);
		}
		return list;
	}

	public void addTimeLine(TimeLine timeLine) {
		timeListSet.add(timeLine);		
	}

/*	public int getPacketSize() {
		return trafGenerator.getAveragePacketSize();
	}*/

	public int checkIfContinueSimulation(double timeSoFar) {
		return criterium.check(timeSoFar);
	}
	
	public double getSimTimeNS() {
		return manager.getClock();
	}
	
	public void logRankComm(int srcRank, int dstRank, int size, Time ref){
		if (writer == null){
			try {
				writer = new PrintWriter("AMR_trace_"+this.getNumberOfClients(), "UTF-8");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		writer.println(srcRank + "\t" + dstRank + "\t" + size/1024/8 + "\t" + ref.getNanoseconds());
	}
	
	public void logThread(String s, String voqType){
		if (writer == null) {
			try {
				writer = new PrintWriter("./log/AMR_trace_"
						+ this.getNumberOfClients() + "_" + voqType, "UTF-8");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		writer.println(s);
	}
	
	public int nHit;
	
	public void logCircuitHit(){
		nHit++;
	}
	
	public int nCircuitUse;
	public void logCircuitUse(){
		nCircuitUse++;
	}

	public int nAccuratePredict;
	public void logAccuratePredict() {
		nAccuratePredict++;		
	}

	public double totalVacantTime;		//minisecond
	public void logVacantTimeSpan(double d) {
		totalVacantTime += d/1e6;
		
	}
	
	public double totalCacheTime;	//minisecond

	public void logInCacheTimeSpan(double d) {
		totalCacheTime += d/1e6;
		
	}
	
	private float onPower = 1;
	private float vacantPower = onPower * 1;
	
	public double calcTotalPower(){
		if (totalVacantTime > totalCacheTime)
			throw new IllegalStateException("totalVacantTime > totalCacheTime");
		return (totalCacheTime - totalVacantTime )*onPower + totalVacantTime * vacantPower;
	}

	public int nPrefetchHit;
	public int nPfHitWithinTail;
	public void logPrefetchHit(boolean prefetched, boolean withinTail) {
		if (prefetched) {
			nPrefetchHit++;
			if (withinTail) {
				nPfHitWithinTail++;
			}
		}
	}
	
	public int nPrefetch;
	public void logPrefetch(){
		nPrefetch++;
	}

	public int nHitAndReady;
	public void logHitAndReady() {
		nHitAndReady++;
	}
	
}
