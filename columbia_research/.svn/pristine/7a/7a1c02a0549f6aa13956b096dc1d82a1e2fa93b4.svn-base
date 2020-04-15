package edu.columbia.ke.component.burst_assembly;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import edu.columbia.lrl.general.Evt;
import edu.columbia.lrl.general.Message;

public class TimeBasedBurstAssembler extends AbstractBurstAssembler {
	protected double windowTime;
	protected double assembleEndTime;

	public double getAssembleEndTime() {
		return assembleEndTime;
	}

	public TimeBasedBurstAssembler(
			@ParamName(name = "max assemble window", default_ = "1000") double maxTime) {
		this.windowTime = maxTime;
	}

	@Override
	public String getBurstAssemblerName() {
		// TODO Auto-generated method stub
		return "Time-Based BA";
	}

	@Override
	public void addMessage(Evt e) {
		double now = e.getTimeNS();
		Message m = e.getMessage();
		if (this.burst == null)
			burst = new BurstMessage();
		
		this.burst.addMessage(m);

		if (updateAssembleEndTime(now, m)) {
			double emitTime = getAssembleEndTime();
			scheduleBurstEmission(emitTime);
		}
	}
	
	protected boolean updateAssembleEndTime(double now, Message m){
		if (this.burst.size() == 1) {
			this.assembleEndTime = now + windowTime;
			return true;
		} else
			return false;
	}
	
	protected void scheduleBurstEmission(double emitTime){
		Evt self = new Evt(emitTime, this, this, burst.index);
		self.setMessage(this.burst);
		lwSimExperiment.manager.queueEvent(self);
	}

	@Override
	public Map<String, String> getAllParameters() {
		Map<String, String> map = super.getAllParameters();
		map.put("window time", windowTime+"");
		return map;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		TimeBasedBurstAssembler ba = (TimeBasedBurstAssembler) super.clone();
		ba.windowTime = this.windowTime;
		return ba;
	}
	
	

}
