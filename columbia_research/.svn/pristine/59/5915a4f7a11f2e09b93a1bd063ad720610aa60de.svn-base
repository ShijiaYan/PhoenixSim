package edu.columbia.ke.circuit_oriented;

import java.util.ArrayList;
import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import edu.columbia.lrl.LWSim.AbstractTrafficGenerator;
import edu.columbia.lrl.LWSim.LWSimComponent;
import edu.columbia.lrl.LWSim.components.TransmissionLink;

public abstract class AbstractVOQFullyMeshedAssociativityBuilder extends
		AbstractVOQFullyMeshedBuilder {
	
	/*
	 * Associativity: 1 ==> Direct Mapped
	 * Associativity: N ==> Fully Associated
	 */
	
	private int nTotalVoq;
	private Dst2VoqMapper[] ts;
	protected int associativity;
	
	/*
	 * = circuits per node / associativity
	 * = # of cache lines per way
	 * = # of wavelength partitions 
	 * = # of wavelengths in an FSR
	 * = # of independent wave groups
	 */
	private int nVoqPerNode;
	
	public AbstractVOQFullyMeshedAssociativityBuilder(
			@ParamName(name="A VOQ template") AbstractCircuitLimitedVOQ voqTemplate,	
		//	@ParamName(name="VOQ type", default_="LRU_CL_VOQ") String voqType,
			@ParamName(name="Max number of circuits per node", default_="16") int circuitPerNode,	//cache size
			@ParamName(name="Associativity (circuits per VOQ)", default_="2") int circuitPerVOQ,	//Number of ways
			@ParamName(name="Circuit setup latency", default_="10000") double circuitSetupLatency, 
			@ParamName(name="Max vacant time", default_="-1") double maxVacantTime, 
			@ParamName(name="Include self as traffic dest?", default_="true") boolean self, 
			@ParamName(name="Add src-dest time line?", default_="false") boolean sdTimeline,
			@ParamName(name="Add src time line?", default_="true") boolean srcTimeline) {
		super(voqTemplate, circuitPerNode, circuitSetupLatency, maxVacantTime, self, sdTimeline, srcTimeline);
		nVoqPerNode = circuitPerNode / circuitPerVOQ;	
		associativity = circuitPerVOQ;
	}
	
	
	@Override
	protected void buildVoqArray() {
		nTotalVoq = nVoqPerNode * nClient;
		voq = new AbstractCircuitLimitedVOQ[nTotalVoq];	
	}

	@Override
	protected void buildVOQs(ArrayList<LWSimComponent> dests) {
		int nDestPerVoq = nClient / nVoqPerNode;
		int voqi;
		// populate the VOQs
		for (int node = 0; node < nClient; node++) {
			for (int v = 0; v < nVoqPerNode; v++) {
				voqi = node * nVoqPerNode + v;
				instantiateVoq(voqi, node, nDestPerVoq, associativity);
				dests.add(voq[voqi]);
			}
		}
		
		// build traffic splitter
		ts = new Dst2VoqMapper[nClient];
		for (int i = 0; i < nClient; i++){
			ts[i] = new Dst2VoqMapper();
			dests.add(ts[i]);
		}
	}


	@Override
	protected void connectVoqToLinks(TransmissionLink[][] links) {
		// i: source node
		// j: dest node
		for (int i = 0; i < this.nClient; i++){
			for (int j = 0; j < this.nClient; j++) {
				int voqInd = mapSrcDstToVoqInd(i, j);
				voq[voqInd].addDestination(j, dests);
				voq[voqInd].bufs.get(j).setTrafficDestination(links[i][j]);
			}
		}
	}
	
	/*@Override
	protected void connectGenToAllVoq(TraceTrafficGenerator tgen) {
		for (int i = 0; i < this.nClient; i++) {
			// connect tgen to traffic splitter first
			tgen.addDestination(ts[i], i);
			
			// connect splitter to voq
			for (int dstId = 0; dstId < nClient; dstId++) {
				int voqInd = mapSrcDstToVoqInd(i, dstId);
				ts[i].addDestination(voq[voqInd], dstId);
			}
		}
	}*/
	

	@Override
	protected void connectGen2Voq(AbstractTrafficGenerator gen, int src) {
		// splitter is per source node
		gen.setTrafficDestination(ts[src]);

		// connect splitter to voq
		for (int dstId = 0; dstId < nClient; dstId++) {
			int voqInd = mapSrcDstToVoqInd(src, dstId);
			ts[src].addDestination(voq[voqInd], dstId);
		}
	}


	protected int mapSrcDstToVoqInd(int src, int dst) {
		return src * nVoqPerNode + dst % nVoqPerNode;
	}


	@Override
	public Map<String, String> getAllParameters() {
		Map<String, String> m = super.getAllParameters();
		m.put("Associativity",associativity +"");
		return m;
	}
	
	
}
