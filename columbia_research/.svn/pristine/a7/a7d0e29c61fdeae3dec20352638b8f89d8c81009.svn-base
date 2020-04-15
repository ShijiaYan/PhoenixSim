package edu.columbia.lrl.CrossLayer.simulator.components;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import ch.epfl.general_libraries.simulation.SimulationException;
import ch.epfl.general_libraries.utils.SimpleMap;

import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.general.EventTarget;
import edu.columbia.lrl.general.Evt;
import edu.columbia.lrl.general.Message;
import ch.epfl.general_libraries.clazzes.*;
import ch.epfl.general_libraries.graphics.timeline.TimeLine;

public class RandomInputArbiter extends Arbiter {

	LWSIMExperiment experiment;

	class RequestBuffer {
		Map<Integer, CircuitRequestBuffer<Message>> inputs;
		int current;

		public RequestBuffer(int numClients, int start) {

			inputs = new TreeMap<Integer, CircuitRequestBuffer<Message>>();

			for (int i = 0; i < numClients; i++) {
				inputs.put(i, new CircuitRequestBuffer<Message>(1000));
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
			return (inputs.get(input).getDepth() == 0);
		}
	}

	Map<Integer, RequestBuffer> requestBuffers;
	Map<Integer, EventTarget> inputMap;
	Map<Integer, Double> outputHorizon;
	Map<Integer, Double> inputHorizon;

	double switchTime; // TODO: ehhhhhhhhh think about it

	ArrayList<TimeLine> timelines;
	boolean useTimeline;

	public RandomInputArbiter(
			@ParamName(name = "Switch time (ns)", default_ = "0") double switchTime,
			@ParamName(name = "Add arbiter grants to timeline", default_ = "false") boolean useTimeline) {
		super(null);
		this.switchTime = switchTime;
		this.useTimeline = useTimeline;

		requestBuffers = new TreeMap<Integer, RequestBuffer>();
		inputMap = new TreeMap<Integer, EventTarget>();

		outputHorizon = new TreeMap<Integer, Double>();
		inputHorizon = new TreeMap<Integer, Double>();

		if (useTimeline)
			timelines = new ArrayList<TimeLine>();
	}
	
	public Transmitter getTransmitter(int index, Collection<Integer> destinations) {
		return new DefaultTransmitter(destinations, this, index);
	}	

	@Override
	public Map<String, String> getArbiterParameters() {
		return SimpleMap.getMap("Switch time (ns)", switchTime + "", "Add arbiter grants to timeline", useTimeline+"");
	}

	@Override
	public InitFeedback initComponent(LWSIMExperiment lwSimExperiment) {
		experiment = lwSimExperiment;

		numClients = lwSimExperiment.getNumberOfClients();

		if (useTimeline) {
			int index = 0;
			for (int i = 0; i < numClients; i++) {
				for (int j = 0; j < numClients; j++) {
					TimeLine t = new TimeLine(index, "Out(" + i + "),In(" + j + ")");
					timelines.add(t);
					index++;

					// initialize with phony send/receive
					t.addSend(0, index, 1);
					t.addReceive(1, index);
				}
			}

			for (TimeLine tl : timelines) {
				experiment.addTimeLine(tl);
			}
		}

		return null;
	}

	@Override
	public void notifyEnd(double ref, double status) {
		// TODO Auto-generated method stub
	}

	public void registerInput(EventTarget input, int index) {
		if (inputMap.containsKey(index)) {
			throw new SimulationException("Input index " + index + " already registered in arbiter");
		}

		inputMap.put(index, input);
		inputHorizon.put(index, 0.0);
	}

	public void registerOutput(EventTarget output, int index) {
		if (requestBuffers.containsKey(index)) {
			throw new SimulationException("Output index " + index + " already registered in arbiter");
		}

		// Create a new request buffer for each output ("infinite" capacity)
		requestBuffers.put(index, new RequestBuffer(numClients, index));
		outputHorizon.put(index, 0.0);
	}

	@Override
	public String toShortString() {
		return "RandomInputArbiter";
	}

	public String toString() {
		return toShortString();
	}

	private void tryToGrant() {

		// For each receiver
		for (Map.Entry<Integer, RequestBuffer> requestBufferEntry : requestBuffers.entrySet()) {

			int requestBufferIndex = requestBufferEntry.getKey();
			RequestBuffer requestBuffer = requestBufferEntry.getValue();

			// Skip if this receiver is busy
			if (outputFree(requestBufferIndex)) {

				// Generate random grant order
				List<Integer> grantOrder = new ArrayList<Integer>();
				for (int i = 0; i < numClients; i++) {
					grantOrder.add(i);
				}
				Collections.shuffle(grantOrder); // Runs in O(# elements in
													// list)

				boolean granted = false;
				for (int i = 0; i < grantOrder.size() && !granted; i++) {
					// Get randomly selected input with request and try to grant
					int input = grantOrder.get(i);
					if (!requestBuffer.empty(input) && inputFree(input)) {

						// Grant input
						Message msg = requestBuffer.dequeue(input);
						double grantTime = experiment.getSimTimeNS();
						Message grantMessage = new Message();
						grantMessage.dest = requestBufferIndex;
						Evt grant = new Evt(grantTime, this, inputMap.get(msg.origin), MessageType.GRANT, grantMessage);
						experiment.manager.queueEvent(grant);

						// Update input/output horizons to keep track of when
						// they're busy
						double serializationTime = (msg.sizeInBits + 1) / experiment.getReferenceBandwidth().getInGbitSeconds();
						double newHorizon = Math.max(grantTime + serializationTime + switchTime, experiment.getSimTimeNS());
						outputHorizon.put(requestBufferIndex, newHorizon);
						inputHorizon.put(msg.origin, newHorizon);

						granted = true;

						// Schedule another grant for this receiver after
						// current one is done
						Evt grantDone = new Evt(newHorizon, this, this, MessageType.SELF);
						experiment.manager.queueEvent(grantDone);

						if( useTimeline ) {
							int timelineIndex = (requestBufferIndex * numClients) + input;
							timelines.get(timelineIndex).addJobPhase(grantTime, newHorizon, "Grant: Out(" + requestBufferIndex + "),In(" + input + ")",
									Color.GREEN);
						}
					}
				}
			}
		}
	}

	private boolean outputFree(int output) {
		return outputHorizon.get(output) <= experiment.getSimTimeNS();
	}

	private boolean inputFree(int input) {
		return inputHorizon.get(input) <= experiment.getSimTimeNS();
	}

	@Override
	public void processEvent(Evt e) {

		/*
		 * Pre: Message has sender ID in origin field Pre: Message has receiver
		 * ID in dest field Pre: Message has sizeInBits set
		 */
		if (e.getType() == MessageType.REQUEST) {
			// Queue request
			requestBuffers.get(e.getMessage().dest).enqueue(e.getMessage());

			if (outputFree(e.getMessage().dest))
				tryToGrant();
		} else if (e.getType() == MessageType.SELF) {
			tryToGrant();
		} else {
			throw new SimulationException("Default arbiter received unknown event type: " + e.getType());
		}
	}

	@Override
	public void clear() {
		requestBuffers.clear();
		inputMap.clear();
		outputHorizon.clear();
		inputHorizon.clear();
	}

}
