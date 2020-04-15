package edu.columbia.lrl.experiments.task_traffic;

import java.util.Map;

import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.LWSim.application.AbstractApplication;
import edu.columbia.lrl.LWSim.application.ActionManager;
import edu.columbia.lrl.experiments.task_traffic.configurators.AbstractTaskGenerationConfigurator;
import edu.columbia.lrl.experiments.task_traffic.schedulers.AbstractScheduler;
import edu.columbia.lrl.experiments.task_traffic.task_generators.AbstractTaskGenerator;
import ch.epfl.general_libraries.clazzes.ConstructorDef;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.simulation.Time;
import ch.epfl.general_libraries.traffic.Rate;

public class IrregularTrafficApplication extends AbstractApplication {
	
	public  int GRANTED_NODE_LIST_MESSAGE_SIZE = 0;
	public  int NODE_ALLOCATION_MESSAGE_SIZE = 0;
	public  int NODE_DONE_MESSAGE_SIZE = 0;
	public  int GENERIC_CONTROL_MESSAGE_SIZE = 0;
	
	
	private AbstractScheduler scheduler;
	private AbstractTaskGenerator taskGenerator;
	private AbstractTaskGenerationConfigurator configurator;
	
	private int nbTasks;
	
	@ConstructorDef(def="Null control message size")	
	public IrregularTrafficApplication(AbstractScheduler scheduler,
			AbstractTaskGenerator taskGenerator,
			AbstractTaskGenerationConfigurator configurator, 
			@ParamName(name="nb tasks") int nbTasks) {
		this.scheduler = scheduler;
		this.taskGenerator = taskGenerator;
		this.configurator = configurator;
		this.nbTasks = nbTasks;
	}
	
	@ConstructorDef(def="Specify control message size")
	public IrregularTrafficApplication(AbstractScheduler scheduler,
			AbstractTaskGenerator taskGenerator,
			AbstractTaskGenerationConfigurator configurator, 
			@ParamName(name="nb tasks") int nbTasks,
			@ParamName(name="Control message size in bits", default_="320") int bits) {
		this.scheduler = scheduler;
		this.taskGenerator = taskGenerator;
		this.configurator = configurator;
		this.nbTasks = nbTasks;
		setControlMessageSizes(bits);
	}
	
	public double getTimeNSforFlops(double flops) {
		double P = configurator.getNodeComputePowerFlopsPerNS();
		if (P > 0) {
			return flops/P;
		} else {
			return flops;
		}
	}
	
	public double getFlopsForTimeNS(double timeNS) {
		double P = configurator.getNodeComputePowerFlopsPerNS();
		if (P > 0) {
			return P * timeNS;
		} else {
			return timeNS;
		}
	}	
	
	private void setControlMessageSizes(int bits) {
		GRANTED_NODE_LIST_MESSAGE_SIZE = bits;
		NODE_ALLOCATION_MESSAGE_SIZE = bits;
		NODE_DONE_MESSAGE_SIZE = bits;
		GENERIC_CONTROL_MESSAGE_SIZE = bits;
	}

	public AbstractTaskGenerationConfigurator getConfigurator() {
		return configurator;
	}
	
	public AbstractScheduler getScheduler() {
		return scheduler;
	}
	
	@Override
	public Map<String, String> getAllParameters() {
		Map<String, String> m = scheduler.getAllParameters();
		m.put("Scheduler type", scheduler.getClass().getSimpleName());
		m.put("Control messages size", GENERIC_CONTROL_MESSAGE_SIZE+"");
		m.putAll(configurator.getAllParameters());
		
		return m;
	}

	@Override
	public void runImpl(ActionManager c, int rank, Time ref)
			throws InterruptedException {
		scheduler.runImpl(c, rank, ref);
		
	}

	public InitFeedback init(LWSIMExperiment exp) {
		InitFeedback  failure = configurator.initAndsetNumberOfNotComputingNodes(this, exp, taskGenerator);	
		super.init(configurator.getN());
		if (failure != null) return failure;
		exp.setReferenceBandwidth(new Rate.GigabitPerS(configurator.getB()));
		scheduler.init(exp, this, nbTasks);	
		return null;
	}

	@Override
	public void addApplicationInfoImpl(LWSIMExperiment lwSimExp, double ref, boolean analyseNodes) {
		scheduler.addApplicationInfo(lwSimExp, ref, analyseNodes);
		
	}

	public int getInitialSchedulingBits() {
		return GENERIC_CONTROL_MESSAGE_SIZE;
	}

	public int getFinalSchedulingBits() {
		// TODO Auto-generated method stub
		return GENERIC_CONTROL_MESSAGE_SIZE;
	}

	public boolean useIntegerMessageSizes() {
		return false;
	}
}
