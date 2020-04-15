package edu.columbia.sebastien.data_center_consumption;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.TreeSet;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.batik.ext.swing.GridBagConstants;

import ch.epfl.general_libraries.math.Rounder;
import ch.epfl.general_libraries.utils.More;
import ch.epfl.general_libraries.utils.MoreArrays;

abstract class ElementPanel {
	
	private String element;
	
	private ConsumptionFrame parent;

	protected MyJTextField averageConsumption;
	protected MyJTextField activeConsumption;
	protected MyJTextField passiveConsumption;
	protected MyJTextField proportionality;
	protected MyJTextField subUtilization;
	protected JSlider partInOverall;

	protected MyJTextField partInNumber = new MyJTextField(6);
	
	protected MyJTextField metricTotal;
	protected MyJTextField deliveredMetric;
	protected MyJTextField energyPerMetric;
	protected MyJTextField energyPerDeliveredMetric;
	protected MyJTextField energyAt100;
	
	protected MyJTextField[] allFields;
	
	protected JButton cal;
	
	private boolean inUpdateMode = false;
	
	int size = 5;
	
	protected abstract String getSpecificMetric();
	
	public String saveToString() {
		StringBuilder sb = new StringBuilder();
		sb.append(activeConsumption.getText()+",");
		sb.append(passiveConsumption.getText()+",");
		sb.append(proportionality.getText()+",");
		sb.append(subUtilization.getText()+",");
		sb.append(averageConsumption.getText()+",");
		sb.append(metricTotal.getText()+",");
		sb.append(deliveredMetric.getText()+",");
		sb.append(energyPerMetric.getText()+",");
		sb.append(energyPerDeliveredMetric.getText()+",");
		sb.append(energyAt100.getText()+",");
		return sb.toString();
	}
	
	public void restore(String s) {
		String[] tab = s.split(",");
		inUpdateMode = true;
		
		activeConsumption.setText(tab[0]);
		passiveConsumption.setText(tab[1]);
		proportionality.setText(tab[2]);
		subUtilization.setText(tab[3]);
		averageConsumption.setText(tab[4]);
		metricTotal.setText(tab[5]);
		deliveredMetric.setText(tab[6]);
		energyPerMetric.setText(tab[7]);
		energyPerDeliveredMetric.setText(tab[8]);
		energyAt100.setText(tab[9]);
		inUpdateMode = false;
	}

