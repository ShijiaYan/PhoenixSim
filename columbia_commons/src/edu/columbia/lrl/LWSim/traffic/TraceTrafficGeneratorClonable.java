package edu.columbia.lrl.LWSim.traffic;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Map;


import java.util.TreeMap;

import ch.epfl.general_libraries.clazzes.ConstructorDef;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;
import edu.columbia.lrl.LWSim.AbstractTrafficGenerator;
import edu.columbia.lrl.LWSim.EventOrigin;
import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.LWSim.LWSimComponent;
import edu.columbia.lrl.LWSim.TrafficDestination;
import edu.columbia.lrl.LWSim.TrafficTransitPoint;
import edu.columbia.lrl.LWSim.traffic.traces.AbstractTraceLineParser;
import edu.columbia.lrl.LWSim.traffic.traces.TraceEvent;
import edu.columbia.lrl.general.Evt;
import edu.columbia.lrl.general.Message;

public class TraceTrafficGeneratorClonable extends AbstractTrafficGenerator implements LWSimComponent {
	
	private static final long serialVersionUID = 1L;
	
	public TrafficGenDelegate[] dests;
	private int nNode;
	private BufferedReader br = null;
	protected int messageIndex = 0;
	protected LWSIMExperiment lwSimExperiment;

	private String appName;
	private String pathPrefix;
	private int minSize = 0;
	
	private boolean initialized = false;
	
	private PrintWriter writer; 
	private PrintWriter dstWriter; 
	
	private double lastActualEmitTime = -1;
	private double lastReadSendTime = -1;
	private boolean traceFinished = false;
	private double ZERO_TIME = 1e-12;

	private int src2Track = -1;
	
	private AbstractTraceLineParser parser;

	private int byteOverhead;

	public TraceTrafficGeneratorClonable(
			@ParamName(name="Number of Nodes") int nNode, 
			@ParamName(name="Application", default_="") String appName,
			@ParamName(name="Path", default_="./data/traces/") String pathPrefix,
			@ParamName(name="Min Size to Consider", default_="0") int minSize,
			@ParamName(name="Overhead in Byte", default_="0") int byteOverhead,
			@ParamName(name="Trace Parser") AbstractTraceLineParser parser) {
		this.nNode = nNode;
		this.appName = appName;
		this.pathPrefix = pathPrefix;
		this.minSize = minSize;
		dests = new TrafficGenDelegate[nNode];
		this.index = -1;	// this means I am THE cockpit copy; I do nothing but spawn (getCopy)
		this.parser = parser;
		this.byteOverhead = byteOverhead;
		// System.out.println("Trace Traffic Gen Created ... "+ appName + "...");
	}
	
