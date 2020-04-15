//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package edu.columbia.lrl.CrossLayer.physical_models.devices.interleaver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.results.Execution;
import ch.epfl.general_libraries.utils.MoreArrays;
import ch.epfl.general_libraries.utils.SimpleMap;
import edu.columbia.lrl.CrossLayer.PowerConsumption;
import edu.columbia.lrl.CrossLayer.physical_models.PhysicalParameterAndModelsSet;
import edu.columbia.lrl.CrossLayer.physical_models.PowerPenalty;
import edu.columbia.lrl.CrossLayer.physical_models.util.AbstractLinkFormat;


public class Interleaver extends AbstractInterleaver {

	private final double leaverIL;
	private final double leaverXTalk;
	private final int leaverChannel;
	private final double leaverStaticPower;

	public Interleaver(@ParamName(name = "Insertion Loss(dB)", default_ = "1.4") double leaverIL,
			@ParamName(name = "Number of Interleaver Groups", default_ = "4") int leaverChannel,
			@ParamName(name = "Crosstalk(dB)", default_ = "-20") double leaverXTalk,
			@ParamName(name = "Interleaver Tuning Power(mW)", default_ = "0") double leaverStaticPower) {
		this.leaverIL = leaverIL;
		this.leaverXTalk = leaverXTalk;
		this.leaverChannel = leaverChannel;
		this.leaverStaticPower = leaverStaticPower;
	}

	public Interleaver(@ParamName(name = "Insertion Loss(dB)", default_ = "1") double leaverIL,
			@ParamName(name = "Number of Interleaver Groups", default_ = "4") int leaverChannel,
			@ParamName(name = "Crosstalk(dB)", default_ = "-20") double leaverXTalk) {
		this(leaverIL, leaverChannel, leaverXTalk, 0.0D);
	}

	public Map<String, String> getAllParameters() {
		Map<String, String> map = new SimpleMap<>();
		map.put("Crosstalk(dB)", String.valueOf(this.leaverXTalk));
		map.put("Interleaver Groups", String.valueOf(this.leaverChannel));
		map.put("Interleaver Insertion Loss", String.valueOf(this.leaverIL));
		return map;
	}

	public List<PowerConsumption> getDevicePowerConsumptions(PhysicalParameterAndModelsSet modelSet,
			AbstractLinkFormat linkFormat) {
		ArrayList<PowerConsumption> pc = new ArrayList<>(0);
		pc.add(new PowerConsumption("InterleaverTuning", false, false, false, this.leaverStaticPower));
		return pc;
	}

	public ArrayList<PowerPenalty> getPowerPenalties(PhysicalParameterAndModelsSet modelSet,
			AbstractLinkFormat linkFormat, Execution ex, double modulationER) {
		double xtalk = -10.0D * Math.log10(1.0D - 10.0D * Math.pow(10.0D, this.leaverXTalk / 2.0D));
		double totalLeaverLoss = (this.leaverIL + xtalk) * 3.0D;
		PowerPenalty interleaverTotalLoss = new PowerPenalty("Interleaver Penalty", "Interleaver", totalLeaverLoss);
		return MoreArrays.getArrayList(new PowerPenalty[] { interleaverTotalLoss });
	}

	public boolean hasThroughCapability() {
		return true;
	}

	public ArrayList<PowerPenalty> getPassbyPowerPenalties(PhysicalParameterAndModelsSet modelSet,
			AbstractLinkFormat linkFormat) {
		return null;
	}

	public double getLeaverIL() {
		return this.leaverIL;
	}

	public double getLeaverXTalk() {
		return this.leaverXTalk;
	}

	public int getLeaverChannel() {
		return this.leaverChannel;
	}

	public double getLeaverStaticPower() {
		return this.leaverStaticPower;
	}

	public int getPortNumbers() {
		return this.leaverChannel;
	}
}
