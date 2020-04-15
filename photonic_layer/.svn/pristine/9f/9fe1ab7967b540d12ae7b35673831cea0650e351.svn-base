package PhotonicElements.RingStructures.AllPass;

import PhotonicElements.DirectionalCoupler.CompactCoupler;
import PhotonicElements.Utilities.MathLibraries.Complex;
import PhotonicElements.Utilities.Wavelength;
import PhotonicElements.Waveguides.CurvedWaveguide.CurvedWg;
import PhotonicElements.Waveguides.WaveguideProperties.WgProperties;
import ch.epfl.general_libraries.clazzes.ParamName;

public class RingWg {

	Wavelength inputLambda ;
	WgProperties wgProp ;
	CurvedWg Cwg1 ;
	CompactCoupler DC1 ;

	int steps = 1000 ;

	Complex zero = Complex.ZERO ;
	Complex one = Complex.ONE ;

	public Complex port1, port2 ;
	Complex port1_accumulated, port2_accumulated ;

	Complex S11, S12 ;
	Complex S21, S22 ;

	public RingWg (
			Wavelength inputLambda,
			WgProperties wgProp,
			@ParamName(name="Radius (um)") double radiusMicron,
			CompactCoupler coupler
			){
		this.inputLambda = inputLambda ;
		this.wgProp = wgProp ;
		this.steps = wgProp.getConvergenceSteps() ;
		this.DC1 = coupler ;
		this.Cwg1 = new CurvedWg(inputLambda, wgProp, radiusMicron, 360, false, null, false, null) ; // full circle curved waveguide = 360 degree

		initializePorts();
		calculateScattParams();
	}

	// For critical coupling ring
	public RingWg (
			Wavelength inputLambda,
			WgProperties wgProp,
			@ParamName(name="Radius (um)") double radiusMicron
			){
		this.inputLambda = inputLambda ;
		this.wgProp = wgProp ;
		this.steps = wgProp.getConvergenceSteps() ;
		double loss = Math.exp(-2*Math.PI*radiusMicron*1e-6*wgProp.getBendLossModel().getLossPerMeter(radiusMicron)) ; // round trip loss
		double inputKappa = Math.sqrt(1-loss) ;
		this.DC1 = new CompactCoupler(inputKappa) ;
		this.Cwg1 = new CurvedWg(inputLambda, wgProp, radiusMicron, 360, false, null, false, null) ; // full circle curved waveguide = 360 degree

		initializePorts();
		calculateScattParams();
	}

	private void calculateScattParams(){
		Complex[] Sx1 = excitePorts(one, zero) ;
		this.S11 = Sx1[0] ; this.S21 = Sx1[1] ;

		Complex[] Sx2 = excitePorts(zero, one) ;
		this.S12 = Sx2[0] ; this.S22 = Sx2[1] ;
	}

	public void setConvergenceSteps(int steps){
		this.steps = steps ;
	}

	public Wavelength getWavelength(){
		return inputLambda ;
	}

	public CompactCoupler getInputCouplingRegion(){
		return DC1 ;
	}

	public double getRadiusMicron(){
		return Cwg1.getRadiusOfCurvatureMicron() ;
	}

	public CurvedWg getCurvedWg(){
		return Cwg1 ;
	}

	/**
	 * new methods for connecting devices
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

	public Complex[] excitePorts(Complex port1In, Complex port2In){
		DC1.initializePorts();
		Cwg1.initializePorts();
		for(int i=0; i<steps; i++){
			DC1.connectPorts(port1In, port2In, Cwg1.port2, Cwg1.port1);
			Cwg1.connectPorts(DC1.port4, DC1.port3);
			// turn input excitations off
			port1In = Complex.ZERO ;
			port2In = Complex.ZERO ;
		}
		Complex[] outPorts = {DC1.getPort1(), DC1.getPort2()} ;
		return outPorts ;
	}

	public Complex getPort1(){
		return port1_accumulated ;
	}

	public Complex getPort2(){
		return port2_accumulated ;
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

	// old methods to be declared as private
	@Deprecated
	public Complex getPort1(Complex port1In, Complex port2In){
		Complex T1 = port1In.times(S11) ;
		Complex T2 = port2In.times(S12) ;
		return T1.plus(T2) ;
	}

	@Deprecated
	public Complex getPort2(Complex port1In, Complex port2In){
		Complex T1 = port1In.times(S21) ;
		Complex T2 = port2In.times(S22) ;
		return T1.plus(T2) ;
	}

}
