package edu.columbia.lrl.switch_arch;

import java.util.ArrayList;
import java.util.Map;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.gui.reflected.MethodDef;
import ch.epfl.general_libraries.gui.reflected.ParameterDef;
import ch.epfl.general_libraries.utils.SimpleMap;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.network.LinkContainer;
import ch.epfl.javanco.network.NodeContainer;
import edu.columbia.lrl.switch_arch.AbstractSwitchArchitectureGenerator;

public class BenesSwitchGenerator extends AbstractSwitchArchitectureGenerator {
	
	private double log2 = Math.log(2);
	
	private int nodes;
	
	public BenesSwitchGenerator(
		@ParamName(name="Number of inputs", default_="8") int inputs) {
		this.nodes = inputs/2;
	}
	
	public int getMaxNumberOfStages() {
		return getNumberOfStages();
	}
	
	public int getNumberOfStages() {
		return (int)Math.ceil(2*log2(2*nodes)-1);
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
//		int nodestages = getNumberOfStages();		// columns of switches
//		int normalStages = getNormalStages();		// number of switching stages; nodestages = normalStages + 1;
		int nodestages = getNumberOfStages();
	//	int normalStages = nodestages/2 +1;
		ensureLayer(agh);
		int[] nodeIndexes = new int[nodestages*nodes + 4*nodes];
		
		// generate the switching nodes
		int nodeIndex = 0;
        for (int i = 0 ; i < nodestages ; i++) {
            for (int j = 0 ; j < nodes ; j++) {
                NodeContainer nc = agh.newNode(i*200,j*80);
                nodeIndexes[nodeIndex++] = nc.getIndex();
                nc.attribute("switch_id").setValue(String.format("%1$-3d", i) + "-" + String.format("%1$3d",j));
                nc.attribute("type").setValue(TYPE_2x2);
            }
        }
        
        
        // connect the switches
        for (int i = 0 ; i < nodestages - 1; i++) {
    	
        	if (i < nodestages/2) {
        		int nbGroups = (int)Math.pow(2, i);
        		int nodesInGroup = nodes / nbGroups;           		
        		for (int j = 0 ; j < nbGroups ; j++) {
        			int startStartIndex = (nodes*i) + (j*nodesInGroup);
        			int startEndIndex1 = (nodes*(i+1)) + (j*nodesInGroup);
        			int startEndIndex2 = (nodes*(i+1)) + (j*nodesInGroup) + (nodesInGroup/2);
        			for (int k = 0 ; k < nodesInGroup ; k++) {
        				agh.newLink(startStartIndex+k, startEndIndex1).attribute("directed").setValue("true");;
        				agh.newLink(startStartIndex+k, startEndIndex2).attribute("directed").setValue("true");;
        				if (k % 2 == 1) {
        					startEndIndex1++;
        					startEndIndex2++;
        				}
        			}
        		}
        	} else {
        		int nbGroups = (int)Math.pow(2, nodestages - i - 2);
        		int nodesInGroup = nodes / nbGroups;   
        		for (int j = 0 ; j < nbGroups ; j++) {
        			int startStartIndex1 = (nodes*i) + (j*nodesInGroup);
        			int startStartIndex2 = (nodes*i) + (j*nodesInGroup) + (nodesInGroup/2);
        			
        			int startEndIndex = (nodes*(i+1)) + (j*nodesInGroup);
        			for (int k = 0 ; k < nodesInGroup ; k++) {
        				agh.newLink(startStartIndex1, startEndIndex+k).attribute("directed").setValue("true");;
        				agh.newLink(startStartIndex2, startEndIndex+k).attribute("directed").setValue("true");;
        				if (k % 2 == 1) {
        					startStartIndex1++;
        					startStartIndex2++;
        				}
        			}
        		}
        	}
        	
        	/*for (int k = 0; k < power; k++){
	            for (int j = 0 ; j < (nodes/power) ; j++) {
	                int startNode = (i*nodes)+k*(nodes/power)+j;
	                int endNode1 = ((i+1)*nodes)+k*(nodes/power)+(int)Math.floor(j/2);	                
	                int endNode2 = ((i+1)*nodes)+k*(nodes/power)+(int)Math.floor(j/2)+(nodes/power)/2;
	                agh.newLink(nodeIndexes[startNode], nodeIndexes[endNode1]).attribute("directed").setValue("true");
	                agh.newLink(nodeIndexes[startNode], nodeIndexes[endNode2]).attribute("directed").setValue("true");
	            }
        	}*/
        }   

        //Generate clients
        for (int i = 0 ; i < nodes ; i++) {
        	NodeContainer nc = agh.newNode(-100, i*80 - 25);
        	nodeIndexes[nodeIndex++] = nc.getIndex();
        	NodeContainer nc2 = agh.newNode(-100, i*80 + 25);
        	nodeIndexes[nodeIndex++] = nc2.getIndex();
        	nc.attribute("input").setValue(""+(2*i));
        	nc2.attribute("input").setValue(""+((2*i)+1));
        	agh.newLink(nc.getIndex(), nodeIndexes[i]).attribute("directed").setValue("true");;
        	agh.newLink(nc2.getIndex(), nodeIndexes[i]).attribute("directed").setValue("true");;
        }
        for (int i = 0 ; i < nodes ; i++) {       	
        	NodeContainer nc = agh.newNode(nodestages*200 - 100, i*80 - 25);
        	nodeIndexes[nodeIndex++] = nc.getIndex();
        	NodeContainer nc2 = agh.newNode(nodestages*200 - 100, i*80 + 25);
        	nodeIndexes[nodeIndex++] = nc2.getIndex();
        	
        	
        	nc.attribute("output").setValue(""+(2*i));
        	nc2.attribute("output").setValue(""+((2*i)+1));
        	
        	agh.newLink(nodeIndexes[(nodestages-1)*nodes + i], nc.getIndex()).attribute("directed").setValue("true");;
        	agh.newLink(nodeIndexes[(nodestages-1)*nodes + i], nc2.getIndex()).attribute("directed").setValue("true");;        	
        }
	}
	
	public Map<String, String> getGeneratorParameters() {
		return SimpleMap.getMap("Producers", nodes +"");
	}
	
	public String getName() {
		return "shift_generator";
	}
	
	private double log2(double num) {
		return (Math.log(num)/log2);
	} 
		
	public static class BenesSwitch extends WebTopologyGeneratorStub {

		@MethodDef
		public String generateCLOS(AbstractGraphHandler agh,
				@ParameterDef (name="Number of inputs")int numberOfNodes)
				//@ParameterDef (name="Extra stages")int extraStages) 
				{
					
			BenesSwitchGenerator gen = new BenesSwitchGenerator(numberOfNodes);
			gen.generate(agh);
			int index = 0;
			for (LinkContainer lc : agh.getLinkContainers()) {;
				lc.attribute("label").setValue(index);
				index++;
			}
			gen.getPossiblePaths();
			return null;
		}
	}

	@Override
	public boolean hasOwnRouting() {
		return false;
	}

	@Override
	public int[][] getRouting() {
		throw new IllegalStateException("This shift has no own routing");
	}

	@Override
	public int getNumberOfLinksInvolved() {
		return (getNumberOfStages()-1)*nodes*2;
	}
	
}
