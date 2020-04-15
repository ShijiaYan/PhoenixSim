package edu.columbia.lrl.CrossLayer.simulator.components;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import cern.colt.Arrays;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.graphics.ColorMap;
import ch.epfl.general_libraries.graphics.timeline.TimeLine;
import ch.epfl.general_libraries.simulation.SimulationException;
import ch.epfl.general_libraries.utils.SimpleMap;

import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.general.EventTarget;
import edu.columbia.lrl.general.Evt;
import edu.columbia.lrl.general.Message;

public class RoundRobinArbiter extends Arbiter {

	LWSIMExperiment experiment;

	Map<Integer, EventTarget> inputMap;

	ArrayList<TimeLine> timelines;
	boolean useTimeline;
	
	double switchTime;
	double grantLatency;
	
	class ArbiterBuffer extends CircuitRequestBuffer<Message> {
		public ArbiterBuffer() {
			super(1000);
		}		
	}
	
	ArbiterBuffer reqBuffers[][];
	int roundRobinPointer[];
	int outputOrder[];
	
	Map<Integer, Double> transitioningSwitches;

	public RoundRobinArbiter(
			@ParamName(name = "Reservation strategy", defaultClass_=NextPathReservation.class) AbstractReservationStrategy reservation,
			@ParamName(name = "Switching time (ns)", default_="0") double switchTime,
			@ParamName(name = "Grant message latency (ns)", default_="0") double grantLatency,
			@ParamName(name = "Add arbiter grants to timeline", default_ = "false") boolean useTimeline) {
		super(reservation);
		this.switchTime = switchTime;
		this.grantLatency = grantLatency;
		this.useTimeline = useTimeline;

		inputMap = new TreeMap<Integer, EventTarget>();

		transitioningSwitches = new TreeMap<Integer, Double>();
		
		if (useTimeline)
			timelines = new ArrayList<TimeLine>();
	}

	public Transmitter getTransmitter(int index, Collection<Integer> destinations) {
		return new DefaultTransmitter(destinations, this, index);
	}

	@Override
	public Map<String, String> getArbiterParameters() {
		return SimpleMap.getMap(
				"Switch time", switchTime+"",
				"Grant latency", grantLatency+""
				);
	}

	@Override
	public InitFeedback initComponent(LWSIMExperiment lwSimExperiment) {
		super.initComponent(lwSimExperiment);

		experiment = lwSimExperiment;

		numClients = lwSimExperiment.getNumberOfClients();
		
		reqBuffers = new ArbiterBuffer[numClients][numClients];
		outputOrder = new int[numClients];
		roundRobinPointer = new int[numClients];
		
		for( int i = 0; i < reqBuffers.length; i++ ) {
			outputOrder[i] = i;
			roundRobinPointer[i] = 0;
			for( int j = 0; j < reqBuffers[i].length; j++ ) {
				reqBuffers[i][j] = new ArbiterBuffer();
			}
		}

		if (useTimeline) {
			lwSimExperiment.withTimeLine = true;
			int index = 0;
			for (int i = 0; i < numClients; i++) {
				for (int j = 0; j < numClients; j++) {
					TimeLine t = new TimeLine(index, "In(" + j + "),Out(" + i + ")");
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
	}

	@Override
	public String toShortString() {
		return "RoundRobinArbiter";
	}

	public String toString() {
		return toShortString();
	}

	private void tryToGrant() {

		// Create random ordering of output buffers
		outputOrder = experiment.getRandomStreamForEverythingButTraffic().shuffle(outputOrder);

		for (int output : outputOrder) {
			//Find the next input buffer with a message and an available path
			int toGrant = -1;
			for( int i = 0; i < reqBuffers[output].length; i++ ) {
				int input = (i + roundRobinPointer[output]) % numClients;
				if( !reqBuffers[output][input].empty() &&
						reservation.pathAvailable(input, output)) {
					toGrant = input;
					break;
				}
			}
			
			//If such a buffer was found...
			if( toGrant != -1 ) {
				
				//Reserve paths and mark any switch transitions
				
				for( int trans : reservation.reservePath(toGrant, output) ) {
					transitioningSwitches.put(trans, experiment.getSimTimeNS() + switchTime);
				}
				//Determine grant time
				int[] path = reservation.getReservedSwitchPath(toGrant, output);
				double grantTime = experiment.getSimTimeNS();
				for( int sw : path ) {
					if( transitioningSwitches.containsKey(sw)) {
						grantTime = Math.max(grantTime, transitioningSwitches.get(sw));
					}
				}
				
				//Grant request
				Message msg = reqBuffers[output][toGrant].dequeue();
				Message grantMessage = new Message();
				grantMessage.dest = output;

				Evt grant = new Evt(grantTime, this, inputMap.get(msg.origin), MessageType.GRANT, grantMessage);
				experiment.manager.queueEvent(grant);
				
				//Schedule release of resources
				double serializationTime = experiment.getReferenceBandwidth().getTimeNS(msg.sizeInBits);
				double releaseTime = grantTime + serializationTime;
				Message releaseMessage = new Message();
				releaseMessage.origin = toGrant;
				releaseMessage.dest = output;
				
				Evt releaseEvt = new Evt(releaseTime, this, this, MessageType.SELF, releaseMessage);
				experiment.manager.queueEvent(releaseEvt);
				
				//Update round-robin point to one after buffer that was just granted
				roundRobinPointer[output] = (toGrant+1)%numClients;
				
				//visualize
				if (useTimeline) {
					int timelineIndex = (output * numClients) + toGrant;
					if( grantTime > experiment.getSimTimeNS() ) 
						timelines.get(timelineIndex).addJobPhase(experiment.getSimTimeNS(), grantTime, "Switching", Color.BLUE);
					timelines.get(timelineIndex).addJobPhase(grantTime, releaseTime, Arrays.toString(reservation.getReservedSwitchPath(toGrant, output)),
							ColorMap.getLighterTone(Color.GRAY, numClients - 1, output));
					
					//timelines.get(timelineIndex).addJobPhase(grantTime, releaseTime,
					//		"In(" + toGrant + ") Out(" + output + ")",
					//		ColorMap.getLighterTone(Color.GRAY, numClients - 1, output));
				}
			}
		}	
	}

	@Override
	public void processEvent(Evt e) {
		/*
		 * Pre: Message has sender ID in origin field Pre: Message has receiver
		 * ID in dest field Pre: Message has sizeInBits set
		 */
		if (e.getType() == MessageType.REQUEST) {
			// Queue request
			reqBuffers[e.getMessage().dest][e.getMessage().origin].enqueue(e.getMessage());
			// Schedule a grant to happen in the future
			tryToGrant();
		} else if (e.getType() == MessageType.SELF) {
			// Self messages indicate arbiter events that may allow more grants
			// to occur
			Message msg = e.getMessage();
			reservation.releasePath(msg.origin, msg.dest);

			tryToGrant();
		} else {
			throw new SimulationException("Default arbiter received unknown event type: " + e.getType());
		}
	}

	@Override
	public void clear() {
		inputMap.clear();
	}

}
