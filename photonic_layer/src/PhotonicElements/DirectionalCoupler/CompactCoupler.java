package PhotonicElements.DirectionalCoupler;

import PhotonicElements.Utilities.MathLibraries.Complex;
import ch.epfl.general_libraries.clazzes.ParamName;

public class CompactCoupler {

	double t ;
	double kappa ;
	double couplerLossdB ;

	public Complex port1, port2, port3, port4 ;
	Complex port1_accumulated, port2_accumulated, port3_accumulated, port4_accumulated ;

	//***********************************************************************************
// This constructor for the general case: "Compact Lossy coupler that can be either 50/50 or not"
	public CompactCoupler(
			@ParamName(name="Coupler Loss (dB)", default_="0") double couplerLossdB,
			@ParamName(name="Is 50/50 Coupler?", default_="false") boolean is5050,
			@ParamName(name="kappa coefficient") double kappa
			){
		if(is5050){
			this.couplerLossdB = couplerLossdB ;
			double couplerLoss = Math.pow(10, -couplerLossdB/10) ;
			this.t = Math.sqrt(couplerLoss/2) ;
			this.kappa = this.t ;
		}
		else{
			this.kappa = kappa ;
			this.couplerLossdB = couplerLossdB ;
			double couplerLoss = Math.pow(10, -couplerLossdB/10) ;
			this.t = Math.sqrt(couplerLoss - kappa*kappa) ;
		}

		initializePorts();
	}
	//***********************************************************************************
// This constructor for Lossy 50/50 coupler
	public CompactCoupler(
			@ParamName(name="Coupler Loss (dB)", default_="0") double couplerLossdB,
			@ParamName(name="Is 50/50 Coupler?", default_="true") boolean is5050
			){
		this.couplerLossdB = couplerLossdB ;
		double couplerLoss = Math.pow(10, -couplerLossdB/10) ;
		this.t = Math.sqrt(couplerLoss/2) ;
		this.kappa = Math.sqrt(couplerLoss/2) ;

		initializePorts();
	}
	//***********************************************************************************
// This constructor for lossLess coupler (used for Ring-waveguide coupling region)
	public CompactCoupler(
			@ParamName(name="kappa coefficient") double kappa
			){
		this.kappa = kappa ;
		this.t = Math.sqrt(1-kappa*kappa) ;
		this.couplerLossdB = 0 ;

		initializePorts();
	}
	//***********************************************************************************
	public double getKappa(){
		return kappa ;
	}

	public double getT(){
		return t ;
	}

	public double getLossdB(){
		return couplerLossdB ;
	}
	//***********************************************************************************
	// Reflection at each port
	public Complex getS11(){
		return new Complex(0,0) ;
	}

	public Complex getS22(){
		return new Complex(0,0) ;
	}

	public Complex getS33(){
		return new Complex(0,0) ;
	}

	public Complex getS44(){
		return new Complex(0,0) ;
	}

	// No cross return loss
	public Complex getS23(){
		return new Complex(0,0) ;
	}

	public Complex getS32(){
		return new Complex(0,0) ;
	}

	public Complex getS14(){
		return new Complex(0,0) ;
	}

	public Complex getS41(){
		return new Complex(0,0) ;
	}

	// through path coupling
	public Complex getS12(){
		return new Complex(t,0) ;
	}

	public Complex getS21(){
		return new Complex(t,0) ;
	}

	public Complex getS34(){
		return new Complex(t,0) ;
	}

	public Complex getS43(){
		return new Complex(t,0) ;
	}

	// cross path coupling
	public Complex getS31(){
		return new Complex(0, -1*kappa) ;
	}

	public Complex getS13(){
		return new Complex(0, -1*kappa) ;
	}

	public Complex getS24(){
		return new Complex(0, -1*kappa) ;
	}

	public Complex getS42(){
		return new Complex(0, -1*kappa) ;
	}

	/**
	 * new methods for easy connection
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

	//***********************************************************************************

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

}
