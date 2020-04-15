package edu.columbia.lrl.experiments.task_traffic.task_generators;

import ch.epfl.general_libraries.clazzes.ConstructorDef;
import ch.epfl.general_libraries.clazzes.ParamName;

@SuppressWarnings("unused")
public class BandwidthDependentPropertiesGenerator extends
		ConstantsTaskPropertiesGenerator {
	
	private boolean taskFixed = false;
	private boolean comFixed = false;
	private boolean withZeta = false;
	private boolean withF = false;
	private boolean withRhoC = false;
	
	private double zeta;
	private float F;
	private float rho_c;
	
	@ConstructorDef(def="Fix the compute times (zeta=1)")
	public BandwidthDependentPropertiesGenerator(@ParamName(name="Task duration (ti + ta) in ns") double taskDurationInNs) {	
		this(taskDurationInNs, 1d);
	}

	@ConstructorDef(def="Fix the compute times and adapt comm sizes for a zeta (network bandwidth dependent)")
	public BandwidthDependentPropertiesGenerator(@ParamName(name="Task duration (ti + ta) in ns") double taskDurationInNs, 
												 @ParamName(name="Zeta") double zeta) {
		super(taskDurationInNs/2, taskDurationInNs/2, 0, 0);
		taskFixed = true;
		withZeta = true;
		this.zeta = zeta;
		// TODO Auto-generated constructor stub
	}
	
	@ConstructorDef(def="Fix the compute times and adapt comm sizes for a F (absolute byte/flop type)")
	public BandwidthDependentPropertiesGenerator(@ParamName(name="Task duration (ti + ta) in ns") double taskDurationInNs, 
												 @ParamName(name="F (bit/flop or bit/ns)") float F) {
		super(taskDurationInNs/2, taskDurationInNs/2, 0, 0);
		taskFixed = true;
		withF = true;
		this.F = F;
		// TODO Auto-generated constructor stub
	}
	
	@ConstructorDef(def="Fix the compute times and adapt comm sizes for a rho_c (relative network load)")
	public BandwidthDependentPropertiesGenerator(@ParamName(name="Task duration (ti + ta) in ns") float taskDurationInNs, 
											     @ParamName(name="rho_c") float rho_c) {
		super(taskDurationInNs/2, taskDurationInNs/2, 0, 0);
		taskFixed = true;
		withRhoC = true;
		this.rho_c = rho_c;
		// TODO Auto-generated constructor stub
	}	
	
	
	@ConstructorDef(def="Fix the comm sizes (zeta=1)")
	public BandwidthDependentPropertiesGenerator(@ParamName(name="Task size (ss + sr) in bits") int taskComInBits) {	
		this(taskComInBits, 1d);
	}
	
	@ConstructorDef(def="Fix the comm sizes and adapt compute time for a zeta (network bandwidth dependent)")
	public BandwidthDependentPropertiesGenerator(@ParamName(name="Task size (ss + sr) in bits")int taskComInBits, 
											     @ParamName(name="Zeta") double zeta) {
		super(0, 0, taskComInBits, taskComInBits);
		comFixed = true;
		withZeta = true;
		this.zeta = zeta;
		// TODO Auto-generated constructor stub
	}
	
	@ConstructorDef(def="Fix the comm sizes and adapt compute time for a F (absolute byte/flop type)")
	public BandwidthDependentPropertiesGenerator(@ParamName(name="Task size (ss + sr) in bits")int taskComInBits, 
											     @ParamName(name="F (bit/flop or bit/ns)") float F) {
		super(0, 0, taskComInBits, taskComInBits);
		comFixed = true;
		withF = true;
		this.F = F;
		// TODO Auto-generated constructor stub
	}
	
	@ConstructorDef(def="Fix the comm sizes and adapt compute time for a rho_c (relative network load)")
	public BandwidthDependentPropertiesGenerator(@ParamName(name="Task size (ss + sr) in bits")long taskComInBits, 
											     @ParamName(name="rho_c") float rho_c) {
		super(0, 0, (int)taskComInBits, (int)taskComInBits);
		comFixed = true;
		withRhoC = true;
		this.rho_c = rho_c;
		// TODO Auto-generated constructor stub
	}	
	
/*	@Override
	public void init(AbstractTaskGenerator generator, double BWinBitsPerSecond, double nodePowerGflopPerSecond, int comNodes, int compNodes, double rho_x) {
		double bwInBitsPerNS = BWinBitsPerSecond / 1000000000;
		int gg = 0;
		double Fconv;
		if (nodePowerGflopPerSecond > 0) {
			// must convert F (given in bit/flop) in bit/ns
			Fconv = F * nodePowerGflopPerSecond;
		} else {
			Fconv = F;
		}
		int gg2 = 0;
		
		if (taskFixed) {
			double Fx = generator.getMeanComputingTimePerCompoundTaskNS__Fx();
		//	Fx /= 1000000000d; // converting to second
			double Fc;
			if (withZeta) {
				Fc = bwInBitsPerNS*Fx/zeta;
			} else if (withF) {
				Fc = Fconv*Fx;
			} else {
				double beta = 1000000000*rho_x*compNodes/Fx;
				Fc = rho_c*BWinBitsPerSecond*comNodes/beta;
			}
			
			double ss = generator.getTaskSizesForA_Fc(Fc);
			super.setSchedulingSize((int)(ss/2d));
			super.setResultSize((int)(ss/2d));
		} else {
			double Fc = generator.getMeanCommunicationFootprintPerCompoundTask__Fc();
			double Fx;
			if (withZeta) {
				Fx = 1000000000*zeta*Fc/BWinBitsPerSecond;
			} else if (withF) {
				Fx = Fc/Fconv;
			} else {
				double beta;
				if (withRhoC) {
					beta = rho_c*BWinBitsPerSecond*comNodes/Fc;
				} else {
					beta = generator.getArrivalRatePerMS()*1000;
				}
				Fx = 1000000000*rho_x*compNodes/beta;
			}
			double tt = generator.getTaskLengthForA_Fx(Fx);
			super.setInitComputeIndex(tt/2d);
			super.setAggregateComputeIndex(tt/2d);
		}
	}*/
}
