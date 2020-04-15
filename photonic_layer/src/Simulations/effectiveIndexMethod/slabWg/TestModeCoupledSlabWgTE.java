package Simulations.effectiveIndexMethod.slabWg;

import PhotonicElements.EffectiveIndexMethod.ModeSolver.CoupledSlab.ModeCoupledSlabWgTE;
import PhotonicElements.EffectiveIndexMethod.ModeSolver.SlabWg.ModeSlabWgTE_old;
import PhotonicElements.EffectiveIndexMethod.Structures.CoupledSlabWg;
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

public class TestModeCoupledSlabWgTE implements Experiment {
	
	ModeCoupledSlabWgTE slabTE_Coupled ;
	ModeSlabWgTE_old slabTE1, slabTE2 ;
	Wavelength inputLambda ;
	double w1_nm, w2_nm, gap_nm , lambdaNm;
	double n_si, n_sio2 ;
	//*****************************************************
	// Based on Normalized Frequency
	public TestModeCoupledSlabWgTE(
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
		CoupledSlabWg coupledSlab = new CoupledSlabWg(w1_nm, w2_nm, gap_nm, n_sio2, n_si, n_sio2, n_si, n_sio2) ;
		this.slabTE_Coupled = new ModeCoupledSlabWgTE(inputLambda, coupledSlab) ;
		SlabWg slab1 = new SlabWg(inputLambda, w1_nm, n_sio2, n_si, n_sio2) ;
		SlabWg slab2 = new SlabWg(inputLambda, w2_nm, n_sio2, n_si, n_sio2) ;
		slabTE1 = new ModeSlabWgTE_old(slab1) ;
		slabTE2 = new ModeSlabWgTE_old(slab2) ;
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
		dp.addResultProperty("Neff (even) TE", slabTE_Coupled.findNeffEven(1));
		dp.addResultProperty("Neff (odd) TE", slabTE_Coupled.findNeffOdd(1));
		dp.addResultProperty("Neff slab1 (TE)", slabTE1.findSpecificModeIndex(1));
		dp.addResultProperty("Neff slab2 (TE)", slabTE2.findSpecificModeIndex(1));
		man.addDataPoint(dp);
	}

}
