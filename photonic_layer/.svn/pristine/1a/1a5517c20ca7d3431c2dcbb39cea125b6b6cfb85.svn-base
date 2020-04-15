package Simulations.materials.dielectrics;

import PhotonicElements.Materials.Dielectric.AbstractDielectric;
import PhotonicElements.Utilities.Wavelength;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;

public class MaterialSim implements Experiment {

	Wavelength inputLambda ;
	AbstractDielectric material ;
	
	public MaterialSim(
			Wavelength inputLambda,
			@ParamName(name="Choose Material") AbstractDielectric material
			) {
		this.inputLambda = inputLambda ;
		this.material = material ;
	}
	
	
	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {
		DataPoint dp = new DataPoint() ;
		dp.addProperty("Wavelength (nm)", inputLambda.getWavelengthNm());
		dp.addResultProperty("Refractive Index", material.getIndex(inputLambda));
		dp.addProperty("Material Name", material.getMaterialName());
		man.addDataPoint(dp);
	}

}
