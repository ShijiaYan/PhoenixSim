package edu.columbia.lrl.experiments.lwsim_generic.parallel_bucket_sort;

import java.util.Map;
import java.util.Random;

import ch.epfl.general_libraries.simulation.Time;
import ch.epfl.general_libraries.utils.SimpleMap;

import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.LWSim.application.AbstractApplication;
import edu.columbia.lrl.LWSim.application.ActionManager;

public class PBS_App extends AbstractApplication {
	
	protected int sizeoftheproblem;
	protected int opsPerNs;
	protected int myProblemSize;
	protected DistributionType distribution;
	protected double[] nextStageShare;
	
	public PBS_App(int sizeoftheproblem, DistributionType dist, int opsPerNs) {
		this.sizeoftheproblem = sizeoftheproblem;
		this.opsPerNs = opsPerNs;
		this.myProblemSize = 1;
		this.distribution = dist;
	}
	
	public void addApplicationInfoImpl(LWSIMExperiment lwSimExp, double ref, boolean d) {}	
	
	public void runImpl(ActionManager communicator, int rank, Time ref) throws InterruptedException {

		int parti = communicator.getParticipantNumber();
		myProblemSize = sizeoftheproblem/(parti);

		// bucketizing
		communicator.doSomeJob(ref, complexBucketize(myProblemSize)/opsPerNs, "bucketize");

		// get the share distribution here
		nextStageShare = getBucketShare(parti);

		// send buckets
		for (int partnerIdx = 0; partnerIdx < parti; partnerIdx++){
			if (partnerIdx != rank){
				communicator.send(ref, "send buckets", (int)(myProblemSize * nextStageShare[partnerIdx]), partnerIdx%parti);
			}
		}
		// receive buckets
		for (int partnerIdx = 0; partnerIdx < parti; partnerIdx++){
			if (partnerIdx != rank){
				communicator.blockingRead(ref, partnerIdx%parti);
			}
		} 

		// serial sort
		int mySerialSortSize = (int)(nextStageShare[rank]*sizeoftheproblem);
		communicator.doSomeJob(ref, complexSerialSort(mySerialSortSize)/opsPerNs, "serial sort");
		System.out.println(complexSerialSort(mySerialSortSize)/opsPerNs);
		System.out.println("===>>> Node " + rank + " " + ref + " Done");
	}
	
	public double complexBucketize(int n){
		return (double)n;
	}
	
	public double complexSerialSort(int n){
		return (double)(n*Math.log(n));
		// return (double)(Math.pow(n, 1.3));
	}
	
	public double[] getBucketShare(int parti){
		double[] nextStageShare = new double[parti];
		Random randShareGen = new Random();
		double tmpShareSum = 0;
		switch (this.distribution){
		case HOTSPOT_EXP:
			for (int k = 0; k < parti; k++){
				nextStageShare[k] = Math.pow(1.5, -k);
				tmpShareSum += nextStageShare[k];
			}
			break;
		case RANDOM_BIN:
			for (int k = 0; k < parti; k++){
				nextStageShare[k] = randShareGen.nextDouble();
				tmpShareSum += nextStageShare[k];
			}
			break;
		default:
			double uniShare = 1/(double)(parti);
			for (int k = 0; k < parti; k++){
				nextStageShare[k] = uniShare;
				tmpShareSum += nextStageShare[k];
			}
			break;
		}
		
		// scale the share so that they sum up to 1
		double scale = 1/tmpShareSum;
		for (int k = 0; k < parti; k++){
			nextStageShare[k] = nextStageShare[k] * scale;
		//	System.out.println(" --> " + k + ": " + nextStageShare[k]);
		}
		
		return nextStageShare;
	}
	
	public Map<String, String> getAllParameters() {
		return SimpleMap.getMap("application", "PBS_2_Stage","problem size", sizeoftheproblem+"", "distribution", distribution+"");
	}
	
	@Override
	public int getNumberOfClients() {
		throw new IllegalStateException("This application generator doesn't provide the number of clients");
	}

	@Override
	public InitFeedback  init(LWSIMExperiment exp) {return null;} 	
}