	public ElementPanel(String element, ConsumptionFrame parent) {
		this.element = element;
		this.parent = parent;
		partInOverall = new JSlider(0, 100);

		averageConsumption = new MyJTextField(size);
		activeConsumption = new MyJTextField(size);
		passiveConsumption = new MyJTextField(size);
		proportionality = new MyJTextField(size);
		subUtilization = new MyJTextField(size);

		metricTotal = new MyJTextField(size);
		deliveredMetric = new MyJTextField(size);
		energyPerMetric = new MyJTextField(size);
		energyPerDeliveredMetric = new MyJTextField(size);
		energyAt100 = new MyJTextField(size);

		allFields = new MyJTextField[]{activeConsumption, passiveConsumption, proportionality,
				subUtilization, averageConsumption, metricTotal, deliveredMetric, energyPerMetric,
				energyPerDeliveredMetric, energyAt100};

		for (final MyJTextField f : allFields) {
			f.addMouseListener(new MouseListener() {

				@Override public void mouseReleased(MouseEvent arg0) {}		
				@Override public void mousePressed(MouseEvent arg0) {}		
				@Override public void mouseExited(MouseEvent arg0) {}	
				@Override public void mouseEntered(MouseEvent arg0) {}	
				@Override public void mouseClicked(MouseEvent arg0) {
					if (arg0.getClickCount() == 2) {
						updateInit((JTextField)arg0.getSource(), false);
						for (MyJTextField ff : allFields) {
							ff.setVirtuallyEnabled(true);
						}
					}
				}
			});

			f.getDocument().addDocumentListener(new DocumentListener() {
				public void changedUpdate(DocumentEvent e) {
					update(e);
				}
				public void removeUpdate(DocumentEvent e) {
					update(e);
				}
				public void insertUpdate(DocumentEvent e) {
					update(e);
				}
				private void update(DocumentEvent e) {
					f.updateValue();
					if (!inUpdateMode) {
						updateSystem(f);
						for (MyJTextField ff : allFields) {
							ff.setVirtuallyEnabled(true);
						}
					}					
				}
			});
		}

/*		for (final MyJTextField f : metricArray) {
			if (f != averageConsumption && f != subUtilization) {
				f.addMouseListener(new MouseListener() {

					@Override public void mouseReleased(MouseEvent arg0) {}		
					@Override public void mousePressed(MouseEvent arg0) {}		
					@Override public void mouseExited(MouseEvent arg0) {}	
					@Override public void mouseEntered(MouseEvent arg0) {}	
					@Override public void mouseClicked(MouseEvent arg0) {
						if (arg0.getClickCount() == 2) {
							updateInit((JTextField)arg0.getSource());
						}
					}
				});
			}

			f.getDocument().addDocumentListener(new DocumentListener() {
				public void changedUpdate(DocumentEvent e) {
					update(e);
				}
				public void removeUpdate(DocumentEvent e) {
					update(e);
				}
				public void insertUpdate(DocumentEvent e) {
					update(e);
				}

				private void update(DocumentEvent e) {
					if (!inUpdateMode) {
						updateSystem(f);
					}					
				}
			});
		}	*/	

		cal = new JButton("Calculate");
	}
	


	
	protected void updateSystem(MyJTextField f) {
		boolean previousMode = inUpdateMode;
		inUpdateMode = true;
		try {
			if (f == energyPerDeliveredMetric && hasEnergyPerDeliveredMetric()) {
				energyPerDeliveredMetric.setVirtuallyEnabled(false);

				if (deliveredMetric.isVirtuallyEnabled()) {
					boolean b = averageConsumption.setVirtuallyEnabled(false);
					_5setDeliveredMetricFromAverageEnergyPerDelivered();
					updateSystem(deliveredMetric);
					averageConsumption.setVirtuallyEnabled(b);
				} else if (averageConsumption.isVirtuallyEnabled()) {
					boolean b = deliveredMetric.setVirtuallyEnabled(false);
					_5setAverageFromDeliveredMetricEnergyPerDelivered();
					updateSystem(averageConsumption);
					deliveredMetric.setVirtuallyEnabled(b);
				} else {
					check5();
				}
				energyPerDeliveredMetric.setVirtuallyEnabled(true);
			}
			if (f == energyPerMetric && hasEnergyPerMetric()) {
				energyPerMetric.setVirtuallyEnabled(false);
				if (averageConsumption.isVirtuallyEnabled()) {
					boolean b = metricTotal.setVirtuallyEnabled(false);
					_4setAverageFromMetricEnergy();	
					updateSystem(averageConsumption);
					metricTotal.setVirtuallyEnabled(b);
				} else if (metricTotal.isVirtuallyEnabled()) {
					boolean b = averageConsumption.setVirtuallyEnabled(false);
					_4setMetricFromAverageEnergy();
					updateSystem(metricTotal);
					averageConsumption.setVirtuallyEnabled(b);
				} else {
					check4();
				}
				energyPerMetric.setVirtuallyEnabled(true);				
			}
			if (f == deliveredMetric && hasDeliveredMetric()) {
				deliveredMetric.setVirtuallyEnabled(false);
				if (energyPerDeliveredMetric.isVirtuallyEnabled()) {
					boolean b = averageConsumption.setVirtuallyEnabled(false);
					_5setEnergyPerDeliveredFromAverageDeliveredMetric();
					updateSystem(energyPerDeliveredMetric);
					averageConsumption.setVirtuallyEnabled(b);
				} else if (averageConsumption.isVirtuallyEnabled()) {
					boolean b = energyPerDeliveredMetric.setVirtuallyEnabled(false);
					_5setAverageFromDeliveredMetricEnergyPerDelivered();
					updateSystem(averageConsumption);
					energyPerDeliveredMetric.setVirtuallyEnabled(b);
				} else {
					check5();
				}
				if (metricTotal.isVirtuallyEnabled()) {
					boolean b = subUtilization.setVirtuallyEnabled(false);
					_3setMetricFromUtilDelivered();
					updateSystem(metricTotal);
					subUtilization.setVirtuallyEnabled(b);
				} else if (subUtilization.isVirtuallyEnabled()) {
					boolean b = metricTotal.setVirtuallyEnabled(false);
					_3setUtilFromMetricDelivered();
					updateSystem(subUtilization);
					metricTotal.setVirtuallyEnabled(b);
				} else {
					check3();
				}
				parent.updateSystem(f);
				deliveredMetric.setVirtuallyEnabled(true);
			}
			if (f == metricTotal && hasMetric()) {
				metricTotal.setVirtuallyEnabled(false);
				if (energyPerMetric.isVirtuallyEnabled()) {
					boolean b = averageConsumption.setVirtuallyEnabled(false);
					_4setEnergyFromAverageMetric();
					averageConsumption.setVirtuallyEnabled(b);
				} else if (averageConsumption.isVirtuallyEnabled()) {
					_4setAverageFromMetricEnergy();
					updateSystem(averageConsumption);
				} else {
					check4();
				}
				if (deliveredMetric.isVirtuallyEnabled()) {
					_3setDeliveredFromUtilMetric();
					updateSystem(deliveredMetric);
				} else if (subUtilization.isVirtuallyEnabled()) {
					_3setUtilFromMetricDelivered();
					updateSystem(subUtilization);
				} else {
					check3();
				}
				if (energyAt100.isVirtuallyEnabled()) {
					_6set100FromActiveMetric();
				} else if (activeConsumption.isVirtuallyEnabled()) {
					boolean prev = energyAt100.setVirtuallyEnabled(false);
					_6setActiveFromMetric100();
					updateSystem(activeConsumption);
					energyAt100.setVirtuallyEnabled(prev);
				} else {
					check6();
				}
				parent.updateSystem(f);
				metricTotal.setVirtuallyEnabled(true);
			}
			if (f == activeConsumption && hasActiveConsumption()) {
				activeConsumption.setVirtuallyEnabled(false);
				if (proportionality.isVirtuallyEnabled()) {
					_1setProportionalityFromActivePassive();
				} else if (passiveConsumption.isVirtuallyEnabled()) {
					_1setPassiveFromActivePropor();
				} else {
					check1();
				}
				if (energyAt100.isVirtuallyEnabled()) {
					_6set100FromActiveMetric();
				} else if (metricTotal.isVirtuallyEnabled()) {
					_6setMetricFromActive100();
					boolean prev = energyAt100.setVirtuallyEnabled(false);
					updateSystem(metricTotal);
					energyAt100.setVirtuallyEnabled(prev);
				} else {
					check6();
				}
				
				passiveConsumption.setVirtuallyEnabled(false);
				proportionality.setVirtuallyEnabled(false);
				if (averageConsumption.isVirtuallyEnabled()) {
					_2setAverageFromActivePassiveUtilization();
					updateSystem(averageConsumption);
				} else if (subUtilization.isVirtuallyEnabled()) {
					_2setSubUtilFromActivePassiveAverage();
					updateSystem(subUtilization);
				} else {
					check2();
				}	
				proportionality.setVirtuallyEnabled(true);
				passiveConsumption.setVirtuallyEnabled(true);
				
				activeConsumption.setVirtuallyEnabled(true);				
			}
			if (f == passiveConsumption && hasPassiveConsumption()) {
				passiveConsumption.setVirtuallyEnabled(false);
				if (proportionality.isVirtuallyEnabled()) {
					_1setProportionalityFromActivePassive();
				} else if (activeConsumption.isVirtuallyEnabled()) {
					boolean prev = proportionality.setVirtuallyEnabled(false);
					_1setActiveFromPassivePropor();
					updateSystem(activeConsumption);
					proportionality.setVirtuallyEnabled(prev);
				} else {
					check1();
				}
				
				activeConsumption.setVirtuallyEnabled(false);
				proportionality.setVirtuallyEnabled(false);
				if (averageConsumption.isVirtuallyEnabled()) {
					_2setAverageFromActivePassiveUtilization();
					updateSystem(averageConsumption);
				} else if (subUtilization.isVirtuallyEnabled()) {
					_2setSubUtilFromActivePassiveAverage();
					updateSystem(subUtilization);
				} else {
					check2();
				}	
				proportionality.setVirtuallyEnabled(true);
				activeConsumption.setVirtuallyEnabled(true);
				
				passiveConsumption.setVirtuallyEnabled(true);				
			}
			if (f == proportionality && hasProportionality()) {
				proportionality.setVirtuallyEnabled(false);
				if (passiveConsumption.isVirtuallyEnabled()) {
					_1setPassiveFromActivePropor();
				} else if (activeConsumption.isVirtuallyEnabled()) {
					boolean prev = passiveConsumption.setVirtuallyEnabled(false);
					_1setActiveFromPassivePropor();
					updateSystem(activeConsumption);
					passiveConsumption.setVirtuallyEnabled(prev);
				} else {
					check1();
				}
				

				if (averageConsumption.isVirtuallyEnabled()) {
					_2setAverageFromActivePassiveUtilization();
					activeConsumption.setVirtuallyEnabled(false);
					passiveConsumption.setVirtuallyEnabled(false);
					updateSystem(averageConsumption);
					passiveConsumption.setVirtuallyEnabled(true);
					activeConsumption.setVirtuallyEnabled(true);
				} else if (subUtilization.isVirtuallyEnabled()) {
					_2setSubUtilFromActivePassiveAverage();
					activeConsumption.setVirtuallyEnabled(false);
					passiveConsumption.setVirtuallyEnabled(false);
					updateSystem(subUtilization);
					passiveConsumption.setVirtuallyEnabled(true);
					activeConsumption.setVirtuallyEnabled(true);
				} else if (activeConsumption.isVirtuallyEnabled() && passiveConsumption.isVirtuallyEnabled()) {
					_XsetActiveFromProporAverageUtil();
				//	activeConsumption.setVirtuallyEnabled(false);
				//	_1setPassiveFromActivePropor();
				//	activeConsumption.setVirtuallyEnabled(true);
					updateSystem(activeConsumption);
				} else {
					check2();
				}	

				
				proportionality.setVirtuallyEnabled(true);				
			}
			if (f == subUtilization && hasSubUtilization()) {
				subUtilization.setVirtuallyEnabled(false);
				if (passiveConsumption.isVirtuallyEnabled() && proportionality.isVirtuallyEnabled()) {
					if (hasActiveConsumption()) {
						_2setPassiveFromActiveAverageUtil();
						_1setProportionalityFromActivePassive();
					}
				} else if (passiveConsumption.isVirtuallyEnabled() && activeConsumption.isVirtuallyEnabled()) {
					if (hasProportionality()) {
						_XsetActiveFromProporAverageUtil();
						_1setPassiveFromActivePropor();
					}
				} else if (activeConsumption.isVirtuallyEnabled() && proportionality.isVirtuallyEnabled()) {
					if (hasPassiveConsumption()) {
						_2setActiveFromPassiveAverageUtil();
						_1setProportionalityFromActivePassive();
					}
				} else if (averageConsumption.isVirtuallyEnabled()) {
					if (hasActiveConsumption() && hasPassiveConsumption()) {
						_2setAverageFromActivePassiveUtilization();
						activeConsumption.setVirtuallyEnabled(false);
						passiveConsumption.setVirtuallyEnabled(false);
						updateSystem(averageConsumption);
						passiveConsumption.setVirtuallyEnabled(true);
						activeConsumption.setVirtuallyEnabled(true);
					}
					if (hasActiveConsumption() && hasProportionality()) {
						_XsetAverageFromActiveProporUtilization();
						activeConsumption.setVirtuallyEnabled(false);
						proportionality.setVirtuallyEnabled(false);
						updateSystem(averageConsumption);
						proportionality.setVirtuallyEnabled(true);
						activeConsumption.setVirtuallyEnabled(true);						
					}
					if (hasPassiveConsumption() && hasProportionality()) {
						_YsetAverageFromPassiveProporUtilization();
						passiveConsumption.setVirtuallyEnabled(false);
						proportionality.setVirtuallyEnabled(false);
						updateSystem(averageConsumption);
						proportionality.setVirtuallyEnabled(true);
						passiveConsumption.setVirtuallyEnabled(true);					
					}
				} else {
					check2();
				} 
				
				if (metricTotal.isVirtuallyEnabled()) {
					boolean prev = deliveredMetric.setVirtuallyEnabled(false);
					_3setMetricFromUtilDelivered();
					updateSystem(metricTotal);
					deliveredMetric.setVirtuallyEnabled(prev);
				} else if (deliveredMetric.isVirtuallyEnabled()) {
					boolean prev = metricTotal.setVirtuallyEnabled(false);
					_3setDeliveredFromUtilMetric();
					updateSystem(deliveredMetric);
					metricTotal.setVirtuallyEnabled(prev);
				} else {
					check3();
				}
				subUtilization.setVirtuallyEnabled(true);
			}
			
			if (f == averageConsumption && hasAverageConsumption()) {
				averageConsumption.setVirtuallyEnabled(false);
				if (deliveredMetric.isVirtuallyEnabled()) {
					boolean prev = energyPerDeliveredMetric.setVirtuallyEnabled(false);
					_5setDeliveredMetricFromAverageEnergyPerDelivered();
					updateSystem(deliveredMetric);
					energyPerDeliveredMetric.setVirtuallyEnabled(prev);
				} else if (energyPerDeliveredMetric.isVirtuallyEnabled()) {
					boolean prev = deliveredMetric.setVirtuallyEnabled(false);
					_5setEnergyPerDeliveredFromAverageDeliveredMetric();
					deliveredMetric.setVirtuallyEnabled(prev);
				} else {
					check5();
				}
				if (metricTotal.isVirtuallyEnabled()) {
					boolean prev = energyPerMetric.setVirtuallyEnabled(false);
					_4setMetricFromAverageEnergy();
					updateSystem(metricTotal);
					energyPerMetric.setVirtuallyEnabled(prev);
				} else if (energyPerMetric.isVirtuallyEnabled()) {
					boolean prev = metricTotal.setVirtuallyEnabled(false);
					_4setEnergyFromAverageMetric();
					metricTotal.setVirtuallyEnabled(prev);
				} else {
					check4();
				}
				if (subUtilization.isVirtuallyEnabled()) {
					if (hasActiveConsumption() && hasPassiveConsumption()) {
						_2setSubUtilFromActivePassiveAverage();
						activeConsumption.setVirtuallyEnabled(false);
						passiveConsumption.setVirtuallyEnabled(false);
						updateSystem(subUtilization);
						passiveConsumption.setVirtuallyEnabled(true);
						activeConsumption.setVirtuallyEnabled(true);
					}
					if (hasActiveConsumption() && hasProportionality()) {
						_XsetSubUtilFromActiveProporAverage();
						activeConsumption.setVirtuallyEnabled(false);
						proportionality.setVirtuallyEnabled(false);
						updateSystem(subUtilization);
						proportionality.setVirtuallyEnabled(true);
						activeConsumption.setVirtuallyEnabled(true);						
					}
					if (hasPassiveConsumption() && hasProportionality()) {
						_YsetSubUtilFromPassiveProporAverage();
						passiveConsumption.setVirtuallyEnabled(false);
						proportionality.setVirtuallyEnabled(false);
						updateSystem(subUtilization);
						proportionality.setVirtuallyEnabled(true);
						passiveConsumption.setVirtuallyEnabled(true);					
					}
				} else if (passiveConsumption.isVirtuallyEnabled() && proportionality.isVirtuallyEnabled()) {
					_2setPassiveFromActiveAverageUtil();
					_1setProportionalityFromActivePassive();
				} else if (passiveConsumption.isVirtuallyEnabled() && activeConsumption.isVirtuallyEnabled()) {
					_XsetActiveFromProporAverageUtil();
					updateSystem(activeConsumption);
				} else if (activeConsumption.isVirtuallyEnabled() && proportionality.isVirtuallyEnabled()) {
					_2setActiveFromPassiveAverageUtil();
					updateSystem(activeConsumption);
				} else {
					check2();
				} 					
				parent.updateSystem(f);
				averageConsumption.setVirtuallyEnabled(true);
			}
			
			if (f == energyAt100) {
				energyAt100.setVirtuallyEnabled(false);
				if (metricTotal.isVirtuallyEnabled()) {
					_6setMetricFromActive100();
					boolean prev = activeConsumption.setVirtuallyEnabled(false);
					updateSystem(metricTotal);
					activeConsumption.setVirtuallyEnabled(prev);
				} else if (activeConsumption.isVirtuallyEnabled()) {
					_6setActiveFromMetric100();
					boolean prev = metricTotal.setVirtuallyEnabled(false);
					updateSystem(activeConsumption);
					metricTotal.setVirtuallyEnabled(prev);
				} else {
					check6();
				}
			}
			

			
		//<Integer> exclude = new TreeSet<Integer>();
		//	updateSystem(f, 0, exclude);
		//	checkSubUtil();
		//	checkProportionality();
		}
		finally {
			inUpdateMode = previousMode;
		}
	}
	
