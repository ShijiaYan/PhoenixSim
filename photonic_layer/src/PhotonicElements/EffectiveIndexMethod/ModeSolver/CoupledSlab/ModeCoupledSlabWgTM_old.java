package PhotonicElements.EffectiveIndexMethod.ModeSolver.CoupledSlab;


import PhotonicElements.EffectiveIndexMethod.InterfaceTransferMatrix.TransferMatrixTM;
import PhotonicElements.EffectiveIndexMethod.ModeSolver.SlabWg.ModeSlabWgTM_old;
import PhotonicElements.EffectiveIndexMethod.Structures.SlabWg;
import PhotonicElements.Utilities.MathLibraries.Complex;
import PhotonicElements.Utilities.MathLibraries.ComplexMatrix;
import PhotonicElements.Utilities.Wavelength;
import ch.epfl.general_libraries.clazzes.ParamName;


public class ModeCoupledSlabWgTM_old {

	Complex plusJ = new Complex(0,1), minusJ = new Complex(0,-1), one = new Complex(1,0), zero = new Complex(0,0) ;

	Wavelength inputLambda ;
	double w1_nm, w2_nm, gap_nm , lambdaNm , k0;
	double n_down, n_core1, n_core2, n_up, n_gap, n_high, n_low ;
	double neffSlabTM_min, neffSlabTM_max ;

/*				n_up
		-----------------------
				n_core1
		-----------------------
				n_gap
		-----------------------
				n_core2
		-----------------------
				n_down
*/

	// First type of constructor for directly inputing parameters of the slab
	public ModeCoupledSlabWgTM_old(
			@ParamName(name="wavelength (nm)") double lambdaNm,
			@ParamName(name="waveguide 1 width (nm)") double w1_nm,
			@ParamName(name="waveguide 2 width (nm)") double w2_nm,
			@ParamName(name="gap size (nm)") double gap_nm,
			@ParamName(name="up index") double n_u,
			@ParamName(name="core 1 index") double n_c_1,
			@ParamName(name="gap index") double n_g,
			@ParamName(name="core 2 index") double n_c_2,
			@ParamName(name="down index") double n_d
			){
		this.lambdaNm = lambdaNm ;
		this.inputLambda = new Wavelength(lambdaNm) ;
		this.k0 = 2*Math.PI/(lambdaNm*1e-9) ;
		this.w1_nm = w1_nm ;
		this.w2_nm = w2_nm ;
		this.gap_nm = gap_nm ;
		this.n_up = n_u ;
		this.n_core1 = n_c_1 ;
		this.n_gap = n_g ;
		this.n_core2 = n_c_2 ;
		this.n_down = n_d ;
		this.n_high = Math.min(n_c_1, n_c_2) ;
		this.n_low = Math.max(Math.max(n_u, n_d), n_g) ;
		
		SlabWg slab1 = new SlabWg(inputLambda, w1_nm, n_gap, n_core1, n_up) ;
		SlabWg slab2 = new SlabWg(inputLambda, w2_nm, n_down, n_core2, n_gap) ;
		double neffTM1 = new ModeSlabWgTM_old(slab1).findSpecificModeIndex(1) ;
		double neffTM2 = new ModeSlabWgTM_old(slab2).findSpecificModeIndex(1) ;
		neffSlabTM_min = Math.min(neffTM1, neffTM2) ;
		neffSlabTM_max = Math.max(neffTM1, neffTM2) ;
	}

	// Next I need to find the Even mode equation (Y1 + Y2 = 0) 
	private double getModeEquation(double neff){
		TransferMatrixTM Q1 = new TransferMatrixTM(inputLambda, n_up, n_core1, neff, 0, 0) ;
		TransferMatrixTM Q2 = new TransferMatrixTM(inputLambda, n_core1, n_gap, neff, w1_nm, 0) ;
		TransferMatrixTM Q3 = new TransferMatrixTM(inputLambda, n_gap, n_core2, neff, w1_nm+gap_nm, 0) ;
		TransferMatrixTM Q4 = new TransferMatrixTM(inputLambda, n_core2, n_down, neff, w1_nm+gap_nm+w2_nm, 0) ;
		ComplexMatrix T1 = Q1.getTransferMatrix() ;
		ComplexMatrix T2 = Q2.getTransferMatrix() ;
		ComplexMatrix T3 = Q3.getTransferMatrix() ;
		ComplexMatrix T4 = Q4.getTransferMatrix() ;
		ComplexMatrix Ttot = T4.times(T3).times(T2).times(T1) ;
		return Ttot.getElement(1, 1).abs() ;	
	}	

	public double findNeffEven(int modeNumber){
		int N = 10000 ;
		double[] neffValues = new double[N] ;
		double Dn = (n_high - neffSlabTM_max)/(N-1) ;
		for(int i=0; i<N; i++){
			neffValues[i] = neffSlabTM_max + i*Dn ; // going from high index to low index
		}
		// find the minimum of the mode equation
		int numModes = 0 ;
		int modeIndex = 0 ;
		for(int i=1; i<N-1; i++){
				if(getModeEquation(neffValues[i])<getModeEquation(neffValues[i-1])&&getModeEquation(neffValues[i])<getModeEquation(neffValues[i+1])){
					numModes++ ;
					if(numModes == modeNumber){
						modeIndex = i ;
					}
				}

		}
			return neffValues[modeIndex] ;
			
	}
	

	// the best way is to divide the interval into subintervals and search each one separately
	public double findNeffOdd(int modeNumber){
		int N = 10000 ; // number of points in each subinterval
		int M = 10 ; // number of subintervals
//		double[] intervalPoints = linspace(neffSlabTM_min, n_low, M+1) ;
		double[] intervalPoints = linspace(neffSlabTM_max, n_low, M+1) ;
		double[][] neffValues = new double[M][N] ;
		for(int i=0; i<M; i++){
			neffValues[i] = linspace(intervalPoints[i], intervalPoints[i+1], N) ;
		}
		// find the minimum of the mode equation
		int numModes = 0 ;
		int modeIndex = 0 ;
		int rowIndex = 0 ;
		for(int j=0; j<M; j++){
			for(int i=1; i<N-1; i++){
					if(getModeEquation(neffValues[j][i])<getModeEquation(neffValues[j][i-1])&&getModeEquation(neffValues[j][i])<getModeEquation(neffValues[j][i+1])){
						numModes++ ;
						if(numModes == modeNumber){
							modeIndex = i ;
							rowIndex = j ;
							break ;
						}
					}
	
	
			}
		}
		return neffValues[rowIndex][modeIndex] ;	
	}
	//*******************************************************************************
	
	private double[] linspace(double start, double end, int numPoints){
		double[] Values = new double[numPoints] ;
		double Delta = (end - start)/(numPoints-1) ;
		for(int i=0; i<numPoints; i++){
			Values[i] = start + i*Delta ; // going from high index to low index
		}
		return Values ;
	}
	
	


}