	@Override
	public InitFeedback initComponent(LWSIMExperiment lwSimExperiment) {
		if (this.index == -1 || initialized)
			return null;
		
		this.lwSimExperiment = lwSimExperiment;		
		
		if(appName.isEmpty())
			return null;
		
		String filePath = pathPrefix + appName + "_" + nNode;
		
		try {
 			br = new BufferedReader(new FileReader(filePath)); 
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (br != null) {
			System.out.println("\t Generator " + index + " Reading from "+ filePath + "...");
			Evt traf = new Evt(0, this, this);
			lwSimExperiment.manager.queueEvent(traf);
		} else
			throw new IllegalStateException("file reader is null: " + filePath);
		
		dests[0].initComponent(lwSimExperiment);
		
		initialized = true;
		return null;
	}
	
	/*private double parse(String s, ArrayList<Integer> intArr) {
		if (this.pathPrefix.toLowerCase().contains("UCL".toLowerCase())) {
			
			// for UCL trace 3rd batch
			
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
			return Double.parseDouble(stime);	// remember to magnify send time by 100 times to avoid event queue overflow
		} else {
			
			// for my own trace
			
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
	}*/

	
	public void processEvent(Evt e) {
		if (traceFinished)
			return;
		String sCurrentLine;
		try {
			boolean msgSent = false;
			while ((sCurrentLine = br.readLine()) != null) {
				TraceEvent ev = parser.parse(sCurrentLine);

				double sendTime = ev.getSendTime();
				
				ev.check(lastReadSendTime, nNode);

				if (sendTime == lastReadSendTime){
					sendTime = lastActualEmitTime + ZERO_TIME;
				} else {
					lastReadSendTime = sendTime;
				}
				
				lastActualEmitTime = sendTime;
				
				int src = ev.getSource();
				int dst = ev.getDest();
				int sizeByte = ev.getByte();
				
				// size filter
				if (sizeByte < this.minSize) continue;
				int sizeBit = (sizeByte + byteOverhead) * 8;
				
				//System.out.println("Scheduled: \t"+ src + "\t -> " + dst + "\t" + sendTime);

				// Creating an event for the next dest
				int delegateIndex = mapSrcDstToInd(src, dst);
				Message m = new Message(messageIndex++, src, dst, sendTime,
						sizeBit);
				dests[delegateIndex].delegateSend ( sendTime,  m );
				msgSent = true;
				
				// log
				if ( src == src2Track ) {
					//logRankSend(m.index + ": \t" + src + " --> " + dst + "\t" + sizeByte + "\t" + sendTime);
					logDstList(dst);
				}
				
				// creating an event for the next packet
				Evt traf = new Evt(sendTime, this, this);
				lwSimExperiment.manager.queueEvent(traf);
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
	
	/*
	 * (non-Javadoc)
	 * I shouldn't have a traffic dest; I will give it to my delegate 0 (because I have index 0)
	 */
	@Override
	public void setTrafficDestination(TrafficDestination nextDest) {
		dests[0].setTrafficDestination(nextDest);
	}

	protected int mapSrcDstToInd(int src, int dst) {
		return src; 
	}

	@Override
	public void notifyEnd(double ref, double status) {
		if (br != null) {
			try {
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (writer != null)
			writer.close();
		
		if (dstWriter != null)
			dstWriter.close();
	}

	@Override
	public String toShortString() {
		return "Trace Traffic Generator Clonable";
	}

	@Override
	public int getAveragePacketSize() {
		throw new IllegalStateException(); 
	}
	
	public TraceTrafficGeneratorClonable genRegistered;

	@Override
	public AbstractTrafficGenerator getCopy(double loadCoeff, int index) {
		if (index == 0) {
			genRegistered = new TraceTrafficGeneratorClonable(this.nNode, this.appName, this.pathPrefix, this.minSize, this.byteOverhead, this.parser);
			genRegistered.index = 0;
		}
		TrafficGenDelegate d = new TrafficGenDelegate(index); 
		genRegistered.dests[index] = d;
		if (index == 0)
			return genRegistered;
		else
			return d;
	}

	@Override
	public int getNumberOfClients() {
		return nNode;
	}
	
	@Override
	public Map<String, String> getAllParameters(LWSIMExperiment lwSimExp) {
		Map<String, String> m = SimpleMap.getMap();
		m.put("Application",  appName);
		m.put("Size Threshold (B)",  minSize + "");
		return m;
	}
	
	protected void logRankSend(String s){
		if (writer == null) {
			try {
				String validAppName = appName.replace('/', '-');
				validAppName = validAppName.replace('\\', '-');
				writer = new PrintWriter("./log/" + validAppName + "_"
						+ this.getNumberOfClients() + "_size-" + minSize + "_rank-" + src2Track + ".send", "UTF-8");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		writer.println(s);
	}
	
	protected void logDstList(int dst){
		if (dst == src2Track)
			return;
		
		if (dstWriter == null) {
			try {
				String validAppName = appName.replace('/', '-');
				validAppName = validAppName.replace('\\', '-');
				dstWriter = new PrintWriter("./log/" + validAppName + "_"
						+ this.getNumberOfClients() + "_size-" + minSize + "_rank-" + src2Track + ".dest", "UTF-8");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		dstWriter.print(dst + "\t");
	}
	
	public static class TrafficGenDelegate extends AbstractTrafficGenerator implements TrafficDestination {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@ConstructorDef(ignore=true)
		public TrafficGenDelegate(int index) {
			this.index = index;
		}
		
		@Override 
		public InitFeedback initComponent(LWSIMExperiment lwSimExperiment) {
			this.lwSimExperiment = lwSimExperiment;
			return null;
		}

		@Override
		public void notifyEnd(double ref, double status) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public String toShortString() {
			// TODO Auto-generated method stub
			return "TrafficGenDelegate";
		}

		@Override
		public void processEvent(Evt e) {
			Evt next = new Evt(e.getTimeNS(), this, nextDest, e);
			lwSimExperiment.manager.queueEvent(next);
		}
		
		public void delegateSend (double sendTime, Message m) {
			Evt next = new Evt(sendTime, this, nextDest, 0);
			next.setMessage(m);
			lwSimExperiment.packetEmitted(m);
			lwSimExperiment.manager.queueEvent(next);
		}

		@Override
		public int getAveragePacketSize() {
			throw new IllegalStateException();		// i don't know anything
		}

		@Override
		public AbstractTrafficGenerator getCopy(double loadCoeff, int index) {
			throw new IllegalStateException("Do not support copy for now.");		// i don't know anything
		}

		@Override
		public int getNumberOfClients() {
			throw new IllegalStateException();		// i don't know anything
		}

	}	
	
	public static class TrafficSplitter implements TrafficTransitPoint {
		
		TreeMap<Integer, TrafficDestination> destMap = new TreeMap<>();
		private LWSIMExperiment lwSimExperiment;
		
		public void addDestination(TrafficDestination dest, int dstId) {
			destMap.put(dstId, dest);
		}

		@Override
		public InitFeedback initComponent(LWSIMExperiment lwSimExperiment) {
			this.lwSimExperiment = lwSimExperiment;
			return null;
		}

		@Override
		public void notifyEnd(double ref, double status) {
			// TODO Auto-generated method stub

		}

		@Override
		public String toShortString() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void processEvent(Evt e) {
			int msgDest = e.getMessage().dest;
			double now = e.getTimeNS();
			Evt next = new Evt(now, this, destMap.get(msgDest), 0, e);		//type = 0, as a producer event for next dest (VOQ) 
			lwSimExperiment.manager.queueEvent(next);
		}

		@Override
		public void setTrafficDestination(TrafficDestination sw) {
			// TODO Auto-generated method stub

		}

		@Override
		public void setTrafficOrigin(EventOrigin origin) {
			// TODO Auto-generated method stub

		}

	}	
	
}