	public void updateSlider() {
		if (hasAverageConsumption() && parent.hasTotalConsumption() && parent.hasVariablePowerOverhead()) {
			double part = getAverageConsumption()/(parent.getTotalConsumption()/(1+parent.getVariablePowerOverhead()));
			if (part >= 0 && part <= 1) {
				partInOverall.setValue((int)(part*100));
			}
		}		
	}
	
	private void check6() {
		// TODO Auto-generated method stub
		
	}

	private void _YsetSubUtilFromPassiveProporAverage() {
		double passive = getPassiveConsumption();
		double average = getAverageConsumption();
		double top = -passive + passive*getProportionality() + average - average*getProportionality();
		double down = passive*getProportionality();
		setSubUtil(top/down);
	}

	private MyJTextField _YsetAverageFromPassiveProporUtilization() {
		double top = (1 - getProportionality() + getSubUtil()*getProportionality())*getPassiveConsumption();
		double down = (1 - getProportionality());
		return setAverageConsumption(top/down);
	}

	private void check1() {
		// TODO Auto-generated method stub
		
	}

	private void check2() {
		// TODO Auto-generated method stub
		
	}

	private void check3() {
		// TODO Auto-generated method stub
		
	}

	private void check4() {
		// TODO Auto-generated method stub
		
	}

