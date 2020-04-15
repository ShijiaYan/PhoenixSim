package PhotonicElements.Nonlinearity.RingStructures;

import PhotonicElements.DirectionalCoupler.CompactCoupler;
import PhotonicElements.Heater.SimpleHeater;
import PhotonicElements.Nonlinearity.CurvedWaveguide.CurvedWgNL;
import PhotonicElements.Utilities.MathLibraries.Complex;
import PhotonicElements.Utilities.Wavelength;
import PhotonicElements.Waveguides.WaveguideProperties.WgProperties;
import PhotonicElements.WavelengthLocking.ResonanceLock;
import ch.epfl.general_libraries.clazzes.ParamName;

public class AddDropFirstOrderNL {
	
	CurvedWgNL NCwg1, NCwg2 ;
	CompactCoupler DC1, DC2 ;
	
	Wavelength inputLambda ;
	WgProperties wgProp;
	boolean includeHeater ;
	SimpleHeater H ;
	
	Complex one = new Complex(1,0) ; Complex zero = new Complex(0,0) ;
	
	Complex S11, S12, S13, S14 ;
	Complex S21, S22, S23, S24 ;
	Complex S31, S32, S33, S34 ;
	Complex S41, S42, S43, S44 ;
	
	double radiusMicron, lambdaNm ;
	public double pin_mW, pin_Cwg1, pin_Cwg2 ;
	double dropAngleDegree ;
	
	public int steps ; // update steps for propagating the light through the structure
	
	public AddDropFirstOrderNL (
			@ParamName(name="Wavelength (nm)") Wavelength inputLambda,
			@ParamName(name="Waveguide Properties") WgProperties wgProp,
			@ParamName(name="Radius (micron)") double radiusMicron,
			@ParamName(name="Angle of drop port (degree)") double anglePositionOfDropPortDegree,
			@ParamName(name="Input Kappa") double inputKappa,
			@ParamName(name="Output Kappa") double outputKappa,
			@ParamName(name="Includes Heater?") boolean includeHeater,
			@ParamName(name="Heater") SimpleHeater H,
			@ParamName(name="Input Power (mW)") double pin_mW
			){
		this.wgProp = wgProp ;
		this.steps = wgProp.getConvergenceSteps() ;
		this.radiusMicron = radiusMicron ;
		this.inputLambda = inputLambda ;
		this.includeHeater = includeHeater ;
		this.H = H ;
		this.dropAngleDegree = anglePositionOfDropPortDegree ;
		this.lambdaNm = inputLambda.getWavelengthNm() ;
		this.pin_mW = pin_mW ;
		this.DC1 = new CompactCoupler(inputKappa) ;
		this.DC2 = new CompactCoupler(outputKappa) ;
		
		// Now calculate the scattering parameters
		Complex[] Sx1 = excitePorts(one.times(Math.sqrt(pin_mW)), zero, zero, zero) ;
		this.S11 = Sx1[0].divides(Math.sqrt(pin_mW)) ; this.S21 = Sx1[1].divides(Math.sqrt(pin_mW)) ; this.S31 = Sx1[2].divides(Math.sqrt(pin_mW)) ; this.S41 = Sx1[3].divides(Math.sqrt(pin_mW)) ;
		
		Complex[] Sx2 = excitePorts(zero, one.times(Math.sqrt(pin_mW)), zero, zero) ;
		this.S12 = Sx2[0].divides(Math.sqrt(pin_mW)) ; this.S22 = Sx2[1].divides(Math.sqrt(pin_mW)) ; this.S32 = Sx2[2].divides(Math.sqrt(pin_mW)) ; this.S42 = Sx2[3].divides(Math.sqrt(pin_mW)) ;
		
		Complex[] Sx3 = excitePorts(zero, zero, one.times(Math.sqrt(pin_mW)), zero) ;
		this.S13 = Sx3[0].divides(Math.sqrt(pin_mW)) ; this.S23 = Sx3[1].divides(Math.sqrt(pin_mW)) ; this.S33 = Sx3[2].divides(Math.sqrt(pin_mW)) ; this.S43 = Sx3[3].divides(Math.sqrt(pin_mW)) ;
		
		Complex[] Sx4 = excitePorts(zero, zero, zero, one.times(Math.sqrt(pin_mW))) ;
		this.S14 = Sx4[0].divides(Math.sqrt(pin_mW)) ; this.S24 = Sx4[1].divides(Math.sqrt(pin_mW)) ; this.S34 = Sx4[2].divides(Math.sqrt(pin_mW)) ; this.S44 = Sx4[3].divides(Math.sqrt(pin_mW)) ;
	}
	
