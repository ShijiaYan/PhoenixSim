package edu.columbia.sebastien.link_util.models;

import edu.columbia.sebastien.link_util.LinkUtilisationExperiment;
import ch.epfl.general_libraries.experiment_aut.AbstractExperimentBlock;

public abstract class AbstractLinkDimensioningModel extends AbstractExperimentBlock {

	public abstract void build(LinkUtilisationExperiment lue);

	public abstract double getTransmissionTimeNS(int sizeInBits, int linkId);
	
	public abstract int getLanes(int linkId);
	
	public abstract int[] getLanes();

	public abstract double getJoules(double transmissionTime, int linkId);

	public abstract double getNumberOfLanes();
	
	

}
