package edu.columbia.lrl.CrossLayer.physical_models.devices.FP;

import ch.epfl.general_libraries.clazzes.ParamName;

public class AwgSwitch {

	// parameters
	private double awgStaticPower ;
	private AwgDemux demuxModel ;
	private AwgMux muxModel ;

	public AwgSwitch(
			@ParamName(name = "AWG MUX model", defaultClass_ = AwgMux.class) AwgMux muxModel,
			@ParamName(name = "AWG DeMux model", defaultClass_ = AwgDemux.class) AwgDemux demuxModel,
			@ParamName(name = "AWG Thermal Tunning Power (mW)", default_ = "0") double awgStaticPower) {
		this.muxModel = muxModel ;
		this.demuxModel = demuxModel ;
		this.awgStaticPower = awgStaticPower ;
		
	}



	public double getPowerPenalties(Constants ct, AbstractMZM modModel, int NumberOfChannels) {
		
		double muxPP = muxModel.getPowerPenalties(NumberOfChannels) ;
		double demuxPP = demuxModel.getPowerPenalties(ct, modModel.getEffectiveModulationRate(), NumberOfChannels, null) ;
		double totalSwitchLoss = muxPP + demuxPP ;
		
		return totalSwitchLoss ;
		
	}
	
	// Power Consumption?


}
