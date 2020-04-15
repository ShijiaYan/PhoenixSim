package edu.columbia.ke.circuit_oriented.in_limited_only;

import ch.epfl.general_libraries.clazzes.ParamName;
import edu.columbia.ke.circuit_oriented.AbstractVOQFullyMeshedBuilder;

public class InCiruitLimitedOnlyNetworkBuilder extends
		AbstractVOQFullyMeshedBuilder {
	
	public InCiruitLimitedOnlyNetworkBuilder(
			@ParamName(name="A VOQ template") InCircuitLimitedVOQ voqTemplate,
			@ParamName(name="Max number of circuits per node", default_="8")int circuitPerNode,
			@ParamName(name="Circuit setup latency", default_="10000") double circuitSetupLatency, 
			@ParamName(name="Max vacant time", default_="-1") double maxVacantTime, 
			@ParamName(name="Include self as traffic dest?", default_="true") boolean self, 
			@ParamName(name="Add src-dest time line?", default_="false") boolean sdTimeline,
			@ParamName(name="Add src time line?", default_="true") boolean srcTimeline) {
		super(voqTemplate, circuitPerNode, circuitSetupLatency, maxVacantTime, self, sdTimeline, srcTimeline);
	}

}
