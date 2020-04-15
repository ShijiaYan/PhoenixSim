package PhotonicElements.Waveguides.CurvedWaveguide;


import PhotonicElements.EffectiveIndexMethod.ModeSolver.StripWg.ModeStripWgTE;
import PhotonicElements.EffectiveIndexMethod.Structures.StripWg;
import PhotonicElements.Heater.SimpleHeater;
import PhotonicElements.PNJunction.PlasmaDispersionModel;
import PhotonicElements.Utilities.MathLibraries.Complex;
import PhotonicElements.Utilities.Wavelength;
import PhotonicElements.Waveguides.WaveguideProperties.WgProperties;


public class CurvedWg {

	double wgPropLossdBperCm ;
	double radiusOfCurvatureMicron ;
	double angleOfCurvagureDegree ;
	double lengthMicron ;
	double angleOfCurvatureRadian ;
	double lambdaNm  ;
	boolean includeHeater;
	SimpleHeater heater ;
	PlasmaDispersionModel plasmaEffect ;
	WgProperties wgProp ;
	Wavelength inputLambda ;

	StripWg stripWg ;
	ModeStripWgTE modeTE ;

	// now setting up ports
	public Complex port1, port2 ;
	Complex port1_accumulated, port2_accumulated ;

	// This is the constructor
	public CurvedWg(
			Wavelength inputLambda,
			WgProperties wgProp,
			double radiusOfCurvatureMicron ,
			double angleOfCurvatureDegree ,
			boolean includePNJunction,
			PlasmaDispersionModel plasmaEffect,
			boolean includeHeater,
			SimpleHeater heater
			){
		this.inputLambda = inputLambda ;
		this.lambdaNm = inputLambda.getWavelengthNm() ;
		this.wgPropLossdBperCm = wgProp.getBendLossModel().getLossdBperCm(radiusOfCurvatureMicron) ;
		this.radiusOfCurvatureMicron = radiusOfCurvatureMicron ;
		this.angleOfCurvagureDegree = angleOfCurvatureDegree ;
		this.angleOfCurvatureRadian = angleOfCurvatureDegree * Math.PI/180 ;
		this.lengthMicron = radiusOfCurvatureMicron * angleOfCurvatureDegree * Math.PI/180 ;
		this.wgProp = wgProp ;
		//
		if(includePNJunction){
			this.plasmaEffect = plasmaEffect ;
		}
		else{
			this.plasmaEffect = new PlasmaDispersionModel(0, false, 0, false, 0, false) ;
		}
		//
		if(includeHeater){
			this.heater = heater ;
		}
		else{
			this.heater = new SimpleHeater(0) ;
		}
		// building the waveguide
		buildWg();
		// initialize ports
		initializePorts();
	}

	private void buildWg(){
		double widthNm = wgProp.getWidthNm() ;
		double heightNm = wgProp.getHeightNm() ;
		stripWg = new StripWg(inputLambda, widthNm, heightNm) ;
		modeTE = new ModeStripWgTE(stripWg, 0, 0) ; // TE00 mode
	}

	public double getEffectiveIndex() {
		double neff = modeTE.getEffectiveIndex() ;
		return neff ;
	}

	public double getGroupIndex() {
		double ng = modeTE.getGroupIndex() ;
		return ng;
	}

//	public double getEffectiveIndex() {
//		double lambdaMicron = lambdaNm/1000 ;
//		double A4 = 0.3391437655 ;
//		double A3 = -1.954733 ;
//		double A2 = 4.155727848 ;
//		double A1 = -5.089414343 ;
//		double A0 = 5.585688163 ;
//		double neff = A4*Math.pow(lambdaMicron, 4) + A3*Math.pow(lambdaMicron, 3)+
//				A2*Math.pow(lambdaMicron, 2)+A1*lambdaMicron + A0 ;
//		return neff ;
//	}
//
//	public double getGroupIndex() {
//		double lambdaMicron = lambdaNm/1000 ;
//		double A4 = 0.3391437655 ;
//		double A3 = -1.954733 ;
//		double A2 = 4.155727848 ;
//		double A1 = -5.089414343 ;
//		double neff = getEffectiveIndex() ;
//		double dneff_dlambda = 4*A4*Math.pow(lambdaMicron, 3) + 3*A3*Math.pow(lambdaMicron, 2)+
//				2*A2*lambdaMicron + A1 ;
//		double ng = neff - lambdaMicron * dneff_dlambda ;
//		return ng;
//	}

