package ch.epfl.general_libraries.path;

import java.util.Comparator;

import ch.epfl.general_libraries.utils.NodePair;


/**
 * This path calculator simply returns the number of hops of each path that is evaluated
 * @author rumley
 *
 */
public class HopsCalculator extends PathCalculator {


	/**
	 * Returns p.size() - 1
	 */
	@Override
	public float getPathValue(Path p) {
		return p.size() -1;
	}
	
	/**
	 * Returns 1
	 */
	@Override
	public float getSegmentValue(int i, int j) { return 1; }

	
	public Comparator<Path> getComparator() {
		return (p1, p2) -> p1.size() - p2.size();
	}

	@Override
	public Comparator<NodePair> getNodePairComparator() {
		return (p1, p2) -> 0;
	}

	public HopsCalculator() {
	}
}
