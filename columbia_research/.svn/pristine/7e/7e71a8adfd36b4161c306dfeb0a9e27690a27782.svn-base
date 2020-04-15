package edu.columbia.lrl.experiments.topology_radix;

import java.util.List;
import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.general_libraries.results.Execution;
import ch.epfl.general_libraries.utils.ParetoSet;
import ch.epfl.general_libraries.utils.SimpleMap;

import edu.columbia.lrl.experiments.topology_radix.locality.AbstractNormalisedTrafficVectorGenerator;
import edu.columbia.lrl.experiments.topology_radix.topogen.AnalysableTopologyGenerator;

public class FixedClientHPCtopologyAnalysis extends HPCtopologyAnalysis {

	private int desiredClients;
	
	Map<String, String> additionalParameters = new SimpleMap<String, String>();
	
	public FixedClientHPCtopologyAnalysis(AnalysableTopologyGenerator atopogen, 
			  AbstractNormalisedTrafficVectorGenerator localityVec, 
			  @ParamName(name="Radix") int radix,
			  @ParamName(name="relative load") double relativeLoad,
			  @ParamName(name="Desired clients") int clients,
			  HPCSolutionFilter filter) {
		super(atopogen, localityVec, radix, filter);
		
		this.desiredClients = clients;
		
		this.atopogen = atopogen;
		this.localityVec = localityVec;
		localityVec.setMaxLoad(relativeLoad);
		this.radix = radix;
		this.filter = filter;	
		
		additionalParameters.put("Relative load", relativeLoad+"");		
	}
	
	public FixedClientHPCtopologyAnalysis(AnalysableTopologyGenerator atopogen, 
										  AbstractNormalisedTrafficVectorGenerator localityVec, 
										  @ParamName(name="Radix") int radix,
										  @ParamName(name="Teraflop per second per node") double tflopPerSecondPerNode, 
										  @ParamName(name="Byte per flop") double bytesPerFlop, 
										  @ParamName(name="link rate tbit/s") double linkRateInTbitPerS,
										  @ParamName(name="Total petaflop target") double totalPetaFlopTarget,
										  HPCSolutionFilter filter) {
		super(atopogen, localityVec, radix, filter);
		
		desiredClients = (int)(totalPetaFlopTarget * 1000 /tflopPerSecondPerNode);
		
		double averageInjectionTbits = tflopPerSecondPerNode * bytesPerFlop * 8;
		double relativeLoad = Math.min(averageInjectionTbits/linkRateInTbitPerS, 1);
		
		this.atopogen = atopogen;
		this.localityVec = localityVec;
		localityVec.setMaxLoad(relativeLoad);
		this.radix = radix;
		this.filter = filter;
		
		additionalParameters.put("Teraflop/s per node", tflopPerSecondPerNode+"");
		additionalParameters.put("Byte per flop", bytesPerFlop+"");
		additionalParameters.put("Link rate in tbit/s", linkRateInTbitPerS+"");
		additionalParameters.put("Total petaflop/s target", totalPetaFlopTarget + "");
		additionalParameters.put("Per node injection rate tbit/s", averageInjectionTbits+"");
		additionalParameters.put("Relative load", relativeLoad+"");
		
		
		
	}
	
	public FixedClientHPCtopologyAnalysis(int desiredClients, 
										  AnalysableTopologyGenerator atopogen, 
										  AbstractNormalisedTrafficVectorGenerator localityVec, 
										  int radix,
										  HPCSolutionFilter filter) {
		super(atopogen, localityVec, radix, filter);
		this.atopogen = atopogen;

		this.localityVec = localityVec;
		this.radix = radix;
		this.filter = filter;
		
		this.desiredClients = desiredClients;
	}	
	
	
	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis) {
		Execution e = new Execution();
		DataPoint dp = new DataPoint();
		dp.addProperty("Topology type", atopogen.getClass().getSimpleName());
		dp.addProperty("Traffic locality", localityVec.getClass().getSimpleName());
		dp.addProperty("Desired clients", this.desiredClients);
		dp.addProperties(additionalParameters);
		dp.addProperties(localityVec.getAllParameters());
		
		ParetoSet<HPCDesignPoint> paretoSet = new ParetoSet<HPCDesignPoint>(4);
		atopogen.setTotalDesiredClientsAndRadix(desiredClients, radix);
		
		List<HPCDesignPoint> list = atopogen.getDesignPoints(localityVec);
		
		for (HPCDesignPoint point : list) {
			if (filter.filter(point)) {
				paretoSet.addCandidate(point);					
			} 
		}
		
		for (HPCDesignPoint designPoint : paretoSet) {
			e.addDataPoint(designPoint.addProperties(dp.getDerivedDataPoint()));		
		}		
		man.addExecution(e);
	}	
	
	
}
