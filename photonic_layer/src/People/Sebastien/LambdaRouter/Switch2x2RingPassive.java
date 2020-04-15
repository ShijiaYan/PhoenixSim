package People.Sebastien.LambdaRouter;

import PhotonicElements.DirectionalCoupler.CompactCoupler;
import PhotonicElements.Junctions.Crossings.SimpleCrossing;
import PhotonicElements.Utilities.MathLibraries.Complex;
import PhotonicElements.Utilities.Wavelength;
import PhotonicElements.Waveguides.CurvedWaveguide.CurvedWg;
import PhotonicElements.Waveguides.StraightWaveguide.StraightWg;
import PhotonicElements.Waveguides.WaveguideProperties.WgProperties;
import PhotonicElements.WavelengthLocking.ResonanceLock;
import ch.epfl.general_libraries.clazzes.ParamName;

public class Switch2x2RingPassive {

	SimpleCrossing X ;
	StraightWg wg1, wg2, wg3, wg4 ; // straight waveguides
	CurvedWg Cwg1, Cwg2, Cwg3, Cwg4 ; // curved waveguides
	CompactCoupler DC1, DC2, DC3, DC4 ; // four compact directional couplers
	
	Wavelength inputLambda ;
	
	int steps ; // this is the propagation steps to get a good convergence
	
	Complex zero = new Complex(0,0), one = new Complex(1,0) ;
	
