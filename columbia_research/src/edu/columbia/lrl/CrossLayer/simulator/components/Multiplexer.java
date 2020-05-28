package edu.columbia.lrl.CrossLayer.simulator.components;

import java.util.TreeMap;

import edu.columbia.lrl.LWSim.EventOrigin;
import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.LWSim.TrafficDestination;
import edu.columbia.lrl.general.Evt;
import edu.columbia.lrl.general.Message;

public class Multiplexer implements TrafficDestination, EventOrigin {

    private TreeMap<Integer, TrafficDestination> dests;
    private LWSIMExperiment experiment;
    private int index;

    public Multiplexer(int index) {
        dests = new TreeMap<>();
        this.index = index;
    }

    @Override
    public InitFeedback initComponent(LWSIMExperiment lwSimExperiment) {
        this.experiment = lwSimExperiment;
        return null;
    }

    @Override
    public void notifyEnd(double ref, double status) {
    }

    @Override
    public String toShortString() {
        return "Multiplexer " + index;
    }

    public void addDest(int id, TrafficDestination dest) {
        dests.put(id, dest);
    }

    @Override
    public void processEvent(Evt e) {

        //Message destination is its output port
        Message msg = e.getMessage();
        TrafficDestination dest = dests.get(msg.dest);
        experiment.manager.queueEvent(new Evt(e.getTimeNS(), this, dest, 0, msg));
    }

}
