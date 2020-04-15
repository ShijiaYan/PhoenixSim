package edu.columbia.lrl.LWSim.application;

import java.util.Map;

import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.LWSim.LWSIMExperiment;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.graphics.timeline.TimeLine;
import ch.epfl.general_libraries.simulation.Time;
import ch.epfl.general_libraries.utils.SimpleMap;

public class TestApplication extends AbstractApplication {
	
	private int rounds;
	private int messageSize;
	private int duration;
	private int brodSize = 40;
	
	public TestApplication(int rounds) {
		this.rounds = rounds;
		this.messageSize = 20;
		this.duration = 40;
	}
	
	public TestApplication(@ParamName(name="Rounds") long rounds,
			@ParamName(name="CPU capability (GFLOPS)", default_="40") int gflops,
			   @ParamName(name="Job size (KFLOPS)") double jobSize,
			   @ParamName(name="Verbosity (byte/flop)") double verb
			) {
		this.rounds = (int)rounds;
		this.duration = (int)(jobSize*1000d/(double)gflops);
		this.messageSize = (int)(jobSize*verb*8);
		
	}
	
	public TestApplication(@ParamName(name="Rounds") int rounds,
						   @ParamName(name="Message size (KB)") int messageSize,
						   @ParamName(name="Job duration (ns)") int duration) {
		this.rounds = rounds;
		this.messageSize = messageSize * 8000;
		this.duration = duration;		
		this.brodSize = messageSize;
	}
	
	public TestApplication(@ParamName(name="Rounds") int rounds,
			   @ParamName(name="Message size (KB)") int messageSize,
			   @ParamName(name="Job duration (ns)") int duration,
			   @ParamName(name="Initial broadcast size (KB)") int brodSize) {
		this.rounds = rounds;
		this.messageSize = messageSize * 8000;
		this.duration = duration;		
		this.brodSize = brodSize * 8000;
	}	
	
	public InitFeedback  init(LWSIMExperiment exp) {
		return super.init(exp.getNumberOfClients());
	} 
	
	public void addApplicationInfoImpl(LWSIMExperiment lwSimExp, double ref, boolean detailed) {}	
	
	public void runImpl(ActionManager manager, int rank, Time ref) throws InterruptedException {
		
		//manager.init();
		int participants = manager.getParticipantNumber();

		if (rank == 0) {
			manager.doSomeJob(ref, 10, "initial", TimeLine.EnumType.INIT);
			manager.broadcastButMe(ref, rounds, brodSize);
		} else {
			manager.doSomeJob(ref, 10, "initial", TimeLine.EnumType.INIT);
		}
		if (rank != 0) {
			manager.blockingRead(ref, 0);
		}
		int jobId = 0;

		for (int i = 0 ; i < rounds ; i++) {
			
				manager.doSomeJob(ref, duration, "job" + jobId, TimeLine.EnumType.COMPUTE);	

				manager.send(ref, "test", messageSize, (rank+i+1)%participants);			
				//manager.blockingRead(ref, (rank + (2*participants) - i)%participants);
				manager.blockingReadFromAny(ref);				
			//	manager.doSomeJob(ref, duration, "agregate results", TimeLine.EnumType.AGGREGATE);
				jobId++;
		}
		if (rank == 0) {
			manager.blockingRead(ref, 0);
		}
		
		this.executionEnd(rank, ref);
		
		System.out.println(rank + " " + ref);
	}
	
	public Map<String, String> getAllParameters() {
		return SimpleMap.getMap("application", "test application", "rounds", rounds+"","job size", duration+"", "app msg size", messageSize+"",
				"Broadcast msg size", brodSize+"");
	}
	
	@Override
	public int getNumberOfClients() {
		throw new IllegalStateException("This application generator doesn't provide the number of clients");
	}	
	
	
}
