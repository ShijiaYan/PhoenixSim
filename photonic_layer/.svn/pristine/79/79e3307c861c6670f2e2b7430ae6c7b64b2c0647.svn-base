package PhotonicElements.DirectionalCoupler.RingWgCoupling;

import People.Meisam.GUI.Plotters.MatlabPlot.MatlabChart;
import PhotonicElements.DirectionalCoupler.DistributedCoupler.DistributedCouplerStripWg;
import PhotonicElements.Utilities.Wavelength;
import PhotonicElements.Utilities.MathLibraries.Complex;
import PhotonicElements.Utilities.MathLibraries.ComplexMatrix;
import PhotonicElements.Utilities.MathLibraries.MoreMath;
import PhotonicElements.Waveguides.WaveguideProperties.WgProperties;
import ch.epfl.general_libraries.clazzes.ParamName;
import flanagan.interpolation.CubicSpline;

public class RingRingCoupler {

	double ZminMicron, ZmaxMicron ;
	double gapNm, lengthMicron , radiusMicron, widthMicron = 0.45 ;
	double gapMaxNm = 500 ;
	Wavelength inputLambda ;
	WgProperties wgProp ;

	double neffEven = Double.NaN, neffOdd = Double.NaN ;

	CubicSpline neffEvenGapInterpolator, neffOddGapInterpolator  ;

	ComplexMatrix scattMatrix = new ComplexMatrix(2,2) ;
	Complex zero = new Complex(0,0) , one = new Complex(1,0) ;
	// constructor to initialize the class
	public RingRingCoupler(
			Wavelength inputLambda,
			WgProperties wgProp,
			@ParamName(name="Radius (micron)") double radiusMicron,
			@ParamName(name="gap size (nm)") double gapNm
			){
		this.inputLambda = inputLambda ;
		this.wgProp = wgProp ;
		this.gapNm = gapNm ;
		this.gapMaxNm = 500 ;
		this.radiusMicron = radiusMicron ; // need to modify this later
		this.ZmaxMicron = Math.sqrt( (gapMaxNm/2-gapNm/2)/1000 * (2*radiusMicron-(gapMaxNm/2-gapNm/2)/1000) ) ; // correct this...
		this.ZminMicron = -this.ZmaxMicron ;
		this.scattMatrix = getScattMatrix() ;
	}

	public RingRingCoupler(
			Wavelength inputLambda,
			WgProperties wgProp,
			@ParamName(name="Waveguide width (nm)") double widthNm,
			@ParamName(name="Radius (micron)") double radiusMicron,
			@ParamName(name="gap size (nm)") double gapNm,
			@ParamName(name="max coupling gap (nm)") double gapMaxNm,
			CubicSpline neffEvenGapInterpolator,
			CubicSpline neffOddGapInterpolator
			){
		this.inputLambda = inputLambda ;
		this.wgProp = wgProp ;
		this.gapNm = gapNm ;
		this.gapMaxNm = gapMaxNm ;
		this.widthMicron = widthNm*1e-3 ;
		this.radiusMicron = radiusMicron ; // need to modify this later
		this.ZmaxMicron = Math.sqrt( (gapMaxNm/2-gapNm/2)/1000 * (2*(radiusMicron+widthMicron/2)-(gapMaxNm/2-gapNm/2)/1000) ) ;
		this.ZminMicron = -this.ZmaxMicron ;
		this.neffEvenGapInterpolator = neffEvenGapInterpolator ;
		this.neffOddGapInterpolator = neffOddGapInterpolator ;
		this.scattMatrix = getScattMatrix() ;
	}

	public RingRingCoupler(
			Wavelength inputLambda,
			WgProperties wgProp,
			@ParamName(name="Waveguide width (nm)") double widthNm,
			@ParamName(name="Radius (micron)") double radiusMicron,
			@ParamName(name="gap size (nm)") double gapNm,
			CubicSpline neffEvenGapInterpolator,
			CubicSpline neffOddGapInterpolator
			){
		this.inputLambda = inputLambda ;
		this.wgProp = wgProp ;
		this.gapNm = gapNm ;
		this.gapMaxNm = 500 ;
		this.widthMicron = widthNm*1e-3 ;
		this.radiusMicron = radiusMicron ; // need to modify this later
		this.ZmaxMicron = Math.sqrt( (gapMaxNm/2-gapNm/2)/1000 * (2*(radiusMicron+widthMicron/2)-(gapMaxNm/2-gapNm/2)/1000) ) ;
		this.ZminMicron = -this.ZmaxMicron ;
		this.neffEvenGapInterpolator = neffEvenGapInterpolator ;
		this.neffOddGapInterpolator = neffOddGapInterpolator ;
		this.scattMatrix = getScattMatrix() ;
	}

