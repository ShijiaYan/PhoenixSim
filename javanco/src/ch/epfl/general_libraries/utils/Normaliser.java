package ch.epfl.general_libraries.utils;

import java.util.ArrayList;
import java.util.List;

public class Normaliser {

	public static List<Double> normalise(List<Double> list) {
		ArrayList<Double> newList = new ArrayList<>(list.size());
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;

        for (double d : list) {
            if (d < min) {
                min = d;
            }
            if (d > max) {
                max = d;
            }
        }

        for (Double aDouble : list) {
            newList.add((aDouble - min) / max);
        }

		return newList;
	}
}
