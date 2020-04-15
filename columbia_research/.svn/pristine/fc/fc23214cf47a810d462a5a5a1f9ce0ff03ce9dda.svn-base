package edu.columbia.sebastien.link_util.models;

import java.util.Map;

import edu.columbia.lrl.general.EventTarget;
import edu.columbia.lrl.general.Message;
import edu.columbia.sebastien.link_util.LinkUtilisationExperiment;
import ch.epfl.general_libraries.experiment_aut.AbstractExperimentBlock;
import ch.epfl.general_libraries.graphics.timeline.TimeLine;

public abstract class AbstractBufferModel extends AbstractExperimentBlock implements EventTarget {
	
	protected AbstractLinkDimensioningModel linkDimMod;
	
	public AbstractBufferModel(AbstractLinkDimensioningModel linkDimMod) {
		this.linkDimMod = linkDimMod;
	}

	@Override
	public Map<String, String> getAllParameters() {
		Map<String, String> map = linkDimMod.getAllParameters();
		map.putAll(getImplParameters());
		return map;
	}

	public abstract void build(AbstractPowerSharingModel optPowMod, LinkUtilisationExperiment linkUtilisationExperiment);
	
	public AbstractLinkDimensioningModel getLinkDimensioningModel() {
		return linkDimMod;
	}
	
	public abstract void receiveMessage(Message m, int linkId, double timeNS);
	public abstract Map<String, String> getImplParameters();

	public abstract TimeLine[] getTimeLines();

	public abstract void noMoreMessage();

}
