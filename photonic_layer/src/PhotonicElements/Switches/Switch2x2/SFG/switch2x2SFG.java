//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package PhotonicElements.Switches.Switch2x2.SFG;

import java.util.ArrayList;
import PhotonicElements.Utilities.MathLibraries.Complex;
import PhotonicElements.Utilities.MathLibraries.ComplexGraphSolver.SFG;


public class switch2x2SFG {

	private static final Complex ZERO = new Complex(0.0D, 0.0D);
	private static final Complex ONE = new Complex(1.0D, 0.0D);

	public switch2x2SFG() {
	}

	public static void main(String[] args) {
		ArrayList<String> nodes = new ArrayList<>();
		nodes.add("DC1.I1");
		nodes.add("DC1.O1");
		nodes.add("DC1.I2");
		nodes.add("DC1.O2");
		nodes.add("DC1.I3");
		nodes.add("DC1.O3");
		nodes.add("DC1.I4");
		nodes.add("DC1.O4");
		nodes.add("DC2.I1");
		nodes.add("DC2.O1");
		nodes.add("DC2.I2");
		nodes.add("DC2.O2");
		nodes.add("DC2.I3");
		nodes.add("DC2.O3");
		nodes.add("DC2.I4");
		nodes.add("DC2.O4");
		nodes.add("wg1.I1");
		nodes.add("wg1.O1");
		nodes.add("wg1.I2");
		nodes.add("wg1.O2");
		nodes.add("wg2.I1");
		nodes.add("wg2.O1");
		nodes.add("wg2.I2");
		nodes.add("wg2.O2");
		SFG sfg = new SFG(0, nodes);
		sfg.addArrow("DC1.I1", "DC1.O1", ZERO);
		sfg.addArrow("DC1.I1", "DC1.O2", ONE);
		sfg.addArrow("DC1.I1", "DC1.O3", ONE);
		sfg.addArrow("DC1.I1", "DC1.O4", ZERO);
		sfg.addArrow("DC1.I2", "DC1.O1", ONE);
		sfg.addArrow("DC1.I2", "DC1.O2", ZERO);
		sfg.addArrow("DC1.I2", "DC1.O3", ZERO);
		sfg.addArrow("DC1.I2", "DC1.O4", ONE);
		sfg.addArrow("DC1.I3", "DC1.O1", ONE);
		sfg.addArrow("DC1.I3", "DC1.O2", ZERO);
		sfg.addArrow("DC1.I3", "DC1.O3", ZERO);
		sfg.addArrow("DC1.I3", "DC1.O4", ONE);
		sfg.addArrow("DC1.I4", "DC1.O1", ZERO);
		sfg.addArrow("DC1.I4", "DC1.O2", ONE);
		sfg.addArrow("DC1.I4", "DC1.O3", ONE);
		sfg.addArrow("DC1.I4", "DC1.O4", ZERO);
		sfg.addArrow("DC2.I1", "DC2.O1", ZERO);
		sfg.addArrow("DC2.I1", "DC2.O2", ONE);
		sfg.addArrow("DC2.I1", "DC2.O3", ONE);
		sfg.addArrow("DC2.I1", "DC2.O4", ZERO);
		sfg.addArrow("DC2.I2", "DC2.O1", ONE);
		sfg.addArrow("DC2.I2", "DC2.O2", ZERO);
		sfg.addArrow("DC2.I2", "DC2.O3", ZERO);
		sfg.addArrow("DC2.I2", "DC2.O4", ONE);
		sfg.addArrow("DC2.I3", "DC2.O1", ONE);
		sfg.addArrow("DC2.I3", "DC2.O2", ZERO);
		sfg.addArrow("DC2.I3", "DC2.O3", ZERO);
		sfg.addArrow("DC2.I3", "DC2.O4", ONE);
		sfg.addArrow("DC2.I4", "DC2.O1", ZERO);
		sfg.addArrow("DC2.I4", "DC2.O2", ONE);
		sfg.addArrow("DC2.I4", "DC2.O3", ONE);
		sfg.addArrow("DC2.I4", "DC2.O4", ZERO);
		sfg.addArrow("wg1.I1", "wg1.O1", ONE);
		sfg.addArrow("wg1.I1", "wg1.O2", ONE);
		sfg.addArrow("wg1.I2", "wg1.O1", ONE);
		sfg.addArrow("wg1.I2", "wg1.O2", ONE);
		sfg.addArrow("wg2.I1", "wg2.O1", ONE);
		sfg.addArrow("wg2.I1", "wg2.O2", ONE);
		sfg.addArrow("wg2.I2", "wg2.O1", ONE);
		sfg.addArrow("wg2.I2", "wg2.O2", ONE);
		sfg.addArrow("DC1.O3", "wg1.I1", ONE);
		sfg.addArrow("wg1.O1", "DC1.I3", ONE);
		sfg.addArrow("DC1.O2", "wg2.I1", ONE);
		sfg.addArrow("wg2.O1", "DC1.I2", ONE);
		sfg.addArrow("DC2.O4", "wg1.I2", ONE);
		sfg.addArrow("wg1.O2", "DC2.I4", ONE);
		sfg.addArrow("DC2.O1", "wg2.I2", ONE);
		sfg.addArrow("wg2.O2", "DC2.I1", ONE);
		System.out.println(sfg.printAllLoops());
		sfg.buildForwardPaths("DC1.I1", "DC2.O3");
		System.out.println(sfg.printForwardPaths());
	}
}
