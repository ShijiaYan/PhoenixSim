package PhotonicElements.Modulators;

import ch.epfl.general_libraries.clazzes.ParamName;
import flanagan.roots.RealRootFunction;

import static java.lang.Math.* ;

import People.Meisam.GUI.Plotters.MatlabPlot.MatlabChart;
import PhotonicElements.Utilities.MathLibraries.MoreMath;
import PhotonicElements.Utilities.MathLibraries.RealRootFinder;

public class ModSelfHeatingPP {

	public double k, t, L, Hlambda, dlambdaNm, FSRnm ;
	double ng = 4.393 ;
	public double pWgmW, pWgdBm ;
	public int numSolutions;

	public ModSelfHeatingPP(
			@ParamName(name="kappa") double k,
			@ParamName(name="L") double L,
			@ParamName(name="Hlambda (nm/mW)") double Hlambda,
			@ParamName(name="Pwg (dBm)") double pWgdBm,
			@ParamName(name="FSR (nm)") double FSRnm,
			@ParamName(name="Laser detuning (nm)") double dLambdaNm
			){
		this.k = k ;
		this.t = sqrt(1-k*k) ;
		this.L = L ;
		this.Hlambda = Hlambda ;
		this.dlambdaNm = dLambdaNm ;
		this.FSRnm = FSRnm ;
		this.pWgdBm = pWgdBm ;
		this.pWgmW = pow(10, pWgdBm/10) ;
	}

	private double getResEquation(double DlambdaResNm){
		double arg = (-DlambdaResNm+dlambdaNm)*2*PI/FSRnm ;
		double num = k*k*Hlambda*pWgmW ;
//		double denom = 1+t*t*L-2*t*sqrt(L)*cos(arg) ;
		double denom = 1+t*t*L-2*t*sqrt(L)*(1-0.5*arg*arg) ;
		double eq = DlambdaResNm - num/denom ;
		return eq ;
	}

	private double[] getResSolutions(){
		RealRootFunction func = x -> getResEquation(x);
		RealRootFinder roots = new RealRootFinder(func, 0, 20) ;
		roots.setAccuracy(1e-3);
		roots.findAllRoots();
		double[] allroots = roots.getAllRoots() ;
		if(allroots.length == 1){numSolutions = 1; return new double[]{allroots[0], Double.NaN, Double.NaN}; }
		else if(allroots.length == 2){numSolutions = 2; return new double[]{allroots[0], allroots[1], Double.NaN};}
		else if(allroots.length == 3){numSolutions = 3; return allroots ;}
		return allroots ;
	}

	public double getThruTransmission(double DlambdaResNm){
		double arg = (-DlambdaResNm+dlambdaNm)*2*PI/FSRnm ;
//		double num = t*t + L - 2*t*sqrt(L)*cos(arg) ;
//		double denom = 1+t*t*L-2*t*sqrt(L)*cos(arg) ;

		double num = t*t + L - 2*t*sqrt(L)*(1-0.5*arg*arg) ;
		double denom = 1+t*t*L-2*t*sqrt(L)*(1-0.5*arg*arg) ;
		return num/denom ;
	}

	public int getNumSolutions(){
		return numSolutions ;
	}


	// for test
	public static void main(String[] args){

		double[] dLambdaLaserNm = MoreMath.linspace(-1, 1, 5000) ;
		double[] dLambdaResNmFirst = new double[dLambdaLaserNm.length] ;
		double[] dLambdaResNmSecond = new double[dLambdaLaserNm.length] ;
		double[] dLambdaResNmThird = new double[dLambdaLaserNm.length] ;
		double[] transFirst = new double[dLambdaLaserNm.length] ;
		for(int i=0; i<dLambdaLaserNm.length; i++){
			ModSelfHeatingPP mod = new ModSelfHeatingPP(0.2, 0.9857, 0.0423, -10, 17.4, dLambdaLaserNm[i]) ;
			double[] allRoots = mod.getResSolutions() ;
			dLambdaResNmFirst[i] = allRoots[0] ;
			dLambdaResNmSecond[i] = allRoots[1] ;
			dLambdaResNmThird[i] = allRoots[2] ;
			transFirst[i] = MoreMath.Conversions.todB(mod.getThruTransmission(allRoots[0])) ;
		}
		MatlabChart fig = new MatlabChart() ;
		fig.plot(dLambdaLaserNm, dLambdaResNmFirst, "b");
		fig.plot(dLambdaLaserNm, dLambdaResNmSecond, "r");
		fig.plot(dLambdaLaserNm, dLambdaResNmThird, "k");
		fig.RenderPlot();
		fig.run(true);

		MatlabChart fig1 = new MatlabChart() ;
		fig1.plot(dLambdaLaserNm, transFirst);
		fig1.RenderPlot();
		fig1.run(true);


	}


}
