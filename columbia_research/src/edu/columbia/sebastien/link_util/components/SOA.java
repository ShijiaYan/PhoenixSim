package edu.columbia.sebastien.link_util.components;

import java.awt.Color;
import java.util.Map;

import edu.columbia.sebastien.link_util.LinkUtilisationExperiment;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.AbstractExperimentBlock;
import ch.epfl.general_libraries.graphics.timeline.TimeLine;
import ch.epfl.general_libraries.utils.StringFormatter;

public class SOA extends AbstractExperimentBlock implements LinkUtilisationObject<SOA> {
	
	private final static Color SOA_STAB = Color.RED.brighter();
	private final static Color SOA_ON = Color.GREEN;
	
	private LinkUtilisationExperiment lue;
	
	private double activationTime;
	private AbstractSOAConsumptionModel soaModel;
	private int maxIndex;
	private int index;
	private boolean useTimeLine;
	
	private boolean soaOn;
	private double lastActivationTime;
	private double actualInputPowerMW;
	private double actualOutputLinePowerMW;
	private double lastStartAmplifyingTime;
	private double lastDisablingTime = Double.NEGATIVE_INFINITY;
	
	private TimeLine timeLine;
	
	public SOA(@ParamName(name="Activation time in ns", default_="10000")  double activationTime, AbstractSOAConsumptionModel soaModel) {
		this.activationTime = activationTime;
		this.soaModel = soaModel;
	}
	
	private SOA(double activationTime, AbstractSOAConsumptionModel soaModel, int index, int maxIndex, boolean useTimeLine) {
		this.activationTime = activationTime;
		this.index = index;
		this.maxIndex = maxIndex;
		this.soaModel = soaModel;
		if (useTimeLine)
			timeLine = new TimeLine(index, "SOAs", index+"", Color.ORANGE);
		this.useTimeLine = useTimeLine;
	}

	@Override
	public Map<String, String> getAllParameters() {
		Map<String, String> m = soaModel.getAllParameters();
		m.put("soa activation time", activationTime+"");
		m.put("soa consumption model", soaModel.getClass().getSimpleName());
		return m;
	}

	@Override
	public void setLinkUtilisationExperiment(LinkUtilisationExperiment lue) {
		this.lue = lue;
	}
	
	public boolean getState() {
		return soaOn;
	}
	
	public TimeLine getTimeLine() {
		return timeLine;
	}	

	@Override
	public SOA getCopy(int index, int maxIndex, boolean line) {
		return new SOA(activationTime, soaModel, index, maxIndex,line);
	}
	
	public double activate(double timeNS, double inputPowerMW, double desiredOutputPowerMW) {
		if (soaOn) throw new IllegalStateException("soa is already on");
		soaOn = true;
		lastActivationTime = timeNS;
		actualOutputLinePowerMW = desiredOutputPowerMW;
		if (timeNS > lastDisablingTime + activationTime) {		// activation time here act as desactivation time
			if (useTimeLine)
				timeLine.addJobPhase(timeNS, timeNS + activationTime, "soa\n stab", SOA_STAB);
			lastStartAmplifyingTime = timeNS + activationTime;
			return timeNS + activationTime;
		} else {
			double spent = timeNS - lastDisablingTime; // spent is the time spent since the last desactivation, and its basically the time that has to be
														// spent again to reactivate it
			lastStartAmplifyingTime = timeNS + spent;
			if (useTimeLine)
				timeLine.addJobPhase(timeNS, timeNS + spent, "soa\n stab", SOA_STAB);
			return timeNS + spent;
		}
	}
	
	public double changePower(double timeNS, double inputPowerMW, double desiredOutputPowerMW) {
		if (!soaOn) throw new IllegalStateException("soa is off, cannot be changed");
		disable(timeNS);
		return activate(timeNS, inputPowerMW, desiredOutputPowerMW);
	}
	
	public void disable(double timeNS) {
		if (!soaOn) throw new IllegalStateException("SOA is already off");
		soaOn = false;
		lastDisablingTime = timeNS;
		if (useTimeLine)
			timeLine.addJobPhase(lastStartAmplifyingTime, timeNS, "outputing\n " + actualOutputLinePowerMW + "mW", SOA_ON);
		
		double joules = soaModel.getJoules(timeNS + activationTime - lastActivationTime, actualInputPowerMW, actualOutputLinePowerMW);
		String soaId = "SOA " + StringFormatter.zeroPadding(index, maxIndex);
		lue.accountForSOAOn(joules, timeNS - lastActivationTime, timeNS - lastStartAmplifyingTime, soaId);
	}	
	
	

}
