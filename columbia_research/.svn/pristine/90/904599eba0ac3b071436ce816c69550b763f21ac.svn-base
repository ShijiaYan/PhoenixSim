package edu.columbia.sebastien.link_util.models.buffermodel;

import java.awt.Color;
import java.util.Map;

import ch.epfl.general_libraries.graphics.timeline.TimeLine;
import ch.epfl.general_libraries.utils.MoreArrays;
import ch.epfl.general_libraries.utils.SimpleMap;
import ch.epfl.general_libraries.utils.StringFormatter;
import edu.columbia.lrl.general.Evt;
import edu.columbia.lrl.general.Message;
import edu.columbia.sebastien.link_util.LinkUtilisationExperiment;
import edu.columbia.sebastien.link_util.models.AbstractBufferModel;
import edu.columbia.sebastien.link_util.models.AbstractLinkDimensioningModel;
import edu.columbia.sebastien.link_util.models.AbstractPowerSharingModel;

public class MinimalBufferingModel extends AbstractBufferModel {
	
	private double[] nextIdle;
	private TimeLine[] timeLines;
	private boolean shutDown = false;
	
	private AbstractPowerSharingModel optPowMod;
	private LinkUtilisationExperiment lue;	
	
	
	public MinimalBufferingModel(AbstractLinkDimensioningModel linkDimMod) {
		super(linkDimMod);

	}
	
	@Override
	public Map<String, String> getImplParameters() {
		return SimpleMap.getMap();
	}	

	@Override
	public void build(AbstractPowerSharingModel optPowMod,
			LinkUtilisationExperiment linkUtilisationExperiment) {
		this.optPowMod = optPowMod;
		this.shutDown = false;
		this.lue = linkUtilisationExperiment;
		this.linkDimMod.build(lue);
		this.nextIdle = new double[lue.getNbLinks()];
		this.timeLines = new TimeLine[lue.getNbLinks()];
		for (int i = 0 ; i < timeLines.length ; i++) {
			timeLines[i] = new TimeLine(i, "Buffers", "" + i);
		}
	}

	@Override
	public void receiveMessage(Message m, int linkId, double timeNS) {
		double transmissionTimeNS = linkDimMod.getTransmissionTimeNS(m.sizeInBits, linkId);
		double joules = linkDimMod.getJoules(transmissionTimeNS, linkId);
		
		if (optPowMod.isActive(linkId)) {
			// add actual message to the transmission time

			double start = Math.max(optPowMod.scheduleAfter(linkId, transmissionTimeNS), timeNS);			
			

			nextIdle[linkId] = start + transmissionTimeNS;
			lue.accountForLatency(start - timeNS, nextIdle[linkId] - timeNS, linkId);


		} else {
			double activeSince = optPowMod.activateChannel(linkId, timeNS, transmissionTimeNS);
			// check that activeSince is relative and not absolute
			nextIdle[linkId] = activeSince + transmissionTimeNS;
			// latency accounts no propagation time
			lue.accountForLatency(activeSince- timeNS, nextIdle[linkId] - timeNS, linkId);
		}
		Evt e = new Evt(nextIdle[linkId], null, this, linkId);
		lue.scheduleEvent(e);
		
		String linkString = "link " + StringFormatter.zeroPadding(linkId, nextIdle.length);
		lue.accountForLinkUtilisation(joules, transmissionTimeNS, m.sizeInBits, linkString);
		timeLines[linkId].addJobPhase(nextIdle[linkId] - transmissionTimeNS, nextIdle[linkId], "transmission", Color.BLUE);
	}
	
	@Override
	public void noMoreMessage() {
		if (shutDown == false) {
			double max = MoreArrays.max(nextIdle);
			optPowMod.shutDown(max);
			shutDown = true;
		}
	}	

	@Override
	public void processEvent(Evt e) {	
		int channel = e.getType();
		double time = e.getTimeNS();
		if (time == nextIdle[channel]) {
			optPowMod.disableChannel(channel, time);
		}
	}

	@Override
	public Object toShortString() {
		return "minimal buffering model";
	}

	@Override
	public TimeLine[] getTimeLines() {
		return timeLines;
	}

}
