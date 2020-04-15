package Simulations.effectiveIndexMethod.transferMatrix;

import PhotonicElements.EffectiveIndexMethod.InterfaceTransferMatrix.TransferMatrixTE;
import PhotonicElements.Utilities.Wavelength;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;

public class TransferMatrixTESim implements Experiment {

	TransferMatrixTE Qmatrix ;
	Wavelength inputLambda ;
	double angleOfIncidenceDegree ;
	
	public TransferMatrixTESim(
			@ParamName(name="wavelength (nm)") Wavelength inputLambda,
			@ParamName(name="index of the first material") double nFirst,
			@ParamName(name="index of the second material") double nSecond,
			@ParamName(name="angle of incidence (degree)") double thetaFirstDegree,
			@ParamName(name="position of the interface (nm)") double Xnm
			){
		this.inputLambda = inputLambda ;
		angleOfIncidenceDegree = thetaFirstDegree ;
		Qmatrix = new TransferMatrixTE(inputLambda, nFirst, nSecond, thetaFirstDegree, Xnm) ;
	}
	
	
	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {
		DataPoint dp = new DataPoint() ;
		dp.addProperty("Wavelength (nm)", inputLambda.getWavelengthNm());
		dp.addProperty("Angle of Incidence (degree)", angleOfIncidenceDegree);
		dp.addResultProperty("Angle of Refraction - real part (degree)", Qmatrix.getComplexAngleOfRefractionDegree().re()) ;
		dp.addResultProperty("Angle of Refraction - imag part (degree)", Qmatrix.getComplexAngleOfRefractionDegree().im()) ;
		dp.addResultProperty("Angle of Reflection - real part (degree)", Qmatrix.getComplexAngleOfReflectionDegree().re());
		dp.addResultProperty("Angle of Reflection - imag part (degree)", Qmatrix.getComplexAngleOfReflectionDegree().im());
		dp.addResultProperty("Reflectance", Qmatrix.getPowerReflection());
		dp.addResultProperty("Transmittance", Qmatrix.getPowerTransmission());
		man.addDataPoint(dp); 
	}
	

}
