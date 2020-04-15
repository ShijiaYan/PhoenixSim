package PhotonicElements.Switches.Switch2x2;

import PhotonicElements.DirectionalCoupler.DistributedCoupler.DistributedCouplerStripWg;
import PhotonicElements.PNJunction.PlasmaDispersionModel;
import PhotonicElements.Utilities.MathLibraries.Complex;
import PhotonicElements.Utilities.Wavelength;
import PhotonicElements.Waveguides.StraightWaveguide.StraightWg;
import PhotonicElements.Waveguides.WaveguideProperties.WgProperties;
import ch.epfl.general_libraries.clazzes.ParamName;

public class Switch2x2MZIDistributedCoupler {

	public Wavelength inputLambda ;
	public WgProperties wgProp ;
	public StraightWg wg1, wg2 ;
	public DistributedCouplerStripWg DC1, DC2 ;
	public PlasmaDispersionModel plasmaEffect ;
	boolean isCross ;
	boolean isBar ;

	public double couplerLengthMicron = 12.3 ; // in order to get 50/50 coupler at 1550nm for 450x220nm waveguide
	public double gapNm = 200 ;
	public double armLengthMicron ;

	int steps = 5 ;

	Complex zero = Complex.ZERO, one = Complex.ONE ;

	public Complex port1, port2, port3, port4 ;
	Complex port1_accumulated, port2_accumulated, port3_accumulated, port4_accumulated ;

	public Complex S11, S21, S31, S41 ;
	public Complex S12, S22, S32, S42 ;
	public Complex S13, S23, S33, S43 ;
	public Complex S14, S24, S34, S44 ;

	public Switch2x2MZIDistributedCoupler(
			Wavelength inputLambda,
			WgProperties wgProp,
			@ParamName(name="Length of arm (um)") double lengthMicron,
			@ParamName(name="coupler length (um)") double couplerLengthMicron,
			@ParamName(name="coupler gap (nm)") double couplerGapNm,
			@ParamName(name="coupler loss (dB)")double couplerLossdB,
			PlasmaDispersionModel plasmaEffect
			){
		this.armLengthMicron = lengthMicron ;
		this.couplerLengthMicron = couplerLengthMicron ;
		this.gapNm = couplerGapNm ;
		this.inputLambda = inputLambda ;
		this.wgProp = wgProp ;
		this.DC1 = new DistributedCouplerStripWg(inputLambda, couplerLossdB, couplerLengthMicron, couplerGapNm) ;
		this.DC2 =  new DistributedCouplerStripWg(inputLambda, couplerLossdB, couplerLengthMicron, couplerGapNm) ;
		this.plasmaEffect = plasmaEffect ;
		this.wg1 = new StraightWg(inputLambda, wgProp, lengthMicron, false, null, false, null) ; // no PN or thermal heater
		this.wg2 = new StraightWg(inputLambda, wgProp, lengthMicron, true, plasmaEffect, false, null) ; // PN phase shifter, no thermal heater

		// Now calculate the scattering parameters
		initializePorts();
		calculateScattParams();
	}

//	public Switch2x2MZIDistributedCoupler(
//			Wavelength inputLambda,
//			WgProperties wgProp,
//			@ParamName(name="Length of arm (um)") double lengthMicron,
//			@ParamName(name="coupler length (um)") double couplerLengthMicron,
//			@ParamName(name="coupler gap (nm)") double couplerGapNm,
//			@ParamName(name="coupler loss (dB)") double couplerLossdB,
//			@ParamName(name="is CROSS?") boolean crossState,
//			@ParamName(name="is BAR?") boolean barState
//			){
//		this.armLengthMicron = lengthMicron ;
//		this.couplerLengthMicron = couplerLengthMicron ;
//		this.gapNm = couplerGapNm ;
//		this.inputLambda = inputLambda ;
//		this.wgProp = wgProp ;
//		this.DC1 = new DistributedCouplerStripWg(inputLambda, couplerLossdB, couplerLengthMicron, couplerGapNm) ;
//		this.DC2 =  new DistributedCouplerStripWg(inputLambda, couplerLossdB, couplerLengthMicron, couplerGapNm) ;
//		this.wg1 = new StraightWg(inputLambda, wgProp, lengthMicron, false, null, false, null) ;
//		this.isCross = crossState ;
//		this.isBar = barState ;
//		if(crossState && !barState){
//			double deltaPhi = 0 ;
//			double DnSi = deltaPhi/(2*Math.PI/inputLambda.getWavelengthMeter() * wgProp.getConfinementFactor() * lengthMicron*1e-6) ;
//			this.plasmaEffect = new PlasmaDispersionModel(DnSi, true, 0, false, 0, false) ;
//			this.wg2 = new StraightWg(inputLambda, wgProp, lengthMicron, true, plasmaEffect, false, null) ; // phase shifter
//		}
//		else if(barState && !crossState){
//			double deltaPhi = -Math.PI ;
//			double DnSi = deltaPhi/(2*Math.PI/inputLambda.getWavelengthMeter() * wgProp.getConfinementFactor() * lengthMicron*1e-6) ;
//			this.plasmaEffect = new PlasmaDispersionModel(DnSi, true, 0, false, 0, false) ;
//			this.wg2 = new StraightWg(inputLambda, wgProp, lengthMicron, true, plasmaEffect, false, null) ; // phase shifter
//		}
//		else{
//			this.plasmaEffect = new PlasmaDispersionModel(0, true, 0, false, 0, false) ;
//			this.wg2 = new StraightWg(inputLambda, wgProp, lengthMicron, true, plasmaEffect, false, null) ; // phase shifter
//		}
//
//		// Now calculate the scattering parameters
//		initializePorts();
//		calculateScattParams();
//	}

