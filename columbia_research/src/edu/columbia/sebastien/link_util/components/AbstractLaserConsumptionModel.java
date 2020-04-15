package edu.columbia.sebastien.link_util.components;

import ch.epfl.general_libraries.experiment_aut.AbstractExperimentBlock;

public abstract class AbstractLaserConsumptionModel extends AbstractExperimentBlock {

	public abstract double getJoules(double lasingTime, double actualLasingPowerMW);

}
