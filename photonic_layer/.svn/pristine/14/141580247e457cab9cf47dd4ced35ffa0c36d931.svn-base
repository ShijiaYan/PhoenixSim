package PhotonicElements.EffectiveIndexMethod.ModeSolver.CoupledStrip;

import PhotonicElements.EffectiveIndexMethod.ModeSolver.CoupledSlab.ModeCoupledSlabWgTM;
import PhotonicElements.EffectiveIndexMethod.ModeSolver.SlabWg.ModeSlabWgTE_old;
import PhotonicElements.EffectiveIndexMethod.Structures.SlabWg;
import PhotonicElements.EffectiveIndexMethod.Structures.StripWg;
import ch.epfl.general_libraries.clazzes.ParamName;

public class ModeCoupledStripWgTE_old {

	// Slab X --> TM mode (d = widthNm) --> Two coupled Slab Waveguides
	// Slab Y --> TE mode (d = heightNm)
	
	SlabWg slabY ;
	ModeSlabWgTE_old slabTE ;
	ModeCoupledSlabWgTM slabTM ;
	StripWg stripWg ;
	double lambdaNm, neff_x_Even, neff_x_Odd, neff_y ;
	
	public ModeCoupledStripWgTE_old(
			@ParamName(name="Strip Waveguide") StripWg stripWg,
			@ParamName(name="Gap size (nm)") double gap_nm,
			@ParamName(name="m: TE_(m,n) [m=0,1,2,...]") int mNumber,
			@ParamName(name="n: TE_(m,n) [n=0,1,2,...]") int nNumber
			){
		this.stripWg = stripWg ;
		lambdaNm = stripWg.getWavelengthNm() ;
		slabY = stripWg.getSlabY() ; // for the TE mode
		slabTE = new ModeSlabWgTE_old(slabY) ;
		neff_y = slabTE.findSpecificModeIndex(nNumber+1) ;
		double n_core = stripWg.getCoreIndex(); 
		double n_cladd = stripWg.getCladIndex() ;
		slabTM = new ModeCoupledSlabWgTM(lambdaNm, stripWg.getWidthNm(), stripWg.getWidthNm(), gap_nm, n_cladd, n_core, n_cladd, n_core, n_cladd) ;
		neff_x_Even = slabTM.findNeffEven(mNumber+1) ;
		neff_x_Odd = slabTM.findNeffOdd(mNumber+1) ;
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
