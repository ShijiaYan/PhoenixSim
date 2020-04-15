package PhotonicElements.EffectiveIndexMethod.ModeSolver.CoupledRib;

import PhotonicElements.EffectiveIndexMethod.ModeSolver.CoupledSlab.ModeCoupledSlabWgTE;
import PhotonicElements.EffectiveIndexMethod.ModeSolver.SlabWg.ModeSlabWgTM_old;
import PhotonicElements.EffectiveIndexMethod.Structures.RibWg;
import PhotonicElements.EffectiveIndexMethod.Structures.SlabWg;
import PhotonicElements.Utilities.Wavelength;
import ch.epfl.general_libraries.clazzes.ParamName;

public class ModeCoupledRibWgTM {

	// Slab X --> TE mode (d = widthNm) --> Two coupled Slab Waveguides
	// Slab Y --> TM mode (d = heightNm)
	
	Wavelength inputLambda ;
	SlabWg slabY ;
	ModeSlabWgTM_old slabTM ;
	ModeCoupledSlabWgTE slabTE ;
	RibWg ribWgFirst, ribWgSecond ;
	double lambdaNm, neff_x_Even, neff_x_Odd, neff_y ;
	double gapNm ;
	
	public ModeCoupledRibWgTM(
			@ParamName(name="Wavelength (nm)") Wavelength inputLambda,
			@ParamName(name="Width of First Rib Wg (nm)") double widthNm_firstRib,
			@ParamName(name="Width of Second Rib Wg (nm)") double widthNm_secondRib,
			@ParamName(name="Waveguide height (nm)") double heightNm,
			@ParamName(name="Slab height (nm)") double heightSlabNm,
			@ParamName(name="Gap size (nm)") double gap_nm,
			@ParamName(name="m: TM_(m,n) [m=0,1,2,...]") int mNumber,
			@ParamName(name="n: TM_(m,n) [n=0,1,2,...]") int nNumber
			){
		this.gapNm = gap_nm ;
		this.inputLambda = inputLambda ;
		this.ribWgFirst = new RibWg(inputLambda, widthNm_firstRib, heightNm, heightSlabNm) ;
		this.ribWgSecond = new RibWg(inputLambda, widthNm_secondRib, heightNm, heightSlabNm) ;
		lambdaNm = inputLambda.getWavelengthNm() ;
		slabY = ribWgFirst.getSlabY() ; // for the TE mode
		slabTM = new ModeSlabWgTM_old(slabY) ;
		neff_y = slabTM.findSpecificModeIndex(nNumber+1) ;
		double n_core = ribWgFirst.getCoreIndex(); 
//		double n_cladd = ribWg.getCladIndex() ;
		ModeSlabWgTM_old sideSlabY_TM = new ModeSlabWgTM_old(ribWgFirst.getSideSlabY()) ; 
		double neffSideSlabTM = sideSlabY_TM.findSpecificModeIndex(nNumber+1) ;
		slabTE = new ModeCoupledSlabWgTE(lambdaNm, ribWgFirst.getWidthNm(), ribWgSecond.getWidthNm(), gap_nm, neffSideSlabTM, n_core, neffSideSlabTM, n_core, neffSideSlabTM) ;
		neff_x_Even = slabTE.findNeffEven(mNumber+1) ;
		neff_x_Odd = slabTE.findNeffOdd(mNumber+1) ;
	}
	
	public double getWavelengthNm(){
		return lambdaNm ;
	}
	
	public double getGapNm(){
		return gapNm ;
	}
	
	public double getNeffEven(){
		double n_core = ribWgFirst.getCoreIndex() ;
		double neff = Math.sqrt(neff_x_Even*neff_x_Even + neff_y * neff_y - n_core * n_core) ;
		return neff ;
	}
	
	public double getNeffOdd(){
		double n_core = ribWgFirst.getCoreIndex() ;
		double neff = Math.sqrt(neff_x_Odd*neff_x_Odd + neff_y * neff_y - n_core * n_core) ;
		return neff ;
	}
	
	
}
