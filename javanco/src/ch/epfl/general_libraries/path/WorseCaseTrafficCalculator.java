package ch.epfl.general_libraries.path;

import java.util.ArrayList;

import ch.epfl.general_libraries.utils.Matrix;
import ch.epfl.general_libraries.utils.NodePair;

public class WorseCaseTrafficCalculator {
	
	private Matrix<ArrayList<NodePair>> mat;
	
	public WorseCaseTrafficCalculator(int size) {
		mat = new Matrix<>(size);
	}
	
	public void addPaths(Path[] parray) {
        for (Path integers : parray) {
            if (integers == null) continue;
            NodePair ext = integers.getExtremities();
            for (NodePair np : integers.getPathSegments()) {
                ArrayList<NodePair> l = mat.getMatrixElement(np);
                if (l == null) {
                    l = new ArrayList<>();
                    mat.setMatrixElement(np, l);
                }
                l.add(ext);
            }
        }
	}

	public void getWorseTraffic() {
		
	}
}
