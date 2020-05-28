package test.edu.columbia.lrl.experiments.spinet;

import edu.columbia.lrl.CrossLayer.physical_models.devices.modulations.OOK_NRZ_ModulatorPowerModel_Hannes;
import edu.columbia.lrl.CrossLayer.physical_models.util.RateOnlyFormat;

import java.util.Scanner;

public class HannesTest {

    public static void main(String[] args) {
        test();
    }


    public static void test() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            try {
                double rate = scanner.nextDouble();
                OOK_NRZ_ModulatorPowerModel_Hannes hannes = new OOK_NRZ_ModulatorPowerModel_Hannes();
                double ePerBit = hannes.getEnergyPJperBit(5, 40, new RateOnlyFormat(rate));
                System.out.println("Energy Per Bit at " + rate + "Gbps is:\n\t" + ePerBit + "pJ/bit.");
                scanner.nextLine();
            } catch (RuntimeException e) {
                break;
            }
        }
        scanner.close();
    }
}