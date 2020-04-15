package edu.columbia.lrl.experiments.task_traffic.task_generators.tasks;

import java.awt.Color;


public class RootTask extends Task {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double occurenceTime;	
	private double scheduledTime;

	public double getScheduledTime() {
		return scheduledTime;
	}

	public void setTimeAtWhichExecutionStarted(double scheduledTime) {
		this.scheduledTime = scheduledTime;
	}

	public RootTask(double initComputeIndex, double aggregateComputeIndex, int schedulingSize, int resultSize, double horizon) {
		super(initComputeIndex, aggregateComputeIndex, schedulingSize, resultSize, Color.RED);
		occurenceTime = horizon;
	}
	
	public void setDescriptions(int index) {
		this.description = "R";
		for (int i = 0 ; i < this.size() ; i++) {
			Task t = this.get(i);
			t.setDescriptions(/*description + "-"*/ "" + i);
		}
	}
	
	public double getOccurenceTimeNS() {
		return occurenceTime;
	}	
	
	public String toString() {
		return super.toString() + "(appeared at: " + this.occurenceTime + ")";
	}
	
}
