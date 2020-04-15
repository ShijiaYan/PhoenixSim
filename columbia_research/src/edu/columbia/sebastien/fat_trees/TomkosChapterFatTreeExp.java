package edu.columbia.sebastien.fat_trees;

import java.util.Map;

import cern.colt.Arrays;
import ch.epfl.general_libraries.clazzes.ConstructorDef;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.math.MoreMaths;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.general_libraries.utils.SimpleMap;
import ch.epfl.general_libraries.utils.TypeParser;

public class TomkosChapterFatTreeExp implements Experiment {
	
	private int radix;
	private int levels;
	private double[] overProFac;
	
	public TomkosChapterFatTreeExp(int radix,
			int levels,
			String overprovisioningfactors) {
		this.radix = radix;
		this.levels = levels;
		this.overProFac = TypeParser.parseDouble(overprovisioningfactors);
	}
	
	@ConstructorDef(ignore=true)
	public TomkosChapterFatTreeExp(int radix,
			int levels,
			double[] overprovisioningfactors) {
		this.radix = radix;
		this.levels = levels;
		this.overProFac = overprovisioningfactors;

	}

	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {
		
		DataPoint dp = new DataPoint();
		dp.addProperty("Levels", levels);
		dp.addProperty("Radix", radix);
		for (int i = 0 ; i < levels-1 ; i++) {
			dp.addProperty("Over at level " + i , overProFac[i]);
		}
		dp.addProperty("Over vector", Arrays.toString(overProFac));
		
		distanceDistribution(dp, man);
		
		dp.addResultProperties(calculate());
		
		man.addDataPoint(dp);
	}
	
	public void distanceDistribution(DataPoint dp, AbstractResultsManager man) {
		int[] EPAtDistance = new int[levels+1];
		int scalability = (int)(getScaleCoeff()*Math.pow(radix, levels)/Math.pow(2, levels-1));
		EPAtDistance[0] = 1;
		double factor = 1;
		for (int i = 1; i < levels; i++) {
			factor *= radix*overProFac[i-1]/(overProFac[i-1]+1);
		//	int nbOfClusters = (int)(scalability/factor);
			
			EPAtDistance[i] = (int)(factor) - MoreMaths.sum(EPAtDistance, 0, i);
		}
		EPAtDistance[levels] = scalability - MoreMaths.sum(EPAtDistance, 0, levels);
		
		double accum = 0;
		
		for (int i = 0 ; i < levels+1 ; i++) {
			DataPoint dp2 = dp.getDerivedDataPoint();
			dp2.addProperty("Distance", i*2);
			dp2.addResultProperty("Number of other EP at distance", EPAtDistance[i]);
			dp2.addResultProperty("proportion of other EP at distance", (double)EPAtDistance[i]/(double)(scalability));
			man.addDataPoint(dp2);
			
			if (i > 0)
			accum += EPAtDistance[i]*(i*2);
		}
		
		dp.addResultProperty("Average distance", accum/(double)(scalability*-1));
	}
	
	private double getScaleCoeff() {
		double scaleCoeff = 1;
		for (int i = 0 ; i < levels-1 ; i++) {
			scaleCoeff *= 2*overProFac[i]/(overProFac[i]+1);
		}	
		return scaleCoeff;
	}
	
	public int getEndpoints() {
		double r2km1 = Math.pow(radix/2d, levels-1);
		double endpointsFullPro = radix*r2km1;
		double endpointsOver = endpointsFullPro*getScaleCoeff();
		return (int)Math.round(endpointsOver);
	}
	
	public double getEPPerRouteratLevel(int level) {
		if (level == 0) {
			return  (radix/(1 + (1/(overProFac[0]))));
		} else if (level == this.levels-1) {
			return  (radix/(1/getCumulatedOver(level)));
		} else {
			double first = 1/getCumulatedOver(level);
			double second = 1/getCumulatedOver(level+1);
			return (radix/(first + second));
		}
	}
	
	public double getCumulatedOver(int levels) {
		return MoreMaths.product(overProFac, 0, levels);
	}
	
	public Map<String, String> calculate() {
		int r2km1 = (int)Math.pow(radix/2, levels-1);
		
		
		int routersFullPro = (2*levels - 1)*r2km1;
		int internalLinksFullPro = r2km1 * radix * (levels -1);
		int endpointsFullPro = radix*r2km1;
		
		double routerPerEndpointFullPro = (double)routersFullPro/(double)endpointsFullPro;
		double internalLinksPerEndpointFullPro = (double)internalLinksFullPro/(double)endpointsFullPro;
		double routerPerEndpointTheo = (double)(2*levels -1)/(double)radix;
		if (Math.abs(routerPerEndpointFullPro - routerPerEndpointTheo) > 0.0001) {
			throw new IllegalStateException();
		}
		
		

		
		
		double seriesRouterPerEndpoint = 1;
		for (int i = 1 ; i < levels ; i++) {
			seriesRouterPerEndpoint += 2/MoreMaths.product(overProFac, 0, i);
		}
		
		double routerPerEndpointOver = routerPerEndpointFullPro/((2*levels - 1)/seriesRouterPerEndpoint);
		
		double seriesLinkPerEndpoint = 0;
		for (int i = 1 ; i < levels ; i++) {
			seriesLinkPerEndpoint += 1/MoreMaths.product(overProFac, 0, i);
		}	
		double internalLinksPerEndpointOver = internalLinksPerEndpointFullPro/((levels-1)/(seriesLinkPerEndpoint));

		SimpleMap<String, String> results = new SimpleMap<String, String>();
		
		int endpointsOver = getEndpoints();
		
		results.put("Scalability (full prov)", endpointsFullPro+"");
		results.put("Routers (full prov)", routersFullPro+"");
		results.put("Internal links (full prov)", internalLinksFullPro+"");
		results.put("Routers per end-point (full prov)", routerPerEndpointFullPro+"");
		results.put("Internal links per end-point (full prov)", internalLinksPerEndpointFullPro +"");
		
		results.put("Scalability (over)", endpointsOver+"");
		results.put("Scalability gains", (double)endpointsOver/(double)endpointsFullPro +"");
		
		results.put("Routers (over)", routerPerEndpointOver*endpointsOver + "");
		results.put("Routers per end-point (over)", routerPerEndpointOver +"");
		
		results.put("Internal links (over)", internalLinksPerEndpointOver*endpointsOver +"");
		results.put("Internal links per end-point (over)", internalLinksPerEndpointOver +"");
		
		results.put("Routers gains", routerPerEndpointFullPro/routerPerEndpointOver + "");
		results.put("Link gains", internalLinksPerEndpointFullPro/internalLinksPerEndpointOver+"");
		
		return results;

	}
	

}
