package PhotonicElements.Switches.Switch1x2;

import PhotonicElements.DirectionalCoupler.CompactCoupler;
import PhotonicElements.Junctions.Crossings.SimpleCrossing;
import PhotonicElements.PNJunction.PlasmaDispersionModel;
import PhotonicElements.Utilities.MathLibraries.Complex;
import PhotonicElements.Utilities.Wavelength;
import PhotonicElements.Waveguides.CurvedWaveguide.CurvedWg;
import PhotonicElements.Waveguides.StraightWaveguide.StraightWg;
import PhotonicElements.Waveguides.WaveguideProperties.WgProperties;
import PhotonicElements.WavelengthLocking.ResonanceLock;
import ch.epfl.general_libraries.clazzes.ParamName;

public class Switch1x2Ring {

	public Wavelength inputLambda ;
	public WgProperties wgProp ;
	public SimpleCrossing X ;
	public StraightWg wg1, wg2 ;
	public CurvedWg Cwg1, Cwg2 ;
	public CompactCoupler DC1, DC2 ;
	public PlasmaDispersionModel plasmaEffect ;

	int steps = 1000 ; // this is the terms of the resonance that we sum up
	Complex zero = Complex.ZERO, one = Complex.ONE ;

	public Complex port1, port2, port3, port4 ;
	Complex port1_accumulated, port2_accumulated, port3_accumulated, port4_accumulated ;

	public Complex S11, S21, S31, S41 ; // exciting port 1
	public Complex S12, S22, S32, S42 ; // exciting port 2
	public Complex S13, S23, S33, S43 ; // exciting port 3
	public Complex S14, S24, S34, S44 ; // exciting port 4

	public Switch1x2Ring(
			Wavelength inputLambda,
			WgProperties wgProp,
			@ParamName(name="radius (um)") double radiusMicron,
			SimpleCrossing crossingModel,
			@ParamName(name="input kappa") double inputKappa,
			@ParamName(name="output kappa") double outputKappa,
			PlasmaDispersionModel plasmaEffect,
			@ParamName(name="is resonance locked?") boolean isLocked,
			@ParamName(name="locked resonance (nm)") double lockedLambdaNm
			){
		this.steps = wgProp.getConvergenceSteps() ;
		this.inputLambda = inputLambda ;
		this.plasmaEffect = plasmaEffect ;
		this.wgProp = wgProp ;
		this.X = crossingModel ;
		this.wg1 = new StraightWg(inputLambda, wgProp, radiusMicron, false, null, false, null) ;
		this.wg2 = new StraightWg(inputLambda, wgProp, radiusMicron, false, null, false, null) ;
		this.DC1 = new CompactCoupler(inputKappa) ;
		this.DC2 = new CompactCoupler(outputKappa) ;
		ResonanceLock resLock = new ResonanceLock(radiusMicron, lockedLambdaNm) ;
		if(isLocked){
			this.Cwg1 = new CurvedWg(inputLambda, wgProp, radiusMicron, 90, true, plasmaEffect, false, null) ;
			this.Cwg2 = new CurvedWg(inputLambda, wgProp, radiusMicron, 270, true, plasmaEffect, true, resLock.getLockHeater()) ;
		}
		else{
			this.Cwg1 = new CurvedWg(inputLambda, wgProp, radiusMicron, 90, true, plasmaEffect, false, null) ;
			this.Cwg2 = new CurvedWg(inputLambda, wgProp, radiusMicron, 270, true, plasmaEffect, false, null) ;
		}

		initializePorts();
		calculateScattParams();
	}

	// This constructor for critical coupling*****************
	public Switch1x2Ring(
			Wavelength inputLambda,
			WgProperties wgProp,
			@ParamName(name="radius (um)") double radiusMicron,
			SimpleCrossing crossingModel,
			@ParamName(name="output kappa") double outputKappa,
			PlasmaDispersionModel plasmaEffect,
			@ParamName(name="is resonance locked?") boolean isLocked,
			@ParamName(name="locked resonance (nm)") double lockedLambdaNm
			){
		this.steps = wgProp.getConvergenceSteps() ;
		this.X = crossingModel ;
		this.wg1 = new StraightWg(inputLambda, wgProp, radiusMicron, false, null, false, null) ;
		this.wg2 = new StraightWg(inputLambda, wgProp, radiusMicron, false, null, false, null) ;
		this.plasmaEffect = plasmaEffect ;
		// need to lock the wavelength in passive mode (delta alpha = 0)
		ResonanceLock resLock = new ResonanceLock(radiusMicron, lockedLambdaNm) ;
		if(isLocked){
			this.Cwg1 = new CurvedWg(inputLambda, wgProp, radiusMicron, 90, true, plasmaEffect, false, null) ;
			this.Cwg2 = new CurvedWg(inputLambda, wgProp, radiusMicron, 270, true, plasmaEffect, true, resLock.getLockHeater()) ;
		}
		else{
			this.Cwg1 = new CurvedWg(inputLambda, wgProp, radiusMicron, 90, true, plasmaEffect, false, null) ;
			this.Cwg2 = new CurvedWg(inputLambda, wgProp, radiusMicron, 270, true, plasmaEffect, false, null) ;
		}
		double loss = Math.exp(-2*Math.PI*radiusMicron*1e-6*wgProp.getBendLossModel().getLossPerMeter(radiusMicron)) ; // loss is not in dB --> Round trip power loss inside the ring
		double inputKappa = Math.sqrt(1-loss*(1-outputKappa*outputKappa)) ;
		this.DC1 = new CompactCoupler(inputKappa) ;
		this.DC2 = new CompactCoupler(outputKappa) ;

		initializePorts();
		calculateScattParams();
	}
	//********************************************************

