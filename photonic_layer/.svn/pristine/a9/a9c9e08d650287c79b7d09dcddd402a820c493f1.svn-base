package PhotonicElements.Materials.LorentzModel;

import PhotonicElements.Utilities.MathLibraries.Complex;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;

import java.util.Map;

public class EpsilonLorentz {

	double Omega, Gamma, chi0 ;
	Complex zero = new Complex(0,0), one = new Complex(1,0) ;
	
	public EpsilonLorentz(
			@ParamName(name="Normalized Omega") double Omega,
			@ParamName(name="Normalized Gamma") double Gamma,
			@ParamName(name="Chi_0") double chi0
			){
		this.Omega = Omega ;
		this.Gamma = Gamma ;
		this.chi0 = chi0 ;
	}
	
	public Map<String, String> getAllParameters(){
		Map<String, String> map = new SimpleMap<String, String>() ;
		map.put("Omega", Omega+"") ;
		map.put("Gamma", Gamma+"") ;
		map.put("Chi0", chi0+"") ;
		return map ;
	}
	
	public Complex getComplexChi(){
		Complex denom = new Complex(1-Omega*Omega, Gamma*Omega) ;
		Complex num = new Complex(chi0, 0) ;
		return (num.divides(denom)) ;
	}
	
	public double getRealChi(){
		return getComplexChi().re() ;
	}
	
	public double getImagChi(){
		return getComplexChi().im() ;
	}
	
	public double getRealIndex(){
		return -getComplexChi().plus(one).sqrt().re() ;
	}
	
	public double getImagIndex(){
		return -getComplexChi().plus(one).sqrt().im() ;
	}
	
}
