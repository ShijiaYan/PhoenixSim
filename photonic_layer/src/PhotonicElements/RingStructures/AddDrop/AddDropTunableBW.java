package PhotonicElements.RingStructures.AddDrop;

import PhotonicElements.DirectionalCoupler.CompactCoupler;
import PhotonicElements.Heater.SimpleHeater;
import PhotonicElements.Utilities.MathLibraries.Complex;
import PhotonicElements.Utilities.Wavelength;
import PhotonicElements.Waveguides.CurvedWaveguide.CurvedWg;
import PhotonicElements.Waveguides.StraightWaveguide.StraightWg;
import PhotonicElements.Waveguides.WaveguideProperties.WgProperties;

public class AddDropTunableBW {

	public Wavelength inputLambda ;
	public WgProperties wgProp ;
	public CompactCoupler DC1, DC2, DC3, DC4 ;
	public CurvedWg Cwg1, Cwg2, Cwg3, Cwg4 ;
	public StraightWg wg1, wg2 ;
	
	public int steps = 1000 ;
	
	Complex one = Complex.ONE ; 
	Complex zero = Complex.ZERO ;
	
	public Complex port1, port2, port3, port4 ;
	Complex port1_accumulated, port2_accumulated, port3_accumulated, port4_accumulated ;
	
	public Complex S11, S12, S13, S14 ;
	public Complex S21, S22, S23, S24 ;
	public Complex S31, S32, S33, S34 ;
	public Complex S41, S42, S43, S44 ;
	
	public AddDropTunableBW(
			Wavelength inputLambda,
			WgProperties wgProp,
			double radiusMicron,
			double angleOfTuningRegionDegree,
			double inputKappa1,
			double inputKappa2,
			double inputKappa3,
			double inputKappa4,
			SimpleHeater heater1,
			SimpleHeater heater2
			){
		this.inputLambda = inputLambda ;
		this.wgProp = wgProp ;
		this.DC1 = new CompactCoupler(inputKappa1) ;
		this.DC2 = new CompactCoupler(inputKappa2) ;
		this.DC3 = new CompactCoupler(inputKappa3) ;
		this.DC4 = new CompactCoupler(inputKappa4) ;
		this.Cwg1 = new CurvedWg(inputLambda, wgProp, radiusMicron, angleOfTuningRegionDegree, false, null, false, null) ;
		this.Cwg2 = new CurvedWg(inputLambda, wgProp, radiusMicron, 180-angleOfTuningRegionDegree, false, null, false, null) ;
		this.Cwg3 = new CurvedWg(inputLambda, wgProp, radiusMicron, angleOfTuningRegionDegree, false, null, false, null) ;
		this.Cwg4 = new CurvedWg(inputLambda, wgProp, radiusMicron, 180-angleOfTuningRegionDegree, false, null, false, null) ;
		this.wg1 = new StraightWg(inputLambda, wgProp, 0, false, null, true, heater1) ; // the physical length is zero, just the phase shift of the heater is included
		this.wg2 = new StraightWg(inputLambda, wgProp, 0, false, null, true, heater2) ;
		
		initializePorts();
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
		DC3.initializePorts();
		DC4.initializePorts();
		Cwg1.initializePorts();
		Cwg2.initializePorts();
		Cwg3.initializePorts();
		Cwg4.initializePorts();
		wg1.initializePorts();
		wg2.initializePorts();
		for(int i=0; i<steps; i++){
			DC1.connectPorts(wg1.port2, port2In, Cwg2.port2, Cwg1.port1);
			DC2.connectPorts(port3In, wg2.port2, Cwg3.port2, Cwg2.port1);
			DC3.connectPorts(wg2.port1, port4In, Cwg4.port2, Cwg3.port1);
			DC4.connectPorts(port1In, wg1.port1, Cwg1.port2, Cwg4.port1);
			Cwg1.connectPorts(DC1.port4, DC4.port3);
			Cwg2.connectPorts(DC2.port4, DC1.port3);
			Cwg3.connectPorts(DC3.port4, DC2.port3);
			Cwg4.connectPorts(DC4.port4, DC3.port3);
			wg1.connectPorts(DC4.port2, DC1.port1);
			wg2.connectPorts(DC3.port1, DC2.port2);
			port1In = port2In = port3In = port4In = Complex.ZERO ;
		}
		Complex[] outPorts = {DC4.getPort1(), DC1.getPort2(), DC2.getPort1(), DC3.getPort2()} ;
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
