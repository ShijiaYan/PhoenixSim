package edu.columbia.sebastien.link_util.models.powergenmodel;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;
import edu.columbia.lrl.general.EventTarget;
import edu.columbia.lrl.general.Evt;
import edu.columbia.sebastien.link_util.components.Laser;
import edu.columbia.sebastien.link_util.models.AbstractPowerSharingModel;

public class TDMSharedLasers extends AbstractPowerSharingModel implements EventTarget {
	
	private int linksPerLaser;
	private double tdmSwitchingTimeNS;
	
	// state variable
	private int[] poweredChannelForLaserI;
	private double[] poweredHorizonForLaserI;
	private boolean[] canBeExtended;
	
	public TDMSharedLasers(@ParamName(name="Links sharing a laser")  int linksPerLaser,
						   @ParamName(name="Laser switching time in ns")  double TDMswitchingTimeNS) {
		this.tdmSwitchingTimeNS = TDMswitchingTimeNS;
		this.linksPerLaser = linksPerLaser;
	}
	
	@Override
	public void buildImpl() {
		// try to regroup lasers by power here? For now, just dumb allocation
		int nbLasers =(int)Math.ceil((double)lanesPerLink.length / (double)linksPerLaser);
		lasers = new Laser[nbLasers];
		poweredChannelForLaserI = new int[nbLasers];
		poweredHorizonForLaserI = new double[nbLasers];
		canBeExtended = new boolean[nbLasers];
		boolean timeLine = lue.isWithTimeLine();
		for (int i = 0; i < nbLasers ; i++) {
			lasers[i] = laserTemplate.getCopy(i, nbLasers, timeLine);
			lasers[i].setLinkUtilisationExperiment(lue);
		}
	}

	@Override
	public Map<String, String> getImplParameters() {
		return SimpleMap.getMap("links per laser", linksPerLaser+"", "tdm laser sharing switching time", tdmSwitchingTimeNS+"");
	}
	
	private int getLaserForChannel(int channelIndex) {
		return channelIndex / linksPerLaser;
	}
	
	private static class LocalEvent extends Evt {		
		int laserId;
		int channelDestination;

		public LocalEvent(double deadlineNS, EventTarget dest) {
			super(deadlineNS, dest);
		}		
	}

	@Override
	public double activateChannel(int i, double timeNS, double duration) {
		int laserId = getLaserForChannel(i);
		if (lasers[laserId].getState()) {
			double start;
			if (poweredChannelForLaserI[laserId] == i) {
				start = poweredHorizonForLaserI[laserId];
				canBeExtended[laserId] = true;
			} else {
				start = poweredHorizonForLaserI[laserId] + tdmSwitchingTimeNS;
				// schedule an event to update the power destination
				LocalEvent e = new LocalEvent(start, this);
				e.laserId = laserId;
				e.channelDestination = i;
				canBeExtended[laserId] = false;
				lue.scheduleEvent(e);
			}
			poweredHorizonForLaserI[laserId] = start+duration;
			return start;
		} else {
			double start = lasers[laserId].activate(timeNS, linkPowReqmod.getRequiredOpticalPowerMW(lanesPerLink[i]));
			start += tdmSwitchingTimeNS;
			poweredChannelForLaserI[laserId] = i;
			poweredHorizonForLaserI[laserId] = start + duration;
			canBeExtended[laserId] = true;
			return start;
		}
	}

	@Override
	public void disableChannel(int i, double timeNS) {
		int laserId = getLaserForChannel(i);
		double horizon = poweredHorizonForLaserI[laserId];
		if (horizon == timeNS) {
			lasers[laserId].disable(timeNS);
		} else if (horizon < timeNS) {
			throw new IllegalStateException();
		}
	}
	
	@Override
	public double scheduleAfter(int linkId, double duration) {
		int laserId = getLaserForChannel(linkId);		
		if (canBeExtended[laserId]) {
			double start = poweredHorizonForLaserI[laserId];
			poweredHorizonForLaserI[laserId] += duration;
			return start;
		} else {
			double start = poweredHorizonForLaserI[laserId] + tdmSwitchingTimeNS;
			poweredHorizonForLaserI[laserId] += tdmSwitchingTimeNS + duration;
			return start;
		}
	}	

	@Override
	public boolean isActive(int i) {
		int laserId = getLaserForChannel(i);
		return lasers[laserId].getState() && poweredChannelForLaserI[laserId] == i;
	}

	@Override
	public void processEvent(Evt e) {
		LocalEvent ev = (LocalEvent)e;
		poweredChannelForLaserI[ev.laserId] = ev.channelDestination;
	}

	@Override
	public Object toShortString() {
		return "tdm shared laser pool";
	}
	
	@Override
	public void shutDown(double max) {
		// TODO Auto-generated method stub
		
	}

	
}
