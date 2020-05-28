package People.Sebastien.DATE2017;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;
import flanagan.roots.RealRoot;
import flanagan.roots.RealRootFunction;

public class RingWg implements Experiment {

	double phiRad, lossDB, loss, inputKappa ; // this is the round trip phase inside the ring
	
	public RingWg(
			@ParamName(name="Input Kappa") double inputKappa,
			@ParamName(name="Round Trip Loss (dB)") double lossDB,
			@ParamName(name="Phi/2pi") double phiRatio
			){
		this.inputKappa = inputKappa ;
		this.lossDB = lossDB ;
		loss = Math.pow(10, -lossDB/10) ;
		this.phiRad = phiRatio * 2*Math.PI ;
	}
	
	
	private double getThruTransmission(){
		return getThruTransmission(this.phiRad);
	}
	
	private double getThruTransmission(double phiRad) {
		double t_in = Math.sqrt(1 - inputKappa * inputKappa) ;
		double L = loss ;
		final double v = 2 * t_in * Math.sqrt(L) * Math.cos(phiRad);
		double num = t_in*t_in + L - v;
		double denum = 1 + t_in*t_in * L - v;
		return num/denum;
	}
	
	private double getThruTransmissionDB(){
		return 10*Math.log10(getThruTransmission());
	}
	
	private double getFinesse(){
		double t_in = Math.sqrt(1 - inputKappa * inputKappa) ;
		double L = loss ;
		double A = 1-t_in * Math.sqrt(L) ;
		double B = 2*t_in*Math.sqrt(L) ;
		double arg = 1- A*A/B;
		double Dphi3dB = 2*Math.acos(arg) ;
		double fsr = 2*Math.PI ;
		return fsr/Dphi3dB;
	}
	
	private double getFinesseNumeric(){
		RealRootFunction func = phi -> {
			return getThruTransmission(phi) - (getThruTransmission(0) + getThruTransmission(Math.PI)) * 1/2;
        };
		RealRoot rootFinder = new RealRoot() ;
		double phi3dB = rootFinder.bisect(func, 0, Math.PI) ;
		return Math.PI/phi3dB;
	}
	
	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {
		double twoPi = Math.PI *2;
		DataPoint dp = new DataPoint() ;
		
		double er = 1 - getThruTransmission(0);
		dp.addProperty("Transmission at resonance (dB)", 10*Math.log10(getThruTransmission(0)));

		for (double d = 0; d < twoPi ; d = d + 0.001) {
			double value = 1 - getThruTransmission(d);
			if (value < er/2d) {
				double finesse = twoPi/(2*d);
				dp.addProperty("Finesse", finesse);
				break;
			}
		}
		
		dp.addResultProperty("Finesse (equation)", getFinesse());
		dp.addResultProperty("Finesse (numeric)", getFinesseNumeric());
		dp.addProperty("phi/2pi", phiRad/ twoPi);
		dp.addProperty("lossDB", lossDB);
		dp.addProperty("input kappa", inputKappa);
		dp.addResultProperty("Thru Transmission (dB)", getThruTransmissionDB());
		dp.addResultProperty("Thru Transmission", getThruTransmission());
	/*	if (phiRad == 2*Math.PI) {
			DataPoint dp2 = new DataPoint() ;
			dp2.addProperty("lossDB", lossDB);
			dp2.addProperty("input kappa", inputKappa);
			dp.addResultProperty("ER", getThruTransmissionDB());
			
		}*/
		man.addDataPoint(dp);
	}

}
