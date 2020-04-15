package edu.columbia.lrl.experiments.task_traffic.configurators;

import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.experiments.task_traffic.IrregularTrafficApplication;
import edu.columbia.lrl.experiments.task_traffic.task_generators.AbstractTaskGenerator;

public abstract class AbstractSubConf {

	protected double B = -1;
	protected double N = -1;
	protected double P;
	protected double computingNodes = 1;
	
	protected IrregularTrafficApplication appl;
	
	protected Runnable toInvoke;
	protected LWSIMExperiment exp;
	
	protected InitFeedback  configurationReturnedFailure = null;
	
	public void addParam(String s, String v) {
		appl.getConfigurator().addParam(s, v);
	}
	
	protected AbstractTaskGenerator getTaskGenerator() {
		return appl.getConfigurator().getTaskGenerator();
	}
	
	protected int getNonComputingNodes() {
		return appl.getScheduler().getNumberOfNotComputingNodes();
	}
	
	protected double getFc(double Fc_) {
		double Fc = Fc_;
		if (Fc < 0) {
			addParam("Task comm sizes fixed by", "atomic sizes ss");
			Fc = getTaskGenerator().getMeanCommunicationFootprintPerCompoundTask__Fc(appl);
		} else {
			addParam("Task com sizes fixed by", "Fc");					
		}
		return Fc;
	}
	
	protected double getFx(double Fx_) {
		double Fx = Fx_;
		if (Fx < 0) {
			addParam("Task duration fixed by", "atomic task tt");	
			Fx = getTaskGenerator().getMeanFlopsPerCompoundTask_Fx();
		} else {
			addParam("Task duration fixed by", "Fx");
		}
		return Fx;
	}
	
	protected double getFx(double rhox, double beta) {
		if (computingNodes == -1) throw new IllegalStateException("Fix computing nodes number first");
		return (rhox*(computingNodes*1e9*P))/(beta*1e3);	
	}
	
	protected double getFc(double rhoc, double beta) {
		if (B == -1) throw new IllegalStateException("Fix B number first");
		if (N == -1) throw new IllegalStateException("Fix N number first");	
		return rhoc*B*1e9*N / (beta*1e3);
	}
	
	protected double getBetaFromC(double rhoc, double Fc) {
		if (B == -1) throw new IllegalStateException("Fix B number first");
		if (N == -1) throw new IllegalStateException("Fix N number first");			
		return (rhoc*B*1e9*N/Fc)/1e3;
	}
	
	protected double getBetaFromX(double rhox, double Fx) {
		if (computingNodes == -1) throw new IllegalStateException("Fix computing nodes number first");		
		return (rhox*computingNodes*1e9*P/Fx)/1e3;
	}
	
	protected double getRhoc(double Fc, double beta) {
		if (B == -1) throw new IllegalStateException("Fix B number first");
		if (N == -1) throw new IllegalStateException("Fix N number first");		
		return Fc*beta*1e3/(B*N*1e9);
	}
	
	protected double getRhox(double Fx, double beta) {
		if (computingNodes == -1) throw new IllegalStateException("Fix computing nodes number first");		
		return Fx*beta*1e3/(computingNodes*1e9*P);
	}
	
	protected double getXiFromZetaP(double zeta, double P) {
		return zeta/P;
	}
	
	
	
	protected double getFfromZetaB(double zeta, double B) {
		return B/zeta;
	}	
	
	protected double getZetaFromPxi(double P, double xi) {
		return P*xi;
	}
	
	protected double getZetaFromBF(double B, double F) {
		return B/F;
	}
	
	protected double getRhoxFromXi(double rhoc, double xi) {
		return xi*rhoc;
	}
	
	protected double getRhocFromXi(double rhox, double xi) {
		return rhox/xi;
	}
	
	
	
	
}
