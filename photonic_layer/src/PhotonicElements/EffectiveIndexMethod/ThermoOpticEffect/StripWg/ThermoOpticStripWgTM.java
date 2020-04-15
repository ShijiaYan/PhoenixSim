package PhotonicElements.EffectiveIndexMethod.ThermoOpticEffect.StripWg;

import PhotonicElements.EffectiveIndexMethod.ModeSolver.StripWg.ModeStripWgTM;
import PhotonicElements.EffectiveIndexMethod.Structures.StripWg;
import PhotonicElements.Utilities.Wavelength;
import ch.epfl.general_libraries.clazzes.ParamName;

public class ThermoOpticStripWgTM {

	Wavelength inputLambda ;
	double alphaSi = 2.6e-6, alphaSiO2 = 5.6e-7 ;
	double dnSi_dT = 1.8e-4, dnSiO2_dT = 8.66e-6 ;
	double widthNm, heightNm, DT, DwNm, DhNm, DnSi, DnSiO2 ;
	double defaultTemperature = 27, temperature ; // oC
	double neff_original, neff_perturbed, Dneff ;
	StripWg stripWg_original, stripWg_perturbed ;
	
	public ThermoOpticStripWgTM(
			@ParamName(name="Strip Waveguide") StripWg stripWg,
			@ParamName(name="Temperature (C) ") double temperature
			){
		this.stripWg_original = stripWg ;
		this.temperature = temperature ;
		inputLambda = stripWg.getInputLambda() ;
		widthNm = stripWg.getWidthNm() ;
		heightNm = stripWg.getHeightNm() ;
		DT = temperature - defaultTemperature ;
		DwNm = widthNm * alphaSi * DT ;
		DhNm = heightNm * alphaSi * DT ;
		DnSi = dnSi_dT * DT ;
		DnSiO2 = dnSiO2_dT * DT ;
		this.stripWg_perturbed = new StripWg(inputLambda, widthNm + DwNm, heightNm + DhNm, DnSi, DnSiO2) ;
		ModeStripWgTM stripTM_original = new ModeStripWgTM(stripWg_original, 0, 0) ;
		ModeStripWgTM stripTM_perturbed = new ModeStripWgTM(stripWg_perturbed, 0, 0) ;
		neff_original = stripTM_original.getEffectiveIndex() ;
		neff_perturbed = stripTM_perturbed.getEffectiveIndex() ;
		Dneff = neff_perturbed - neff_original ;
	}
	
	public void setReferenceTemperature(double refTempC){
		defaultTemperature = refTempC ;
	}
	
	// this is in case user changes the reference temperature
	public void reCalculate(){
		inputLambda = stripWg_original.getInputLambda() ;
		widthNm = stripWg_original.getWidthNm() ;
		heightNm = stripWg_original.getHeightNm() ;
		DT = temperature - defaultTemperature ;
		DwNm = widthNm * alphaSi * DT ;
		DhNm = heightNm * alphaSi * DT ;
		DnSi = dnSi_dT * DT ;
		DnSiO2 = dnSiO2_dT * DT ;
		stripWg_perturbed = new StripWg(inputLambda, widthNm + DwNm, heightNm + DhNm, DnSi, DnSiO2) ;
		ModeStripWgTM stripTM_original = new ModeStripWgTM(stripWg_original, 0, 0) ;
		ModeStripWgTM stripTM_perturbed = new ModeStripWgTM(stripWg_perturbed, 0, 0) ;
		neff_original = stripTM_original.getEffectiveIndex() ;
		neff_perturbed = stripTM_perturbed.getEffectiveIndex() ;
		Dneff = neff_perturbed - neff_original ;
	}
	
	public double getNg(){
        double dlambda_nm = 1e-1 ;
        double lambda_nm = inputLambda.getWavelengthNm() ;
        double lambda_max_nm, lambda_min_nm, neff_max, neff_min ;
        lambda_max_nm = lambda_nm + dlambda_nm ;
        lambda_min_nm = lambda_nm ;
        neff_min = neff_perturbed ;
        Wavelength inputLambda = new Wavelength(lambda_max_nm) ;
        StripWg stripWg = new StripWg(inputLambda, widthNm + DwNm, heightNm + DhNm, DnSi, DnSiO2) ;
        ModeStripWgTM modeSolver = new ModeStripWgTM(stripWg, 0, 0) ;
        neff_max = modeSolver.getEffectiveIndex() ;
        double dneff_dlambda = (neff_max-neff_min)/(lambda_max_nm-lambda_min_nm) ;
        return (neff_min - lambda_nm * dneff_dlambda) ;
	}
	
	public Wavelength getInputLambda(){
		return inputLambda ;
	}
	
	public double getDT(){
		return DT ;
	}
	
	public double getTemperature(){
		return temperature ;
	}
	
	public double getDNeff(){
		return Dneff ;
	}
	
	public double getDNeff_DT(){
		return Dneff/DT ;
	}
	
	public double getNeff(){
		return neff_perturbed ;
	}
	
	public double getDwNm(){
		return DwNm ;
	}
	
	public double getDhNm(){
		return DhNm ;
	}
	
	public double getWidthNm(){
		return (widthNm+DwNm) ;
	}
	
	public double getHeightNm(){
		return (heightNm+DhNm) ;
	}
	
	public double getSiIndex(){
		return stripWg_perturbed.getCoreIndex() ;
	}
	
	public double getSiO2Index(){
		return stripWg_perturbed.getCladIndex() ;
	}
	
	public double getDnSi(){
		return DnSi ;
	}
	
	public double getDnSiO2(){
		return DnSiO2 ;
	}
	
}
