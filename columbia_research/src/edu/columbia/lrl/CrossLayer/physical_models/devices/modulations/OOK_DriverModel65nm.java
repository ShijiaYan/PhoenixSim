package edu.columbia.lrl.CrossLayer.physical_models.devices.modulations;

import java.util.Map;

import ch.epfl.general_libraries.utils.SimpleMap;
import edu.columbia.lrl.CrossLayer.physical_models.devices.rings.AbstractRingPNJunctionDriverPowerModel;
import edu.columbia.lrl.CrossLayer.physical_models.util.AbstractLinkFormat;

public class OOK_DriverModel65nm extends AbstractRingPNJunctionDriverPowerModel {
	

	public double getEnergyPJperBit(double voltage, double capacitance, AbstractLinkFormat linkFormat){
		
		double VDD = 1.2 ; // supply voltage for 65nm CMOS
		double refCapacitance = 50 ; // in femto Farad. This is the modulator capacitance in Robert's paper
		
		double rate = linkFormat.getWavelengthRate() ;
		double driverSlope = 1.4e-23 * 0.5 ; // grows as V^3 , for VDD = 1.2 V (65nm CMOS tech)
		double driverConst = 8.4e-14 * 0.5 ; // grows as V^2 , for VDD = 1.2 V (65nm CMOS tech)
		
		double modifiedDriverSlope = driverSlope * Math.abs(Math.pow(voltage/VDD, 1)) *(capacitance/refCapacitance) ;
		double modifiedDriverConst = driverConst * Math.pow(voltage/VDD, 2) *(capacitance/refCapacitance) ;
		
		double EnergyJperBit =  modifiedDriverSlope * rate + modifiedDriverConst ;
		double EnergyPJperBit = EnergyJperBit * 1e12 ;
		
		return EnergyPJperBit ;
	}

/*	public double getPowerConsumptionMW(AbstractLinkFormat linkFormat, double supplyVoltage, double modulatorDriveVoltage) {
		
		double powerMW = getEnergyPJperBit(linkFormat, supplyVoltage, modulatorDriveVoltage) * linkFormat.getWavelengthRate()/1e9 ; // This is for just one driver 
		
		return powerMW ;
	}
*/
	@Override
	public double getAverageConsumption(double voltage, double capacitance, AbstractLinkFormat linkFormat) {
		
		double powerMW = getEnergyPJperBit(voltage, capacitance, linkFormat) * linkFormat.getWavelengthRate()/1e9 ; // This is for just one driver 
		return powerMW ;
	}

	@Override
	public Map<String, String> getAllParameters() {
		Map<String, String> map = new SimpleMap<String, String>();
		return map;
	}

	
	
}
