package PhotonicElements.RingDesignSpace.AllPass;

import ch.epfl.general_libraries.clazzes.ParamName;
import flanagan.roots.RealRoot;
import flanagan.roots.RealRootFunction;

public class RingWgGeneral {

	double phiRad, lossDB, loss, inputKappa ; // this is the round trip phase inside the ring

	public RingWgGeneral(
			@ParamName(name="Input Kappa") double inputKappa,
			@ParamName(name="Round Trip Loss (dB)") double lossDB,
			@ParamName(name="Phi/2pi") double phiRatio
			){
		this.inputKappa = inputKappa ;
		this.lossDB = lossDB ;
		loss = Math.pow(10, -lossDB/10) ;
		this.phiRad = phiRatio * 2*Math.PI ;
	}


	public double getThruTransmission(){
		return getThruTransmission(this.phiRad);
	}

	public double getThruTransmission(double phiRad) {
		double t_in = Math.sqrt(1 - inputKappa * inputKappa) ;
		double L = loss ;
		double num = t_in*t_in + L - 2*t_in*Math.sqrt(L)*Math.cos(phiRad) ;
		double denum = 1 + t_in*t_in * L - 2*t_in* Math.sqrt(L)*Math.cos(phiRad) ;
		double trans = num/denum ;
		return trans ;
	}

	public double getThruTransmissionDB(){
		double trdB = 10*Math.log10(getThruTransmission()) ;
		return trdB ;
	}

	public double getFinesse(){
		double t_in = Math.sqrt(1 - inputKappa * inputKappa) ;
		double L = loss ;
		double A = 1-t_in * Math.sqrt(L) ;
		double B = 2*t_in*Math.sqrt(L) ;
		double arg = 1-(A*A/B) ;
		double Dphi3dB = 2*Math.acos(arg) ;
		double fsr = 2*Math.PI ;
		return (fsr/Dphi3dB) ;
	}

	public double getFinesseNumeric(){
		RealRootFunction func = new RealRootFunction() {
			@Override
			public double function(double phi) {
				double y = getThruTransmission(phi) - (getThruTransmission(0) + getThruTransmission(Math.PI)) * 1/2 ;
				return y;
			}
		};
		RealRoot rootFinder = new RealRoot() ;
		double phi3dB = rootFinder.bisect(func, 0, Math.PI) ;
		return (Math.PI/phi3dB) ;
	}


}
