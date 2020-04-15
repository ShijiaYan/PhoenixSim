package edu.columbia.lrl.LWSim.components;

import ch.epfl.javanco.network.DefaultNodeImpl;

import edu.columbia.lrl.LWSim.EventOrigin;
import edu.columbia.lrl.LWSim.TrafficDestination;

public abstract class AbstractSwitch extends DefaultNodeImpl implements TrafficDestination, EventOrigin {

	private static final long serialVersionUID = 1L;

}
