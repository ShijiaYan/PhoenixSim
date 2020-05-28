package edu.columbia.lrl.LWSim.analysers;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.graphics.pcolor.PcolorGUI;
import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.general_libraries.results.Execution;
import ch.epfl.general_libraries.traffic.Rate;
import ch.epfl.general_libraries.utils.Matrix;
import ch.epfl.general_libraries.utils.MoreArrays;
import ch.epfl.general_libraries.utils.ObjectIndexedCounter;
import ch.epfl.general_libraries.utils.Pair;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.LWSim.TrafficDestination;
import edu.columbia.lrl.general.Message;


public class DetailedSourceDestAnalyser extends AbstractLWSimAnalyser {

	private int[][][] emittedDropReceivedRetranPerSrcDest;
	private long[][] emittedBits;
	private long[][] receivedBits;
	@SuppressWarnings("rawtypes")
	private ObjectIndexedCounter[][] whereDropsPerSrcDestCounters;
	@SuppressWarnings("rawtypes")
	private ObjectIndexedCounter dropPlacesCounters;

	private boolean perSrc;
	private boolean perSrcDest;
	private boolean withPColor;

	public DetailedSourceDestAnalyser(@ParamName(name = "Per source?") boolean src,
			@ParamName(name = "Per source-dest?") boolean perSrcDest, @ParamName(name = "Pcolor?") boolean withPColor) {
		this.perSrc = src;
		this.withPColor = withPColor;
		if (withPColor == true) perSrc = true;
		this.perSrcDest = perSrcDest;
	}

	@SuppressWarnings("rawtypes")
	public void init(LWSIMExperiment exp) {
		super.init(exp);
		if (perSrc || perSrcDest) {
			int clients = exp.getTopologyBuilder().getNumberOfClients();
			emittedDropReceivedRetranPerSrcDest = new int[clients][clients][4];
			whereDropsPerSrcDestCounters = new ObjectIndexedCounter[clients][clients];
			emittedBits = new long[clients][clients];
			receivedBits = new long[clients][clients];
			for (int i = 0; i < clients; i++) {
				for (int j = 0; j < clients; j++) {
					whereDropsPerSrcDestCounters[i][j] = new ObjectIndexedCounter();
				}
			}
		}
		dropPlacesCounters = new ObjectIndexedCounter();
	}

	@Override
	public void packetTransmitted(Message m) {
		// TODO Auto-generated method stub
	}

	@Override
	public void packetEmitted(Message m) {
		if (perSrc || perSrcDest) {
			emittedBits[m.origin][m.dest] += m.sizeInBits;
			emittedDropReceivedRetranPerSrcDest[m.origin][m.dest][0]++;
		}
	}

	@Override
	public void notifyEnd(double clock, int status) {
		// TODO Auto-generated method stub
	}

	public void packetQuenched(Message m, String where) {
	}

	@SuppressWarnings("unchecked")
	public void packetContented(Message m, String where, TrafficDestination swi, int type) {
		if (perSrc || perSrcDest) {
			whereDropsPerSrcDestCounters[m.origin][m.dest].increment(where);
			emittedDropReceivedRetranPerSrcDest[m.origin][m.dest][1]++;
		}
		dropPlacesCounters.increment(where);
	}

	@Override
	public void packetReceived(Message m, int origin, int dest, double timeEmitted, double timeReceived) {
		if (perSrc || perSrcDest) {
			receivedBits[origin][dest] += m.sizeInBits;
			emittedDropReceivedRetranPerSrcDest[origin][dest][2]++;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addInfo(DataPoint basic, DataPoint globals, Execution e, double simTime) {
		for (Pair<String, Integer> p : (Iterable<Pair<String, Integer>>) dropPlacesCounters) {
			DataPoint dropsPerPlace = lwSimExp.getDerivedDatapoint();
			dropsPerPlace.addProperty("Drop place", p.getFirst());
			dropsPerPlace.addResultProperty("Packet drop, per place", p.getSecond());
			e.addDataPoint(dropsPerPlace);
		}
		if (perSrc || perSrcDest) {
			int clients = lwSimExp.getTopologyBuilder().getNumberOfClients();
			for (int i = 0; i < clients; i++) {
				int emittedAtNode = 0;
				for (int j = 0; j < clients; j++) {
					emittedAtNode += emittedDropReceivedRetranPerSrcDest[i][j][0];
				}
				DataPoint perSource = lwSimExp.getSourceDataPoint(i);
				perSource.addResultProperty("Per source emissions", emittedAtNode);
				long totalBits = MoreArrays.sum(emittedBits[i]);
				perSource.addResultProperty("Per source emitted bits", totalBits);
				Rate em = new Rate(totalBits, simTime / 1000d);
				perSource.addResultProperty("Per source emitted throughput (gbit/s)", em.getInGbitSeconds());
				if (perSrc) e.addDataPoint(perSource);
				if (perSrcDest) {
					for (int j = 0; j < clients; j++) {
						DataPoint perSrcDest = lwSimExp.getSourceDestDataPoint(i, j);
						for (Pair<String, Integer> p : (Iterable<Pair<String, Integer>>) whereDropsPerSrcDestCounters[i][j]) {
							DataPoint dropsPerPlace = perSrcDest.getDerivedDataPoint();
							dropsPerPlace.addProperty("Drop place", p.getFirst());
							dropsPerPlace.addResultProperty("Packet drop, per place, per src-dest", p.getSecond());
							e.addDataPoint(dropsPerPlace);
						}
						DataPoint perRoute = perSrcDest.getDerivedDataPoint();
						perRoute.addResultProperty("per_route_emitted_bits", emittedBits[i][j]);
						perRoute.addResultProperty("per_route_received_bits", receivedBits[i][j]);
						Rate sent = new Rate(emittedBits[i][j], simTime / 1000d);
						perRoute.addResultProperty("per_route_emitted_throughput (gbit/s)", sent.getInGbitSeconds());
						perRoute.addResultProperty("per_route_emissions", emittedDropReceivedRetranPerSrcDest[i][j][0]);
						perRoute.addResultProperty("per_route_drops", emittedDropReceivedRetranPerSrcDest[i][j][1]);
						perRoute.addResultProperty("per_route_receptions",
								emittedDropReceivedRetranPerSrcDest[i][j][2]);
						perRoute.addResultProperty("per_route_retransmissions",
								emittedDropReceivedRetranPerSrcDest[i][j][3]);
						e.addDataPoint(perRoute);
					}
				}
			}
		}
		if (withPColor) {
			double max = Matrix.max(emittedBits);

			double[][] d = new double[emittedBits.length][emittedBits.length];
			for (int i = 0; i < d.length; i++) {
				for (int j = 0; j < d.length; j++) {
					d[i][j] = (double) emittedBits[i][j] / max;
				}
			}
			PcolorGUI gui = new PcolorGUI(d);
			gui.showInFrame();
		}
	}

	@Override
	public void packetRetransmitted(Message m) {
		if (perSrc) emittedDropReceivedRetranPerSrcDest[m.origin][m.dest][3]++;

	}

	@Override
	public void timeElapsed(double time) {
		// TODO Auto-generated method stub

	}

}
