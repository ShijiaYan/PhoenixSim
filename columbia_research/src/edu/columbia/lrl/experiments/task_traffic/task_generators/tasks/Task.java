package edu.columbia.lrl.experiments.task_traffic.task_generators.tasks;

import java.awt.Color;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;

import ch.epfl.general_libraries.simulation.Time;

import edu.columbia.lrl.LWSim.application.ActionManager;

public class Task extends ArrayList<Task> {
	
//	private final static ArrayList<DataReference> EMPTY_DATA_REFERENCE_LIST = new ArrayList<DataReference>(0);
	
	private static final long serialVersionUID = 1L;
	public Task parent;
	
	public String description;
	
	public int assignedWorkingNodeIndex = -1;
	
	public double initComputeIndex;
	public double aggregateComputeIndex;
	public double schedulingSize;
	public double resultSize;
	
	public int index;
	private Color color;
	
	public Task(double initComputeIndex, double aggregateComputeIndex, double schedulingSize, double resultSize, Color color) {
		this.initComputeIndex = initComputeIndex;
		this.aggregateComputeIndex = aggregateComputeIndex;
		this.schedulingSize = schedulingSize;
		this.resultSize = resultSize;
		this.color = color;
	}
	
	public void executeInit(Time ref, ActionManager c) {
		if (getColor() != null) {
			c.doSomeJob(ref, initComputeIndex, getDescription()+"i", getColor());		
		} else {
			c.doSomeJob(ref, initComputeIndex, getDescription()+"i");
		}		
	}
	
	public void executeAgg(Time ref, ActionManager c) {
		if (getColor() != null) {
			c.doSomeJob(ref, aggregateComputeIndex, getDescription()+"a", getColor());		
		} else {
			c.doSomeJob(ref, aggregateComputeIndex, getDescription()+"a");
		}		
	}	
	
	@Override
	public boolean add(Task t) {
		t.setParent(this);
		return super.add(t);
	}
	
	@Override
	public void add(int index, Task t) {
		t.setParent(this);
		super.add(index, t);
	}
	
	@Override
	public boolean addAll(Collection<? extends Task> col) {
		for (Task t : col) {
			t.setParent(this);
		}
		return super.addAll(col);
	}
	
	@Override
	public boolean addAll(int index, Collection<? extends Task> col) {
		for (Task t : col) {
			t.setParent(this);
		}
		return super.addAll(index, col);		
	}

	public void setDescriptions(String fromParent) {
		this.description = fromParent;
		for (int i = 0 ; i < this.size() ; i++) {
			Task t = this.get(i);
			t.setDescriptions(fromParent + "-" + i);
		}
	}
	
	public String getDescription() {
		return description;
	}
	
	/**
	 * If this task is allocated to some node, this is the amount of data that has to be transmitted to this node
	 * @return
	 */
	public double getSchedulingSize() {
		return schedulingSize;
	}
	
	/**
	 * When this task completes, this is the amount of data that is shifted back to initiator
	 * @return
	 */
	public double getResultSize() {
		return resultSize;
	}
	
	public Task getParent() {
		return parent;
	}
	
	public ArrayList<Task> getChildrens() {
		return this;
	}
	
	public void setParent(Task task) {
		this.parent = task;
	}
	
	public void addChildren(Task task) {
		add(task);
	}


	public ArrayDeque<Task> getSubTasksListCopy() {
		ArrayDeque<Task> list = new ArrayDeque<>(this);
		return list;
	}
	
	public String toString() {
		return "task with " + this.size() + " sub tasks" ;
	}

	public Color getColor() {
		return color;
	}

}
