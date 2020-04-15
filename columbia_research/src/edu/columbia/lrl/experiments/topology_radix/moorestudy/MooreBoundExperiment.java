package edu.columbia.lrl.experiments.topology_radix.moorestudy;

import ch.epfl.general_libraries.clazzes.ConstructorDef;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.math.MooreBound;
import ch.epfl.general_libraries.math.MoreMaths;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.general_libraries.results.Execution;
import ch.epfl.general_libraries.utils.MoreArrays;

public class MooreBoundExperiment implements Experiment {

	private int nodes;
	private int totNodes;
	private int radx;
	private LocalityModel model;
	private int mode;
	private boolean withLocal;
	private double trafPerNode;
	private int mult;
	private boolean roundLinks = false;
	private int nodesOrig = -1;

	public MooreBoundExperiment(
			@ParamName(name="nb nodes") int nodes, 
			@ParamName(name="radix") int radx, 
			LocalityModel mod, 
			@ParamName(name="With local") boolean withLocal,			
			@ParamName(name="Round links") boolean roundLinks,
			@ParamName(name="mode") int mode
			) {
		this.nodes = nodes;
		this.totNodes = nodes;
		this.radx = radx;
		this.model = mod;
		this.mode = mode;
		this.trafPerNode = 1d;
		this.mult = 1;
		this.roundLinks = roundLinks;
		this.withLocal = withLocal;
	}
	
	public MooreBoundExperiment(
			@ParamName(name="nb nodes") int nodes, 
			@ParamName(name="node mult", default_="1") int mult,
			@ParamName(name="radix") int radx, 
			@ParamName(name="traf (load)", default_="1") double traf, 
			LocalityModel mod,
			@ParamName(name="mode") int mode,
			@ParamName(name="Round links") boolean roundLinks,
			@ParamName(name="With local") boolean withLocal) {
		this.nodes = nodes;
		this.totNodes = nodes;
		this.radx = radx;
		this.model = mod;
		this.mode = mode;
		this.trafPerNode = traf;
		this.mult = mult;
		this.roundLinks = roundLinks;
		this.withLocal = withLocal;
	}
	
	public MooreBoundExperiment(
			@ParamName(name="TOTAL nb nodes") int totNodes, 
			@ParamName(name="node mult", default_="1") int mult, 
			@ParamName(name="radix") int radx, 
			LocalityModel mod, 
			@ParamName(name="mode") int mode,
			@ParamName(name="Round links") boolean roundLinks,
			@ParamName(name="With local") boolean withLocal) {
		this.nodes = MoreMaths.ceilDiv(totNodes, mult);
		this.totNodes = nodes*mult;
		this.radx = radx;
		this.model = mod;
		this.mode = mode;
		this.trafPerNode = mult;
		this.mult = mult;
		this.roundLinks = roundLinks;
		this.withLocal = withLocal;
	}
	
	@ConstructorDef(def="Optimize radix, give total number of nodes")
	public MooreBoundExperiment(
			@ParamName(name="Total number of nodes") int totNodes, 
			@ParamName(name="node mult", default_="1") int mult, 
			LocalityModel mod, 
			@ParamName(name="mode") int mode,
			@ParamName(name="Round links") boolean roundLinks,
			@ParamName(name="With local") boolean withLocal) {
		this.nodesOrig = totNodes;
		this.nodes = MoreMaths.ceilDiv(totNodes, mult);
		this.totNodes = nodes*mult;		
		this.radx = -1;
		this.model = mod;
		this.mode = mode;
		this.trafPerNode = mult;
		this.mult = mult;
		this.roundLinks = roundLinks;
		this.withLocal = withLocal;
	}	
	
	@ConstructorDef(def="Optimize radix, give number of switches")
	public MooreBoundExperiment(
			LocalityModel mod, 			
			@ParamName(name="Switches") int nodes, 
			@ParamName(name="node mult", default_="1") int mult,
			@ParamName(name="traf (load)", default_="1") double traf, 
			@ParamName(name="mode") int mode,
			@ParamName(name="Round links") boolean roundLinks,
			@ParamName(name="With local") boolean withLocal) {
		this.nodes = nodes;
		this.totNodes = nodes*mult;
		this.radx = -1;
		this.model = mod;
		this.mode = mode;
		this.trafPerNode = traf;
		this.mult = mult;
		this.roundLinks = roundLinks;
		this.withLocal = withLocal;
	}	

	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {
		
		if (radx == -1) {
			LocStruct ls = optimizeRadix();
			store(man, ls);
		} else {
			LocStruct ls = calculate();
			store(man, ls);
		}
	}
	
