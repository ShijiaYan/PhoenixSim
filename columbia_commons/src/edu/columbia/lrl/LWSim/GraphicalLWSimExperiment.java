package edu.columbia.lrl.LWSim;

import java.util.Iterator;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.gui.PlayStopDialog;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.network.LinkContainer;
import ch.epfl.javanco.network.NodeContainer;
import ch.epfl.javanco.ui.AbstractGraphicalUI;
import ch.epfl.javanco.ui.swing.multiframe.JavancoDefaultGUI;
import edu.columbia.lrl.LWSim.analysers.AbstractLWSimAnalyser;
import edu.columbia.lrl.LWSim.builders.AbstractTopologyBuilder;
import edu.columbia.lrl.LWSim.components.AbstractSwitch;
import edu.columbia.lrl.general.Evt;
import edu.columbia.lrl.general.Message;


public class GraphicalLWSimExperiment extends LWSIMExperiment implements LWSimComponent, EventOrigin {

	private PlayStopDialog diag;
	private AbstractGraphHandler agh;
	JavancoDefaultGUI javancoGUI;
	private double repaintEach = 10000.0D;
	private int[][] counters;

	public GraphicalLWSimExperiment(@ParamName(name = "Model builder") AbstractTopologyBuilder builder,
			@ParamName(name = "The default traffic generator") AbstractTrafficGenerator trafGenerator,
			@ParamName(name = "PRNG Seed") int seed,
			@ParamName(name = "Simulate until?") SimulationEndCriterium criterium, AbstractLWSimAnalyser[] analysers,
			@ParamName(name = "Popup time line?", default_ = "false") boolean withTimeLine) {
		super(builder, trafGenerator, seed, criterium, analysers, withTimeLine);
	}

	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis) {
		Thread.currentThread().setName("Simulation thread");
		this.javancoGUI = JavancoDefaultGUI.getAndShowDefaultGUI();
		this.diag = new PlayStopDialog(this.javancoGUI);
		super.run(man, (AbstractResultsDisplayer) null);
	}

	public String toShortString() {
		return "";
	}

	public InitFeedback initComponent(LWSIMExperiment lwSimExperiment) {
		return null;
	}

	public void processEvent(Evt e) {
		if (this.agh != null) { this.updateColors(); }

		if (this.diag.waitFor()) {
			this.manager.clearQueue();
		} else {
			Evt ev = new Evt(e.getTimeNS() + this.repaintEach, this, this);
			this.manager.queueEvent(ev);
		}

	}

	protected int launchSim(AbstractResultsManager man) {
		AbstractGraphHandler agh = this.javancoGUI.getCurrentlyActiveAgh();
		if (agh != null) {
			AbstractGraphicalUI ui = agh.getUIDelegate().getDefaultGraphicalUI(agh, true);
			ui.setBestFit(this.javancoGUI.getActuallyActiveInternalFrame().getSize());
			Evt e = new Evt(this.repaintEach, this, this);
			this.manager.queueEvent(e);
			boolean var5 = false;

			int endCondition;
			try {
				endCondition = this.manager.runSimulation();
			} catch (Exception var7) {
				throw new IllegalStateException(var7);
			}

			this.javancoGUI.getActuallyActiveInternalFrame().dispose();
			this.diag.setVisible(false);
			return endCondition;
		} else {
			return super.launchSim(man);
		}
	}

	public void packetDropped(Message m, String where, TrafficDestination swi, int type) {
		this.updateCounters((AbstractSwitch) swi, type);
		super.packetDropped(m, where, swi, type);
	}

	private void initCounters() {
		this.counters = new int[this.agh.getNumberOfNodes()][2];
	}

	private void updateCounters(AbstractSwitch swi, int type) {
		if (this.agh == null) { this.agh = swi.getNodeContainer().getAbstractGraphHandler(); this.initCounters(); }

		int var10002;
		if (type == -1 || type == -3) { var10002 = this.counters[swi.getIndex()][0]++; }

		if (type == -2 || type == -3) { var10002 = this.counters[swi.getIndex()][1]++; }

	}

	private void updateColors() {
		int max = 0;

		int i;
		for (i = 0; i < this.counters.length; ++i) {
			if (this.counters[i][0] > max) { max = this.counters[i][0]; }

			if (this.counters[i][1] > max) { max = this.counters[i][1]; }
		}

		for (i = 0; i < this.counters.length; ++i) {
			NodeContainer nc = this.agh.getNodeContainer(i);
			Iterator var5 = nc.getOutgoingLinks().iterator();

			while (var5.hasNext()) {
				LinkContainer lc = (LinkContainer) var5.next();
				int drop;
				if (lc.attribute("orig_port").getValue().equals("up")) {
					drop = (int) (255.0D * (double) this.counters[i][0] / (double) max);
					lc.attribute("link_color").setValue(drop + "," + (255 - drop) + ", 0");
				}

				if (lc.attribute("orig_port").getValue().equals("down")) {
					drop = (int) (255.0D * (double) this.counters[i][1] / (double) max);
					lc.attribute("link_color").setValue(drop + "," + (255 - drop) + ", 0");
				}
			}
		}

		for (i = 0; i < this.counters.length; ++i) {
			this.counters[i][0] = 0;
			this.counters[i][1] = 0;
		}

	}

	public void notifyEnd(double ref, double status) {
	}
}
