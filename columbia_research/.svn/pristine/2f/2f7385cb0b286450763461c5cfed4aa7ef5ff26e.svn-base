package edu.columbia.lrl.experiments.topology_radix.routing_study;

import java.util.Arrays;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.Execution;
import edu.columbia.lrl.experiments.topology_radix.locality.AbstractTrafficMatrix;
import edu.columbia.lrl.experiments.topology_radix.routing_study.RoutingResult;
import edu.columbia.lrl.experiments.topology_radix.routing_study.structures.AbstractAxisStructure;

public class ImposedDimensionRoutingExperiment extends
		AbstractRoutingExperiment {
	
	private int dimensions;
	private int size;
	private int nodeMult;
	private String type;
	private boolean extraData;

	public ImposedDimensionRoutingExperiment(AbstractTrafficMatrix trafMat, 
			 @ParamName(name="Traffic load") double load,
			@ParamName(name="Dimensions size") int size, 
			@ParamName(name="Node multiplicity") int nodeMult,
			 @ParamName(name="With extra data?", default_="false") boolean extraData,	
			 GlobalStructure.MODE[] modes,
			@ParamName(name="Axes") String t) {
		super(trafMat, load, modes, extraData);
		this.size = size;
		this.type = t;
		this.nodeMult = nodeMult;
		this.dimensions = t.length();
	}
	
	
	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis) {
		

		
		Execution ex = new Execution();
		
		int[] sizes = new int[dimensions];
		Arrays.fill(sizes, size);
		Class[] types = new Class[dimensions];
		for (int i = 0 ; i < type.length() ; i++) {
			types[i] = AbstractAxisStructure.getCorrespondingAxis(type.charAt(i));
		}
		GlobalStructure gs = new GlobalStructure(sizes, types, nodeMult);
		trafMat.init((int)Math.pow(size, dimensions)*nodeMult, gs);
		RoutingResult rr = gs.routeTraffic(trafMat, extraData);
		for (GlobalStructure.MODE m : modes) {
			rr.setMode(m);
			rr.store(ex);
		}
		
		man.addExecution(ex);

	}

}
