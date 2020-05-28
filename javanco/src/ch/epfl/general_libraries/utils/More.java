package ch.epfl.general_libraries.utils;

public class More {

	public static int countNull(Object ... t) {
		int count = 0;
        for (Object o : t) {
            if (o == null) {
                count++;
            }
        }
		return count;
	}
}
