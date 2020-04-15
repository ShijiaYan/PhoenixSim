package PhotonicElements.Switches.Switch2x2;

import PhotonicElements.DirectionalCoupler.CompactCoupler;
import PhotonicElements.Heater.SimpleHeater;
import PhotonicElements.PNJunction.PlasmaDispersionModel;
import PhotonicElements.Utilities.MathLibraries.Complex;
import PhotonicElements.Utilities.Wavelength;
import PhotonicElements.Waveguides.StraightWaveguide.StraightWg;
import PhotonicElements.Waveguides.WaveguideProperties.WgProperties;
import ch.epfl.general_libraries.clazzes.ParamName;

public class Switch2x2MZITO {

	public Wavelength inputLambda ;
	public WgProperties wgProp ;
	public StraightWg wg1, wg2 ;
	public CompactCoupler DC1, DC2 ;
	public SimpleHeater H ;
	boolean isCross ;
	boolean isBar ;

	public int steps = 4 ;
	Complex zero = Complex.ZERO , one = Complex.ONE ;

	public Complex port1, port2, port3, port4 ;
	Complex port1_accumulated, port2_accumulated, port3_accumulated, port4_accumulated ;

	public Complex S11, S21, S31, S41 ;
	public Complex S12, S22, S32, S42 ;
	public Complex S13, S23, S33, S43 ;
	public Complex S14, S24, S34, S44 ;

	public Switch2x2MZITO(
			Wavelength inputLambda,
			WgProperties wgProp,
			@ParamName(name="Length of Arm (um)") double lengthMicron,
			CompactCoupler DC1,
			CompactCoupler DC2,
			SimpleHeater H
			){
		this.inputLambda = inputLambda ;
		this.wgProp = wgProp ;
		this.DC1 = DC1 ;
		this.DC2 = DC2 ;
		this.wg1 = new StraightWg(inputLambda, wgProp, lengthMicron, false, null, false, null) ; // no  thermal heater
		this.wg2 = new StraightWg(inputLambda, wgProp, lengthMicron, false, null, true, H) ; //  thermal heater

		initializePorts() ;
		calculateScattParams() ;
	}

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

	public Wavelength getWavelength(){
		return inputLambda ;
	}

	public String getStateOfSwitch(){
		if(isBar && !isCross){return "B" ;}
		else{return "C";}
	}

	/**
	 * new methods for connection of ports
	 */

	public void initializePorts(){
		port1 = port2 = port3 = port4 = Complex.ZERO ;
		port1_accumulated = port2_accumulated = port3_accumulated = port4_accumulated = Complex.ZERO ;
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

	// define ports based on scattering parameters
	@Deprecated
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
