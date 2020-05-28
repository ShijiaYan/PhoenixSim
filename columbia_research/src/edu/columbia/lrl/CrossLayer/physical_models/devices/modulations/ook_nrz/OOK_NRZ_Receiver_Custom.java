package edu.columbia.lrl.CrossLayer.physical_models.devices.modulations.ook_nrz;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class OOK_NRZ_Receiver_Custom extends Abstract_OOK_NRZ_Receiver {
    private TreeMap<Float, TreeMap<Float, Float>> db = new TreeMap<>();
    private double responsivity;
    private double polarizationLoss;
    private String fileName;

    public OOK_NRZ_Receiver_Custom(@ParamName(name = "Receiver data file") String fileName,
                                   @ParamName(name = "Responsivity(A/W)", default_ = "1") double responsivity,
                                   @ParamName(name = "Polarization loss(dB)", default_ = "0.5") double polarizationLoss,
                                   @ParamName(name = "SERDES model") Abstract_OOK_SERDES_PowerModel serdes
    ) {
        super(0.0D, serdes);
        this.responsivity = responsivity;
        this.polarizationLoss = polarizationLoss;
        this.fileName = fileName;
        parseFile(fileName, this, null);
    }


    public Map<String, String> getAllReceiverParameters() {
        PropertyMap m = new PropertyMap();
        m.put("Polarization loss", this.polarizationLoss);
        m.put("Responsivity", this.responsivity);
        m.put("Receiver file", this.fileName);
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

        for (Map.Entry<Float, TreeMap<Float, Float>> floatTreeMapEntry : db.entrySet()) {
            double maxBitRateOfCurrent = (double) (Float) ((TreeMap) floatTreeMapEntry.getValue()).lastKey();
            if (maxBitRateOfCurrent > bitrate) {
                return currentToOpticalPowerdBm((double) floatTreeMapEntry.getKey() * 1000000.0D);
            }
        }

        throw new WrongExperimentException("Bit rate beyond receiver capabilities");
    }

    public ArrayList<PowerPenalty> getDemodulationPowerPenalties(PhysicalParameterAndModelsSet modelSet,
                                                                 AbstractLinkFormat linkFormat) {
        PowerPenalty pola = new PowerPenalty("Polarization loss", "Demodulator/Receiver", this.polarizationLoss);
        return MoreArrays.getArrayList(pola);
    }

    private static void parseFile(String filename, OOK_NRZ_Receiver_Custom db, AbstractResultsManager man) {
        Execution exec = new Execution();

        try {
            InputStream input = OOK_NRZ_Receiver_RobertPolster.class.getResourceAsStream(filename);
            BufferedReader br = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
            String line;

            while (true) {
                if ((line = br.readLine()) == null) {
                    br.close();
                    break;
                }

                String[] bits = line.split(",");
                String[] first = bits[0].split("/");
                float currentF = Float.parseFloat(first[0]) / Float.parseFloat(first[1]);
                float bitRateS = Float.parseFloat(bits[1]);
                float pJperBitT = Float.parseFloat(bits[2]);
                if (man != null) {
                    DataPoint dp = new DataPoint();
                    dp.addProperty("current", currentF);
                    dp.addProperty("bitrate", bitRateS);
                    dp.addResultProperty("power pJ/bit", (double) pJperBitT * 1.0E12D);
                    if (Double.isNaN(pJperBitT)) {
                        System.out.println(".");
                    }

                    if (Double.isInfinite(pJperBitT)) {
                        System.out.println(".");
                    }

                    exec.addDataPoint(dp);
                }

                if (db != null) {
                    if (Double.isNaN(pJperBitT)) {
                        System.out.println(".");
                    }

                    if (!Double.isInfinite(pJperBitT)) {
                        db.insert(currentF, bitRateS, pJperBitT);
                    }
                }
            }
        } catch (IOException var13) {
            throw new IllegalStateException(var13);
        }

        if (man != null) {
            man.addExecution(exec);
        }

    }

    private void insert(float f, float s, float t) {
        TreeMap<Float, Float> forF = db.get(f);
        if (forF == null) {
            forF = new TreeMap<>();
            this.db.put(f, forF);
        }

        forF.put(s, t);
    }

    public double getPowerConsumptionMW(double bitrate, double iCurrent) {
        float inputCurrent = (float) iCurrent;
        Map.Entry<Float, TreeMap<Float, Float>> bottomCurrent = this.db.floorEntry(inputCurrent);
        Map.Entry<Float, TreeMap<Float, Float>> topCurrent = this.db.higherEntry(inputCurrent);
        if (bottomCurrent == null) {
            throw new WrongExperimentException("Current too low");
        } else {
            if (topCurrent == null) {
                System.out.println("Warning: current too high");
                topCurrent = bottomCurrent;
            }

            Map.Entry<Float, Float> bottomCurrentBottomBitrate = bottomCurrent.getValue()
                    .floorEntry((float) bitrate);
            Map.Entry<Float, Float> bottomCurrentTopBitrate = bottomCurrent.getValue()
                    .higherEntry((float) bitrate);
            Map.Entry<Float, Float> topCurrentBottomBitrate = topCurrent.getValue().floorEntry((float) bitrate);
            Map.Entry<Float, Float> topCurrentTopBitrate = topCurrent.getValue().higherEntry((float) bitrate);
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
                    float x1 = bottomCurrent.getKey();
                    float x2 = topCurrent.getKey();
                    float fx;
                    if (x1 != x2) {
                        fx = (inputCurrent - x1) / (x2 - x1);
                    } else {
                        fx = 0.0F;
                    }

                    float y1 = bottomCurrentBottomBitrate.getKey();
                    float y2 = topCurrentBottomBitrate.getKey();
                    float y3 = bottomCurrentTopBitrate.getKey();
                    float y4 = topCurrentTopBitrate.getKey();
                    float z1 = bottomCurrentBottomBitrate.getValue();
                    float z2 = topCurrentBottomBitrate.getValue();
                    float z3 = bottomCurrentTopBitrate.getValue();
                    float z4 = topCurrentTopBitrate.getValue();
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
        test65nm();
    }

    public static void test1() {

        SmartDataPointCollector col = new SmartDataPointCollector();

        Execution e = new Execution();


        //	for (Techno t: new Techno[]{Techno._65nm}) {
        for (OOK_NRZ_Receiver_RobertPolster.Techno t : OOK_NRZ_Receiver_RobertPolster.Techno.values()) {
            DataPoint d = new DataPoint();
            d.addProperty("Techno", t.toString());
            OOK_NRZ_Receiver_Custom rec = new OOK_NRZ_Receiver_Custom("data/DataS28PrecVsBRVsIin.csv", 1, 0.5,
                    new OOK_SERDES_PowerModel());
            //	for (Double bitRateInGbs : new double[]{1d, 10d, 20, 30}) {
            for (Double bitRateInGbs : TypeParser.parseDouble("1:40lin200")) {
                //	for (Float currentMicroA : TypeParser.parseFloat("20:24lin5")) {
                for (Float currentMicroA : TypeParser.parseFloat("1:250log500")) {
                    try {

                        DataPoint dp = d.getDerivedDataPoint();


                        double pow = rec.getPowerConsumptionMW(bitRateInGbs * 1e9, currentMicroA * 1e-6f);


                        dp.addProperty("input optical power (uW)", currentMicroA / rec.responsivity);
                        dp.addProperty("input optical power (dBm)", rec.currentToOpticalPowerdBm(currentMicroA));

                        dp.addProperty("current (uA)", currentMicroA);
                        dp.addProperty("bitrate (gbs)", bitRateInGbs);
                        dp.addResultProperty("power", pow);

                        e.addDataPoint(dp);
                    } catch (Exception ex) {
                        System.out.println(ex.getMessage());
                    }
                }
            }
        }

        col.addExecution(e);

        DefaultResultDisplayingGUI.displayDefault(col);
    }

    public static void test65nm() {
        SmartDataPointCollector col = new SmartDataPointCollector();
        parseFile("data/DataS28PrecVsBRVsIin.csv", null, col);
        DefaultResultDisplayingGUI.displayDefault(col);
    }
}
