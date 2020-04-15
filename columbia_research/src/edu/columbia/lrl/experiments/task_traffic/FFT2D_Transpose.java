package edu.columbia.lrl.experiments.task_traffic;

import java.util.Map;

import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.LWSim.application.AbstractApplication;
import edu.columbia.lrl.LWSim.application.ActionManager;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.graphics.timeline.TimeLine;
import ch.epfl.general_libraries.math.Complex;
import ch.epfl.general_libraries.simulation.Time;
import ch.epfl.general_libraries.utils.SimpleMap;

public class FFT2D_Transpose extends AbstractApplication {
	
	// one float is 32 bits; two floats is 64 bits (8 bytes)
	public static final int FFT_ELEMENT_SIZE = 8;
	
	private int logN = 0;
	
	public FFT2D_Transpose(@ParamName(name="Log2 Size (logN)") int logN){
		this.logN = logN;
	}

	@Override
	public Map<String, String> getAllParameters() {
		Map<String, String> hh = SimpleMap.getMap("Log2 Size (logN)", logN + "");
		return hh;
	}

	@Override
	public void runImpl(ActionManager manager, int myRank, Time ref)
			throws InterruptedException {
		
		int n_ranks = manager.getParticipantNumber();
		int N = 1 << logN;
		
		// compute 1D FFT of own row
		// time complexity: O(n logn)
		int comp_time = N * logN;
		manager.doSomeJob(ref, comp_time, "row-wise FFT", TimeLine.EnumType.COMPUTE);
		
		// transpose
		Complex dummy = new Complex(0, 0);
		for (int offset = 1; offset < n_ranks; offset++) {
			int dest = (myRank + offset) % n_ranks;
			System.out.println("Rank " + myRank + " sending @ " + ref);
			manager.send(ref, dummy, FFT_ELEMENT_SIZE, dest, "transpose");
		}
		
		// barrier
		manager.blockingReadFromAllButOne(ref, myRank, true, 0);
	//	manager.blockingReadFromAny(ref);
		System.out.println("Rank " + myRank + " receive finished @ " + ref);
		
		// compute 1D FFT of own "row"
		// time complexity: O(n logn)
		comp_time = N * logN;
		manager.doSomeJob(ref, comp_time, "col-wise FFT", TimeLine.EnumType.COMPUTE);
		
		manager.barrierDone(ref);
		
		this.executionEnd(myRank, ref);
		
		System.out.println("Rank " + myRank + " done @ " + ref);
	}

	@Override
	public InitFeedback init(LWSIMExperiment exp) {
		return super.init(exp.getNumberOfClients());
	}

	@Override
	public void addApplicationInfoImpl(LWSIMExperiment lwSimExp, double ref,
			boolean analyseNodes) {
		// TODO Auto-generated method stub
		
	}
	
	
}
