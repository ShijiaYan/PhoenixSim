package NASA.Link;

import PhotonicElements.LinkFormat.AbstractLinkFormat;
import ch.epfl.general_libraries.clazzes.ParamName;

/**
 * This is OOK DRIVER for 65nm CMOS technology --> Based on Robert's model
 * @author Meisam
 *
 */

public class Driver {
	
	double voltage, capacitance ;
	AbstractLinkFormat linkFormat ;
	
	public Driver(
			@ParamName(name="Link Format") AbstractLinkFormat linkFormat,
			@ParamName(name="Peak-to-Peak Voltage (V)") double voltage,
			@ParamName(name="Load Capacitance (fF)") double capacitance
			){
		this.linkFormat = linkFormat ;
		this.voltage = voltage ;
		this.capacitance = capacitance ;
	}

	public double getEnergyPJperBit(){
		
		double VDD = 1.2 ; // supply voltage for 65nm CMOS
		double refCapacitance = 50 ; // in femto Farad. This is the modulator capacitance in Robert's paper
		
		double rate = linkFormat.getWavelengthRate() ;
		double driverSlope = 1.4e-23  ; // grows as V^3 , for VDD = 1.2 V (65nm CMOS tech) --> What about 0.5 factor??
		double driverConst = 8.4e-14  ; // grows as V^2 , for VDD = 1.2 V (65nm CMOS tech) --> What about 0.5 factor??
		
		double modifiedDriverSlope = driverSlope * Math.abs(Math.pow(voltage/(2*VDD), 2)) *(capacitance/refCapacitance) ;
		double modifiedDriverConst = driverConst-refCapacitance*1e-15*Math.pow(2*VDD, 2)/4 + capacitance*1e-15 * Math.pow(voltage, 2) * (1/4) ;
		
		double EnergyJperBit =  modifiedDriverSlope * rate + modifiedDriverConst ;
		double EnergyPJperBit = EnergyJperBit * 1e12 ;
		
		return EnergyPJperBit ;
	}

	public double getPowerConsumption() {
		
		double powerMW = getEnergyPJperBit() * linkFormat.getWavelengthRate()/1e9 ; // This is for just one driver 
		return powerMW ;
	}


	
}
