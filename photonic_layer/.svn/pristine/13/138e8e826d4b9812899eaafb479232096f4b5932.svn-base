package GDS.Layout.Executable;

import GDS.IO.PythonCompiler;
import GDS.Layout.Cells.Cell;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;

public class Create_New_Cell implements Experiment {

	Cell cell ;

	public Create_New_Cell(
			@ParamName(name="Design your Cell") Cell cell
			){
		this.cell = cell ;
	}

	@SuppressWarnings("static-access")
	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis) throws WrongExperimentException {
		// creating the gds file
		PythonCompiler p = new PythonCompiler(cell) ;
		p.main(null);
		AbstractResultsDisplayer.showGUI = false ;
	}


}
