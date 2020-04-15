package edu.columbia.lrl.CrossLayer.physical_models.devices;

import java.util.Map;

public abstract class AbstractTunableLaserModel extends AbstractLaserModel {
	
	public abstract double getTuningTimeNs();
	public abstract Map<String, String> getAllParameters();

}
