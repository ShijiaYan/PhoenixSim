package edu.columbia.lrl.LWSim;

public class InitFeedback {
	
	String failureReason;
	
	public InitFeedback(String s) {
		this.failureReason = s;
	}
	
	public InitFeedback() {
		failureReason = null;
	}

}