	private void calculateScattParams(){
		Complex[] Sx1 = excitePorts(one, zero, zero, zero) ;
		this.S11 = Sx1[0] ; this.S21 = Sx1[1] ; this.S31 = Sx1[2] ; this.S41 = Sx1[3] ;

		Complex[] Sx2 = excitePorts(zero, one, zero, zero) ;
		this.S12 = Sx2[0] ; this.S22 = Sx2[1] ; this.S32 = Sx2[2] ; this.S42 = Sx2[3] ;

		Complex[] Sx3 = excitePorts(zero, zero, one, zero) ;
		this.S13 = Sx3[0] ; this.S23 = Sx3[1] ; this.S33 = Sx3[2] ; this.S43 = Sx3[3] ;

		Complex[] Sx4 = excitePorts(zero, zero, zero, one) ;
		this.S14 = Sx4[0] ; this.S24 = Sx4[1] ; this.S34 = Sx4[2] ; this.S44 = Sx4[3] ;
	}

	/**
	 * easy methods for connecting ports
	 */

	public void initializePorts(){
		port1 = port2 = port3 = port4 = zero ;
		port1_accumulated = port2_accumulated = port3_accumulated = port4_accumulated = zero ;
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

	public Complex[] excitePorts(Complex port1In, Complex port2In, Complex port3In, Complex port4In){
		wg1.initializePorts();
		wg2.initializePorts();
		DC1.initializePorts();
		DC2.initializePorts();
		X.initializePorts();
		Cwg1.initializePorts();
		Cwg2.initializePorts();
		for(int i=0; i<steps; i++){
			DC1.connectPorts(port1In, wg1.port1, Cwg1.port2, Cwg2.port1);
			DC2.connectPorts(wg2.port1, port2In, Cwg2.port2, Cwg1.port1);
			wg1.connectPorts(DC1.port2, X.port1);
			wg2.connectPorts(DC2.port1, X.port2);
			Cwg1.connectPorts(DC2.port4, DC1.port3);
			Cwg2.connectPorts(DC1.port4, DC2.port3);
			X.connectPorts(wg1.port2, wg2.port2, port3In, port4In);
			port1In = port2In = port3In = port4In = zero ;
		}
		Complex[] outPorts = {DC1.getPort1(), DC2.getPort2(), X.getPort3(), X.getPort4()} ;
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

	@Deprecated
	public Complex getPort1(Complex port1In, Complex port2In, Complex port3In, Complex port4In){
		Complex T1 = port1In.times(S11) ;
		Complex T2 = port2In.times(S12) ;
		Complex T3 = port3In.times(S13) ;
		Complex T4 = port4In.times(S14) ;
		return T1.plus(T2).plus(T3).plus(T4) ;
	}

	@Deprecated
	public Complex getPort2(Complex port1In, Complex port2In, Complex port3In, Complex port4In){
		Complex T1 = port1In.times(S21) ;
		Complex T2 = port2In.times(S22) ;
		Complex T3 = port3In.times(S23) ;
		Complex T4 = port4In.times(S24) ;
		return T1.plus(T2).plus(T3).plus(T4) ;
	}

	@Deprecated
	public Complex getPort3(Complex port1In, Complex port2In, Complex port3In, Complex port4In){
		Complex T1 = port1In.times(S31) ;
		Complex T2 = port2In.times(S32) ;
		Complex T3 = port3In.times(S33) ;
		Complex T4 = port4In.times(S34) ;
		return T1.plus(T2).plus(T3).plus(T4) ;
	}

	@Deprecated
	public Complex getPort4(Complex port1In, Complex port2In, Complex port3In, Complex port4In){
		Complex T1 = port1In.times(S41) ;
		Complex T2 = port2In.times(S42) ;
		Complex T3 = port3In.times(S43) ;
		Complex T4 = port4In.times(S44) ;
		return T1.plus(T2).plus(T3).plus(T4) ;
	}


}
