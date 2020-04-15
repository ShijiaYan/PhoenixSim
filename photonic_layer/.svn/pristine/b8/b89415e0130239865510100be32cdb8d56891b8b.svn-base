package Simulations.effectiveIndexMethod.stripWg;

import PhotonicElements.EffectiveIndexMethod.ModeProfile.StripWg.ProfileStripWgTE;
import PhotonicElements.EffectiveIndexMethod.Structures.StripWg;
import PhotonicElements.Utilities.MathLibraries.Complex;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;

public class TestProfileStripWgTE implements Experiment {

	double xValNm, yValNm ;
	int mIndex, nIndex ;
	ProfileStripWgTE TEprofile ;
	
	public TestProfileStripWgTE(
			@ParamName(name="Strip Waveguide") StripWg stripWg,
			@ParamName(name="X position (nm)") double xValNm,
			@ParamName(name="Y position (nm)") double yValNm,
			@ParamName(name="m index") int mIndex,
			@ParamName(name="n index") int nIndex
			){
		this.xValNm = xValNm ;
		this.yValNm = yValNm ;
		this.mIndex = mIndex ;
		this.nIndex = nIndex ;
		this.TEprofile = new ProfileStripWgTE(stripWg, mIndex, nIndex, xValNm, yValNm) ;
	}
	
	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis) throws WrongExperimentException {
		DataPoint dp = new DataPoint() ;
		dp.addProperty("X position (nm)", xValNm );
		dp.addProperty("Y position (nm)", yValNm );
		dp.addProperty("m index", mIndex);
		dp.addProperty("n index", nIndex);
		// Find the components of electric and magnetic fields
		Complex Ex_field = TEprofile.get_Ex_field() ;
		Complex Ey_field = TEprofile.get_Ey_field() ;
		Complex Ez_field = TEprofile.get_Ez_field() ;
		
		Complex Hx_field = TEprofile.get_Hx_field() ;
		Complex Hy_field = TEprofile.get_Hy_field() ;
		Complex Hz_field = TEprofile.get_Hz_field() ;
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
		
		dp.addResultProperty("Index", TEprofile.getIndex());
		man.addDataPoint(dp);
	}

}
