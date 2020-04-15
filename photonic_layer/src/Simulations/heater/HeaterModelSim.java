package Simulations.heater;

import PhotonicElements.Heater.Model.ImpulseResponseModel.AbstractImpulseResponse;
import PhotonicElements.Heater.Model.Structure.SelfHeating;
import PhotonicElements.Heater.Model.TransientResponseModel.TransientResponse;
import PhotonicElements.Heater.Model.VoltageFunc.AbstractVoltage;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;

public class HeaterModelSim implements Experiment {

	double d_um, t_usec, freq_hz ;
	AbstractVoltage voltage ;
	SelfHeating selfHeat ;
	AbstractImpulseResponse impulse ;
	TransientResponse transResponse ;

	public HeaterModelSim(
			@ParamName(name="Impulse Response") AbstractImpulseResponse impulse,
			@ParamName(name="Self Heating Model") SelfHeating selfHeat,
			@ParamName(name="Heater Voltage (V)") AbstractVoltage voltage,
			@ParamName(name="Time (usec)") double t_usec,
			@ParamName(name="Frequency (Hz)") double freq_hz
			){
		this.selfHeat = selfHeat ;
		this.t_usec = t_usec ;
		this.freq_hz = freq_hz ;
		this.selfHeat = selfHeat ;
		this.voltage = voltage ;
		// create impulse response
//		impulse = new ImpulseResponse1D(f3dB_kHz) ;
		this.impulse = impulse ;
		// create transient response
		transResponse = new TransientResponse(selfHeat, impulse, voltage) ;
	}

	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {
		DataPoint dp = new DataPoint() ;
		dp.addProperty("time (usec)", t_usec);
		dp.addProperty("Freq (Hz)", freq_hz);
		dp.addProperty("Freq (kHz)", freq_hz/1e3);
//		dp.addProperty("f3dB (kHz)", impulse.getf3dBKHz());
		dp.addProperties(impulse.getAllParameters());
		dp.addResultProperty("Impulse Time Response", impulse.getTimeResponse(t_usec));
		dp.addResultProperty("Normalized Impulse Response", impulse.getNormalizedImpulseResponse(t_usec));
		dp.addResultProperty("Impulse Freq Response (dB)", impulse.getFreqResponsedB(freq_hz));
		dp.addResultProperty("Transient Response", transResponse.getTimeResponse(t_usec));
		dp.addResultProperty("Heater Voltage (V)", voltage.getVoltage(t_usec));
		dp.addResultProperties(voltage.getAllParameters());
		dp.addProperties(voltage.getAllParameters());
		dp.addResultProperty("DeltaT_H (K)", selfHeat.getDeltaT(voltage.getVoltage(t_usec)));
		man.addDataPoint(dp);
	}

}
