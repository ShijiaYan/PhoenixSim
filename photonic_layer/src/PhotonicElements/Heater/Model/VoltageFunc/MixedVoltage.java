package PhotonicElements.Heater.Model.VoltageFunc;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;

import java.util.Map;

public class MixedVoltage extends AbstractVoltage {

	// This class takes in an array of voltages and mix them together
	
	AbstractVoltage[] voltage ;
	
	public MixedVoltage(
			@ParamName(name="Choose Voltage") AbstractVoltage[] voltage
			){
		this.voltage = voltage ;
	}
	
	
	@Override
	public double getVoltage(double t_usec) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Map<String, String> getAllParameters() {
		Map<String, String> map = new SimpleMap<>() ;
		map.put("VoltageName", "BoostPWMSignal") ;
		return map;
	}

}
