package edu.columbia.lrl.CrossLayer.physical_models.util;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.general_libraries.results.Execution;

public class PlotRingModulatorDepletion implements Experiment {


    private double resLambda;
    private double Q;
    private int index;
    private double resER;
    private double resShiftnm;


    // Commented to do not appear in the cockpit
    public PlotRingModulatorDepletion(
            @ParamName(name = "Resonance Wavelength (nm)") double resLambda,
            @ParamName(name = "Quality Factor") double Q,
            @ParamName(name = "Shift of Spectrum (nm)") double resShiftnm,
            @ParamName(name = "Resonance Extinction Ratio (dB)") double resER,
            @ParamName(name = "Number of Freq Points") int Index) {
        this.resLambda = resLambda;
        this.Q = Q;
        this.resER = resER;
        this.index = Index;
        this.resShiftnm = resShiftnm;
    }


    @Override
    public void run(AbstractResultsManager man, AbstractResultsDisplayer dis) {

        double resHz = 3e8 / (resLambda * 1e-9);
        double effectiveIndex = 4;

        // Find the parameters of the original spectrum (without carriers)
        double t0 = Math.pow(10, -resER / 10);
        double FWHM = resHz / Q;
        double tau_i = 2 / (Math.PI * FWHM * (1 + Math.sqrt(t0)));
        double tau_c = (1 + Math.sqrt(t0)) / (1 - Math.sqrt(t0)) * tau_i;
        double alpha = 2 * effectiveIndex / (3e8 * 1e2 * tau_i); // Note: must be in 1/cm
        double Gamma = 0.8;
        double DeltaNsilicon = effectiveIndex / Gamma * resShiftnm / resLambda;

        // Find the parameters of the shifted spectrum (with carriers)
        double deltaAlpha = getDeltaAlphaFromDeltaN(DeltaNsilicon); // is in 1/cm
        double alphaPrime = alpha + deltaAlpha; // calculated in 1/cm
        double tau_i_prime = 2 * effectiveIndex / (3e8 * 1e2 * alphaPrime);
        double FWHMprime = 1 / Math.PI * (1 / tau_i_prime + 1 / tau_c);
        double resHzPrime = 3e8 / ((resLambda + resShiftnm) * 1e-9); // in Hz
        double Qprime = resHzPrime / FWHMprime;
        double t0Prime = Math.pow((1 - tau_i_prime / tau_c) / (1 + tau_i_prime / tau_c), 2);
        double extinctionWithCarriers = -10 * Math.log10(t0Prime);


        double[] resonances = new double[index];
        double[] resonancesDB = new double[index];
        double[] lambdas = new double[index];

        double resLambdaShifted = resLambda + resShiftnm;

        double lambdaMin = 0.998 * resLambdaShifted;
        double lambdaMax = 1.003 * resLambdaShifted;
        double stepLambda = (lambdaMax - lambdaMin) / index;

        for (int i = 0; i < lambdas.length; i++) {
            lambdas[i] = lambdaMin + i * stepLambda;
            resonances[i] = getTransmission(lambdas[i], resLambdaShifted, Qprime, extinctionWithCarriers);
            resonancesDB[i] = 10 * Math.log10(resonances[i]);
        }
		
/*		Chart chart = new Chart("Wavelength", "res");
		chart.addSerie(lambdas, resonances, "res");
		chart.popupChart();*/
        Execution ex = new Execution();

        for (int i = 0; i < lambdas.length; i++) {
            DataPoint dp = new DataPoint();

            dp.addResultProperty("Resonance", resonances[i]);
            dp.addResultProperty("Resonance (dB)", resonancesDB[i]);
            dp.addProperty("Wavelength", lambdas[i]);
            dp.addProperty("Quality Factor", Qprime);
            dp.addProperty("Resonance Shift", resShiftnm);
            ex.addDataPoint(dp);
        }

        man.addExecution(ex);


    }

    // This method calculates the transmission spectrum of the ring modulators
    public double getTransmission(double lambda, double resLambda, double qFactor, double resExtinctionDB) {

        double FWHM = resLambda / qFactor;
        double resExtinction = Math.pow(10, -resExtinctionDB / 10);

        double trans =
                (Math.pow(2 * (lambda - resLambda) / FWHM, 2) + resExtinction) / (Math.pow(2 * (lambda - resLambda) / FWHM, 2) + 1);

        return trans;

    }

    public double getDeltaAlphaFromDeltaN(double DeltaN) {
        double DeltaAlpha = 2.3474e3 * DeltaN;
        return DeltaAlpha;
    }


}



