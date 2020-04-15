package edu.columbia.sebastien.autobooksim.config;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.ExtendedStringBuilder;
import ch.epfl.general_libraries.utils.SimpleMap;
import ch.epfl.general_libraries.utils.SimpleStringMap;
import edu.columbia.sebastien.autobooksim.AbstractBooksimConfigBlock;


public class FlowControlConfig extends AbstractBooksimConfigBlock {
	
	private int  read_request_begin_vc;
	private int  read_request_end_vc;
	private int  write_reply_begin_vc;
	private int  write_reply_end_vc;
	private int  read_reply_begin_vc;
	private int  read_reply_end_vc;
	private int  write_request_begin_vc;
	private int  write_request_end_vc;
	
	private boolean default_ = true;
	
	public FlowControlConfig() {
		this.read_request_begin_vc = 0;
		this.read_request_end_vc = 1;
		this.write_reply_begin_vc = 2;
		this.write_reply_end_vc = 3;
		this.read_reply_begin_vc = 2;
		this.read_reply_end_vc = 3;
		this.write_request_begin_vc = 0;
		this.write_request_end_vc = 1;		
	}
	
	
	public FlowControlConfig(
			@ParamName(name="read_request_begin_vc", default_="0") int read_request_begin_vc,
			@ParamName(name="read_request_end_vc", default_="0") int read_request_end_vc,
			@ParamName(name="write_reply_begin_vc", default_="0") int write_reply_begin_vc,
			@ParamName(name="write_reply_end_vc", default_="0") int write_reply_end_vc,
			@ParamName(name="read_reply_begin_vc", default_="0") int read_reply_begin_vc,
			@ParamName(name="read_reply_end_vc", default_="0") int read_reply_end_vc,
			@ParamName(name="write_request_begin_vc", default_="0") int write_request_begin_vc,
			@ParamName(name="write_request_end_vc", default_="0") int write_request_end_vc) {
		
		this.default_ = false; // flow control parameters may have changed
	
		this.read_request_begin_vc = read_request_begin_vc;
		this.read_request_end_vc =read_request_end_vc;
		this.write_reply_begin_vc = write_reply_begin_vc;
		this.write_reply_end_vc = write_reply_end_vc;
		this.read_reply_begin_vc = read_reply_begin_vc;
		this.read_reply_end_vc = read_reply_end_vc;
		this.write_request_begin_vc = write_request_begin_vc;
		this.write_request_end_vc = write_request_end_vc;
	
	}


	@Override
	public String getConfigBlock() {
		ExtendedStringBuilder sb = new ExtendedStringBuilder();
		
		sb.appendln("read_request_begin_vc = " + read_request_begin_vc + ";");
		sb.appendln("read_request_end_vc = " + read_request_end_vc + ";");
		sb.appendln("write_reply_begin_vc = " + write_reply_begin_vc + ";");
		sb.appendln("write_reply_end_vc = " + write_reply_end_vc + ";");
		sb.appendln("read_reply_begin_vc = " + read_reply_begin_vc + ";");
		sb.appendln("read_reply_end_vc = " + read_reply_end_vc + ";");
		sb.appendln("write_request_begin_vc = " + write_request_begin_vc + ";");
		sb.appendln("write_request_end_vc = " + write_request_end_vc + ";");
		
		// this parameters are not exposed to constructor yet
		
		sb.appendln("vc_buf_size = 8;");
		sb.appendln("vc_buf_size_injection = 16;");
		sb.appendln("wait_for_tail_credit = 0;");
		sb.appendln("classes = 1;");
		
		
		return sb.toString();
	}


	@Override
	public Map<String, String> getParameters() {
		if (default_) {
			return SimpleMap.getMap("FlowControlParameters", "defaults");
		} else {
			SimpleStringMap map = new SimpleStringMap();
			map.put("read_request_begin_vc", read_request_begin_vc);
			map.put("read_request_end_vc", read_request_end_vc);
			map.put("write_reply_begin_vc", write_reply_begin_vc);
			map.put("write_reply_end_vc", write_reply_end_vc);
			map.put("read_reply_begin_vc", read_reply_begin_vc);
			map.put("read_reply_end_vc", read_reply_end_vc);
			map.put("write_request_begin_vc", write_request_begin_vc);
			map.put("write_request_end_vc", write_request_end_vc);
			return map;
		}
	}

}
