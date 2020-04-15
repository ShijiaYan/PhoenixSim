package PhotonicElements.Nonlinearity.RingStructures;

import PhotonicElements.DirectionalCoupler.CompactCoupler;
import PhotonicElements.Nonlinearity.CurvedWaveguide.CurvedWgNL;
import PhotonicElements.Utilities.MathLibraries.Complex;
import PhotonicElements.Utilities.Wavelength;
import PhotonicElements.Waveguides.WaveguideProperties.WgProperties;
import ch.epfl.general_libraries.clazzes.ParamName;

public class RingWgNL {
	
	CurvedWgNL NCwg1 ;
	CompactCoupler DC1 ;
	Wavelength inputLambda ;
	WgProperties wgProp ;
	double radiusMicron, lambdaNm, pin_mW, pin_NCwg ;
	
	public int steps ;
	
	Complex zero = new Complex(0,0) ;
	Complex one = new Complex(1,0) ;
	
	Complex S11, S12 ;
	Complex S21, S22 ;
	//***********************************************************
	public RingWgNL (
			@ParamName(name="Wavelength (nm)") Wavelength inputLambda,
			@ParamName(name="Waveguide Properties") WgProperties wgProp,
			@ParamName(name="Radius (micron)") double radiusMicron,
			@ParamName(name="Coupling Region") CompactCoupler coupler,
			@ParamName(name="Input Power (mW)") double pin_mW
			){
		this.steps = wgProp.getConvergenceSteps() ;
		this.inputLambda = inputLambda ;
		this.DC1 = coupler ;
		this.wgProp = wgProp ;
		this.radiusMicron = radiusMicron ;
		this.lambdaNm = inputLambda.getWavelengthNm() ;
		this.pin_mW = pin_mW ;
		
		Complex[] Sx1 = excitePorts(one.times(Math.sqrt(pin_mW)), zero) ;
		this.S11 = Sx1[0].divides(Math.sqrt(pin_mW)) ; this.S21 = Sx1[1].divides(Math.sqrt(pin_mW)) ;
		
		Complex[] Sx2 = excitePorts(zero, one.times(Math.sqrt(pin_mW))) ;
		this.S12 = Sx2[0].divides(Math.sqrt(pin_mW)) ; this.S22 = Sx2[1].divides(Math.sqrt(pin_mW)) ;
	}
	//***********************************************************
	// For critical coupling ring
	public RingWgNL (
			@ParamName(name="Wavelength (nm)") Wavelength inputLambda,
			@ParamName(name="Waveguide Properties") WgProperties wgProp,
			@ParamName(name="Radius (micron)") double radiusMicron,
			@ParamName(name="Input Power (mW)") double pin_mW
			){
		this.steps = wgProp.getConvergenceSteps() ;
		this.inputLambda = inputLambda ;
		this.pin_mW = pin_mW ;
		double loss = Math.exp(-2*Math.PI*radiusMicron*1e-6*wgProp.getBendLossModel().getLossPerMeter(radiusMicron)) ; // round trip loss
		double inputKappa = Math.sqrt(1-loss) ;
		this.DC1 = new CompactCoupler(inputKappa) ;
		this.wgProp = wgProp ;
		this.radiusMicron = radiusMicron ;
		this.lambdaNm = inputLambda.getWavelengthNm() ;

		Complex[] Sx1 = excitePorts(one.times(Math.sqrt(pin_mW)), zero) ;
		this.S11 = Sx1[0].divides(Math.sqrt(pin_mW)) ; this.S21 = Sx1[1].divides(Math.sqrt(pin_mW)) ;
		
		Complex[] Sx2 = excitePorts(zero, one.times(Math.sqrt(pin_mW))) ;
		this.S12 = Sx2[0].divides(Math.sqrt(pin_mW)) ; this.S22 = Sx2[1].divides(Math.sqrt(pin_mW)) ;
	}
	//***********************************************************
	public CompactCoupler getInputCouplingRegion(){
		return DC1 ;
	}
	
	public double getRadiusMicron(){
		return radiusMicron ;
	}

	public double getWavelengthNm(){
		return lambdaNm ;
	}
	
