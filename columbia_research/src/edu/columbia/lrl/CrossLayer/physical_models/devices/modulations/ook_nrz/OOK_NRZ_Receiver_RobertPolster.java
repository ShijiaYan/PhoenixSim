//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package edu.columbia.lrl.CrossLayer.physical_models.devices.modulations.ook_nrz;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.general_libraries.results.Execution;
import ch.epfl.general_libraries.results.PropertyMap;
import ch.epfl.general_libraries.utils.MoreArrays;
import ch.epfl.general_libraries.utils.TypeParser;
import ch.epfl.javancox.results_manager.SmartDataPointCollector;
import ch.epfl.javancox.results_manager.gui.DefaultResultDisplayingGUI;
import edu.columbia.lrl.CrossLayer.PowerConsumption;
import edu.columbia.lrl.CrossLayer.physical_models.PhysicalParameterAndModelsSet;
import edu.columbia.lrl.CrossLayer.physical_models.PowerPenalty;
import edu.columbia.lrl.CrossLayer.physical_models.devices.modulations.Abstract_OOK_SERDES_PowerModel;
import edu.columbia.lrl.CrossLayer.physical_models.devices.modulations.OOK_SERDES_PowerModel;
import edu.columbia.lrl.CrossLayer.physical_models.util.AbstractLinkFormat;


public class OOK_NRZ_Receiver_RobertPolster extends Abstract_OOK_NRZ_Receiver {

	private static String fileName28 = "data/DataS28PrecVsBRVsIin.csv";
	private static String fileName65 = "data/DataS65PrecVsBRVsIin.csv";
	private TreeMap<Float, TreeMap<Float, Float>> db = new TreeMap<>();
	private double responsivity;
	private double polarizationLoss;
	private OOK_NRZ_Receiver_RobertPolster.Techno techno;

	public OOK_NRZ_Receiver_RobertPolster(OOK_NRZ_Receiver_RobertPolster.Techno t,
			@ParamName(name = "Responsivity (A/W)", default_ = "1") double responsivity,
			@ParamName(name = "Polarization loss (dB)", default_ = "0.5") double polarizationLoss,
			@ParamName(name = "SERDES model") Abstract_OOK_SERDES_PowerModel serdes) {
		super(0.0D, serdes);
		this.responsivity = responsivity;
		this.polarizationLoss = polarizationLoss;
		this.techno = t;
		switch (t) {
			case _28nm:
				parseFile(fileName28, this, (AbstractResultsManager) null);
				break;
			case _65nm:
				parseFile(fileName65, this, (AbstractResultsManager) null);
		}

	}

	public Map<String, String> getAllReceiverParameters() {
		PropertyMap m = new PropertyMap();
		m.put("Polarization loss", this.polarizationLoss);
		m.put("Responsivity", this.responsivity);
		m.put("Receiver techno", this.techno.toString());
		return m;
	}

	public ArrayList<PowerConsumption> getDemodulationAndReceptionPowerConsumptions(double opticalPowerAtReceiverdBm,
			PhysicalParameterAndModelsSet modelSet, AbstractLinkFormat linkFormat) {
		ArrayList<PowerConsumption> superPowerCons = super.getDemodulationAndReceptionPowerConsumptions(
				opticalPowerAtReceiverdBm, modelSet, linkFormat);
		double inputCurrent = this.opticalPowerdBmToCurrent_A(opticalPowerAtReceiverdBm);
		double powCon = this.getPowerConsumptionMW(linkFormat.getWavelengthRate(), inputCurrent);
		PowerConsumption pc = new PowerConsumption("Receiver", false, false, true, powCon);
		superPowerCons.add(pc);
		return superPowerCons;
	}

	public double getReceiverSensitivity(PhysicalParameterAndModelsSet modeSet, AbstractLinkFormat format) {
		double bitrate = format.getWavelengthRate();
		Iterator var6 = this.db.entrySet().iterator();

		while (var6.hasNext()) {
			Entry<Float, TreeMap<Float, Float>> ent = (Entry) var6.next();
			double maxBitRateOfCurrent = (double) (Float) ((TreeMap) ent.getValue()).lastKey();
			if (maxBitRateOfCurrent > bitrate) {
				double optPow = this.currentToOpticalPowerdBm((double) (Float) ent.getKey() * 1000000.0D);
				return optPow;
			}
		}

		throw new WrongExperimentException("Bit rate beyond receiver capabilities");
	}

