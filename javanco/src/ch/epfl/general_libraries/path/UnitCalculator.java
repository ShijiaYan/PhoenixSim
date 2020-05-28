package ch.epfl.general_libraries.path;

import java.util.Comparator;

import ch.epfl.general_libraries.utils.NodePair;

public class UnitCalculator extends PathCalculator {
	
	@Override
	public float getPathValue(Path p) {
		return 1;
	}
	@Override
	public float getSegmentValue(int i, int j) { return 1; }

	public Comparator<Path> getComparator() {
		return (p1, p2) -> 0;
	}

	@Override
	public Comparator<NodePair> getNodePairComparator() {
		return (p1, p2) -> 0;
	}	
	
}
