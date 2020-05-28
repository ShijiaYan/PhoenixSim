package Simulations.couplers;

import PhotonicElements.DirectionalCoupler.DistributedCoupler.DistributedCouplerStripWg;
import PhotonicElements.Utilities.MathLibraries.MoreMath;
import PhotonicElements.Utilities.Wavelength;
import PhotonicElements.Waveguides.WaveguideProperties.WgProperties;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;

public class TestDirectionalCoupler implements Experiment {

	DistributedCouplerStripWg coupler ;
	
	public TestDirectionalCoupler(
			@ParamName(name="Set Wavelength (nm)") Wavelength inputLambda,
			@ParamName(name="Set Waveguide Properties") WgProperties wgProp,
			@ParamName(name="Length of the coupling region (micron)") double lengthMicron ,
			@ParamName(name="Gap Size (nm)") double gapNm
			){
		this.coupler = new DistributedCouplerStripWg(inputLambda, wgProp, lengthMicron, gapNm) ;
	}
	
	public TestDirectionalCoupler(
			@ParamName(name="Distributed Coupler") DistributedCouplerStripWg coupler
			){
		this.coupler = coupler ;
	}

	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {
		
		DataPoint dp = new DataPoint() ;
		dp.addProperty("Wavelength (nm)", coupler.inputLambda.getWavelengthNm());
		dp.addProperty("Length of Coupling Region (micron)", coupler.lengthMicron);
		dp.addProperty("Gap size (nm)", coupler.gapNm);
		dp.addResultProperty("|S21|^2 (dB)", 10*Math.log10(coupler.S21.absSquared()));
		dp.addResultProperty("|S21|^2", coupler.S21.absSquared());
		dp.addResultProperty("|S31|^2 (dB)", 10*Math.log10(coupler.S31.absSquared()));
		dp.addResultProperty("|S31|^2", coupler.S31.absSquared());
		dp.addResultProperty("Loss (dB)", -MoreMath.Conversions.todB(coupler.S21.absSquared()+coupler.S31.absSquared()));
		
		man.addDataPoint(dp);
	}
	
	
	
}
