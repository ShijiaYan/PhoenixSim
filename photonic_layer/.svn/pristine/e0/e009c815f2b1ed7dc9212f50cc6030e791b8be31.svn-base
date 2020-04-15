package PhotonicElements.EffectiveIndexMethod.ModeSolver.RibWg;

import PhotonicElements.EffectiveIndexMethod.ModeSolver.SlabWg.ModeSlabWgTE;
import PhotonicElements.EffectiveIndexMethod.ModeSolver.SlabWg.ModeSlabWgTM;
import PhotonicElements.EffectiveIndexMethod.Structures.RibWg;
import PhotonicElements.EffectiveIndexMethod.Structures.SlabWg;
import PhotonicElements.Utilities.Wavelength;
import ch.epfl.general_libraries.clazzes.ParamName;

public class ModeRibWgTE {

	// Slab X --> TM mode (d = widthNm)
	// Slab Y --> TE mode (d = heightNm)

	SlabWg slabX, slabY, sideSlabY ;
	ModeSlabWgTE slabTE ;
	ModeSlabWgTM slabTM ;
	RibWg ribWg ;
	double lambdaNm, neff_x, neff_y ;
	int mNumber, nNumber ;

	public ModeRibWgTE(
			@ParamName(name="Rib Waveguide") RibWg ribWg,
			@ParamName(name="m: TE_(m,n) [m=0,1,2,...]") int mNumber,
			@ParamName(name="n: TE_(m,n) [n=0,1,2,...]") int nNumber
			){
		this.ribWg = ribWg ;
		lambdaNm = ribWg.getWavelengthNm() ;
		sideSlabY = ribWg.getSideSlabY() ;
		// Check with COMSOL to see which choice (TE or TM) is correct for side slab waveguide --> in the Y direction should be TE
		ModeSlabWgTE sideSlabY_TE = new ModeSlabWgTE(sideSlabY) ;
		double neff_sideSlab_TE = sideSlabY_TE.findSpecificModeIndex(nNumber) ;
		Wavelength inputLambda = new Wavelength(ribWg.getWavelengthNm()) ;
		slabX = new SlabWg(inputLambda, ribWg.getWidthNm(), neff_sideSlab_TE, ribWg.getCoreIndex(), neff_sideSlab_TE) ; // for the TE mode
		slabY = ribWg.getSlabY() ; // for the TM mode
		slabTE = new ModeSlabWgTE(slabY) ;
		slabTM = new ModeSlabWgTM(slabX) ;
		neff_x = slabTM.findSpecificModeIndex(mNumber) ;
		neff_y = slabTE.findSpecificModeIndex(nNumber) ;
		this.mNumber = mNumber ;
		this.nNumber = nNumber ;
	}

	public RibWg getRibWg(){
		return ribWg ;
	}

	public double getWavelengthNm(){
		return lambdaNm ;
	}

	public double getEffectiveIndex(){
		double n_core = ribWg.getCoreIndex() ;
		double neff = Math.sqrt(neff_x*neff_x + neff_y * neff_y - n_core * n_core) ;
		return neff ;
	}

	public int getMnumber(){
		return mNumber;
	}

	public int getNnumber(){
		return nNumber ;
	}

}
