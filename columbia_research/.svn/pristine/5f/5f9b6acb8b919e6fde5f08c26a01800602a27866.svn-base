package edu.columbia.lrl.CrossLayer.physical_models.ad_hoc;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;


public class HorizontalWaveguideLengthModel extends AbstractWaveguideLengthModel {

	private double chipWidth; //mm
	
	public HorizontalWaveguideLengthModel(
			@ParamName(name="Chip width (cm)", default_="1") double chipWidth
			) {
		this.chipWidth = chipWidth;
	}
		
	@Override
	public Map<String, String> getAllParameters() {
		return SimpleMap.getMap("Chip width (mm)", chipWidth+"");
	}

	public double getWaveguideLengthCm(int numberSites) {
		//Assume number of sites is a power of 2
		//TODO: make that assertion in code
		
		return chipWidth; //cm
	}
	
	public int getNumBends(int numberSites) {
		return 0;
	}
	
	public String toString() {
		return "Horizontal waveguide";
	}

}
