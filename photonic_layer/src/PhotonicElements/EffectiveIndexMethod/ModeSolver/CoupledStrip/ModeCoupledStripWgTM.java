package PhotonicElements.EffectiveIndexMethod.ModeSolver.CoupledStrip;

import PhotonicElements.EffectiveIndexMethod.ModeSolver.CoupledSlab.ModeCoupledSlabWgTE;
import PhotonicElements.EffectiveIndexMethod.ModeSolver.SlabWg.ModeSlabWgTM_old;
import PhotonicElements.EffectiveIndexMethod.Structures.SlabWg;
import PhotonicElements.EffectiveIndexMethod.Structures.StripWg;
import ch.epfl.general_libraries.clazzes.ParamName;

public class ModeCoupledStripWgTM {

	// Slab X --> TE mode (d = widthNm) --> Two coupled Slab Waveguides
	// Slab Y --> TM mode (d = heightNm)
	
	SlabWg slabY ;
	ModeSlabWgTM_old slabTM ;
	ModeCoupledSlabWgTE slabTE ;
	StripWg stripWg ;
	double lambdaNm, neff_x_Even, neff_x_Odd, neff_y ;
	
	public ModeCoupledStripWgTM(
			@ParamName(name="Strip Waveguide") StripWg stripWg,
			@ParamName(name="Gap size (nm)") double gap_nm,
			@ParamName(name="m: TE_(m,n) [m=0,1,2,...]") int mNumber,
			@ParamName(name="n: TE_(m,n) [n=0,1,2,...]") int nNumber
			){
		this.stripWg = stripWg ;
		lambdaNm = stripWg.getWavelengthNm() ;
		slabY = stripWg.getSlabY() ; // for the TE mode
		slabTM = new ModeSlabWgTM_old(slabY) ;
		neff_y = slabTM.findSpecificModeIndex(nNumber+1) ;
		double n_core = stripWg.getCoreIndex(); 
		double n_cladd = stripWg.getCladIndex() ;
		slabTE = new ModeCoupledSlabWgTE(lambdaNm, stripWg.getWidthNm(), stripWg.getWidthNm(), gap_nm, n_cladd, n_core, n_cladd, n_core, n_cladd) ;
		neff_x_Even = slabTE.findNeffEven(mNumber+1) ;
		neff_x_Odd = slabTE.findNeffOdd(mNumber+1) ;
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
	
}
