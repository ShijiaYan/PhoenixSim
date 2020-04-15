package PhotonicElements.EffectiveIndexMethod.ModeSolver.CoupledSlab;

import PhotonicElements.EffectiveIndexMethod.InterfaceTransferMatrix.TransferMatrixTE;
import PhotonicElements.EffectiveIndexMethod.ModeSolver.SlabWg.ModeSlabWgTE;
import PhotonicElements.EffectiveIndexMethod.Structures.CoupledSlabWg;
import PhotonicElements.EffectiveIndexMethod.Structures.SlabWg;
import PhotonicElements.Utilities.MathLibraries.ComplexMatrix;
import PhotonicElements.Utilities.MathLibraries.RealRootFinder;
import PhotonicElements.Utilities.Wavelength;
import ch.epfl.general_libraries.clazzes.ParamName;
import flanagan.roots.RealRootFunction;

public class ModeCoupledSlabWgTE {

	Wavelength inputLambda ;
	double w1_nm, w2_nm, gap_nm , lambdaNm , k0;
	double n_down, n_core1, n_core2, n_up, n_gap, n_high, n_low ;
	ModeSlabWgTE slabTE1, slabTE2 ;
	
	double[] neff_AllModes ;
	
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
	public ModeCoupledSlabWgTE(
			@ParamName(name="wavelength (nm)") Wavelength inputLambda,
			@ParamName(name="Coupled Slab Structure") CoupledSlabWg coupledSlab
			){
		this.inputLambda = inputLambda ;
		this.k0 = 2*Math.PI/(lambdaNm*1e-9) ;
		this.w1_nm = coupledSlab.w1_nm ;
		this.w2_nm = coupledSlab.w2_nm ;
		this.gap_nm = coupledSlab.gap_nm ;
		this.n_up = coupledSlab.n_up ;
		this.n_core1 = coupledSlab.n_core1 ;
		this.n_gap = coupledSlab.n_gap;
		this.n_core2 = coupledSlab.n_core2 ;
		this.n_down = coupledSlab.n_down ;
		this.n_high = coupledSlab.n_high ;
		this.n_low = coupledSlab.n_low ;
		
		SlabWg slab1 = new SlabWg(inputLambda, w1_nm, n_gap, n_core1, n_up) ;
		SlabWg slab2 = new SlabWg(inputLambda, w2_nm, n_down, n_core2, n_gap) ;
		slabTE1 = new ModeSlabWgTE(slab1) ;
		slabTE2 = new ModeSlabWgTE(slab2) ;
		
		findNeff_AllModes();
	}

	public ModeCoupledSlabWgTE(
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
		this.n_high = Math.max(n_c_1, n_c_2) ;
		this.n_low = Math.min(Math.min(n_u, n_d), n_g) ;

		SlabWg slab1 = new SlabWg(inputLambda, w1_nm, n_gap, n_core1, n_up) ;
		SlabWg slab2 = new SlabWg(inputLambda, w2_nm, n_down, n_core2, n_gap) ;
		slabTE1 = new ModeSlabWgTE(slab1) ;
		slabTE2 = new ModeSlabWgTE(slab2) ;
		
		findNeff_AllModes();
	}
	
	// Next I need to find the Mode Equation
	private double getModeEquation(double neff){
		TransferMatrixTE Q1 = new TransferMatrixTE(inputLambda, n_up, n_core1, neff, 0, 0) ;
		TransferMatrixTE Q2 = new TransferMatrixTE(inputLambda, n_core1, n_gap, neff, w1_nm, 0) ;
		TransferMatrixTE Q3 = new TransferMatrixTE(inputLambda, n_gap, n_core2, neff, w1_nm+gap_nm, 0) ;
		TransferMatrixTE Q4 = new TransferMatrixTE(inputLambda, n_core2, n_down, neff, w1_nm+gap_nm+w2_nm, 0) ;
		ComplexMatrix T1 = Q1.getTransferMatrix() ;
		ComplexMatrix T2 = Q2.getTransferMatrix() ;
		ComplexMatrix T3 = Q3.getTransferMatrix() ;
		ComplexMatrix T4 = Q4.getTransferMatrix() ;
		ComplexMatrix Ttot = T4.times(T3).times(T2).times(T1) ;
		return Ttot.getElement(1, 1).re() ;
	}	
	

	
	public void findNeff_AllModes(){
		RealRootFunction func = new RealRootFunction() {
			@Override
			public double function(double neff) {
				return getModeEquation(neff);
			}
		};
		RealRootFinder rootFinder = new RealRootFinder(func, n_low, n_high) ;
		rootFinder.setAccuracy(1e-4);
		rootFinder.findAllRoots();
		neff_AllModes = rootFinder.getAllRoots() ;
	}
	
