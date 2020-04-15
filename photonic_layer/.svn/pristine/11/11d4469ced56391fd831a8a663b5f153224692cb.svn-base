package Simulations.effectiveIndexMethod.slabWg;

import PhotonicElements.EffectiveIndexMethod.ModeProfile.CoupledSlabWg.ProfileCoupledSlabWgTM;
import PhotonicElements.Utilities.MathLibraries.Complex;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;

public class TestProfileCoupledSlabTM implements Experiment {

	ProfileCoupledSlabWgTM coupledTM ;
	
	public TestProfileCoupledSlabTM(
			ProfileCoupledSlabWgTM coupledTM
			){
		this.coupledTM = coupledTM ;
	}

	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {
		DataPoint dp = new DataPoint() ;
		dp.addProperty("X (nm)", coupledTM.getPositionNm());
//		dp.addProperty("Mode Number", coupledTE.getModeNumber());
		dp.addResultProperty("Mode Effective Index", coupledTM.getNeff());
		// Find the components of electric and magnetic fields
		Complex Ex_field = coupledTM.get_Ex_field() ;
		Complex Ey_field = coupledTM.get_Ey_field() ;
		Complex Ez_field = coupledTM.get_Ez_field() ;
		
		Complex Hx_field = coupledTM.get_Hx_field() ;
		Complex Hy_field = coupledTM.get_Hy_field() ;
		Complex Hz_field = coupledTM.get_Hz_field() ;
		
		// plot the real and imaginary parts of fields
		dp.addResultProperty("TM profile - real(Ex)", Ex_field.re());
		dp.addResultProperty("TM profile - imag(Ex)", Ex_field.im());
		dp.addResultProperty("TM profile - real(Ey)", Ey_field.re());
		dp.addResultProperty("TM profile - imag(Ey)", Ey_field.im());
		dp.addResultProperty("TM profile - real(Ez)", Ez_field.re());
		dp.addResultProperty("TM profile - imag(Ez)", Ez_field.im());
		
		dp.addResultProperty("TM profile - real(Hx)", Hx_field.re());
		dp.addResultProperty("TM profile - imag(Hx)", Hx_field.im());
		dp.addResultProperty("TM profile - real(Hy)", Hy_field.re());
		dp.addResultProperty("TM profile - imag(Hy)", Hy_field.im());
		dp.addResultProperty("TM profile - real(Hz)", Hz_field.re());
		dp.addResultProperty("TM profile - imag(Hz)", Hz_field.im());
		
		man.addDataPoint(dp);
	}

}
