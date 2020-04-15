package edu.columbia.lrl.experiments.task_traffic.configurators;

import java.util.Map;

import ch.epfl.general_libraries.utils.SimpleMap;
import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.experiments.task_traffic.IrregularTrafficApplication;
import edu.columbia.lrl.experiments.task_traffic.task_generators.AbstractTaskGenerator;

public abstract class AbstractTaskGenerationConfigurator {
	
	public AbstractTaskGenerationConfigurator() {
	}

	protected AbstractTaskGenerator taskgen;
	protected double B;
	protected int N;
	protected double P;
	private double Fx;
	private double Fc;
	private double F;
	private double zeta;
	private double rhox;
	private double rhoc;
	private double beta;
	private double xi;
	private Map<String, String> extraParams = new SimpleMap<String, String>(2);
	
	public void addParam(String s, String v) {
		extraParams.put(s, v);
	}
	
	public AbstractTaskGenerator getTaskGenerator() {
		return taskgen;
	}
	public double getXi() {
		return xi;
	}
	
	public double getB() {
		return B;
	}

	public int getN() {
		return N;
	}

	public double getFx() {
		return Fx;
	}

	public double getFc() {
		return Fc;
	}

	public double getF() {
		return F;
	}

	public double getZeta() {
		return zeta;
	}

	public double getRhox() {
		return rhox;
	}

	public double getRhoc() {
		return rhoc;
	}

	public double getBeta() {
		return beta;
	}
	
	public double getNodeComputePowerFlopsPerNS() {
		return P;
	}

	public InitFeedback  configure(LWSIMExperiment exp, IrregularTrafficApplication appl, double B, int N, double Fx, double Fc, double rhox, double rhoc, double beta) {
		if (N - appl.getScheduler().getNumberOfNotComputingNodes() <= 0) return new InitFeedback("At least one compute node is required");
		this.N = N;
		this.B = B;
		this.Fx = Fx;
		this.Fc = Fc;
		this.F = Fx/Fc;
		this.zeta = rhox/rhoc;
		this.rhox = rhox;
		this.rhoc = rhoc;
		this.beta = beta;
		return taskgen.configure(exp, appl);		
	}

	public abstract InitFeedback  initAndsetNumberOfNotComputingNodes(IrregularTrafficApplication appl, LWSIMExperiment exp, AbstractTaskGenerator taskGenerator);

	public Map<? extends String, ? extends String> getAllParameters() {
		Map<String, String> m = taskgen.getAllParameters();
		m.putAll(extraParams);
		return m;
	}
	
}
