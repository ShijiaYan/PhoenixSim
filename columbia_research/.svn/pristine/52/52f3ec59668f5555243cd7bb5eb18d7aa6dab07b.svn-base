package edu.columbia.ke.component.burst_assembly;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import edu.columbia.lrl.general.Evt;
import edu.columbia.lrl.general.Message;

public class SizeBasedBurstAssembler extends AbstractBurstAssembler {
	protected int packetWindow;

	public SizeBasedBurstAssembler(
			@ParamName(name = "max packet number", default_ = "10") int packetWindow) {
		this.packetWindow = packetWindow;
	}

	@Override
	public String getBurstAssemblerName() {
		return "Size-Based BA";
	}

	@Override
	public void addMessage(Evt e) {
		double now = e.getTimeNS();
		Message m = e.getMessage();
		if (this.burst == null)
			burst = new BurstMessage();
		
		this.burst.addMessage(m);

		if (this.burst.size() >= packetWindow) {
			Evt next = new Evt(now, this, nextDest, e);
			next.setMessage(burst);
			lwSimExperiment.manager.queueEvent(next);
			burst = new BurstMessage();
			return;
		}
	}
	
	@Override
	public Map<String, String> getAllParameters() {
		Map<String, String> map = super.getAllParameters();
		map.put("window size", packetWindow+"");
		return map;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		SizeBasedBurstAssembler ba = (SizeBasedBurstAssembler) super.clone();
		ba.packetWindow = this.packetWindow;
		return ba;
	}
	
	

}
