package PhotonicElements.EffectiveIndexMethod.ModeSensitivity;

import PhotonicElements.EffectiveIndexMethod.ModeSolver.RibWg.ModeRibWgTE;
import PhotonicElements.EffectiveIndexMethod.Structures.RibWg;
import PhotonicElements.Utilities.Wavelength;
import ch.epfl.general_libraries.clazzes.ParamName;

public class NeffVariationRibWg {
	
	// Note that this class is written for rib waveguide and TE0 mode. We can write a separate class for the TM modes in case we need that.
	
	Wavelength inputLambda ;
	double DnSi, DnSiO2, DwNm, DhNm, DhSlabNm, neff_original, neff_perturbed, Dneff ;
	
	public NeffVariationRibWg(
			@ParamName(name="Wavelength (nm)") Wavelength inputLambda,
			@ParamName(name="Waveguide Width (nm)") double widthNm,
			@ParamName(name="Waveguide Height (nm)") double heightNm,
			@ParamName(name="Slab Height (nm)") double heightSlabNm,
			@ParamName(name="Chagne of WG width (nm)") double DwNm,
			@ParamName(name="Chagne of WG height (nm)") double DhNm,
			@ParamName(name="Chagne of Slab height (nm)") double DhSlabNm,
			@ParamName(name="Change of Silicon Index") double DnSi,
			@ParamName(name="Chagne of Silica Index") double DnSiO2
			){
		this.inputLambda = inputLambda ;
		this.DnSi = DnSi ;
		this.DnSiO2 = DnSiO2 ;
		this.DwNm = DwNm ;
		this.DhNm = DhNm ;
		this.DhSlabNm = DhSlabNm ;
		RibWg ribWg_original = new RibWg(inputLambda, widthNm, heightNm, heightSlabNm, 0, 0) ;
		RibWg ribWg_perturbed = new RibWg(inputLambda, widthNm + DwNm, heightNm + DhNm, heightSlabNm+DhSlabNm, DnSi, DnSiO2) ;
		ModeRibWgTE ribTE_original = new ModeRibWgTE(ribWg_original, 0, 0) ; // for TE00 mode
		ModeRibWgTE ribTE_perturbed = new ModeRibWgTE(ribWg_perturbed, 0, 0) ; // for TE00 mode
		neff_original = ribTE_original.getEffectiveIndex() ;
		neff_perturbed = ribTE_perturbed.getEffectiveIndex() ;
		Dneff = neff_perturbed - neff_original ;
	}
	
	public double getLambdaNm(){
		return inputLambda.getWavelengthNm() ;
	}
	
	public double getDnSi(){
		return DnSi ;
	}
	
	public double getDnSiO2(){
		return DnSiO2 ;
	}
	
	public double getDwNm(){
		return DwNm ;
	}
	
	public double getDhNm(){
		return DhNm ;
	}
	
	public double getDhSlabNm(){
		return DhSlabNm ;
	}
	
	public double getDNeffDnSiCoeff(){
		return Dneff/DnSi ;
	}
	
	public double getDNeffDnSiO2Coeff(){
		return Dneff/DnSiO2 ;
	}
	
	public double getDNeffDwNmCoeff(){
		return Dneff/DwNm ;
	}
	
	public double getDNeffDhNmCoeff(){
		return Dneff/DhNm ;
	}
	
	public double getDNeffDhSlabNmCoeff(){
		return Dneff/DhSlabNm ;
	}
	
	public double getDNeff(){
		return Dneff ;
	}
	
	public double getNeffOriginal(){
		return neff_original ;
	}
	
	public double getNeffPerturbed(){
		return neff_perturbed ;
	}
	
}