	public LocStruct calculate() {
		int[] layout = MooreBound.getNodesInLayers(radx, nodes);
		int diam = layout.length-1;	
		double[] trafs = getTraffics(layout);
		double trafficForOneNode = trafs[0];
		double injectedForOneNode = trafs[1];
		double totalLinks = getLinks(trafficForOneNode, radx);
		
		LocStruct ls = new LocStruct(trafficForOneNode, injectedForOneNode, totalLinks, diam, radx, (double)totalLinks / (nodes*radx));
		return ls;
	}
	
	public LocStruct calculateWithSpecificLinks(int connections) {
		int[] layout = MooreBound.getNodesInLayers(radx, nodes);
		int diam = layout.length-1;		
		double[] trafs = getTraffics(layout);
		double trafficForOneNode = trafs[0];
		double injectedForOneNode = trafs[1];
		double totalLinks = getLinks(trafficForOneNode, radx);
	//	int linkMult = (int)Math.ceil(trafficForOneNode*nodes/(connections*2));
	//	int totalLinks = linkMult*connections*2;
		
		LocStruct ls = new LocStruct(trafficForOneNode, injectedForOneNode, totalLinks, diam, radx, /*linkMult*/(double)totalLinks / (nodes*radx));
		return ls;
	}	
	
	private void store(AbstractResultsManager man, LocStruct ls) {
		DataPoint dp = new DataPoint();
		dp.addProperty("nodes", nodes);
		dp.addProperty("tot nodes", totNodes);
		dp.addProperty("radix", ls.radix);
		dp.addProperty("locality", model.getLocality()+"");
		dp.addProperty("mode", mode+"");
		dp.addProperty("mult", mult+"");
		dp.addProperty("round links", roundLinks+"");
		dp.addProperty("with local", withLocal+"");
		if (nodesOrig >= 0) {
			dp.addProperty("nodes orig", nodesOrig);
		}
		
		double detOverhead = nodes * (ls.trafficForOneNode -1);
		double conOverhead = ls.totalLinks - (nodes*ls.trafficForOneNode);
		dp.addResultProperty("detour overhead", detOverhead);
		dp.addResultProperty("con overhead", conOverhead);
		dp.addResultProperty("total overhead", detOverhead + conOverhead);
		dp.addResultProperty("total links", ls.totalLinks);
		dp.addResultProperty("links con", nodes*ls.radix);
		dp.addResultProperty("links con total", nodes*ls.radix*ls.linkMult);		
		dp.addResultProperty("links traf", nodes*ls.trafficForOneNode);
		dp.addResultProperty("average traffic path length (if traf=1), per src impact", ls.trafficForOneNode);	
		dp.addResultProperty("per src injected traffic", ls.injectedForOneNode);
		dp.addResultProperty("total injected traffic", ls.injectedForOneNode*nodes);
		dp.addResultProperty("average impact per src-dest", ls.trafficForOneNode/(double)(totNodes-1));
		int linkMult = (int)(ls.totalLinks / (nodes*ls.radix));
		dp.addResultProperty("link mult", linkMult);
		dp.addResultProperty("total radix", ls.radix*linkMult + mult);
		dp.addResultProperty("total radix round", MoreMaths.ceilDiv(ls.radix*linkMult + mult, 8)*8);		
		dp.addResultProperty("norm det overhead", detOverhead/ls.totalLinks);
		dp.addResultProperty("norm con overhed", conOverhead/ls.totalLinks);
		dp.addResultProperty("norm tot overhead", (detOverhead + conOverhead)/ls.totalLinks);
	//	dp.addResultProperty("total traffic", (detOverhead + conOverhead)/ls.totalLinks);
		

		dp.addProperty("diameter", ls.diameter);

		Execution e = new Execution();
		e.addDataPoint(dp);
		man.addExecution(e);		
	}
	
