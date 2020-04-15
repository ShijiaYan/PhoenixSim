package edu.columbia.lrl.CrossLayer.physical_models.devices.mux;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ch.epfl.general_libraries.utils.MoreArrays;
import ch.epfl.general_libraries.utils.SimpleMap;
import ch.epfl.general_libraries.clazzes.ParamName;
import edu.columbia.lrl.CrossLayer.PowerConsumption;
import edu.columbia.lrl.CrossLayer.physical_models.PhysicalParameterAndModelsSet;
import edu.columbia.lrl.CrossLayer.physical_models.PowerPenalty;
import edu.columbia.lrl.CrossLayer.physical_models.util.AbstractLinkFormat;

public class AwgMux extends AbstractMux {

	// parameters
	double passiveIL ;
	double polarizationLoss ;
	double awgStaticPower ;
	


	public AwgMux(
			@ParamName(name = "Passive insertion loss (dB)", default_ = "1") double passiveIL,
			@ParamName(name = "Polarization loss (dB)", default_ = "0.5") double polarizationLoss,
			@ParamName(name = "AWG Thermal Tunning Power (mW)", default_ = "0") double awgStaticPower) {
		this.passiveIL = passiveIL ;
		this.polarizationLoss = polarizationLoss ;
		
	}

	@Override
	public Map<String, String> getAllParameters() {
		Map<String, String> map = new SimpleMap<String, String>();
		map.put("Passive insertion loss (dB)", passiveIL + "");
		map.put("Polarization Loss (dB)", polarizationLoss + "");
		map.put("AWG Thermal Tunning Power", awgStaticPower + "");
		return map;
	}

//	@Override
	public ArrayList<PowerPenalty> getMuxPowerPenalties(PhysicalParameterAndModelsSet modelSet, AbstractLinkFormat linkFormat) {
		
		double totalMuxLoss = passiveIL + polarizationLoss ;
		PowerPenalty AwgTotalLoss = new PowerPenalty("AWG Penalty", MUX, totalMuxLoss) ;
		
		return MoreArrays.getArrayList(AwgTotalLoss) ;
		
	}
	@Override
	public double getMuxChannelPenalty() {
		return 0;}
	// Overriding the abstract power consumption in the super class
	@Override
	public List<PowerConsumption> getDevicePowerConsumptions(
			PhysicalParameterAndModelsSet modelSet, AbstractLinkFormat linkFormat) {
		ArrayList<PowerConsumption> pc = new ArrayList<PowerConsumption>(1);
		if (awgStaticPower != 0) {
			throw new IllegalStateException("Not implemented yet");	
		} else {
			return pc;
		}
	}

	@Override
	public double getMuxTotalPenalty() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMuxChannel() {
		// TODO Auto-generated method stub
		return 0;
	}	

}