	Complex S11, S21, S31, S41 ; // exciting port 1
	Complex S12, S22, S32, S42 ; // exciting port 2 
	Complex S13, S23, S33, S43 ; // exciting port 3
	Complex S14, S24, S34, S44 ; // exciting port 4

	
	public Switch2x2RingPassive(
			@ParamName(name="Wavelength (nm)") Wavelength inputLambda,
			@ParamName(name="Waveguide Properties") WgProperties wgProp,
			@ParamName(name="Radius (um)") double radiusMicron,
			@ParamName(name="input Kappa") double inputKappa,
			@ParamName(name="output Kappa") double outputKappa,
			@ParamName(name="Crossing Model") SimpleCrossing crossingModel,
			@ParamName(name="Locked Resonance Lambda (nm)") double lockedLambdaNm
			){
		this.inputLambda = inputLambda ;
		this.steps = wgProp.getConvergenceSteps() ;
		// specifying crossing model
		this.X = crossingModel ;
		// specifying straight waveguides
		this.wg1 = new StraightWg(inputLambda, wgProp, radiusMicron, false, null, false, null) ;
		this.wg2 = new StraightWg(inputLambda, wgProp, radiusMicron, false, null, false, null) ;
		this.wg3 = new StraightWg(inputLambda, wgProp, radiusMicron, false, null, false, null) ;
		this.wg4 = new StraightWg(inputLambda, wgProp, radiusMicron, false, null, false, null) ;
		// specifying curved waveguides
		this.Cwg1 = new CurvedWg(inputLambda, wgProp, radiusMicron, 90, false, null, false, null) ;
		this.Cwg3 = new CurvedWg(inputLambda, wgProp, radiusMicron, 90, false, null, false, null) ;
		ResonanceLock resLock = new ResonanceLock(radiusMicron, lockedLambdaNm) ;
		this.Cwg2 = new CurvedWg(inputLambda, wgProp, radiusMicron, 270, false, null, true, resLock.getLockHeater()) ;
		this.Cwg4 = new CurvedWg(inputLambda, wgProp, radiusMicron, 270, false, null, true, resLock.getLockHeater()) ;
		// specifying coupling regions between rings and waveguides
		this.DC1 = new CompactCoupler(inputKappa) ;
		this.DC2 = new CompactCoupler(outputKappa) ;
		this.DC3 = new CompactCoupler(outputKappa) ;
		this.DC4 = new CompactCoupler(inputKappa) ;
		
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
	
	public double getRadiusMicron(){
		return Cwg1.getRadiusOfCurvatureMicron() ;
	}
	
	public SimpleCrossing getCrossing(){
		return X ;
	}
	
	public CompactCoupler getInputCoupling(){
		return DC1 ;
	}
	
	public CompactCoupler getOutputCoupling(){
		return DC2 ;
	}
	
	public Wavelength getInputLambda(){
		return inputLambda ;
	}
	
	// describe the geometry and physical connections
	private Complex[] excitePorts(Complex port1_in, Complex port2_in, Complex port3_in, Complex port4_in){
		
		Complex port1_out = zero, port2_out = zero, port3_out = zero, port4_out = zero ;
				
		Complex DC1_port1_out= zero, DC1_port2_out= zero, DC1_port3_out= zero, DC1_port4_out= zero   ;
		Complex DC2_port1_out= zero, DC2_port2_out= zero, DC2_port3_out= zero, DC2_port4_out= zero  ;
		Complex DC3_port1_out= zero, DC3_port2_out= zero, DC3_port3_out= zero, DC3_port4_out= zero  ;
		Complex DC4_port1_out= zero, DC4_port2_out= zero, DC4_port3_out= zero, DC4_port4_out = zero ;
		
		Complex wg1_port1_out= zero, wg1_port2_out= zero  ;
		Complex wg2_port1_out= zero, wg2_port2_out= zero  ;
		Complex wg3_port1_out= zero, wg3_port2_out= zero  ;
		Complex wg4_port1_out= zero, wg4_port2_out= zero  ;
		
		Complex Cwg1_port1_out= zero, Cwg1_port2_out = zero ;
		Complex Cwg2_port1_out= zero, Cwg2_port2_out= zero  ;
		Complex Cwg3_port1_out= zero, Cwg3_port2_out= zero  ;
		Complex Cwg4_port1_out= zero, Cwg4_port2_out= zero  ;
		
		Complex X_port1_out= zero, X_port2_out= zero, X_port3_out= zero, X_port4_out= zero ;
		
		// now update the output ports at each step of propagation
		
		for(int i=0; i<steps; i++){
			
			DC1_port1_out = DC1.getPort1(port1_in, wg1_port1_out, Cwg1_port2_out, Cwg2_port1_out) ; // this is output port 1
			DC1_port2_out = DC1.getPort2(port1_in, wg1_port1_out, Cwg1_port2_out, Cwg2_port1_out) ;
			DC1_port3_out = DC1.getPort3(port1_in, wg1_port1_out, Cwg1_port2_out, Cwg2_port1_out) ;
			DC1_port4_out = DC1.getPort4(port1_in, wg1_port1_out, Cwg1_port2_out, Cwg2_port1_out) ;
			
			DC2_port1_out = DC2.getPort1(wg2_port1_out, port2_in, Cwg2_port2_out, Cwg1_port1_out) ;
			DC2_port2_out = DC2.getPort2(wg2_port1_out, port2_in, Cwg2_port2_out, Cwg1_port1_out) ; // this is output port 2
			DC2_port3_out = DC2.getPort3(wg2_port1_out, port2_in, Cwg2_port2_out, Cwg1_port1_out) ;
			DC2_port4_out = DC2.getPort4(wg2_port1_out, port2_in, Cwg2_port2_out, Cwg1_port1_out) ;
			
			wg1_port1_out = wg1.getPort1(DC1_port2_out, X_port1_out) ;
			wg1_port2_out = wg1.getPort2(DC1_port2_out, X_port1_out) ;
			
			wg2_port1_out = wg2.getPort1(DC2_port1_out, X_port2_out) ;
			wg2_port2_out = wg2.getPort2(DC2_port1_out, X_port2_out) ;
			
			Cwg1_port1_out = Cwg1.getPort1(DC2_port4_out, DC1_port3_out) ;
			Cwg1_port2_out = Cwg1.getPort2(DC2_port4_out, DC1_port3_out) ;
			
			Cwg2_port1_out = Cwg2.getPort1(DC1_port4_out, DC2_port3_out) ;
			Cwg2_port2_out = Cwg2.getPort2(DC1_port4_out, DC2_port3_out) ;
			
			X_port1_out = X.getPort1(wg1_port2_out, wg2_port2_out, wg3_port1_out, wg4_port1_out) ;
			X_port2_out = X.getPort2(wg1_port2_out, wg2_port2_out, wg3_port1_out, wg4_port1_out) ;
			X_port3_out = X.getPort3(wg1_port2_out, wg2_port2_out, wg3_port1_out, wg4_port1_out) ;
			X_port4_out = X.getPort4(wg1_port2_out, wg2_port2_out, wg3_port1_out, wg4_port1_out) ;
			
			wg3_port1_out = wg3.getPort1(X_port3_out, DC3_port1_out) ;
			wg3_port2_out = wg3.getPort2(X_port3_out, DC3_port1_out) ;
			
			wg4_port1_out = wg4.getPort1(X_port4_out, DC4_port2_out) ;
			wg4_port2_out = wg4.getPort2(X_port4_out, DC4_port2_out) ;
			
			DC4_port1_out = DC4.getPort1(port4_in, wg4_port2_out, Cwg3_port1_out, Cwg4_port2_out) ; // this is output port 4
			DC4_port2_out = DC4.getPort2(port4_in, wg4_port2_out, Cwg3_port1_out, Cwg4_port2_out) ;
			DC4_port3_out = DC4.getPort3(port4_in, wg4_port2_out, Cwg3_port1_out, Cwg4_port2_out) ;
			DC4_port4_out = DC4.getPort4(port4_in, wg4_port2_out, Cwg3_port1_out, Cwg4_port2_out) ;
			
			DC3_port1_out = DC3.getPort1(wg3_port2_out, port3_in, Cwg4_port1_out, Cwg3_port2_out) ;
			DC3_port2_out = DC3.getPort2(wg3_port2_out, port3_in, Cwg4_port1_out, Cwg3_port2_out) ; // this is output port 3
			DC3_port3_out = DC3.getPort3(wg3_port2_out, port3_in, Cwg4_port1_out, Cwg3_port2_out) ;
			DC3_port4_out = DC3.getPort4(wg3_port2_out, port3_in, Cwg4_port1_out, Cwg3_port2_out) ;
			
			Cwg3_port1_out = Cwg3.getPort1(DC4_port3_out, DC3_port4_out) ;
			Cwg3_port2_out = Cwg3.getPort2(DC4_port3_out, DC3_port4_out) ;
			
			Cwg4_port1_out = Cwg4.getPort1(DC4_port4_out, DC3_port3_out) ;
			Cwg4_port2_out = Cwg4.getPort2(DC4_port4_out, DC3_port3_out) ;
					
			// after the first propagation, must turn all the excitations off
			port1_in = zero ;
			port2_in = zero ;
			port3_in = zero ;
			port4_in = zero ;
			
			// update the field at each output port
			port1_out = port1_out.plus(DC1_port1_out) ;
			port2_out = port2_out.plus(DC2_port2_out) ;
			port3_out = port3_out.plus(DC3_port2_out) ;
			port4_out = port4_out.plus(DC4_port1_out) ;
			
		}
		Complex[] outPorts = {port1_out, port2_out, port3_out, port4_out} ;
		return outPorts ;
		
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
