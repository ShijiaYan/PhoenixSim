package edu.columbia.sebastien.link_util.models.powergenmodel;

import java.util.Map;

import ch.epfl.general_libraries.utils.SimpleMap;
import edu.columbia.sebastien.link_util.components.Laser;
import edu.columbia.sebastien.link_util.models.AbstractPowerSharingModel;

public class OneLaserForEach extends AbstractPowerSharingModel {
	
	private double[] horizon;
	private boolean alwaysOn;
	
	public OneLaserForEach() {}
	
	public OneLaserForEach(boolean alwaysOn) {
		this.alwaysOn = alwaysOn;
	}
	
	@Override
	public Map<String, String> getImplParameters() {
		return SimpleMap.getMap("laser always on", alwaysOn+"");
	}	

	@Override
	public void buildImpl() {	
		int links = lanesPerLink.length;
		lasers = new Laser[links];
		horizon = new double[links];
		boolean timeLine = lue.isWithTimeLine();
		for (int i = 0 ; i < links ; i++) {
			lasers[i] = laserTemplate.getCopy(i, links, timeLine);
			lasers[i].setLinkUtilisationExperiment(lue);
			if (alwaysOn) {
				lasers[i].setActive(true, linkPowReqmod.getRequiredOpticalPowerMW(lanesPerLink[i]));
			}
		}	
	}	

	
	@Override
	public double activateChannel(int i, double timeNS, double duration) {
		if (!lasers[i].getState()) {
			double start = lasers[i].activate(timeNS, linkPowReqmod.getRequiredOpticalPowerMW(lanesPerLink[i]));
			horizon[i] = start + duration;
			return start;
		} else {
			throw new IllegalStateException("Cannot activate an already active, use scheduleAfter()");
			//horizon += duration;
			//	return 0;			
		}
	}
	
	@Override
	public void shutDown(double max) {
		if (alwaysOn) {
			for (Laser l : lasers) {
				l.disable(max);
			}
		}
	}	

	@Override
	public void disableChannel(int i, double timeNS) {
		if (!alwaysOn) {
			if (lasers[i].getState()) {
				lasers[i].disable(timeNS);
			} else {
				throw new IllegalStateException("Cannot disable an inactive channel");
			}
		}
	}

	@Override
	public double scheduleAfter(int linkId, double duration) {
		double start = horizon[linkId];
		horizon[linkId] += duration;
		return start;
		
	}
	
	@Override
	public boolean isActive(int i) {
		//if (alwaysOn) return true;
		return lasers[i].getState();
	}






}
