package Simulations.effectiveIndexMethod.slabWg;

import PhotonicElements.EffectiveIndexMethod.ModeSolver.CoupledSlab.ModeCoupledSlabWgTM;
import PhotonicElements.EffectiveIndexMethod.ModeSolver.SlabWg.ModeSlabWgTM_old;
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

public class TestModeCoupledSlabWgTM implements Experiment {
	
	ModeCoupledSlabWgTM slabTM_Coupled ;
	ModeSlabWgTM_old slabTM ;
	Wavelength inputLambda ;
	double w1_nm, w2_nm, gap_nm , lambdaNm;
	double n_si, n_sio2 ;
	//*****************************************************
	// Based on Normalized Frequency
	public TestModeCoupledSlabWgTM(
			@ParamName(name="Wavelength (nm)") Wavelength inputLambda,
			@ParamName(name="width of slab 1 (nm)") double w1_nm,
			@ParamName(name="width of slab 2 (nm)") double w2_nm,
			@ParamName(name="gap size (nm)") double gap_nm
			){
		this.w1_nm = w1_nm ;
		this.w2_nm = w2_nm ;
		this.gap_nm = gap_nm ;
		this.inputLambda = inputLambda ;
		Silicon Si = new Silicon() ;
		Silica SiO2 = new Silica() ;
		n_si = Si.getIndex(inputLambda) ;
		n_sio2 = SiO2.getIndex(inputLambda) ; 
		this.lambdaNm = inputLambda.getWavelengthNm() ;
		this.slabTM_Coupled = new ModeCoupledSlabWgTM(inputLambda.getWavelengthNm(), w1_nm, w2_nm, gap_nm, n_sio2, n_si, n_sio2, n_si, n_sio2) ;
		SlabWg slab = new SlabWg(inputLambda, w1_nm, n_sio2, n_si, n_sio2) ;
		this.slabTM = new ModeSlabWgTM_old(slab) ;
	}
	//*****************************************************
	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {

		DataPoint dp = new DataPoint() ;
		dp.addProperty("width of slab 1 (nm)", w1_nm);
		dp.addProperty("width of slab 2 (nm)", w2_nm);
		dp.addProperty("gap size (nm)", gap_nm);
		dp.addProperty("wavelength (nm)", lambdaNm);
		dp.addResultProperty("Neff (even) TM", slabTM_Coupled.findNeffEven(1));
		dp.addResultProperty("Neff (odd) TM", slabTM_Coupled.findNeffOdd(1));
		dp.addResultProperty("Neff TM", slabTM.findSpecificModeIndex(1));
		man.addDataPoint(dp);
	}

}
