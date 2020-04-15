package NASA.Tests;

import NASA.Graphene.Graphene;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;

public class TestGraphene implements Experiment{

	Graphene gr;
	double fermi_eV ;
	
	
	public TestGraphene(
			Graphene gr,
			@ParamName(name="Fermi Level (eV)") double fermi_eV
			){
		this.gr = gr;
		this.fermi_eV = fermi_eV ;
	}
	
	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis) throws WrongExperimentException {
		DataPoint dp = new DataPoint();
		
		//plotting Fermi energy level
		dp.addProperty("Voltage (V)", gr.getVin());
		dp.addResultProperty("Voltage", gr.getVin());
		dp.addResultProperty("Fermi level - calculated (eV)", gr.getFermiLevel_eV());
		dp.addProperty("Fermi Level - input (eV)", fermi_eV);
		dp.addResultProperty("Real Epsilon Calculated", gr.getRealEpsilon(fermi_eV));
		dp.addResultProperty("Imag Epsilon Calculated", gr.getImagEpsilon(fermi_eV));
		dp.addResultProperty("|Epsilon|", gr.getComplexExpsilon(fermi_eV).abs());
		dp.addResultProperty("Graphene real index", gr.getIndex(fermi_eV));
		dp.addResultProperty("Graphene imag index", gr.getImagIndex(fermi_eV));
		
		man.addDataPoint(dp);
	}

}
