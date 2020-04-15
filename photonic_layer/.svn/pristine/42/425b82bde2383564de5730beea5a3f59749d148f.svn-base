package PhotonicElements.EffectiveIndexMethod.ModeSolver.CoupledStrip;

import PhotonicElements.EffectiveIndexMethod.ModeSolver.CoupledSlab.ModeCoupledSlabWgTM_fast;
import PhotonicElements.EffectiveIndexMethod.ModeSolver.SlabWg.ModeSlabWgTE;
import PhotonicElements.EffectiveIndexMethod.Structures.SlabWg;
import PhotonicElements.EffectiveIndexMethod.Structures.StripWg;
import ch.epfl.general_libraries.clazzes.ParamName;

public class ModeCoupledStripWgTE_fast {

	// Slab X --> TM mode (d = widthNm) --> Two coupled Slab Waveguides
	// Slab Y --> TE mode (d = heightNm)
	
	SlabWg slabY ;
	ModeSlabWgTE slabTE ;
	ModeCoupledSlabWgTM_fast slabTM ;
	StripWg stripWg ;
	double lambdaNm, neff_x_Even, neff_x_Odd, neff_y ;
	double[] neff_AllModes ;
	
	public ModeCoupledStripWgTE_fast(
			@ParamName(name="Strip Waveguide") StripWg stripWg,
			@ParamName(name="Gap size (nm)") double gap_nm,
			@ParamName(name="m: TE_(m,n) [m=0,1,2,...]") int mNumber,
			@ParamName(name="n: TE_(m,n) [n=0,1,2,...]") int nNumber
			){
		this.stripWg = stripWg ;
		lambdaNm = stripWg.getWavelengthNm() ;
		slabY = stripWg.getSlabY() ; // for the TE mode
		slabTE = new ModeSlabWgTE(slabY) ;
		neff_y = slabTE.findSpecificModeIndex(nNumber) ;
		double n_core = stripWg.getCoreIndex(); 
		double n_cladd = stripWg.getCladIndex() ;
		slabTM = new ModeCoupledSlabWgTM_fast(lambdaNm, stripWg.getWidthNm(), stripWg.getWidthNm(), gap_nm, n_cladd, n_core, n_cladd, n_core, n_cladd) ;
		neff_x_Even = slabTM.findNeffEven(mNumber) ;
		neff_x_Odd = slabTM.findNeffOdd(mNumber) ;
	}
	
	public ModeCoupledStripWgTE_fast(
			@ParamName(name="Strip Waveguide 1") StripWg stripWg1,
			@ParamName(name="Strip Waveguide 2") StripWg stripWg2,
			@ParamName(name="Gap size (nm)") double gap_nm,
			@ParamName(name="m: TE_(m,n) [m=0,1,2,...]") int mNumber,
			@ParamName(name="n: TE_(m,n) [n=0,1,2,...]") int nNumber
			){
		this.stripWg = stripWg1 ;
		lambdaNm = stripWg.getWavelengthNm() ;
		slabY = stripWg.getSlabY() ; // for the TE mode
		slabTE = new ModeSlabWgTE(slabY) ;
		neff_y = slabTE.findSpecificModeIndex(nNumber) ;
		double n_core = stripWg.getCoreIndex(); 
		double n_cladd = stripWg.getCladIndex() ;
		slabTM = new ModeCoupledSlabWgTM_fast(lambdaNm, stripWg1.getWidthNm(), stripWg2.getWidthNm(), gap_nm, n_cladd, n_core, n_cladd, n_core, n_cladd) ;
		neff_x_Even = slabTM.findNeffEven(mNumber) ;
		neff_x_Odd = slabTM.findNeffOdd(mNumber) ;
	}
	
	public double getWavelengthNm(){
		return lambdaNm ;
	}
	
	public double getNeffEven(){
		double n_core = stripWg.getCoreIndex() ;
		double neff = Math.sqrt(neff_x_Even*neff_x_Even + neff_y * neff_y - n_core * n_core) ;
		return neff ;
	}
	
	public double getNeffOdd(){
		double n_core = stripWg.getCoreIndex() ;
		double neff = Math.sqrt(neff_x_Odd*neff_x_Odd + neff_y * neff_y - n_core * n_core) ;
		return neff ;
	}
	
	// ********************* test *********************
//	public static void main(String[] args){
//		double lambda = 1550 ;
//		Wavelength inputLambda = new Wavelength(lambda) ;
//		double[] gaps = MoreMath.linspace(50, 1000, 100) ;
//		double[] neff_even = {} ;
//		double[] neff_odd = {} ;
//		for(double g : gaps){
//			StripWg stripWg = new StripWg(inputLambda, 450, 220) ;
//			ModeCoupledStripWgTE_fast modeSolver = new ModeCoupledStripWgTE_fast(stripWg, g, 0, 0) ;
//			neff_even = MoreMath.Arrays.append(neff_even, modeSolver.getNeffEven()) ;
//			neff_odd = MoreMath.Arrays.append(neff_odd, modeSolver.getNeffOdd()) ;
//
//		}
//		MatlabChart fig = new MatlabChart() ;
//		fig.plot(gaps, neff_even, "b") ;
//		fig.plot(gaps, neff_odd, "r");
//
////		CubicSpline inter_even = new CubicSpline(gaps, neff_even) ;
////		CubicSpline inter_odd = new CubicSpline(gaps, neff_odd) ;
////		double[] g = MoreMath.linspace(50, 1000, 1000) ;
////		double[] neff_even_inter = {} ;
////		double[] neff_odd_inter = {} ;
////		for(double x : g){
////			neff_even_inter = MoreMath.Arrays.append(neff_even_inter, inter_even.interpolate(x)) ;
////			neff_odd_inter = MoreMath.Arrays.append(neff_odd_inter, inter_odd.interpolate(x)) ;
////		}
////		fig.plot(g, neff_even_inter, "k");
////		fig.plot(g, neff_odd_inter, "m");
//		fig.RenderPlot();
//		fig.run();
//
//	}
	
}
