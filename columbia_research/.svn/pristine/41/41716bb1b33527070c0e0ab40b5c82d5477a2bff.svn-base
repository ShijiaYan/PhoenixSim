package edu.columbia.lrl.experiments.task_traffic.schedulers;

import edu.columbia.lrl.experiments.task_traffic.task_generators.tasks.Task;


public class ThreadInfo {
	
	public int stealings = 0;
	
	public Task task;
	public int origin;
	public ThreadInfo parent;
	
	public String desc;
	
	public boolean initialised = false;
	public int receivedSubResponses = 0;
	
	public ThreadInfo(Task t, int o, ThreadInfo parent, String desc) {
		this.task = t;
		this.origin = o;
		this.parent = parent;
		this.desc = desc;
	}
	
	public String toString() {
		return "TH: "+ desc + " - " + task.description;
	}

}
