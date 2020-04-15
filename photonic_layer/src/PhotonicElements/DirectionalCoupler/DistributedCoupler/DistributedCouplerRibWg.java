package PhotonicElements.DirectionalCoupler.DistributedCoupler;

import PhotonicElements.Utilities.MathLibraries.Complex;
import PhotonicElements.Utilities.Wavelength;
import PhotonicElements.Waveguides.WaveguideProperties.WgProperties;
import ch.epfl.general_libraries.clazzes.ParamName;

//This class is for 450nmX250nmX50nm Rib waveguides

public class DistributedCouplerRibWg {

	double lengthMicron ;
	double gapNm ;
	double lambdaNm ;
	Wavelength inputLambda ;
	WgProperties wgProp ;
	
	
	public DistributedCouplerRibWg(
			Wavelength inputLambda,
			WgProperties wgProp,
			@ParamName(name="Length (micron)") double lengthMicron,
			@ParamName(name="gap (nm)") double gapNm
			){
		this.inputLambda = inputLambda ;
		this.lambdaNm = inputLambda.getWavelengthNm() ;
		this.lengthMicron = lengthMicron ;
		this.gapNm = gapNm ;
		this.wgProp = wgProp ;
	}
	
	public double getLengthMicron(){
		return lengthMicron ;
	}
	
	public double getGapSizeNm(){
		return gapNm ;
	}
	
	public double getWavelengthNm(){
		return lambdaNm ;
	}
	
	// First I need to read gap and wavelength
	// Then I need to determine the wavelength interval and weights
	// Then I need to calculate the even & odd effective indexes based on gap interpolations and weights on the wavelengths
	// Finally I need to interpolate the wavelength dependence for a dense simulation
	
	public double getEffectiveIndexEven(){
		double[] coeffs = getCoeffs(true) ;
		double nEffEven = 0 ;
		for(int i=0; i<9; i++){
			nEffEven += coeffs[i] * Math.pow(gapNm, 8-i) ;
		}
		return nEffEven ;
	}
	
	public double getEffectiveIndexOdd(){
		double[] coeffs = getCoeffs(false) ;
		double nEffOdd = 0 ;
		for(int i=0; i<9; i++){
			nEffOdd += coeffs[i] * Math.pow(gapNm, 8-i) ;
		}
		return nEffOdd ;
	}
	
	public double getBetaPlus(){
		double lambdaMeter = inputLambda.getWavelengthMeter() ;
		double neffEven = getEffectiveIndexEven() ;
		double neffOdd = getEffectiveIndexOdd() ;
		double betaPlus = (2*Math.PI/lambdaMeter) * (neffEven + neffOdd)/2 ;
		return betaPlus ;
	}
	
	public double getBetaMinus(){
		double lambdaMeter = inputLambda.getWavelengthMeter() ;
		double neffEven = getEffectiveIndexEven() ;
		double neffOdd = getEffectiveIndexOdd() ;
		double betaPlus = (2*Math.PI/lambdaMeter) * (neffEven - neffOdd)/2 ;
		return betaPlus ;
	}
	
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
	
	public Complex getS14(){
		return new Complex(0,0) ;
	}
	
	public Complex getS41(){
		return new Complex(0,0) ;
	}
	
	public Complex getS23(){
		return new Complex(0,0) ;
	}
	
	public Complex getS32(){
		return new Complex(0,0) ;
	}
	
	public Complex getS21(){
		double alpha = wgProp.getWgPropLossPerMeter()/2 ; // this is for electric field
		Complex phi_plus = new Complex(getBetaPlus() * lengthMicron*1e-6, -alpha*lengthMicron*1e-6) ;
		Complex T1 = phi_plus.times(new Complex(0,-1)).exp() ;
		double phi_minus = getBetaMinus() * lengthMicron*1e-6 ;
		Complex C = new Complex(Math.cos(phi_minus),0) ;
		Complex S21 = T1.times(C) ;
		return S21 ;
		 
	}
	
	public Complex getS12(){
		return getS21() ;
	}
	
	public Complex getS34(){
		return getS21() ;
	}
	
	public Complex getS43(){
		return getS34() ;
	}
	
	public Complex getS31(){
		double alpha = wgProp.getWgPropLossPerMeter()/2 ; // this is for electric field
		Complex phi_plus = new Complex(getBetaPlus() * lengthMicron*1e-6, -alpha*lengthMicron*1e-6) ;
		Complex T1 = phi_plus.times(new Complex(0,-1)).exp() ;
		double phi_minus = getBetaMinus() * lengthMicron*1e-6 ;
		Complex S = new Complex(0,-Math.sin(phi_minus)) ;
		Complex S31 = T1.times(S) ;
		return S31 ;
	}
	
	public Complex getS13(){
		return getS31() ;
	}
	
	public Complex getS24(){
		return getS31() ;
	}
	
	public Complex getS42(){
		return getS24() ;
	}
	
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
	
	//*************************************
	
