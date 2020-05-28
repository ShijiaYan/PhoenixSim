package edu.columbia.lrl.CrossLayer.physical_models.ad_hoc;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;


public class SnakedBusWaveguideLengthModel extends AbstractWaveguideLengthModel {

	double chipWidth;
	
	public SnakedBusWaveguideLengthModel(
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
		
		if( numberSites <= 2 ) {
			return chipWidth;
		}
		
		int horizontals = (int)Math.sqrt(numberSites);
		
		return chipWidth*horizontals + chipWidth;
	}
	
	public int getNumBends(int numberSites) {
		int horizontals = (int)Math.sqrt(numberSites);
		return 2*(horizontals-1);
	}
	
	public String toString() {
		return "Snaked bus waveguide";
	}
} 