	// This constructor for locking the initial resonance to a specific value
	public AddDropFirstOrderNL (
			@ParamName(name="Wavelength (nm)") Wavelength inputLambda,
			@ParamName(name="Waveguide Properties") WgProperties wgProp,
			@ParamName(name="Radius (micron)") double radiusMicron,
			@ParamName(name="Angle of drop port (degree)") double anglePositionOfDropPortDegree,
			@ParamName(name="Input Kappa") double inputKappa,
			@ParamName(name="Output Kappa") double outputKappa,
			@ParamName(name="is resonance Locked?") boolean isLocked,
			@ParamName(name="Locked resonance (nm)") double lockedLambdaNm,
			@ParamName(name="Input Power (mW)") double pin_mW
			){
		this.wgProp = wgProp ;
		this.steps = wgProp.getConvergenceSteps() ;
		this.radiusMicron = radiusMicron ;
		this.inputLambda = inputLambda ;
		if(isLocked){
			includeHeater = true ;
			ResonanceLock resLock = new ResonanceLock(radiusMicron, lockedLambdaNm) ;
			H = resLock.getLockHeater() ;
		}
		else{
			includeHeater = false ;
		}
		this.dropAngleDegree = anglePositionOfDropPortDegree ;
		this.lambdaNm = inputLambda.getWavelengthNm() ;
		this.pin_mW = pin_mW ;
		this.DC1 = new CompactCoupler(inputKappa) ;
		this.DC2 = new CompactCoupler(outputKappa) ;
		
		// Now calculate the scattering parameters
		Complex[] Sx1 = excitePorts(one.times(Math.sqrt(pin_mW)), zero, zero, zero) ;
		this.S11 = Sx1[0].divides(Math.sqrt(pin_mW)) ; this.S21 = Sx1[1].divides(Math.sqrt(pin_mW)) ; this.S31 = Sx1[2].divides(Math.sqrt(pin_mW)) ; this.S41 = Sx1[3].divides(Math.sqrt(pin_mW)) ;
		
		Complex[] Sx2 = excitePorts(zero, one.times(Math.sqrt(pin_mW)), zero, zero) ;
		this.S12 = Sx2[0].divides(Math.sqrt(pin_mW)) ; this.S22 = Sx2[1].divides(Math.sqrt(pin_mW)) ; this.S32 = Sx2[2].divides(Math.sqrt(pin_mW)) ; this.S42 = Sx2[3].divides(Math.sqrt(pin_mW)) ;
		
		Complex[] Sx3 = excitePorts(zero, zero, one.times(Math.sqrt(pin_mW)), zero) ;
		this.S13 = Sx3[0].divides(Math.sqrt(pin_mW)) ; this.S23 = Sx3[1].divides(Math.sqrt(pin_mW)) ; this.S33 = Sx3[2].divides(Math.sqrt(pin_mW)) ; this.S43 = Sx3[3].divides(Math.sqrt(pin_mW)) ;
		
		Complex[] Sx4 = excitePorts(zero, zero, zero, one.times(Math.sqrt(pin_mW))) ;
		this.S14 = Sx4[0].divides(Math.sqrt(pin_mW)) ; this.S24 = Sx4[1].divides(Math.sqrt(pin_mW)) ; this.S34 = Sx4[2].divides(Math.sqrt(pin_mW)) ; this.S44 = Sx4[3].divides(Math.sqrt(pin_mW)) ;
	}
	
	//*******************************************************************
	public CompactCoupler getInputCoupling(){
		return DC1 ;
	}
	
	public CompactCoupler getOutCoupling(){
		return DC2 ;
	}
	
	public double getRadiusMicron(){
		return radiusMicron ;
	}
	
	public double getWavelengthNm(){
		return lambdaNm ;
	}

