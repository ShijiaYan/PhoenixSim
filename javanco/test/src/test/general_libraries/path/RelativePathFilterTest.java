package test.general_libraries.path;

import org.junit.Test;

import ch.epfl.general_libraries.path.HopsCalculator;
import ch.epfl.general_libraries.path.Path;
import ch.epfl.general_libraries.path.PathCalculator;
import ch.epfl.general_libraries.path.PathRelativeFilter;
import ch.epfl.general_libraries.path.PathSet;

public class RelativePathFilterTest {
	
	public static void main(String[] args) throws Exception {
		new RelativePathFilterTest().testFilterPlus();
	}
	
	@Test
	public void testFilterPlus() throws Exception {
		Path p = new Path(new int[]{1,2,3});
		PathSet set = new PathSet(4);
		set.addPath(p);
		PathCalculator pc = new HopsCalculator();
		PathRelativeFilter filter = new PathRelativeFilter(set, pc, 3f);
		Path shortP = new Path(new int[]{1,2,0,1,3});
		Path longP = new Path(new int[]{1,2,0,2,3,0,3});
		if (filter.filter(shortP) != true) throw new Exception("Filter does not works");
		if (filter.filter(longP) != true) throw new Exception("Filter does not works");
		filter.setInclusive(false);
		if (filter.filter(longP) != false) throw new Exception("Filter does not works");
		
	}	
	
	
}
