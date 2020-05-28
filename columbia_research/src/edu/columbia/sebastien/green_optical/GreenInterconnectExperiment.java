package edu.columbia.sebastien.green_optical;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.general_libraries.results.Execution;
import edu.columbia.lrl.experiments.topology_radix.abstractmodel.AbstractModel;
import edu.columbia.sebastien.green_optical.EPS.AbstractSwitchCoreModel;
import edu.columbia.sebastien.green_optical.elecSERDES.AbstractSERDESModel;
import edu.columbia.sebastien.green_optical.simple_trans.AbstractOpticalModel;


public class GreenInterconnectExperiment implements Experiment {
	
	private DefaultMachineModel machineModel;
	private AbstractSERDESModel serdesMod;
	private AbstractOpticalModel opticalModel;
	private AbstractSwitchCoreModel coreModel;
	private int concentration;
	private double nrRRratio;
	private double opticalRRlinksRatio;
	
	public GreenInterconnectExperiment(DefaultMachineModel machineModel,		
			AbstractSERDESModel serdesMod,
			AbstractOpticalModel opticalModel,
			AbstractSwitchCoreModel coreModel,
			@ParamName(name="Concentration", default_="10") int concentration,
			@ParamName(name="NR/RR ratio", default_="0.1:10log20") double nrRRratio,
			@ParamName(name="Portion of RR links optical") double opticalRRlinksRatio) {
		
		this.machineModel = machineModel;
		this.serdesMod = serdesMod;
		this.opticalModel = opticalModel;
		this.coreModel = coreModel;
		this.concentration = concentration;
		this.nrRRratio = nrRRratio;
		this.opticalRRlinksRatio = opticalRRlinksRatio;
	}

	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {

		
		int nodes = machineModel.getNumberOfNodes();
		double injectionBW = machineModel.getNodeBandwidthInGbs();
		
		AbstractModel abm = new AbstractModel(nodes, concentration, 1, nrRRratio);
		
		if (abm.runFindC()) {
		
			int nbSwitches = abm.getNumberOfSwitches();
			// we could have radix either rounded or not here...
			int radix = abm.getUnroundedRadix();
			
			
			double RRrateInGbs = injectionBW / nrRRratio;
			
			double totalRRRate = (radix - concentration) * RRrateInGbs;
			double totalNRRate = concentration * injectionBW;
			
			double averageRate = (totalRRRate + totalNRRate)/ radix;
			
			DataPoint dp = new DataPoint();
			Execution e = new Execution();
			
			dp.addProperties(opticalModel.getAllParameters());
			dp.addProperties(coreModel.getAllProperties());
			dp.addProperties(serdesMod.getAllParameters());
			dp.addProperties(machineModel.getAllProperties());
			dp.addProperty("Concentration", concentration);
			dp.addProperty("nrRR ratio", nrRRratio);
			dp.addProperty("portion of optical links", opticalRRlinksRatio);
			dp.addProperty("radix", radix);
			dp.addProperty("number of switches", nbSwitches);
			dp.addProperty("number of nodes", nodes);
			dp.addProperty("average distance", abm.getAverageDistance());
			
			double[] coreConsumptionEpb = coreModel.getPjPerSwitchedBit(injectionBW, RRrateInGbs, 
					concentration, radix - concentration, man, dp);
			if (coreConsumptionEpb[0] < 0) {
				throw new WrongExperimentException();
			}
			double ratePerPinQuadRR = RRrateInGbs/coreConsumptionEpb[2];
			double ratePerPinQuadNR = injectionBW/coreConsumptionEpb[1];
			
			double coreConsumption = nbSwitches * coreConsumptionEpb[0] * radix * averageRate;
					
			
			double nrLinksConsumption = nodes * serdesMod.getPjPerBit(ratePerPinQuadNR) * injectionBW; // one serdes is included in the switch
			
			
		//	double RRLinksConsumptionBaseConsumption = abm.getOptCapacity() * 
		//			                          serdesMod.getPjPerBit(injectionBW /nrRRratio);
			
			double numberOfOpticalRRLinks = abm.getOptCapacity()*opticalRRlinksRatio;
			
			double opticalRRLinksPowerConsumption = (opticalModel.getPjPerBit(ratePerPinQuadRR) + 
													 serdesMod.getPjPerBit(ratePerPinQuadRR)) *
													 RRrateInGbs;
			
			
			double RRLinksOpticalConsumption = numberOfOpticalRRLinks * opticalRRLinksPowerConsumption;
			

			
			
			double totalConsumption = coreConsumption + nrLinksConsumption + RRLinksOpticalConsumption;
			
			

			
			String[] consumptionNames = {"Switch core", "NR links", "optical RR links","total"};
			double[] powers = new double[]{coreConsumption/1e6d, nrLinksConsumption/1e6d, RRLinksOpticalConsumption/1e6d, totalConsumption/1e6d};
			
			DataPoint copy = dp.getDerivedDataPoint();
			
			copy.addResultProperty("Total power kW", totalConsumption/1e6d);
			
			copy.addResultProperty("Energy efficiency (pJ/bit)", totalConsumption/machineModel.getTotalBandwidthInGbs());
			copy.addResultProperty("% of budget with 50GF/J", 100d*(totalConsumption/1e3d)/(machineModel.getGFlops()/50d));
			copy.addResultProperty("epb switching", coreConsumptionEpb[0]);
			
			copy.addResultProperty("Power of one NR link", serdesMod.getPjPerBit(ratePerPinQuadNR) * injectionBW);
		//	copy.addResultProperty("radix", radix);
			
			e.addArrayResult("power consumptions KW", powers, consumptionNames, dp);
			
			e.addDataPoint(copy);
			
			man.addExecution(e);
		}
			
		
	}
	


}
