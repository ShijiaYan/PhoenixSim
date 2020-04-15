package PhotonicElements.EffectiveIndexMethod.ModeSolver.StripWg;

import PhotonicElements.EffectiveIndexMethod.ModeSolver.SlabWg.ModeSlabWgTE_old;
import PhotonicElements.EffectiveIndexMethod.ModeSolver.SlabWg.ModeSlabWgTM_old;
import PhotonicElements.EffectiveIndexMethod.Structures.SlabWg;
import PhotonicElements.EffectiveIndexMethod.Structures.StripWg;
import ch.epfl.general_libraries.clazzes.ParamName;

@Deprecated
public class ModeStripWgTM_old {

	// Slab X --> TE mode (d = widthNm)
	// Slab Y --> TM mode (d = heightNm)

	SlabWg slabX, slabY ;
	ModeSlabWgTE_old slabTE ;
	ModeSlabWgTM_old slabTM ;
	StripWg stripWg ;
	double lambdaNm, neff_x, neff_y ;

	public ModeStripWgTM_old(
			@ParamName(name="Strip Waveguide") StripWg stripWg,
			@ParamName(name="m: TM_(m,n) [m=0,1,2,...]") int mNumber,
			@ParamName(name="n: TM_(m,n) [n=0,1,2,...]") int nNumber
			){
		this.stripWg = stripWg ;
		lambdaNm = stripWg.getWavelengthNm() ;
		slabX = stripWg.getSlabX() ; // for the TE mode
		slabY = stripWg.getSlabY() ; // for the TM mode
		slabTE = new ModeSlabWgTE_old(slabX) ;
		slabTM = new ModeSlabWgTM_old(slabY) ;
		neff_x = slabTE.findSpecificModeIndex(mNumber+1) ;
		neff_y = slabTM.findSpecificModeIndex(nNumber+1) ;
	}

	public StripWg getStripWg(){
		return stripWg ;
	}

	public double getWavelengthNm(){
		return lambdaNm ;
	}

	public double getEffectiveIndex(){
		double n_core = stripWg.getCoreIndex() ;
		double neff = Math.sqrt(neff_x*neff_x + neff_y * neff_y - n_core * n_core) ;
		return neff ;
	}

}