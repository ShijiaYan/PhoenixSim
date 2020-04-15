package edu.columbia.sebastien.link_util.components;

import edu.columbia.sebastien.link_util.LinkUtilisationExperiment;

public interface LinkUtilisationObject<K> {

	public void setLinkUtilisationExperiment(LinkUtilisationExperiment lue);
	
	public K getCopy(int index, int maxIndex, boolean timeLine);
}
