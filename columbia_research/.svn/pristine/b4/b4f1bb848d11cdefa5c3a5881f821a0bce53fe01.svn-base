package edu.columbia.lrl.switch_arch;

import java.util.ArrayList;
import java.util.Map;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.gui.reflected.MethodDef;
import ch.epfl.general_libraries.gui.reflected.ParameterDef;
import ch.epfl.general_libraries.utils.SimpleMap;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.network.NodeContainer;
import edu.columbia.lrl.switch_arch.AbstractSwitchArchitectureGenerator;

//for our problem 3
public class ButterflyTopology extends AbstractSwitchArchitectureGenerator {
    
	private double log2 = Math.log(2);
    
	private int nodes;
    
	public ButterflyTopology(@ParamName(name="Number of inputs", default_="8") int inputs) {
		this.nodes = inputs/2;
	}
    
	public int getMaxNumberOfStages() {
		return getNumberOfStages();
	}
    
	public int getNumberOfStages() {
		return (int)Math.ceil(log2(nodes))+1;
	}
    
	public int getNumberOfNodes() {
		return nodes*getNumberOfStages();
	}
    
	public ArrayList<Integer> getInputNodesIndexes() {
		int nodestages = getNumberOfStages();
		ArrayList<Integer> inputs = new ArrayList<Integer>();
		for (int i = 0 ; i < nodes*2 ; i++) {
			inputs.add(nodestages*nodes + i);
		}
		return inputs;
	}
    
	public ArrayList<Integer> getOutputNodesIndexes() {
		int nodestages = getNumberOfStages();
		ArrayList<Integer> outputs = new ArrayList<Integer>();
		for (int i = 0 ; i < nodes*2 ; i++) {
			outputs.add(nodestages*nodes + 2*nodes + i);
		}
		return outputs;
	}
    
	public ArrayList<Integer> getSwitchingNodesIndexes() {
		ArrayList<Integer> answer = new ArrayList<Integer>();
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

		int[] nodeIndexes = new int[nodestages*nodes + 4*nodes];

		int nodeIndex = 0;
		for (int i = 0 ; i < nodestages ; i++) {
			for (int j = 0 ; j < nodes ; j++) {
				NodeContainer nc = agh.newNode(i*200,j*80);
				nodeIndexes[nodeIndex++] = nc.getIndex();

				nc.attribute("switch_id").setValue(String.format("%1$-3d", i) + "-" +  
						String.format("%1$3d",j));
				nc.attribute("type").setValue(TYPE_2x2);
			}
		}

		for(int k =0;k<nodestages;k++){
			for (int i = 0; i < (int)Math.pow(2,k); i++) {
				for (int j = 0; j < nodes/((int)Math.pow(2, k+1)); j++) {
					int startNode = (k*nodes) + j+i*nodes/((int)Math.pow(2,k));
					int endNode1 = ((k+1)*nodes) + j+i*nodes/((int)Math.pow(2,k)); 
					int endNode2 = ((k+1)*nodes) +j+i*nodes/((int)Math.pow(2,k)) + nodes/((int)Math.pow(2,k+1));

					agh.newLink(nodeIndexes[startNode], 
							nodeIndexes[endNode1]).attribute("directed").setValue("true");
					agh.newLink(nodeIndexes[startNode], 
							nodeIndexes[endNode2]).attribute("directed").setValue("true");

				}

				for (int j = 0; j < nodes/((int)Math.pow(2, k+1)); j++) {
					int startNode = (k*nodes) + j+i*nodes/((int)Math.pow(2,k)) + nodes/((int)Math.pow(2,k+1));
					int endNode1 = ((k+1)*nodes) + j+i*nodes/((int)Math.pow(2,k)) + nodes/((int)Math.pow(2,k+1)); 
					int endNode2 = ((k+1)*nodes) +j+i*nodes/((int)Math.pow(2,k)) ;

					agh.newLink(nodeIndexes[startNode], 
							nodeIndexes[endNode1]).attribute("directed").setValue("true");
					agh.newLink(nodeIndexes[startNode], 
							nodeIndexes[endNode2]).attribute("directed").setValue("true");

				}

			}
		}

		for (int i = 0 ; i < nodes ; i++) {
			NodeContainer nc = agh.newNode(-100, i*80 - 25);
			nodeIndexes[nodeIndex++] = nc.getIndex();
			NodeContainer nc2 = agh.newNode(-100, i*80 + 25);
			nodeIndexes[nodeIndex++] = nc2.getIndex();
			nc.attribute("input").setValue(""+(2*i));
			nc2.attribute("input").setValue(""+((2*i)+1));
			agh.newLink(nc.getIndex(),nodeIndexes[i]).attribute("directed").setValue("true");
			agh.newLink(nc2.getIndex(),nodeIndexes[i]).attribute("directed").setValue("true");
		}
		for (int i = 0 ; i < nodes ; i++) {
			NodeContainer nc = agh.newNode(nodestages*200 - 100, i*80 - 25);
			nodeIndexes[nodeIndex++] = nc.getIndex();
			NodeContainer nc2 = agh.newNode(nodestages*200 - 100, i*80 + 25);
			nodeIndexes[nodeIndex++] = nc2.getIndex();
			nc.attribute("output").setValue(""+(2*i));
			nc2.attribute("output").setValue(""+((2*i)+1));
			agh.newLink(nodeIndexes[(nodestages-1)*nodes + i],nc.getIndex()).attribute("directed").setValue("true");
			agh.newLink(nodeIndexes[(nodestages-1)*nodes + i],nc2.getIndex()).attribute("directed").setValue("true");
		}
	}
    
	public Map<String, String> getGeneratorParameters() {
		return SimpleMap.getMap("Producers", nodes +"");
	}
    
	public String getName() {
		return "butterfly_topology";
	}
    
	private double log2(double num) {
		return (Math.log(num)/log2);
	}
    
    //the wrapper class
	public static class ButterflyTopology_ extends WebTopologyGeneratorStub {
        
		@MethodDef
		public String generateButterfly(AbstractGraphHandler agh,
                                    @ParameterDef (name="Desired number of vertices")int numberOfNodes) {
            
			ButterflyTopology gen = new ButterflyTopology(numberOfNodes);
			gen.generate(agh);
			return null;
		}
	}
    
	@Override
	public boolean hasOwnRouting() {
		return false;
	}
    
	@Override
	public int[][] getRouting() {
		throw new IllegalStateException("This topology network has no own routing");
	}

	@Override
	public int getNumberOfLinksInvolved() {
		return nodes*2*(getNumberOfStages()-1);
	}
}
