//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package edu.columbia.lrl.CrossLayer.physical_models.util;

import java.util.Map;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;


public class Constants {

	public static final double SPEED_OF_LIGHT = 3.0E8D;
	private double fullFSR;
	private double centerWavelength;
	public static final double ELECTRON_CHARGE = 1.6022E-19D;
	private double centerFrequency;

	public Constants(@ParamName(name = "Full FSR (nm)", default_ = "25.6") double fullFSR,
			@ParamName(name = "Center Wavelength(nm)", default_ = "1550") double centerWavelength) {
		this.fullFSR = fullFSR * 1.0E-9D;
		this.centerWavelength = centerWavelength * 1.0E-9D;
		this.centerFrequency = 3.0E8D / centerWavelength * 1.0E9D;
	}

	public Map<String, String> getAllParameters() {
		Map<String, String> map = new SimpleMap<>();
		map.put("Speed of light", "3.0E8");
		map.put("Full FSR(nm)", String.valueOf(this.fullFSR * 1.0E9D));
		map.put("Center wavelength(nm)", String.valueOf(this.centerWavelength * 1.0E9D));
		return map;
	}

	public double wavelengthsToFreqSpacing(int wavelengths) {
		return this.fullFSR / (double) wavelengths * (3.0E8D / Math.pow(this.centerWavelength, 2.0D));
	}

	public static double wavelengthToFreq(double wavelengthNm) {
		return 3.0E17D / wavelengthNm;
	}

	public static double freqToWavelength(double freq) {
		return 3.0E17D / freq;
	}

	public double getSpeedOfLight() {
		return 3.0E8D;
	}

	public double getFullFSR() {
		return this.fullFSR;
	}

	public double getCenterWavelength() {
		return this.centerWavelength;
	}

	public double getCenterFrequency() {
		return this.centerFrequency;
	}

	public double e() {
		return 1.6022E-19D;
	}

	public void setFullFSR(double fullFSR) {
		this.fullFSR = fullFSR;
	}
}
