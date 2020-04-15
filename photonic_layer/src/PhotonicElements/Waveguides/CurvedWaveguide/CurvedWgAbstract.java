package PhotonicElements.Waveguides.CurvedWaveguide;

import PhotonicElements.Utilities.MathLibraries.Complex;
import ch.epfl.general_libraries.clazzes.ParamName;

public class CurvedWgAbstract {

	public double phaseShiftRad ;
	public double lossFactor ;

	public Complex port1, port2 ;
	Complex port1_accumulated, port2_accumulated ;

	public Complex S11, S12 ;
	public Complex S21, S22 ;

	public CurvedWgAbstract(
			@ParamName(name="Power Loss Factor") double lossFactor,
			@ParamName(name="Prop Phase Shift (rad)") double phaseShiftRad
			){
		this.lossFactor = lossFactor ;
		this.phaseShiftRad = phaseShiftRad ;

		initializePorts() ;
		calculateScattParams() ;
	}

	private void calculateScattParams(){
		S11 = Complex.ZERO ;
		S22 = Complex.ZERO ;
		S12 = new Complex(Math.sqrt(lossFactor)*Math.cos(phaseShiftRad), -Math.sqrt(lossFactor)*Math.sin(phaseShiftRad)) ;
		S21 = new Complex(Math.sqrt(lossFactor)*Math.cos(phaseShiftRad), -Math.sqrt(lossFactor)*Math.sin(phaseShiftRad)) ;
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

	public Complex getPort1(){
		return port1_accumulated ;
	}

	public Complex getPort2(){
		return port2_accumulated ;
	}

	public Complex getS11(){
		return S11 ;
	}

	public Complex getS12(){
		return S12 ;
	}

	public Complex getS21(){
		return S21 ;
	}

	public Complex getS22(){
		return S22 ;
	}

	private Complex getPort1(Complex port1In, Complex port2In){
		Complex T1 = port1In.times(S11) ;
		Complex T2 = port2In.times(S12) ;
		return T1.plus(T2) ;
	}

	private Complex getPort2(Complex port1In, Complex port2In){
		Complex T1 = port1In.times(S21) ;
		Complex T2 = port2In.times(S22) ;
		return T1.plus(T2) ;
	}

}
