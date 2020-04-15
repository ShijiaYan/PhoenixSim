package edu.columbia.lrl.experiments.lwsim_generic.parallel_bucket_sort;

import java.util.Map;
import ch.epfl.general_libraries.simulation.Time;
import ch.epfl.general_libraries.utils.SimpleMap;
import edu.columbia.lrl.LWSim.application.ActionManager;

public class PBS_App_with_sct_gth extends PBS_App {
	
	
	public PBS_App_with_sct_gth(int sizeoftheproblem, DistributionType dist, int opsPerNs) {
		super(sizeoftheproblem, dist, opsPerNs);
	}
	
	public void runImpl(ActionManager communicator, int rank, Time ref) throws InterruptedException {
		//communicator.init();
		int parti = communicator.getParticipantNumber();
		
		if (rank == 0) {
			communicator.doSomeJob(ref, 10, "initial");
			// communicator.broadcastButMe(ref, rounds, sizeoftheproblem);
			for (int childIdx = 1; childIdx < parti; childIdx++){
				communicator.send(ref, "send inital chunks", sizeoftheproblem/(parti-1), childIdx%parti);
			}
		} else {
			communicator.doSomeJob(ref, 10, "initial");
		}
		
		if (rank != 0) {
			// rounds = (Integer)communicator.blockingRead(ref, 0);
			communicator.blockingRead(ref, 0);
			myProblemSize = sizeoftheproblem/(parti-1);
			
			// bucketizing
			communicator.doSomeJob(ref, complexBucketize(myProblemSize)/opsPerNs, "bucketize");
			
			// get the share distribution here
			nextStageShare = getBucketShare(parti);
			
			// send buckets
			for (int partnerIdx = 1; partnerIdx < parti; partnerIdx++){
				if (partnerIdx != rank){
					communicator.send(ref, "send buckets", (int)(myProblemSize * nextStageShare[partnerIdx]), partnerIdx%parti);
				}
			}
			
			for (int partnerIdx = 1; partnerIdx < parti; partnerIdx++){
				if (partnerIdx != rank){
					communicator.blockingRead(ref, partnerIdx%parti);
				}
			} 
			
			// serial sort
			int mySerialSortSize = (int)(nextStageShare[rank]*sizeoftheproblem);
			communicator.doSomeJob(ref, complexSerialSort(mySerialSortSize)/opsPerNs, "serial sort");
			
			// send results back to node 0
			communicator.send(ref, "send resutls", mySerialSortSize, 0);
		}
		
		// node 0 gather the results
		if (rank == 0) {
			for (int childIdx = 1; childIdx < parti; childIdx++){
				communicator.blockingRead(ref, childIdx%parti);
			}
			
			//this.execTime = ref;
		}
		
		//System.out.println("===>>> Node " + rank + " " + ref + " Done");
	}
	
	public Map<String, String> getAllParameters() {
		
		return SimpleMap.getMap("application", "PBS with gather scatter","problem size", sizeoftheproblem+"", "distribution", distribution+"");
	}
	
	
}
