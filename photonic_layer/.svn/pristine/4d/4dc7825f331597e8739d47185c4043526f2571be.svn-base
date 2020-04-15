package PhotonicElements.RingStructures.AddDrop;

import PhotonicElements.EffectiveIndexMethod.ModeSolver.StripWg.ModeStripWgTE;
import PhotonicElements.EffectiveIndexMethod.Structures.StripWg;
import PhotonicElements.Heater.SimpleHeater;
import PhotonicElements.Utilities.Wavelength;
import PhotonicElements.Utilities.MathLibraries.Complex;
import PhotonicElements.Waveguides.WaveguideProperties.WgProperties;
import ch.epfl.general_libraries.clazzes.ParamName;

public class AddDropFirstOrderFast {

	public Wavelength inputLambda ;
	public WgProperties wgProp ;
	public SimpleHeater H ;
	public double radiusMicron, inputKappa, outputKappa ;

	Complex one = Complex.ONE ;
	Complex zero = Complex.ZERO ;

	public Complex port1, port2, port3, port4 ;
	Complex port1_accumulated, port2_accumulated, port3_accumulated, port4_accumulated ;

	public Complex S11, S12, S13, S14 ;
	public Complex S21, S22, S23, S24 ;
	public Complex S31, S32, S33, S34 ;
	public Complex S41, S42, S43, S44 ;

	public AddDropFirstOrderFast (
			Wavelength inputLambda,
			WgProperties wgProp,
			@ParamName(name="Radius (um)") double radiusMicron,
			@ParamName(name="Input Kappa") double inputKappa,
			@ParamName(name="Output Kappa") double outputKappa,
			@ParamName(name="Includes Heater?") boolean includesHeater,
			SimpleHeater H
			){
		this.inputLambda = inputLambda ;
		this.wgProp = wgProp ;
		this.radiusMicron = radiusMicron ;
		this.inputKappa = inputKappa ;
		this.outputKappa = outputKappa ;
		this.H = H ;

		initializePorts() ;
		calculateScattParams();
	}

	public void calculateScattParams(){
		S11 = S22 = S33 = S44 = Complex.ZERO ;
		S12 = S21 = S34 = S43 = getThru() ;
		S13 = S31 = S42 = S24 = Complex.ZERO ;
		S14 = S41 = S23 = S32 = getDrop() ;
	}

	private Complex getThru(){
		double tin = Math.sqrt(1-inputKappa*inputKappa) ;
		double tout = Math.sqrt(1-outputKappa*outputKappa) ;
		double L = Math.exp(-2*Math.PI*radiusMicron*1e-6*wgProp.getBendLossModel().getLossPerMeter(radiusMicron)) ;
		double phi = inputLambda.getK0()*getNeff()*2*Math.PI*radiusMicron*1e-6 + H.getPhaseShiftRadian() ;
		Complex num = Complex.minusJ.times(phi).exp().times(Math.sqrt(L)*tout).minus(new Complex(tin,0)).times(-1) ;
		Complex denom = Complex.minusJ.times(phi).exp().times(Math.sqrt(L)*tin*tout).minus(Complex.ONE).times(-1) ;
		return num.divides(denom) ;
	}

	private Complex getDrop(){
		double tin = Math.sqrt(1-inputKappa*inputKappa) ;
		double tout = Math.sqrt(1-outputKappa*outputKappa) ;
		double L = Math.exp(-2*Math.PI*radiusMicron*1e-6*wgProp.getBendLossModel().getLossPerMeter(radiusMicron)) ;
		double phi = inputLambda.getK0()*getNeff()*2*Math.PI*radiusMicron*1e-6 + H.getPhaseShiftRadian() ;
		Complex num = Complex.minusJ.times(phi/2).exp().times(inputKappa*outputKappa*Math.pow(L, 0.25)) ;
		Complex denom = Complex.minusJ.times(phi).exp().times(Math.sqrt(L)*tin*tout).minus(Complex.ONE).times(-1) ;
		return num.divides(denom) ;
	}

	private double getNeff(){
		StripWg stripWg = new StripWg(inputLambda, wgProp.getWidthNm(), wgProp.getHeightNm()) ;
		ModeStripWgTE modeTE = new ModeStripWgTE(stripWg, 0, 0) ;
		return modeTE.getEffectiveIndex() ;
	}

	public void initializePorts(){
		port1 = Complex.ZERO ;
		port2 = Complex.ZERO ;
		port3 = Complex.ZERO ;
		port4 = Complex.ZERO ;
		port1_accumulated = Complex.ZERO ;
		port2_accumulated = Complex.ZERO ;
		port3_accumulated = Complex.ZERO ;
		port4_accumulated = Complex.ZERO ;
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
