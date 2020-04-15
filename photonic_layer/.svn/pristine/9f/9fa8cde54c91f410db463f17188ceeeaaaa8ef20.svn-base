package PhotonicElements.Heater.Model.ImpulseResponseModel.LumpedModel;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;

import java.util.Map;

public class LowPassFilter {

	double f3dB_kHz, tau_usec ;
	
	public LowPassFilter(
			@ParamName(name="f3dB (kHz) or tau (usec)? [f3dB --> TRUE]") boolean isf3dB,
			@ParamName(name="Value") double x
			){
		if(isf3dB){
			this.f3dB_kHz = x ;
			this.tau_usec = 1/(2*Math.PI*x*1e3) * 1e6 ;
		}
		else{
			this.tau_usec = x ;
			this.f3dB_kHz = 1/(2*Math.PI*x*1e-6) * 1e3 ;
		}
		
	}
	
	public Map<String, String> getAllParameters(){
		Map<String, String> map = new SimpleMap<String, String>() ;
		map.put("f3dB (kHz)", f3dB_kHz+"") ;
		map.put("tau (usec)", tau_usec+"") ;
		return map ;
	}
	
	public double getImpulseResponse(double t_usec){
		if(t_usec <= 0){
			return 0 ;
		}
		else{
			double value = 1/(tau_usec * 1e-6) * Math.exp(-t_usec/tau_usec) ;
			return value ;
		}
	}
	
	public double getFreqResponse(double freqHz){
		double arg = 2*Math.PI*freqHz/(f3dB_kHz*1e3) ;
		double denom = 1 + arg * arg ;
		return 1/denom ;
	}
	
	public double getFreqResponsedB(double freqHz){
		return 10*Math.log10(getFreqResponse(freqHz)) ;
	}
	
}
