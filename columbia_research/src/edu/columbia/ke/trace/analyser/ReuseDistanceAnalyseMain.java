package edu.columbia.ke.trace.analyser;

import java.io.IOException;

public class ReuseDistanceAnalyseMain {

	public ReuseDistanceAnalyseMain() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		int nRank = 256;
		int minOccurance = 0;
		/* Mode parameters:
		 * "STACK" means non-unique
		 * "UNIQUE" means unique
		 */
		StackReuseDistanceAnalyser analyser = new StackReuseDistanceAnalyser("multigrid_large_do_isend_256", nRank, "STACK");
		System.out.println("starting anaysis ...");
		analyser.analyse();
		System.out.println("writing to files ...");
		for (int i = 0; i < nRank; i++)
			for (int j = 0; j < nRank; j++)
				analyser.dumpReuseDistance(i, j, "ALL", minOccurance);	// can choose hashed or not hashed
		System.out.println("done writing. exiting. ");
	}

}

/* for UCL traces, use THRESHOLD mode and Hashed dumpReuseDistance methods */
