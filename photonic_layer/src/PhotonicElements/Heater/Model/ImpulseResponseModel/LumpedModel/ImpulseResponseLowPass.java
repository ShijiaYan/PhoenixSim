package PhotonicElements.Heater.Model.ImpulseResponseModel.LumpedModel;

import ch.epfl.general_libraries.clazzes.ParamName;

public class ImpulseResponseLowPass {

	double xi_sio2 = 8.7e-7 ;
	double f0_hz, f3dB_kHz ;
	
	
	public ImpulseResponseLowPass(
			@ParamName(name="3dB Bandwidth (kHz)") double f3dB_kHz
			){
		this.f3dB_kHz = f3dB_kHz ;
		f0_hz = f3dB_kHz * 1e3 /(Math.log(2) * Math.log(2)) ;
	}
	
	
	public double getTimeResponse(double t_usec) {
		double tau = 1/(2*Math.PI*f3dB_kHz*1e3) ;
		double tau_usec = tau*1e6 ;
		if(t_usec < 0){
			return 0 ;
		}
		else{
			return (1/tau * Math.exp(-t_usec/tau_usec)) ;
		}
	}

	
	public double getFreqResponse(double freqHz) {
		double func = 1/(1+Math.pow(freqHz/(f3dB_kHz*1e3), 2)) ;
		return func ;
	}

	
	public double getFreqResponsedB(double freqHz) {
		double response = 10*Math.log10(getFreqResponse(freqHz)) ;
		return response ;
	}

	
	public double getf3dBKHz() {
		return f3dB_kHz ;
	}

	
	public double getNormalizedImpulseResponse(double t_usec) {
		double tau = 1/(2*Math.PI*f3dB_kHz*1e3) ;
		double tau_usec = tau*1e6 ;
		if(t_usec < 0){
			return 0 ;
		}
		else{
			return (Math.exp(-t_usec/tau_usec)) ;
		}
	}

}
