package edu.columbia.sebastien.autobooksim;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.io.MoreFile;
import ch.epfl.general_libraries.utils.ExtendedStringBuilder;
import ch.epfl.general_libraries.utils.SimpleStringMap;
import edu.columbia.sebastien.autobooksim.config.FlowControlConfig;
import edu.columbia.sebastien.autobooksim.config.RouterArchitectureConfig;

public class BooksimConfig extends AbstractBooksimConfigBlock {
	
	private FlowControlConfig flowControlConfig;
	private RouterArchitectureConfig routerArchConfig;
	
	private int seed;
	private int channel_width;
	private int bw_ratio;
	
	private File configFile = null;
	
	public BooksimConfig(FlowControlConfig flowControlConfig,
			RouterArchitectureConfig routerArchConfig,
			@ParamName(name="seed", default_="1") int seed,
			@ParamName(name="channel_width", default_="64") int channel_width,
			@ParamName(name="bw_ratio", default_="1") int bw_ratio) {
		this.flowControlConfig = flowControlConfig;
		this.routerArchConfig = routerArchConfig;
		
		this.seed = seed;
		this.channel_width = channel_width;
		this.bw_ratio = bw_ratio;
	}
	
	public void makeAndwrite(String booksimDirectory) {
		try {
			configFile = MoreFile.incrementFileName(booksimDirectory + getConfigFileName(), "");
			FileWriter fw = new FileWriter(configFile);
			
			fw.write(getConfigBlock());
			fw.close();
		}
		catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	public String getConfigFileNameAndId() {
		if (configFile == null) {
			throw new IllegalStateException("Cannot be call before makeAndwrite is called");
		}
		return configFile.getName();
	}

	public String getConfigFileName() {
		return "autobooksim_config";
	}	
	
	@Override
	public String getConfigBlock() {
		ExtendedStringBuilder sb = new ExtendedStringBuilder();
		sb.appendln(getStandardConfigBlock());
		
		sb.appendln("routing_function = dim_order;");
		sb.appendln("topology = mesh;");
		sb.appendln("traffic       = uniform;");
		
		return sb.toString();
	}

	public String getStandardConfigBlock() {
		ExtendedStringBuilder sb = new ExtendedStringBuilder();
		
		sb.appendln("// FLOW CONTROL");
		sb.appendln(flowControlConfig.getConfigBlock());
		sb.appendln();
		
		sb.appendln("// ROUTER ARCHITECTURE");
		sb.appendln(routerArchConfig.getConfigBlock());
		sb.appendln();
		
		// ALL PARAMETERS BELOW COULD BE PLACED IN SUB-OBJECT WHERE APPROPRIATE
		sb.appendln("warmup_periods = 3;");
		sb.appendln("sim_count          = 1;");
		sb.appendln("max_outstanding_requests = 0;");
		sb.appendln("sim_power = 1;");
		sb.appendln("priority = none;");
		
		
		sb.appendln("sim_type = latency;");
		sb.appendln("num_vcs     = 8;");
		sb.appendln("injection_rate = 0.1;");
		sb.appendln("hold_switch_for_packet = 0;");
		sb.appendln("vc_allocator = select;");
		sb.appendln("sw_allocator = select;");
		sb.appendln("alloc_iters = 1;");
		
		sb.appendln("bw_ratio = " + bw_ratio + ";");	// exposed	
		sb.appendln("c  = 1;");		
		sb.appendln("k  = 16;");		
		sb.appendln("router = iq;");		
		sb.appendln("packet_size = 256; // This is in bits.");		
		sb.appendln("print_activity = 0;");		
		sb.appendln("sample_period  = 1000;");	
		
		sb.appendln("use_read_write = 0;");		
		sb.appendln("read_request_size = 128;");		
		sb.appendln("write_request_size = 640;");		
		sb.appendln("write_reply_size = 640;");		
		sb.appendln("read_reply_size = 128;");		
		sb.appendln("use_noc_latency=0;");		
		sb.appendln("seed = " + seed + ";");		// exposed
		sb.appendln("watch_out = -;");		
		sb.appendln("watch_file = watch_list;");		
		sb.appendln("stats_out = output.m;");
		
		sb.appendln("tech_file = techfile_65;");
		sb.appendln("channel_width = "+ channel_width + ";"); // exposed
		
		return sb.toString();
	}

	@Override
	public Map<String, String> getParameters() {
		SimpleStringMap map = new SimpleStringMap();
		map.putAll(this.flowControlConfig.getParameters());
		map.putAll(this.routerArchConfig.getParameters());
		
		map.put("seed", seed);
		map.put("channel_width", channel_width);
		map.put("bw_ratio", bw_ratio);
		
		return map;
	}



}
