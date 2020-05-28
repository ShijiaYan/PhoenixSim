package edu.columbia.lrl.experiments.lwsim_generic.parallel_bucket_sort;

import java.util.Map;

import ch.epfl.general_libraries.simulation.Time;
import ch.epfl.general_libraries.utils.SimpleMap;

import edu.columbia.lrl.LWSim.application.ActionManager;

public class PBSWithStreaming extends PBS_App {
	
	private int granularity;

	public PBSWithStreaming(int sizeoftheproblem, DistributionType dist, int granularity, int opsPerNs) {
		super(sizeoftheproblem, dist, opsPerNs);
		this.granularity = granularity;
	}
	
	@Override
	public void runImpl(ActionManager communicator, int rank, Time ref) throws InterruptedException {
		int parti = communicator.getParticipantNumber();
		
		// get the share distribution here
		nextStageShare = getBucketShare(parti);		

			myProblemSize = sizeoftheproblem/ parti;
			
			int parcel = (int)((double)myProblemSize/(double)granularity);
			

			for (int i = 0 ; i < granularity ; i++) {
				// bucketizing
				communicator.doSomeJob(ref, complexBucketize(parcel)/opsPerNs, "bucketize #" + i);

				// send buckets
				for (int partnerIdx = 0; partnerIdx < parti; partnerIdx++){
					if (partnerIdx != rank){
						communicator.send(ref, "send buckets #" + i, (int)(parcel * nextStageShare[partnerIdx]), partnerIdx%parti);
					}
				}			
			}
			
			// serial sort
			int mySerialSortSize = (int)(nextStageShare[rank]*sizeoftheproblem);			
			
			int sortId = 0;
			for (int partnerIdx = 0; partnerIdx < parti*granularity; partnerIdx++){
				if (partnerIdx % parti != rank){
					communicator.blockingReadFromAny(ref);
					communicator.doSomeJob(ref, (complexSerialSort(mySerialSortSize) / opsPerNs) / (granularity * (parti - 1)), "serial sort #"+sortId );
					System.out.println((complexSerialSort(mySerialSortSize) / opsPerNs) / (granularity * (parti - 1)));
					sortId++;
				}
			} 
			


		
	//	System.out.println("===>>> Node " + rank + " " + ref + " Done");
	}	
	
	public Map<String, String> getAllParameters() {
		return SimpleMap.getMap("application", "PBS_2_Stage_streaming","problem size", sizeoftheproblem+"", "distribution", distribution+"", "granularity", granularity+"");
	}	

}