	// Now set the excitation and propagate the wave
	public Complex[] excitePorts(Complex port1_in, Complex port2_in, Complex port3_in, Complex port4_in){
		
		Complex port1_out = zero, port2_out = zero , port3_out = zero , port4_out = zero ;
		
		Complex DC1_port1_out = zero, DC1_port2_out = zero, DC1_port3_out = zero, DC1_port4_out = zero ;
		Complex DC2_port1_out = zero, DC2_port2_out = zero, DC2_port3_out = zero, DC2_port4_out = zero ;
		Complex Cwg1_port1_out = zero , Cwg1_port2_out = zero ;
		Complex Cwg2_port1_out = zero , Cwg2_port2_out = zero ;
		
		// First I need to iterate to find the power that goes into Cwg1 and Cwg2 
		for(int i=0; i<steps; i++){
			DC1_port1_out = DC1.getPort1(port1_in, port2_in, Cwg1_port2_out, Cwg2_port1_out) ; // this is output port 1
			DC1_port2_out = DC1.getPort2(port1_in, port2_in, Cwg1_port2_out, Cwg2_port1_out) ; // this is output port 2
			DC1_port3_out = DC1.getPort3(port1_in, port2_in, Cwg1_port2_out, Cwg2_port1_out) ;
			DC1_port4_out = DC1.getPort4(port1_in, port2_in, Cwg1_port2_out, Cwg2_port1_out) ;
			
			DC2_port1_out = DC2.getPort1(port4_in, port3_in, Cwg1_port1_out, Cwg2_port2_out) ; // this is output port 4
			DC2_port2_out = DC2.getPort2(port4_in, port3_in, Cwg1_port1_out, Cwg2_port2_out) ; // this is output port 3
			DC2_port3_out = DC2.getPort3(port4_in, port3_in, Cwg1_port1_out, Cwg2_port2_out) ; 
			DC2_port4_out = DC2.getPort4(port4_in, port3_in, Cwg1_port1_out, Cwg2_port2_out) ; 
			// update the power inside each curved waveguide
			pin_Cwg1 = DC1_port3_out.absSquared() + DC2_port3_out.absSquared() ;
			pin_Cwg2 = DC1_port4_out.absSquared() + DC2_port4_out.absSquared() ;
			// now update the curved waveguides based on the power inside them
			NCwg1 = new CurvedWgNL(inputLambda, wgProp, radiusMicron, dropAngleDegree, includeHeater, H, pin_Cwg1) ; // assume only one curved Wg has the heater
			NCwg2 = new CurvedWgNL(inputLambda, wgProp, radiusMicron, 360-dropAngleDegree, false, null, pin_Cwg2) ; 
			
			Cwg1_port1_out = NCwg1.getPort1(DC2_port3_out, DC1_port3_out) ;
			Cwg1_port2_out = NCwg1.getPort2(DC2_port3_out, DC1_port3_out) ;
			
			Cwg2_port1_out = NCwg2.getPort1(DC1_port4_out, DC2_port4_out) ;
			Cwg2_port2_out = NCwg2.getPort2(DC1_port4_out, DC2_port4_out) ;
		}
		
		NCwg1 = new CurvedWgNL(inputLambda, wgProp, radiusMicron, dropAngleDegree, includeHeater, H, pin_Cwg1) ; // assume only one curved Wg has the heater
		NCwg2 = new CurvedWgNL(inputLambda, wgProp, radiusMicron, 360-dropAngleDegree, false, null, pin_Cwg2) ;
		
		DC1_port1_out = zero; DC1_port2_out = zero; DC1_port3_out = zero; DC1_port4_out = zero ;
		DC2_port1_out = zero; DC2_port2_out = zero; DC2_port3_out = zero; DC2_port4_out = zero ;
		Cwg1_port1_out = zero ; Cwg1_port2_out = zero ;
		Cwg2_port1_out = zero ; Cwg2_port2_out = zero ;
		// now do the iteration for all the output ports
		for(int i=0; i<steps; i++){
			DC1_port1_out = DC1.getPort1(port1_in, port2_in, Cwg1_port2_out, Cwg2_port1_out) ; // this is output port 1
			DC1_port2_out = DC1.getPort2(port1_in, port2_in, Cwg1_port2_out, Cwg2_port1_out) ; // this is output port 2
			DC1_port3_out = DC1.getPort3(port1_in, port2_in, Cwg1_port2_out, Cwg2_port1_out) ;
			DC1_port4_out = DC1.getPort4(port1_in, port2_in, Cwg1_port2_out, Cwg2_port1_out) ;
			
			DC2_port1_out = DC2.getPort1(port4_in, port3_in, Cwg1_port1_out, Cwg2_port2_out) ; // this is output port 4
			DC2_port2_out = DC2.getPort2(port4_in, port3_in, Cwg1_port1_out, Cwg2_port2_out) ; // this is output port 3
			DC2_port3_out = DC2.getPort3(port4_in, port3_in, Cwg1_port1_out, Cwg2_port2_out) ; 
			DC2_port4_out = DC2.getPort4(port4_in, port3_in, Cwg1_port1_out, Cwg2_port2_out) ; 

			Cwg1_port1_out = NCwg1.getPort1(DC2_port3_out, DC1_port3_out) ;
			Cwg1_port2_out = NCwg1.getPort2(DC2_port3_out, DC1_port3_out) ;
			
			Cwg2_port1_out = NCwg2.getPort1(DC1_port4_out, DC2_port4_out) ;
			Cwg2_port2_out = NCwg2.getPort2(DC1_port4_out, DC2_port4_out) ;
			
			// Now update the output ports
			port1_out = port1_out.plus(DC1_port1_out) ;
			port2_out = port2_out.plus(DC1_port2_out) ;
			port3_out = port3_out.plus(DC2_port2_out) ;
			port4_out = port4_out.plus(DC2_port1_out) ;
			
			// Set the excitation to zero after first propagation
			port1_in = zero ;
			port2_in = zero ;
			port3_in = zero ;
			port4_in = zero ;
		}
		
		Complex[] outputPorts = {port1_out, port2_out, port3_out, port4_out} ; // define the output port array
		return outputPorts ;	
	}
	
