package edu.columbia.lrl.experiments.topology_radix.routing_study;

import edu.columbia.lrl.experiments.topology_radix.locality.AbstractTrafficMatrix;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.Execution;

public class BasicRoutingExperiment extends AbstractRoutingExperiment {
	
	private boolean trafPColor;
	private boolean topoPColor;
	private boolean resultPcolor;
	

	
	public BasicRoutingExperiment(GlobalStructure gloStr, 
								  AbstractTrafficMatrix trafMat, 
								  @ParamName(name="Mode", default_="ONE") GlobalStructure.MODE[] modes,
									 @ParamName(name="Node mult", default_="1") int nodeMult,
									 @ParamName(name="Traffic load", default_="1") double load,
									 @ParamName(name="Extra data", default_="false") boolean extraData, 
								  @ParamName(name="Traf pColor?", default_="false") boolean trafPColor,
								  @ParamName(name="Topo pColor?", default_="false") boolean topoPColor,
								  @ParamName(name="Result pColor?", default_="false") boolean resultPColor) {
		super(trafMat, load, modes, extraData);
		this.gloStr = gloStr;
		this.trafPColor = trafPColor;
		this.topoPColor = topoPColor;
		this.resultPcolor = resultPColor;
		if (nodeMult > 0) {
			if (gloStr.getMultiplicity() > 1) System.err.println("Warning: overriding node multiplcity");
			gloStr.setNodeMultiplicity(nodeMult);
		}
	}
	

	


	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis) {
		
	
		
		Execution execution = route();
		
		if (trafPColor) {
			trafMat.showPColor();
		}
		if (topoPColor)
			gloStr.showPColor();
		if (resultPcolor)
			gloStr.showTrafPColor();
		
		man.addExecution(execution);
	}

}
