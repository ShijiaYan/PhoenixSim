//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package PhotonicElements.RingStructures.AddDrop.Graph;

import java.util.ArrayList;
import PhotonicElements.Utilities.MathLibraries.Complex;
import PhotonicElements.Utilities.MathLibraries.ComplexGraphSolver.SFG;
import ch.epfl.general_libraries.clazzes.ParamName;


public class AddDropSecondOrder {

	public double kin;
	public double kout;
	public double tin;
	public double tout;
	public double kmiddle;
	public double tmiddle;
	public double L;
	public double phi_rad;
	ArrayList<String> nodes;
	public SFG sfg;

	public AddDropSecondOrder(@ParamName(name = "round-trip phase (rad)") double phi_rad,
			@ParamName(name = "ring loss") double L, @ParamName(name = "input kappa") double kin,
			@ParamName(name = "output kappa") double kout, @ParamName(name = "middle kappa") double kmiddle) {
		this.phi_rad = phi_rad;
		this.L = L;
		this.kin = kin;
		this.kout = kout;
		this.tin = Math.sqrt(1.0D - kin * kin);
		this.tout = Math.sqrt(1.0D - kout * kout);
		this.kmiddle = kmiddle;
		this.tmiddle = Math.sqrt(1.0D - kmiddle * kmiddle);
		this.buildSFG();
		this.nodes = new ArrayList<>();
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
		this.sfg = new SFG(this.nodes.size(), this.nodes);
		Complex gain = Complex.minusJ.times(this.phi_rad / 2.0D).exp().times(Math.pow(this.L, 0.25D));
		this.sfg.addArrow("DC1.N1", "DC1.N2", new Complex(this.tin, 0.0D));
		this.sfg.addArrow("DC1.N1", "DC1.N3", new Complex(0.0D, -this.kin));
		this.sfg.addArrow("DC1.N4", "DC1.N3", new Complex(this.tin, 0.0D));
		this.sfg.addArrow("DC1.N4", "DC1.N2", new Complex(0.0D, -this.kin));
		this.sfg.addArrow("DC2.N2", "DC2.N1", new Complex(this.tmiddle, 0.0D));
		this.sfg.addArrow("DC2.N2", "DC2.N4", new Complex(0.0D, -this.kmiddle));
		this.sfg.addArrow("DC2.N3", "DC2.N4", new Complex(this.tmiddle, 0.0D));
		this.sfg.addArrow("DC2.N3", "DC2.N1", new Complex(0.0D, -this.kmiddle));
		this.sfg.addArrow("DC1.N3", "DC2.N2", gain);
		this.sfg.addArrow("DC2.N1", "DC1.N4", gain);
		this.sfg.addArrow("DC3.N1", "DC3.N2", new Complex(this.tout, 0.0D));
		this.sfg.addArrow("DC3.N1", "DC3.N3", new Complex(0.0D, -this.kout));
		this.sfg.addArrow("DC3.N4", "DC3.N3", new Complex(this.tout, 0.0D));
		this.sfg.addArrow("DC3.N4", "DC3.N2", new Complex(0.0D, -this.kout));
		this.sfg.addArrow("DC2.N4", "DC3.N1", gain);
		this.sfg.addArrow("DC3.N2", "DC2.N3", gain);
	}

	public Complex getS31() {
		this.sfg.buildForwardPaths("DC1.N1", "DC3.N3");
		return this.sfg.computeForwardGain().divides(this.sfg.computeDelta());
	}

	public Complex getS21() {
		this.sfg.buildForwardPaths("DC1.N1", "DC1.N2");
		return this.sfg.computeForwardGain().divides(this.sfg.computeDelta());
	}

	public void buildDrop() {
		this.sfg.buildForwardPaths("DC1.N1", "DC3.N3");
	}

	public void buildThru() {
		this.sfg.buildForwardPaths("DC1.N1", "DC1.N2");
	}

	public static void main(String[] args) {
		AddDropSecondOrder adr = new AddDropSecondOrder(0.0D, 1.0D, 0.1D, 0.1D, 0.1D);
		System.out.println(adr.sfg.printAllLoops());
	}
}
