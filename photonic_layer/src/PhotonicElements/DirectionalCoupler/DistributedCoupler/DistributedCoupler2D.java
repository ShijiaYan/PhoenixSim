package PhotonicElements.DirectionalCoupler.DistributedCoupler;

import PhotonicElements.Utilities.MathLibraries.Complex;
import PhotonicElements.Utilities.Wavelength;
import PhotonicElements.Waveguides.WaveguideProperties.WgProperties;
import ch.epfl.general_libraries.clazzes.ParamName;

//This class is for 450nm wide Slab Waveguides (2D)

public class DistributedCoupler2D {
	double lengthMicron ;
	double gapNm ;
	double lambdaNm ;
	Wavelength inputLambda ;
	WgProperties wgProp ;
	
	
	public DistributedCoupler2D(
			Wavelength inputLambda,
			WgProperties wgProp,
			@ParamName(name="Coupler length (micron)") double lengthMicron,
			@ParamName(name="gap size (nm)") double gapNm
			){
		this.inputLambda = inputLambda ;
		this.lambdaNm = inputLambda.getWavelengthNm() ;
		this.lengthMicron = lengthMicron ;
		this.gapNm = gapNm ;
		this.wgProp = wgProp ;
	}
	
	public double getWavelengthNm(){
		return inputLambda.getWavelengthNm() ;
	}
	
	public double getLengthMicron(){
		return lengthMicron ;
	}
	
	public double getGapSizeNm(){
		return gapNm ;
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
		double lambda_min_interval = 0 ; 
//		double lambda_max_interval = 0 ;
		double lambdaMin = 1500 ;
		double lambdaMax = 1600 ;
		double DlambdaNm = 2.5 ; // resolution is 2.5 nm
		int N = 41 ;
		int intervalIndex = 0 ;
		double[] lambdaArray = new double[N] ;
		for(int i=0; i<N; i++){
			lambdaArray[i] = lambdaMin + i * DlambdaNm ;
		}
		if(lambdaNm >= lambdaMax){
			lambda_min_interval = lambdaArray[N-2] ;
//			lambda_max_interval = lambdaArray[N-1] ;
			intervalIndex = N-1 ;
		}
		else if(lambdaNm <= lambdaMin){
			lambda_min_interval = lambdaArray[0] ;
//			lambda_max_interval = lambdaArray[1] ;
			intervalIndex = 1 ;
		}
		else{
			for(int i=0; i<N-1; i++){
				if(lambdaNm >= lambdaArray[i] && lambdaNm < lambdaArray[i+1] ){
					lambda_min_interval = lambdaArray[i] ;
//					lambda_max_interval = lambdaArray[i+1] ;
					intervalIndex = i+1 ;
				}
			}
		}
		double weight = 1 - (lambdaNm-lambda_min_interval)/ DlambdaNm;
		
		double[] coeffs = new double[9] ;
		if(isEven){
			for(int i=0; i<9; i++){
			coeffs[i] = weight*getCoeffsFromDataBase(intervalIndex, true)[i] + (1-weight)*getCoeffsFromDataBase(intervalIndex+1, true)[i] ;
			}
		}
		else{
			for(int i=0; i<9; i++){
			coeffs[i] = weight*getCoeffsFromDataBase(intervalIndex, false)[i] + (1-weight)*getCoeffsFromDataBase(intervalIndex+1, false)[i] ;
			}
		}
		
		return coeffs ;
	}
	
