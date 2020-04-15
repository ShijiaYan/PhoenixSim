package edu.columbia.sebastien.link_util;

import java.util.HashMap;
import java.util.Map;

import edu.columbia.lrl.LWSim.EventManager;
import edu.columbia.lrl.LWSim.SimulationEndCriterium;
import edu.columbia.lrl.general.EventTarget;
import edu.columbia.lrl.general.Evt;
import edu.columbia.lrl.general.SimulationExperiment;
import edu.columbia.sebastien.link_util.models.AbstractBufferModel;
import edu.columbia.sebastien.link_util.models.OpticalPowerModel;
import edu.columbia.sebastien.link_util.models.TrafficModel;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.graphics.timeline.TimeLineGUI;
import ch.epfl.general_libraries.graphics.timeline.TimeLineSet;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.general_libraries.results.Execution;
import ch.epfl.general_libraries.utils.DoubleAccumulator;
import ch.epfl.general_libraries.utils.MoreArrays;

public class LinkUtilisationExperiment extends SimulationExperiment implements Experiment {
	// param
	private TrafficModel trafMod;
	private AbstractBufferModel bufMod;
	private OpticalPowerModel powMod;
	
	// helping object during runtime
	private EventManager manager;
	
	// results collectors
	private HashMap<String, DoubleAccumulator> soaData = new HashMap<String, DoubleAccumulator>();	
	private HashMap<String, DoubleAccumulator> laserData = new HashMap<String, DoubleAccumulator>();
	private HashMap<String, DoubleAccumulator> linkUtilisations = new HashMap<String, DoubleAccumulator>();
//	private HashMap<String, HashMap<String, DoubleAccumulator>> activities = new HashMap<String, HashMap<String, DoubleAccumulator>>();
	private double[] headToHeadLatenciesTotal;
	private double[] headToTailLatenciesTotal;	
	private int[] messages;
	private boolean timeLine;
	
	public LinkUtilisationExperiment(TrafficModel trafMod, 
									 AbstractBufferModel bufMod, 
									 OpticalPowerModel powMod, 
									 boolean timeLine) {
		this.trafMod = trafMod;
		this.bufMod = bufMod;
		this.powMod = powMod;
		this.timeLine = timeLine;
	}
	
	public int getNbLinks() {
		return trafMod.getNumberOfLinks();
	}
	
	public boolean isWithTimeLine() {
		return timeLine;
	}

	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis) {
		// TODO Auto-generated method stub
		int links = trafMod.getNumberOfLinks();
		
		headToTailLatenciesTotal = new double[links];
		headToHeadLatenciesTotal = new double[links];
		messages = new int[links];
		
		manager = new EventManager(this);

		bufMod.build(powMod.getPowerSharingModel(), this);
		
		powMod.build(bufMod.getLinkDimensioningModel().getLanes(), this);

		trafMod.init(bufMod, this);
		
		manager.runSimulation();
		
		bufMod.noMoreMessage();
		 
		
		storeResults(man, manager.getClock());
		
		if (timeLine) {
			TimeLineSet timeLineSet = new TimeLineSet();
			
			timeLineSet.addAll(bufMod.getTimeLines());
			timeLineSet.addAll(powMod.getTimeLines());
			
			new TimeLineGUI(timeLineSet);
		}
		
	}
	
	public void scheduleEvent(EventTarget target, double timeNS) {
		Evt ev = new Evt(timeNS, target);
		manager.queueEvent(ev);
	}
	
	public void scheduleEvent(Evt e) {
		manager.queueEvent(e);
	}
	
/*	private DoubleAccumulator getFromTable(HashMap<String, DoubleAccumulator> table, String key) {
		DoubleAccumulator accum = table.get(key);
		if (accum == null) {
			accum = new DoubleAccumulator();
			table.put(key, accum);
		}
		return accum;
	}*/

