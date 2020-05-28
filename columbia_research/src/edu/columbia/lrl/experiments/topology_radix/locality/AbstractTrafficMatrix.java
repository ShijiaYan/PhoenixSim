package edu.columbia.lrl.experiments.topology_radix.locality;

import java.util.Map;

import ch.epfl.general_libraries.experiment_aut.ExperimentBlock;
import ch.epfl.general_libraries.graphics.pcolor.PcolorGUI;
import ch.epfl.general_libraries.utils.Matrix;
import ch.epfl.general_libraries.utils.SimpleMap;
import edu.columbia.lrl.experiments.topology_radix.routing_study.GlobalStructure;

public abstract class AbstractTrafficMatrix implements ExperimentBlock {
	
	protected int clients;
	protected GlobalStructure gs;
	protected double normLoad;
	
	public AbstractTrafficMatrix() {}
	
	public AbstractTrafficMatrix(double normLoad) {
		this.normLoad = normLoad;
	}
	
	public void setNormalisedLoad(double load) {
		this.normLoad = load;
	}	

	
	public void init(int clients, GlobalStructure gs) {
		this.clients = clients;
		this.gs = gs;
	}
	
	public abstract double getTraffic(int src, int dest);
	public abstract double getTrafficFrom(int src);
	public double[] getTrafficVectorFrom(int src) {
		double[] t = new double[clients];
		for (int i = 0 ; i < clients ; i++) {
			t[i] = getTraffic(src, i);
		}
		return t;
	}
	
	public double getTraffic(int srcStartIndex, int srcRange, int destStartIndex, int destRange) {
		if (clients == 0) 
			throw new IllegalStateException("Traffic matrix size unset");
		double sum = 0;
		for (int i = srcStartIndex ; i < srcStartIndex + srcRange ; i++) {
			sum += getTrafficFrom(i%clients, destStartIndex, destRange);
		}
		return sum;
	}
	
	public double getTrafficFrom(int src, int startDest, int range) {
		double tot = 0;
		for (int i = startDest ; i < range + startDest ; i++) {
			tot += getTraffic(src, i%clients);
		}
		return tot;
	}	
	
	public void showPColor() {
		double[][] intensity = new double[clients][clients];
		for (int i = 0 ; i < clients ; i++) {
			for (int j = 0 ; j < clients ; j++) {
				intensity[i][j] = getTraffic(i, j);
			}
		}
		PcolorGUI gui = new PcolorGUI("Traffic_"+this.getClass().getSimpleName(), Matrix.normalize(intensity));
		gui.showInFrame();
	}
	
	@Override
	public Map<String, String> getAllParameters() {
		Map<String, String> m = new SimpleMap<>();
		m.put("Normalized load", normLoad+"");
		m.put("Traffic matrix type",this.getClass().getSimpleName());
		return m;
	}




}