	public double getPin_mW(){
		return pin_mW ;
	}
	
	public double getPin_mW_NCwg(){
		return pin_NCwg ;
	}
	
	//***********************************************************
	public Complex[] excitePorts(Complex port1_in, Complex port2_in){
		// initialize the ports
		Complex port1_out = zero, port2_out = zero ;
		Complex DC1_port1_out = zero , DC1_port2_out = zero , DC1_port3_out = zero, DC1_port4_out = zero ;
		Complex Cwg1_port1_out = zero , Cwg1_port2_out = zero ;
		// First need to calculate power inside the ring
		for(int i=0; i<steps; i++){
			DC1_port1_out = DC1.getPort1(port1_in, port2_in, Cwg1_port2_out, Cwg1_port1_out) ; // this is output port 1
			DC1_port2_out = DC1.getPort2(port1_in, port2_in, Cwg1_port2_out, Cwg1_port1_out) ; // this is output port 2
			DC1_port3_out = DC1.getPort3(port1_in, port2_in, Cwg1_port2_out, Cwg1_port1_out) ;
			DC1_port4_out = DC1.getPort4(port1_in, port2_in, Cwg1_port2_out, Cwg1_port1_out) ;
			
			pin_NCwg = DC1_port3_out.absSquared() + DC1_port4_out.absSquared() ;
			NCwg1 = new CurvedWgNL(inputLambda, wgProp, radiusMicron, 360, false, null, pin_NCwg) ; // starting with no input power
			
			Cwg1_port1_out = NCwg1.getPort1(DC1_port4_out, DC1_port3_out) ;
			Cwg1_port2_out = NCwg1.getPort2(DC1_port4_out, DC1_port3_out) ;
		}
		
		NCwg1 = new CurvedWgNL(inputLambda, wgProp, radiusMicron, 360, false, null, pin_NCwg) ; // starting with no input power
		// Then need to 
		 port1_out = zero; port2_out = zero ;
		 DC1_port1_out = zero ; DC1_port2_out = zero ; DC1_port3_out = zero; DC1_port4_out = zero ;
		 Cwg1_port1_out = zero ; Cwg1_port2_out = zero ;
		for(int i=0; i<steps; i++){
			DC1_port1_out = DC1.getPort1(port1_in, port2_in, Cwg1_port2_out, Cwg1_port1_out) ; // this is output port 1
			DC1_port2_out = DC1.getPort2(port1_in, port2_in, Cwg1_port2_out, Cwg1_port1_out) ; // this is output port 2
			DC1_port3_out = DC1.getPort3(port1_in, port2_in, Cwg1_port2_out, Cwg1_port1_out) ;
			DC1_port4_out = DC1.getPort4(port1_in, port2_in, Cwg1_port2_out, Cwg1_port1_out) ;

			Cwg1_port1_out = NCwg1.getPort1(DC1_port4_out, DC1_port3_out) ;
			Cwg1_port2_out = NCwg1.getPort2(DC1_port4_out, DC1_port3_out) ;
			
			// now turn the input ports off and let the light propagates
			port1_in = zero ; port2_in = zero ;
			// now sum up the light at each output port
			port1_out = port1_out.plus(DC1_port1_out) ;
			port2_out = port2_out.plus(DC1_port2_out) ;
		}
		
		Complex[] outPorts = {port1_out, port2_out} ;
		return outPorts ;
	}
	
	public Complex getS11(){
		return S11 ;
	}
	
	public Complex getS21(){
		return S21 ;
	}
	
	public Complex getS12(){
		return S12 ;
	}
	
	public Complex getS22(){
		return S22 ;
	}
	
	// Calculating waves in each port
	
	public Complex getPort1(Complex port1In, Complex port2In){
		Complex T1 = port1In.times(S11) ;
		Complex T2 = port2In.times(S12) ;
		return T1.plus(T2) ;
	}
	
	public Complex getPort2(Complex port1In, Complex port2In){
		Complex T1 = port1In.times(S21) ;
		Complex T2 = port2In.times(S22) ;
		return T1.plus(T2) ;
	}
	
}
