package PhotonicElements.DirectionalCoupler.RingWgCoupling;

import PhotonicElements.Utilities.MathLibraries.Complex;
import PhotonicElements.Utilities.MathLibraries.ComplexMatrix;
import PhotonicElements.Utilities.Wavelength;
import PhotonicElements.Waveguides.WaveguideProperties.WgProperties;
import ch.epfl.general_libraries.clazzes.ParamName;
import flanagan.interpolation.CubicSpline;

public class RingWgCouplerNonSymmetric {

	// step 1: I need to define the non-uniform gap equation
	// step 2: I need to discretize the coupling region
	// step 3: I need to use the S-parameters of each small region to find the transfer matrix

	double ZminMicron, ZmaxMicron ;
	double gapNm, lengthMicron , radiusMicron, widthMicron ;
	double gapMaxNm = 500 ;
	Wavelength inputLambda ;
	WgProperties wgProp ;
	ComplexMatrix Mmatrix, MInverseMatrix ;

	double neffEven = Double.NaN, neffOdd = Double.NaN ;

	CubicSpline neffEvenGapInterpolator, neffOddGapInterpolator  ;

	ComplexMatrix scattMatrix = new ComplexMatrix(2,2) ;
	Complex zero = new Complex(0,0) , one = new Complex(1,0), plusJ = new Complex(0,1), minusJ = new Complex(0,-1) ;

