package People.Qixiang.Benes.MZI2x2;

import PhotonicElements.DirectionalCoupler.CompactCoupler;
import PhotonicElements.PNJunction.PlasmaDispersionModel;
import PhotonicElements.Utilities.MathLibraries.Complex;
import PhotonicElements.Utilities.Wavelength;
import PhotonicElements.Waveguides.StraightWaveguide.StraightWg;
import PhotonicElements.Waveguides.WaveguideProperties.WgProperties;
import ch.epfl.general_libraries.clazzes.ParamName;

public class Switch2x2MZI_qixiang {

	StraightWg wg1, wg2 ;
	CompactCoupler DC1, DC2 ;
//	DistributedCouplerStripWg DC1, DC2 ;
	public double lambdaNm ;
	PlasmaDispersionModel plasmaEffect ;
	boolean isCross ;
	boolean isBar ;
	// for extra length of directional couplers
	double lengthDC_um = 200 ;
	StraightWg wgDC ;
	
	int steps = 2 ; // This is not a resonant device, so a few steps are enough to get a convergence.
	Complex zero = new Complex(0,0) , one = new Complex(1,0) ;
	Complex S11, S21, S31, S41 ;
	Complex S12, S22, S32, S42 ;
	Complex S13, S23, S33, S43 ;
	Complex S14, S24, S34, S44 ;
	
	// This constructor for the general transmission of Switch structure
	public Switch2x2MZI_qixiang(
			Wavelength inputLambda,
			WgProperties wgProp,
			@ParamName(name="Length of arm (um)", default_="300") double lengthMicron,
			CompactCoupler DC1,
			CompactCoupler DC2,
			PlasmaDispersionModel plasmaEffect
			){
//		this.steps = wgProp.getConvergenceSteps() ;
		this.lambdaNm = inputLambda.getWavelengthNm() ;
		this.DC1 = DC1 ;
		this.DC2 =  DC2 ;
		this.plasmaEffect = plasmaEffect ;
		this.wg1 = new StraightWg(inputLambda, wgProp, lengthMicron, false, null, false, null) ; // no PN or thermal heater
		this.wg2 = new StraightWg(inputLambda, wgProp, lengthMicron, true, plasmaEffect, false, null) ; // PH phase shifter, no thermal heater
		this.wgDC = new StraightWg(inputLambda, wgProp, lengthDC_um, false, null, false, null) ;
		
		// Now calculate the scattering parameters
		Complex[] Sx1 = excitePorts(one, zero, zero, zero) ;
		this.S11 = Sx1[0] ; this.S21 = Sx1[1] ; this.S31 = Sx1[2] ; this.S41 = Sx1[3] ;
		
		Complex[] Sx2 = excitePorts(zero, one, zero, zero) ;
		this.S12 = Sx2[0] ; this.S22 = Sx2[1] ; this.S32 = Sx2[2] ; this.S42 = Sx2[3] ;
		
		Complex[] Sx3 = excitePorts(zero, zero, one, zero) ;
		this.S13 = Sx3[0] ; this.S23 = Sx3[1] ; this.S33 = Sx3[2] ; this.S43 = Sx3[3] ;
		
		Complex[] Sx4 = excitePorts(zero, zero, zero, one) ;
		this.S14 = Sx4[0] ; this.S24 = Sx4[1] ; this.S34 = Sx4[2] ; this.S44 = Sx4[3] ;
	}
	
