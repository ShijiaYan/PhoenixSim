package edu.columbia.lrl.experiments.topology_radix.locality;

import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.graphics.pcolor.PcolorGUI;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.utils.Matrix;

public class TrafficMatrixTestExperiment implements Experiment {
	
	private AbstractTrafficMatrix mat;
	private int clients;
	
	public TrafficMatrixTestExperiment(AbstractTrafficMatrix mat, int clients) {
		this.clients = clients;
		this.mat = mat;
	}

	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis) {
		mat.init(clients, null);
		double[][] val = new double[clients][clients];
		for (int i = 0 ; i < clients ; i++)  {
			for (int j = 0 ; j < clients ; j++) {
				val[i][j] = mat.getTraffic(i, j);
			}
		}
		val = Matrix.normalize(val);
		PcolorGUI gui = new PcolorGUI(val);
		gui.showInFrame();
		
	}

}