	// mode number starts from 0 --> TE0, TE1, TE2, ...
	public double findNeffEven(int modeNumber){
		int M = neff_AllModes.length ;
		if(M >= 2*modeNumber+1){
			return neff_AllModes[M-(2*modeNumber+1)] ;
		}
		else{
			return Double.NaN ;
		}
		
	}
	
	public double findNeffOdd(int modeNumber){
		int M = neff_AllModes.length ;
		if(M >= 2*modeNumber+2){
			return neff_AllModes[M-(2*modeNumber+2)] ;
		}
		else{
			return Double.NaN ;
		}
	}
	
	public double findNeffCoupledMode(int modeNumber){
		int M = neff_AllModes.length ;
		if(M >= modeNumber+1){
			return neff_AllModes[(M-1)-modeNumber] ;
		}
		else{
			return Double.NaN ;
		}
	}
	
	public double[] getNeff_allModes(){
		return neff_AllModes ;
	}

	// ********************* test *********************
//	public static void main(String[] args){
//		double lambda = 1550 ;
//		Wavelength inputLambda = new Wavelength(lambda) ;
//		double[] gaps = MoreMath.Arrays.concat(MoreMath.linspace(50, 300, 20),  MoreMath.linspace(300, 700, 20)) ;
//		double[] neff_even = {} ;
//		double[] neff_odd = {} ;
//		double[] neff_slab1 = {} ;
//		double[] neff_slab2 = {} ;
//		double[] neff_mode1 = {} ;
//		double[] neff_mode2 = {} ;
//		double[] neff_mode3 = {} ;
//		double[] neff_mode4 = {} ;
//		double[] neff_mode5 = {} ;
//		int modeNumber = 0 ;
//		for(double g : gaps){
//			CoupledSlabWg slabs = new CoupledSlabWg(350, 500, g, 1.444, 3.444, 1.444, 3.444, 1.444) ;
//			ModeCoupledSlabWgTE modeSolver = new ModeCoupledSlabWgTE(inputLambda, slabs) ;
//			neff_even = MoreMath.Arrays.append(neff_even, modeSolver.findNeffEven(modeNumber)) ;
//			neff_odd = MoreMath.Arrays.append(neff_odd, modeSolver.findNeffOdd(modeNumber)) ;
//			neff_slab1 = MoreMath.Arrays.append(neff_slab1, modeSolver.slabTE1.findSpecificModeIndex(modeNumber)) ;
//			neff_slab2 = MoreMath.Arrays.append(neff_slab2, modeSolver.slabTE2.findSpecificModeIndex(modeNumber)) ;
//			neff_mode1 = MoreMath.Arrays.append(neff_mode1, modeSolver.findNeffCoupledMode(0)) ;
//			neff_mode2 = MoreMath.Arrays.append(neff_mode2, modeSolver.findNeffCoupledMode(1)) ;
//			neff_mode3 = MoreMath.Arrays.append(neff_mode3, modeSolver.findNeffCoupledMode(2)) ;
//			neff_mode4 = MoreMath.Arrays.append(neff_mode4, modeSolver.findNeffCoupledMode(3)) ;
//			neff_mode5 = MoreMath.Arrays.append(neff_mode5, modeSolver.findNeffCoupledMode(4)) ;
//		}
//		
//		MatlabChart fig = new MatlabChart() ;
////		fig.plot(gaps, neff_even, "b") ;
////		fig.plot(gaps, neff_odd, "r");
////		fig.plot(gaps, neff_slab1, "k");
////		fig.plot(gaps, neff_slab2, "m");
//		
//		fig.plot(gaps, neff_mode1, "b");
//		fig.plot(gaps, neff_mode2, "r");
//		fig.plot(gaps, neff_mode3, "k");
//		fig.plot(gaps, neff_mode4, "m");
//		fig.plot(gaps, neff_mode5, "g");
//		
//		fig.RenderPlot(); 
//		fig.run();
//		
//	}


}
