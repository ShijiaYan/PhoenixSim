package edu.columbia.lrl.switch_arch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.event.CasualEvent;
import ch.epfl.general_libraries.gui.reflected.MethodDef;
import ch.epfl.general_libraries.gui.reflected.ParameterDef;
import ch.epfl.general_libraries.utils.SimpleMap;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.network.NodeContainer;

public class GeneralizedGenerator extends AbstractSwitchArchitectureGenerator {
	
	private int numberOfClients;
	private int logRadix;
	private int splits;
	private int addStages;	
	
	public GeneralizedGenerator(@ParamName(name="Log (ports)") int logNumberOfClients, 
								@ParamName(name="Splits", default_="0") int splits, 
								@ParamName(name="Additional stages", default_="0") int addStages) {
		if (logNumberOfClients < 1) throw new IllegalStateException("With less than 2 clients it is not a switch");
		if (splits > logNumberOfClients + 1) throw new IllegalStateException("Splits cannot be larger than logClients + 1");
		if (logNumberOfClients == 1 && splits == 0 && addStages > 0) throw new IllegalStateException("Cannot add additional stages with two clients");
		this.logRadix = logNumberOfClients;
		this.numberOfClients = (int)Math.pow(2, logNumberOfClients);
		this.splits = splits;
		this.addStages = addStages;
	}

	@Override
	public List<Integer> getInputNodesIndexes() {
		ArrayList<Integer> l = new ArrayList<>(numberOfClients);
		for (int i = 0 ; i < numberOfClients ; i++)
			l.add(i);
		return l;
	}

	@Override
	public List<Integer> getOutputNodesIndexes() {
		int endIndex = getNumberOfInternalNodes() + numberOfClients;
		ArrayList<Integer> l = new ArrayList<>(endIndex - numberOfClients);
		for (int i = endIndex ; i < endIndex + numberOfClients ; i++)
			l.add(i);
		return l;
	}

	@Override
	public List<Integer> getSwitchingNodesIndexes() {
		int endIndex = getNumberOfInternalNodes() + numberOfClients;
		ArrayList<Integer> l = new ArrayList<>(endIndex - numberOfClients);
		for (int i = numberOfClients ; i < endIndex ; i++)
			l.add(i);
		return l;
	}

	@Override
	public int getMaxNumberOfStages() {
		return logRadix + splits + addStages;
	}
	

	@Override
	public String getName() {
		return "Generalized switch";
	}

	@Override
	public Map<String, String> getGeneratorParameters() {
		return SimpleMap.getMap("Splits", splits+"", "Additional stages", addStages+"");
	}

	@Override
	public int getNumberOfNodes() {
		return getNumberOfInternalNodes() + 2*numberOfClients;
	}
	
	private int getNumberOfInternalNodes() {
		int sum;
		if (splits == 0) {
			sum = (logRadix + addStages)*numberOfClients/2;
		} else {
			sum = 0;
			int nodeOnLastStage = numberOfClients;
			for (int i = 1 ; i <= splits ; i++) {
				nodeOnLastStage = numberOfClients * (int)Math.pow(2, i-1);
				sum += nodeOnLastStage*2;
			}
			sum += (Math.max(logRadix - splits, 0) + addStages)*nodeOnLastStage;
		}	
		return sum;
	}

	@Override
	public boolean hasOwnRouting() {
		return false;
	}

	@Override
	public int[][] getRouting() {
		return null;
	}

	@Override
	public void generate(AbstractGraphHandler agh) {
		ensureLayer(agh);
		int maxNodePerColumn = (int)Math.max(numberOfClients, numberOfClients * Math.pow(2, splits-1));
		int column = 0;
		
		// place input nodes
		List<NodeContainer> l0 = placeOneColumn(0, numberOfClients, maxNodePerColumn, agh);
		int index = 0;
		for (NodeContainer nc: l0)
        	nc.attribute("input").setValue(""+index++);
		column++;
		
		// place division stages (or merge if no splits)
		if (splits == 0) {
			List<NodeContainer> l1 = placeOneColumn(1, numberOfClients/2, maxNodePerColumn, agh);
			column++;			
			connectMerge(l0,  l1, agh);
			l0 = l1;
		} else {
			for (int i = 0 ; i < splits ; i++) {
				List<NodeContainer> li = placeOneColumn(column, (int)(numberOfClients*Math.pow(2, i)), maxNodePerColumn, agh);
				column++;
				if (li.size() == l0.size())
					connectStraight(l0, li, agh);
				else
					connectDivide(l0, li, agh);
				l0 = li;
			}
		}
		
		// adding additional stages
		int reqstages = addStages;
		if (splits == 0) {
			reqstages += logRadix -1;
		} else {
			reqstages += Math.max(logRadix - splits, 0);
		}
		List<NodeContainer> ls = l0;
		for (int i = 0 ; i < reqstages ; i++) {
			ls = placeOneColumn(column, l0.size(), maxNodePerColumn, agh);
			column++;
			connectStage(l0, ls, i, agh);
			l0 = ls;
		}
		
		if (splits > 0) {
			// adding the conciliate stage
			ls = placeOneColumn(column, l0.size(), maxNodePerColumn, agh);	
			column++;
			connectConciliate(l0, ls, agh);		
			l0 = ls;
		} else {
		/*	ls = placeOneColumn(column, l0.size(), maxNodePerColumn, agh);
			column++;
			connectStage(l0, ls, 0, agh);
			l0 = ls;	*/		
		}
		
		while (ls.size() > numberOfClients) {
			ls = placeOneColumn(column, ls.size()/2, maxNodePerColumn, agh);
			column++;
			connectMerge(l0, ls, agh);
			l0 = ls;
		}
		
		// placing the output stage
		List<NodeContainer> last = placeOneColumn(column, numberOfClients, maxNodePerColumn, agh);
		index = 0;
		for (NodeContainer nc: last)
        	nc.attribute("output").setValue(""+index++);		
		if (splits == 0) {
			connectDivide(l0, last, agh);
		} else {
			connectStraight(l0, last, agh);
		}
		
		label(agh);

		//agh.fireAllElementsModificationEvent(new CasualEvent(this));		
		//System.out.println("Output " + getOutputNodesIndexes());
		//System.out.println("Switching " + getSwitchingNodesIndexes());	
		//System.out.println("Stages " + getMaxNumberOfStages());
	}
	

	
	private void connectStraight(List<NodeContainer> l1, List<NodeContainer> l2, AbstractGraphHandler agh) {
		for (int i = 0 ; i < l1.size() ; i++) {
			agh.newLink(l1.get(i), l2.get(i)).attribute("directed").setValue("true");
		}
	}
	
