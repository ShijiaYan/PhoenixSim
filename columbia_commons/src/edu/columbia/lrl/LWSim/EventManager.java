package edu.columbia.lrl.LWSim;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.PriorityQueue;

import edu.columbia.lrl.general.Evt;
import edu.columbia.lrl.general.SimulationExperiment;

import ch.epfl.general_libraries.simulation.SimulationException;

public class EventManager {
	
	public static FileWriter fw;
	public static boolean logEvent = false;
	
	static {
		if (logEvent) {
			try {
				fw = new FileWriter("event.txt");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	

	
	public int checkPointSeparation = 1000000;
	public int nextCheckPoint = 1000000;
	private double timeSoFar = 0;	
	private SimulationExperiment exp;
	private PriorityQueue<Evt> queue;
	private boolean terminate = false;
	
	private double lastWallTimeMs;
	private double peakMemUse;
	private double peakMem;
	private int evtCounter;
	
	public EventManager(SimulationExperiment exp) {
		this.exp = exp;
		this.queue = new PriorityQueue<>();
	}
	
	public void queueEvent(Evt e) {
		queue.add(e);
		if (queue.size() % 200000 == 0 || evtCounter % 10000 == 0) {
			MemoryUsage usage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
			double memUsage = (double)usage.getUsed() / (double)usage.getMax();
			peakMemUse = Math.max(peakMemUse, memUsage);
			peakMem = usage.getUsed();
			if (memUsage > 0.80) {
				System.gc();
				usage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
				memUsage = (double)usage.getUsed() / (double)usage.getMax();
				if (memUsage > 0.8) {
					System.out.println("Simulator has " + (double)queue.size() / 1000000d + " millions of events in its queue and memory usage (rougly 1GB per million) become critical. Aborting");
					terminate = true;
				}
			}
			
	/*		System.out.println("Before gc " + ( ));
			ManagementFactory.getMemoryMXBean().gc();
			System.out.println("After gc " + ( (double)usage.getUsed() / (double)usage.getMax()));			
			System.out.println("Events in queue in millions : " + ((double)queue.size() / 1000000d));*/
		}
		evtCounter++;
		
	}
	
	public void clearQueue() {
		queue.clear();
	}
	
	public Collection<Evt> getEventsRelatedTo(LWSimComponent dest) {
		ArrayList<Evt> list = new ArrayList<>();
		for (Evt e : queue) {
			if (e.getTarget().equals(dest)) {
				list.add(e);
			}
		}
		return list;
	}
	
	public int getQueueSize(){
		return queue.size();
	}
	
	public boolean approachingQueueLimit(){
		return queue.size() > 200000;
	}
	
	public double getClock() {
		return timeSoFar;
	}
	
	public double getLastWallClock() {
		return lastWallTimeMs;
	}
	
	public double getPeakMemoryUse() {
		return peakMemUse;
	}
	
	public double getPeakMemoryAbs() {
		return peakMem;
	}	
	
	/**
	 *  First element returned : sim length. Second : -1 : error, 0 : no more event, 1 end crit reach
	 * @return
	 */
	public int runSimulation() {
		long l = System.nanoTime();
		Evt e = null;
		int ret;		
		if (queue.size() > 0) {
			SimulationEndCriterium crit = exp.getEndCriterium();
			if (crit != null) {
				System.out.print("Starting sim (expect max " + crit.getNumberOfPoints() + " point) : ");
			} else {
				System.out.print("Starting sim :");
			}
			try {
				do {
					synchronized (this) {
						e = queue.poll();
						if (logEvent)
							fw.append(e.getTimeNS() + "\r\n");
					}
					timeSoFar = e.getTimeNS();
					if (timeSoFar > nextCheckPoint) {
						exp.timeElapsed(timeSoFar);
						nextCheckPoint += checkPointSeparation;
					}
				//	System.out.println(timeSoFar);
					e.execute();
					ret = exp.checkIfContinueSimulation(timeSoFar);
					if (ret > 0) {
						System.out.print(".");
					//	pointSoFar += pointValue;
					}
				}
				while (ret >= 0 && queue.size() > 0 && !terminate);
			}
			catch (IOException ex) {
				throw new IllegalStateException("Impossible to log event in file");
			}
			catch (NullPointerException ex) {
				throw new IllegalStateException("Is the constants and global object defined? Likely not",ex);				
			}
			catch (SimulationException ex) {
				System.out.println("Simulation exception:\n" + ex.getMessage());
				return -1;
			}
			finally {
				try {
					if (fw != null)
						fw.flush();
				}
				catch (IOException ex2) {
					throw new IllegalStateException(ex2);
				}
			}
			l = System.nanoTime() - l;
			lastWallTimeMs = l/1000000;
			int retu;
			if (ret < 0) {
				System.out.println("End criterium reached");
				retu = 1;
			}
			else {
				System.out.println("done, no more events");
				retu = 0;
			}
			return retu;
		}
		throw new IllegalStateException("No event in sim queue");
	//	return new double[]{0, 0};
	}

	public void tryToTerminate() {
		terminate = true;
		
	}


}
