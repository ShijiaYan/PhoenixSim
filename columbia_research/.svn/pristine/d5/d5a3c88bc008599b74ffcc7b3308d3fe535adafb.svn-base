package edu.columbia.sebastien.autobooksim.config;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.ExtendedStringBuilder;
import ch.epfl.general_libraries.utils.SimpleMap;
import edu.columbia.sebastien.autobooksim.AbstractBooksimConfigBlock;

public class RouterArchitectureConfig extends AbstractBooksimConfigBlock {

	int input_speedup;
	int output_speedup;
	double internal_speedup;
	
	public RouterArchitectureConfig(
			@ParamName(name="input_speedup", default_="1") int input_speedup,
			@ParamName(name="output_speedup", default_="1") int output_speedup,
			@ParamName(name="internal_speedup", default_="1") double internal_speedup
			) {
		this.input_speedup = input_speedup;
		this.output_speedup = output_speedup;
		this.internal_speedup = internal_speedup;
	}

	@Override
	public String getConfigBlock() {
		ExtendedStringBuilder sb = new ExtendedStringBuilder();
		
		sb.appendln("credit_delay   = 2;");		
		sb.appendln("routing_delay  = 1;");		
		sb.appendln("vc_alloc_delay = 1;");		
		sb.appendln("sw_alloc_delay = 1;");		
		sb.appendln("st_final_delay = 1;");		
		sb.appendln("");	
		sb.appendln("input_speedup     = " + input_speedup + ";");		
		sb.appendln("output_speedup    = " + output_speedup + ";");
		sb.appendln("internal_speedup  = " + internal_speedup + ";");

		return sb.toString();
	}

	@Override
	public Map<String, String> getParameters() {
		// TODO Auto-generated method stub
		return SimpleMap.getMap("input_speedup", input_speedup, "output_speedup", output_speedup, 
				"internal_speedup", internal_speedup);
	}

}
