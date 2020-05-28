package edu.columbia.lrl.LWSim.analysers;

import java.util.Iterator;

import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.general_libraries.results.Execution;
import ch.epfl.general_libraries.statistics.StatisticalDistribution;
import ch.epfl.general_libraries.utils.Pair;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.LWSim.TrafficDestination;
import edu.columbia.lrl.general.Message;

public class LatencyDistributionAnalyser extends AbstractLWSimAnalyser {
	
	boolean perRoute;
	
	protected StatisticalDistribution.DoubleDistribution[][] perRouteLatencies;
	private StatisticalDistribution<Double> latencies;
	
	public LatencyDistributionAnalyser(boolean perRoute) {
		this.perRoute = perRoute;
	}
	
	public void init(LWSIMExperiment exp) {
		super.init(exp);
		latencies = new StatisticalDistribution<>(10000);
		if (perRoute) {
			int clients = exp.getTopologyBuilder().getNumberOfClients();
			perRouteLatencies = new StatisticalDistribution.DoubleDistribution[clients][clients];
			for (int i = 0 ; i < clients ; i++) {
				for (int j = 0 ; j < clients ; j++) {	
					perRouteLatencies[i][j] = new StatisticalDistribution.DoubleDistribution();
				}
			}		
		}		
	}

	@Override
	public void packetTransmitted(Message m) {
		// TODO Auto-generated method stub
	}

	@Override
	public void packetEmitted(Message m) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void notifyEnd(double clock, int status) {
		// TODO Auto-generated method stub
	}

	@Override
	public void packetReceived(Message m, int origin, int dest, double timeEmitted, double timeReceived) {
		latencies.add(timeReceived - timeEmitted);
		if (perRoute) {
			perRouteLatencies[origin][dest].add(timeReceived - timeEmitted);
		}		
	}

	@Override
	public void addInfo(DataPoint basic, DataPoint globals, Execution e, double simTime) {
		if (perRoute) {
			for (int i = 0 ; i < perRouteLatencies.length ; i++) {
				for (int j = 0 ; j < perRouteLatencies.length ; j++) {
					DataPoint latency = lwSimExp.getSourceDestDataPoint(i, j).getDerivedDataPoint();
					latency.addResultProperty("per_route_latency", perRouteLatencies[i][j].getMean());
					e.addDataPoint(latency);					
				}
			}
		}
		Iterator<Pair<Double, Double>> it = latencies.iterator(150, 20000);	
		for ( ; it.hasNext() ;) {
			Pair<Double, Double> p = it.next();	
			if (p.getSecond() > 0) {
				DataPoint lat = lwSimExp.getDerivedDatapoint();
				lat.addProperty("latency_of", p.getFirst()+"");
				lat.addResultProperty("relative_frequency", p.getSecond());
				e.addDataPoint(lat);
			}
		}		
	}

	@Override
	public void packetQuenched(Message m, String where) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void packetContented(Message m, String where, TrafficDestination swi, int type) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void packetRetransmitted(Message m) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void timeElapsed(double time) {
		// TODO Auto-generated method stub
		
	}	

}
