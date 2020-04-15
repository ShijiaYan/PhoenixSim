package edu.columbia.lrl.switch_arch;

import java.util.ArrayList;
import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.gui.reflected.MethodDef;
import ch.epfl.general_libraries.gui.reflected.ParameterDef;
import ch.epfl.general_libraries.traffic.Rate;
import ch.epfl.general_libraries.utils.SimpleMap;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.network.NodeContainer;
import ch.epfl.javancox.results_manager.SmartDataPointCollector;
import ch.epfl.javancox.results_manager.gui.DefaultResultDisplayingGUI;
import edu.columbia.lrl.LWSim.AbstractTrafficGenerator;
import edu.columbia.lrl.LWSim.GraphicalLWSimExperiment;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.LWSim.PerDestinationReceivedPacketEndCriterium;
import edu.columbia.lrl.LWSim.analysers.AbstractLWSimAnalyser;
import edu.columbia.lrl.LWSim.builders.AbstractTopologyBuilder;
import edu.columbia.lrl.LWSim.traffic.DefaultPoissonTrafficGenerator;
import edu.columbia.lrl.LWSim.traffic.RelativeToReferenceBWLoad;
import edu.columbia.lrl.experiments.spinet.FromGeneratorSpinetTopologyBuilder;
import edu.columbia.lrl.experiments.spinet.link_model.ConstantLinkDistanceModel;
import edu.columbia.lrl.experiments.spinet.variants.FastNackVariant;