	private void check5() {
		// TODO Auto-generated method stub
		
	}
	
	int[][] mappings = new int[][]{{0,1,2},{0,1,3,4},{3,5,6},{4,5,7},{4,6,8},{0,2,3,4},{0,5,9}};
	int[][] revMapping = new int[][]{{2,6,1},{2,1},{6,1},{2,3,6},{2,4,5,6}, {3,4},{3,5},{4},{5}};
	
	private boolean[] getCurrentStatus(JTextField changed, boolean newStatus) {
		boolean[] flags = new boolean[10];
		for (int i = 0 ; i < flags.length ; i++) {
			flags[i] = allFields[i].isEnabled();
			if (allFields[i] == changed) {
				flags[i] = newStatus;
			}
		}
		return flags;
	}
	
	public void setAverageEnabled(boolean f) {
		averageConsumption.setEnabled(f);
	}	
	
	public void setMetricEnabled(boolean f) {
		metricTotal.setEnabled(f);
	}
	
	public void setDeliveredMetricEnabled(boolean f) {
		deliveredMetric.setEnabled(f);	
	}	
	
/*	public boolean isAverageEnabled() {
		return averageConsumption.isEnabled();
	}
	

	public boolean isDeliveredMetricEnabled() {
		return deliveredMetric.isEnabled();
	}	
	
	public boolean isMetricEnabled() {
		return metricTotal.isEnabled();
	}*/
	