	// This constructor for specific BAR and CROSS states
	public Switch2x2MZI_qixiang(
			Wavelength inputLambda,
			WgProperties wgProp,
			@ParamName(name="Length of arm (um)", default_="300") double lengthMicron,
			CompactCoupler DC1,
			CompactCoupler DC2,
			@ParamName(name="is CROSS state?") boolean crossState,
			@ParamName(name="is BAR state?") boolean barState
			){
//		this.steps = wgProp.getConvergenceSteps() ;
		this.lambdaNm = inputLambda.getWavelengthNm() ;
		this.DC1 = DC1 ;
		this.DC2 = DC2 ;
		this.wg1 = new StraightWg(inputLambda, wgProp, lengthMicron, false, null, false, null) ;
		this.wgDC = new StraightWg(inputLambda, wgProp, lengthDC_um, false, null, false, null) ;
		this.isCross = crossState ;
		this.isBar = barState ;
		if(crossState && !barState){
			double deltaPhi = 0 ;
			double DnSi = deltaPhi/(2*Math.PI/inputLambda.getWavelengthMeter() * wgProp.getConfinementFactor() * lengthMicron*1e-6) ;
			this.plasmaEffect = new PlasmaDispersionModel(DnSi, true, 0, false, 0, false) ;
			this.wg2 = new StraightWg(inputLambda, wgProp, lengthMicron, true, plasmaEffect, false, null) ; // phase shifter
		}
		else if(barState && !crossState){
			double deltaPhi = -Math.PI ;
			double DnSi = deltaPhi/(2*Math.PI/inputLambda.getWavelengthMeter() * wgProp.getConfinementFactor() * lengthMicron*1e-6) ;
			this.plasmaEffect = new PlasmaDispersionModel(DnSi, true, 0, false, 0, false) ;
			this.wg2 = new StraightWg(inputLambda, wgProp, lengthMicron, true, plasmaEffect, false, null) ; // phase shifter
		}
		else{
			this.plasmaEffect = new PlasmaDispersionModel(0, true, 0, false, 0, false) ;
			this.wg2 = new StraightWg(inputLambda, wgProp, lengthMicron, true, plasmaEffect, false, null) ; // phase shifter
		}
		
		// Now calculate the scattering parameters
		Complex[] Sx1 = excitePorts(one, zero, zero, zero) ;
		this.S11 = Sx1[0] ; this.S21 = Sx1[1] ; this.S31 = Sx1[2] ; this.S41 = Sx1[3] ;
		
		Complex[] Sx2 = excitePorts(zero, one, zero, zero) ;
		this.S12 = Sx2[0] ; this.S22 = Sx2[1] ; this.S32 = Sx2[2] ; this.S42 = Sx2[3] ;
		
		Complex[] Sx3 = excitePorts(zero, zero, one, zero) ;
		this.S13 = Sx3[0] ; this.S23 = Sx3[1] ; this.S33 = Sx3[2] ; this.S43 = Sx3[3] ;
		
		Complex[] Sx4 = excitePorts(zero, zero, zero, one) ;
		this.S14 = Sx4[0] ; this.S24 = Sx4[1] ; this.S34 = Sx4[2] ; this.S44 = Sx4[3] ;
		
	}
	
	
	public StraightWg getUpperWg(){
		return wg1 ;
	}
	
	public StraightWg getLowerWg(){
		return wg2 ;
	}
	
	public CompactCoupler getInputCoupler(){
		return DC1 ;
	}
	
	public CompactCoupler getOutputCoupler(){
		return DC2 ;
	}
	
	public PlasmaDispersionModel getPlasmaEffect(){
		return wg2.getPlasmaEffect() ;
	}
	
	public String getStateOfSwitch(){
		if(isBar && !isCross){return "B" ;}
		else{return "C";}
	}
	
	// Calculating Scattering Parameters for the 2x2 Switch
	
