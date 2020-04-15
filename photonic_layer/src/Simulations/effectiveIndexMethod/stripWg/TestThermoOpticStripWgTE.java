package Simulations.effectiveIndexMethod.stripWg;

import PhotonicElements.EffectiveIndexMethod.ThermoOpticEffect.StripWg.ThermoOpticStripWgTE;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;

public class TestThermoOpticStripWgTE implements Experiment {

	ThermoOpticStripWgTE thermoWg ;
	
	public TestThermoOpticStripWgTE(
			ThermoOpticStripWgTE thermoWg
			) {
		this.thermoWg = thermoWg ;
	}
	
	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {
		DataPoint dp = new DataPoint() ;
		dp.addProperty("Temperature (k)", thermoWg.getTemperature());
		dp.addProperty("Temperature Change (K)", thermoWg.getDT());
		dp.addResultProperty("DnSi", thermoWg.getDnSi());
		dp.addResultProperty("DnSiO2", thermoWg.getDnSiO2());
		dp.addResultProperty("Dw", thermoWg.getDwNm());
		dp.addResultProperty("Dh", thermoWg.getDhNm());
		dp.addResultProperty("width (nm)", thermoWg.getWidthNm());
		dp.addResultProperty("height (nm)", thermoWg.getHeightNm());
		dp.addResultProperty("Si index", thermoWg.getSiIndex());
		dp.addResultProperty("SiO2 index", thermoWg.getSiO2Index());
		dp.addResultProperty("Neff", thermoWg.getNeff());
		dp.addResultProperty("DT (K)", thermoWg.getDT());
		dp.addResultProperty("DNeff", thermoWg.getDNeff());
		dp.addResultProperty("DNeff/DT", thermoWg.getDNeff_DT());
		dp.addResultProperty("Ng", thermoWg.getNg());
		
		dp.addResultProperty("DnSi/nSi (%)", thermoWg.getDnSi()/(thermoWg.getSiIndex()-thermoWg.getDnSi()) * 100);
		dp.addResultProperty("DnSiO2/nSiO2 (%)", thermoWg.getDnSiO2()/(thermoWg.getSiO2Index()-thermoWg.getDnSiO2()) * 100);
		dp.addResultProperty("Dw/w (%)", thermoWg.getDwNm()/(thermoWg.getWidthNm()-thermoWg.getDwNm()) * 100);
		dp.addResultProperty("Dh/h (%)", thermoWg.getDhNm()/(thermoWg.getHeightNm() - thermoWg.getDhNm()) * 100);
		dp.addResultProperty("DNeff/Neff (%)", thermoWg.getDNeff()/(thermoWg.getNeff()-thermoWg.getDNeff()) * 100);
		
		man.addDataPoint(dp);
	}

}