	protected boolean updateInit(JTextField updated, boolean newStatus) {
		modCount++;
		boolean[] flags;
		if (updated != null) {
			newStatus = !updated.isEnabled();
		}
		flags = getCurrentStatus(updated, newStatus);
		int initialSum = MoreArrays.sum(flags);
		int sum, newSum;
		do {
			sum = MoreArrays.sum(flags);		
			for (int i = 0 ; i < mappings.length ; i++) {
				flags = updateEqN(flags, i, newStatus);
			}
			newSum = MoreArrays.sum(flags);
		} while (newSum != sum);
		
		boolean[] prevState = new boolean[flags.length];
		for (int i = 0 ; i < flags.length ; i++) {
			prevState[i] = allFields[i].isEnabled();
			allFields[i].setEnabled(flags[i]);
		}
		
		if (updated != null) {
			// called locally
			for (int i = 0 ; i < flags.length ; i++) {
				if (prevState[i] != flags[i] && (allFields[i] == averageConsumption || allFields[i] == metricTotal || allFields[i] == deliveredMetric)) {
					parent.updateEnabled(this, flags[i]);
				}
			}
			return false;
		} else {
			// called from frame
			return newSum == initialSum;
		}
	}
	
	private int modCount = 0;
	private int[] modCountPerField = new int[10];
	
	private boolean[] updateEqN(boolean[] flags, int n, boolean newStatus) {
		int sum = 0;
		for (int i = 0 ; i < mappings[n].length ; i++) {
			sum += flags[mappings[n][i]] ? 1 : 0;
		}
		if (sum == 1) {
			if (newStatus == false) {
				for (int i = 0 ; i < mappings[n].length ; i++) {
					flags[mappings[n][i]] = false;
					modCountPerField[mappings[n][i]] = modCount;
				}	
			} else {
				// enable one among the disabled of the group
				int maxModCount = 0;
				int maxModCountIndex = 0;
				for (int i = 0 ; i < mappings[n].length ; i++) {
					if (flags[mappings[n][i]] == false) {
						if (modCountPerField[mappings[n][i]] > maxModCount) {
							maxModCount = modCountPerField[mappings[n][i]];
							maxModCountIndex = mappings[n][i];
						}
					}
					flags[maxModCountIndex] = true;
				}
			}
		}
		return flags;
	}


	
	private MyJTextField _1setProportionalityFromActivePassive() {
		if (hasPassiveConsumption() && hasActiveConsumption()) {
			double diff = getActiveConsumption()- getPassiveConsumption();
			return setProportionality(diff/getActiveConsumption());
		}
		return null;
	}
	
