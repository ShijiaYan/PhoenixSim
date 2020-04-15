package edu.columbia.lrl.switch_arch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ch.epfl.general_libraries.gui.reflected.MethodDef;
import ch.epfl.general_libraries.gui.reflected.ParameterDef;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.network.LinkContainer;
import ch.epfl.javanco.network.NodeContainer;

public class Piloss extends AbstractSwitchArchitectureGenerator {
	
	private int nbNodes;
	private int extra;
	
	public Piloss(int numberOfNodes, int extra) {
		this.nbNodes = numberOfNodes;
		this.extra = extra;
	}

	@Override
	public List<Integer> getInputNodesIndexes() {	
		ArrayList<Integer> inputs = new ArrayList<Integer>();
		int offset = (extra+nbNodes/2)*((nbNodes/2));
		for (int i = 0 ; i < nbNodes ; i++) {
			inputs.add(i+offset);
		}
		return inputs;
	}

	@Override
	public List<Integer> getOutputNodesIndexes() {
		ArrayList<Integer> outputs = new ArrayList<Integer>();
		int offset = (extra+nbNodes/2)*((nbNodes/2)) + nbNodes;
		for (int i = 0 ; i < nbNodes ; i++) {
			outputs.add(i+offset);
		}
		return outputs;
	}

	@Override
	public List<Integer> getSwitchingNodesIndexes() {
		ArrayList<Integer> ss = new ArrayList<Integer>();
		int offset = (extra+nbNodes/2)*((nbNodes/2));
		for (int i = 0 ; i < offset ; i++) {
			ss.add(i);
		}
		return ss;
	}

	@Override
	public int getMaxNumberOfStages() {
		return extra+nbNodes/2;
	}

	@Override
	public boolean hasOwnRouting() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int[][] getRouting() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getNumberOfLinksInvolved() {
		return (nbNodes/2)*((extra+nbNodes/2)-1)*2;
	}

	@Override
	public void generate(AbstractGraphHandler agh) {
		List<NodeContainer> init = placeOneColumn(0, nbNodes/2, nbNodes, agh);
		List<NodeContainer> lc = init;

		for (int i = 1 ; i < extra+nbNodes/2 ; i++) {
			List<NodeContainer> l2 = placeOneColumn(i, nbNodes/2, nbNodes, agh);
			NodeContainer prev0 = lc.get(0);
			NodeContainer next0 = l2.get(0);
			agh.newLink(prev0, next0).attribute("directed").setValue("true");
			agh.newLink(prev0, l2.get(1)).attribute("directed").setValue("true");
			for (int j = 1 ; j < lc.size()-1 ; j++) {
				NodeContainer prevI = lc.get(j);
				NodeContainer nextIm = l2.get(j-1);
				NodeContainer nextIp = l2.get(j+1);
				agh.newLink(prevI, nextIm).attribute("directed").setValue("true");
				agh.newLink(prevI, nextIp).attribute("directed").setValue("true");
			}
			NodeContainer prevL = lc.get((nbNodes/2)-1);
			agh.newLink(prevL, l2.get((nbNodes/2)-1));
			agh.newLink(prevL, l2.get((nbNodes/2)-2));	
			lc = l2;
		}
		
		List<NodeContainer> inputs = placeOneColumn(-1, nbNodes, nbNodes, agh);
		for (int i = 0 ; i < inputs.size() ; i++) {
			agh.newLink(inputs.get(i), init.get(i/2)).attribute("directed").setValue("true");
		}
		
		List<NodeContainer> outputs = placeOneColumn(extra+nbNodes/2, nbNodes, nbNodes, agh);
		for (int i = 0 ; i < inputs.size() ; i++) {
			agh.newLink(lc.get(i/2), outputs.get(i)).attribute("directed").setValue("true");
		}		
	}

	@Override
	public String getName() {
		return "piloss";
	}

	@Override
	public Map<String, String> getGeneratorParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getNumberOfNodes() {
		return nbNodes* nbNodes /4;
	}
	
	public static class Piloss_ extends WebTopologyGeneratorStub {

		@MethodDef
		public String generatePiloss(AbstractGraphHandler agh,
				@ParameterDef (name="Desired number of Clients")int numberOfClients,
				@ParameterDef (name="Extra stage")int extraStages) {
					
			Piloss gen = new Piloss(numberOfClients, extraStages);
			gen.generate(agh);
			int index = 0;
			for (LinkContainer lc : agh.getLinkContainers()) {;
				lc.attribute("label").setValue(index);
				index++;
			}
			gen.getPossiblePaths();		
			System.out.println(gen.getNumberOfLinksInvolved());
			return null;
		}
	}	

}
