package PhotonicElements.EffectiveIndexMethod.ModeSolver.SlabWg;

import PhotonicElements.EffectiveIndexMethod.Structures.SlabWg;
import PhotonicElements.Utilities.MathLibraries.Complex;
import PhotonicElements.Utilities.Wavelength;
import ch.epfl.general_libraries.clazzes.ParamName;

public class ModeSlabWgTM_old {

Complex J = new Complex(0,1), one = new Complex(1,0), zero = new Complex(0,0) ;

	Wavelength inputLambda ;
	double widthNm, lambdaNm ;
	double n_down, n_core, n_up, n_low, n_high , V ;
	public double asymmetryFactor;

	// First type of constructor for directly inputing parameters of the slab
	public ModeSlabWgTM_old(
			@ParamName(name="normalized frequency V") double V,
			@ParamName(name="waveguide width (nm)") double widthNm,
			@ParamName(name="down index") double n_d,
			@ParamName(name="core index") double n_c,
			@ParamName(name="up index") double n_u
			){
		this.V = V ;
		this.lambdaNm = 2*Math.PI*widthNm*Math.sqrt(n_c*n_c-n_d*n_d) /V ;
		this.widthNm = widthNm ;
		this.n_core = n_c ;
		this.n_down = n_d ;
		this.n_up = n_u ;
		this.n_low = Math.max(n_d, n_u) ;
		this.n_high = n_c ;
		this.asymmetryFactor = (n_d*n_d-n_u*n_u)/(n_c*n_c-n_d*n_d) ;
	}

	// Second type of constructor for using the SlabWg class
	public ModeSlabWgTM_old(
			SlabWg slab
			){
		this.V = slab.getNormalizedFreq() ;
		this.lambdaNm = slab.getWavelengthNm() ;
		this.widthNm = slab.getWidthNm() ;
		this.n_core = slab.getCoreIndex() ;
		this.n_down = slab.getSubstrateIndex() ;
		this.n_up = slab.getCladIndex() ;
		this.n_low = slab.getSubstrateIndex() ;
		this.n_high = slab.getCoreIndex() ;
		this.asymmetryFactor = slab.getTEsymmetryFactor() ;
	}

	// now I need to calculate normal index of each area (n_x)
	private Complex get_nx_up(double neff){
		double A = Math.sqrt(neff*neff - n_up * n_up) ;
		return new Complex(0, -1*A) ;
	}

	private Complex get_nx_core(double neff){
		double A = Math.sqrt(n_core * n_core - neff * neff) ;
		return new Complex(A, 0) ;
	}

	private Complex get_nx_down(double neff){
		double A = Math.sqrt(neff*neff - n_down * n_down) ;
		return new Complex(0, -1*A) ;
	}
	// Now I need to find the normalized impedance of each region
	private Complex get_Zd_over_Zc(double neff){
		Complex nx_core = get_nx_core(neff) ;
		Complex nx_down = get_nx_down(neff) ;
		return nx_down.divides(nx_core).times(n_core*n_core /(n_down*n_down));
	}

	private Complex get_Zu_over_Zc(double neff){
		Complex nx_core = get_nx_core(neff) ;
		Complex nx_up = get_nx_up(neff) ;
		return nx_up.divides(nx_core).times(n_core*n_core /(n_up*n_up));
	}
	// Finally need to solve the eigen value equation
	private double getModeEquation(double neff){
		Complex D = get_Zd_over_Zc(neff) ;
		Complex U = get_Zu_over_Zc(neff) ;
		Complex arg = get_nx_core(neff).times(2*Math.PI*widthNm/lambdaNm) ;
		Complex Jtan = arg.tan().times(J) ;
		Complex A = D.times(U).plus(one) ;
		Complex eq = D.plus(U).plus(Jtan.times(A)) ;
		return eq.abs() ;
	}

	public double findSpecificModeIndex(int modeNumber){
		int N = 1000000 ;
		double[] neffValues = new double[N] ;
		double Dn = (n_high - n_low)/(N-1) ;
		for(int i=0; i<N; i++){
			neffValues[i] = n_high - i*Dn ; // going from high index to low index
		}
		// find the minimum of the mode equation
		int numModes = 0 ;
		int modeIndex = N-1 ;
		double error = 1e-1 ;
		for(int i=1; i<N-1; i++){
			if(getModeEquation(neffValues[i])<error){
				if(getModeEquation(neffValues[i])<getModeEquation(neffValues[i-1])&&getModeEquation(neffValues[i])<getModeEquation(neffValues[i+1])){
					numModes++ ;
					if(numModes == modeNumber){
						modeIndex = i ;
					}
				}
			}

		}
		return neffValues[modeIndex] ;
	}

	public double findSpecificModeNormalizedIndex(int modeNumber){
		int N = 1000000 ;
		double[] neffValues = new double[N] ;
		double Dn = (n_high - n_low)/(N-1) ;
		for(int i=0; i<N; i++){
			neffValues[i] = n_high - i*Dn ; // going from high index to low index
		}
		// find the minimum of the mode equation
		int numModes = 0 ;
		int modeIndex = N-1 ;
		double error = 1e-1 ;
		for(int i=1; i<N-1; i++){
			if(getModeEquation(neffValues[i])<error){
				if(getModeEquation(neffValues[i])<getModeEquation(neffValues[i-1])&&getModeEquation(neffValues[i])<getModeEquation(neffValues[i+1])){
					numModes++ ;
					if(numModes == modeNumber){
						modeIndex = i ;
					}
				}
			}

		}
		double b = (neffValues[modeIndex]*neffValues[modeIndex]-n_low*n_low)/(n_high*n_high - n_low*n_low) ; // normalized index
		return b ;
	}

	//***************************Diagnostics***************************************

	// I need to find the number of modes and their corresponding effective indexes
/*	public DataPoint findSpecificMode(int modeNumber){
		int N = 1000000 ;
		double[] neffValues = new double[N] ;
		double Dn = (n_high - n_low)/(N-1) ;
		for(int i=0; i<N; i++){
			neffValues[i] = n_high - i*Dn ; // going from high index to low index
		}
		// find the minimum of the mode equation
		int numModes = 0 ;
		int modeIndex = N-1 ;
		double error = 1e-2 ;
		for(int i=1; i<N-1; i++){
			if(getModeEquation(neffValues[i])<error){
				if(getModeEquation(neffValues[i])<getModeEquation(neffValues[i-1])&&getModeEquation(neffValues[i])<getModeEquation(neffValues[i+1])){
					numModes++ ;
					if(numModes == modeNumber){
						modeIndex = i ;
					}
				}
			}

		}
//		return neffValues[modeIndex] ;

		double b = (neffValues[modeIndex]*neffValues[modeIndex]-n_low*n_low)/(n_high*n_high - n_low*n_low) ; // normalized index

		DataPoint dp = new DataPoint() ;

		dp.addResultProperty("Effective index", neffValues[modeIndex]);
		dp.addProperty("width (nm)", widthNm);
		dp.addProperty("V", V);
		dp.addResultProperty("b", b);
		return dp ;
	}*/


	// checking that the eigen equation is correct and works properly
/*	public DataPoint getTestIndexVals(int i){
		int N = 10000 ;
		double Dn = (n_high - n_low)/N ;

		double neffValue = n_low + i*Dn ;

		DataPoint dp = new DataPoint() ;
		dp.addProperty("neff test value", neffValue);
		dp.addResultProperty("equation value", getModeEquation(neffValue));
		return dp ;
	}*/


}
