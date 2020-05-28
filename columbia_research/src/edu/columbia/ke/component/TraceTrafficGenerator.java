package edu.columbia.ke.component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


import ch.epfl.general_libraries.clazzes.ParamName;
import edu.columbia.lrl.LWSim.AbstractTrafficGenerator;
import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.LWSim.LWSimComponent;
import edu.columbia.lrl.LWSim.TrafficDestination;
import edu.columbia.lrl.general.Evt;
import edu.columbia.lrl.general.Message;

public class TraceTrafficGenerator extends AbstractTrafficGenerator implements LWSimComponent {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private TrafficDestination[] dests;
	private int nNode;
	private BufferedReader br = null;
	private String prefix = "mpiSend";
	protected int messageIndex = 0;
	protected LWSIMExperiment lwSimExperiment;

	private String fileName;
	private String pathPrefix;
	private int minSize = 0;
	
	private boolean initialized = false;

	public TraceTrafficGenerator(
			@ParamName(name="Number of Nodes") int nNode, 
			@ParamName(name="Read Trace File", default_="") String fileName,
			@ParamName(name="Path", default_="") String pathPrefix,
			@ParamName(name="Min Size to Consider") int minSize) {
		this.nNode = nNode;
		this.fileName = fileName;
		this.pathPrefix = pathPrefix;
		this.minSize = minSize;
		dests = new TrafficDestination[nNode];
	}
	
	@Override
	public InitFeedback initComponent(LWSIMExperiment lwSimExperiment) {
		if (initialized)
			return null;
		
		this.lwSimExperiment = lwSimExperiment;		
		
		if(fileName.isEmpty())
			return null;
		
		String filePath = this.pathPrefix+fileName;
		
		try {
 			br = new BufferedReader(new FileReader(filePath)); 
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (br != null) {
			System.out.println("\t Reading from "+ filePath + "...");
			Evt traf = new Evt(0, this, this);
			lwSimExperiment.manager.queueEvent(traf);
		} else
			throw new IllegalStateException("file reader is null: " + filePath);
		
		initialized = true;
		return null;
	}
	
	private double parse(String s, ArrayList<Integer> intArr) {
		if (this.pathPrefix.toLowerCase().contains("UCL".toLowerCase())) {
			/*
			 * for UCL trace 3rd batch
			 */
			int count = 0;
			String stime = "";
			for (String str : s.split(" ")) {
				count++;
				if (count == 1) {
					stime = str;
				} else if (count == 4) {
					intArr.add(72);
					break;
				} else
					intArr.add(Integer.parseInt(str));
			}
			return 100* Double.parseDouble(stime);	// magnify send time by 100 times to avoid event queue overflow
		} else {
			/*
			 * for my own trace
			 */
			int count = 0;
			String stime = "";
			for (String str : s.split("\t")) {
				if (str.equals(prefix))
					continue;
				if (count == 3) {
					stime = str;
					break;
				}
				intArr.add(Integer.parseInt(str));
				count++;
			}
			return Double.parseDouble(stime);
		}
	}
	
	private double lastActualEmitTime = -1;
	private double lastReadSendTime = -1;
	private boolean traceFinished = false;
	private double ZERO_TIME = 1e-3;
	
	public void processEvent(Evt e) {
		if (traceFinished)
			return;
		String sCurrentLine;
		try {
			boolean msgSent = false;
			while ((sCurrentLine = br.readLine()) != null) {
				
				ArrayList<Integer> args = new ArrayList<>();
				double sendTime = parse(sCurrentLine, args);
				
				int src = args.get(0);
				int dst = args.get(1);
				int sizeByte = args.get(2) + 2;	//size in byte; overhead = 2 bytes
				
				if (sizeByte < this.minSize) continue;
				
				if (sendTime < lastReadSendTime) 
					throw new IllegalStateException("sendTime < lastReadSendTime");
				else if (sendTime == lastReadSendTime){
					sendTime = lastActualEmitTime + ZERO_TIME;
				} else {
					lastReadSendTime = sendTime;
				}
				
				lastActualEmitTime = sendTime;
				
				int size = sizeByte * 8;
				//System.out.println("\t"+ src + " -> " + dst + "\t" + sendTime);

				if (src >= nNode || dst >= nNode || src < 0 || dst <0 || size <= 0) {
					throw new IllegalStateException("Invalid trace entry: " + src + " -> " + dst + " size: " + size);
				}
				 
				// creating an event for the next packet
				Evt traf = new Evt(sendTime, this, this);
				lwSimExperiment.manager.queueEvent(traf);

				// Creating an event for the next element
				int dstInd = mapSrcDstToInd(src, dst);
				Evt next = new Evt(sendTime, this, dests[dstInd]);
				Message m = new Message(messageIndex++, src, dst, sendTime,
						size);

				next.setMessage(m);
				lwSimExperiment.packetEmitted(m);
				lwSimExperiment.manager.queueEvent(next);
				msgSent = true;
				break;
			}
			if (!msgSent) {
				br.close();
				traceFinished = true;
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	protected int mapSrcDstToInd(int src, int dst) {
		return src;
	}
	
	public void addDestination(TrafficDestination dest, int index) {
		dests[index]=dest;
	}
	
	public void addDestination(TrafficDestination dest, int src, int dst) {
		int index = mapSrcDstToInd(src, dst);
		dests[index]=dest;
	}

	@Override
	public void notifyEnd(double ref, double status) {
		// nothing to do
		
	}

	@Override
	public String toShortString() {
		return "Trace Traffic Generator";
	}

	@Override
	public int getAveragePacketSize() {
		throw new IllegalStateException(); 
	}

	@Override
	public AbstractTrafficGenerator getCopy(double loadCoeff, int index) {
		throw new IllegalStateException(); 
	}

	@Override
	public int getNumberOfClients() {
		return nNode;
	}
}
