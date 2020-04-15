package edu.columbia.sebastien.link_util.models;

import ch.epfl.general_libraries.experiment_aut.AbstractExperimentBlock;

public abstract class AbstractLinkPowerRequirementModel extends AbstractExperimentBlock {
	
	public abstract double getRequiredOpticalPowerMW(int lanes);

}
