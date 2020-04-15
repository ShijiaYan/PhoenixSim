package Simulations.effectiveIndexMethod.ribWg;

import PhotonicElements.EffectiveIndexMethod.ModeSensitivity.NeffVariationRibWg;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;

public class TestNeffVariationRibWg implements Experiment {

	NeffVariationRibWg DNeff ;
	
	public TestNeffVariationRibWg(
			NeffVariationRibWg DNeff
			){
		this.DNeff = DNeff ;
	}
	
	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {
		DataPoint dp = new DataPoint() ;
		dp.addProperty("Wavelength (nm)", DNeff.getLambdaNm());
		dp.addProperty("DnSi", DNeff.getDnSi());
		dp.addProperty("DnSiO2", DNeff.getDnSiO2());
		dp.addProperty("Dw (nm)", DNeff.getDwNm());
		dp.addProperty("Dh (nm)", DNeff.getDhNm());
		dp.addProperty("DhSlab (nm)", DNeff.getDhSlabNm());
		dp.addResultProperty("Neff (original)", DNeff.getNeffOriginal());
		dp.addResultProperty("Neff (new)", DNeff.getNeffPerturbed());
		dp.addResultProperty("DNeff", DNeff.getDNeff());
		dp.addResultProperty("DNeff/DwNm", DNeff.getDNeffDwNmCoeff());
		dp.addResultProperty("DNeff/DhNm", DNeff.getDNeffDhNmCoeff());
		dp.addResultProperty("DNeff/DhSlabNm", DNeff.getDNeffDhSlabNmCoeff());
		dp.addResultProperty("DNeff/DnSi", DNeff.getDNeffDnSiCoeff());
		dp.addResultProperty("DNeff/DnSiO2", DNeff.getDNeffDnSiO2Coeff());
		man.addDataPoint(dp);
	}

}
