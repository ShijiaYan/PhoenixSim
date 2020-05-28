package PhotonicElements.PNJunction;

import PhotonicElements.Utilities.MathLibraries.MoreMath;
import PhotonicElements.Utilities.PhysicalConstants;
import PhotonicElements.Waveguides.WaveguideProperties.WgProperties;
import ch.epfl.general_libraries.clazzes.ParamName;
import flanagan.interpolation.LinearInterpolation;

public class PINdiode {

	// PIN is forward biased
	
	WgProperties wgProp = new WgProperties(10, 1, 1, null) ;
	PhysicalConstants pc = new PhysicalConstants() ;
	double length_um, lifetime_ns, voltage_volt, current_mA ;
	
	public PINdiode(
			@ParamName(name="Length (micron)") double length_um,
			@ParamName(name="Carrier lifetime (ns)", default_="1") double lifetime_ns,
			@ParamName(name="Voltage (volt)") double voltage_volt
			){
		this.length_um = length_um ;
		this.lifetime_ns = lifetime_ns ;
		this.voltage_volt = voltage_volt ;
		this.current_mA = getCurrent_mA_fromVoltage_volt(voltage_volt) ;
	}
	
	public double getVoltage_volt(){
		return voltage_volt ;
	}
	
	public double getCurrent_mA(){
		return current_mA ;
	}
	
	public double getLength_um(){
		return length_um ;
	}
	
	// step 1: need DC I-V and V-I model
	private double getCurrent_mA_fromVoltage_volt(double voltage_volt){
		if(voltage_volt<=getVoltage_volt_fromCurrent_mA(0)){
			return 0 ;
			}
		int m = 10000 ;
		double[] I_mA = MoreMath.linspace(0, 100, m) ;
		double[] V_volt = new double[m] ;
		for(int i=0; i<m; i++){
			V_volt[i] = getVoltage_volt_fromCurrent_mA(I_mA[i]) ;
		}
		LinearInterpolation linCurrent= new LinearInterpolation(V_volt, I_mA) ;
		double current_mA = linCurrent.interpolate(voltage_volt) ;
		return current_mA ;
	}
	
	private double getVoltage_volt_fromCurrent_mA(double I_mA){
		double R_kohm = 0.25 ; // in Kilo Ohms
		double Vbi_volt = 0.7 ; // built-in diode voltage
		double n = 0.62 ; // non-ideality factor of the diode
		double Vthermal_volt = 0.026 ; // Thermal voltage = KT/q
		double Is_mA = 90 * (1e-9/1e-3) ; // 90 nano amperes = 9e-5 mA
		double V_volt = Vbi_volt + R_kohm * I_mA + 1/n * Vthermal_volt * Math.log(I_mA/Is_mA + 1) ;
		return V_volt ;
	}
	
	// step 2: Use the Plasma Dispersion Model (assuming DN = DP) 
	public double getDN(){
		double I_mA = getCurrent_mA_fromVoltage_volt(voltage_volt) ;
		double length_cm = length_um*1e-6/1e-2 ;
		double volume = wgProp.getCrossSectionAreaCmSquare()* length_cm;
		double Q_coulomb = I_mA * 1e-3 * (lifetime_ns * 1e-9);
		double q = PhysicalConstants.getElementaryCharge() ;
		double DN = (Q_coulomb / q) / volume; // charge carrier density 1/cm^3
		return DN ;
	}
	
	public double getDalphaSi(){
		double DN = getDN() ;
		PlasmaDispersionModel plasma = new PlasmaDispersionModel(0, false, 0, false, DN, true) ;
		double DalphaSi = plasma.getDalphadBperCm() ;
		return DalphaSi ;
	}
	
	public double getDnSi(){
		double DN = getDN() ;
		PlasmaDispersionModel plasma = new PlasmaDispersionModel(0, false, 0, false, DN, true) ;
		double DnSi = plasma.getDnSi() ;
		return DnSi ;
	}
	
	// step3: Construct the small-signal RF model of the diode
	
	
}
