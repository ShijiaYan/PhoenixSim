package edu.columbia.lrl.CrossLayer.physical_models.devices.FP;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName; 
import ch.epfl.general_libraries.utils.SimpleMap;

public class Constants {
	
	//Parameters
	private double speedOfLight;
	private double fullFSR;
	private double centerWavelength;
	private double dataRate; 
	private double effectiveIndex;
	private double waveguideCrossingWidth;
	private double extraWaveguideLength; //Extra length of waveguide due to some layout routing that is current unknown
	private final double electronCharge = 1.6022e-19; //C
	
	//Calculated
	private double centerFrequency;
	
	// Constructor for initializing the parameters
	public Constants(
			@ParamName(name="Speed of light (m/s)", default_="3e8") double speedOfLight,
			@ParamName(name="Full FSR (m)", default_="50e-9") double fullFSR,
			@ParamName(name="Center wavelength (m)", default_="1550e-9") double centerWavelength
//			@ParamName(name="Data rate (bits/s)", default_="10e9") double dataRate,
//			@ParamName(name="effective index", default_="4.23209") double effectiveIndex,
//			@ParamName(name="Waveguide crossing width (cm)", default_=".001") double waveguideCrossingWidth,
//			@ParamName(name="Extra waveguide length (cm)", default_="1") double extraWaveguideLength
			) {
		this.speedOfLight = speedOfLight;
		this.fullFSR = fullFSR;
		this.centerWavelength = centerWavelength;
		this.dataRate = dataRate;
		this.effectiveIndex = effectiveIndex;
		this.waveguideCrossingWidth = waveguideCrossingWidth;
		this.extraWaveguideLength = extraWaveguideLength;
		
		this.centerFrequency = speedOfLight/centerWavelength;
	}
	
	// Mapping the parameters, need to convert to strings. Comes from the super class (AbstractDeviceModel)
	public Map<String, String> getAllParameters() {
		Map<String, String> map = new SimpleMap<String, String>();
		map.put("Speed of light", speedOfLight+"");
		map.put("Full fsr", fullFSR+"");
		map.put("Center wavelength", centerWavelength+"");
		map.put("Data rate", dataRate+"");
		map.put("Switch ring effective index", effectiveIndex+"");
		map.put("Waveguide crossing width (cm)", waveguideCrossingWidth+"");
		return map;
	}
	
	public double wavelengthsToChannelSpacing(int wavelengths) {
		return fullFSR * (speedOfLight/ (wavelengths*Math.pow(centerWavelength,2))); 
	}
	
	public double getSpeedOfLight() {
		return speedOfLight;
	}

	public double getFullFSR() {
		return fullFSR;
	}

	public double getCenterWavelength() {
		return centerWavelength;
	}

	public double getCenterFrequency() {
		return centerFrequency;
	}
	
/*	public static double getDataRate() {
		checkInit();
		return instance.dataRate;
	}*/
	
/*	public static void overrideDataRate(double dataRate) {
		instance.dataRate = dataRate;
	}*/
	
/*	public double getSwitchRingEffectiveIndex() {
		return switchEffectiveIndex;
	}*/
	
	public double getWaveguideCrossingWidthCm() {
		return waveguideCrossingWidth;
	}
	
	public double getExtraWaveguideLength() {
		return extraWaveguideLength;
	}
	
	public double e() {
		return electronCharge;
	}

	// The value of effectiveIndex is not consistent with the one used in modulators and ring filters. Need to resolve this.
	public double getEffectiveIndex() {
		return effectiveIndex;
	}
}
