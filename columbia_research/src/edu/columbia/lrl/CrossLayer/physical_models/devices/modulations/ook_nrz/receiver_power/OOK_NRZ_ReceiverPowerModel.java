//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package edu.columbia.lrl.CrossLayer.physical_models.devices.modulations.ook_nrz.receiver_power;

import java.util.Map;
import ch.epfl.general_libraries.math.MoreMaths;
import ch.epfl.general_libraries.results.PropertyMap;


public class OOK_NRZ_ReceiverPowerModel {

	private double pStatic;
	private double oneQuarterCVSquare;

	protected OOK_NRZ_ReceiverPowerModel(double pStatic, double oneQuarterCVSquare) {
		this.pStatic = pStatic;
		this.oneQuarterCVSquare = oneQuarterCVSquare;
	}

	public double getPowerConsumptionMW(double bitrate) {
		double bitrateGbs = bitrate / 1.0E9D;
		double cothArg = 1.5707963267948965E10D / bitrate;
		double second = Math.pow(MoreMaths.coth(cothArg), 2.0D) * this.oneQuarterCVSquare;
		double first = this.pStatic;
		return first + second * bitrateGbs;
	}

	public Map<? extends String, ? extends String> getAllParameters() {
		PropertyMap m = new PropertyMap();
		m.put("model type", this.getClass().getSimpleName());
		m.put("Receiver static power (mW)", this.pStatic);
		return m;
	}

	public static class Agressive extends OOK_NRZ_ReceiverPowerModel {

		public Agressive() {
			super(3.6757D, 0.1783D);
		}
	}

	public static class Conservative extends OOK_NRZ_ReceiverPowerModel {

		public Conservative() {
			super(5.9459D, 0.2283D);
		}
	}

	public static class Constant extends OOK_NRZ_ReceiverPowerModel {

		public Constant(double pStatic) {
			super(pStatic, 0.0D);
		}
	}
}
