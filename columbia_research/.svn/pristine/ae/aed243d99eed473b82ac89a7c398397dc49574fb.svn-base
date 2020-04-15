package edu.columbia.sebastien.link_util.models;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.random.PRNStream;
import edu.columbia.lrl.general.EventTarget;
import edu.columbia.lrl.general.Evt;
import edu.columbia.lrl.general.Message;
import edu.columbia.sebastien.link_util.LinkUtilisationExperiment;
import edu.columbia.sebastien.link_util.models.trafficmodel.AbstractMessageGenerator;

public class TrafficModel implements EventTarget {
	
	private double time;
	
	private int nbLinks;
	private int seed;
	private PRNStream stream;
	
	private int msgId;
	
	private AbstractBufferModel bufMod;
	private LinkUtilisationExperiment lue;
	private AbstractMessageGenerator msgGen;
	
	public TrafficModel(@ParamName(name="Links in the system", default_="2")  int nbLinks, 
                        @ParamName(name="Message generation") AbstractMessageGenerator msgGen,
									  @ParamName(name="Seed", default_="1") int seed,
									  @ParamName(name="Sim time in mls", default_="1000") double time) {
		this.nbLinks = nbLinks;
		this.msgGen = msgGen;

		this.seed = seed;
		this.stream = PRNStream.getDefaultStream(seed);
		this.time = time *1e6;
	}
	
	public Map<String, String> getAllParameters() {
		Map<String, String> m = msgGen.getAllParameters();
		m.put("nbLinks", nbLinks+"");
		m.put("seed", seed +"");
		m.put("sim time", time+"");
		return m;
	}

	public void init(AbstractBufferModel bufMod, LinkUtilisationExperiment lue) {
		this.bufMod = bufMod;
		this.lue = lue;
		this.msgGen.init();
		for (int i = 0 ; i < nbLinks ; i++) {
			double firstStati = msgGen.nextExpNS(stream);
			Evt e = new Evt(firstStati, null, this, i);
			lue.scheduleEvent(e);
		}
	}	

	public int getNumberOfLinks() {
		return nbLinks;
	}

	public void processEvent(Evt e) {
		int linkId = e.getType();
		double time = e.getTimeNS();
		Message m = new Message(msgId++, linkId, linkId, time, msgGen.getMessageBits());
		bufMod.receiveMessage(m, linkId, time);
		double interTime = msgGen.nextExpNS(stream);
		double newArrival = time + interTime;
		if (newArrival < this.time) {
			Evt ev = new Evt(newArrival, null, this, linkId);
			lue.scheduleEvent(ev);
		}
	}

	public Object toShortString() {
		return "traffic gen";
	}

	public double getSimeTimeNS() {
		return time;
	}

}
