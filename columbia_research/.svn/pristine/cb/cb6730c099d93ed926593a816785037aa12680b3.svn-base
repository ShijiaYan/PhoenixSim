package edu.columbia.lrl.switch_arch;

import java.util.List;
import java.util.Map;

import ch.epfl.general_libraries.gui.reflected.MethodDef;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.base.Javanco;
import ch.epfl.javanco.network.NodeContainer;

public class PracticeGenerator extends AbstractSwitchArchitectureGenerator {
	
	public static void main(String args[]) {
		Javanco.initJavancoSafe();
		AbstractGraphHandler agh = Javanco.getDefaultGraphHandler(true);
		agh.newLayer("physical");
		
		PracticeGenerator p = new PracticeGenerator();
		p.generate(agh);
		
		System.out.println(p.getFullName() + " " + agh.getNumberOfNodes());
	}
	
	@Override
	public List<Integer> getInputNodesIndexes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Integer> getOutputNodesIndexes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Integer> getSwitchingNodesIndexes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getMaxNumberOfStages() {
		// TODO Auto-generated method stub
		return 0;
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
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void generate(AbstractGraphHandler agh) {
		ensureLayer(agh);
		
		NodeContainer node1 = agh.newNode(200,80);
		NodeContainer node2 = agh.newNode(400,80);
		agh.newLink(node1.getIndex(), node2.getIndex()).attribute("directed").setValue("true");		
	}

	@Override
	public String getName() {
		return "Practice";
	}

	@Override
	public Map<String, String> getGeneratorParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getNumberOfNodes() {
		// TODO Auto-generated method stub
		return 2;
	}
	
	public static class PracticeGenerator_ extends WebTopologyGeneratorStub {

		@MethodDef
		public String generateLink(AbstractGraphHandler agh) {
			PracticeGenerator p = new PracticeGenerator();
			p.generate(agh);
			return null;
		}
	}

}
