package Simulations.effectiveIndexMethod.slabWg;

import PhotonicElements.EffectiveIndexMethod.ModeProfile.SlabWg.ProfileSlabWgTM;
import PhotonicElements.EffectiveIndexMethod.ModeSolver.SlabWg.ModeSlabWgTM_old;
import PhotonicElements.EffectiveIndexMethod.Structures.SlabWg;
import PhotonicElements.Utilities.MathLibraries.Complex;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;

public class TestProfileSlabTM implements Experiment {

	ProfileSlabWgTM TMprofile ;
	ModeSlabWgTM_old slabTM ;
	int modeNumber ;
	double xValNm, widthNm ;
	
	public TestProfileSlabTM(
			@ParamName(name="Slab Waveguide") SlabWg slab,
			@ParamName(name="Mode Number") int modeNumber,
			@ParamName(name="X position (nm)") double xValNm
			){
		this.modeNumber = modeNumber ;
		this.xValNm = xValNm ;
		this.widthNm = slab.getWidthNm() ;
		this.TMprofile = new ProfileSlabWgTM(slab, xValNm, modeNumber) ;
	}
	
	
	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {
		DataPoint dp = new DataPoint() ;
		dp.addProperty("X position (nm)", xValNm );
		dp.addProperty("Mode Number", modeNumber);
		// Find the components of electric and magnetic fields
		Complex Ex_field = TMprofile.get_Ex_field() ;
		Complex Ey_field = TMprofile.get_Ey_field() ;
		Complex Ez_field = TMprofile.get_Ez_field() ;
		
		Complex Hx_field = TMprofile.get_Hx_field() ;
		Complex Hy_field = TMprofile.get_Hy_field() ;
		Complex Hz_field = TMprofile.get_Hz_field() ;
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
