package edu.columbia.lrl.CrossLayer.physical_models.devices.FP;

import java.util.Map;

import ch.epfl.general_libraries.utils.SimpleMap;
import ch.epfl.general_libraries.clazzes.ParamName;

public class LaserModel {

	// parameters
	private double laserPower ;
	private double efficiency ;
	private double tuningTime ;


	public LaserModel(
			@ParamName(name = "Laser Power (dBm)", default_ = "10") double laserPower,
			@ParamName(name = "efficiency (%)", default_ = "10") double efficiency,
			@ParamName(name = "Tuning time (nsec)", default_ = "5") double tuningTime
			) {
		this.laserPower = laserPower ;
		this.efficiency = efficiency ;
		this.tuningTime = tuningTime ;
		
	}

	// Write the map

	public Map<String, String> getAllParameters() {
		// TODO Auto-generated method stub
		Map<String, String> map = new SimpleMap<>();
		map.put("Wall-plug Efficiency (%)", efficiency + "");
		return map;
	}

	public double getLaserPowerdBm() {
		return laserPower ;
	}

	public double getLaserefficiency(){
		return efficiency/100 ;
	}
		
	public double getLaserTuningTime(){
		return tuningTime ;
	}
	
	// power consumption

}
