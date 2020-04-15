package PhotonicElements.DirectionalCoupler;

import PhotonicElements.DirectionalCoupler.DistributedCoupler.DistributedCouplerStripWg;
import PhotonicElements.Utilities.MathLibraries.Complex;
import PhotonicElements.Utilities.MathLibraries.ComplexMatrix;
import PhotonicElements.Utilities.Wavelength;
import PhotonicElements.Waveguides.WaveguideProperties.WgProperties;

public class WgWgCoupler {

	// step 1: I need to define the non-uniform gap equation
	// step 2: I need to discretize the coupling region
	// step 3: I need to use the S-parameters of each small region to find the transfer matrix
	
	double ZminMicron, ZmaxMicron ;
	double gapNm, lengthMicron ;
	Wavelength inputLambda ;
	WgProperties wgProp ;
	
	double neffEven = Double.NaN, neffOdd = Double.NaN ;
	
	ComplexMatrix scattMatrix = new ComplexMatrix(2,2) ;
	Complex zero = new Complex(0,0) , one = new Complex(1,0) ;
	// constructor to initialize the class
	public WgWgCoupler(
			Wavelength inputLambda,
			WgProperties wgProp,
			double lengthMicron,
			double gapNm
			){
		this.inputLambda = inputLambda ;
		this.wgProp = wgProp ;
		this.ZminMicron = 0 ;
		this.ZmaxMicron = lengthMicron ;
		this.gapNm = gapNm ;
		this.lengthMicron = lengthMicron ;
//		this.scattMatrix = getScattMatrix() ;
	}
	
	public WgWgCoupler(
			double lambdaNm,
			WgProperties wgProp,
			double lengthMicron,
			double gapNm
			){
		this.inputLambda = new Wavelength(lambdaNm) ;
		this.wgProp = wgProp ;
		this.ZminMicron = 0 ;
		this.ZmaxMicron = lengthMicron ;
		this.gapNm = gapNm ;
		this.lengthMicron = lengthMicron ;
//		this.scattMatrix = getScattMatrix() ;
	}
	
	public void setNeffEvenAndOdd(double neffEven, double neffOdd){
		this.neffEven = neffEven ;
		this.neffOdd = neffOdd ;
		scattMatrix = getScattMatrix() ;
	}
	
	public double getLengthMicron(){
		return lengthMicron ;
	}
	
	// these methods get the parameters for the small coupling regions
	private Complex S21(double z, double Dz){
		DistributedCouplerStripWg DC = new DistributedCouplerStripWg(inputLambda, wgProp, Dz, getCouplingGapNm(z), neffEven, neffOdd) ;
		return DC.S21 ;
	}
	
	private Complex S31(double z, double Dz){
		DistributedCouplerStripWg DC = new DistributedCouplerStripWg(inputLambda, wgProp, Dz, getCouplingGapNm(z), neffEven, neffOdd) ;
		return DC.S31 ;
	}
	
	private Complex S24(double z, double Dz){
		DistributedCouplerStripWg DC = new DistributedCouplerStripWg(inputLambda, wgProp, Dz, getCouplingGapNm(z), neffEven, neffOdd) ;
		return DC.S24 ;
	}
	
	private Complex S34(double z, double Dz){
		DistributedCouplerStripWg DC = new DistributedCouplerStripWg(inputLambda, wgProp, Dz, getCouplingGapNm(z), neffEven, neffOdd) ;
		return DC.S34 ;
	}
	// this method defines the non-uniform gap equation
	private double getCouplingGapNm(double z){
		if(z>ZminMicron && z<ZmaxMicron){
			return gapNm ;
		}
		else{
			return 1000 ; // in Nm
		}
	}
	// Now finally calculate the scattering parameters
	private ComplexMatrix getScattMatrix(){
		int N = 1000 ;
		double Dz = (ZmaxMicron-ZminMicron)/N ;
		double[] Z = new double[N] ;
		double[] Zmid = new double[N] ;
		double[] g = new double[N] ;
		for(int i=0; i<N; i++){
			Z[i] = ZminMicron + i * Dz ;
			Zmid[i] = Z[i] + Dz/2 ;
			g[i] = getCouplingGapNm(Z[i]) ;
		}
		Complex[][] d = {{new Complex(1,0), new Complex(0,0)}, {new Complex(0,0), new Complex(1,0)}} ;
		ComplexMatrix M = new ComplexMatrix(d) ;
		for(int i=0; i<N; i++){
			Complex[][] di = {{S21(Zmid[i], Dz), S24(Zmid[i], Dz)}, {S31(Zmid[i], Dz), S34(Zmid[i], Dz)}} ;
			ComplexMatrix Mi = new ComplexMatrix(di) ;
			M = M.times(Mi) ;
		}
		return M ;
	}
	// Return scattering parameters
	public Complex getS11(){
		return new Complex(0,0) ;
	}
	
	public Complex getS21(){
		return scattMatrix.getElement(0, 0) ;
	}
	
	public Complex getS31(){
		return scattMatrix.getElement(1, 0) ;
	}
	
	public Complex getS41(){
		return new Complex(0,0) ;
	}
	
	public Complex getS12(){
		return getS21() ;
	}
	
	public Complex getS22(){
		return new Complex(0,0) ;
	}
	
	public Complex getS32(){
		return new Complex(0,0) ;
	}
	
	public Complex getS42(){
		return scattMatrix.getElement(0, 1) ;
	}
	
	public Complex getS13(){
		return getS31() ;
	}
	
	public Complex getS23(){
		return getS32() ;
	}
	
	public Complex getS33(){
		return new Complex(0,0) ;
	}
	
	public Complex getS43(){
		return scattMatrix.getElement(1, 1) ;
	}
	
	public Complex getS14(){
		return getS41() ;
	}
	
	public Complex getS24(){
		return getS42() ;
	}
	
	public Complex getS34(){
		return getS43() ;
	}
	
	public Complex getS44(){
		return new Complex(0,0) ;
	}
	
	// Get the ports
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
