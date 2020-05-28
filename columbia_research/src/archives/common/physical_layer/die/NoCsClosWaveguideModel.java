package archives.common.physical_layer.die;

import java.util.Map;

import ch.epfl.general_libraries.utils.SimpleMap;

public class NoCsClosWaveguideModel extends AbstractDieModel {

	public NoCsClosWaveguideModel() {
	}
	
	@Override
	public double getWaveguideLengthInCm() {

		//Determine minimum chip width from stages and switch radius
		//double radius = gloco.getLightSpeed()/(2*Math.PI*gloco.getNeff()*gloco.getChannelSpacing());
		//radius += gloco.getRingDopingPaddingMicrometers()/10000; //convert to cm
		
		//XYResult wavelengthsVsRadius = new XYResult("Number of wavelengths", "Switch ring radius");
		//gloco.getResultManager().add(wavelengthsVsRadius);
		
		//wavelengthsVsRadius.add(gloco.getNumberOfWavelengths(), radius);
		
		//double chipWidth = radius * gloco.getNumStages();
		
		double longestSwitchPath_cm = 2*Math.sqrt(Math.pow(3,  2) + Math.pow(1.5, 2));
		
		return longestSwitchPath_cm;
	}

	@Override
	public Map<String, String> getAllParameters() {
		Map<String, String> map = new SimpleMap<>();
		return map;
	}

}