	public double getRadiusMicron(){
		return radiusMicron ;
	}

	public double getWavelengthNm(){
		return inputLambda.getWavelengthNm() ;
	}

	public double getGapSizeNm(){
		return gapNm ;
	}

	public double getZmin(){
		return ZminMicron ;
	}

	public double getZmax(){
		return ZmaxMicron ;
	}



	// these methods get the parameters for the small coupling regions
	private Complex S21(double z, double Dz){
		DistributedCouplerStripWg DC ;
		if(neffEvenGapInterpolator != null && neffOddGapInterpolator != null){
			neffEven = neffEvenGapInterpolator.interpolate(getCouplingGapNm(z)) ;
			neffOdd = neffOddGapInterpolator.interpolate(getCouplingGapNm(z)) ;
			DC = new DistributedCouplerStripWg(inputLambda, wgProp, Dz, getCouplingGapNm(z), neffEven, neffOdd) ;
		}
		else{
			DC = new DistributedCouplerStripWg(inputLambda, wgProp, Dz, getCouplingGapNm(z), neffEven, neffOdd) ;
		}
		return DC.S21 ;
	}

	private Complex S31(double z, double Dz){
		DistributedCouplerStripWg DC ;
		if(neffEvenGapInterpolator != null && neffOddGapInterpolator != null){
			neffEven = neffEvenGapInterpolator.interpolate(getCouplingGapNm(z)) ;
			neffOdd = neffOddGapInterpolator.interpolate(getCouplingGapNm(z)) ;
			DC = new DistributedCouplerStripWg(inputLambda, wgProp, Dz, getCouplingGapNm(z), neffEven, neffOdd) ;
		}
		else{
			DC = new DistributedCouplerStripWg(inputLambda, wgProp, Dz, getCouplingGapNm(z), neffEven, neffOdd) ;
		}
		return DC.S31 ;
	}

	private Complex S24(double z, double Dz){
		DistributedCouplerStripWg DC ;
		if(neffEvenGapInterpolator != null && neffOddGapInterpolator != null){
			neffEven = neffEvenGapInterpolator.interpolate(getCouplingGapNm(z)) ;
			neffOdd = neffOddGapInterpolator.interpolate(getCouplingGapNm(z)) ;
			DC = new DistributedCouplerStripWg(inputLambda, wgProp, Dz, getCouplingGapNm(z), neffEven, neffOdd) ;
		}
		else{
			DC = new DistributedCouplerStripWg(inputLambda, wgProp, Dz, getCouplingGapNm(z), neffEven, neffOdd) ;
		}
		return DC.S24 ;
	}

	private Complex S34(double z, double Dz){
		DistributedCouplerStripWg DC ;
		if(neffEvenGapInterpolator != null && neffOddGapInterpolator != null){
			neffEven = neffEvenGapInterpolator.interpolate(getCouplingGapNm(z)) ;
			neffOdd = neffOddGapInterpolator.interpolate(getCouplingGapNm(z)) ;
			DC = new DistributedCouplerStripWg(inputLambda, wgProp, Dz, getCouplingGapNm(z), neffEven, neffOdd) ;
		}
		else{
			DC = new DistributedCouplerStripWg(inputLambda, wgProp, Dz, getCouplingGapNm(z), neffEven, neffOdd) ;
		}
		return DC.S34 ;
	}

	// this method defines the non-uniform gap equation
	public double getCouplingGapNm(double z){
		double gMicron = 2*(0.5*gapNm/1000 + radiusMicron+widthMicron/2 - Math.sqrt((radiusMicron+widthMicron/2)*(radiusMicron+widthMicron/2) - z * z)) ;
		double gNm = gMicron * 1000 ;
		return gNm ;
	}
	// Now finally calculate the scattering parameters
	private ComplexMatrix getScattMatrix(){
		int N = 100 ;
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

	//***** for test
	public static void main(String[] args){
		int M = 1000 ;
		double[] zMicron = MoreMath.linspace(-2, 2, M) ;
		double[] gapNm = new double[M] ;
		RingRingCoupler rrc = new RingRingCoupler(new Wavelength(1550), new WgProperties(1, 1, 1, null), 5, 200) ;
		for(int i=0; i<M; i++){
			gapNm[i] = rrc.getCouplingGapNm(zMicron[i])/2 ;
		}
		MatlabChart fig = new MatlabChart() ;
		fig.plot(zMicron, gapNm, "b", 3f);
		fig.plot(zMicron, MoreMath.Arrays.times(gapNm, -1), "r", 3f);
		fig.RenderPlot();
		fig.run(true);
	}
	//*****

}
