package PhotonicElements.Junctions.Crossings;

import PhotonicElements.Utilities.MathLibraries.Complex;
import ch.epfl.general_libraries.clazzes.ParamName;

public class SimpleCrossing {

	double x, t, R ;

	public Complex port1, port2, port3, port4 ;
	Complex port1_accumulated, port2_accumulated, port3_accumulated, port4_accumulated ;

	public SimpleCrossing(
			@ParamName(name="Thru Loss of the Crossing (dB)") double thruLossdB
			){
		t = Math.pow(10, -thruLossdB/20) ; // for electric field
		
//		x = Math.sqrt(t*(1-t)) ; // worst-case of crosstalk
//		R = 1-t ;
		
		x = Math.sqrt(0.5*(1-t*t)) ; // worst-case of crosstalk
		R = 0 ; // no reflection

		initializePorts();
	}

	public SimpleCrossing(
			@ParamName(name="Thru Loss of the Crossing (dB)") double thruLossdB,
			@ParamName(name="Crosstalk suppression (dB) (Example: 20 dB)") double crossLossdB
			){
		double t_tot = Math.pow(10, -thruLossdB/20) ; // for electric field
		x = Math.pow(10, -crossLossdB/20) ; // Note that "(2*crossLoss + thruLoss)<1" must be held for conservation of energy
		
//		double A = Math.sqrt(1-4*x*x) ;
//		double t_original = (1+A)/2 ; // best case of thru loss
//		R = (1-A)/2 ;
		
		double t_original = Math.sqrt(1-2*x*x) ;
		R = 0 ; // no reflection
		
		if(t_tot < t_original){
			t = t_tot ;
		}
		else{
			t = t_original ;
		}

		initializePorts();
	}

	// Input ports are not matched; there is reflection at each port
	public Complex getS11(){
		return new Complex(R,0) ;
	}

	public Complex getS22(){
		return new Complex(R,0) ;
	}

	public Complex getS33(){
		return new Complex(R,0) ;
	}

	public Complex getS44(){
		return new Complex(R,0) ;
	}
	//********************************************
	public Complex getS31(){
		return new Complex(t, 0) ; // t
	}

	public Complex getS13(){
		return new Complex(t, 0) ; // t
	}
	//********************************************
	public Complex getS24(){
		return new Complex(t, 0) ; // t
	}

	public Complex getS42(){
		return new Complex(t, 0) ; // t
	}
	//********************************************
	public Complex getS21(){
		return new Complex(0, x) ; // j*x
	}

	public Complex getS12(){
		return new Complex(0, x) ; // j*x
	}
	//********************************************
	public Complex getS41(){
		return new Complex(0, -x) ; // -j*x
	}

	public Complex getS14(){
		return new Complex(0, -x) ; // -j*x
	}
	//********************************************
	public Complex getS23(){
		return new Complex(0, -x) ; // -j*x
	}

	public Complex getS32(){
		return new Complex(0, -x) ; // -j*x
	}
	//********************************************
	public Complex getS43(){
		return new Complex(0, x) ; // j*x
	}

	public Complex getS34(){
		return new Complex(0, x) ; // j*x
	}

	/**
	 * new methods for calculating ports
	 */

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

	public Complex[] getAllPorts(){
		return new Complex[] {port1_accumulated, port2_accumulated, port3_accumulated, port4_accumulated} ;
	}

	//********************************************
	
	public Complex getPort1(Complex port1In, Complex port2In, Complex port3In, Complex port4In){
		Complex T1 = port1In.times(getS11()) ;
		Complex T2 = port2In.times(getS12()) ;
		Complex T3 = port3In.times(getS13()) ;
		Complex T4 = port4In.times(getS14()) ;
		return T1.plus(T2).plus(T3).plus(T4) ;
	}

	public Complex getPort2(Complex port1In, Complex port2In, Complex port3In, Complex port4In){
		Complex T1 = port1In.times(getS21()) ;
		Complex T2 = port2In.times(getS22()) ;
		Complex T3 = port3In.times(getS23()) ;
		Complex T4 = port4In.times(getS24()) ;
		return T1.plus(T2).plus(T3).plus(T4) ;
	}

	public Complex getPort3(Complex port1In, Complex port2In, Complex port3In, Complex port4In){
		Complex T1 = port1In.times(getS31()) ;
		Complex T2 = port2In.times(getS32()) ;
		Complex T3 = port3In.times(getS33()) ;
		Complex T4 = port4In.times(getS34()) ;
		return T1.plus(T2).plus(T3).plus(T4) ;
	}

	public Complex getPort4(Complex port1In, Complex port2In, Complex port3In, Complex port4In){
		Complex T1 = port1In.times(getS41()) ;
		Complex T2 = port2In.times(getS42()) ;
		Complex T3 = port3In.times(getS43()) ;
		Complex T4 = port4In.times(getS44()) ;
		return T1.plus(T2).plus(T3).plus(T4) ;
	}

	public Complex[] getAllPorts(Complex port1In, Complex port2In, Complex port3In, Complex port4In){
		Complex port1_out = getPort1(port1In, port2In, port3In, port4In) ;
		Complex port2_out = getPort2(port1In, port2In, port3In, port4In) ;
		Complex port3_out = getPort3(port1In, port2In, port3In, port4In) ;
		Complex port4_out = getPort4(port1In, port2In, port3In, port4In) ;
		Complex[] outPorts = {port1_out, port2_out, port3_out, port4_out} ;
		return outPorts ;
	}

}
