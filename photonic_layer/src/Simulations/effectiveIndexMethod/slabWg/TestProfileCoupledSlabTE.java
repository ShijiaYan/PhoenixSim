package Simulations.effectiveIndexMethod.slabWg;

import PhotonicElements.EffectiveIndexMethod.ModeProfile.CoupledSlabWg.ProfileCoupledSlabWgTE;
import PhotonicElements.Utilities.MathLibraries.Complex;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;

public class TestProfileCoupledSlabTE implements Experiment {

	ProfileCoupledSlabWgTE coupledTE ;
	
	public TestProfileCoupledSlabTE(
			ProfileCoupledSlabWgTE coupledTE
			){
		this.coupledTE = coupledTE ;
	}

	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {
		DataPoint dp = new DataPoint() ;
		dp.addProperty("X (nm)", coupledTE.getPositionNm());
//		dp.addProperty("Mode Number", coupledTE.getModeNumber());
		dp.addResultProperty("Mode Effective Index", coupledTE.getNeff());
		// Find the components of electric and magnetic fields
		Complex Ex_field = coupledTE.get_Ex_field() ;
		Complex Ey_field = coupledTE.get_Ey_field() ;
		Complex Ez_field = coupledTE.get_Ez_field() ;
		
		Complex Hx_field = coupledTE.get_Hx_field() ;
		Complex Hy_field = coupledTE.get_Hy_field() ;
		Complex Hz_field = coupledTE.get_Hz_field() ;
		
		// plot the real and imaginary parts of fields
		dp.addResultProperty("TE profile - real(Ex)", Ex_field.re());
		dp.addResultProperty("TE profile - imag(Ex)", Ex_field.im());
		dp.addResultProperty("TE profile - real(Ey)", Ey_field.re());
		dp.addResultProperty("TE profile - imag(Ey)", Ey_field.im());
		dp.addResultProperty("TE profile - real(Ez)", Ez_field.re());
		dp.addResultProperty("TE profile - imag(Ez)", Ez_field.im());
		
		dp.addResultProperty("TE profile - real(Hx)", Hx_field.re());
		dp.addResultProperty("TE profile - imag(Hx)", Hx_field.im());
		dp.addResultProperty("TE profile - real(Hy)", Hy_field.re());
		dp.addResultProperty("TE profile - imag(Hy)", Hy_field.im());
		dp.addResultProperty("TE profile - real(Hz)", Hz_field.re());
		dp.addResultProperty("TE profile - imag(Hz)", Hz_field.im());
		
		man.addDataPoint(dp);
	}

}