	public String getWgName() {
		return "450X220";
	}


	public double getWgPropLossdBperCm(){
		return wgPropLossdBperCm + plasmaEffect.getDalphadBperCm()*wgProp.getConfinementFactor() + heater.getChangeOfLossdBperCm() ;
	}

	public double getRadiusOfCurvatureMicron(){
		return radiusOfCurvatureMicron ;
	}

	public double getAngleOfCurvatureDegree(){
		return angleOfCurvagureDegree ;
	}

	public double getAngleOfCurvatureRadian(){
		return angleOfCurvatureRadian ;
	}

	public double getWgLengthMicron(){
		return lengthMicron ;
	}

	public SimpleHeater getHeater(){
		return heater ;
	}

	public Wavelength getWavelength(){
		return inputLambda ;
	}

	// Now calculate the scattering parameters*****************************
	public Complex getS11(){
		return new Complex(0,0) ;
	}

	//Added by Ziyi For Nonlinear
	public double getDalphaCurvedWg(){
		return getWgPropLossdBperCm()*23;
	}

	public Complex getS22(){
		return new Complex(0,0) ;
	}

	public Complex getS21(){
		double neff = getEffectiveIndex() ;
		double lambdaMeter = lambdaNm * 1e-9 ;
		double deltaNeff = plasmaEffect.getDnSi() * wgProp.getConfinementFactor() ; // plasma dispersion effect
		double beta = 2*Math.PI/lambdaMeter * (neff + deltaNeff) ;
		double alphaPerMeter = getWgPropLossdBperCm()/2 * 23 ; // conversion from dB/cm to 1/m for the electric field
		double lengthMeter = lengthMicron * 1e-6 ;
		Complex propPhase = new Complex(beta*lengthMeter + heater.getPhaseShiftRadian(), -alphaPerMeter*lengthMeter) ;
		Complex S21 = propPhase.times(new Complex(0,-1)).exp() ;
		return S21 ;
	}

	public Complex getS12(){
		return getS21() ;
	}

	/**
	 * New methods for easy connection of ports
	 */

	public void initializePorts(){
		port1 = Complex.ZERO ;
		port2 = Complex.ZERO ;
		port1_accumulated = Complex.ZERO ;
		port2_accumulated = Complex.ZERO ;
	}

	public void connectPorts(Complex port1In, Complex port2In){
		// these are the temporary port values in each propagation iteration
		port1 = getPort1(port1In, port2In) ;
		port2 = getPort2(port1In, port2In) ;
		// these are the real port values accumulated over all iterations
		port1_accumulated = port1_accumulated.plus(port1) ;
		port2_accumulated = port2_accumulated.plus(port2) ;
	}

	public Complex getPort1(){
		return port1_accumulated ;
	}

	public Complex getPort2(){
		return port2_accumulated ;
	}

	public Complex[] getAllPorts(){
		return new Complex[] {port1_accumulated, port2_accumulated} ;
	}

	// old methods to be turned into private

	public Complex getPort1(Complex port1In, Complex port2In){
		Complex T1 = port1In.times(getS11()) ;
		Complex T2 = port2In.times(getS12()) ;
		return T1.plus(T2) ;
	}

	public Complex getPort2(Complex port1In, Complex port2In){
		Complex T1 = port1In.times(getS21()) ;
		Complex T2 = port2In.times(getS22()) ;
		return T1.plus(T2) ;
	}



}