	private double getLinks(double trafficForOneNode, int radix) {
		double totalLinks;
		if (roundLinks) {
			int linkMult = (int)Math.ceil(trafficForOneNode/radix);
			totalLinks = (double)linkMult*(double)nodes*(double)radix;
		} else {
			totalLinks = Math.max(nodes*radix, nodes*trafficForOneNode);
		}
		return totalLinks;
	}

	private double[] getTraffics(int[] layout) {
		double trafficForOneNode = 0;
		double injectedForOneNode = 0;
		if (mode == 0) {
			double[] distanceCoeff = model.calculate(!withLocal, layout.length);
			for (int i = 0 ; i < layout.length ; i++) {
				trafficForOneNode += distanceCoeff[i]*i;
			}
		} else if (mode == -1) {
			double[] distanceCoeff = model.calculate(!withLocal, layout.length);
			for (int i = 0 ; i < distanceCoeff.length ; i++) {
				distanceCoeff[i] *= layout[i];
			}
			distanceCoeff = MoreArrays.normalize(distanceCoeff);
			for (int i = 1 ; i < distanceCoeff.length ; i++) {
				trafficForOneNode += distanceCoeff[i]*i;
				injectedForOneNode += distanceCoeff[i];
			}			
			
		} else {
			double[] distanceCoeff = model.calculate(mode);
			int distanceBin = 1;
			int[] repart = MoreMaths.divide(nodes-1, mode);
			for (int i = 0 ; i < mode ; i++) {
				int currentNode = repart[i];
				double intensityForBin = distanceCoeff[i]/(double)repart[i];
				while (currentNode > 0) {
					int n = Math.min(currentNode, layout[distanceBin]);
					trafficForOneNode += intensityForBin*n*distanceBin;
					currentNode -= n;
					layout[distanceBin] -= n;
					if (layout[distanceBin] == 0) {
						distanceBin++;
					}
				}
			}			
		}
		trafficForOneNode *= trafPerNode*mult;
		injectedForOneNode *= trafPerNode*mult;
		return new double[]{trafficForOneNode, injectedForOneNode};
	}

	public LocStruct optimizeRadix() {
		
		int idealRadix = -1;
		int diamPrev = 0;
		int totalLinksPrev = Integer.MAX_VALUE;
		double trafficForOneNodePrev = 0;
		double injectedForOneNodePrev = 0;
		if (nodes == 2) {
			double[] trafs = getTraffics(new int[]{1,1});
			double trafficForOneNode = trafs[0];
			double injectedForOneNode = trafs[1];
			return new LocStruct(trafficForOneNode, injectedForOneNode, Math.ceil(trafPerNode)*2, 1, 1, Math.ceil(trafPerNode));
		}		
		for (int rad = 2 ; rad <= Math.min(nodes,300) ; rad++) {
			int[] layout = MooreBound.getNodesInLayers(rad, nodes);
			int diam = layout.length-1;	
			double[] trafs = getTraffics(layout);
			double trafficForOneNode = trafs[0];
			double injectedForOneNode = trafs[1];
			double totalLinks = getLinks(trafficForOneNode, rad);
			if (totalLinks < totalLinksPrev) {
				totalLinksPrev = (int)totalLinks;
				trafficForOneNodePrev = trafficForOneNode;
				injectedForOneNodePrev = injectedForOneNode;
				diamPrev = diam;
				idealRadix = rad;
			}
		}
		return new LocStruct(trafficForOneNodePrev, injectedForOneNodePrev, totalLinksPrev, diamPrev, idealRadix, totalLinksPrev/(nodes*idealRadix));
	}

	public static class LocStruct {
		public double trafficForOneNode;
		public double injectedForOneNode;
		public double totalLinks;
		public int diameter;
		public int radix;
		public double linkMult;
		
		private LocStruct(double ta, double injectedForOneNode, double to, int di, int id, double linkMult) {
			this.trafficForOneNode = ta;
			this.injectedForOneNode = injectedForOneNode;
			this.totalLinks = to;
			this.diameter = di;
			this.radix = id;
			this.linkMult = linkMult;
		}
	}

}
