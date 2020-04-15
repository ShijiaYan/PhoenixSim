package edu.columbia.lrl.experiments.task_traffic.protocols;

import edu.columbia.lrl.experiments.task_traffic.schedulers.ThreadInfo;

public class ThreadResponse {
	
	public ThreadInfo source;
	public int origin;
	
	public ThreadResponse(ThreadInfo source, int origin) {
		this.source = source;
		this.origin = origin;
	}

}
