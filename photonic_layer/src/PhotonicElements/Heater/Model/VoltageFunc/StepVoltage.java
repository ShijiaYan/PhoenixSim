package PhotonicElements.Heater.Model.VoltageFunc;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;

import java.util.Map;

public class StepVoltage extends AbstractVoltage {

	double vStart, vEnd, tJump_usec ;
	
	public StepVoltage(
			@ParamName(name="Jump time (usec)") double tJump_usec,
			@ParamName(name="Start Voltage (V)") double vStart,
			@ParamName(name="End Voltage (V)") double vEnd
			){
		this.tJump_usec = tJump_usec ;
		this.vStart = vStart ;
		this.vEnd = vEnd ;
	}
	
	@Override
	public double getVoltage(double t_usec) {
		if(t_usec < tJump_usec){
			return vStart ;
		}
		else{
			return vEnd ;
		}
	}

	@Override
	public Map<String, String> getAllParameters() {
		Map<String, String> map = new SimpleMap<>() ;
		map.put("tStep (usec)", tJump_usec+"") ;
		map.put("vStart (V)", vStart+"") ;
		map.put("vEnd (V)", vEnd+"") ;
		map.put("VoltageName", "StepVoltage") ;
		return map;
	}

}
