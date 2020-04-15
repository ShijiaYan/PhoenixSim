package PhotonicElements.Heater.Model.ImpulseResponseModel.LumpedModel;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;
import flanagan.integration.IntegralFunction;
import flanagan.integration.Integration;

import java.util.Map;

public class HeaterLumpedModel {

	HeatDelayModel heat ;
	LowPassFilter lowpass ;
	double tau, tau_d ;
	
	public HeaterLumpedModel(
			@ParamName(name="Heat Delay Model") HeatDelayModel heat,
			@ParamName(name="Low Pass Model") LowPassFilter lowpass
			){
		this.heat = heat ;
		this.lowpass = lowpass ;
		tau = lowpass.tau_usec ;
		tau_d = heat.tau_usec ;
	}
	
	public Map<String, String> getAllParameters(){
		Map<String, String> map = new SimpleMap<String, String>() ;
		map.putAll(heat.getAllParameters());
		map.putAll(lowpass.getAllParameters());
		return map ;
	}
	
	public double getImpulseResponse(final double t_usec){
		IntegralFunction func = new IntegralFunction() {
			@Override
			public double function(double x_usec) {
				double func = heat.getImpulseResponse(x_usec) * lowpass.getImpulseResponse(t_usec - x_usec) ;
				return func ;
			}
		};
		Integration integral = new Integration() ;
		integral.setIntegrationFunction(func);
		integral.setLimits(0, 2*tau_d);
		double response = integral.gaussQuad(1000) * 1e-6 ;
		return response ;
	}
	
	public double getImpulseResponseClosedForm(double t_usec){
		double coeff = heat.peakVal*(heat.tau_usec*1e-6) ;
		if(t_usec < 0){
			return 0 ;
		}
		else if(t_usec < tau_d){
			double response = ((t_usec-tau)/tau_d + tau/tau_d * Math.exp(-t_usec/tau))/(tau_d*1e-6) ;
			return response * coeff ;
		}
		else if(t_usec < 2*tau_d){
			double e0 = Math.exp(-(t_usec-tau_d)/tau) ;
			double a0 = 2*(1-e0)/(tau_d*1e-6) ;
			double a1 = e0*(2*(tau_d-tau)/tau_d + tau/tau_d * Math.exp(-tau_d/tau) - (t_usec-tau)/tau_d / e0)/(tau_d*1e-6) ;
			double response = a0 + a1 ;
			return response * coeff ;
		}
		else{
			double e0 = Math.exp(-(t_usec-2*tau_d)/tau) ;
			double a0 = e0* 2*(1-Math.exp(-tau_d/tau))/(tau_d*1e-6) ;
			double a1 = e0*2*(tau_d-tau)/tau_d * Math.exp(-tau_d/tau)/(tau_d*1e-6) ;
			double a2 = e0*(tau/tau_d * Math.exp(-2*tau_d/tau) - (2*tau_d-tau)/tau_d)/(tau_d*1e-6)  ;
			double response = a0 + a1 + a2 ;
			return response * coeff ;
		}
	}
	
	public double getImpulseResponseDerivative(final double t_usec){
		IntegralFunction func = new IntegralFunction() {
			@Override
			public double function(double x_usec) {
				double func = heat.getImpulseResponseDerivative(x_usec) * lowpass.getImpulseResponse(t_usec - x_usec) ;
				return func ;
			}
		};
		Integration integral = new Integration() ;
		integral.setIntegrationFunction(func);
		integral.setLimits(0, 2*tau_d);
		double response = integral.gaussQuad(1000) * 1e-6 ;
		return response ;
	}
	
	public double getImpulseResponseDerivativeClosedForm(double t_usec){
		double coeff = heat.peakVal*(heat.tau_usec*1e-6) ;
		if(t_usec < 0){
			return 0 ;
		}
		else if (t_usec < tau_d){
			double result = (1-Math.exp(-t_usec/tau))/(tau_d)/(tau_d*1e-6) ;
			return result * coeff ;
		}
		else if (t_usec < 2*tau_d){
			double result = Math.exp(-t_usec/tau)/tau_d * (2*Math.exp(tau_d/tau)-Math.exp(t_usec/tau)-1)/(tau_d*1e-6) ;
			return result * coeff ;
		}
		else{
			double result = -Math.pow(1-Math.exp(-tau_d/tau), 2)*Math.exp(-(t_usec-2*tau_d)/tau)/tau_d/(tau_d*1e-6) ;
			return result * coeff ;
		}
	}
	
	
}