	public Switch2x2MZIDistributedCoupler(
			Wavelength inputLambda,
			WgProperties wgProp,
			@ParamName(name="Length of arm (um)") double lengthMicron,
			@ParamName(name="coupler length (um)") double couplerLengthMicron,
			@ParamName(name="coupler gap (nm)") double couplerGapNm,
			@ParamName(name="coupler loss (dB)") double couplerLossdB,
			@ParamName(name="state (true = CROSS, false = BAR)") boolean isCross,
			@ParamName(name="Delta Alpha (1/cm)") double DalphaPerCm
			){
		this.armLengthMicron = lengthMicron ;
		this.couplerLengthMicron = couplerLengthMicron ;
		this.gapNm = couplerGapNm ;
		this.inputLambda = inputLambda ;
		this.wgProp = wgProp ;
		this.DC1 = new DistributedCouplerStripWg(inputLambda, couplerLossdB, couplerLengthMicron, couplerGapNm) ;
		this.DC2 =  new DistributedCouplerStripWg(inputLambda, couplerLossdB, couplerLengthMicron, couplerGapNm) ;
		this.wg1 = new StraightWg(inputLambda, wgProp, lengthMicron, false, null, false, null) ;
		this.isCross = isCross ;
		this.isBar = !isCross ;
		if(isCross){
			this.plasmaEffect = new PlasmaDispersionModel(0, false, 0, true, 0, false) ;
			this.wg2 = new StraightWg(inputLambda, wgProp, lengthMicron, true, plasmaEffect, false, null) ; // phase shifter
		}
		else {
			this.plasmaEffect = new PlasmaDispersionModel(0, false, DalphaPerCm, true, 0, false) ;
			this.wg2 = new StraightWg(inputLambda, wgProp, lengthMicron, true, plasmaEffect, false, null) ; // phase shifter
		}

		// Now calculate the scattering parameters
		initializePorts();
		calculateScattParams();
	}

	public void initializePorts(){
		port1 = port2 = port3 = port4 = Complex.ZERO ;
		port1_accumulated = port2_accumulated = port3_accumulated = port4_accumulated = Complex.ZERO ;
	}

	public void calculateScattParams(){
		Complex[] Sx1 = excitePorts(one, zero, zero, zero) ;
		this.S11 = Sx1[0] ; this.S21 = Sx1[1] ; this.S31 = Sx1[2] ; this.S41 = Sx1[3] ;

		Complex[] Sx2 = excitePorts(zero, one, zero, zero) ;
		this.S12 = Sx2[0] ; this.S22 = Sx2[1] ; this.S32 = Sx2[2] ; this.S42 = Sx2[3] ;

		Complex[] Sx3 = excitePorts(zero, zero, one, zero) ;
		this.S13 = Sx3[0] ; this.S23 = Sx3[1] ; this.S33 = Sx3[2] ; this.S43 = Sx3[3] ;

		Complex[] Sx4 = excitePorts(zero, zero, zero, one) ;
		this.S14 = Sx4[0] ; this.S24 = Sx4[1] ; this.S34 = Sx4[2] ; this.S44 = Sx4[3] ;
	}

	public void connectPorts(Complex port1In, Complex port2In, Complex port3In, Complex port4In){
		port1 = getPort1(port1In, port2In, port3In, port4In) ;
		port2 = getPort2(port1In, port2In, port3In, port4In) ;
		port3 = getPort3(port1In, port2In, port3In, port4In) ;
		port4 = getPort4(port1In, port2In, port3In, port4In) ;
		port1_accumulated = port1_accumulated.plus(port1) ;
		port2_accumulated = port2_accumulated.plus(port2) ;
		port3_accumulated = port3_accumulated.plus(port3) ;
		port4_accumulated = port4_accumulated.plus(port4) ;
	}

	public String getStateOfSwitch(){
		if(isBar && !isCross){return "B" ;}
		else{return "C";}
	}

	public Complex[] excitePorts(Complex port1In, Complex port2In, Complex port3In, Complex port4In){
		DC1.initializePorts();
		DC2.initializePorts();
		wg1.initializePorts();
		wg2.initializePorts();
		for(int i=0; i<steps; i++){
			DC1.connectPorts(port1In, wg2.port1, wg1.port1, port4In);
			DC2.connectPorts(wg2.port2, port2In, port3In, wg1.port2);
			wg1.connectPorts(DC1.port3, DC2.port4);
			wg2.connectPorts(DC1.port2, DC2.port1);
			port1In = port2In = port3In = port4In = Complex.ZERO ;
		}
		Complex[] outPorts = {DC1.getPort1(), DC2.getPort2(), DC2.getPort3(), DC1.getPort4()} ;
		return outPorts ;
	}

	public Complex getPort1(){
		return port1_accumulated ;
	}

	public Complex getPort2(){
		return port2_accumulated ;
	}

	public Complex getPort3(){
		return port3_accumulated ;
	}

	public Complex getPort4(){
		return port4_accumulated ;
	}

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
