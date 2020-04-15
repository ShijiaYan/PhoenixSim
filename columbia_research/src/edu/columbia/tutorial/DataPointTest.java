package edu.columbia.tutorial;

import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.general_libraries.results.Execution;
import ch.epfl.javancox.results_manager.SmartDataPointCollector;
import ch.epfl.javancox.results_manager.gui.DefaultResultDisplayingGUI;

public class DataPointTest {
	
	public static void main(String[] args) {
		double a = 4;
		double b = 10;
		
		Execution ex = new Execution();
		
		for (int g = 10 ;g < 30 ; g++) {
			double result = a/b + Math.cos(g);
			
			
			DataPoint dp = new DataPoint();
			
			dp.addProperty("a", a);
			dp.addProperty("b", b);
			dp.addProperty("g", g);
			
			dp.addResultProperty("Result", result);
			
			ex.addDataPoint(dp);
		}
		
		SmartDataPointCollector col = new SmartDataPointCollector();
		
		col.addExecution(ex);
		
		DefaultResultDisplayingGUI.displayDefault(col);
	}

}