/*	public void accountForConsumption(double joules, String string) {

		accum.add(joules);	
	}*/
	
	public void accountForLinkUtilisation(double joules, double duration, int bits, String linkId) {
		DoubleAccumulator accum = linkUtilisations.get(linkId);
		if (accum == null) {
			accum = new DoubleAccumulator(3);
			linkUtilisations.put(linkId, accum);
		}
		accum.add(joules, 0);
		accum.add(duration, 1);
		accum.add(bits, 2);
	}
	
	public void accountForLaserOn(double joules, double timeOn, double timeLasing, String id) {
		DoubleAccumulator accum = laserData.get(id);
		if (accum == null) {
			accum = new DoubleAccumulator(3);
			laserData.put(id, accum);
		}
		accum.add(joules, 0);
		accum.add(timeOn, 1);
		accum.add(timeLasing, 2);		
	}
	

	public void accountForSOAOn(double joules, double timeOn, double timeUseful, String soaId) {
		DoubleAccumulator accum = soaData.get(soaId);
		if (accum == null) {
			accum = new DoubleAccumulator(3);
			soaData.put(soaId, accum);
		}
		accum.add(joules, 0);
		accum.add(timeOn, 1);
		accum.add(timeUseful, 2);	
	}	
	
	/*public void accountForActivity(double value, String activityType, String id) {
		HashMap<String, DoubleAccumulator> activityGroup = activities.get(activityType);
		if (activityGroup == null) {
			activityGroup = new HashMap<String, DoubleAccumulator>();
			activities.put(activityType, activityGroup);
		}
		DoubleAccumulator accum = getFromTable(activityGroup, id);
		accum.add(value);	
	}	*/
	
	public void accountForLatency(double headToHead, double headToTail, int i) {
		headToHeadLatenciesTotal[i] += headToHead;
		headToTailLatenciesTotal[i] += headToTail;
		messages[i]++;
	}	

	@Override
	public void timeElapsed(double timeNS) {}

	@Override
	public int checkIfContinueSimulation(double timeNS) { 
		return 0;
	}
	
	private Map<String, String> getAllParameters() {
		Map<String, String> map = bufMod.getAllParameters();
		map.putAll(powMod.getAllParameters());
		map.putAll(trafMod.getAllParameters());
		map.put("traffic gen mod", trafMod.getClass().getSimpleName());
		map.put("buffering mod", bufMod.getClass().getSimpleName());
		return map;
	}

	private void storeResults(AbstractResultsManager man, double endTime) {
		Execution e = new Execution();
		
		DataPoint dp = new DataPoint(getAllParameters());
		
		double[] totals = new double[9]; // 
		String[] titles = new String[]{"laser energy", "laserOn", "laserReady",
				                           "soa energy", "soa on", "soa ready",
				                           "link energy", "link time utilisation", "link bits utilisation"};

			
	//	int index = 0;
		for (int i = 0 ; i < 3 ; i++) {
			totals[i] = 0;
			for (Map.Entry<String, DoubleAccumulator> entry : laserData.entrySet()) {
				totals[i] += entry.getValue().getValue(i);
			}
	//		index++;			
		}
		
		for (int i = 0 ; i < 3 ; i++) {
			totals[i+3] = 0;
			for (Map.Entry<String, DoubleAccumulator> entry : soaData.entrySet()) {
				totals[i+3] += entry.getValue().getValue(i);
			}	
	//		index++;			
		}
		
		for (int i = 0 ; i < 3 ; i++) {
			totals[i+6] = 0;
			for (Map.Entry<String, DoubleAccumulator> entry : linkUtilisations.entrySet()) {
				totals[i+6] += entry.getValue().getValue(i);
			}	
	//		index++;			
		}		
		
		// time values
		for (int i : new int[]{1,2,4,5,7}) {
			DataPoint copy = (DataPoint)dp.clone();	
			copy.addProperty("measurement name", titles[i]);
			copy.addResultProperty("total time measurement", totals[i]);
			e.addDataPoint(copy);
			
			copy = (DataPoint)dp.clone();	
			copy.addProperty("average utilisation name", titles[i]);
			copy.addResultProperty("average utilisation", totals[i]/endTime);
			e.addDataPoint(copy);
		}

		// power values
		for (int i : new int[]{0,3,6}) {
			DataPoint copy = (DataPoint)dp.clone();	
			copy.addProperty("measurement name", titles[i]);
			copy.addResultProperty("total power measurement (mW)", totals[i]*1000);
			e.addDataPoint(copy);
		}
		
		dp.addResultProperty("laser overhead (time heating/time on)", (totals[1]-totals[2])/totals[1]);
		dp.addResultProperty("soa overhead (time heating/time on)", totals[4]-totals[5]/totals[4]);
		
		dp.addResultProperty("Transmission efficiency (pJ/b)", 1e12* (totals[0]+totals[3]+totals[6])/totals[8]);
		
		dp.addResultProperty("total bits transmitted", totals[8]);
		
		dp.addResultProperty("observed rate in Gb/s", totals[8]/trafMod.getSimeTimeNS());
		double obsLinkUtil = (totals[8]/trafMod.getSimeTimeNS())/(10*bufMod.getLinkDimensioningModel().getNumberOfLanes());
		dp.addResultProperty("observed utilization", obsLinkUtil);
		double obsLaserUtil = totals[1]/trafMod.getSimeTimeNS();
		dp.addResultProperty("observed laser utilization", obsLaserUtil);
		dp.addResultProperty("laser proportionality", 1 - ((obsLaserUtil-obsLinkUtil)/(1-obsLinkUtil)));
		
		dp.addResultProperty("energy", totals[0] + totals[3] + totals[6]);
		dp.addResultProperty("average head-to-head latency in ns", MoreArrays.sum(headToHeadLatenciesTotal) / (double)(MoreArrays.sum(messages)));
		dp.addResultProperty("average head-to-tail latency in ns", MoreArrays.sum(headToTailLatenciesTotal) / (double)(MoreArrays.sum(messages)));	
		//dp.addResultProperty("cumulated buffering (bit*s), b);
		e.addDataPoint(dp);
		
		man.addExecution(e);
		
	}

	@Override
	public SimulationEndCriterium getEndCriterium() {
		return null;
	}


}
