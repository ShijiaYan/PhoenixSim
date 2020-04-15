package edu.columbia.sebastien.green_optical.simple_trans;

import java.util.Collection;
import java.util.Map;

import ch.epfl.general_libraries.results.Property;

public abstract class AbstractOpticalModel {

	public abstract double getPjPerBit(double rateInGbs);

	public abstract Map<String, String> getAllParameters();
}
