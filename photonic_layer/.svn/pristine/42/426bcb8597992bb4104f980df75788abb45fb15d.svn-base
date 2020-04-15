package PhotonicElements.MachZehnder;

import PhotonicElements.Utilities.MathLibraries.Complex;

public class TheoreticalMZI {
	
	private double powerCouplingCoeff1;
	private double powerCouplingCoeff2;
	private double phaseDelay1;
	private double phaseDelay2;
	private double lossArm1;
	private double lossArm2;
	
	public TheoreticalMZI(double powerCouplingCoeff1,
						  double powerCouplingCoeff2,
						  double phaseDelay1,
						  double phaseDelay2,
						  double lossArm1,
						  double lossArm2) {
		
		this.powerCouplingCoeff1 = powerCouplingCoeff1;
		this.powerCouplingCoeff2 = powerCouplingCoeff2;
		this.phaseDelay1 = phaseDelay1;
		this.phaseDelay2 = phaseDelay2;
		this.lossArm1 = Math.sqrt(lossArm1);
		this.lossArm2 = Math.sqrt(lossArm2);
	} 
	
	public double get11Transfer() {
		double t1 = Math.sqrt(1 - powerCouplingCoeff1);
		double r1 = Math.sqrt(powerCouplingCoeff1);
		double t2 = Math.sqrt(1 - powerCouplingCoeff2);
		double r2 = Math.sqrt(powerCouplingCoeff2);
		Complex c1 = Complex.minusJ.times(phaseDelay1).exp() ;
		Complex c2 = Complex.minusJ.times(phaseDelay2).exp() ;
		Complex temp = c2.times(r1*r2*lossArm2);
		return c1.times(t1*t2*lossArm1).minus(temp).absSquared();
	}
	
	public double get22Transfer() {
		double t1 = Math.sqrt(1 - powerCouplingCoeff1);
		double r1 = Math.sqrt(powerCouplingCoeff1);
		double t2 = Math.sqrt(1 - powerCouplingCoeff2);
		double r2 = Math.sqrt(powerCouplingCoeff2);
		Complex c1 = Complex.minusJ.times(phaseDelay1).exp() ;
		Complex c2 = Complex.minusJ.times(phaseDelay2).exp() ;
		Complex temp = c2.times(r1*r2*lossArm1);
		return c1.times(t1*t2*lossArm2).minus(temp).absSquared();
	}	
	
	public double get21Transfer() {
		double t1 = Math.sqrt(1 - powerCouplingCoeff1);
		double r1 = Math.sqrt(powerCouplingCoeff1);
		double t2 = Math.sqrt(1 - powerCouplingCoeff2);
		double r2 = Math.sqrt(powerCouplingCoeff2);
		Complex c1 = Complex.minusJ.times(phaseDelay1).exp() ;
		Complex c2 = Complex.minusJ.times(phaseDelay2).exp() ;
		Complex temp = c1.times(r1*t2*lossArm1).plus(c2.times(r2*t1*lossArm2)).times(new Complex(0,1));
		return temp.absSquared();		
	}
	
	public double get12Transfer() {
		double t1 = Math.sqrt(1 - powerCouplingCoeff1);
		double r1 = Math.sqrt(powerCouplingCoeff1);
		double t2 = Math.sqrt(1 - powerCouplingCoeff2);
		double r2 = Math.sqrt(powerCouplingCoeff2);
		Complex c1 = Complex.minusJ.times(phaseDelay1).exp() ;
		Complex c2 = Complex.minusJ.times(phaseDelay2).exp() ;
		Complex temp = c1.times(r1*t2*lossArm2).plus(c2.times(r2*t1*lossArm1)).times(new Complex(0,1));
		return temp.absSquared();		
	}	

}
