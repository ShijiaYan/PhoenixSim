package PhotonicElements.Waveguides.StraightWaveguide;

import java.util.Map;

import PhotonicElements.EffectiveIndexMethod.ModeSolver.StripWg.ModeStripWgTE;
import PhotonicElements.EffectiveIndexMethod.Structures.StripWg;
import PhotonicElements.Heater.SimpleHeater;
import PhotonicElements.PNJunction.PlasmaDispersionModel;
import PhotonicElements.Utilities.MathLibraries.Complex;
import PhotonicElements.Utilities.Wavelength;
import PhotonicElements.Waveguides.WaveguideProperties.WgProperties;
import ch.epfl.general_libraries.utils.SimpleMap;

public class StraightWg {

	public Wavelength inputLambda ;
	public WgProperties wgProp ;
	public double lengthMicron ;
	public boolean includePNJunction ;
	public PlasmaDispersionModel plasmaEffect ;
	public SimpleHeater heater ;

	StripWg stripWg ;
	ModeStripWgTE modeTE ;

	public Complex port1, port2 ;
	Complex port1_accumulated, port2_accumulated ;

	public Complex S11, S12 ;
	public Complex S21, S22 ;

	public StraightWg(
			Wavelength inputLambda,
			WgProperties wgProperties,
			double lengthMicron,
			boolean includePNJunction,
			PlasmaDispersionModel plasmaEffect,
			boolean includeHeater,
			SimpleHeater heater
			){
		this.lengthMicron = lengthMicron ;
		this.inputLambda = inputLambda ;
		this.wgProp = wgProperties ;
		if(includePNJunction){
			this.plasmaEffect = plasmaEffect ;
		}
		else{
			this.plasmaEffect = new PlasmaDispersionModel(0, true, 0, false, 0, false) ;
		}
		if(includeHeater){
			this.heater = heater ;
		}
		else{
			this.heater = new SimpleHeater(0) ;
		}

		buildWg();
		calculateScattParams() ;
		initializePorts();
	}

	public Map<String, String> getAllParameters(){
		Map<String, String> map = new SimpleMap<String, String>() ;
		map.put("wavelength (nm)", inputLambda.getWavelengthNm()+"") ;
		map.put("wg length (um)", lengthMicron+"") ;
		map.putAll(wgProp.getAllParameters());
		return map ;
	}

	private void buildWg(){
		double widthNm = wgProp.getWidthNm() ;
		double heightNm = wgProp.getHeightNm() ;
		stripWg = new StripWg(inputLambda, widthNm, heightNm) ;
		modeTE = new ModeStripWgTE(stripWg, 0, 0) ; // TE00 mode
	}

	private void calculateScattParams(){
		S11 = getS11() ;
		S22 = getS22() ;
		S12 = getS12() ;
		S21 = getS21() ;
	}

	public double getEffectiveIndex() {
		double neff = modeTE.getEffectiveIndex() ;
		return neff ;
	}

	public double getGroupIndex() {
		double ng = modeTE.getGroupIndex() ;
		return ng;
	}

	public String getWgName() {
		return wgProp.getWidthNm()+"X"+wgProp.getHeightNm();
	}

//	public double getWgPropLossdBperCm(){
//		return wgProp.getWgPropLossdBperCm() + plasmaEffect.getDalphadBperCm() + heater.getChangeOfLossdBperCm() ;
//	}

	public double getWgPropLossdBperCm(){
		return wgProp.getWgPropLossdBperCm() + heater.getChangeOfLossdBperCm() + plasmaEffect.getDalphadBperCm()*wgProp.getConfinementFactor() ;
	}


	public double getWgLengthMicron(){
		return lengthMicron ;
	}

	public PlasmaDispersionModel getPlasmaEffect(){
		return plasmaEffect ;
	}

	public SimpleHeater getHeater(){
		return heater ;
	}

	public Wavelength getWavelength(){
		return inputLambda ;
	}

	// Now calculate the scattering parameters
	public Complex getS11(){
		return new Complex(0,0) ;
	}

	public Complex getS22(){
		return new Complex(0,0) ;
	}

	public Complex getS21(){
		double neff = getEffectiveIndex() ;
		double lambdaMeter = inputLambda.getWavelengthNm() * 1e-9 ;
		double deltaNeff = plasmaEffect.getDnSi() * wgProp.getConfinementFactor() ; // plasma dispersion effect
		double beta = 2*Math.PI/lambdaMeter * (neff + deltaNeff) ;
		double alphaPerMeter = getWgPropLossdBperCm()/2 * 23 ; // conversion from dB/cm to 1/m
		double lengthMeter = lengthMicron * 1e-6 ;
		Complex propPhase = new Complex(beta*lengthMeter + heater.getPhaseShiftRadian(), -alphaPerMeter*lengthMeter) ;
		Complex S21 = propPhase.times(Complex.minusJ).exp() ;
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
		port1 = getPort1(port1In, port2In) ;
		port2 = getPort2(port1In, port2In) ;
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
