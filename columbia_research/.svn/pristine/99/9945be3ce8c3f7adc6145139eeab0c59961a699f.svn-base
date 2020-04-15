package edu.columbia.lrl.experiments.topology_radix.topogen;

import java.util.ArrayList;
import java.util.Map;

import edu.columbia.lrl.experiments.topology_radix.HPCtopologyAnalysis.HPCDesignPoint;
import edu.columbia.lrl.experiments.topology_radix.locality.AbstractNormalisedTrafficVectorGenerator;

public abstract class AnalysableTopologyGenerator {
	
	public abstract void setTotalDesiredClientsAndRadix(int desiredClients, int radix);
	
	public abstract Map<String, String> getAllParameters();	
	
	public abstract ArrayList<HPCDesignPoint> getDesignPoints(AbstractNormalisedTrafficVectorGenerator localityVec);

}
