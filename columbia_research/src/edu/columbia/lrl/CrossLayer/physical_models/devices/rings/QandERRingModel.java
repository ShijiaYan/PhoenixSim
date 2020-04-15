//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package edu.columbia.lrl.CrossLayer.physical_models.devices.rings;

import ch.epfl.general_libraries.clazzes.ParamName;
import edu.columbia.lrl.CrossLayer.physical_models.util.Constants;


public class QandERRingModel extends AbstractRingModel {

	private double q;
	private double er;
	private double Neff;

	public QandERRingModel(@ParamName(name = "Ring Q") double q, @ParamName(name = "Effective Index") double Neff,
			@ParamName(name = "Extinction ratio in dB") double er) {
		this.q = q;
		this.er = er;
		this.Neff = Neff;
	}

	public double getQ() {
		return this.q;
	}

	public double getER() {
		return this.er;
	}

	public double[] getCouplingCoeff_t_k(Constants ct) {
		double erInDecimal = Math.pow(10.0D, -this.er / 20.0D);
		double x = (1.0D + erInDecimal) / (1.0D - erInDecimal);
		double[] t_and_k = new double[2];
		double radius = this.getRadius(ct);
		double roundTripLoss = 6.283185307179586D * radius * this.getDBperCm(ct) * 23.0D;
		double kappa = Math.sqrt(roundTripLoss * x);
		double t = Math.sqrt(1.0D - kappa * kappa);
		t_and_k[0] = t;
		t_and_k[1] = kappa;
		return t_and_k;
	}

	public double getDBperCm(Constants ct) {
		double r = Math.pow(10.0D, -this.er / 20.0D);
		double x = (1.0D + r) / (1.0D - r);
		double ng = this.Neff;
		double speedLight = ct.getSpeedOfLight();
		double f0 = ct.getCenterFrequency();
		double tau_i = this.q * (1.0D + x) / (3.141592653589793D * f0);
		double alphaDBperCm = 2.0D * ng / (speedLight * tau_i) / 23.0D;
		return alphaDBperCm;
	}

	public double getRadius(Constants ct) {
		double ng = this.Neff;
		double lambda = ct.getCenterWavelength();
		double radius = lambda * lambda / (6.283185307179586D * ng * ct.getFullFSR());
		return radius;
	}

	public double getNeff() {
		return this.Neff;
	}
}