	private MyJTextField _1setPassiveFromActivePropor() {
		if (hasActiveConsumption() && hasProportionality()) {
			return setPassiveCon((1-getProportionality())*getActiveConsumption());		
		}
		return null;
	}
	
	private MyJTextField _1setActiveFromPassivePropor() {
		if (hasPassiveConsumption() && hasProportionality()) {
			return setActiveCons(getPassiveConsumption()/(1-getProportionality()));
		}
		return null;
	}	
	
	private MyJTextField _XsetActiveFromProporAverageUtil() {
		if (hasAverageConsumption() && hasProportionality() && hasSubUtilization()) {
			double up = getAverageConsumption();
			double down = 1 - getProportionality() + getSubUtil()*getProportionality();
			return setActiveCons(up/down, true);
		}
		return null;
	}

	private MyJTextField _XsetSubUtilFromActiveProporAverage() {
		if (hasActiveConsumption() && hasProportionality() && hasAverageConsumption()) {
			double ppa = getActiveConsumption()*getProportionality();
			double up = getAverageConsumption() - getActiveConsumption() + ppa;
			return setSubUtil(up/ppa);
		}
		return null;
	}

	private MyJTextField _XsetAverageFromActiveProporUtilization() {
		if (hasActiveConsumption() && hasProportionality() && hasSubUtilization()) {
			double ppa = getActiveConsumption()*getProportionality();
			double pow = getActiveConsumption() - ppa + (getSubUtil()*ppa);
			return setAverageConsumption(pow);
		}
		return null;
	}

	private MyJTextField _XsetProporFromActiveAverageUtil() {
		if (hasAverageConsumption() && hasActiveConsumption() && hasSubUtilization()) {
			double up = getAverageConsumption() - getActiveConsumption();
			double down = (getSubUtil()*getActiveConsumption()) - getActiveConsumption();
			return setProportionality(up/down);
		}
		return null;
	}

	private MyJTextField _2setPassiveFromActiveAverageUtil() {
		if (hasProportionality() && hasActiveConsumption() && hasSubUtilization()) {
			double up = getProportionality() - getActiveConsumption()*getSubUtil();
			double down = (1 - getSubUtil());
			return setPassiveCon(up/down);
		}
		return null;
	}
	
	private MyJTextField _2setActiveFromPassiveAverageUtil() {
		if (hasPassiveConsumption() && hasSubUtilization() && hasAverageConsumption()) {
			double passive = getPassiveConsumption();
			double top = getAverageConsumption() - passive + (getSubUtil()/passive);
			return setActiveCons(top/getSubUtil());
		}
		return null;
	}	

	private MyJTextField _2setSubUtilFromActivePassiveAverage() {
		if (hasAverageConsumption() && hasPassiveConsumption() && hasActiveConsumption()) {
			double up = getAverageConsumption() - getPassiveConsumption();
			double down = getActiveConsumption() - getPassiveConsumption();
			return setSubUtil(up/down);
		}
		return null;
	}

	private MyJTextField _2setAverageFromActivePassiveUtilization() {
		if (hasSubUtilization() && hasPassiveConsumption() && hasActiveConsumption()) {
			double U = getSubUtil();
			double cons = U*getActiveConsumption() + (1-U)*getPassiveConsumption();
			return setAverageConsumption(cons);
		}
		return null;
	}	
	
	private MyJTextField _3setUtilFromMetricDelivered() {
		if (hasDeliveredMetric() && hasMetric()) {
			return setSubUtil(getDeliveredMetric()/getMetric());
		}
		return null;
	}

	private MyJTextField _3setMetricFromUtilDelivered() {
		if (hasDeliveredMetric() && hasSubUtilization()) {
			return setMetricTotal(getDeliveredMetric()/getSubUtil());
		}
		return null;
	}

	private MyJTextField _3setDeliveredFromUtilMetric() {
		if (hasMetric() && hasSubUtilization()) {
			return setDeliveredMetric(getMetric()*getSubUtil());
		}
		return null;
	}

	private MyJTextField _4setAverageFromMetricEnergy() {
		if (hasMetric() && hasEnergyPerMetric()) {
			return setAverageConsumption(getMetric()*getEnergyPerMetric());
		}
		return null;
	}

	private MyJTextField _4setMetricFromAverageEnergy() {
		if (hasAverageConsumption() && hasEnergyPerMetric()) {
			return setMetricTotal(getAverageConsumption()/getEnergyPerMetric());
		}
		return null;
	}

	private MyJTextField _4setEnergyFromAverageMetric() {
		if (hasAverageConsumption() && hasMetric()) {
			return setEnergyPerMetric(getAverageConsumption()/getMetric());
		}
		return null;
	}	

	private MyJTextField _5setEnergyPerDeliveredFromAverageDeliveredMetric() {
		if (hasAverageConsumption() && hasDeliveredMetric()) {
			return setEnergyPerDeliveredMetric(getAverageConsumption()/getDeliveredMetric());
		}
		return null;
	}

