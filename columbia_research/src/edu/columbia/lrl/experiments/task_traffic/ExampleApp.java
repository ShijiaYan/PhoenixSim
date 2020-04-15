package edu.columbia.lrl.experiments.task_traffic;

import java.util.Map;

import ch.epfl.general_libraries.simulation.Time;
import ch.epfl.general_libraries.utils.SimpleMap;
import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.LWSim.application.AbstractApplication;
import edu.columbia.lrl.LWSim.application.ActionManager;

public class ExampleApp extends AbstractApplication {

	@Override
	public Map<String, String> getAllParameters() {
		return SimpleMap.getMap();
	}

	@Override
	public void runImpl(ActionManager c, int rank, Time timeRef) 
			throws InterruptedException {	
		
		int[] jobDurations = {1000, 1200, 820, 400};
		int[] packetSizes = {8000, 2000, 1200, 1700};
		
		c.doSomeJob(timeRef, jobDurations[rank], "first task");
		c.send(timeRef, packetSizes[rank], (rank + 1)% 4);
		c.blockingReadFromAny(timeRef);	
		c.doSomeJob(timeRef, 1000, "second task");
		if (rank == 0) {
			c.broadcastButMe(timeRef, null, 20000);
		}
		if (rank != 0) {
			c.blockingReadFromAny(timeRef);
		}

		this.executionEnd(0, timeRef);
	}

	@Override
	public InitFeedback init(LWSIMExperiment exp) {
		return super.init(exp.getNumberOfClients());
	}

	@Override
	public void addApplicationInfoImpl(LWSIMExperiment lwSimExp, double ref,
			boolean analyseNodes) {
		// TODO Auto-generated method stub

	}

}