	public double[] getCoeffs(boolean isEven){
//		double lambda_min_interval = 0 ; 
//		double lambda_max_interval = 0 ;
		double lambdaMin = 1300 ;
		double lambdaMax = 1300 ;
		double DlambdaNm = 2.5 ; // resolution is 2.5 nm
		int N = 41 ;
		int intervalIndex = 0 ;
		double[] lambdaArray = new double[N] ;
		for(int i=0; i<N; i++){
			lambdaArray[i] = lambdaMin + i * DlambdaNm ;
		}
		if(lambdaNm >= lambdaMax){
//			lambda_min_interval = lambdaArray[N-2] ;
//			lambda_max_interval = lambdaArray[N-1] ;
			intervalIndex = N-1 ;
		}
		else if(lambdaNm <= lambdaMin){
//			lambda_min_interval = lambdaArray[0] ;
//			lambda_max_interval = lambdaArray[1] ;
			intervalIndex = 1 ;
		}
		else{
			for(int i=0; i<N-1; i++){
				if(lambdaNm >= lambdaArray[i] && lambdaNm < lambdaArray[i+1] ){
//					lambda_min_interval = lambdaArray[i] ;
//					lambda_max_interval = lambdaArray[i+1] ;
					intervalIndex = i+1 ;
				}
			}
		}
//		double weight = 1 - ((lambdaNm-lambda_min_interval)/(DlambdaNm)) ;
//		double weight = 1 ;
		
		double[] coeffs = new double[9] ;
		if(isEven){
			for(int i=0; i<9; i++){
//			coeffs[i] = weight*getCoeffsFromDataBase(intervalIndex, true)[i] + (1-weight)*getCoeffsFromDataBase(intervalIndex+1, true)[i] ;
			coeffs[i] = getCoeffsFromDataBase(intervalIndex, true)[i] ;
			}
		}
		else{
			for(int i=0; i<9; i++){
//			coeffs[i] = weight*getCoeffsFromDataBase(intervalIndex, false)[i] + (1-weight)*getCoeffsFromDataBase(intervalIndex+1, false)[i] ;
			coeffs[i] = getCoeffsFromDataBase(intervalIndex, false)[i] ;
			}
		}
		
		return coeffs ;
	}
	
	// DataBase from the fitted curves (index vs. gap size) simulated in COMSOL --> 8-degree polynomial (A8*x^8 + ... + A1*x + A0) 
/*	public double[] getCoeffsFromDataBase(int curveIndex, boolean isEven){
			if(isEven){return new double[] {2.1324200152928423557e-21, -0.000000000000000005201945501999353486, 0.000000000000005414586429067718802, -0.0000000000031429537030871226294, 0.0000000011167416259635000947, -0.00000025117335644522422365, 0.000035731980484896622683, -0.0030845186169626390323, 3.0169947991580872149
};} 
			else{return new double[] { 2.4478552634751453294e-23, -0.000000000000000000078432361268221597072, 0.00000000000000010389046826891352198, -0.000000000000073649118549081421768, 0.000000000029496753773068726392, -0.0000000059857908428999466296, 0.00000014348136938048992761, 0.00018283428305054460973, 2.8551515886746545725
};}
		}*/
		
	// this oen is for 1550nm (w=450nm)
	public double[] getCoeffsFromDataBase(int curveIndex, boolean isEven){
		if(isEven){return new double[] {1.5659441024604786614e-21, -0.0000000000000000038935547963965313252, 0.0000000000000041562625080555672796, -0.0000000000024980972335978106487, 0.00000000093362360383512451469, -0.00000022690844758333887493, 0.000036601950507340239277, -0.0038916193525806649922, 2.957926899164923018
				 };} 
		else{return new double[] {7.5633934632282092614e-24, -0.000000000000000000030005429124574613256, 0.000000000000000049721526050237535578, -0.000000000000044430996750633441268, 0.00000000002254961911727270944, -0.0000000057642429667836882409, 0.000000084731389906296257, 0.00034470209547403498967, 2.6570337959897329938
				 };}
	}
	
	// this oen is for 1550nm (w=500nm)
/*	public double[] getCoeffsFromDataBase(int curveIndex, boolean isEven){
		if(isEven){return new double[] {1.5042452912105317524e-21, -0.0000000000000000037374037066480518319, 0.0000000000000039816023222269813819, -0.0000000000023832538084121450514, 0.00000000088383069815424546939, -0.00000021181605302478108968, 0.000033324320666965292939, -0.0033964063545997161611, 2.9763297230219456502
				 };} 
		else{return new double[] {-1.5045508244307954399e-23, 0.00000000000000000002323670166382584203, -0.0000000000000000046990032783005349538, -0.000000000000012563086397058954818, 0.000000000010899097442639446429, -0.0000000032654765946194898958, -0.000000051134745720707362475, 0.00026305840792574032051, 2.7350120344737156586
				 };}
	}*/
	
	
	
	
	
	
	
	
}