	public ArrayList<PowerPenalty> getDemodulationPowerPenalties(PhysicalParameterAndModelsSet modelSet,
			AbstractLinkFormat linkFormat) {
		PowerPenalty pola = new PowerPenalty("Polarization loss", "Demodulator/Receiver", this.polarizationLoss);
		return MoreArrays.getArrayList(new PowerPenalty[] { pola });
	}

	private static void parseFile(String filename, OOK_NRZ_Receiver_RobertPolster db, AbstractResultsManager man) {
		Execution exec = new Execution();

		try {
			InputStream input = OOK_NRZ_Receiver_RobertPolster.class.getResourceAsStream(filename);
			BufferedReader br = new BufferedReader(new InputStreamReader(input, "UTF-8"));
			String line = "";

			while (true) {
				if ((line = br.readLine()) == null) { br.close(); break; }

				String[] bits = line.split(",");
				String[] first = bits[0].split("/");
				float currentF = Float.parseFloat(first[0]) / Float.parseFloat(first[1]);
				float bitRateS = Float.parseFloat(bits[1]);
				float pJperBitT = Float.parseFloat(bits[2]);
				if (man != null) {
					DataPoint dp = new DataPoint();
					dp.addProperty("current", (double) currentF);
					dp.addProperty("bitrate", (double) bitRateS);
					dp.addResultProperty("power pJ/bit", (double) pJperBitT * 1.0E12D);
					if (Double.isNaN((double) pJperBitT)) { System.out.println("."); }

					if (Double.isInfinite((double) pJperBitT)) { System.out.println("."); }

					exec.addDataPoint(dp);
				}

				if (db != null) {
					if (Double.isNaN((double) pJperBitT)) { System.out.println("."); }

					if (!Double.isInfinite((double) pJperBitT)) { db.insert(currentF, bitRateS, pJperBitT); }
				}
			}
		} catch (IOException var13) {
			throw new IllegalStateException(var13);
		}

		if (man != null) { man.addExecution(exec); }

	}

	private void insert(float f, float s, float t) {
		TreeMap<Float, Float> forF = (TreeMap) this.db.get(f);
		if (forF == null) { forF = new TreeMap(); this.db.put(f, forF); }

		forF.put(s, t);
	}

	public double getPowerConsumptionMW(double bitrate, double iCurrent) {
		float inputCurrent = (float) iCurrent;
		Entry<Float, TreeMap<Float, Float>> bottomCurrent = this.db.floorEntry(inputCurrent);
		Entry<Float, TreeMap<Float, Float>> topCurrent = this.db.higherEntry(inputCurrent);
		if (bottomCurrent == null) {
			throw new WrongExperimentException("Current too low");
		} else {
			if (topCurrent == null) { System.out.println("Warning: current too high"); topCurrent = bottomCurrent; }

			Entry<Float, Float> bottomCurrentBottomBitrate = ((TreeMap) bottomCurrent.getValue())
					.floorEntry((float) bitrate);
			Entry<Float, Float> bottomCurrentTopBitrate = ((TreeMap) bottomCurrent.getValue())
					.higherEntry((float) bitrate);
			Entry<Float, Float> topCurrentBottomBitrate = ((TreeMap) topCurrent.getValue()).floorEntry((float) bitrate);
			Entry<Float, Float> topCurrentTopBitrate = ((TreeMap) topCurrent.getValue()).higherEntry((float) bitrate);
			if (bottomCurrentBottomBitrate == null) {
				bottomCurrentBottomBitrate = bottomCurrentTopBitrate;
				System.out.println("Warning: bitrate " + bitrate
						+ " too low for correct receiver power estimation (using value for "
						+ bottomCurrentTopBitrate.getKey() + ")");
			}

			if (bottomCurrentTopBitrate == null) {
				throw new WrongExperimentException("Too high bitrate for current");
			} else {
				if (topCurrentBottomBitrate == null) {
					topCurrentBottomBitrate = topCurrentTopBitrate;
					System.out.println("Warning: current potentially too high for bitrate");
				}

				if (topCurrentTopBitrate == null) {
					throw new IllegalStateException("Should not get there");
				} else {
					float x1 = (Float) bottomCurrent.getKey();
					float x2 = (Float) topCurrent.getKey();
					float fx;
					if (x1 != x2) {
						fx = (inputCurrent - x1) / (x2 - x1);
					} else {
						fx = 0.0F;
					}

					float y1 = (Float) bottomCurrentBottomBitrate.getKey();
					float y2 = (Float) topCurrentBottomBitrate.getKey();
					float y3 = (Float) bottomCurrentTopBitrate.getKey();
					float y4 = (Float) topCurrentTopBitrate.getKey();
					float z1 = (Float) bottomCurrentBottomBitrate.getValue();
					float z2 = (Float) topCurrentBottomBitrate.getValue();
					float z3 = (Float) bottomCurrentTopBitrate.getValue();
					float z4 = (Float) topCurrentTopBitrate.getValue();
					float fy1;
					if (y3 != y1) {
						fy1 = ((float) bitrate - y1) / (y3 - y1);
					} else {
						fy1 = 0.0F;
					}

					float fy2;
					if (y4 != y2) {
						fy2 = ((float) bitrate - y2) / (y4 - y2);
					} else {
						fy2 = 0.0F;
					}

					float fza = z1 + fy1 * (z3 - z1);
					float fzb = z2 + fy2 * (z4 - z2);
					float finalZ = fza + fx * (fzb - fza);
					return (double) finalZ * bitrate * 1000.0D;
				}
			}
		}
	}

