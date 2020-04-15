package PhotonicElements.EffectiveIndexMethod.ModeSolver.CoupledRib;

import PhotonicElements.EffectiveIndexMethod.ModeSolver.CoupledSlab.ModeCoupledSlabWgTM;
import PhotonicElements.EffectiveIndexMethod.ModeSolver.SlabWg.ModeSlabWgTE_old;
import PhotonicElements.EffectiveIndexMethod.Structures.RibWg;
import PhotonicElements.EffectiveIndexMethod.Structures.SlabWg;
import PhotonicElements.Utilities.Wavelength;
import ch.epfl.general_libraries.clazzes.ParamName;

public class ModeCoupledRibWgTE {

	// Slab X --> TM mode (d = widthNm) --> Two coupled Slab Waveguides
	// Slab Y --> TE mode (d = heightNm)
	
	Wavelength inputLambda ;
	SlabWg slabY ;
	ModeSlabWgTE_old slabTE ;
	ModeCoupledSlabWgTM slabTM ;
	RibWg ribWgFirst, ribWgSecond ;
	double lambdaNm, neff_x_Even, neff_x_Odd, neff_y ;
	double gapNm ;
	
	public ModeCoupledRibWgTE(
			@ParamName(name="Wavelength (nm)") Wavelength inputLambda,
			@ParamName(name="Width of First Rib Wg (nm)") double widthNm_firstRib,
			@ParamName(name="Width of Second Rib Wg (nm)") double widthNm_secondRib,
			@ParamName(name="Waveguide height (nm)") double heightNm,
			@ParamName(name="Slab height (nm)") double heightSlabNm,
			@ParamName(name="Gap size (nm)") double gap_nm,
			@ParamName(name="m: TE_(m,n) [m=0,1,2,...]") int mNumber,
			@ParamName(name="n: TE_(m,n) [n=0,1,2,...]") int nNumber
			){
		this.gapNm = gap_nm ;
		this.inputLambda = inputLambda ;
		this.ribWgFirst = new RibWg(inputLambda, widthNm_firstRib, heightNm, heightSlabNm) ;
		this.ribWgSecond = new RibWg(inputLambda, widthNm_secondRib, heightNm, heightSlabNm) ;
		lambdaNm = inputLambda.getWavelengthNm() ;
		slabY = ribWgFirst.getSlabY() ; // for the TE mode
		slabTE = new ModeSlabWgTE_old(slabY) ;
		neff_y = slabTE.findSpecificModeIndex(nNumber+1) ;
		double n_core = ribWgFirst.getCoreIndex(); 
//		double n_cladd = ribWg.getCladIndex() ;
		ModeSlabWgTE_old sideSlabY_TE = new ModeSlabWgTE_old(ribWgFirst.getSideSlabY()) ; 
		double neffSideSlabTE = sideSlabY_TE.findSpecificModeIndex(nNumber+1) ;
		slabTM = new ModeCoupledSlabWgTM(lambdaNm, ribWgFirst.getWidthNm(), ribWgSecond.getWidthNm(), gap_nm, neffSideSlabTE, n_core, neffSideSlabTE, n_core, neffSideSlabTE) ;
		neff_x_Even = slabTM.findNeffEven(mNumber+1) ;
		neff_x_Odd = slabTM.findNeffOdd(mNumber+1) ;
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
