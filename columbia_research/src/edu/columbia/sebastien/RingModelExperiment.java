package edu.columbia.sebastien;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.general_libraries.results.Execution;

public class RingModelExperiment implements Experiment {
	
	private int period;
	private int ringSize;
	private int duration;
	private double xi;
	private double attenuation;
	private double t;
	private int store;
	
	public RingModelExperiment(@ParamName(name="wavelength") int period, 
			@ParamName(name="ring size") int ringSize, 
			@ParamName(name="sim duration") int duration, 
			@ParamName(name="t") double t, 
			@ParamName(name="modif factor") double attenuation, 
			@ParamName(name="store") int store) {
		this.period = period;
		this.ringSize = ringSize;
		this.duration = duration;
		this.attenuation = attenuation;
		this.t = t;
		this.xi = Math.sqrt(1 - (t*t));
		this.store = store;
		ringStateReal = new double[ringSize];
		ringStateImg  = new double[ringSize];
	}
	
	private int state = 0;
	private double[] ringStateReal;
	private double[] ringStateImg;
	
	private String[] desc = new String[]{
			"output real",
			"output img",
			"input real",
			"input img",
			"from ring real",
			"from ring img",
			"to ring real",
			"to ring img"
	};
	
/*	private String[] pow = new String[]{
			"ring transit power",
			"waveguide transit power",
			"from ring to wave",
			"from wave to fing"
	};*/

	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {
		
		Execution ex = new Execution();
		
		DataPoint dp = new DataPoint();
		dp.addProperty("period", period);
		dp.addProperty("ring size", ringSize);
		dp.addProperty("t", t);
		dp.addProperty("duration", duration);
		dp.addProperty("attenuation", attenuation);
		double lastTransmission = 0;
			
		for (int i = 0 ; i < duration; i++) {
			DataPoint dp2 = dp.getDerivedDataPoint();
			dp2.addProperty("state", state);
			double w = ringStateReal[ringSize-1]*(attenuation);
			double z  = ringStateImg[ringSize-1]*(attenuation);
			double[] copyReal = new double[ringSize];
			double[] copyImg = new double[ringSize];
			System.arraycopy(ringStateReal, 0, copyReal, 1, ringSize-1);
			System.arraycopy(ringStateImg, 0, copyImg, 1, ringSize-1);
		/*	for (int j = ringSize -1 ; j >= 1 ; j--) {
				ringState[j] = ringState[j-1];
			}*/
			ringStateReal = copyReal;
			ringStateImg  = copyImg;
			double[] input = getNextInput();
			double x = input[0];
			double y = input[1];
			
			double s = -xi*y + t*w;
			double r = (t*z) + (xi*x);
			
			ringStateReal[0] = s;
			ringStateImg[0] = r;
			
			double p = t*x - xi*z;
			double q = t*y + xi*w;
			 
			double outputPower = p*p + q*q;
			double startRingPower = ringStateReal[0]*ringStateReal[0] + ringStateImg[0]*ringStateImg[0];
			
			if (store > 1) {
				double[] vals = new double[]{
						p,q,
						x,y,
						w,z,
						s,r 
				};
				ex.addArrayResult("fields", vals , desc, dp2);
				
				double[] poww = new double[]{
						t*t*x*x,
						t*t*z*z,
						xi*xi*x*x,
						xi*xi*z*z,
						2*t*xi*x*z
						
				};
				
				String[] powwLib = new String[]{
						"waveguide straight",
						"ring straigth",
						"linear waveguide->ring",
						"linear ring->waveguide",
						"extra to ring"
				};
				
				ex.addArrayResult("power transfers", poww, powwLib, dp2);
				dp2.addResultProperty("power transfer to the ring", (x*x+y*y)- outputPower);
				dp2.addResultProperty("power transfer to the waveguide", (w*w+z*z) - startRingPower);
				dp2.addResultProperty("transmission", outputPower/(x*x));
				dp2.addResultProperty("output power", outputPower);
				dp2.addResultProperty("from ring power", w*w + z*z);
				ex.addDataPoint(dp2);
			}
			lastTransmission = outputPower/(x*x);
			state++;
		}
		DataPoint dp3 = dp.getDerivedDataPoint();
		dp3.addResultProperty("transmission", lastTransmission);
		ex.addDataPoint(dp3);
		man.addExecution(ex);
	}
	
	private double[] getNextInput() {
		double rel = (double)state/(double)period;
		double prod =rel*2*Math.PI;
		return new double[]{Math.cos(prod)/*, Math.sin(prod)*/,0};
	}

}
