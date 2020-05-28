package edu.columbia.sebastien.link_util.models.powergenmodel;

import java.util.Collection;
import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.graphics.timeline.TimeLine;
import ch.epfl.general_libraries.utils.SimpleMap;
import edu.columbia.sebastien.link_util.components.Laser;
import edu.columbia.sebastien.link_util.components.SOA;
import edu.columbia.sebastien.link_util.models.AbstractPowerSharingModel;

public class SOAreplicatedLaser extends AbstractPowerSharingModel {
	
	private int linksPerLasers;
	private double minimalLaserPowerMW;
	private boolean alwaysOn;
	
	private double[] horizons;
	
	private int[] usagesPerLaser;
	
	public SOAreplicatedLaser(@ParamName(name="Links per laser", default_="5")  int linksPerLasers, 
							  @ParamName(name="Minimal laser power MW", default_="0.5")  double minimalLaserPowerMW, 
							  @ParamName(name="Always on", default_="false")   boolean alwaysOn) {
		this.linksPerLasers = linksPerLasers;
		this.minimalLaserPowerMW = minimalLaserPowerMW;
		this.alwaysOn = alwaysOn;
	}

	@Override
	public void buildImpl() {
		int nbLasers = (int)Math.ceil((double)lanesPerLink.length / (double)linksPerLasers);
		lasers = new Laser[nbLasers];
		usagesPerLaser = new int[nbLasers];
		boolean timeLine = lue.isWithTimeLine();
		for (int i = 0 ; i < lasers.length ; i++) {
			lasers[i] = laserTemplate.getCopy(i, lasers.length, timeLine);
			lasers[i].setLinkUtilisationExperiment(lue);
		}
		soa = new SOA[lanesPerLink.length];
		horizons = new double[lanesPerLink.length];
		for (int i = 0 ; i < soa.length ; i++) {
			soa[i] = soaTemplate.getCopy(i, soa.length, timeLine);
			soa[i].setLinkUtilisationExperiment(lue);
		}
		if (alwaysOn) {
            for (Laser laser : lasers) {
                laser.activate(0, minimalLaserPowerMW);
            }
		}
	}
	
	@Override
	public void shutDown(double max) {
		if (alwaysOn) {
            for (Laser laser : lasers) {
                laser.disable(max);
            }
		}
	}
	
	public Collection<TimeLine> getTimeLines() {
		Collection<TimeLine> list = super.getTimeLines();
		for (SOA s : soa) {
			list.add(s.getTimeLine());
		}
		return list;	
	}	

	@Override
	public Map<String, String> getImplParameters() {
		return SimpleMap.getMap("links per laser", linksPerLasers +"", "laser always on", alwaysOn+"");
	}
	
	private int getLaserIdForChannel(int linkId) {
		return linkId / linksPerLasers;
	}

	@Override
	public double activateChannel(int i, double timeNS, double durationNS) {
		if (soa[i].getState()) 
			throw new IllegalStateException("Cannot activate channel, already active, use schedule after");
		int laserId = getLaserIdForChannel(i);
		double laserReadyTime = 0;
		if (!lasers[laserId].getState()) {
			laserReadyTime = lasers[laserId].activate(timeNS, minimalLaserPowerMW);
		} else {
			laserReadyTime = timeNS;
		}
		double power = linkPowReqmod.getRequiredOpticalPowerMW(lanesPerLink[i]);
		double start = soa[i].activate(laserReadyTime, minimalLaserPowerMW, power);
		usagesPerLaser[laserId]++;
		horizons[i] = start + durationNS;
		return start;
	}

	@Override
	public void disableChannel(int i, double timeNS) {
		int laserId = getLaserIdForChannel(i);		
		if (!soa[i].getState()) throw new IllegalStateException("Cannot disable an inactive channel");
		soa[i].disable(timeNS);
		usagesPerLaser[laserId]--;
		if (usagesPerLaser[laserId] == 0 && !alwaysOn) {
			lasers[laserId].disable(timeNS);
		}
	}

	@Override
	public boolean isActive(int i) {
		return soa[i].getState();
	}

	@Override
	public double scheduleAfter(int linkId, double duration) {
		double start = horizons[linkId];
		horizons[linkId] += duration;
		return start;
	}


}
