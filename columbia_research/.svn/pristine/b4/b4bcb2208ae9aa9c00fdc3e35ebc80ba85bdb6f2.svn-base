package edu.columbia.sebastien.data_center_consumption;

import java.awt.Color;

import javax.swing.JTextField;

public class MyJTextField extends JTextField {
	
	public MyJTextField(int i) {
		super(i);
	}
	
	public boolean virtualState = true;
	public double value;
	
	public boolean setVirtuallyEnabled(boolean b) {
		boolean ret = virtualState;
		virtualState = b;
		return ret;
	}
	
	public boolean isVirtuallyEnabled() {
		if (isEnabled() == false) return false;
		return virtualState;
	}
	
	public void setEnabled(boolean b) {
		super.setEnabled(b);
		if (b) {
			this.setBackground(Color.WHITE);
		} else {
			this.setBackground(Color.GRAY);
		}
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value2) {
		this.value = value2;
	}

	public void updateValue() {
		try {
			value = Double.parseDouble(getText());
		}
		catch (Exception e) {}
	}
}