	private void connectDivide(List<NodeContainer> l1, List<NodeContainer> l2, AbstractGraphHandler agh) {
		for (int i = 0 ; i < l1.size() ; i++) {
			agh.newLink(l1.get(i), l2.get(2*i)).attribute("directed").setValue("true");
			agh.newLink(l1.get(i), l2.get(2*i +1)).attribute("directed").setValue("true");
		}
	}
	
	private void connectMerge(List<NodeContainer> l1, List<NodeContainer> l2, AbstractGraphHandler agh) {
		for (int i = 0 ; i < l2.size() ; i++) {
			agh.newLink(l1.get(2*i), l2.get(i)).attribute("directed").setValue("true");
			agh.newLink(l1.get(2*i+1), l2.get(i)).attribute("directed").setValue("true");			
		}		
	}
	
	private void connectStage(List<NodeContainer> l1, List<NodeContainer> l2, int index, AbstractGraphHandler agh) {
		int j = 0;
        for (NodeContainer n1 : l1) {
            agh.newLink(n1, l2.get(j)).attribute("directed").setValue("true");
            agh.newLink(n1, l2.get(j + 1)).attribute("directed").setValue("true");
            j = (j + 2) % l2.size();
        }
	}
	
	private void connectConciliate(List<NodeContainer> l1, List<NodeContainer> l2, AbstractGraphHandler agh) {
		ArrayList<Stack<Integer>> groups = new ArrayList<>(numberOfClients);
		int nodePerGroup = l1.size()/numberOfClients;
		int index = 0;
		for (int i = 0 ; i < numberOfClients ; i++) {
			Stack<Integer> forGroup = new Stack<>();
			for (int j = 0 ; j < nodePerGroup ; j++) {
				forGroup.add(l2.get(index).getIndex());
				forGroup.add(l2.get(index).getIndex());
				index++;				
			}
			groups.add(forGroup);
		}
		
		for (int i = 0 ; i < l1.size() ; i++) {
			NodeContainer n1 = l1.get(i);
			
			int dest = groups.get(2*i % groups.size()).pop();
			agh.newLink(n1.getIndex(), dest).attribute("directed").setValue("true");
			dest = groups.get((2*i +1) % groups.size()).pop();
			agh.newLink(n1.getIndex(), dest).attribute("directed").setValue("true");
			
			agh.fireAllElementsModificationEvent(new CasualEvent(this));	
		}	
	}
	
	private void label(AbstractGraphHandler agh) {
		for (Integer i : getSwitchingNodesIndexes()) {
			NodeContainer nc = agh.getNodeContainer(i);
			if (nc.getIncomingLinks().size() == 2) {
				if (nc.getOutgoingLinks().size() == 2) {
					nc.attribute("type").setValue(TYPE_2x2);
				} else {
					nc.attribute("type").setValue(TYPE_2x1);
				}
			} else {
				if (nc.getOutgoingLinks().size() == 2) {
					nc.attribute("type").setValue(TYPE_1x2);
				} else {
					throw new IllegalStateException();
				}				
			}
		}
	}

	public static class GeneralizedGenerator_ extends WebTopologyGeneratorStub {

		@MethodDef
		public String generate(AbstractGraphHandler agh,
				@ParameterDef (name="Log of clients")int log,
				@ParameterDef (name="Splits")int splits,
				@ParameterDef (name="Add stage")int add) {
					
			GeneralizedGenerator gen = new GeneralizedGenerator(log, splits, add);
			gen.generate(agh);
			System.out.println(gen.getNumberOfLinksInvolved());
			return null;
		}
	}

	@Override
	public int getNumberOfLinksInvolved() {
		if (splits > 0) throw new IllegalStateException("Cant use link numbering with splits");
		return numberOfClients*(getMaxNumberOfStages()-1);
	}
	

}
