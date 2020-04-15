package edu.columbia.lrl.CrossLayer.physical_models.devices.modulations.ook_rz;

import java.util.ArrayList;
import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.results.PropertyMap;
import ch.epfl.general_libraries.utils.MoreArrays;
import edu.columbia.lrl.CrossLayer.PowerConsumption;
import edu.columbia.lrl.CrossLayer.physical_models.PhysicalParameterAndModelsSet;
import edu.columbia.lrl.CrossLayer.physical_models.PowerPenalty;
import edu.columbia.lrl.CrossLayer.physical_models.devices.modulations.AbstractDemodulatorAndReceiver;
import edu.columbia.lrl.CrossLayer.physical_models.devices.modulations.AbstractReceiverSensitivityModel;
import edu.columbia.lrl.CrossLayer.physical_models.devices.modulations.ook_rz.sensitivity.Abstract_OOK_RZ_SensitivityModel;
import edu.columbia.lrl.CrossLayer.physical_models.util.AbstractLinkFormat;

public class OOK_RZ_Receiver extends AbstractDemodulatorAndReceiver {

	private AbstractReceiverSensitivityModel sensitivityModel;
	
	private double passiveIL ;
	private double polarizationLoss ;
	private double demodStaticPower ;	
	
	public OOK_RZ_Receiver(Abstract_OOK_RZ_SensitivityModel sensitivityModel,
			@ParamName(name = "Passive insertion loss (dB)", default_ = "0.5") double passiveIL,
			@ParamName(name = "Polarization loss (dB)", default_ = "0.5") double polarizationLoss,
			@ParamName(name = "Jitter Penalty (dB)", default_ = "2") double passiveJitterPenalty,
			@ParamName(name = "Receiver Power @ 10 Gb/s (mW)", default_ = "3") double demodStaticPower) {
		super(passiveJitterPenalty);
		this.passiveIL = passiveIL ;
		this.polarizationLoss = polarizationLoss ;
		this.demodStaticPower = demodStaticPower ;	
		this.sensitivityModel = sensitivityModel;		
	}
	
	@Override
	public double getReceiverSensitivity(PhysicalParameterAndModelsSet modeSet,
			AbstractLinkFormat format) {
		return sensitivityModel.getSensitivitydB(modeSet.getConstants(), format.getWavelengthRate());
	}		

	@Override
	public ArrayList<PowerPenalty> getDemodulationPowerPenalties(PhysicalParameterAndModelsSet modelSet,
			AbstractLinkFormat linkFormat) {
		PowerPenalty pola = new PowerPenalty("Polarization loss", AbstractDemodulatorAndReceiver.RECEIVER, polarizationLoss);
		PowerPenalty paIL = new PowerPenalty(PowerPenalty.PASSIVEINSERTIONLOSS, AbstractDemodulatorAndReceiver.RECEIVER, passiveIL);
			
		return MoreArrays.getArrayList(pola, paIL);
	}

	@Override
	public ArrayList<PowerConsumption> getDemodulationAndReceptionPowerConsumptions(
			double opticalPowerAtReceiverdBm,
			PhysicalParameterAndModelsSet modelSet,
			AbstractLinkFormat linkFormat) {
		PowerConsumption pc = new PowerConsumption("Receiver", false, false, true, demodStaticPower);
		return MoreArrays.getArrayList(pc);
	}

	@Override
	public Map<String, String> getAllReceiverParameters() {
		PropertyMap m = new PropertyMap();
		m.put("Polarization loss", polarizationLoss);
		m.put("Receiver power mW", demodStaticPower);
		m.put("Passive IL", passiveIL);
		return m;
	}	

}