	public RingWgCouplerNonSymmetric(
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
		this.radiusMicron = radiusMicron ;
		this.ZmaxMicron = Math.sqrt( (gapMaxNm-gapNm)/1000 * (2*(radiusMicron+widthMicron/2)-(gapMaxNm-gapNm)/1000) ) ;
		this.ZminMicron = -this.ZmaxMicron ;
		this.neffEvenGapInterpolator = neffEvenGapInterpolator ;
		this.neffOddGapInterpolator = neffOddGapInterpolator ;
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

	public void setUpMmatrix(double[][] d){
		Mmatrix = new ComplexMatrix(d) ;
		MInverseMatrix = getInverse(Mmatrix) ;
	}
	
	private ComplexMatrix getInverse(ComplexMatrix M){
		Complex det = M.getElement(0, 0).times(M.getElement(1, 1)).minus(M.getElement(0, 1).times(M.getElement(1, 0))) ;
		Complex[][] mInv = new Complex[2][2] ;
		mInv[0][0] = M.getElement(1, 1).divides(det) ;
		mInv[1][1] = M.getElement(0, 0).divides(det) ;
		mInv[0][1] = M.getElement(0, 1).times(-1).divides(det) ;
		mInv[1][0] = M.getElement(1, 0).times(-1).divides(det) ;
		return new ComplexMatrix(mInv) ;
	}
	
	private ComplexMatrix getEMatrix(double gap_nm, double DzMicron){
		Complex[][] d = new Complex[2][2] ;
		double neff_mode1 = neffEvenGapInterpolator.interpolate(gap_nm) ;
		double neff_mode2 = neffOddGapInterpolator.interpolate(gap_nm) ;
		double beta1 = inputLambda.getK0() * neff_mode1 ;
		double beta2 = inputLambda.getK0() * neff_mode2 ;
		d[0][0] = minusJ.times(beta1 * DzMicron*1e-6).exp() ;
		d[1][1] = minusJ.times(beta2 * DzMicron*1e-6).exp() ;
		d[0][1] = zero ;
		d[1][0] = zero ;
		return new ComplexMatrix(d) ;
	}
	
	public void calculate(){
		scattMatrix = getScattMatrix() ;
	}


	// this method defines the non-uniform gap equation
	public double getCouplingGapNm(double z){
		double gMicron = gapNm/1000 + radiusMicron+widthMicron/2 - Math.sqrt((radiusMicron+widthMicron/2)*(radiusMicron+widthMicron/2) - z * z) ;
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
			g[i] = getCouplingGapNm(Zmid[i]) ;
		}
		Complex[][] d = {{new Complex(1,0), new Complex(0,0)}, {new Complex(0,0), new Complex(1,0)}} ;
		ComplexMatrix T = new ComplexMatrix(d) ;
		for(int i=0; i<N; i++){
			ComplexMatrix Mi = MInverseMatrix.times(getEMatrix(g[i], Dz)).times(Mmatrix) ;
			T = T.times(Mi) ;
		}
		return T ;
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
	
	// ************ for test
//	public static void main(String[] args){
//    	double[] gapNm = {50.0, 59.183673469387755102, 68.367346938775510204, 77.551020408163265306, 86.734693877551020408, 95.91836734693877551, 105.10204081632653061, 114.28571428571428571, 123.46938775510204082, 132.65306122448979592, 141.83673469387755102, 151.02040816326530612, 160.20408163265306122, 169.38775510204081633, 178.57142857142857143, 187.75510204081632653, 196.93877551020408163, 206.12244897959183673, 215.30612244897959184, 224.48979591836734694, 233.67346938775510204, 242.85714285714285714, 252.04081632653061224, 261.22448979591836735, 270.40816326530612245, 279.59183673469387755, 288.77551020408163265, 297.95918367346938776, 307.14285714285714286, 316.32653061224489796, 325.51020408163265306, 334.69387755102040816, 343.87755102040816327, 353.06122448979591837, 362.24489795918367347, 371.42857142857142857, 380.61224489795918367, 389.79591836734693878, 398.97959183673469388, 408.16326530612244898, 417.34693877551020408, 426.53061224489795918, 435.71428571428571429, 444.89795918367346939, 454.08163265306122449, 463.26530612244897959, 472.44897959183673469, 481.6326530612244898, 490.8163265306122449, 500.0} ;
//    	double[] neff_mode1 = {2.8881272538902997837, 2.8839136220538508226, 2.8808250429925057468, 2.8785197325939289037, 2.8767782147014941962, 2.8754248555054244818, 2.8743660961361983119, 2.8735465296334843366, 2.8728960865590691398, 2.8723808931455017124, 2.8719653908686764154, 2.8716368806242558165, 2.8713721839117596346, 2.8711577479475067776, 2.8709857548471129363, 2.8708483597058656045, 2.8707356773374237413, 2.8706411414558576922, 2.870568140432113946, 2.8705095307218124745, 2.8704607253644023324, 2.8704213967171936517, 2.8703902967277010738, 2.8703678530738279129, 2.8703484469884918973, 2.8703266843327654989, 2.8703124193550428878, 2.870302320528554052, 2.870294986532924586, 2.8702883786289374868, 2.870280756992078075, 2.8702760464438843435, 2.8702715212094567221, 2.8702687832243669597, 2.8702623336222372608, 2.8702580508299604389, 2.8702562215930176315, 2.8702552746937808337, 2.8702555932632849434, 2.8702586378323267091, 2.8702583650145627736, 2.8702530322650385841, 2.8702541423240499618, 2.8702530165719997157, 2.8702509502582111978, 2.8702562257781965016, 2.8702549927146039366, 2.8702548396831795152, 2.8702521319539311406, 2.8702553523675224945} ;
//    	CubicSpline neff_mode1_interpolate = new CubicSpline(gapNm, neff_mode1) ;
//    	double[] neff_mode2 = {2.5908313552863253548, 2.5833683193049923332, 2.5774183777489940717, 2.5726308828605271017, 2.5687870275988622382, 2.5656177665168540258, 2.5630361093155373808, 2.5609363848843567091, 2.559176718308418863, 2.5577191133232233256, 2.5564983760672257418, 2.5554927375350549035, 2.5546509523453457824, 2.553937909384206062, 2.5533438128382233145, 2.5528679639980662586, 2.5524392910080462293, 2.5520884021978038625, 2.5517950156311264287, 2.5515524474416064749, 2.551333143177021423, 2.5511524648047454455, 2.5510131419745438741, 2.5508801396351405977, 2.5507910102998447854, 2.5506868225132768302, 2.5506105362494495559, 2.5505446239334670366, 2.5504905497717316187, 2.5504500580378928198, 2.550410563583707102, 2.5503698036900774859, 2.5503594196254626425, 2.550325796191354577, 2.5503094368891390253, 2.5502884780971815104, 2.5502656579383398139, 2.5502554317906405146, 2.5502603944634971711, 2.5502380239498156911, 2.5502262290092336805, 2.5502198626095577971, 2.5501974143139145568, 2.5501952627185366929, 2.5502086491752851138, 2.5502049556680232989, 2.5502076548154257729, 2.5502109921984565943, 2.5501943103780271294, 2.5501982137000571704} ;
//    	CubicSpline neff_mode2_interpolate = new CubicSpline(gapNm, neff_mode2) ;
//    	WgProperties wgProp = new WgProperties(1, 0, 1, null) ;
//    	RingWgCouplerNonSymmetric rwDC = new RingWgCouplerNonSymmetric(new Wavelength(1310), wgProp, 450, 5, 100, 500, neff_mode1_interpolate, neff_mode2_interpolate) ;
//    	rwDC.setUpMmatrix(new double[][] {{0.6982, 0.1016}, {-0.0353, 0.6497}});
//    	rwDC.calculate();
//    	rwDC.Mmatrix.show();
//    	rwDC.MInverseMatrix.show();
//    	rwDC.MInverseMatrix.times(rwDC.Mmatrix).show();
//    	rwDC.getEMatrix(200, 1).show();
//    	rwDC.getScattMatrix().show();
//    	double[] gap_nm = MoreMath.linspace(50, 500, 1000) ;
//    	double[] neff1 = {} ;
//    	double[] neff2 = {} ;
//    	for(int i=0; i<gap_nm.length; i++){
//    		neff1 = MoreMath.Arrays.append(neff1, neff_mode1_interpolate.interpolate(gap_nm[i])) ;
//    		neff2 = MoreMath.Arrays.append(neff2, neff_mode2_interpolate.interpolate(gap_nm[i])) ;
//    	}
//    	MatlabChart fig = new MatlabChart() ;
//    	fig.plot(gap_nm, neff1);
//    	fig.plot(gap_nm, neff2, "r-o");
//    	fig.RenderPlot();
//    	fig.run(true);
//	}

}
