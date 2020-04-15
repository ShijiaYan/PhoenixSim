package edu.columbia.sebastien.link_util.components;

import java.awt.Color;
import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.AbstractExperimentBlock;
import ch.epfl.general_libraries.graphics.timeline.TimeLine;
import ch.epfl.general_libraries.utils.StringFormatter;
import edu.columbia.lrl.general.EventTarget;
import edu.columbia.lrl.general.Evt;
import edu.columbia.sebastien.link_util.LinkUtilisationExperiment;


public class Laser extends AbstractExperimentBlock implements LinkUtilisationObject<Laser>, EventTarget {
	
	private LinkUtilisationExperiment lue;
	private AbstractLaserConsumptionModel laserMod;
	private TimeLine timeLine;
	private double actualLasingPowerMW;	
	private int index;
	private int maxLaserIndex;
	private double activationTime;
	private boolean useTimeLine;
	
	private STATE state;
//	private double lastActivationTime;
	private double nextLaserReadyTime;
	private double lastStartLasingTime;	
	private double lastDisablingTime = Double.NEGATIVE_INFINITY;
	
	private static enum STATE {
		ON, OFF, HEATING, COOLING
	}
	
	public Laser(@ParamName(name="Activation time in ns", default_="1000000")  double activationTime, AbstractLaserConsumptionModel laserMod) {
		this.activationTime = activationTime;	
		this.laserMod = laserMod;
	}
	
	private Laser(double activationTime, AbstractLaserConsumptionModel laserMod, int index, int maxLaserIndex, boolean useTimeLine) {
		this.activationTime = activationTime;
		this.index = index;
		this.maxLaserIndex = maxLaserIndex;
		this.laserMod = laserMod;
		if (useTimeLine)
			timeLine = new TimeLine(index, "Lasers", index+"");
		state = STATE.OFF;
		this.useTimeLine = useTimeLine;
		
	}
	

	@Override
	public Map<String, String> getAllParameters() {
		Map<String, String> m = laserMod.getAllParameters();
		m.put("laser activation time", activationTime+"");
		m.put("laser consumption model", laserMod.getClass().getSimpleName());
		return m;
	}	

	@Override
	public void setLinkUtilisationExperiment(LinkUtilisationExperiment lue) {
		this.lue = lue;	
	}

	@Override
	public Laser getCopy(int index, int maxLaserIndex, boolean useTimeLine) {
		return new Laser(activationTime, laserMod, index, maxLaserIndex, useTimeLine);
	}
	
	public double activate(double timeNS, double powerMW) {
		if (state.equals(STATE.ON)) throw new IllegalStateException("Laser is already on");
		if (state.equals(STATE.HEATING)) {
			if (powerMW != actualLasingPowerMW) throw new IllegalStateException("Cannot change laser power");
			return nextLaserReadyTime;
		}
		state = STATE.HEATING;
	//	lastActivationTime = timeNS;
		actualLasingPowerMW = powerMW;
		if (timeNS > lastDisablingTime + activationTime) {		// activation time here act as desactivation time
			nextLaserReadyTime = timeNS + activationTime;
			if (useTimeLine)
				timeLine.addJobPhase(timeNS, nextLaserReadyTime, "heating", Color.RED);
			Evt e = new Evt(nextLaserReadyTime, null, this, 0);
			lue.scheduleEvent(e);
			double joules = laserMod.getJoules(nextLaserReadyTime - timeNS, actualLasingPowerMW);	
			String laserId = "laser " + StringFormatter.zeroPadding(index, maxLaserIndex);
			lue.accountForLaserOn(joules, nextLaserReadyTime - timeNS, 0, laserId);			
			return nextLaserReadyTime;
		} else {
			nextLaserReadyTime = timeNS + timeNS - lastDisablingTime; // spent is the time spent since the last desactivation, and its basically the time that has to be
														// spent again to reactivate it
			Evt e = new Evt(nextLaserReadyTime, null, this, 0);
			lue.scheduleEvent(e);	
			double joules = laserMod.getJoules(nextLaserReadyTime - timeNS, actualLasingPowerMW);	
			String laserId = "laser " + StringFormatter.zeroPadding(index, maxLaserIndex);
			lue.accountForLaserOn(joules, nextLaserReadyTime - timeNS, 0, laserId);			
			if (useTimeLine)
				timeLine.addJobPhase(timeNS, nextLaserReadyTime, "heating", Color.RED);
			return nextLaserReadyTime;
		}
	}
	
	public double changePower(double timeNS, double powerMW) {
		if (!state.equals(STATE.ON)) throw new IllegalStateException("Laser is off, cannot be changed");
		disable(timeNS);
		return activate(timeNS, powerMW);
	}
	
	public void disable(double timeNS) {
		if (!state.equals(STATE.ON)) throw new IllegalStateException("Laser is already off");
		state = STATE.OFF;
		lastDisablingTime = timeNS;
		if (useTimeLine)
			timeLine.addJobPhase(lastStartLasingTime, timeNS, "lasing\n " + actualLasingPowerMW + "mW", Color.GREEN);
		
		double joules = laserMod.getJoules(timeNS - lastStartLasingTime, actualLasingPowerMW);
		String laserId = "laser " + StringFormatter.zeroPadding(index, maxLaserIndex);
		lue.accountForLaserOn(joules, timeNS - lastStartLasingTime, timeNS - lastStartLasingTime, laserId);
	}
	
	public boolean getState() {
		return (state.equals(STATE.ON) || state.equals(STATE.HEATING));
	}

	public TimeLine getTimeLine() {
		return timeLine;
	}

	@Override
	public void processEvent(Evt e) {
		switch (e.getType()) {
		case 0:
			// has been switched on
			state = STATE.ON;
			lastStartLasingTime = e.getTimeNS();
		}
		
	}

	@Override
	public Object toShortString() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setActive(boolean b, double pow) {
		state = STATE.ON;
		lastStartLasingTime = 0;
		actualLasingPowerMW = pow;
	}
}
