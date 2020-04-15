package PhotonicElements.EffectiveIndexMethod.ModeSolver.CoupledRib;

import PhotonicElements.EffectiveIndexMethod.ModeSolver.CoupledSlab.ModeCoupledSlabWgTM;
import PhotonicElements.EffectiveIndexMethod.ModeSolver.SlabWg.ModeSlabWgTE_old;
import PhotonicElements.EffectiveIndexMethod.Structures.RibWg;
import PhotonicElements.EffectiveIndexMethod.Structures.SlabWg;
import ch.epfl.general_libraries.clazzes.ParamName;

public class ModeCoupledRibWgIdenticalTM {

	// Slab X --> TM mode (d = widthNm) --> Two coupled Slab Waveguides
	// Slab Y --> TE mode (d = heightNm)
	
	SlabWg slabY ;
	ModeSlabWgTE_old slabTE ;
	ModeCoupledSlabWgTM slabTM ;
	RibWg ribWg ;
	double lambdaNm, neff_x_Even, neff_x_Odd, neff_y ;
	
	public ModeCoupledRibWgIdenticalTM(
			@ParamName(name="Rib Waveguide") RibWg ribWg,
			@ParamName(name="Gap size (nm)") double gap_nm,
			@ParamName(name="m: TM_(m,n) [m=0,1,2,...]") int mNumber,
			@ParamName(name="n: TM_(m,n) [n=0,1,2,...]") int nNumber
			){
		this.ribWg = ribWg ;
		lambdaNm = ribWg.getWavelengthNm() ;
		slabY = ribWg.getSlabY() ; // for the TE mode
		slabTE = new ModeSlabWgTE_old(slabY) ;
		neff_y = slabTE.findSpecificModeIndex(nNumber+1) ;
		double n_core = ribWg.getCoreIndex(); 
//		double n_cladd = ribWg.getCladIndex() ;
		ModeSlabWgTE_old sideSlabY_TE = new ModeSlabWgTE_old(ribWg.getSideSlabY()) ; 
		double neffSideSlabTE = sideSlabY_TE.findSpecificModeIndex(nNumber+1) ;
		slabTM = new ModeCoupledSlabWgTM(lambdaNm, ribWg.getWidthNm(), ribWg.getWidthNm(), gap_nm, neffSideSlabTE, n_core, neffSideSlabTE, n_core, neffSideSlabTE) ;
		neff_x_Even = slabTM.findNeffEven(mNumber+1) ;
		neff_x_Odd = slabTM.findNeffOdd(mNumber+1) ;
	}
	
	public double getWavelengthNm(){
		return lambdaNm ;
	}
	
	public double getNeffEven(){
		double n_core = ribWg.getCoreIndex() ;
		double neff = Math.sqrt(neff_x_Even*neff_x_Even + neff_y * neff_y - n_core * n_core) ;
		return neff ;
	}
	
	public double getNeffOdd(){
		double n_core = ribWg.getCoreIndex() ;
		double neff = Math.sqrt(neff_x_Odd*neff_x_Odd + neff_y * neff_y - n_core * n_core) ;
		return neff ;
	}
	
	public RibWg getRibWg(){
		return ribWg ;
	}
	
}
