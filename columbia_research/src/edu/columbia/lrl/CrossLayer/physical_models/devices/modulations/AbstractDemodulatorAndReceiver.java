package edu.columbia.lrl.CrossLayer.physical_models.devices.modulations;

import java.util.ArrayList;
import java.util.Map;
import ch.epfl.general_libraries.utils.MoreArrays;
import edu.columbia.lrl.CrossLayer.PowerConsumption;
import edu.columbia.lrl.CrossLayer.physical_models.PhysicalParameterAndModelsSet;
import edu.columbia.lrl.CrossLayer.physical_models.PowerPenalty;
import edu.columbia.lrl.CrossLayer.physical_models.util.AbstractLinkFormat;


public abstract class AbstractDemodulatorAndReceiver {

	public static final String RECEIVER = "Demodulator/Receiver";

	private double passiveJitterPenalty;

	public AbstractDemodulatorAndReceiver(double passiveJitterPenalty) {
		this.passiveJitterPenalty = passiveJitterPenalty;
	}

	public abstract ArrayList<PowerPenalty> getDemodulationPowerPenalties(PhysicalParameterAndModelsSet modelSet,
			AbstractLinkFormat linkFormat);

	public abstract ArrayList<PowerConsumption> getDemodulationAndReceptionPowerConsumptions(
			double opticalPowerAtReceiverdBm, PhysicalParameterAndModelsSet modelSet, AbstractLinkFormat linkFormat);

	public abstract double getReceiverSensitivity(PhysicalParameterAndModelsSet modeSet, AbstractLinkFormat format);

	public abstract Map<String, String> getAllReceiverParameters();

	public ArrayList<PowerPenalty> getJitterPowerPenalty(PhysicalParameterAndModelsSet modelSet,
			AbstractLinkFormat linkFormat) {
		PowerPenalty jitter = new PowerPenalty(PowerPenalty.JITTER, AbstractDemodulatorAndReceiver.RECEIVER,
				this.passiveJitterPenalty);
		return MoreArrays.getArrayList(jitter);
	}

	public Map<String, String> getAllParameters() {
		Map<String, String> m = getAllReceiverParameters();
		m.put("Jitter penalty", passiveJitterPenalty + "");
		return m;
	}

	public boolean receiverHasThroughCapability() {
		return false;
	}

	public ArrayList<PowerPenalty> getPassbyDemodPowerPenalties(PhysicalParameterAndModelsSet modelSet,
			AbstractLinkFormat linkFormat) {
		throw new IllegalStateException("By default, receiver has no pass through capability");
	}
}
