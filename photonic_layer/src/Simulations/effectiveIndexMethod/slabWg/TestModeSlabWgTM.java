package Simulations.effectiveIndexMethod.slabWg;

import PhotonicElements.EffectiveIndexMethod.ModeSolver.SlabWg.ModeSlabWgTM;
import PhotonicElements.EffectiveIndexMethod.Structures.SlabWg;
import PhotonicElements.Materials.Dielectric.Silica;
import PhotonicElements.Materials.Dielectric.Silicon;
import PhotonicElements.Utilities.Wavelength;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;

public class TestModeSlabWgTM implements Experiment {
	
	ModeSlabWgTM slabTM ;
	Wavelength inputLambda ;
	double widthNm, V ;
	int modeNumber ;
	Silicon Si ;
	Silica SiO2 ;
	//*****************************************************
	// Based on Normalized Frequency
	public TestModeSlabWgTM(
			@ParamName(name="Normalized Frequency (V)") double V,
			@ParamName(name="waveguide width (nm)") double widthNm,
			@ParamName(name="Substrate index") double n_d,
			@ParamName(name="Core index") double n_c,
			@ParamName(name="Cladd index") double n_u,
			@ParamName(name="Mode Number [0,1,2,...]") int modeNumber
			){
		this.modeNumber = modeNumber ;
		this.widthNm = widthNm ;
		this.V = V ;
		this.slabTM = new ModeSlabWgTM(V, widthNm, n_d, n_c, n_u) ;
		double lambdaNm = 2*Math.PI/V * widthNm * Math.sqrt(n_c*n_c-n_d*n_d) ;
		inputLambda = new Wavelength(lambdaNm) ;
	}
	//*****************************************************
	// Based on the slab model
	SlabWg slab ;
	
	public TestModeSlabWgTM(
			SlabWg slab,
			@ParamName(name="Mode Number [0,1,2,...]") int modeNumber
			){
		this.modeNumber = modeNumber ;
		this.slab = slab ;
		this.widthNm = slab.getWidthNm() ;
		this.V = slab.getNormalizedFreq() ;
		this.slabTM = new ModeSlabWgTM(slab) ;
		double lambdaNm = slab.getWavelengthNm() ;
		inputLambda = new Wavelength(lambdaNm) ;
		this.modeNumber = modeNumber ;
	}
	//*****************************************************
	public TestModeSlabWgTM(
			Wavelength inputLambda,
			@ParamName(name="Waveguide width (nm)") double widthNm,
			@ParamName(name="Mode Number [0,1,2,...]") int modeNumber
			){
		this.modeNumber = modeNumber ;
		this.inputLambda = inputLambda ;
		this.widthNm = widthNm ;
		Si = new Silicon() ;
		SiO2 = new Silica() ;
		SlabWg slab = new SlabWg(inputLambda, widthNm, SiO2.getIndex(inputLambda), Si.getIndex(inputLambda), SiO2.getIndex(inputLambda)) ;
		this.slabTM = new ModeSlabWgTM(slab) ;
	}
	//*****************************************************

	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {

		DataPoint dp = new DataPoint() ;
		dp.addProperty("width (nm)", widthNm);
		dp.addProperty("Mode Number", modeNumber);
		dp.addProperty("Wavelength (nm)", inputLambda.getWavelengthNm());
		dp.addProperty("V", V);
		dp.addProperty("asymmetry factor", slabTM.asymmetryFactor);
		double neff = slabTM.findSpecificModeIndex(modeNumber) ;
		dp.addResultProperty("b", slab.getNormalizedIndex(neff));
		dp.addResultProperty("Neff", neff);
		man.addDataPoint(dp);
	}

}
