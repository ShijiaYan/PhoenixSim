package PhotonicElements.Junctions.Yjunctions;

import PhotonicElements.Utilities.MathLibraries.Complex;
import ch.epfl.general_libraries.clazzes.ParamName;

public class Yjunction {
	
/*
			   /========= port 2
	==========/
	port 1	  \
			   \========= port 3
*/
	
	public double junctionLossdB ; // this is the total loss (port2+port3) from port1
	public double junctionLoss ;
	public double fieldLoss ;
	
	public Complex port1, port2, port3 ;
	Complex port1_accumulated, port2_accumulated, port3_accumulated ;
	
	public Yjunction(
			@ParamName(name="Specify Junction Loss (dB)", default_="0") double junctionLossdB
			){
		this.junctionLossdB = junctionLossdB ;
		this.junctionLoss = Math.pow(10, -junctionLossdB/20) ;
		this.fieldLoss = Math.sqrt(this.junctionLoss) ; 
		
		initializePorts();
	}
	
	public Yjunction(){
		this.junctionLossdB = 0 ;
		this.junctionLoss = Math.pow(10, -junctionLossdB/20) ;
		this.fieldLoss = Math.sqrt(this.junctionLoss) ; 
		
		initializePorts();
	}
	
	// The Y-junction structure is reciprocal, therefore: S(i,j) = S(j,i)
	// only the main input port can be perfectly matched
	
	public Complex getS11(){
		return new Complex(0,0).times(fieldLoss) ;
	}
	
	public Complex getS12(){
		return new Complex(1/Math.sqrt(2), 0).times(fieldLoss) ;
	}
	
	public Complex getS21(){
		return new Complex(1/Math.sqrt(2), 0).times(fieldLoss) ;
	}
	
	public Complex getS13(){
		return new Complex(1/Math.sqrt(2), 0).times(fieldLoss) ;
	}
	
	public Complex getS31(){
		return new Complex(1/Math.sqrt(2), 0).times(fieldLoss) ;
	}
	
	public Complex getS22(){
		return new Complex(1/2, 0).times(fieldLoss) ;
	}
	
	public Complex getS33(){
		return new Complex(1/2, 0).times(fieldLoss) ;
	}
	
	public Complex getS23(){
		return new Complex(-1/2, 0).times(fieldLoss) ; // minus sign
	}
	
	public Complex getS32(){
		return new Complex(-1/2, 0).times(fieldLoss) ; // minus sign
	}
	
	/**
	 *  new methods for connections
	 */
	
	public void initializePorts(){
		port1 = Complex.ZERO ;
		port2 = Complex.ZERO ;
		port3 = Complex.ZERO ;
		port1_accumulated = Complex.ZERO ;
		port2_accumulated = Complex.ZERO ;
		port3_accumulated = Complex.ZERO ;
	}
	
	public void connectPorts(Complex port1In, Complex port2In, Complex port3In){
		port1 = getPort1(port1In, port2In, port3In) ;
		port2 = getPort2(port1In, port2In, port3In) ;
		port3 = getPort3(port1In, port2In, port3In) ;
		port1_accumulated = port1_accumulated.plus(port1) ;
		port2_accumulated = port2_accumulated.plus(port2) ;
		port3_accumulated = port3_accumulated.plus(port3) ;
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
	
	public Complex[] getAllPorts(){
		return new Complex[] {port1_accumulated, port2_accumulated, port3_accumulated} ;
	}
	
	// Now characterizing the ports 
	
	@Deprecated
	public Complex getPort1(Complex port1In, Complex port2In, Complex port3In){
		Complex T1 = port1In.times(getS11()) ;
		Complex T2 = port2In.times(getS12()) ;
		Complex T3 = port3In.times(getS13()) ;
		return T1.plus(T2).plus(T3) ;
	}
	
	@Deprecated
	public Complex getPort2(Complex port1In, Complex port2In, Complex port3In){
		Complex T1 = port1In.times(getS21()) ;
		Complex T2 = port2In.times(getS22()) ;
		Complex T3 = port3In.times(getS23()) ;
		return T1.plus(T2).plus(T3) ;
	}
	
	@Deprecated
	public Complex getPort3(Complex port1In, Complex port2In, Complex port3In){
		Complex T1 = port1In.times(getS31()) ;
		Complex T2 = port2In.times(getS32()) ;
		Complex T3 = port3In.times(getS33()) ;
		return T1.plus(T2).plus(T3) ;
	}

}