	private double currentToOpticalPowerdBm(double currentMicroA) {
		return 10.0D * Math.log10(currentMicroA / this.responsivity * 0.001D);
	}

	private double opticalPowerdBmToCurrent_A(double dBm) {
		return Math.pow(10.0D, dBm / 10.0D) * this.responsivity * 0.001D;
	}

	public static void main(String[] args) {
		test1();
	}

	public static void test1() {
		SmartDataPointCollector col = new SmartDataPointCollector();
		Execution e = new Execution();
		OOK_NRZ_Receiver_RobertPolster.Techno[] var5;
		int var4 = (var5 = OOK_NRZ_Receiver_RobertPolster.Techno.values()).length;

		for (int var3 = 0; var3 < var4; ++var3) {
			OOK_NRZ_Receiver_RobertPolster.Techno t = var5[var3];
			DataPoint d = new DataPoint();
			d.addProperty("Techno", t.toString());
			OOK_NRZ_Receiver_RobertPolster rec = new OOK_NRZ_Receiver_RobertPolster(t, 1.0D, 0.5D,
					new OOK_SERDES_PowerModel());
			double[] var11;
			int var10 = (var11 = TypeParser.parseDouble("1:40lin200")).length;

			for (int var9 = 0; var9 < var10; ++var9) {
				Double bitRateInGbs = var11[var9];
				float[] var15;
				int var14 = (var15 = TypeParser.parseFloat("1:250log500")).length;

				for (int var13 = 0; var13 < var14; ++var13) {
					Float currentMicroA = var15[var13];

					try {
						DataPoint dp = d.getDerivedDataPoint();
						double pow = rec.getPowerConsumptionMW(bitRateInGbs * 1.0E9D,
								(double) (currentMicroA * 1.0E-6F));
						dp.addProperty("input optical power (uW)", (double) currentMicroA / rec.responsivity);
						dp.addProperty("input optical power (dBm)",
								rec.currentToOpticalPowerdBm((double) currentMicroA));
						dp.addProperty("current (uA)", (double) currentMicroA);
						dp.addProperty("bitrate (gbs)", bitRateInGbs);
						dp.addResultProperty("power", pow);
						e.addDataPoint(dp);
					} catch (Exception var19) {
						System.out.println(var19.getMessage());
					}
				}
			}
		}

		col.addExecution(e);
		DefaultResultDisplayingGUI.displayDefault(col);
	}

	public static void test65nm() {
		SmartDataPointCollector col = new SmartDataPointCollector();
		parseFile(fileName65, (OOK_NRZ_Receiver_RobertPolster) null, col);
		DefaultResultDisplayingGUI.displayDefault(col);
	}

	public static enum Techno {
		_28nm, _65nm;

		private Techno() {
		}
	}
}