public class Crossbar extends AbstractSwitchArchitectureGenerator {
	
	
	public static void main(String[] args) {
		boolean graphical = true;
		
		int maxBufferSize = 100000;
		double bufferLatency = 0.5;
		SmartDataPointCollector db = new SmartDataPointCollector();	
		
		double[] pow = new double[]{0, 0.5, 1, 1.5, 2, 2.5};
		double[] packetSi = new double[pow.length];
		for (int i = 0 ; i < pow.length ; i++) {
			packetSi[i] = Math.pow(10, pow[i])*3162;
		}

		for(double switchingTime : new double[]{1}){
			for (int seed : new int[]{1}) {
				for (int packetSize : new int[]{10000}) { // with 4800, transmission time is 30ns
					for (double load : new double[]{/*0.02,*/ 0.025, 0.05, 0.075, 0.1, 0.125, 0.15, 0.175, 0.2, 0.225, .25,.275, .3,.325, .35, .375 ,.4,.425, .45,0.475,.5, .6, .7/*, .8, .9*/}) {
							for (boolean selfTraf : new boolean[]{false}){							
								FastNackVariant spinnetVariant = new FastNackVariant(1, maxBufferSize, bufferLatency, switchingTime, true);
								ConstantLinkDistanceModel linkDistMod = new ConstantLinkDistanceModel(1, 0.2);
								for (int clients : new int[]{/*4,8,16,*/8/*32,8,16,32*/ }) {
									for (AbstractTrafficGenerator trafGenerator : new AbstractTrafficGenerator[]{
											new DefaultPoissonTrafficGenerator(new RelativeToReferenceBWLoad(load), packetSize),
									}) {
										for (AbstractSwitchArchitectureGenerator switgen : new AbstractSwitchArchitectureGenerator[]{
												new ShiftGenerator(clients/2,0),	
												new Crossbar(clients)
										}) {
											
											AbstractTopologyBuilder builder = new FromGeneratorSpinetTopologyBuilder(
													switgen,
													spinnetVariant, linkDistMod, selfTraf, Rate.ONE_GBIT_S.multiply(160));

											LWSIMExperiment lwSimExperiment;
											if (graphical) {
												lwSimExperiment = new LWSIMExperiment(builder, 
														trafGenerator,
														seed, 
														new PerDestinationReceivedPacketEndCriterium(10000), 
														(AbstractLWSimAnalyser[])null,
														false /*timeline*/);
											} else {
												lwSimExperiment = new GraphicalLWSimExperiment(builder, 
														trafGenerator,
														seed, 
														new PerDestinationReceivedPacketEndCriterium(10000), 
														null,
														false /*timeline*/);
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
		System.out.println(db);
		DefaultResultDisplayingGUI.displayDefault(db);		
	}
	
	
	
	
	
	private int inputs = 0;

	public Crossbar(@ParamName(name="Inputs", default_="8") int inputs) {
		this.inputs = inputs;
	}
	@Override
	public boolean hasOwnRouting() {
		return true;
	}
	@Override	
	public int[][] getRouting() {
		int[][] decisions = new int[getSwitchingNodesIndexes().size()][inputs];
		for (int i = 0 ; i < inputs ; i++) {
			for (int j = 0 ; j < inputs ; j++) {
				if (i == j) {
					decisions[i][j] = 1;
				} else if (j > i) {
					decisions[i][j] = 0;
				} else {
					decisions[i][j] = -1;
				}
			}
		}
		decisions[inputs-1][inputs-1] = 0;
		for (int k = inputs ; k < (inputs*inputs)-2 ; k++) {
			int i = k % inputs;
			for (int j = 0 ; j < inputs ; j++) {		
				if (i == j) {
					decisions[k][j] = 0;
				} else if (j > i) {
					decisions[k][j] = 1;
				} else {
					decisions[k][j] = -1;
				}
			}
		}
		return decisions;
	}
	

	@Override
	public ArrayList<Integer>  getInputNodesIndexes() {
		ArrayList<Integer>  answer = new ArrayList<Integer>();
		for (int i = 0 ; i < inputs ; i++) {
			answer.add(inputs*inputs - 1 + i);
		}
		return answer;
	}

	@Override
	public ArrayList<Integer> getOutputNodesIndexes() {
		ArrayList<Integer>  answer = new ArrayList<Integer>();
		for (int i = 0 ; i < inputs ; i++) {
			answer.add(inputs*inputs - 1 + inputs + i);
		}
		return answer;
	}
	

	@Override
	public ArrayList<Integer> getSwitchingNodesIndexes() {
		ArrayList<Integer> answer = new ArrayList<Integer>();
		for (int i = 0 ; i < getNumberOfNodes() ; i++) {
			answer.add(i);
		}
		return answer;
	}	

	@Override
	public int getMaxNumberOfStages() {
		return inputs+inputs-1;
	}
	

	@Override
	public int getNumberOfNodes() {
		return (inputs*inputs)-1;
	}	

	@Override
	public void generate(AbstractGraphHandler agh) {
		ensureLayer(agh);
		for (int j = 0 ; j < inputs ; j++) {
			for (int i = 0 ; i < inputs ; i++) {
				if (i != j || i < inputs-1) {
					NodeContainer nc = agh.newNode(i*100-(50*j), j*100);
					if (j == inputs -1) {
						nc.attribute("type").setValue(TYPE_1x2);
					} else if (i == inputs - 1) {
						nc.attribute("type").setValue(TYPE_2x1);
					} else {
						nc.attribute("type").setValue(TYPE_2x2);
					}
					nc.attribute("switch_id").setValue(String.format("%1$-3d", i) + "-" + String.format("%1$3d",j));
				}
			}
		}
		for (int i = 0 ; i < inputs -1 ; i++) {
			for (int j = 0 ; j < inputs ; j++) {
				if (j != inputs - 1 || i < inputs - 2) {
					agh.newLink(i+(inputs*j), i+(inputs*j)+1).attribute("directed").setValue("true");
				}
			}
		}
		
		for (int i = 0 ; i < inputs ; i++) {
			for (int j = 1 ; j < inputs ; j++) {
				if (i < inputs - 1 || j < inputs -1) {
					agh.newLink(i + (inputs*j), i + (inputs*(j-1))).attribute("directed").setValue("true");
				}
			}
		}
		agh.newLink(inputs*inputs -2, inputs*(inputs-1)-1).attribute("directed").setValue("true");
		
		for (int i = 0 ; i < inputs ; i++) {
			NodeContainer nc = agh.newNode(-(inputs*50)-200, i*100);
			nc.attribute("input").setValue(""+(inputs-1-i));
			agh.newLink(nc.getIndex(), i*inputs).attribute("directed").setValue("true");
		}
		// splitting the loop permits to have separated indexes
		for (int i = 0 ; i < inputs ; i++) {			
			NodeContainer nc2 = agh.newNode(i*100, -200);
			nc2.attribute("output").setValue(""+i);
			agh.newLink(i, nc2.getIndex()).attribute("directed").setValue("true");
		}
		
	}

	@Override
	public String getName() {
		return "Crossbar";
	}

	@Override
	public Map<String, String> getGeneratorParameters() {
		return SimpleMap.getMap("Clients", inputs+"");
	}
	
	public static class CrossbarGenerator_ extends WebTopologyGeneratorStub {

		@MethodDef
		public String generateCrossbar(AbstractGraphHandler agh,
				@ParameterDef (name="Inputs")int inputs) {
					
			Crossbar gen = new Crossbar(inputs);
			gen.generate(agh);
			return null;
		}
	}
	
	@Override
	public int getNumberOfLinksInvolved() {
		throw new IllegalStateException("Not supported");
	}	

}
