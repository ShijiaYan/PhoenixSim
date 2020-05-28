package edu.columbia.lrl.CrossLayer.simulator.components;

import java.util.Map;
import java.util.TreeMap;

import ch.epfl.general_libraries.utils.PrettyPrinting;
import edu.columbia.lrl.general.Message;

public class RequestBuffer {
	Map<Integer, CircuitRequestBuffer<Message>> inputs;
	int current;

	public RequestBuffer(int numClients, int start) {

		inputs = new TreeMap<>();

		for (int i = 0; i < numClients; i++) {
			inputs.put(i, new CircuitRequestBuffer<>(1000));
		}

		current = start;
	}

	public void enqueue(Message msg) {
		inputs.get(msg.origin).enqueue(msg);
	}

	public Message dequeue(int input) {
		return inputs.get(input).dequeue();
	}

	public boolean empty(int input) {
		return inputs.get(input).getDepth() == 0;
	}

	public boolean empty() {
		boolean empty = true;
		for (Map.Entry<Integer, CircuitRequestBuffer<Message>> entry : inputs.entrySet()) {
			empty = empty && empty(entry.getKey());
		}
		return empty;
	}

	public int getCurrentInput() {
		return current;
	}

	public void increment() {
		current = (current + 1) % inputs.size();
	}

	public void decrement() {
		current--;
		if (current < 0)
			current = inputs.size() - 1;
	}

	public void print() {
		System.out
				.println(new PrettyPrinting().new PrettyPrintingMap<>(
						inputs));
	}

}
