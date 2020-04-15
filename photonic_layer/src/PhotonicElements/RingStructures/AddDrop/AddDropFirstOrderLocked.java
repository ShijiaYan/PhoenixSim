package PhotonicElements.RingStructures.AddDrop;

import PhotonicElements.DirectionalCoupler.CompactCoupler;
import PhotonicElements.Utilities.MathLibraries.Complex;
import PhotonicElements.Utilities.Wavelength;
import PhotonicElements.Waveguides.CurvedWaveguide.CurvedWg;
import PhotonicElements.Waveguides.WaveguideProperties.WgProperties;
import PhotonicElements.WavelengthLocking.ResonanceLock;

public class AddDropFirstOrderLocked {

	public Wavelength inputLambda ;
	public WgProperties wgProp ;
	public CurvedWg Cwg1, Cwg2 ;
	public CompactCoupler DC1, DC2 ;
	public double radiusMicron ;

	Complex one = Complex.ONE ; 
	Complex zero = Complex.ZERO ;

	public Complex port1, port2, port3, port4 ;
	Complex port1_accumulated, port2_accumulated, port3_accumulated, port4_accumulated ;
	
	public Complex S11, S12, S13, S14 ;
	public Complex S21, S22, S23, S24 ;
	public Complex S31, S32, S33, S34 ;
	public Complex S41, S42, S43, S44 ;

	public int steps = 1000 ; // update steps for propagating the light through the structure

	public AddDropFirstOrderLocked (
			Wavelength inputLambda,
			WgProperties wgProp,
			double radiusMicron ,
			double inputKappa ,
			double outputKappa ,
			boolean isLocked,
			double lockedLambdaNm 
			){
		this.inputLambda = inputLambda ;
		this.wgProp = wgProp ;
		this.radiusMicron = radiusMicron ;
//		ResonanceLock resLock = new ResonanceLock(radiusMicron, lockedLambdaNm) ;
		ResonanceLock resLock = new ResonanceLock(radiusMicron, lockedLambdaNm, wgProp) ;
		this.DC1 = new CompactCoupler(inputKappa) ;
		this.DC2 = new CompactCoupler(outputKappa) ;
		this.Cwg1 = new CurvedWg(inputLambda, wgProp, radiusMicron, 180, false, null, false, null) ;
		this.Cwg2 = new CurvedWg(inputLambda, wgProp, radiusMicron, 180, false, null, isLocked, resLock.getLockHeater()) ;
		
		initializePorts() ;
		calculateScattParams(); 
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
	
	public Complex[] excitePorts(Complex port1In, Complex port2In, Complex port3In, Complex port4In){
		DC1.initializePorts();
		DC2.initializePorts();
		Cwg1.initializePorts();
		Cwg2.initializePorts();
		for(int i=0; i<steps; i++){
			DC1.connectPorts(port1In, port2In, Cwg1.port2, Cwg2.port1);
			Cwg1.connectPorts(DC2.port3, DC1.port3);
			Cwg2.connectPorts(DC1.port4, DC2.port4);
			DC2.connectPorts(port4In, port3In, Cwg1.port1, Cwg2.port2);
			port1In = port2In = port3In = port4In = Complex.ZERO ;
		}
		Complex[] outputPorts = {DC1.getPort1(), DC1.getPort2(), DC2.getPort2(), DC2.getPort1()} ; // define the output port array
		return outputPorts ;
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
	private Complex getPort1(Complex port1In, Complex port2In, Complex port3In, Complex port4In){
		Complex T1 = port1In.times(S11) ;
		Complex T2 = port2In.times(S12) ;
		Complex T3 = port3In.times(S13) ;
		Complex T4 = port4In.times(S14) ;
		return T1.plus(T2).plus(T3).plus(T4) ;
	}

	@Deprecated
	private Complex getPort2(Complex port1In, Complex port2In, Complex port3In, Complex port4In){
		Complex T1 = port1In.times(S21) ;
		Complex T2 = port2In.times(S22) ;
		Complex T3 = port3In.times(S23) ;
		Complex T4 = port4In.times(S24) ;
		return T1.plus(T2).plus(T3).plus(T4) ;
	}

	@Deprecated
	private Complex getPort3(Complex port1In, Complex port2In, Complex port3In, Complex port4In){
		Complex T1 = port1In.times(S31) ;
		Complex T2 = port2In.times(S32) ;
		Complex T3 = port3In.times(S33) ;
		Complex T4 = port4In.times(S34) ;
		return T1.plus(T2).plus(T3).plus(T4) ;
	}

	@Deprecated
	private Complex getPort4(Complex port1In, Complex port2In, Complex port3In, Complex port4In){
		Complex T1 = port1In.times(S41) ;
		Complex T2 = port2In.times(S42) ;
		Complex T3 = port3In.times(S43) ;
		Complex T4 = port4In.times(S44) ;
		return T1.plus(T2).plus(T3).plus(T4) ;
	}

}
