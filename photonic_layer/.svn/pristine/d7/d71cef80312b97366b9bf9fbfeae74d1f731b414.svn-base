package People.Natalie.tutorials.devices;

import PhotonicElements.DirectionalCoupler.DistributedCoupler.DistributedCouplerStripWg;
import PhotonicElements.PNJunction.PlasmaDispersionModel;
import PhotonicElements.Utilities.MathLibraries.Complex;
import PhotonicElements.Utilities.Wavelength;
import PhotonicElements.Waveguides.StraightWaveguide.StraightWg;
import PhotonicElements.Waveguides.WaveguideProperties.WgProperties;

public class SwitchEO2x2MZI {

	/**
	 * First we define the basic building blocks the we need to create the device
	 * For a 2x2 MZI switch we need:
	 * 									1) two straight waveguides that make the arms
	 * 									2) two 50/50 couplers that make the input splitting and output combining
	 * 									3) a phase shifter embedded in an arm. Here assumed to be electro-optic phase shifter
	 * 									4) (optional) thermal tuning
	 */
	
	StraightWg wg1 ; // upper arm of Mach-Zehnder
	StraightWg wg2 ; // lower arm of Mach-Zehnder
	DistributedCouplerStripWg DC1 ; // input coupler for power splitting
	DistributedCouplerStripWg DC2 ; // output coupler for power combining
	public double lambdaNm ; // wavelength of operation. could be private and accessed through a getter.
	PlasmaDispersionModel plasmaEffect ; // this is based on the Soref's equations for index change and loss change of silicon
	boolean isCross ; // this is true if switch is in cross state
	boolean isBar ; // this is true if switch is in bar state
	
	/**
	 * in the ideal case, a 50/50 coupler must be used. to find the gap and length of a 50/50 coupler,
	 * a separate simulation must be done.
	 */
	double couplerLengthMicron = 12.3 ; // in order to get 50/50 coupler at 1550nm. Must be found first! 
	double gapNm = 200 ; // we assume that coupling gap in the directional couplers is 200 nm.
	
	/**
	 * The scattering parameters (port-to-port) are calculated by light propagation in the structure.
	 */
	int steps ; // this is steps for wave propagation.
	
	Complex zero = new Complex(0,0) , one = new Complex(1,0) ;
	
	/**
	 * Compact model of the device is created by calculating the port-to-port scattering parameters.
	 * These are complex numbers that determine the amplitude and phase of the light as it goes from one port to another one.
	 */
	Complex S11, S21, S31, S41 ;
	Complex S12, S22, S32, S42 ;
	Complex S13, S23, S33, S43 ;
	Complex S14, S24, S34, S44 ;
	
