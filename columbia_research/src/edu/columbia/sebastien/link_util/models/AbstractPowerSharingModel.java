package edu.columbia.sebastien.link_util.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import edu.columbia.sebastien.link_util.LinkUtilisationExperiment;
import edu.columbia.sebastien.link_util.components.Laser;
import edu.columbia.sebastien.link_util.components.SOA;
import ch.epfl.general_libraries.experiment_aut.AbstractExperimentBlock;
import ch.epfl.general_libraries.graphics.timeline.TimeLine;

public abstract class AbstractPowerSharingModel extends AbstractExperimentBlock {
	
	protected Laser[] lasers;	
	protected SOA[] soa;
	
	protected LinkUtilisationExperiment lue;
	protected int[] lanesPerLink;
	
	protected AbstractLinkPowerRequirementModel linkPowReqmod;
	protected Laser laserTemplate;
	protected SOA soaTemplate;
	
	

	@Override
	public Map<String, String> getAllParameters() {
		Map<String, String> m = getImplParameters();
		m.putAll(linkPowReqmod.getAllParameters());
		m.putAll(laserTemplate.getAllParameters());
		m.putAll(soaTemplate.getAllParameters());
		return m;
	}
	
	public Collection<TimeLine> getTimeLines() {
		ArrayList<TimeLine> list = new ArrayList<>();
		for (Laser l : lasers) {
			list.add(l.getTimeLine());
		}
		return list;	
	}	
	
	public  void build(int[] lanesPerLink, 
			           LinkUtilisationExperiment lue, 
			           Laser laserTemplate, 
			           SOA soaTemplate, 
			           AbstractLinkPowerRequirementModel linkPowReqmod) {
		this.lue = lue;
		this.lanesPerLink = lanesPerLink;
		this.laserTemplate = laserTemplate;
		this.soaTemplate = soaTemplate;
		this.linkPowReqmod = linkPowReqmod;
		buildImpl();
	}
	

	public abstract void buildImpl();
	
	public abstract Map<String, String> getImplParameters();
	
	/**
	 * Ask the power generation system to active link i.
	 * @param i
	 * @return The delay for stabilizing channel i in nanoseconds
	 */
	public abstract double activateChannel(int i, double timeNS, double durationNS);
	
	public abstract void disableChannel(int i, double timeNS);
	
	public abstract boolean isActive(int i);

	/**
	 * 
	 * @param linkId
	 * @param duration
	 * @return The packet start time
	 */
	public abstract double scheduleAfter(int linkId, double duration);

	public abstract void shutDown(double max);
	

}
