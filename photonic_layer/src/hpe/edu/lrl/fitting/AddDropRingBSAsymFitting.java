package hpe.edu.lrl.fitting;

import java.util.ArrayList;
import PhotonicElements.Utilities.MathLibraries.Complex;
import PhotonicElements.Utilities.MathLibraries.MoreMath.Conversions.Angles;
import PhotonicElements.Utilities.MathLibraries.ComplexGraphSolver.SFG;
import ch.epfl.general_libraries.clazzes.ParamName;
import hpe.edu.lrl.ring.neff.NeffRidge450X250X50;


public class AddDropRingBSAsymFitting {

	public double kin;
	public double kout;
	public double tin;
	public double tout;
	public double fsr_nm;
	public double lambda_nm;
	public double lambdaRes_nm;
	public double Ls;
	public double r1;
	public double phaseR1_deg;
	public double phaseR1_rad;
	public double t1;
	public double phaseT1_deg;
	public double phaseT1_rad;
	public double r2;
	public double phaseR2_deg;
	public double phaseR2_rad;
	public double t2;
	public double phaseT2_deg;
	public double phaseT2_rad;
	public NeffRidge450X250X50 neff = new NeffRidge450X250X50();
	ArrayList<String> nodes;
	public SFG sfg;

	public AddDropRingBSAsymFitting(@ParamName(name = "wavelength (nm)") double lambda_nm,
			@ParamName(name = "resonance wavelength (nm)") double lambdaRes_nm,
			@ParamName(name = "fsr (nm)") double fsr_nm, @ParamName(name = "input kappa") double kin,
			@ParamName(name = "output kappa") double kout, @ParamName(name = "scattering loss") double Ls,
			@ParamName(name = "reflectance [r]") double r1,
			@ParamName(name = "coupling phase (degree)") double phaseR2_deg) {
		this.lambda_nm = lambda_nm;
		this.lambdaRes_nm = lambdaRes_nm;
		this.fsr_nm = fsr_nm;
		this.kin = kin;
		this.kout = kout;
		this.Ls = Ls;
		this.r1 = r1;
		this.phaseR1_deg = 0.0D;
		this.phaseR1_rad = Angles.toRadian(this.phaseR1_deg);
		this.t1 = Math.sqrt(Ls - r1 * r1);
		this.phaseT1_deg = 0.0D;
		this.phaseT1_rad = Angles.toRadian(this.phaseT1_deg);
		this.r2 = r1;
		this.phaseR2_deg = phaseR2_deg;
		this.phaseR2_rad = Angles.toRadian(phaseR2_deg);
		this.t2 = this.t1;
		this.phaseT2_deg = 0.0D;
		this.phaseT2_rad = Angles.toRadian(this.phaseT2_deg);
		this.tin = Math.sqrt(1.0D - kin * kin);
		this.tout = Math.sqrt(1.0D - kout * kout);
		this.nodes = new ArrayList<>();
		this.buildSFG();
	}

	private void buildSFG() {
		this.nodes.add("DC1.N1");
		this.nodes.add("DC1.N2");
		this.nodes.add("DC1.N3");
		this.nodes.add("DC1.N4");
		this.nodes.add("DC2.N1");
		this.nodes.add("DC2.N2");
		this.nodes.add("DC2.N3");
		this.nodes.add("DC2.N4");
		this.nodes.add("DC3.N1");
		this.nodes.add("DC3.N2");
		this.nodes.add("DC3.N3");
		this.nodes.add("DC3.N4");
		this.nodes.add("DC4.N1");
		this.nodes.add("DC4.N2");
		this.nodes.add("DC4.N3");
		this.nodes.add("DC4.N4");
		this.nodes.add("LR.N1");
		this.nodes.add("LR.N2");
		this.sfg = new SFG(this.nodes.size(), this.nodes);
		double phi = (this.lambda_nm - this.lambdaRes_nm) / this.fsr_nm * 2.0D * 3.141592653589793D;
		Complex gain = Complex.minusJ.times(phi / 2.0D).exp().times(Math.pow(this.Ls, 0.25D));
		this.sfg.addArrow("DC1.N1", "DC1.N2", new Complex(this.tin, 0.0D));
		this.sfg.addArrow("DC1.N1", "DC1.N3", new Complex(0.0D, -this.kin));
		this.sfg.addArrow("DC1.N4", "DC1.N3", new Complex(this.tin, 0.0D));
		this.sfg.addArrow("DC1.N4", "DC1.N2", new Complex(0.0D, -this.kin));
		this.sfg.addArrow("DC2.N2", "DC2.N1", new Complex(this.tout, 0.0D));
		this.sfg.addArrow("DC2.N2", "DC2.N4", new Complex(0.0D, -this.kout));
		this.sfg.addArrow("DC2.N3", "DC2.N4", new Complex(this.tout, 0.0D));
		this.sfg.addArrow("DC2.N3", "DC2.N1", new Complex(0.0D, -this.kout));
		this.sfg.addArrow("DC3.N2", "DC3.N1", new Complex(this.tin, 0.0D));
		this.sfg.addArrow("DC3.N2", "DC3.N4", new Complex(0.0D, -this.kin));
		this.sfg.addArrow("DC3.N3", "DC3.N4", new Complex(this.tin, 0.0D));
		this.sfg.addArrow("DC3.N3", "DC3.N1", new Complex(0.0D, -this.kin));
		this.sfg.addArrow("DC4.N1", "DC4.N2", new Complex(this.tout, 0.0D));
		this.sfg.addArrow("DC4.N1", "DC4.N3", new Complex(0.0D, -this.kout));
		this.sfg.addArrow("DC4.N4", "DC4.N3", new Complex(this.tout, 0.0D));
		this.sfg.addArrow("DC4.N4", "DC4.N2", new Complex(0.0D, -this.kout));
		this.sfg.addArrow("LR.N1", "DC2.N2", gain);
		this.sfg.addArrow("DC2.N1", "DC1.N4", gain);
		this.sfg.addArrow("DC4.N2", "LR.N2", gain);
		this.sfg.addArrow("DC3.N4", "DC4.N1", gain);
		Complex r1Complex = new Complex(this.r1 * Math.cos(this.phaseR1_rad), this.r1 * Math.sin(this.phaseR1_rad));
		Complex t1Complex = new Complex(this.t1 * Math.cos(this.phaseT1_rad), this.t1 * Math.sin(this.phaseT1_rad));
		Complex r2Complex = new Complex(this.r2 * Math.cos(this.phaseR2_rad), this.r2 * Math.sin(this.phaseR2_rad));
		Complex t2Complex = new Complex(this.t2 * Math.cos(this.phaseT2_rad), this.t2 * Math.sin(this.phaseT2_rad));
		this.sfg.addArrow("DC1.N3", "DC3.N3", Complex.minusJ.times(r1Complex));
		this.sfg.addArrow("DC1.N3", "LR.N1", t1Complex);
		this.sfg.addArrow("LR.N2", "DC3.N3", t2Complex);
		this.sfg.addArrow("LR.N2", "LR.N1", Complex.minusJ.times(r2Complex));
	}
}