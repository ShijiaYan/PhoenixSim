package edu.columbia.lrl.experiments.spinet.mains;

import ch.epfl.general_libraries.traffic.Rate;
import ch.epfl.javancox.results_manager.SmartDataPointCollector;
import ch.epfl.javancox.results_manager.gui.DefaultResultDisplayingGUI;
import edu.columbia.lrl.LWSim.AbstractTrafficGenerator;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.LWSim.GraphicalLWSimExperiment;
import edu.columbia.lrl.LWSim.PerDestinationReceivedPacketEndCriterium;
import edu.columbia.lrl.LWSim.analysers.AbstractLWSimAnalyser;
import edu.columbia.lrl.LWSim.builders.AbstractTopologyBuilder;
import edu.columbia.lrl.LWSim.traffic.DefaultPoissonTrafficGenerator;
import edu.columbia.lrl.LWSim.traffic.RelativeToReferenceBWLoad;
import edu.columbia.lrl.experiments.spinet.link_model.AbstractLinkDistanceModel;
import edu.columbia.lrl.experiments.spinet.link_model.ConstantLinkDistanceModel;
import edu.columbia.lrl.experiments.spinet.variants.FastNackVariant;
import edu.columbia.lrl.experiments.spinet.variants.SpinnetVariant;
import edu.columbia.lrl.experiments.spinet.FromGeneratorSpinetTopologyBuilder;
import edu.columbia.lrl.switch_arch.ShiftGenerator;


public class Explicit44Configuration {
	
	public static void main(String[] args) {
		
		boolean graphical = true;
		
		int maxBufferSize = 10000;
		double bufferLatency = 0.5;
		SmartDataPointCollector db = new SmartDataPointCollector();	
		
		double[] pow = new double[]{0, 0.5, 1, 1.5, 2, 2.5};
		double[] packetSi = new double[pow.length];
		for (int i = 0 ; i < pow.length ; i++) {
			packetSi[i] = Math.pow(10, pow[i])*3162;
		}
		
		for(double switchingTime : new double[]{1}){
			for (int seed : new int[]{1,2,4,5,6,7,8,9,10/*,11,12,13,14,15*/}) {
				for (int packetSize : new int[]{10000}) { // with 4800, transmission time is 30ns
			//	for (double packetSize : packetSi) {
					for (double load : new double[]{/*0.02,*/ /*0.025, 0.05, 0.075, 0.1, 0.125,*/ 0.15, 0.175, 0.2/*, 0.225, .25,.275, .3,.325, .35, .375 ,.4,.425/*, .45,0.475,.5/*, .6, .7, .8, .9*/}) {
							for (boolean selfTraf : new boolean[]{false}){
								for (boolean doubleWay : new boolean[]{/*false, */true}){								
									for (SpinnetVariant spinnetVariant : new SpinnetVariant[]{
								//			new ACKVariant(1, 100, maxBufferSize, bufferLatency, switchingTime, doubleWay),
								//			new BlockingVariant(false, maxBufferSize, bufferLatency, switchingTime, doubleWay), 
								//			new NACKVariant(1, maxBufferSize, bufferLatency, switchingTime, doubleWay),
											new FastNackVariant(1, maxBufferSize, bufferLatency, switchingTime, doubleWay)
									}) {
										for (AbstractLinkDistanceModel linkDistMod : new AbstractLinkDistanceModel[]{
											new ConstantLinkDistanceModel(1, 0.2), // constant one meter
										//	new ConstantLinkDistanceModel(2), // constant two meter
										//	new SymmetricLinearLinkDistanceModel(0.2, 1), // between 20cm and 1m
										//	new SymmetricLinearLinkDistanceModel(0.15, 2), // between 15cm and 2m
										}) {
											for (int extraStages : new int[]{0}) {
												for (int clients : new int[]{/*4,8,16,*/32/*32,8,16,32*/ }) {
													for (AbstractTrafficGenerator trafGenerator : new AbstractTrafficGenerator[]{
														new DefaultPoissonTrafficGenerator(new RelativeToReferenceBWLoad(load), packetSize),
													//	new ParallelTrafficGenerator()
													//	new SinglePopulationGenerator()
													}) {
												
													AbstractTopologyBuilder builder = new FromGeneratorSpinetTopologyBuilder(
														new ShiftGenerator(clients/2,extraStages), spinnetVariant, linkDistMod, selfTraf,
														Rate.ONE_GBIT_S.multiply(160));
												//	AbstractTopologyBuilder builder = new FourFourBuilder();
													
				
													LWSIMExperiment lwSimExperiment;
													if (graphical) {
														lwSimExperiment = new LWSIMExperiment(builder, 
																							  trafGenerator,
																						      seed,
																						      new PerDestinationReceivedPacketEndCriterium(10000),
																						      (AbstractLWSimAnalyser[])null,
																						      false);
													} else {
														lwSimExperiment = new GraphicalLWSimExperiment(builder,
																                                      trafGenerator,
																									   seed, 
																									   new PerDestinationReceivedPacketEndCriterium(10000),
																									   null,
																									   false);
													}
													
													lwSimExperiment.run(db, null);
													}
												}
											}
										}
									}
								}
							}
					}
				}
			}	
		}
		System.out.println(db);
		DefaultResultDisplayingGUI.displayDefault(db);
	}
}
