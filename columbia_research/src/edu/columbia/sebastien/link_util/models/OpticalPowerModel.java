package edu.columbia.sebastien.link_util.models;

import java.util.Collection;
import java.util.Map;

import ch.epfl.general_libraries.experiment_aut.AbstractExperimentBlock;
import ch.epfl.general_libraries.graphics.timeline.TimeLine;
import edu.columbia.sebastien.link_util.LinkUtilisationExperiment;
import edu.columbia.sebastien.link_util.components.Laser;
import edu.columbia.sebastien.link_util.components.SOA;

public class OpticalPowerModel extends AbstractExperimentBlock {
	
	private Laser laserTemplate;
	private SOA soaTemplate;
	private AbstractLinkPowerRequirementModel linkPowReqmod;
	AbstractPowerSharingModel sharingModel;
	
	public OpticalPowerModel(AbstractLinkPowerRequirementModel linkPowReqmod, Laser laserTemplate, SOA soaTemplate,
			AbstractPowerSharingModel sharingModel) {
		this.laserTemplate = laserTemplate;
		this.soaTemplate = soaTemplate;
		this.linkPowReqmod = linkPowReqmod;
		this.sharingModel = sharingModel;
	}
	
	public void build(int[] lanesPerlink, LinkUtilisationExperiment lue) {
		sharingModel.build(lanesPerlink, lue, laserTemplate, soaTemplate, linkPowReqmod);
	}

	@Override
	public Map<String, String> getAllParameters() {
		
		Map<String, String> m = laserTemplate.getAllParameters();
		m.putAll(soaTemplate.getAllParameters());
		m.putAll(linkPowReqmod.getAllParameters());
		m.putAll(sharingModel.getAllParameters());
		m.put("sharing model", sharingModel.getClass().getSimpleName());
		m.put("link power model", linkPowReqmod.getClass().getSimpleName());
		return m;
	}
	
	public AbstractPowerSharingModel getPowerSharingModel() {
		return sharingModel;
	}

	public Collection<TimeLine> getTimeLines() {
		return sharingModel.getTimeLines();
	}

}
