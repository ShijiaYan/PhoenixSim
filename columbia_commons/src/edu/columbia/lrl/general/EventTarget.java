package edu.columbia.lrl.general;

public interface EventTarget {
	
	public void processEvent(Evt e);

	public Object toShortString();

}
