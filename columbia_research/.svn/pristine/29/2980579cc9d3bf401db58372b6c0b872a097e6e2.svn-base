package edu.columbia.lrl.experiments.task_traffic.schedulers.central.execmod;

import java.util.Map;

import ch.epfl.general_libraries.simulation.Time;
import ch.epfl.general_libraries.utils.SimpleMap;
import edu.columbia.lrl.LWSim.application.ActionManager;
import edu.columbia.lrl.experiments.task_traffic.IrregularTrafficApplication;
import edu.columbia.lrl.experiments.task_traffic.analyzers.TaskExecutionAnalyser;
import edu.columbia.lrl.experiments.task_traffic.task_generators.tasks.Task;

public class SequentialExecutionModel extends AbstractTaskExecutionModel {

	@Override
	public void executeTask(IrregularTrafficApplication appl, Task rt,
			ActionManager c, Time ref, int rank,
			TaskExecutionAnalyser taskExecAnalyser) throws InterruptedException {
		rt.executeInit(ref, c);
		for (Task t : rt.getChildrens()) {
			executeTask(appl, t, c, ref, rank, taskExecAnalyser);
		}
		rt.executeAgg(ref, c);

	}

	@Override
	public Map<String, String> getAllParameters() {
		return SimpleMap.getMap();
	}

}
