package edu.columbia.lrl.experiments.topology_radix.routing_study;

import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.results.Execution;
import edu.columbia.lrl.experiments.topology_radix.locality.AbstractTrafficMatrix;
import edu.columbia.lrl.experiments.topology_radix.routing_study.RoutingResult;

public abstract class AbstractRoutingExperiment implements Experiment {

	protected GlobalStructure gloStr;
	protected AbstractTrafficMatrix trafMat;
	protected GlobalStructure.MODE[] modes;
	protected boolean withDetails;
	
	public AbstractRoutingExperiment(AbstractTrafficMatrix trafMat, 
			double load, GlobalStructure.MODE[] modes, boolean withDetails) {
		this.trafMat = trafMat;
		this.modes = modes;
		if (modes.length == 0) throw new IllegalStateException("give at least one mode");
		trafMat.setNormalisedLoad(load);
		this.withDetails = withDetails;
	}
	
	public GlobalStructure getGlobalStructure() {
		return gloStr;
	}
	
	public Execution route() {	
		Execution execution = new Execution();
		RoutingResult rr = gloStr.routeTraffic(trafMat, withDetails);
		for (GlobalStructure.MODE m : modes) {
			rr.setMode(m);
			rr.store(execution);
		}
		return execution;
	}
	
}
