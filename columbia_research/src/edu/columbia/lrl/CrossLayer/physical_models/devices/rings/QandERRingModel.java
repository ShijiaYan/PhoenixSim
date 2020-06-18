package edu.columbia.lrl.CrossLayer.physical_models.devices.rings;

import ch.epfl.general_libraries.clazzes.ParamName;
import edu.columbia.lrl.CrossLayer.physical_models.util.Constants;


public class QandERRingModel extends AbstractRingModel {

    private double q;
    private double er;
    private double groupIndex;
    private double alphaPerMeter;

    public QandERRingModel(@ParamName(name = "Ring Q") double q, @ParamName(name = "Group Index") double groupIndex,
                           @ParamName(name = "Extinction ratio in dB") double er) {
        this.q = q;
        this.er = er;
        this.groupIndex = groupIndex;
    }

    @Override
    public double getGroupIndex() {
        return groupIndex;
    }

    @Override
    public double getQ() {
        return q;
    }

    @Override
    public double getER() {
        return er;
    }

    @Override
    public double[] getCouplingCoeff_t_k(Constants ct) {

        double r = Math.pow(10, -er / 20);
        double x = (1 + r) / (1 - r);
        double[] t_and_k = new double[2];
        double radius = getRadius(ct);
        double roundTripLoss = 2 * Math.PI * radius * alphaPerMeter;
        double kappa = Math.sqrt(roundTripLoss * x);
        double t = Math.sqrt(1 - kappa * kappa);
        t_and_k[0] = t;
        t_and_k[1] = kappa;

        return t_and_k;
//		throw new IllegalStateException("Not implemented");
    }

    @Override
    public double getDBperCm(Constants ct) {
//		throw new IllegalStateException("Not implemented");
        double r = Math.pow(10, -er / 20);
        double x = (1 + r) / (1 - r);
        double ng = groupIndex;
        double speedLight = ct.getSpeedOfLight();
        double f0 = ct.getCenterFrequency();
        double tau_i = q * (1 + x) / (Math.PI * f0);
        alphaPerMeter = 2 * ng / (speedLight * tau_i);
        double alphaDBperCm = alphaPerMeter / 23; // Unit conversion
        return alphaDBperCm;
    }

    public double getRadius(Constants ct) {
        double ng = groupIndex;
        double FSR = ct.getFullFSR();
        double lambda = ct.getCenterWavelength();
        double radius = lambda * lambda / (2 * Math.PI * ng * FSR);

        return radius;
    }
}
