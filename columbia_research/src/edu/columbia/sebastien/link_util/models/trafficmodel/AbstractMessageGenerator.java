package edu.columbia.sebastien.link_util.models.trafficmodel;

import java.util.Map;

import ch.epfl.general_libraries.random.PRNStream;

public abstract class AbstractMessageGenerator {
	
	public abstract void init();
	
	public abstract Map<String, String> getAllParameters();
	
	public abstract double nextExpNS(PRNStream stream);
	
	public abstract int getMessageBits();

}
