package edu.columbia.sebastien.green_optical.elecSERDES;

import java.util.Map;

public abstract class AbstractSERDESModel {
	
	public abstract double getPjPerBit(double rateInGbs);
	
	public abstract Map<String, String> getAllParameters();
	

}
