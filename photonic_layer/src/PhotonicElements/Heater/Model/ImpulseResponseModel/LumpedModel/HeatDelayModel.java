package PhotonicElements.Heater.Model.ImpulseResponseModel.LumpedModel;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;

import java.util.Map;

public class HeatDelayModel {

	double tau_usec, peakVal ;
	
	public HeatDelayModel(
			@ParamName(name="tau Delay (usec)") double tau_usec,
			@ParamName(name="Peak value (1/s)") double peakVal
			) {
		this.tau_usec = tau_usec ;
		this.peakVal = peakVal ;
	}
	
	public HeatDelayModel(
			@ParamName(name="tau Delay (usec)") double tau_usec
			) {
		this.tau_usec = tau_usec ;
		this.peakVal = 1/(tau_usec*1e-6) ;
	}
	
	public Map<String, String> getAllParameters(){
		Map<String, String> map = new SimpleMap<>() ;
		map.put("tau delay (usec)", tau_usec+"") ;
		map.put("Peak value", peakVal+"") ;
		return map ;
	}
	
	public double getImpulseResponse(double t_usec){
		if(t_usec < 0 || t_usec > 2*tau_usec){
			return 0 ;
		}
		else if(t_usec <= tau_usec){
			double slope = peakVal/tau_usec ;
			return slope * t_usec;
		}
		else{
			double slope = -peakVal/tau_usec ;
			double value = peakVal + slope*(t_usec - tau_usec) ;
			return value ;
		}
	}
	
	public double getImpulseResponseDerivative(double t_usec){
		if(t_usec < 0 || t_usec > 2*tau_usec){
			return 0 ;
		}
		else if(t_usec <= tau_usec){
			double slope = peakVal/tau_usec ;
			return slope ;
		}
		else{
			double slope = -peakVal/tau_usec ;
			return slope ;
		}
	}
	
/*	public double getFreqResponse(double freqHz){
		// need to calculate the Fourier Transform of the time response
	}*/
	
	
}