	// This constructor for the general transmission of Switch structure.
	// it is used to optimize the plasma effect for minimum crosstalk in the switch
	public SwitchEO2x2MZI(
			Wavelength inputLambda,
			WgProperties wgProp,
			double lengthMicron,
			double couplerLossdB,
			PlasmaDispersionModel plasmaEffect
			){
		this.lambdaNm = inputLambda.getWavelengthNm() ;
		this.steps = wgProp.getConvergenceSteps() ;
		this.DC1 = new DistributedCouplerStripWg(inputLambda, couplerLossdB, couplerLengthMicron, gapNm) ;
		this.DC2 =  new DistributedCouplerStripWg(inputLambda, couplerLossdB, couplerLengthMicron, gapNm) ;
		this.plasmaEffect = plasmaEffect ;
		this.wg1 = new StraightWg(inputLambda, wgProp, lengthMicron, false, null, false, null) ; // no PN or thermal heater
		this.wg2 = new StraightWg(inputLambda, wgProp, lengthMicron, true, plasmaEffect, false, null) ; // PN phase shifter, no thermal heater
		
		// Now calculate the scattering parameters
		/**
		 * in order to calculate port-to-port scattering parameters, each port is excited separately and light is propagated 
		 * through the structure.
		 * 
		 * 		Sij defines the scattering parameter at port j when port i is excited.
		 */
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
	// a PN phase shift is required to change the state of switch, hence the plasma effect is automatically configured.
	// it is assumed that the switch is originally in the cross state
	public SwitchEO2x2MZI(
			Wavelength inputLambda,
			WgProperties wgProp,
			double lengthMicron,
			double couplerLossdB,
			boolean crossState,
			boolean barState
			){
		this.lambdaNm = inputLambda.getWavelengthNm() ;
		this.steps = wgProp.getConvergenceSteps() ;
		this.DC1 = new DistributedCouplerStripWg(inputLambda, wgProp, couplerLengthMicron, gapNm) ;
		this.DC2 =  new DistributedCouplerStripWg(inputLambda, wgProp, couplerLengthMicron, gapNm) ;
		this.wg1 = new StraightWg(inputLambda, wgProp, lengthMicron, false, null, false, null) ;
		this.isCross = crossState ;
		this.isBar = barState ;
		// if both bar state and cross state are true, the switch is set to divide the power equally (it becomes a 50/50 power splitter!)
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
			double deltaPhi = -Math.PI/2 ;
			double DnSi = deltaPhi/(2*Math.PI/inputLambda.getWavelengthMeter() * wgProp.getConfinementFactor() * lengthMicron*1e-6) ;
			this.plasmaEffect = new PlasmaDispersionModel(DnSi, true, 0, false, 0, false) ;
			this.wg2 = new StraightWg(inputLambda, wgProp, lengthMicron, true, plasmaEffect, false, null) ; // phase shifter
		}
		
		// Now calculate the scattering parameters
		/**
		 * in order to calculate port-to-port scattering parameters, each port is excited separately and light is propagated 
		 * through the structure.
		 * 
		 * 		Sij defines the scattering parameter at port j when port i is excited.
		 */
		Complex[] Sx1 = excitePorts(one, zero, zero, zero) ; // scattering parameters by exciting port 1
		this.S11 = Sx1[0] ; this.S21 = Sx1[1] ; this.S31 = Sx1[2] ; this.S41 = Sx1[3] ;
		
		Complex[] Sx2 = excitePorts(zero, one, zero, zero) ; // scattering parameters by exciting port 2
		this.S12 = Sx2[0] ; this.S22 = Sx2[1] ; this.S32 = Sx2[2] ; this.S42 = Sx2[3] ;
		
		Complex[] Sx3 = excitePorts(zero, zero, one, zero) ; // scattering parameters by exciting port 3
		this.S13 = Sx3[0] ; this.S23 = Sx3[1] ; this.S33 = Sx3[2] ; this.S43 = Sx3[3] ;
		
		Complex[] Sx4 = excitePorts(zero, zero, zero, one) ; // scattering parameters by exciting port 4
		this.S14 = Sx4[0] ; this.S24 = Sx4[1] ; this.S34 = Sx4[2] ; this.S44 = Sx4[3] ;
		
	}
	
	
	public StraightWg getUpperWg(){
		return wg1 ;
	}
	
	public StraightWg getLowerWg(){
		return wg2 ;
	}
	
	public DistributedCouplerStripWg getInputCoupler(){
		return DC1 ;
	}
	
	public DistributedCouplerStripWg getOutputCoupler(){
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
	/**
	 * this method (excitePorts) is where the connection and propagation of the ports happens.
	 * Please pay very close attention to it. Once you understand how it works, you can use it
	 * to create any structure you want. 
	 * For the 2x2 MZI, we have FOUR ports.
	 * @param port1_in
	 * @param port2_in
	 * @param port3_in
	 * @param port4_in
	 * @return
	 */
	
	private Complex[] excitePorts(Complex port1_in, Complex port2_in, Complex port3_in, Complex port4_in){
		
		// initializing the ports of all basic building blocks (all initial values are zero)
		Complex port1_out = zero, port2_out = zero, port3_out = zero, port4_out = zero ;
		Complex DC1_port1_out = zero, DC1_port2_out = zero, DC1_port3_out = zero, DC1_port4_out = zero ;
		Complex DC2_port1_out = zero, DC2_port2_out = zero, DC2_port3_out = zero, DC2_port4_out = zero ;
		Complex wg1_port1_out = zero, wg1_port2_out = zero, wg2_port1_out = zero, wg2_port2_out = zero ;
		
		// setting up the iteration loop
		
		for(int i=0; i<steps; i++){
			/**
			 * you need to know the convention I used in defining port numbers for basic building blocks.
			 * They are in the slide deck.
			 */
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
			
			// this is an import step. After the first iteration, we need to turn the excitations off 
			// at each port and let the light just propagate in the structure
			port1_in = port2_in = port3_in = port4_in = zero ;
			// at each iteration step, we add the field at the output ports
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
		/**
		 * at the final step, we create the port functions using the calculated scattering parameters.
		 * This will allow us to use this device in a bigger structure.
		 * @param port1In
		 * @param port2In
		 * @param port3In
		 * @param port4In
		 * @return
		 */
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
