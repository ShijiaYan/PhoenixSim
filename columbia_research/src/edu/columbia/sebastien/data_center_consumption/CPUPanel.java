package edu.columbia.sebastien.data_center_consumption;

import java.awt.GridBagConstraints;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

class CPUPanel extends ElementPanel {
	
	CPUPanel(ConsumptionFrame parent) {
		super("CPU", parent);	
	}
		
	public void init(double activCon, double idleCon, double subUt, double gops) {
		setActiveCons(activCon);
		setPassiveCon(idleCon);
		setSubUtil(subUt);
		setMetricTotal(gops);
		calculate();
	}
	
	protected String getSpecificMetric() {
		return "GOPS";
	}




}