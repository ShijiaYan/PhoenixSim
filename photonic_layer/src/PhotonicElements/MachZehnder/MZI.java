package PhotonicElements.MachZehnder;

import PhotonicElements.Heater.SimpleHeater;
import PhotonicElements.Junctions.Yjunctions.Yjunction;
import PhotonicElements.PNJunction.PlasmaDispersionModel;
import PhotonicElements.Utilities.MathLibraries.Complex;
import PhotonicElements.Utilities.Wavelength;
import PhotonicElements.Waveguides.StraightWaveguide.StraightWg;
import PhotonicElements.Waveguides.WaveguideProperties.WgProperties;

public class MZI {
	
	public Wavelength inputLambda ;
	public WgProperties wgProp ;
	public Yjunction Y1, Y2 ;
	public StraightWg wg1, wg2 ;
	public PlasmaDispersionModel plasmaEffect ;
	public SimpleHeater H ;
	
	public int steps = 5 ; // there is only three stages with no loop back
	Complex zero = Complex.ZERO, one = Complex.ONE ;
	
	public Complex port1, port2 ;
	Complex port1_accumulated, port2_accumulated ;
	
	public Complex S11, S12;
	public Complex S21, S22 ;
	
	public MZI(
			Wavelength inputLambda,
			WgProperties wgProp,
			double YjunctionLossdB,
			double upperWgLengthMicron,
			double lowerWgLengthMicron,
			PlasmaDispersionModel plasmaEffect,
			SimpleHeater H
			){
		this.inputLambda = inputLambda ;
		this.wgProp = wgProp ;
		this.Y1 = new Yjunction(YjunctionLossdB) ;
		this.Y2 = new Yjunction(YjunctionLossdB) ;
		this.wg1 = new StraightWg(inputLambda, wgProp, upperWgLengthMicron, false, null, false, null) ;
		this.wg2 = new StraightWg(inputLambda, wgProp, lowerWgLengthMicron, true, plasmaEffect, true, H) ;
		this.plasmaEffect = plasmaEffect ;
		this.H = H ;

		initializePorts() ;
		calculateScattParams() ;
	}
	
	public void calculateScattParams(){
		Complex[] Sx1 = excitePorts(one, zero) ;
		this.S11 = Sx1[0] ; this.S21 = Sx1[1] ;
		
		Complex[] Sx2 = excitePorts(zero, one) ;
		this.S12 = Sx2[0] ; this.S22 = Sx2[1] ;
	}
	
	public void initializePorts(){
		port1 = port2 = Complex.ZERO ;
		port1_accumulated = port2_accumulated = Complex.ZERO ;
	}
	
	public void connectPorts(Complex port1In, Complex port2In){
		port1 = getPort1(port1In, port2In) ;
		port2 = getPort2(port1In, port2In) ;
		port1_accumulated = port1_accumulated.plus(port1) ;
		port2_accumulated = port2_accumulated.plus(port2) ;
	}
	
	public Complex[] excitePorts(Complex port1In, Complex port2In){
		Y1.initializePorts();
		Y2.initializePorts();
		wg1.initializePorts();
		wg2.initializePorts();
		for(int i=0; i<steps; i++){
			Y1.connectPorts(port1In, wg1.port1, wg2.port1);
			wg1.connectPorts(Y1.port2, Y2.port2);
			wg2.connectPorts(Y1.port3, Y2.port3);
			Y2.connectPorts(port2In, wg1.port2, wg2.port2);
			port1In = port2In = zero ;
		}
		Complex[] outPorts = {Y1.getPort1(), Y2.getPort1()} ;
		return outPorts ;	
	}
	
	public Complex getPort1(){
		return port1_accumulated ;
	}
	
	public Complex getPort2(){
		return port2_accumulated ;
	}
	
	// Calculating waves in each port
	
	public Complex getPort1(Complex port1In, Complex port2In){
		Complex T1 = port1In.times(S11) ;
		Complex T2 = port2In.times(S12) ;
		return T1.plus(T2) ;
	}
	
	public Complex getPort2(Complex port1In, Complex port2In){
		Complex T1 = port1In.times(S21) ;
		Complex T2 = port2In.times(S22) ;
		return T1.plus(T2) ;
	}

}