	private Complex[] excitePorts(Complex port1_in, Complex port2_in, Complex port3_in, Complex port4_in){
		Complex port1_out = zero, port2_out = zero, port3_out = zero, port4_out = zero ;
		Complex DC1_port1_out = zero, DC1_port2_out = zero, DC1_port3_out = zero, DC1_port4_out = zero ;
		Complex DC2_port1_out = zero, DC2_port2_out = zero, DC2_port3_out = zero, DC2_port4_out = zero ;
		Complex wg1_port1_out = zero, wg1_port2_out = zero, wg2_port1_out = zero, wg2_port2_out = zero ;
		
		for(int i=0; i<steps; i++){
			DC1_port1_out = DC1.getPort1(port1_in, wg2_port1_out, wg1_port1_out, port4_in) ;
			DC1_port2_out = DC1.getPort2(port1_in, wg2_port1_out, wg1_port1_out, port4_in) ;
			DC1_port3_out = DC1.getPort3(port1_in, wg2_port1_out, wg1_port1_out, port4_in) ;
			DC1_port4_out = DC1.getPort4(port1_in, wg2_port1_out, wg1_port1_out, port4_in) ;
			
			DC2_port1_out = DC2.getPort1(wg2_port2_out, port2_in, port3_in, wg1_port2_out) ;
			DC2_port2_out = DC2.getPort2(wg2_port2_out, port2_in, port3_in, wg1_port2_out) ;
			DC2_port3_out = DC2.getPort3(wg2_port2_out, port2_in, port3_in, wg1_port2_out) ;
			DC2_port4_out = DC2.getPort4(wg2_port2_out, port2_in, port3_in, wg1_port2_out) ;
			
			wg1_port1_out = wg1.getPort1(DC1_port3_out, DC2_port4_out) ;
			wg1_port2_out = wg1.getPort2(DC1_port3_out, DC2_port4_out) ;
			
			wg2_port1_out = wg2.getPort1(DC1_port2_out, DC2_port1_out) ;
			wg2_port2_out = wg2.getPort2(DC1_port2_out, DC2_port1_out) ;
			
			port1_in = port2_in = port3_in = port4_in = zero ;
			port1_out = port1_out.plus(DC1_port1_out) ;
			port2_out = port2_out.plus(DC2_port2_out) ;
			port3_out = port3_out.plus(DC2_port3_out) ;
			port4_out = port4_out.plus(DC1_port4_out) ;
		}
		Complex[] outPorts = {port1_out, port2_out, port3_out, port4_out} ;
		return outPorts ;
	}
	
	// port 1
		public Complex getS11(){
			return S11 ;
		}
		
		public Complex getS21(){
			return S21.times(wgDC.getS21()) ;
		}
		
		public Complex getS31(){
			return S31.times(wgDC.getS21()) ;
		}
		
		public Complex getS41(){
			return S41 ;
		}
		// port 2
		public Complex getS12(){
			return S12.times(wgDC.getS21()) ;
		}
		
		public Complex getS22(){
			return S22 ;
		}
		
		public Complex getS32(){
			return S32 ;
		}
		
		public Complex getS42(){
			return S42.times(wgDC.getS21()) ;
		}
		// port 3
		public Complex getS13(){
			return S13.times(wgDC.getS21()) ;
		}
		
		public Complex getS23(){
			return S23 ;
		}
		
		public Complex getS33(){
			return S33 ;
		}
		
		public Complex getS43(){
			return S43.times(wgDC.getS21()) ;
		}
		// port 4
		public Complex getS14(){
			return S14 ;
		}
		
		public Complex getS24(){
			return S24.times(wgDC.getS21()) ;
		}
		
		public Complex getS34(){
			return S34.times(wgDC.getS21()) ;
		}
		
		public Complex getS44(){
			return S44 ;
		}
		
		// define ports based on scattering parameters
		public Complex getPort1(Complex port1In, Complex port2In, Complex port3In, Complex port4In){
			Complex T1 = port1In.times(getS11()) ;
			Complex T2 = port2In.times(getS12()) ;
			Complex T3 = port3In.times(getS13()) ;
			Complex T4 = port4In.times(getS14()) ;
			return T1.plus(T2).plus(T3).plus(T4) ;
		}
		
		public Complex getPort2(Complex port1In, Complex port2In, Complex port3In, Complex port4In){
			Complex T1 = port1In.times(getS21()) ;
			Complex T2 = port2In.times(getS22()) ;
			Complex T3 = port3In.times(getS23()) ;
			Complex T4 = port4In.times(getS24()) ;
			return T1.plus(T2).plus(T3).plus(T4) ;
		}
		
		public Complex getPort3(Complex port1In, Complex port2In, Complex port3In, Complex port4In){
			Complex T1 = port1In.times(getS31()) ;
			Complex T2 = port2In.times(getS32()) ;
			Complex T3 = port3In.times(getS33()) ;
			Complex T4 = port4In.times(getS34()) ;
			return T1.plus(T2).plus(T3).plus(T4) ;
		}
		
		public Complex getPort4(Complex port1In, Complex port2In, Complex port3In, Complex port4In){
			Complex T1 = port1In.times(getS41()) ;
			Complex T2 = port2In.times(getS42()) ;
			Complex T3 = port3In.times(getS43()) ;
			Complex T4 = port4In.times(getS44()) ;
			return T1.plus(T2).plus(T3).plus(T4) ;
		}
		
}
