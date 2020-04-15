package edu.columbia.ke.component.burst_assembly;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import edu.columbia.lrl.general.Evt;
import edu.columbia.lrl.general.Message;

public class DeadlineAwareBurstAssembler extends TimeBasedBurstAssembler {
	private int packetWindow;
	
	public DeadlineAwareBurstAssembler(
			@ParamName(name = "max packet number", default_ = "10") int packetWindow) {
		super(100);
		this.assembleEndTime = java.lang.Double.MAX_VALUE;
		this.packetWindow = packetWindow;
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

		if (updateAssembleEndTime(now, m)) {
			Evt self = new Evt(getAssembleEndTime(), this, this, burst.index);
			self.setMessage(this.burst);
			lwSimExperiment.manager.queueEvent(self);
		}
	}

	@Override
	public boolean updateAssembleEndTime(double now, Message m) {
		if (this.burst.getDeadline() < this.assembleEndTime) {
			this.assembleEndTime = this.burst.getDeadline();
			return true;
		} else
			return false;
	}
	
	@Override
	public String getBurstAssemblerName() {
		return "Deadline-aware BA";
	}
	
	@Override
	public Map<String, String> getAllParameters() {
		Map<String, String> map = super.getAllParameters();
		map.put("window size", packetWindow+"");
		return map;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		DeadlineAwareBurstAssembler ba = (DeadlineAwareBurstAssembler) super.clone();
		ba.packetWindow = this.packetWindow;
		return ba;
	}

}
