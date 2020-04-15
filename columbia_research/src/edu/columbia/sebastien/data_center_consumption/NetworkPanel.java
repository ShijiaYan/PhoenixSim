package edu.columbia.sebastien.data_center_consumption;

class NetworkPanel extends ElementPanel {
	NetworkPanel(ConsumptionFrame parent) {
		super("Network", parent);
	}
	
	public void init(double active, double idle, double subUt, double bandwidthGbs) {
		setActiveCons(active);
		setPassiveCon(idle);
		setSubUtil(subUt);
		setMetricTotal(bandwidthGbs);
		calculate();		
	}

	@Override
	protected String getSpecificMetric() {
		return "Gb/s";
	}


}