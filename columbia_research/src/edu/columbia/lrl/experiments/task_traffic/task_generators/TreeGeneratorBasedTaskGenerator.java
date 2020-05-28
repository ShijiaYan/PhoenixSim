package edu.columbia.lrl.experiments.task_traffic.task_generators;

import java.awt.Color;
import java.util.List;
import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.graphics.ColorMap;
import ch.epfl.general_libraries.random.PRNStream;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.base.Javanco;
import ch.epfl.javanco.network.NodeContainer;
import ch.epfl.javancox.inputs.topology.TreeGenerator;
import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.experiments.task_traffic.IrregularTrafficApplication;
import edu.columbia.lrl.experiments.task_traffic.task_generators.tasks.RootTask;
import edu.columbia.lrl.experiments.task_traffic.task_generators.tasks.Task;

public class TreeGeneratorBasedTaskGenerator extends AbstractTaskGenerator {
	
	private TreeGenerator treeGen;
	
	protected AbstractTaskPropertiesGenerator taskPropGen;		

	private int taskCounter;
	
	public TreeGeneratorBasedTaskGenerator(@ParamName(name="A javanco tree generator") TreeGenerator treeGen, 
										   @ParamName(name="A task properties provider", defaultClass_=ConstantsTaskPropertiesGenerator.class) AbstractTaskPropertiesGenerator taskPropGen) {
		this.taskPropGen = taskPropGen;
		this.treeGen = treeGen;
	}	

	@Override
	protected RootTask getRootTask(IrregularTrafficApplication appl, double timeNS, PRNStream stream) {
		AbstractGraphHandler agh = Javanco.getDefaultGraphHandler(false);
		agh.newLayer("tree");
		treeGen.generateTree(agh, stream);
		boolean[] dones = new boolean[agh.getHighestNodeIndex()+1];
		RootTask rt = taskPropGen.getRootTask(appl, timeNS, stream);
		dones[0] = true;
		recursion(agh.getNodeContainer(0), rt, dones, stream, 1);
		rt.setDescriptions(taskCounter++);
		return rt;

	}
	
	@Override
	public Map<String, String> getAllParameters() {
		Map<String, String> m = super.getAllParameters();	
		m.putAll(treeGen.getAllParameters());
		return m;
	}	
	
	private int recursion(NodeContainer nc, Task father, boolean[] dones, PRNStream s, int index) {
		Color c = ColorMap.getDarkTonesDefaultMap().getColor(index);
		List<NodeContainer> connectedNodes = nc.getConnectedNodes();
		int conNodesSize = connectedNodes.size();
        for (NodeContainer neighbour : connectedNodes) {
            if (dones[neighbour.getIndex()] == false) {
                c = ColorMap.getLighterTone(c, conNodesSize, index);
                Task t = taskPropGen.getTask(s, c);
                father.add(t);
                dones[neighbour.getIndex()] = true;
                index = recursion(neighbour, t, dones, s, index + 1);
            }
        }
		return index+1;
	}

	@Override
	public double getMeanNumberOfTasksInCompoundTask() {
		return treeGen.getNumberOfNodes();
	}


	@Override
	public double getMeanCommunicationFootprintPerCompoundTask__Fc(IrregularTrafficApplication appl) {
		double ss = taskPropGen.getMeanRetrieveTaskBits() + taskPropGen.getMeanSchedulingTaskBits();
		double subTasksCom = appl.NODE_DONE_MESSAGE_SIZE + ss;
		double averageLeafs = treeGen.getAverageNumberOfLeaves();
		double averageDelegating = treeGen.getAverageHubNumber();
		
		double fc = subTasksCom*(averageLeafs-1);
		fc += appl.getInitialSchedulingBits() + appl.getFinalSchedulingBits();
		fc += averageDelegating * (appl.GRANTED_NODE_LIST_MESSAGE_SIZE + appl.NODE_ALLOCATION_MESSAGE_SIZE);
		return fc;
		
	}

	@Override
	public double getTaskSizesForA_Fc(IrregularTrafficApplication appl) {
		double averageLeafs = treeGen.getAverageNumberOfLeaves();
		double averageDelegating = treeGen.getAverageHubNumber();
		
		double Fcc = appl.getConfigurator().getFc();
		
		Fcc -= averageDelegating * (appl.GRANTED_NODE_LIST_MESSAGE_SIZE + 
				  appl.NODE_ALLOCATION_MESSAGE_SIZE);
		Fcc -= appl.getInitialSchedulingBits() + appl.getFinalSchedulingBits();
		double subTaskCom = Fcc / (averageLeafs -1);
		double ss = subTaskCom - appl.NODE_DONE_MESSAGE_SIZE;
		if (appl.useIntegerMessageSizes()) {
			return Math.max(2,(int)ss);
		} else {
			return ss;
		}
	}

	@Override
	public double getTaskFlopsForA_Fx(double Fx) {
		return Fx/getMeanNumberOfTasksInCompoundTask();
	}

	@Override
	public InitFeedback setMeanInitPlusAggrTimeNSInternal(double time) {
		return taskPropGen.setMeanInitPlusAggrTimeNS(time);
	}

	@Override
	public InitFeedback setMeanSchedPlusRetrieveInternal(double size) {
		// TODO Auto-generated method stub
		return taskPropGen.setMeanSchedPlusRetrieve(size);
	}
	
}
