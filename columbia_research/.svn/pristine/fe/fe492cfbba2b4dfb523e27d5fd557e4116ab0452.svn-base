package edu.columbia.sebastien.data_center_consumption;

public class RamPanel extends ElementPanel {

		
	public RamPanel(ConsumptionFrame parent) { 
		super("RAM", parent);
	}
	
	public void init(double activCon, double idleCon, double subUt, double gops) {
		setActiveCons(activCon);
		setPassiveCon(idleCon);
		setSubUtil(subUt);
		setMetricTotal(gops);
		calculate();		
	}
		
	protected String getSpecificMetric() {
		return "Gb/s";
	}




}
