package edu.columbia.tutorial;

import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.general_libraries.results.Execution;
import ch.epfl.javancox.results_manager.SmartDataPointCollector;
import ch.epfl.javancox.results_manager.gui.DefaultResultDisplayingGUI;

public class CockpitTutorial1 {
	
	
	public static void main(String[] args) {
		
		CockpitTutorial1 t = new CockpitTutorial1();
		
		SmartDataPointCollector col = new SmartDataPointCollector();
		
		Execution execution = new Execution();
		
		
		for (int i = 0 ; i < 10 ; i++) {
			DataPoint dp = new DataPoint();
			dp.addProperty("param1", i);			
			
			
			for (int j = 0 ; j < 10 ; j++) {
				
				DataPoint dp2 = dp.getDerivedDataPoint();
				dp2.addProperty("param2", j);
				
				double[] d = t.blackbox(i,j);
				
				for (int k = 0 ; k < d.length ; k++) {
					DataPoint dp3 = dp2.getDerivedDataPoint();
					dp3.addProperty("vector index", k);
					dp3.addResultProperty("d", d[k]);
					execution.addDataPoint(dp3);
				}
			}
		}
		col.addExecution(execution);
		
		DefaultResultDisplayingGUI.displayDefault_(col);
		
		
	}
	
	public double[] blackbox(int param1, int param2) {
		
		double[] d = new double[]{1,2,3,4,5,6,7,8};
		
		for (int i = 0 ; i < d.length ; i++) {
			d[i] = d[i] + Math.pow(param1, param2);
		}
		
		return d;
	}
}
