package edu.columbia.sebastien.autobooksim.specific_config;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.ExtendedStringBuilder;
import ch.epfl.general_libraries.utils.SimpleStringMap;
import edu.columbia.sebastien.autobooksim.BooksimConfig;
import edu.columbia.sebastien.autobooksim.config.FlowControlConfig;
import edu.columbia.sebastien.autobooksim.config.RouterArchitectureConfig;
import edu.columbia.sebastien.autobooksim.specific_config.PINE.AbstractPINEconstruct;

public class PINEconfig extends BooksimConfig {
	
	private AbstractPINEconstruct pineConstruct;

	public PINEconfig(
			FlowControlConfig flowControlConfig,
			RouterArchitectureConfig routerArchConfig, 
			@ParamName(name="seed", default_="1") int seed,
			@ParamName(name="channel_width", default_="64") int channel_width, 
			@ParamName(name="bw_ratio", default_="1") int bw_ratio,
			@ParamName(name="PINEconstruct") AbstractPINEconstruct pineConstruct) {
		super(flowControlConfig, routerArchConfig, seed, channel_width, bw_ratio);
		this.pineConstruct = pineConstruct;
	}
	
	@Override
	public String getConfigBlock() {
		ExtendedStringBuilder sb = new ExtendedStringBuilder();
		sb.append(super.getStandardConfigBlock());
		
		sb.appendln("configurable_file = connection_files/connections_cpu_gpu_centric;");		
		sb.appendln("injection_process = node_types;");		
		sb.appendln("send_to_neighbors_only = 1;");		
		sb.appendln("types_of_nodes_file = types_of_nodes_file;");		
		sb.appendln("vc_buf_size_injection = 16;");		
		
		return sb.toString();
	}
	
	@Override
	public Map<String, String> getParameters() {
		Map<String, String> map = super.getParameters();
		
		map.put("pineconstruct", pineConstruct.getClass().getSimpleName());
		

		
		return map;
	}

}