	// DataBase from the fitted curves (index vs. gap size) simulated in COMSOL --> 8-degree polynomial (A8*x^8 + ... + A1*x + A0) , lambda steps of 2.5micron
	public double[] getCoeffsFromDataBase(int curveIndex, boolean isEven){
		if(curveIndex==1){
			if(isEven){return new double[] { 5.810162832e-22, -1.482628156e-18, 1.637841611e-15, -1.03116724e-12, 0.0000000004109051027, -0.0000001091030566, 0.0000197247238, -0.002356755821, 3.403686601};} 
			else{return new double[] {2.544082699e-22, -5.990218893e-19, 6.008248376e-16, -3.309267964e-13, 0.0000000001050828002, -0.00000001669868039, -0.0000001915304262, 0.0005617153379, 3.179965374};}
		}
		else if(curveIndex==2){
			if(isEven){return new double[] {5.747848027e-22, -1.470845767e-18, 1.628776942e-15, -1.027425642e-12, 0.0000000004099632059, -0.0000001089489326, 0.00001971262564, -0.00235823941, 3.403257896}; }
			else{return new double[] {3.106364648e-22, -7.751452329e-19, 8.187122913e-16, -4.718150638e-13, 0.0000000001572201112, -0.00000002792361012, 0.000001158887812, 0.0004814963663, 3.180769555}; }
		}
		else if(curveIndex==3){
			if(isEven){return new double[] {6.00809056e-22, -1.527405817e-18, 1.68044562e-15, -1.053229436e-12, 0.0000000004176330081, -0.000000110337408, 0.00001986670728, -0.002369620137, 3.403081783}; }
			else{return new double[] {-3.398916413e-23, 4.070410006e-20, 8.904273244e-18, -3.451145353e-14, 1.780404762e-11, -0.00000000130415344, -0.000001781928458, 0.0006521084607, 3.175813938}; }
		}
		else if(curveIndex==4){
			if(isEven){return new double[] { 5.98774863e-22, -1.524922746e-18, 1.679847862e-15, -1.053665697e-12, 0.0000000004179484479, -0.0000001104323298, 0.00001988783949, -0.002373811978, 3.402756286}; }
			else{return new double[] {2.68219598e-22, -6.517434345e-19, 6.731289824e-16, -3.808235335e-13, 0.0000000001244466797, -0.00000002102907012, 0.0000003400749627, 0.0005332259926, 3.17738327}; }
		}
		else if(curveIndex==5){
			if(isEven){return new double[] {5.868047373e-22, -1.493773755e-18, 1.646011961e-15, -1.033713617e-12, 0.0000000004110110847, -0.0000001089969189, 0.00001972298994, -0.002366275947, 3.402141515}; }
			else{return new double[] {2.539360205e-22, -5.954156295e-19, 5.920032934e-16, -3.215237254e-13, 9.990141815e-11, -0.00000001511641677, -0.0000004669941806, 0.0005908057724, 3.174754416}; }
		}
		else if(curveIndex==6){
			if(isEven){return new double[] {5.675588521e-22, -1.451557389e-18, 1.607404785e-15, -1.01465981e-12, 0.0000000004055279905, -0.0000001080761091, 0.00001964207236, -0.002365018221, 3.401695237}; }
			else{return new double[] {3.634982244e-22, -8.30260569e-19, 8.024641967e-16, -4.245178398e-13, 0.0000000001300914183, -0.00000002057038348, 0.0000001253868941, 0.0005567741947, 3.174543544}; }
		}
		else if(curveIndex==7){
			if(isEven){return new double[] {5.588144229e-22, -1.437600878e-18, 1.599525354e-15, -1.013104901e-12, 0.000000000405673495, -0.0000001081799307, 0.00001966131701, -0.00236844865, 3.401328463}; }
			else{return new double[] { 2.772138242e-22, -6.442053334e-19, 6.356321763e-16, -3.434664637e-13, 0.0000000001066750615, -0.00000001639123142, -0.0000003368963022, 0.0005870735935, 3.172668611}; }
		}
		else if(curveIndex==8){
			if(isEven){return new double[] {5.678501649e-22, -1.449333826e-18, 1.601720826e-15, -1.009370982e-12, 0.0000000004030390637, -0.0000001074529999, 0.00001957113615, -0.002365693275, 3.400853784}; }
			else{return new double[] { 1.983795022e-22, -4.641522178e-19, 4.667070368e-16, -2.599540892e-13, 8.357993178e-11, -0.00000001292344661, -0.0000005852997063, 0.0005936533976, 3.171632448}; }
		}
		else if(curveIndex==9){
			if(isEven){return new double[] { 5.859864785e-22, -1.490331184e-18, 1.640887414e-15, -1.029719733e-12, 0.0000000004091973158, -0.0000001085380558, 0.00001968036657, -0.002373043771, 3.40054465}; }
			else{return new double[] {4.814009071e-22, -1.071065347e-18, 1.00367056e-15, -5.127663697e-13, 0.0000000001516938218, -0.00000002342637176, 0.0000002875098574, 0.000561117727, 3.170942118}; }
		}
		else if(curveIndex==10){
			if(isEven){return new double[] {6.202671923e-22, -1.565380368e-18, 1.709008191e-15, -1.063007252e-12, 0.0000000004187415289, -0.0000001101910413, 0.00001985613891, -0.002385509599, 3.400404593}; }
			else{return new double[] {3.479316058e-22, -8.188204235e-19, 8.174488633e-16, -4.472901556e-13, 0.0000000001420802618, -0.00000002371458618, 0.0000005370358544, 0.0005378529908, 3.170532018}; }
		}
		else if(curveIndex==11){
			if(isEven){return new double[] {5.83894524e-22, -1.482244304e-18, 1.630052067e-15, -1.022732794e-12, 0.0000000004068377631, -0.0000001081483491, 0.00001966832104, -0.002379524824, 3.399901766}; }
			else{return new double[] {2.356629729e-22, -5.737541859e-19, 5.902761842e-16, -3.29936949e-13, 0.00000000010493588, -0.0000000163391251, -0.000000347841026, 0.0005953140621, 3.168051693}; }
		}
		else if(curveIndex==12){
			if(isEven){return new double[] {5.7125861e-22, -1.461231704e-18, 1.616749922e-15, -1.018763459e-12, 0.0000000004062597324, -0.0000001080992406, 0.0000196661222, -0.002381216316, 3.399482568}; }
			else{return new double[] {4.524265542e-23, -1.067529609e-19, 1.152159838e-16, -7.078878011e-14, 2.31231316e-11, -0.000000001295015493, -0.000001889915714, 0.0006750733923, 3.165496259}; }
		}
		else if(curveIndex==13){
			if(isEven){return new double[] {5.609123528e-22, -1.438905646e-18, 1.597236593e-15, -1.009959045e-12, 0.0000000004041252432, -0.0000001078563486, 0.00001966779746, -0.002385478927, 3.39919831}; }
			else{return new double[] {1.280169974e-22, -3.680592777e-19, 4.326785005e-16, -2.692478531e-13, 9.324938105e-11, -0.00000001554150549, -0.0000002922634448, 0.0005881808045, 3.166115029}; }
		}
		else if(curveIndex==14){
			if(isEven){return new double[] {5.280881522e-22, -1.353016274e-18, 1.503926736e-15, -9.55344873e-13, 0.0000000003854585216, -0.0000001040931924, 0.00001924287978, -0.002363274554, 3.398283616}; }
			else{return new double[] {4.786024929e-23, -1.760758083e-19, 2.427339222e-16, -1.693067432e-13, 6.331816061e-11, -0.00000001049696094, -0.0000007318316517, 0.0006048877834, 3.164953738}; }
		}
		else if(curveIndex==15){
			if(isEven){return new double[] {5.460979994e-22, -1.39932732e-18, 1.552555202e-15, -9.823250468e-13, 0.0000000003939872776, -0.0000001056389395, 0.00001940004471, -0.002373083898, 3.39803001}; }
			else{return new double[] {-1.757453102e-23, -2.826009242e-20, 9.958923851e-17, -9.166274908e-14, 3.757246762e-11, -0.000000005184403714, -0.000001393235381, 0.00065077363, 3.162642991}; }
		}
		else if(curveIndex==16){
			if(isEven){return new double[] {5.989387558e-22, -1.5101269e-18, 1.649134566e-15, -1.027671868e-12, 0.0000000004064231673, -0.0000001076683818, 0.00001959732191, -0.00238559852, 3.397879976}; }
			else{return new double[] {3.854539999e-22, -9.243319871e-19, 9.36139614e-16, -5.176111573e-13, 0.0000000001660961895, -0.0000000285283689, 0.000001072344707, 0.0005152375161, 3.164537714}; }
		}
		else if(curveIndex==17){
			if(isEven){return new double[] {5.596984677e-22, -1.429090188e-18, 1.57965102e-15, -9.95577427e-13, 0.0000000003977471002, -0.0000001062751649, 0.00001947256507, -0.002381792137, 3.397375147}; }
			else{return new double[] {2.777003543e-22, -6.650487066e-19, 6.70293474e-16, -3.671540599e-13, 0.0000000001152811257, -0.0000000181916926, -0.0000001480513708, 0.0005920983121, 3.161576483}; }
		}
		else if(curveIndex==18){
			if(isEven){return new double[] {5.498412118e-22, -1.404771534e-18, 1.554929605e-15, -9.82221326e-13, 0.0000000003936386471, -0.0000001055703353, 0.00001941677654, -0.002382615472, 3.397021992}; }
			else{return new double[] {-2.212343616e-22, 4.419915021e-19, -3.52535835e-16, 1.426904668e-13, -3.322498481e-11, 0.000000007387232104, -0.000002655688796, 0.0007182477548, 3.158146241}; }
		}
		else if(curveIndex==19){
			if(isEven){return new double[] {5.163332304e-22, -1.323466381e-18, 1.471967532e-15, -9.359998555e-13, 0.0000000003783796719, -0.0000001025515006, 0.00001907721796, -0.002364991149, 3.396198895}; }
			else{return new double[] {1.112593971e-23, -6.819691669e-20, 1.130643347e-16, -8.578967374e-14, 3.189407579e-11, -0.00000000346056063, -0.000001653346849, 0.0006749014983, 3.157674109}; }
		}
		else if(curveIndex==20){
			if(isEven){return new double[] { 5.897465494e-22, -1.492158945e-18, 1.634519214e-15, -1.021198831e-12, 0.0000000004047273315, -0.0000001074322555, 0.00001960529238, -0.002396984512, 3.396486295}; }
			else{return new double[] { 3.164254506e-22, -7.987450978e-19, 8.475353707e-16, -4.881907049e-13, 0.000000000162144334, -0.00000002867437877, 0.000001152152889, 0.0005152207242, 3.160184715}; }
		}
		else if(curveIndex==21){
			if(isEven){return new double[] {5.012928099e-22, -1.300423087e-18, 1.460845883e-15, -9.359125328e-13, 0.0000000003800925774, -0.0000001031927735, 0.00001919062447, -0.002378106882, 3.395688614}; }
			else{return new double[] {-8.545195447e-23, 1.086546616e-19, -1.178106161e-17, -4.546233112e-14, 2.766068009e-11, -0.000000004318565387, -0.000001374679317, 0.0006509252695, 3.156307703}; }
		}
		else if(curveIndex==22){
			if(isEven){return new double[] {5.408559531e-22, -1.378072686e-18, 1.522428112e-15, -9.609200516e-13, 0.0000000003854469762, -0.0000001037219482, 0.00001920135331, -0.002378392587, 3.395231064}; }
			else{return new double[] {-2.992692218e-22, 6.104111446e-19, -5.034211459e-16, 2.154486063e-13, -5.377317253e-11, 0.00000001088947153, -0.00000302892283, 0.0007476985663, 3.152961193}; }
		}
		else if(curveIndex==23){
			if(isEven){return new double[] {5.570475402e-22, -1.418305771e-18, 1.564086604e-15, -9.842538969e-13, 0.0000000003931253328, -0.0000001052348908, 0.00001937887271, -0.002391394289, 3.395118919}; }
			else{return new double[] {-9.30071667e-23, 1.804113666e-19, -1.288904402e-16, 3.787616489e-14, -3.666962137e-12, 0.000000002245394105, -0.000002134077696, 0.0006977151251, 3.153098927}; }
		}
		else if(curveIndex==24){
			if(isEven){return new double[] { 5.274593625e-22, -1.347236747e-18, 1.493266706e-15, -9.463179042e-13, 0.0000000003812955597, -0.0000001030689515, 0.00001916042436, -0.002382523389, 3.394537715}; }
			else{return new double[] {1.322628713e-22, -3.35987238e-19, 3.649038379e-16, -2.170675004e-13, 7.318068697e-11, -0.00000001139533993, -0.0000007738160088, 0.0006318420384, 3.153161259}; }
		}
		else if(curveIndex==25){
			if(isEven){return new double[] {5.856308687e-22, -1.480800345e-18, 1.62049766e-15, -1.011385368e-12, 0.0000000004006185866, -0.0000001064411365, 0.0000194977444, -0.002401792024, 3.394498778}; }
			else{return new double[] {2.619879368e-22, -6.417386663e-19, 6.642033074e-16, -3.748216801e-13, 0.0000000001217381628, -0.00000002025398811, 0.0000001536378402, 0.0005817054749, 3.153264575}; }
		}
		else if(curveIndex==26){
			if(isEven){return new double[] {5.075441949e-22, -1.311691514e-18, 1.468194752e-15, -9.374814538e-13, 0.0000000003796953083, -0.000000102951334, 0.00001917349076, -0.002388838438, 3.393874114}; }
			else{return new double[] {8.575523196e-23, -2.35712221e-19, 2.698947942e-16, -1.653085261e-13, 5.569077724e-11, -0.000000007745749561, -0.000001221215581, 0.0006625581408, 3.150301225}; }
		}
		else if(curveIndex==27){
			if(isEven){return new double[] {4.94439395e-22, -1.27901456e-18, 1.434158887e-15, -9.183000309e-13, 0.00000000037336514, -0.0000001017205121, 0.00001904282768, -0.00238389879, 3.393353278}; }
			else{return new double[] {2.269767417e-22, -5.788384675e-19, 6.225515399e-16, -3.642196002e-13, 0.0000000001224269928, -0.0000000212195261, 0.0000003507643045, 0.0005685662259, 3.151460287}; }
		}
		else if(curveIndex==28){
			if(isEven){return new double[] {5.301151196e-22, -1.358931833e-18, 1.509007428e-15, -9.561878968e-13, 0.0000000003845611972, -0.0000001036760708, 0.00001924249852, -0.002396633961, 3.393206866}; }
			else{return new double[] {1.404847798e-22, -3.549467887e-19, 3.826797026e-16, -2.261717399e-13, 7.610912852e-11, -0.00000001201760836, -0.0000007011131176, 0.0006328424078, 3.148843187}; }
		}
		else if(curveIndex==29){
			if(isEven){return new double[] {5.283835915e-22, -1.342774665e-18, 1.480893691e-15, -9.344868504e-13, 0.0000000003756011587, -0.0000001016003759, 0.00001898290379, -0.002382521482, 3.392460101}; }
			else{return new double[] { -2.003446672e-23, 9.25784752e-21, 3.804483402e-17, -4.987334091e-14, 2.325413321e-11, -0.000000002598852615, -0.000001668856741, 0.0006863815384, 3.146587551}; }
		}
		else if(curveIndex==30){
			if(isEven){return new double[] {5.149846974e-22, -1.323329023e-18, 1.474037138e-15, -9.375847994e-13, 0.0000000003787521052, -0.0000001026100759, 0.0000191422981, -0.002396398168, 3.392415049}; }
			else{return new double[] { -2.705637101e-23, -8.594749741e-21, 8.619590419e-17, -8.967682276e-14, 3.931004163e-11, -0.00000000608200171, -0.000001263820363, 0.0006639301169, 3.146053512}; }
		}
		else if(curveIndex==31){
			if(isEven){return new double[] {5.346528652e-22, -1.368162181e-18, 1.515347278e-15, -9.572733548e-13, 0.0000000003838849344, -0.0000001033182833, 0.00001919061274, -0.002399464577, 3.392032089}; }
			else{return new double[] {2.50700302e-22, -5.621758421e-19, 5.369412266e-16, -2.824391867e-13, 8.555897414e-11, -0.00000001220367632, -0.0000008668607344, 0.0006572426989, 3.144834202}; }
		}
		else if(curveIndex==32){
			if(isEven){return new double[] {5.183995335e-22, -1.327828478e-18, 1.474113055e-15, -9.347149563e-13, 0.0000000003767099728, -0.0000001019747953, 0.00001905180106, -0.002394150917, 3.391510376}; }
			else{return new double[] {4.554748939e-22, -1.056626825e-18, 1.038508066e-15, -5.597634176e-13, 0.0000000001762590041, -0.00000003000793825, 0.000001158025248, 0.000538247677, 3.14650299}; }
		}
		else if(curveIndex==33){
			if(isEven){return new double[] {5.334949542e-22, -1.355434837e-18, 1.493933545e-15, -9.416376356e-13, 0.0000000003778489577, -0.0000001020326122, 0.00001905224579, -0.002396431059, 3.391162527}; }
			else{return new double[] {-2.329037892e-22, 4.779398413e-19, -3.880256908e-16, 1.56592463e-13, -3.417576289e-11, 0.000000006599810051, -0.000002483349554, 0.0007267373475, 3.141640232}; }
		}
		else if(curveIndex==34){
			if(isEven){return new double[] {5.129650424e-22, -1.309565149e-18, 1.450026791e-15, -9.181567338e-13, 0.0000000003702126441, -0.0000001005074804, 0.00001887774, -0.002388092642, 3.390561616}; }
			else{return new double[] {7.078586169e-23, -1.777604327e-19, 1.922543682e-16, -1.138066061e-13, 3.667538575e-11, -0.000000003698662744, -0.000001728117729, 0.0007069960736, 3.14050953}; }
		}
		else if(curveIndex==35){
			if(isEven){return new double[] {5.157806324e-22, -1.319071634e-18, 1.462843844e-15, -9.272511643e-13, 0.0000000003739322507, -0.0000001014054096, 0.00001900532236, -0.002399241042, 3.390440783}; }
			else{return new double[] {8.223974169e-23, -2.341066615e-19, 2.818586605e-16, -1.836987405e-13, 6.714494924e-11, -0.00000001136845219, -0.0000006495939468, 0.0006320818266, 3.14144606}; }
		}
		else if(curveIndex==36){
			if(isEven){return new double[] { 4.833498546e-22, -1.248483867e-18, 1.399112474e-15, -8.963434384e-13, 0.0000000003652233431, -0.00000009997286189, 0.00001887907881, -0.002396030698, 3.390003234}; }
			else{return new double[] { 1.021695442e-22, -2.825081763e-19, 3.278509757e-16, -2.046639934e-13, 7.12746269e-11, -0.00000001131611339, -0.000000798268997, 0.000652890229, 3.13960086}; }
		}
		else if(curveIndex==37){
			if(isEven){return new double[] {5.143354561e-22, -1.309784973e-18, 1.447266198e-15, -9.150951217e-13, 0.0000000003687761604, -0.0000001001804349, 0.00001885637048, -0.002394570161, 3.389529674}; }
			else{return new double[] { 1.965524248e-22, -4.589906822e-19, 4.563569781e-16, -2.490985155e-13, 7.773456779e-11, -0.00000001118811319, -0.0000009403538023, 0.0006672473032, 3.138188221}; }
		}
		else if(curveIndex==38){
			if(isEven){return new double[] {4.89274625e-22, -1.256390266e-18, 1.399324439e-15, -8.914180677e-13, 0.0000000003617468979, -0.00000009890422259, 0.00001872373843, -0.002389311661, 3.389007828}; }
			else{return new double[] { 4.300353949e-22, -9.881004196e-19, 9.594341389e-16, -5.099582836e-13, 0.0000000001579154826, -0.0000000260084396, 0.0000006407627538, 0.0005819335864, 3.138878822}; }
		}
		else if(curveIndex==39){
			if(isEven){return new double[] {4.955158372e-22, -1.273272664e-18, 1.418882679e-15, -9.038115805e-13, 0.0000000003663525118, -0.00000009991718051, 0.00001885422952, -0.002399805314, 3.388855745}; }
			else{return new double[] {2.783542814e-22, -6.515553581e-19, 6.513538858e-16, -3.592167837e-13, 0.0000000001151108266, -0.00000001880242264, -0.00000007134410736, 0.0006222101331, 3.13686035}; }
		}
		else if(curveIndex==40){
			if(isEven){return new double[] {4.844520139e-22, -1.240054039e-18, 1.378413046e-15, -8.776470122e-13, 0.0000000003565831657, -0.00000009778770681, 0.00001859847153, -0.002386474489, 3.388152457}; }
			else{return new double[] {4.373134642e-22, -1.022606273e-18, 1.016378678e-15, -5.56049358e-13, 0.0000000001784586559, -0.00000003120483657, 0.000001361831442, 0.0005348600336, 3.137934447}; }
		}
		else{
			if(isEven){return new double[] {5.049964774e-22, -1.292176108e-18, 1.433083465e-15, -9.083613685e-13, 0.0000000003665862724, -0.00000009970148277, 0.00001881001257, -0.002400447178, 3.388049267}; }
			else{return new double[] { -5.770168928e-23, 1.046233675e-19, -6.095285496e-17, 4.774434709e-15, 5.935625606e-12, 0.0000000005894708245, -0.000002027808907, 0.0007248296061, 3.132699469}; }
		}

//		return coeffs ;
	}
	

	
}
