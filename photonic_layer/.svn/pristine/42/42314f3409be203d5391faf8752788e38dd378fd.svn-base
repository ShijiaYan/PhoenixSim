package PhotonicElements.Utilities.MathLibraries.ComplexGraphSolver.tests;

import java.util.ArrayList;
import PhotonicElements.Utilities.MathLibraries.Complex;
import PhotonicElements.Utilities.MathLibraries.ComplexGraphSolver.SFG;

public class Example7 {

	public static void main(String[] args){
		ArrayList<String> nodeNames = new ArrayList<>() ;
		nodeNames.add("I") ; // node 1
		nodeNames.add("A") ; // node 2
		nodeNames.add("B") ; // node 3
		nodeNames.add("O") ; // node 4
		nodeNames.add("C") ; // node 5
		SFG sfg = new SFG(nodeNames.size(), nodeNames) ;
		sfg.addArrow("I", "A", Complex.ONE);
		sfg.addArrow("A", "B", new Complex(-1, 1));
		sfg.addArrow("B", "O", Complex.ONE);
		sfg.addArrow("A", "A", new Complex(2, 0));
		sfg.addArrow("B", "B", new Complex(0, -1));
		sfg.addArrow("B", "A", new Complex(1, 2));
		sfg.addArrow("A", "C", new Complex(0, 0.5));
		sfg.addArrow("C", "B", new Complex(3, -0.5));
		// building up the paths between two designated nodes
		String startNode = "I" ;
		String endNode = "O" ;
		sfg.buildForwardPaths(startNode, endNode);

		System.out.println(sfg.printForwardPaths());
		System.out.println(sfg.printAllLoops());
		System.out.println("\nTotal forward gain is: " + sfg.computeForwardGain());
		System.out.println("Graph determinant is: " + sfg.computeDelta());
		Complex gain = sfg.computeGain(1, 4).divides(sfg.computeDelta()) ;
		System.out.println();
		System.out.println("Total gain between node " + startNode +" and node "
							+ endNode + " is: ") ;
		System.out.println(gain);
	}
}
