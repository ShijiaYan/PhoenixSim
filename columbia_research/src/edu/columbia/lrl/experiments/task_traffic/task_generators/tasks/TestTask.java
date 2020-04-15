package edu.columbia.lrl.experiments.task_traffic.task_generators.tasks;

import java.awt.Color;

import edu.columbia.lrl.experiments.task_traffic.IrregularTrafficApplication;

public class TestTask extends RootTask {
	
	private static final long serialVersionUID = 1L;

	public TestTask(double timeNS) {
		super(1000, 1000, 10, 10, timeNS);
		Task t1 = new Task(1000, 2000, 200, 100, Color.BLUE);
		Task t2 = new Task(1000, 3000, 100, 100, Color.GREEN);
		Task t1a = new Task(1000, 1000, 200, 100, Color.PINK);
		Task t1b = new Task(1000, 1000, 100, 200, Color.CYAN);
		t1.add(t1a);
		t1.add(t1b);
		add(t1);
		add(t2);
	}

	public static double getNumberOfSubTasks() {
		return 5;
	}

	
	public static double getCommunicationFootprint(IrregularTrafficApplication appl) {
		// TODO Auto-generated method stub
		return 600 + 20 + 20 + (appl.GRANTED_NODE_LIST_MESSAGE_SIZE + 
						  appl.NODE_ALLOCATION_MESSAGE_SIZE)*2;
	}
	
	
}


