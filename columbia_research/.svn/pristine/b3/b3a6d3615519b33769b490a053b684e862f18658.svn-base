package edu.columbia.lrl.CrossLayer.simulator.components;

import java.util.ArrayDeque;
import ch.epfl.general_libraries.simulation.SimulationException;

public class CircuitRequestBuffer<T> {

	private ArrayDeque<T> queue;
	private int capacity; // total number of items allowed

	public CircuitRequestBuffer(int capacity) {
		this.capacity = capacity;
		
		queue = new ArrayDeque<T>(capacity);
	}

	public void enqueue(T message) {

		if (queue.size() >= capacity) {
			throw new SimulationException("Buffer overflow.");
		}

		queue.addLast(message);
	}

	public void pushFront(T message) {
		
		if (queue.size() >= capacity) {
			throw new SimulationException("Buffer overflow.");
		}
		
		queue.addFirst(message);
	}
	
	public T dequeue() {

		if (queue.size() < 0) {
			throw new SimulationException("Buffer depth < 0.");
		}

		return queue.pop();
	}
	
	public T peekFront() {
		if (queue.size() <= 0) {
			throw new SimulationException("Buffer depth <= 0 on peek.");
		}
		
		return queue.peekFirst();
	}

	public int getDepth() {
		return queue.size();
	}
	

	public boolean empty() {
		return queue.size() == 0;
	}
	
	public String toString() {
		String s = "";
		for( T t : queue ) {
			s += " " + t;
		}
		
		return s;
	}
}
