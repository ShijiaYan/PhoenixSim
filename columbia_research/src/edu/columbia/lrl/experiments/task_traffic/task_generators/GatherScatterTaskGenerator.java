package edu.columbia.lrl.experiments.task_traffic.task_generators;

import java.awt.Color;
import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.graphics.ColorMap;
import ch.epfl.general_libraries.random.PRNStream;
import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.experiments.task_traffic.IrregularTrafficApplication;
import edu.columbia.lrl.experiments.task_traffic.task_generators.tasks.RootTask;
import edu.columbia.lrl.experiments.task_traffic.task_generators.tasks.Task;

public class GatherScatterTaskGenerator extends AbstractTaskGenerator {
	
	private double initFlops;
	private int subTasks;	
	
	private double subTaskTime;
	private double subTaskSize;
	
	private double Fc;
	private double Fx;
	
	public GatherScatterTaskGenerator(
			@ParamName(name="Flops (or time in ns if no compute power specified) for init and aggregation", default_="10000") double init,
			@ParamName(name="Sub-tasks", default_="20") int subs) {
		
		this.initFlops = init;
		this.subTasks = subs;
	}
	
	@Override
	public Map<String, String> getAllParameters() {
		Map<String, String> m = super.getAllParameters();
		m.put("Sub-tasks", subTasks+"");
		m.put("Flops of init", initFlops+"");	
		return m;
	}	

	@Override
	protected RootTask getRootTask(IrregularTrafficApplication appl,
			double timeNS, PRNStream stream) {
		RootTask rt = new RootTask(initFlops/2d, initFlops/2d, 10, 10, timeNS);
		rt.setDescriptions("Root");
		Color c = ColorMap.getRandomColor();
		for (int i = 0 ; i < subTasks ; i++) {
			Task t = new Task(subTaskTime/2d, 
					subTaskTime/2d, 
					(int)(subTaskSize/2d), 
					(int)(subTaskSize/2d), 
					ColorMap.getLighterTone(c,  subTasks, i));
			t.setDescriptions("sub " + i);
			rt.add(t);
		}
		return rt;
	}

	@Override
	public double getMeanFlopsPerCompoundTask_Fx() {
		return Fx;
	}

	@Override
	public double getMeanCommunicationFootprintPerCompoundTask__Fc(
			IrregularTrafficApplication appl) {
		return Fc;
	}

	@Override
	public double getMeanNumberOfTasksInCompoundTask() {
		return subTasks + 1;
	}

	@Override
	public double getTaskSizesForA_Fc(IrregularTrafficApplication appl) {
		this.Fc = appl.getConfigurator().getFc();	
		this.Fc -= 20;
		return this.Fc/subTasks;
	}

	@Override
	public double getTaskFlopsForA_Fx(double Fx) {
		this.Fx = Fx;
		if (Fx <= initFlops) throw new IllegalStateException("Misconfiguration: Fx must be greater than init flops of scatter task");
		return (Fx - initFlops)/subTasks;
	}

	@Override
	protected InitFeedback setMeanInitPlusAggrTimeNSInternal(double time) {
		subTaskTime = time;
		return null;
	}

	@Override
	protected InitFeedback setMeanSchedPlusRetrieveInternal(double size) {
		subTaskSize = size;
		return null;
	}

}
