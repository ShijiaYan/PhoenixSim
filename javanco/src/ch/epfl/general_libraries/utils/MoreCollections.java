package ch.epfl.general_libraries.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class MoreCollections {

	/**
	 * Return a list of integers containing [start, end] (both inclusive)
	 * @param start
	 * @param end
	 * @return a list of integers containing [start, end] (both inclusive)
	 */
	public static ArrayList<Integer> subsetOfN(int start, int end) {
		ArrayList<Integer> ints = new ArrayList<>(end - start + 1);
		for (int i = start ; i <= end ; i++) {
			ints.add(i);
		}
		return ints;	
	}
	
	public static Set<Integer> toIntegerSet(int[] array) {
		HashSet<Integer> hs = new HashSet<>();
        for (int value : array) {
            hs.add(value);
        }
		return hs;
	}
	
	public static int maxInt(List<Integer> list) {
		int max = -Integer.MAX_VALUE;
        for (int t : list) {
            if (t > max)
                max = t;
        }
		return max;
	}

	public static double maxDouble(List<Double> r) {
		double max = Double.MIN_VALUE;
        for (double s : r) {
            if (s > max)
                max = s;
        }
		return max;
	}

	public static double sum(Collection<Double> values) {
		double accum = 0;
		for (Double d : values) {
			accum += d;
		}
		return accum;
	}

	public static void addIntegerArray(Collection<Integer> col, int[] dependences) {
        for (int dependence : dependences) {
            col.add(dependence);
        }
		
	}

}