	// port 1
	public Complex getS11(){
		return S11 ;
	}
	
	public Complex getS21(){
		return S21 ;
	}
	
	public Complex getS31(){
		return S31 ;
	}
	
	public Complex getS41(){
		return S41 ;
	}
	// port 2
	public Complex getS12(){
		return S12 ;
	}
	
	public Complex getS22(){
		return S22 ;
	}
	
	public Complex getS32(){
		return S32 ;
	}
	
	public Complex getS42(){
		return S42 ;
	}
	// port 3
	public Complex getS13(){
		return S13 ;
	}
	
	public Complex getS23(){
		return S23 ;
	}
	
	public Complex getS33(){
		return S33 ;
	}
	
	public Complex getS43(){
		return S43 ;
	}
	// port 4
	public Complex getS14(){
		return S14 ;
	}
	
	public Complex getS24(){
		return S24 ;
	}
	
	public Complex getS34(){
		return S34 ;
	}
	
	public Complex getS44(){
		return S44 ;
	}
	
	// define ports based on scattering parameters
	public Complex getPort1(Complex port1In, Complex port2In, Complex port3In, Complex port4In){
		Complex T1 = port1In.times(S11) ;
		Complex T2 = port2In.times(S12) ;
		Complex T3 = port3In.times(S13) ;
		Complex T4 = port4In.times(S14) ;
		return T1.plus(T2).plus(T3).plus(T4) ;
	}
	
	public Complex getPort2(Complex port1In, Complex port2In, Complex port3In, Complex port4In){
		Complex T1 = port1In.times(S21) ;
		Complex T2 = port2In.times(S22) ;
		Complex T3 = port3In.times(S23) ;
		Complex T4 = port4In.times(S24) ;
		return T1.plus(T2).plus(T3).plus(T4) ;
	}
	
	public Complex getPort3(Complex port1In, Complex port2In, Complex port3In, Complex port4In){
		Complex T1 = port1In.times(S31) ;
		Complex T2 = port2In.times(S32) ;
		Complex T3 = port3In.times(S33) ;
		Complex T4 = port4In.times(S34) ;
		return T1.plus(T2).plus(T3).plus(T4) ;
	}
	
	public Complex getPort4(Complex port1In, Complex port2In, Complex port3In, Complex port4In){
		Complex T1 = port1In.times(S41) ;
		Complex T2 = port2In.times(S42) ;
		Complex T3 = port3In.times(S43) ;
		Complex T4 = port4In.times(S44) ;
		return T1.plus(T2).plus(T3).plus(T4) ;
	}

	
	
}
