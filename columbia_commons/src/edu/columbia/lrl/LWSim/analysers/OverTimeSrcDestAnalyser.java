package edu.columbia.lrl.LWSim.analysers;

import java.util.ArrayList;
import java.util.HashMap;

import cern.colt.matrix.impl.SparseDoubleMatrix2D;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.graphics.pcolor.PcolorGUI;
import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.general_libraries.results.Execution;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.LWSim.TrafficDestination;
import edu.columbia.lrl.general.Message;

public class OverTimeSrcDestAnalyser extends AbstractLWSimAnalyser {

	private double binLengthNS;	
	private int nodes;
	private boolean emitedOrReceived;
	private ArrayList<Integer> sources;
	private double startTime = 0;
	private double endTime = Double.POSITIVE_INFINITY;
	
	private HashMap<Integer, SparseDoubleMatrix2D> vals;
	private int largestBin;
	private int smallestBin;
	private double largestTraffic;
	
	public OverTimeSrcDestAnalyser(@ParamName(name="Bin lengths in ns") double binLengthNS, 
			@ParamName(name="Emited (true) or received (false)?")  boolean emitedOrReceived,
			@ParamName(name="Source") int source) {
		this.binLengthNS = binLengthNS;		
		this.sources = new ArrayList<>(1);
		this.emitedOrReceived = emitedOrReceived;
		sources.add(source);
	}
	
	public OverTimeSrcDestAnalyser(@ParamName(name="Bin lengths in ns") double binLengthNS, 
			@ParamName(name="Emited (true) or received (false)?")  boolean emitedOrReceived,
			@ParamName(name="Sources") int ... sources_) {
		this.binLengthNS = binLengthNS;		
		this.sources = new ArrayList<>(sources_.length);
		this.emitedOrReceived = emitedOrReceived;
        for (int value : sources_) {
            sources.add(value);
        }
	}	
	
	public OverTimeSrcDestAnalyser(@ParamName(name="Start time") double startTime, 
			@ParamName(name="End time") double endTime, 
			@ParamName(name="End time") double bins, 
			@ParamName(name="Emited (true) or received (false)?")  boolean emitedOrReceived,
			@ParamName(name="Sources") int ... sources_) {
		this.binLengthNS = (endTime - startTime)/bins;
		this.startTime = startTime;
		this.endTime = endTime;
		this.sources = new ArrayList<>(sources_.length);
		this.emitedOrReceived = emitedOrReceived;
        for (int value : sources_) {
            sources.add(value);
        }
	}	
	
	@Override
	public void init(LWSIMExperiment exp) {
		super.init(exp);
		nodes = lwSimExp.getNumberOfClients();
		vals = new HashMap<>();
		largestBin = 0;
		smallestBin = Integer.MAX_VALUE;
		largestTraffic = 0;
	}	

	@Override
	public void addInfo(DataPoint basic, DataPoint globals, Execution e, double simTime) {
		double[][] mat = new double[largestBin-smallestBin][nodes*sources.size()];
		double logLargest = Math.log(largestTraffic);
		for (int h = 0 ; h < sources.size() ; h++) {
			int sourceId = sources.get(h);
			for (int i = smallestBin ; i < largestBin ; i++) {
				SparseDoubleMatrix2D tab = vals.get(i);
				if (tab != null) {
					for (int j = 0 ; j < nodes ; j++) {
						double vv = tab.get(sourceId, j);
						if (vv > 0) {
							mat[i-smallestBin][h*nodes +j] = Math.log(vv)/logLargest;
						} else {
							mat[i-smallestBin][h*nodes +j] = 0;
						}
					}
				}
			}
		}
		PcolorGUI gui = new PcolorGUI(mat);
		gui.showInFrame();
	}

	@Override public void packetQuenched(Message m, String where) {}
	@Override public void packetContented(Message m, String where, TrafficDestination swi, int type) {}

	@Override public void packetTransmitted(Message m) {}

	@Override
	public void packetEmitted(Message m) {	}
	
	@Override
	public void notifyEnd(double clock, int status) {
		// TODO Auto-generated method stub
	}	

	@Override
	public void packetReceived(Message m, int origin, int dest,
			double timeEmitted, double timeReceived) {
		int binId;
		if (emitedOrReceived) {
			if (timeEmitted < startTime || timeEmitted > endTime) {
				return;
			}
			binId = (int)Math.floor(timeEmitted/binLengthNS);
		} else {
			if (timeReceived < startTime || timeReceived > endTime) {
				return;
			}			
			binId = (int)Math.floor(timeReceived/binLengthNS);
		}
		SparseDoubleMatrix2D bin;
		if ((bin = vals.get(binId)) == null) {
			bin = new SparseDoubleMatrix2D(nodes, nodes);
			vals.put(binId, bin);
			largestBin = Math.max(largestBin, binId);
			smallestBin = Math.min(smallestBin, binId);
		}
		double actual = bin.get(origin, dest);
		double newVal = actual + m.sizeInBits;
		bin.set(origin, dest, newVal);
		largestTraffic = Math.max(largestTraffic, newVal);
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
