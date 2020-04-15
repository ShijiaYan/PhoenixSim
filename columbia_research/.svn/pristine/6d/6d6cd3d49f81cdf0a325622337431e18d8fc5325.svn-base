package edu.columbia.sebastien.data_center_consumption;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.general_libraries.results.Execution;
import ch.epfl.general_libraries.utils.MoreArrays;

public class DataCenterFConsumptionExperiment implements Experiment {
	
	private double growth;
	private OperationPerSecondModel opsModel;
	private double gopPerJoule;
	private double globalEffGainPerYear;
	private DCcomsumptionPart[] consumptionParts;
	private int startYear;
	private int finalYear;
	
	public DataCenterFConsumptionExperiment(
			@ParamName(name="Data-center yearly growth rate in percent", default_="7") double growth,
			@ParamName(name="Baseline operation per second model") OperationPerSecondModel opsModel,
			@ParamName(name="Baseline GigaGLOP/J", default_="0.1") double gopPerJoule,
			@ParamName(name="Global energy efficiency gains", default_="1.25") double globalEffGainPerYear,
			@ParamName(name="Consumption types") DCcomsumptionPart[] consumptionPart,
			@ParamName(name="Start year") int startYear,
			@ParamName(name="Final year") int finalYear) {			
			
		this.growth = growth;
		this.opsModel = opsModel;
		this.gopPerJoule = gopPerJoule;
		this.globalEffGainPerYear = globalEffGainPerYear;
		this.consumptionParts = consumptionPart;
		this.startYear = startYear;
		this.finalYear = finalYear;
		
	}

	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {
		
		Execution e = new Execution();
		
		DataPoint dp = new DataPoint();
		dp.addProperty("Global growth", growth);
		dp.addProperties(opsModel.getProperties());
		dp.addProperty("Baseline GigaGOP/J", gopPerJoule);
		dp.addProperty("Global eff gain per year", globalEffGainPerYear);
		
		normalizeParts();
		
		double totalConsumptionTWStartYear = opsModel.getConsumptionTW(gopPerJoule);
		
		double[] currentConsumptionTW = new double[consumptionParts.length];
		
		for (int i = 0 ; i < consumptionParts.length ; i++) {
			currentConsumptionTW[i] = totalConsumptionTWStartYear*consumptionParts[i].getInitialpart();
			
			dp.addProperties(consumptionParts[i].getProperties());
		}
		
		
		for (int year = 0 ; year <= finalYear-startYear ; year++) {
			DataPoint dpYear = dp.getDerivedDataPoint();
			dpYear.addProperty("Year after " + startYear, year);
			
			DataPoint dpYearGlo = dpYear.getDerivedDataPoint();
			dpYearGlo.addProperty("Subpart name", "total");
			dpYearGlo.addResultProperty("Power per part in TW", MoreArrays.sum(currentConsumptionTW));
			dpYearGlo.addResultProperty("Total elec use (billions of kWh)", teraWattToBillionsOfkWhPerYear(MoreArrays.sum(currentConsumptionTW)));			
			dpYearGlo.addResultProperty("Total elec use (Quads)", teraWattToQuadPerYear(MoreArrays.sum(currentConsumptionTW)));			

			e.addDataPoint(dpYearGlo);
			
			for (int i = 0 ; i < consumptionParts.length ; i++) {
				DataPoint dpYearSpec = dpYear.getDerivedDataPoint();
				dpYearSpec.addProperty("Subpart name", consumptionParts[i].getName());
				dpYearSpec.addResultProperty("Power per part in TW", currentConsumptionTW[i]);
				
				
				e.addDataPoint(dpYearSpec);
			}
			
			for (int i = 0 ; i < consumptionParts.length ; i++) {
				currentConsumptionTW[i] *= (growth/100d)+1;
				currentConsumptionTW[i] /= globalEffGainPerYear;
				currentConsumptionTW[i] /= consumptionParts[i].getSpecEffGain();
				currentConsumptionTW[i] *= consumptionParts[i].getSpecGrowth();
			}
		}
		
		man.addExecution(e);
	}

	private void normalizeParts() {
		double sum = 0;
		for (int i = 0 ; i < consumptionParts.length ; i++) {
			sum += consumptionParts[i].getUserInitialpart();
		}
		for (int i = 0 ; i < consumptionParts.length ; i++) {
			consumptionParts[i].setInitialPart(consumptionParts[i].getUserInitialpart()/sum);
		}		
	}
	
	private double teraWattToBillionsOfkWhPerYear(double tw) {
		return tw*1e9*365*34/1e9d;
	}
	
	private double teraWattToQuadPerYear(double tw) {
		return tw*365*24/293.08/0.34;
		
	}

}