	private MyJTextField _5setAverageFromDeliveredMetricEnergyPerDelivered() {
		if (hasEnergyPerDeliveredMetric() && hasDeliveredMetric()) {
			return setAverageConsumption(getEnergyPerDeliveredMetric()*getDeliveredMetric());
		}
		return null;
	}

	private MyJTextField _5setDeliveredMetricFromAverageEnergyPerDelivered() {
		if (hasAverageConsumption() && hasEnergyPerDeliveredMetric()) {
			return setDeliveredMetric(getAverageConsumption()/getEnergyPerDeliveredMetric());
		}
		return null;
	}
	
	private MyJTextField _6setActiveFromMetric100() {
		if (hasMetric() && hasEnergy100()) {
			return setActiveCons(getEnergy100()*getMetric());
		}
		return null;
	}



	private MyJTextField _6setMetricFromActive100() {
		if (hasActiveConsumption() && hasEnergy100()) {
			return setMetricTotal(getActiveConsumption()/getEnergy100());
		}
		return null;
	}

	private MyJTextField _6set100FromActiveMetric() {
		if (hasActiveConsumption() && hasMetric()) {
			return setEnergy100(getActiveConsumption()/getMetric());
		}
		return null;
	}	
	
	private void checkSubUtil() {
		boolean okay = true;
		if (hasSubUtilization()) {
			if (getSubUtil() < 0 || getSubUtil() > 1) {
				subUtilization.setBackground(Color.RED);
				subUtilization.setToolTipText("Sub-utilization must be between 0 and 1");	
				okay = false;
			} 
		}
		if (okay) {
			subUtilization.setBackground(Color.WHITE);
			subUtilization.setToolTipText(null);								
		}		
	}
	
	private void checkProportionality() {
		boolean okay = true;
		if (hasProportionality()) {
			if (getProportionality() < 0 || getProportionality() > 1) {
				proportionality.setBackground(Color.RED);
				proportionality.setToolTipText("Proportionality must be between 0 and 1");	
				okay = false;
			} 
		}
		if (okay) {
			proportionality.setBackground(Color.WHITE);
			proportionality.setToolTipText(null);								
		}		
	}
	
	protected void calculate() {
		if (!hasDeliveredMetric()) {
			JTextField f1 = _5setDeliveredMetricFromAverageEnergyPerDelivered();
			JTextField f2 = _3setDeliveredFromUtilMetric();
			if (f1 != null && f2 != null)
				throw new IllegalStateException();
		}
		if (!hasEnergyPerDeliveredMetric()) {
			JTextField f1 = _5setEnergyPerDeliveredFromAverageDeliveredMetric();
		}
		if (!hasEnergyPerMetric()) {
			_4setEnergyFromAverageMetric();
		}
		if (!hasMetric()) {
			JTextField f1 = _3setMetricFromUtilDelivered();
			JTextField f2 = _4setMetricFromAverageEnergy();
			if (f1 != null && f2 != null)
				throw new IllegalStateException();	
		}
		if (!hasAverageConsumption()) {
			JTextField f1 = _2setAverageFromActivePassiveUtilization();
			JTextField f2 = _4setAverageFromMetricEnergy();
			JTextField f3 = _5setAverageFromDeliveredMetricEnergyPerDelivered();
			if (More.countNull(f1,f2,f3) < 2)
				throw new IllegalStateException();		
		}
		if (!hasSubUtilization()) {
			JTextField f1 = _2setSubUtilFromActivePassiveAverage();
			JTextField f2 = _3setUtilFromMetricDelivered();
			if (f1 != null && f2 != null)
				throw new IllegalStateException();				
		}
		if (!hasProportionality()) {
			_1setProportionalityFromActivePassive();
		}
		if (!hasPassiveConsumption()) {
			JTextField f1 = _1setPassiveFromActivePropor();
			JTextField f2 = _2setPassiveFromActiveAverageUtil();
			if (f1 != null && f2 != null)
				throw new IllegalStateException();				
		}
		if (!hasActiveConsumption()) {
			JTextField f1 = _1setActiveFromPassivePropor();
			JTextField f2 = _2setActiveFromPassiveAverageUtil();
			if (f1 != null && f2 != null)
				throw new IllegalStateException();							
		}
		if (!hasEnergy100()) {
			_6set100FromActiveMetric();
		}
		checkSubUtil();
		checkProportionality();
	}	

