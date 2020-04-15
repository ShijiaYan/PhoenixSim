package edu.columbia.lrl.LWSim.traffic;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import edu.columbia.lrl.LWSim.AbstractTrafficGenerator;
import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.LWSim.TrafficDestination;
import edu.columbia.lrl.LWSim.traffic.traces.AbstractTraceLineParser;
import edu.columbia.lrl.LWSim.traffic.traces.TraceEvent;
import edu.columbia.lrl.general.Evt;
import edu.columbia.lrl.general.Message;

public class TraceBasedTrafficGenerator extends AbstractTrafficGenerator {
	
	private static final long serialVersionUID = 1L;
	private String path;
	private String prefix;
	private int nodes;
	private AbstractTraceLineParser parser;
	
	private BufferedReader br = null;
	private TrafficDestination[] dests;
	
	private boolean traceFinished = false;
	private double lastReadSendTime;
	private int messageIndex = 0;

	public TraceBasedTrafficGenerator(String path, String prefix, int nodes, AbstractTraceLineParser parser) {
		this.path = path; 
		this.prefix = prefix;
		this.nodes = nodes;
		this.parser = parser;
	}
	
	@Override
	public Map<String, String> getAllParameters(LWSIMExperiment lwSimExp) {
		Map<String, String> m = super.getAllParameters(lwSimExp);
		m.put("Trace path", path);
		m.put("Trace file", prefix);
		return m;
	}	
	
	@Override
	public InitFeedback initTrafficGeneratorTemplateFirstPass(LWSIMExperiment lwSimExperiment) {
		super.initComponent(lwSimExperiment);
		traceFinished = false;
		lastReadSendTime = 0;
		messageIndex = 0;
		String filePath = this.path+prefix+"_"+nodes;
		
		try {
 			br = new BufferedReader(new FileReader(filePath)); 
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
		
		if (br != null) {
			System.out.println("\t Reading from "+ filePath + "...");
			Evt traf = new Evt(0, this, this);
			lwSimExperiment.manager.queueEvent(traf);
		} else
			throw new IllegalStateException("file reader is null!");
		dests = new TrafficDestination[nodes];
		return null;		
	}
	
	@Override
	public void processEvent(Evt e) {
		if (traceFinished)
			return;
		String sCurrentLine;
		try {
			if ((sCurrentLine = br.readLine()) != null) {
				TraceEvent ev = parser.parse(sCurrentLine);
				lastReadSendTime = ev.check(lastReadSendTime, nodes);

				int from = ev.getSource();
				int to = ev.getDest();
				int bits = ev.getBits();
				
				// creating an event for the next packet
				Evt traf = new Evt(lastReadSendTime, this, this);
				lwSimExperiment.manager.queueEvent(traf);

				// Creating an event for the next element
				Evt next = new Evt(lastReadSendTime, this, dests[to]);
				Message m = new Message(messageIndex++, from, to, lastReadSendTime, bits);

				next.setMessage(m);
				lwSimExperiment.packetEmitted(m);
				lwSimExperiment.manager.queueEvent(next);
			} else {
				br.close();
				traceFinished = true;
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}	
	
	@Override
	public AbstractTrafficGenerator getCopy(double loadCoeff, final int index_) {
		if (index_ >= nodes) {
			throw new IllegalStateException("Trace has " + nodes + " nodes, but simulation wants more clients");
		}
		return new AbstractTrafficGenerator() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			@Override
			public void setTrafficDestination(TrafficDestination nextDest) {
				dests[index_] = nextDest;
			}		
			@Override
			public void notifyEnd(double ref, double status) {}
			@Override
			public String toShortString() { return "fake gen " + index_; }
			@Override
			public void processEvent(Evt e) { throw new IllegalStateException(); }
			@Override
			public int getAveragePacketSize() { throw new IllegalStateException(); }
			@Override
			public AbstractTrafficGenerator getCopy(double loadCoeff, int index) {  throw new IllegalStateException();  }
			@Override
			public int getNumberOfClients() { 
				return nodes; 
			}	
		};
	}

	@Override
	public int getNumberOfClients() {
		return nodes;
	}	

	@Override
	public String toShortString() {
		return "trace based generator";
	}

	/*
	 * Do nothing, trace generator is not affected by sim end (non-Javadoc)
	 * @see edu.columbia.lrl.LWSim.LWSimComponent#notifyEnd(double, double)
	 */
	@Override
	public void notifyEnd(double ref, double status) {}	

	@Override
	public int getAveragePacketSize() {
		 throw new IllegalStateException(); 
	}

}
