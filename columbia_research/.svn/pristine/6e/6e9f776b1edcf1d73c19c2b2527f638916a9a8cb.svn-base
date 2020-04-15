package edu.columbia.lrl.experiments.topo_scalability;

import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.general_libraries.results.Execution;
import ch.epfl.javancox.experiments.builder.ExperimentConfigurationCockpit;

public abstract class Scalability {
	
	public static void main(String[] args) throws Exception {
		new ExperimentConfigurationCockpit(edu.columbia.lrl.experiments.topo_scalability.Scalability.ScalabilityExperiment.class).show();
	}	
	
	public static class ScalabilityExperiment implements  Experiment {

		protected int numberOfNodes;
		protected int numberOfWavelengths;
		protected double targetLoad;
		
		Scalability sca;
		
		public ScalabilityExperiment(Scalability sca, int numberOfNodes, int numberOfWavelengths, double targetLoad) {
			this.sca = sca;
			this.numberOfNodes = numberOfNodes;
			this.numberOfWavelengths = numberOfWavelengths;
			this.targetLoad = targetLoad;			
		}
		
		public void run(AbstractResultsManager man, AbstractResultsDisplayer dis) {
			DataPoint dp = new DataPoint();
			dp.addProperty("Number of clients", numberOfNodes);
			dp.addProperty("Number of wavelengths", numberOfWavelengths);
			dp.addProperty("Target load", targetLoad);
			dp.addProperty("Type", sca.getClass().getSimpleName());		
			Execution e = new Execution();
			int minStar = Integer.MAX_VALUE;
			int minStarCN = Integer.MAX_VALUE;
			int minStarIndex = -1;
			
			int minCN = Integer.MAX_VALUE;
			int minCNStar = Integer.MAX_VALUE;
			int minCNIndex = -1;
			
		/*	int maxClients = -Integer.MAX_VALUE;
			int maxClientsStars = 0;*/
			
			
			for (int i = 1 ; i < Math.min(numberOfWavelengths - 1, numberOfNodes) ; i++) {
				sca.init(numberOfNodes, numberOfWavelengths, targetLoad, i);
				DataPoint dp2 = dp.getDerivedDataPoint();
				dp2.addProperty("Clients per L1 star", i);			
				if (!sca.compute(dp2, e)) continue;
				if (sca.getTotalNumberOfStars() < minStar) {
					minStar = sca.getTotalNumberOfStars();
					minStarCN = sca.getTotalNumberOfConnectingNodes();
					minStarIndex = i;
				}
				if (sca.getTotalNumberOfConnectingNodes() < minCN) {
					minCN = sca.getTotalNumberOfConnectingNodes();
					minCNStar = sca.getTotalNumberOfStars();
					minCNIndex = i;
				}

				
			}
			if (minStarIndex >= 0) {
				DataPoint dpx = dp.getDerivedDataPoint();
				dpx.addResultProperty("Min star number", minStar);
				dpx.addResultProperty("Min star CN number", minStarCN);
				dpx.addResultProperty("Min star client per star", minStarIndex);
				e.addDataPoint(dpx);
			}
			if (minCNIndex >= 0) {
				DataPoint dpx = dp.getDerivedDataPoint();
				dpx.addResultProperty("Min CN number", minCN);
				dpx.addResultProperty("Min CN star number", minCNStar);
				dpx.addResultProperty("Min CN client per star", minCNIndex);
				e.addDataPoint(dpx);				
			}
			
			man.addExecution(e);
		}		
		
	}
	
	// given	
	protected int numberOfNodes;
	protected int numberOfWavelengths;
	protected int clientsPerStar;
	protected double targetLoad;
	
	// computed
	protected double srcDestLoad;
	
	public abstract boolean compute(DataPoint dp, Execution ex);
	public abstract int getTotalNumberOfStars();
	public abstract int getTotalNumberOfConnectingNodes();
	
	public Scalability() {}
	
	public void init(int numberOfNodes, int numberOfWavelengths, double targetLoad, int clientsPerStar) {
		this.numberOfNodes = numberOfNodes;
		this.numberOfWavelengths = numberOfWavelengths;
		this.targetLoad = targetLoad;
		this.clientsPerStar = clientsPerStar;		
		srcDestLoad = targetLoad/(double)numberOfNodes;
	}
	
}
