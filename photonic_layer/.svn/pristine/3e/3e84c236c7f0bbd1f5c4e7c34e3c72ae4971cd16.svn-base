package People.Sebastien.DATE2017;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;
import flanagan.roots.RealRoot;
import flanagan.roots.RealRootFunction;

public class AddDropRing implements Experiment {

	double phiRad, lossDB, loss, inputKappa, outputKappa ; // this is the round trip phase inside the ring
	
	public AddDropRing(
			@ParamName(name="Input Kappa") double inputKappa,
			@ParamName(name="Output Kappa") double outputKappa,
			@ParamName(name="Round Trip Loss (dB)") double lossDB,
			@ParamName(name="Phi/2pi") double phiRatio
			){
		this.inputKappa = inputKappa ;
		this.outputKappa = outputKappa ;
		this.lossDB = lossDB ;
		loss = Math.pow(10, -lossDB/10) ;
		this.phiRad = phiRatio * 2*Math.PI ;
	}
	
	public AddDropRing(
			@ParamName(name="Output Kappa") double outputKappa,
			@ParamName(name="Round Trip Loss (dB)") double lossDB,
			@ParamName(name="Phi/2pi") double phiRatio
			){
		this.outputKappa = outputKappa ;
		this.lossDB = lossDB ;
		loss = Math.pow(10, -lossDB/10) ;
		this.inputKappa = Math.sqrt(1 - loss*(1-(outputKappa*outputKappa)));
		this.phiRad = phiRatio * 2*Math.PI ;
	}
	
	private double getThruTransmission() {
		return getThruTransmission(this.phiRad);
	}
	
	private double getThruTransmission(double phiRad){
		double t_out = Math.sqrt(1 - outputKappa*outputKappa) ;
		double t_in = Math.sqrt(1 - inputKappa * inputKappa) ;
		double L = loss ;
		double num = t_in*t_in + t_out*t_out*L - 2*t_in*t_out*Math.sqrt(L)*Math.cos(phiRad) ;
		double denum = 1 + t_in*t_in*t_out*t_out*L - 2*t_in*t_out*Math.sqrt(L)*Math.cos(phiRad) ;
		double trans = num/denum ;
		return trans ;
	}
	
	private double getThruTransmissionDB(){
		double trdB = 10*Math.log10(getThruTransmission()) ;
		return trdB ;
	}
	
	public double getDropTransmission() {
		return getDropTransmission(this.phiRad);
	}
 	
	
	private double getDropTransmission(double phiRad){
		double t_out = Math.sqrt(1 - outputKappa*outputKappa) ;
		double t_in = Math.sqrt(1 - inputKappa * inputKappa) ;
		double k_out = outputKappa ;
		double k_in = inputKappa ;
		double L = loss ;
		double num = (k_in*k_in)*(k_out*k_out)*Math.sqrt(L) ;
		double denum = 1 + t_in*t_in*t_out*t_out*L - 2*t_in*t_out*Math.sqrt(L)*Math.cos(phiRad) ;
		double trans = num/denum ;
		return trans ;
	}
	
	public double getDropTransmissionDB(){
		double drop = getDropTransmission() ;
		return 10*Math.log10(drop) ;
	}
	
	private double getFinesse(){
		double t_in = Math.sqrt(1 - inputKappa * inputKappa) ;
		double t_out = Math.sqrt(1 - outputKappa*outputKappa) ;
		double L = loss ;
		double A = 1-t_in * t_out * Math.sqrt(L) ;
		double B = 2*t_in*t_out*Math.sqrt(L) ;
		double arg = 1-(A*A/B) ;
		double Dphi3dB = 2*Math.acos(arg) ;
		double fsr = 2*Math.PI ;
		return (fsr/Dphi3dB) ;
	}
	
	private double getFinesseNumeric(){
		RealRootFunction func = new RealRootFunction() {
			@Override
			public double function(double phi) {
				double y = getDropTransmission(phi) - getDropTransmission(0) * 1/2 ;
				return y;
			}
		};
		RealRoot rootFinder = new RealRoot() ;
		double phi3dB = rootFinder.bisect(func, 0, Math.PI) ;
		return (Math.PI/phi3dB) ;
	}
	
	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {
		DataPoint dp = new DataPoint() ;
		dp.addProperty("phi/2pi", phiRad/(2*Math.PI));
		dp.addProperty("lossdB", lossDB);
		dp.addProperty("loss", loss);
		dp.addProperty("kappa in", this.inputKappa);
		dp.addProperty("kappa out", this.outputKappa);

		double twoPi = Math.PI *2;

		double erThru = 1 - getThruTransmission(0);		
		
		for (double d = 0; d < twoPi ; d = d + 0.001) {
			double value = 1 - getThruTransmission(d);
			if (value < erThru/2d) {
				double finesse = twoPi/(2*d);
				dp.addProperty("Finesse thru", finesse);
				break;
			}
		}		
		
		double dropLoss = getDropTransmission(0);
		
		for (double d = 0; d < twoPi ; d = d + 0.001) {
			double value = getDropTransmission(d);
			if (value < dropLoss/2d) {
				double finesse = twoPi/(2*d);
				dp.addProperty("Finesse drop", finesse);
				break;
			}
		}	
		
		dp.addProperty("Drop loss at resonance (dB)", 10*Math.log10(getDropTransmission(0)));

		
		dp.addResultProperty("Thru Transmission (dB)", getThruTransmissionDB());
		dp.addResultProperty("Thru Transmission", getThruTransmission());		
		dp.addResultProperty("Drop Transmission (dB)", getDropTransmissionDB());
		dp.addResultProperty("Drop Transmission", getDropTransmission());	
		double temp = getThruTransmission() + getDropTransmission();
		dp.addResultProperty("Total transmission (dB)", 10*Math.log10(temp));
		dp.addResultProperty("Total transmission", temp);
		dp.addResultProperty("Finesse (equation)", getFinesse());
		dp.addResultProperty("Finesse (numeric)", getFinesseNumeric());
		man.addDataPoint(dp);
	}

}
