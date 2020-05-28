package archives.common.physical_layer.loss_models;

import java.util.HashMap;
import java.util.Map.Entry;

import archives.common.physical_layer.PscanPhyExperiment;


import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.general_libraries.results.Execution;

public class PscanResult {
	
	int wavelengths;
	private double sum;
	
	public PscanResult(int wavelengths) {
		this.wavelengths = wavelengths;
		addPowerDissipatedDB(10*Math.log10(wavelengths), "WDM", "Wavelength budget");
		sum = 0;
	}
	
	HashMap<String, HashMap<String, Double>> groups = new HashMap<>();
	
	HashMap<String, Double> compParam = new HashMap<>();
	HashMap<String, Double> optParam = new HashMap<>();
	HashMap<String, SpecData> specData = new HashMap<>();
		
	public void addPowerDissipatedDB(double power, String group, String text) {
		HashMap<String, Double> pow = groups.get(group);
		if (pow == null) {
			pow = new HashMap<>();
			groups.put(group, pow);
		}
		
		sum += power;
		pow.put(text, power);
	}
	
	private static class SpecData {
		double[] values;
		double[] indexes;
		String indexName;
	}

	public void addSpecificData(double[] values, double[] indexes, String dataType, String indexName) {
		SpecData d = new SpecData();
		d.values = values;
		d.indexes = indexes;
		d.indexName = indexName;
		specData.put(dataType, d);
		
	}

	public void addOptimizedParameter(double d, String string) {
		optParam.put(string, d);
	}
	
	public void addComputedParameter(double d, String string) {
		compParam.put(string, d);
	}	

	public double getSumOfPowers() {
		return sum;
	}

	public void store(Execution ex, DataPoint glo, PscanPhyExperiment exp) {
		for (Entry<String, HashMap<String, Double>> groupEntry : groups.entrySet()) {
			String groupName = groupEntry.getKey();
			double totalDB = 0;
			double totalW = 0;
			if (exp.withLossDetail()) {
				for (Entry<String, Double> entry : groupEntry.getValue().entrySet()) {
					totalDB += entry.getValue();
					totalW += Math.pow(10,entry.getValue()/10);
					DataPoint derivedDataPoint = glo.getDerivedDataPoint();
					derivedDataPoint.addProperty("Loss type", groupName + " - " + entry.getKey());
					derivedDataPoint.addResultProperty("loss value dB", entry.getValue());
					derivedDataPoint.addResultProperty("loss value w", Math.pow(10,entry.getValue()/10));				
					ex.addDataPoint(derivedDataPoint);
				}
			}
			if (exp.withGroupLossDetail()) {
				DataPoint derivedDataPoint = glo.getDerivedDataPoint();
				derivedDataPoint.addProperty("Loss group type", groupName);
				derivedDataPoint.addResultProperty("Group loss value dB", totalDB);
				derivedDataPoint.addResultProperty("Group loss value w", totalW);
				ex.addDataPoint(derivedDataPoint);
			}
		}
		if (exp.withOptimizedAndComputerValues()) {
			for (Entry<String, Double> e : optParam.entrySet()) {
				glo.addResultProperty("optimized value for " + e.getKey(), e.getValue());
			}
			for (Entry<String, Double> e : compParam.entrySet()) {
				glo.addResultProperty("Computed value for " + e.getKey(), e.getValue());
			}	
		}
		if (exp.withSpecificData()) {
			for (Entry<String, SpecData> e : specData.entrySet()) {
				SpecData sp = e.getValue();
				for (int i = 0 ; i < sp.indexes.length ; i++) {
					DataPoint derivedDataPoint = glo.getDerivedDataPoint();
					derivedDataPoint.addProperty(sp.indexName, sp.indexes[i]);
					derivedDataPoint.addResultProperty("Internal serie for " + e.getKey(), sp.values[i]);
					ex.addDataPoint(derivedDataPoint);
				}
			}
		}
		
	}

	


}