	GridBagConstraints build(JPanel p, int line) {

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = line;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstants.BOTH;
		c.anchor = GridBagConstants.WEST;

		JLabel label = new JLabel(element);
		p.add(label, c);

		c.gridx = 1;
		c.gridwidth = 3;

		partInOverall.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent arg0) {}	
			@Override
			public void mousePressed(MouseEvent arg0) {}			
			@Override
			public void mouseExited(MouseEvent arg0) {}			
			@Override
			public void mouseEntered(MouseEvent arg0) {}			
			@Override
			public void mouseClicked(MouseEvent arg0) {
				int clicks = arg0.getClickCount();
				if (clicks == 2) {
					boolean current = partInOverall.isEnabled();
					partInOverall.setEnabled(!current);
					partInNumber.setEnabled(!current);
				}
			}
		});
		partInOverall.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent arg0) {
				partInNumber.setText(partInOverall.getValue()/100d+"");
			}
		});
		p.add(partInOverall, c);
		
		partInNumber.setEditable(false);
		partInOverall.setEnabled(false);
		
		c.gridx = 4;
		c.gridwidth = 1;
		p.add(partInNumber, c);
		p.setBackground(Color.LIGHT_GRAY);

		c.gridx = 5;
		p.add(activeConsumption, c);

		c.gridx++;
		p.add(passiveConsumption, c);

		c.gridx++;
		p.add(proportionality, c);	

		c.gridx++;
		p.add(subUtilization, c);	
		
		c.gridx++;
		p.add(averageConsumption, c);

		c.gridx++;
		p.add(cal, c);
		
		cal.addActionListener(new ActionListener() {		
			@Override
			public void actionPerformed(ActionEvent arg0) {
				calculate();
			}
		});
		
		c.gridx++;
		JLabel cpuL = new JLabel(getSpecificMetric());
		p.add(cpuL, c);
		
		c.gridx++;
		p.add(metricTotal, c);
		
		c.gridx++;
		p.add(deliveredMetric, c);
		
		c.gridx++;
		p.add(energyPerMetric, c);
		
		c.gridx++;
		p.add(energyPerDeliveredMetric, c);	
		
		c.gridx++;
		p.add(energyAt100, c);			
		return c;
	}
	

	// get and set methods
	protected double getPassiveConsumption() {
		return passiveConsumption.getValue();
	}	
	protected double getActiveConsumption() {
		return activeConsumption.getValue();
	}	
	protected double getProportionality() {
		return proportionality.getValue();
	}	
	protected double getAverageConsumption() {
		return averageConsumption.getValue();
	}	
	protected double getSubUtil() {
		return subUtilization.getValue();
	}	
	protected double getMetric() {
		return metricTotal.getValue();
	}	
	protected double getDeliveredMetric() {
		return deliveredMetric.getValue();
	}	
	protected double getEnergyPerMetric() {
		return energyPerMetric.getValue();
	}	
	protected double getEnergyPerDeliveredMetric() {
		return energyPerDeliveredMetric.getValue();
	}	
	protected double getEnergy100() {
		return energyAt100.getValue();
	}		
	protected MyJTextField setSubUtil(double subUt) {
		return setTextField(subUtilization, subUt);
	}
	protected MyJTextField setProportionality(double prop) {	
		return setTextField(proportionality, prop);
	}
	protected MyJTextField setActiveCons(double activCon) {
		return setTextField(activeConsumption, activCon);
	}
	protected MyJTextField setActiveCons(double activCon, boolean force) {
		return setTextField(activeConsumption, activCon, force);
	}	
	protected MyJTextField setPassiveCon(double idleCon) {	
		return setTextField(passiveConsumption, idleCon);
	}	
	protected MyJTextField setAverageConsumption(double averageCon) {
		return setTextField(averageConsumption, averageCon);
	}	
	protected MyJTextField setMetricTotal(double metric) {
		return setTextField(metricTotal, metric);
	}	
	protected MyJTextField setDeliveredMetric(double metric) {
		return setTextField(deliveredMetric, metric);
	}	
	protected MyJTextField setEnergyPerMetric(double energy) {
		return setTextField(energyPerMetric, energy);
	}
	protected MyJTextField setEnergyPerDeliveredMetric(double energy) {
		return setTextField(energyPerDeliveredMetric, energy);
	}
	protected MyJTextField setEnergy100(double energy) {
		return setTextField(energyAt100, energy);
	}	
	
	protected void setPart(double part) {
		partInOverall.setValue((int)Math.round(part*100));
		part = Rounder.round(part, 4);
		partInNumber.setText(part+"");
	}	
	
	protected static MyJTextField setTextField(MyJTextField f, double value) {
		return setTextField(f, value, false);
	}
	
	protected static MyJTextField setTextField(MyJTextField f, double value, boolean force) {
		if (!f.isVirtuallyEnabled() && !force) throw new IllegalStateException();
		String newString = Rounder.roundString(value, 4);
		String oldString = f.getText();
		f.setText(newString);
		f.setValue(value);
		if (newString.equals(oldString)) {
			return null;
		} else {
			return f;
		}
	}
	
	
	// has methods
	public static boolean hasAValue(JTextField field) {
		try {
			double d = Double.parseDouble(field.getText());
			return true;
		}
		catch (Exception e) {
			return false;
		}
	}	
	protected boolean hasProportionality() {
		return hasAValue(proportionality);
	}	
	protected boolean hasActiveConsumption() {
		return hasAValue(activeConsumption);
	}	
	protected boolean hasPassiveConsumption() {
		return hasAValue(passiveConsumption);
	}	
	protected boolean hasAverageConsumption() {
		return hasAValue(averageConsumption);
	}	
	protected boolean hasSubUtilization() {
		return hasAValue(subUtilization);
	}	
	protected boolean hasMetric() {
		return hasAValue(metricTotal);
	}	
	protected boolean hasEnergyPerMetric() {
		return hasAValue(energyPerMetric);
	}	
	protected boolean hasDeliveredMetric() {
		return hasAValue(deliveredMetric);
	}	
	protected boolean hasEnergyPerDeliveredMetric() {
		return hasAValue(energyPerDeliveredMetric);
	}
	private boolean hasEnergy100() {
		return hasAValue(energyAt100);
	}	
}