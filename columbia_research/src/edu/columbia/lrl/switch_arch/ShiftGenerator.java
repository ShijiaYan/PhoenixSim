package edu.columbia.lrl.switch_arch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.gui.reflected.MethodDef;
import ch.epfl.general_libraries.gui.reflected.ParameterDef;
import ch.epfl.general_libraries.utils.SimpleMap;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.network.NodeContainer;

public class ShiftGenerator extends AbstractSwitchArchitectureGenerator {
	
	private double log2 = Math.log(2);
	
	private int nodes;
	private int addStages;
	
	public ShiftGenerator(
		@ParamName(name="Number of inputs", default_="8") int inputs, 
		@ParamName(name="Additional shuffling stages", default_="0") int addStages) {
		this.nodes = inputs/2;
		this.addStages = addStages;
	}
	
	private int getNormalStages() {
		return (int)Math.ceil(log2(nodes));
	}
	
	public int getMaxNumberOfStages() {
		return getNumberOfStages();
	}
	
	public int getNumberOfStages() {
		return getNormalStages()+1+addStages;
	}
	
	public int getNumberOfNodes() {
		return nodes*getNumberOfStages();		
	}	
	
	public List<Integer> getInputNodesIndexes() {
		int nodestages = getNumberOfStages();		
		ArrayList<Integer> inputs = new ArrayList<>();
		for (int i = 0 ; i < nodes*2 ; i++) {
			inputs.add(nodestages*nodes + i);
		}
		return inputs;
	}
	
	public List<Integer> getOutputNodesIndexes() {
		int nodestages = getNumberOfStages();		
		ArrayList<Integer> outputs = new ArrayList<>();
		for (int i = 0 ; i < nodes*2 ; i++) {
			outputs.add(nodestages*nodes + 2*nodes + i);
		}
		return outputs;		
	}
	
	public List<Integer> getSwitchingNodesIndexes() {
		ArrayList<Integer> answer = new ArrayList<>();
		int nodestages = getNumberOfStages();
		int idx = 0;		
        for (int i = 0 ; i < nodestages ; i++) {
            for (int j = 0 ; j < nodes ; j++) {
            	answer.add(idx);
            	idx++;
            }
        }
        return answer;
	}

	
	public void generate(AbstractGraphHandler agh) {
		int nodestages = getNumberOfStages();
		int normalStages = getNormalStages()+1;
		
		int[] nodeIndexes = new int[nodestages*nodes + 4*nodes];
		
		int nodeIndex = 0;
        for (int i = 0 ; i < nodestages ; i++) {
            for (int j = 0 ; j < nodes ; j++) {
                NodeContainer nc = agh.newNode(i*200,j*80);
                nodeIndexes[nodeIndex++] = nc.getIndex();
                nc.attribute("switch_id").setValue(String.format("%1$-3d", i) + "-" + String.format("%1$3d",j));
                nc.attribute("type").setValue("2x2");
            }
        }
        for (int i = 0 ; i < normalStages-1 ; i++) {
            for (int j = 0 ; j < nodes ; j++) {
                int startNode = i*nodes +j;
                int endNode1 = (i+1)*nodes +j;
                int power = (int)Math.pow(2,i);
                int endNode2 = (i+1)*nodes + (power+j)%nodes;
                agh.newLink(nodeIndexes[startNode], nodeIndexes[endNode1]).attribute("directed").setValue("true");
                agh.newLink(nodeIndexes[startNode], nodeIndexes[endNode2]).attribute("directed").setValue("true");

            }
        }        
        for (int i = normalStages-1; i < nodestages-1 ; i++) {
            for (int j = 0 ; j < nodes ; j++) {
                int startNode = i*nodes +j;
                int endNode1 = (i+1)*nodes +j;
                int power = (i-normalStages+1)%(int)(Math.log(nodes)/Math.log(2));
                int endNode2 = (i+1)*nodes +(int)((Math.pow(2,power) +j)%nodes);
                agh.newLink(nodeIndexes[startNode], nodeIndexes[endNode1]).attribute("directed").setValue("true");
                agh.newLink(nodeIndexes[startNode], nodeIndexes[endNode2]).attribute("directed").setValue("true");
            }
        }
        
        for (int i = 0 ; i < nodes ; i++) {
        	NodeContainer nc = agh.newNode(-100, i*80 - 25);
        	nodeIndexes[nodeIndex++] = nc.getIndex();
        	NodeContainer nc2 = agh.newNode(-100, i*80 + 25);
        	nodeIndexes[nodeIndex++] = nc2.getIndex();
        	nc.attribute("input").setValue(""+ 2*i);
        	nc2.attribute("input").setValue(""+(2*i +1));
        	agh.newLink(nc.getIndex(), nodeIndexes[i]).attribute("directed").setValue("true");;
        	agh.newLink(nc2.getIndex(), nodeIndexes[i]).attribute("directed").setValue("true");;
        }
        for (int i = 0 ; i < nodes ; i++) {       	
        	NodeContainer nc = agh.newNode(nodestages*200 - 100, i*80 - 25);
        	nodeIndexes[nodeIndex++] = nc.getIndex();
        	NodeContainer nc2 = agh.newNode(nodestages*200 - 100, i*80 + 25);
        	nodeIndexes[nodeIndex++] = nc2.getIndex();
        	nc.attribute("output").setValue(""+ 2*i);
        	nc2.attribute("output").setValue(""+(2*i +1));
        	agh.newLink(nodeIndexes[(nodestages-1)*nodes + i], nc.getIndex()).attribute("directed").setValue("true");;
        	agh.newLink(nodeIndexes[(nodestages-1)*nodes + i], nc2.getIndex()).attribute("directed").setValue("true");;        	
        }
	}
	
	public Map<String, String> getGeneratorParameters() {
		return SimpleMap.getMap("Clients", 2*nodes +"", "Extra stages", addStages+"");
	}
	
	public String getName() {
		return "shift_generator";
	}
	
	private double log2(double num) {
		return Math.log(num)/log2;
	} 

	@Override
	public boolean hasOwnRouting() {
		return false;
	}

	@Override
	public int[][] getRouting() {
		throw new IllegalStateException("This shift has no own routing");
	}
	
	
	public static class ShiftGenerator_ extends WebTopologyGeneratorStub {

		@MethodDef
		public String generateShift(AbstractGraphHandler agh,
				@ParameterDef (name="Desired number of vertices")int numberOfNodes,
				@ParameterDef (name="Extra stages")int extraStages) {
					
			ShiftGenerator gen = new ShiftGenerator(numberOfNodes, extraStages);
			gen.generate(agh);
			return null;
		}
	}


	@Override
	public int getNumberOfLinksInvolved() {
		// TODO Auto-generated method stub
		return 0;
	}	
}
