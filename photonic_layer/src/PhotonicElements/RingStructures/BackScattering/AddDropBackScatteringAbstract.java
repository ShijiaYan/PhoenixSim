package PhotonicElements.RingStructures.BackScattering;

import PhotonicElements.DirectionalCoupler.CompactCoupler;
import PhotonicElements.Utilities.Wavelength;
import PhotonicElements.Utilities.MathLibraries.Complex;
import PhotonicElements.Waveguides.CurvedWaveguide.CurvedWgAbstract;
import PhotonicElements.Waveguides.TerminatorAndReflector.LumpedReflector;
import ch.epfl.general_libraries.clazzes.ParamName;

public class AddDropBackScatteringAbstract {

	public Wavelength inputLambda ;
	public CompactCoupler DC1, DC2 ;
	public CurvedWgAbstract Cwg1, Cwg2 ;
	public LumpedReflector LR ;
	public double resLambdaNm, FSR_nm, deltaPhiRad ;

	public Complex port1, port2, port3, port4 ;
	Complex port1_accumulated, port2_accumulated, port3_accumulated, port4_accumulated ;

	Complex zero = Complex.ZERO ;
	Complex one = Complex.ONE ;

	public Complex S11, S12, S13, S14 ;
	public Complex S21, S22, S23, S24 ;
	public Complex S31, S32, S33, S34 ;
	public Complex S41, S42, S43, S44 ;

	int steps = 1000 ;

	public AddDropBackScatteringAbstract(
			Wavelength inputLambda,
			@ParamName(name="Resonance wavelength (nm)") double resLambdaNm,
			@ParamName(name="FSR (nm)") double FSR_nm,
			@ParamName(name="input kappa") double inputKappa,
			@ParamName(name="output kappa") double outputKappa,
			@ParamName(name="Round-trip power attenuation of the ring") double L,
			@ParamName(name="Lumped Reflector Model") LumpedReflector LR
			){
		this.inputLambda = inputLambda ;
		this.DC1 = new CompactCoupler(inputKappa) ;
		this.DC2 = new CompactCoupler(outputKappa) ;
		this.LR = LR ;
		this.resLambdaNm = resLambdaNm ;
		this.FSR_nm = FSR_nm ;
		this.deltaPhiRad = 2*Math.PI*(inputLambda.getWavelengthNm()-resLambdaNm)/FSR_nm + LR.S21.phase()  ;
		this.Cwg1 = new CurvedWgAbstract(Math.pow(L, 0.5), deltaPhiRad/2) ;
		this.Cwg2 = new CurvedWgAbstract(Math.pow(L, 0.5), deltaPhiRad/2) ;

		initializePorts() ;
		calculateScattParams() ;
	}

	public void setNumIterations(int steps){
		this.steps = steps ;
	}

	private void calculateScattParams(){
		Complex[] Sx1 = excitePorts(one, zero, zero, zero) ;
		Complex[] Sx2 = excitePorts(zero, one, zero, zero) ;
		Complex[] Sx3 = excitePorts(zero, zero, one, zero) ;
		Complex[] Sx4 = excitePorts(zero, zero, zero, one) ;
		S11 = Sx1[0]; S21 = Sx1[1]; S31 = Sx1[2]; S41 = Sx1[3] ;
		S12 = Sx2[0]; S22 = Sx2[1]; S32 = Sx2[2]; S42 = Sx2[3] ;
		S13 = Sx3[0]; S23 = Sx3[1]; S33 = Sx3[2]; S43 = Sx3[3] ;
		S14 = Sx4[0]; S24 = Sx4[1]; S34 = Sx4[2]; S44 = Sx4[3] ;
	}

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
		Cwg1.initializePorts();
		Cwg2.initializePorts();
		LR.initializePorts();
		for(int i=0; i<steps; i++){
			DC1.connectPorts(port1In, port2In, LR.port1, Cwg2.port1);
			LR.connectPorts(DC1.port3, Cwg1.port2);
			Cwg1.connectPorts(DC2.port3, LR.port2);
			DC2.connectPorts(port4In, port3In, Cwg1.port1, Cwg2.port2);
			Cwg2.connectPorts(DC1.port4, DC2.port4);
			port1In = port2In = port3In = port4In = Complex.ZERO ;
		}
		Complex[] outPorts = {DC1.getPort1(), DC1.getPort2(), DC2.getPort2(), DC2.getPort1()} ;
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

	private Complex getPort1(Complex port1In, Complex port2In, Complex port3In, Complex port4In){
		Complex T1 = port1In.times(S11) ;
		Complex T2 = port2In.times(S12) ;
		Complex T3 = port3In.times(S13) ;
		Complex T4 = port4In.times(S14) ;
		return T1.plus(T2).plus(T3).plus(T4) ;
	}

	private Complex getPort2(Complex port1In, Complex port2In, Complex port3In, Complex port4In){
		Complex T1 = port1In.times(S21) ;
		Complex T2 = port2In.times(S22) ;
		Complex T3 = port3In.times(S23) ;
		Complex T4 = port4In.times(S24) ;
		return T1.plus(T2).plus(T3).plus(T4) ;
	}

	private Complex getPort3(Complex port1In, Complex port2In, Complex port3In, Complex port4In){
		Complex T1 = port1In.times(S31) ;
		Complex T2 = port2In.times(S32) ;
		Complex T3 = port3In.times(S33) ;
		Complex T4 = port4In.times(S34) ;
		return T1.plus(T2).plus(T3).plus(T4) ;
	}

	private Complex getPort4(Complex port1In, Complex port2In, Complex port3In, Complex port4In){
		Complex T1 = port1In.times(S41) ;
		Complex T2 = port2In.times(S42) ;
		Complex T3 = port3In.times(S43) ;
		Complex T4 = port4In.times(S44) ;
		return T1.plus(T2).plus(T3).plus(T4) ;
	}


}
