package edu.columbia.sebastien.data_center_consumption;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.batik.ext.swing.GridBagConstants;
import org.apache.fop.svg.ACIUtils;

import com.jogamp.newt.event.MouseAdapter;

import cern.colt.Arrays;
import ch.epfl.general_libraries.gui.VerticalLabelUI;
import ch.epfl.general_libraries.utils.MoreArrays;

public class ConsumptionFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private NetworkPanel networkPanel;
	private CPUPanel cpuPanel;
	private RamPanel ramPanel;
	
	private boolean inUpdateMode = false;
	
	private JTextField fileName = new JTextField(10);
	private JButton save = new JButton("Save");
	private JButton load = new JButton("Load");
	
	private MyJTextField systemUtil;
	private MyJTextField constantPowerOverhead;
	private MyJTextField variablePowerOverhead;	
	
	private MyJTextField totalConsumption;
	private MyJTextField networkDesignAR;
	private MyJTextField ramAR;
	private MyJTextField networkEffectiveAR;
	private MyJTextField energyPerGopsServerWide;
	
	private MyJTextField[] allGlobalFields;
	
	public void saveToFile() {
		try {
			if (fileName.getText().length() < 1) throw new IllegalStateException();
			FileWriter fw = new FileWriter(new File(fileName.getText()));
			fw.write(saveToString());
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void loadFromFile() {
		try {
			if (fileName.getText().length() < 1) throw new IllegalStateException();
			FileReader fw = new FileReader(new File(fileName.getText()));
			BufferedReader br = new BufferedReader(fw);
			String line1 = br.readLine();
			String line2 = br.readLine();
			String line3 = br.readLine();
			String line4 = br.readLine();
			restore(line1, line2, line3, line4);
			br.close();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	public String saveToString() {
		StringBuilder sb = new StringBuilder();
		sb.append(systemUtil.getText()+",");
		sb.append(constantPowerOverhead.getText()+",");
		sb.append(variablePowerOverhead.getText()+",");
		sb.append(totalConsumption.getText()+",");
		sb.append(networkDesignAR.getText()+",");
		sb.append(ramAR.getText()+",");
		sb.append(networkEffectiveAR.getText()+",");
		sb.append(energyPerGopsServerWide.getText()+"\r\n");
		sb.append(networkPanel.saveToString()+"\r\n");
		sb.append(cpuPanel.saveToString()+"\r\n");
		sb.append(ramPanel.saveToString());	
		return sb.toString();
	}
	
	public void restore(String ... lines) {
		if (lines.length !=4) throw new IllegalStateException();
		String[] tab = lines[0].split(",");
		inUpdateMode = true;
		
		systemUtil.setText(tab[0]);
		constantPowerOverhead.setText(tab[1]);
		variablePowerOverhead.setText(tab[2]);
		totalConsumption.setText(tab[3]);
		networkDesignAR.setText(tab[4]);
		ramAR.setText(tab[5]);
		networkEffectiveAR.setText(tab[6]);
		energyPerGopsServerWide.setText(tab[7]);
		networkPanel.restore(lines[1]);
		cpuPanel.restore(lines[2]);
		ramPanel.restore(lines[3]);		
		inUpdateMode = false;
	}
	

	public static void main(String[] args) {
		ConsumptionFrame f = new ConsumptionFrame();
		f.init();
		f.setVisible(true);
	}
	
	public void init() {
		networkPanel.init(25, 19, 0.2, 10);
		cpuPanel.init(230, 23,  0.9, 200);
		ramPanel.init(50, 20, 0.5, 20);		
		calculate();
	}

	public ConsumptionFrame() {
		super.setSize(new Dimension(1240, 900));
		super.setTitle("Energy model");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		save.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent arg0) {
				saveToFile();
			}
		});
		
		load.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				loadFromFile();
			}
		});
		
		networkPanel = new NetworkPanel(this);
		cpuPanel = new CPUPanel(this);
		ramPanel = new RamPanel(this);

		build();
		
		allGlobalFields = new MyJTextField[]{systemUtil, totalConsumption, networkDesignAR, 
				ramAR, networkEffectiveAR, energyPerGopsServerWide, constantPowerOverhead, variablePowerOverhead
		};
		for (final MyJTextField f : allGlobalFields) {
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
					}					
				}
			});
		}
	}
	
	protected void updateSystem(MyJTextField f) {
		
		boolean previousMode = inUpdateMode;
		inUpdateMode = true;
		
		try {
		
			if (f == networkDesignAR) {
				networkDesignAR.setVirtuallyEnabled(false);
				if (networkPanel.metricTotal.isVirtuallyEnabled()) {
					cpuPanel.metricTotal.setVirtuallyEnabled(false);
					_AsetNetFromCPUdesignAR();
					networkPanel.updateSystem(networkPanel.metricTotal);
					cpuPanel.metricTotal.setVirtuallyEnabled(true);
				} else if (cpuPanel.metricTotal.isVirtuallyEnabled()) {
					networkPanel.metricTotal.setVirtuallyEnabled(false);
					_AsetCPUfromNetDesignAR();
					cpuPanel.updateSystem(cpuPanel.metricTotal);
					networkPanel.metricTotal.setVirtuallyEnabled(true);
				} else {
					checkA();
				}
				networkDesignAR.setVirtuallyEnabled(true);
			}
			if (f == ramAR) {
				ramAR.setVirtuallyEnabled(false);
				if (cpuPanel.metricTotal.isVirtuallyEnabled()) {
					ramPanel.metricTotal.setVirtuallyEnabled(false);
					_BsetCPUfromRamRAMAR();
					cpuPanel.updateSystem(cpuPanel.metricTotal);
					ramPanel.metricTotal.setVirtuallyEnabled(true);				
				} else if (ramPanel.metricTotal.isVirtuallyEnabled()) {
					cpuPanel.metricTotal.setVirtuallyEnabled(false);
					_BsetRAMfromCPURAMAR();
					ramPanel.updateSystem(ramPanel.metricTotal);
					cpuPanel.metricTotal.setVirtuallyEnabled(true);					
				} else {
					checkB();
				}
				ramAR.setVirtuallyEnabled(true);
			}
			if (f == networkEffectiveAR) {
				networkEffectiveAR.setVirtuallyEnabled(false);
				if (cpuPanel.deliveredMetric.isVirtuallyEnabled()) {
					networkPanel.deliveredMetric.setVirtuallyEnabled(false);
					_CsetDeliveredCPUfromDeliveredNetNetEffAR();
					cpuPanel.updateSystem(cpuPanel.deliveredMetric);
					networkPanel.deliveredMetric.setVirtuallyEnabled(true);
				} else if (networkPanel.deliveredMetric.isVirtuallyEnabled()) {
					cpuPanel.deliveredMetric.setVirtuallyEnabled(false);
					_CsetDeliveredNetfromDeliveredCPUNetEffAR();
					networkPanel.updateSystem(networkPanel.deliveredMetric);
					cpuPanel.deliveredMetric.setVirtuallyEnabled(true);				
				} else {
					checkC();
				}
				networkEffectiveAR.setVirtuallyEnabled(true);
			}
			if (f == energyPerGopsServerWide) {
				energyPerGopsServerWide.setVirtuallyEnabled(false);
				if (totalConsumption.isVirtuallyEnabled()) {
					cpuPanel.deliveredMetric.setVirtuallyEnabled(false);
					_DsetTotalconFromEnergyPerGopsDeliveredCPU();
					updateSystem(totalConsumption);
					cpuPanel.deliveredMetric.setVirtuallyEnabled(true);
				} else if (cpuPanel.deliveredMetric.isVirtuallyEnabled()) {
					boolean prev = totalConsumption.setVirtuallyEnabled(false);
					_DsetDeliveredCPUfromTotalconEnergyGops();
					cpuPanel.updateSystem(cpuPanel.deliveredMetric);
					totalConsumption.setVirtuallyEnabled(prev);
				} else {
					checkD();
				}
				energyPerGopsServerWide.setVirtuallyEnabled(true);
			}
			if (f == totalConsumption) {
				totalConsumption.setVirtuallyEnabled(false);
				if (energyPerGopsServerWide.isVirtuallyEnabled()) {
					boolean prev = cpuPanel.deliveredMetric.setVirtuallyEnabled(false);
					_DsetEnergyGopsFromTotalconDeliveredCPU();
					updateSystem(energyPerGopsServerWide);
					cpuPanel.deliveredMetric.setVirtuallyEnabled(prev);
				} else if (cpuPanel.deliveredMetric.isVirtuallyEnabled()) {
					boolean prev = energyPerGopsServerWide.setVirtuallyEnabled(false);
					_DsetDeliveredCPUfromTotalconEnergyGops();
					cpuPanel.updateSystem(cpuPanel.deliveredMetric);
					energyPerGopsServerWide.setVirtuallyEnabled(prev);
				} else {
					checkD();
				}
				
				calculate();
				if (constantPowerOverhead.isVirtuallyEnabled()) {
					boolean prev1 = cpuPanel.averageConsumption.setVirtuallyEnabled(false);
					boolean prev2 = networkPanel.averageConsumption.setVirtuallyEnabled(false);
					boolean prev3 = ramPanel.averageConsumption.setVirtuallyEnabled(false);
					boolean prev4 = variablePowerOverhead.setVirtuallyEnabled(false);
					_EsetConstantPowerFromTotRamNetCPU();
					updateSystem(constantPowerOverhead);
					cpuPanel.averageConsumption.setVirtuallyEnabled(prev1);
					networkPanel.averageConsumption.setVirtuallyEnabled(prev2);						
					ramPanel.averageConsumption.setVirtuallyEnabled(prev3);
					variablePowerOverhead.setVirtuallyEnabled(prev4);
				} else if (variablePowerOverhead.isVirtuallyEnabled()) {
					boolean prev1 = cpuPanel.averageConsumption.setVirtuallyEnabled(false);
					boolean prev2 = networkPanel.averageConsumption.setVirtuallyEnabled(false);
					boolean prev3 = ramPanel.averageConsumption.setVirtuallyEnabled(false);
					boolean prev4 = constantPowerOverhead.setVirtuallyEnabled(false);
					_EsetVariablePowerFromTotRamNetCPU();
					updateSystem(variablePowerOverhead);
					cpuPanel.averageConsumption.setVirtuallyEnabled(prev1);
					networkPanel.averageConsumption.setVirtuallyEnabled(prev2);						
					ramPanel.averageConsumption.setVirtuallyEnabled(prev3);
					constantPowerOverhead.setVirtuallyEnabled(prev4);
				} else if (cpuPanel.averageConsumption.isVirtuallyEnabled()) {
					boolean prev1 = networkPanel.averageConsumption.setVirtuallyEnabled(false);
					boolean prev2 = ramPanel.averageConsumption.setVirtuallyEnabled(false);
					boolean prev3 = constantPowerOverhead.setVirtuallyEnabled(false);
					boolean prev4 = variablePowerOverhead.setVirtuallyEnabled(false);
					_EsetCPUavFromTotRamNetOverheads();
					cpuPanel.updateSystem(cpuPanel.averageConsumption);
					networkPanel.averageConsumption.setVirtuallyEnabled(prev1);
					ramPanel.averageConsumption.setVirtuallyEnabled(prev2);
					constantPowerOverhead.setVirtuallyEnabled(prev3);
					variablePowerOverhead.setVirtuallyEnabled(prev4);
				} else if (networkPanel.averageConsumption.isVirtuallyEnabled()) {
					boolean prev1 = cpuPanel.averageConsumption.setVirtuallyEnabled(false);
					boolean prev2 = ramPanel.averageConsumption.setVirtuallyEnabled(false);
					boolean prev3 = constantPowerOverhead.setVirtuallyEnabled(false);
					boolean prev4 = variablePowerOverhead.setVirtuallyEnabled(false);
					_EsetNetavFromTotRamCpuOverheads();
					networkPanel.updateSystem(networkPanel.averageConsumption);
					cpuPanel.averageConsumption.setVirtuallyEnabled(prev1);
					ramPanel.averageConsumption.setVirtuallyEnabled(prev2);				
					constantPowerOverhead.setVirtuallyEnabled(prev3);
					variablePowerOverhead.setVirtuallyEnabled(prev4);
				} else if (ramPanel.averageConsumption.isVirtuallyEnabled()) {
					boolean prev1 = cpuPanel.averageConsumption.setVirtuallyEnabled(false);
					boolean prev2 = networkPanel.averageConsumption.setVirtuallyEnabled(false);
					boolean prev3 = constantPowerOverhead.setVirtuallyEnabled(false);
					boolean prev4 = variablePowerOverhead.setVirtuallyEnabled(false);
					_EsetRamavFromTotNetCpuOverheads();
					ramPanel.updateSystem(ramPanel.averageConsumption);
					cpuPanel.averageConsumption.setVirtuallyEnabled(prev1);
					networkPanel.averageConsumption.setVirtuallyEnabled(prev2);				
					constantPowerOverhead.setVirtuallyEnabled(prev3);
					variablePowerOverhead.setVirtuallyEnabled(prev4);
				} else {
					checkE();
				}
				totalConsumption.setVirtuallyEnabled(true);
				updateSliders();
			}
			
			if (f == networkPanel.metricTotal) {
				boolean p = networkPanel.metricTotal.setVirtuallyEnabled(false);
				if (networkDesignAR.isVirtuallyEnabled()) {
					boolean prev = cpuPanel.metricTotal.setVirtuallyEnabled(false);
					_AsetDesignARfromNetCPU();
					updateSystem(networkDesignAR);
					cpuPanel.metricTotal.setVirtuallyEnabled(prev);
				} else if (cpuPanel.metricTotal.isVirtuallyEnabled()) {
					boolean prev = networkPanel.metricTotal.setVirtuallyEnabled(false);
					_AsetCPUfromNetDesignAR();
					cpuPanel.updateSystem(cpuPanel.metricTotal);
					networkPanel.metricTotal.setVirtuallyEnabled(prev);				
				} else {
					checkA();
				}
				networkPanel.metricTotal.setVirtuallyEnabled(p);
			}
			
			if (f == cpuPanel.metricTotal) {
				boolean p = cpuPanel.metricTotal.setVirtuallyEnabled(false);
				if (networkDesignAR.isVirtuallyEnabled()) {
					boolean prev = networkPanel.metricTotal.setVirtuallyEnabled(false);
					_AsetDesignARfromNetCPU();
					updateSystem(networkDesignAR);
					networkPanel.metricTotal.setVirtuallyEnabled(prev);
				} else if (networkPanel.metricTotal.isVirtuallyEnabled()) {
					boolean prev = cpuPanel.metricTotal.setVirtuallyEnabled(false);
					_AsetNetFromCPUdesignAR();
					networkPanel.updateSystem(networkPanel.metricTotal);
					cpuPanel.metricTotal.setVirtuallyEnabled(prev);				
				} else {
					checkA();
				}
				if (ramAR.isVirtuallyEnabled()) {
					boolean prev = ramPanel.metricTotal.setVirtuallyEnabled(false);
					_BsetRAMARfromCPURam();
					updateSystem(ramAR);
					ramPanel.metricTotal.setVirtuallyEnabled(prev);
				} else if (ramPanel.metricTotal.isVirtuallyEnabled()) {
					boolean prev = cpuPanel.metricTotal.setVirtuallyEnabled(false);
					_BsetRAMfromCPURAMAR();
					ramPanel.updateSystem(ramPanel.metricTotal);
					cpuPanel.metricTotal.setVirtuallyEnabled(prev);				
				} else {
					checkB();
				}
				cpuPanel.metricTotal.setVirtuallyEnabled(p);
			}
			
			if (f == ramPanel.metricTotal) {
				boolean p = ramPanel.metricTotal.setVirtuallyEnabled(false);
				if (ramAR.isVirtuallyEnabled()) {
					boolean prev = cpuPanel.metricTotal.setVirtuallyEnabled(false);
					_BsetRAMARfromCPURam();
					updateSystem(ramAR);
					cpuPanel.metricTotal.setVirtuallyEnabled(prev);
				} else if (cpuPanel.metricTotal.isVirtuallyEnabled()) {
					boolean prev = ramPanel.metricTotal.setVirtuallyEnabled(false);
					_BsetCPUfromRamRAMAR();
					cpuPanel.updateSystem(cpuPanel.metricTotal);
					ramPanel.metricTotal.setVirtuallyEnabled(prev);				
				} else {
					checkB();
				}
				ramPanel.metricTotal.setVirtuallyEnabled(p);
			}
			
			if (f == networkPanel.deliveredMetric) {
				boolean p = networkPanel.deliveredMetric.setVirtuallyEnabled(false);
				if (networkEffectiveAR.isVirtuallyEnabled()) {
					boolean prev = cpuPanel.deliveredMetric.setVirtuallyEnabled(false);
					_CsetNetEffARfromDeliveredCPUdeliveredNet();
					updateSystem(networkEffectiveAR);
					cpuPanel.deliveredMetric.setVirtuallyEnabled(prev);				
				} else if (cpuPanel.deliveredMetric.isVirtuallyEnabled()) {
					boolean prev = networkEffectiveAR.setVirtuallyEnabled(false);
					_CsetDeliveredCPUfromDeliveredNetNetEffAR();
					cpuPanel.updateSystem(cpuPanel.deliveredMetric);
					networkEffectiveAR.setVirtuallyEnabled(prev);
				} else {
					checkC();
				}		
				networkPanel.deliveredMetric.setVirtuallyEnabled(p);
			}
			
			if (f == cpuPanel.deliveredMetric) {
				boolean p = cpuPanel.deliveredMetric.setVirtuallyEnabled(false);
				if (networkEffectiveAR.isVirtuallyEnabled()) {
					boolean prev = networkPanel.deliveredMetric.setVirtuallyEnabled(false);
					_CsetNetEffARfromDeliveredCPUdeliveredNet();
					updateSystem(networkEffectiveAR);
					networkPanel.deliveredMetric.setVirtuallyEnabled(prev);				
				} else if (networkPanel.deliveredMetric.isVirtuallyEnabled()) {
					boolean prev = networkEffectiveAR.setVirtuallyEnabled(false);
					_CsetDeliveredNetfromDeliveredCPUNetEffAR();
					networkPanel.updateSystem(networkPanel.deliveredMetric);
					networkEffectiveAR.setVirtuallyEnabled(prev);
				} else {
					checkC();
				}	
				
				if (energyPerGopsServerWide.isVirtuallyEnabled()) {
					boolean prev = totalConsumption.setVirtuallyEnabled(false);
					_DsetEnergyGopsFromTotalconDeliveredCPU();
					updateSystem(energyPerGopsServerWide);
					totalConsumption.setVirtuallyEnabled(prev);
				} else if (totalConsumption.isVirtuallyEnabled()) {
					boolean prev = energyPerGopsServerWide.setVirtuallyEnabled(false);
					_DsetTotalconFromEnergyPerGopsDeliveredCPU();
					updateSystem(totalConsumption);
					energyPerGopsServerWide.setVirtuallyEnabled(prev);
				} else {
					checkD();
				}
				cpuPanel.deliveredMetric.setVirtuallyEnabled(p);
			}
			
			if (f == cpuPanel.averageConsumption) {
				boolean b = cpuPanel.averageConsumption.setVirtuallyEnabled(false);
				calculate();
				if (totalConsumption.isVirtuallyEnabled()) {
					boolean prev1 = networkPanel.averageConsumption.setVirtuallyEnabled(false);
					boolean prev2 = ramPanel.averageConsumption.setVirtuallyEnabled(false);
					boolean prev3 = constantPowerOverhead.setVirtuallyEnabled(false);
					boolean prev4 = variablePowerOverhead.setVirtuallyEnabled(false);
					_EsetTotconFromCPUnetRAM();
					updateSystem(totalConsumption);
					networkPanel.averageConsumption.setVirtuallyEnabled(prev1);
					ramPanel.averageConsumption.setVirtuallyEnabled(prev2);
					constantPowerOverhead.setVirtuallyEnabled(prev3);
					variablePowerOverhead.setVirtuallyEnabled(prev4);
				} else if (constantPowerOverhead.isVirtuallyEnabled()) {
					boolean prev1 = totalConsumption.setVirtuallyEnabled(false);
					boolean prev2 = networkPanel.averageConsumption.setVirtuallyEnabled(false);
					boolean prev3 = ramPanel.averageConsumption.setVirtuallyEnabled(false);
					boolean prev4 = variablePowerOverhead.setVirtuallyEnabled(false);
					_EsetConstantPowerFromTotRamNetCPU();
					updateSystem(constantPowerOverhead);
					totalConsumption.setVirtuallyEnabled(prev1);
					networkPanel.averageConsumption.setVirtuallyEnabled(prev2);						
					ramPanel.averageConsumption.setVirtuallyEnabled(prev3);
					variablePowerOverhead.setVirtuallyEnabled(prev4);
				} else if (variablePowerOverhead.isVirtuallyEnabled()) {
					boolean prev1 = totalConsumption.setVirtuallyEnabled(false);
					boolean prev2 = networkPanel.averageConsumption.setVirtuallyEnabled(false);
					boolean prev3 = ramPanel.averageConsumption.setVirtuallyEnabled(false);
					boolean prev4 = constantPowerOverhead.setVirtuallyEnabled(false);
					_EsetVariablePowerFromTotRamNetCPU();
					updateSystem(variablePowerOverhead);
					totalConsumption.setVirtuallyEnabled(prev1);
					networkPanel.averageConsumption.setVirtuallyEnabled(prev2);						
					ramPanel.averageConsumption.setVirtuallyEnabled(prev3);
					constantPowerOverhead.setVirtuallyEnabled(prev4);
				} else if (networkPanel.averageConsumption.isVirtuallyEnabled()) {
					boolean prev1 = totalConsumption.setVirtuallyEnabled(false);
					boolean prev2 = ramPanel.averageConsumption.setVirtuallyEnabled(false);
					boolean prev3 = constantPowerOverhead.setVirtuallyEnabled(false);
					boolean prev4 = variablePowerOverhead.setVirtuallyEnabled(false);
					_EsetNetavFromTotRamCpuOverheads();
					networkPanel.updateSystem(networkPanel.averageConsumption);
					totalConsumption.setVirtuallyEnabled(prev1);
					ramPanel.averageConsumption.setVirtuallyEnabled(prev2);				
					constantPowerOverhead.setVirtuallyEnabled(prev3);
					variablePowerOverhead.setVirtuallyEnabled(prev4);
				} else if (ramPanel.averageConsumption.isVirtuallyEnabled()) {
					boolean prev1 = totalConsumption.setVirtuallyEnabled(false);
					boolean prev2 = networkPanel.averageConsumption.setVirtuallyEnabled(false);
					boolean prev3 = constantPowerOverhead.setVirtuallyEnabled(false);
					boolean prev4 = variablePowerOverhead.setVirtuallyEnabled(false);
					_EsetRamavFromTotNetCpuOverheads();
					ramPanel.updateSystem(ramPanel.averageConsumption);
					totalConsumption.setVirtuallyEnabled(prev1);
					networkPanel.averageConsumption.setVirtuallyEnabled(prev2);					
					constantPowerOverhead.setVirtuallyEnabled(prev3);
					variablePowerOverhead.setVirtuallyEnabled(prev4);
				} else {
					checkE();
				}
				updateSliders();
				cpuPanel.averageConsumption.setVirtuallyEnabled(b);
			}
			
			if (f == networkPanel.averageConsumption) {
				boolean b = networkPanel.averageConsumption.setVirtuallyEnabled(false);
				calculate();
				if (totalConsumption.isVirtuallyEnabled()) {
					boolean prev1 = cpuPanel.averageConsumption.setVirtuallyEnabled(false);
					boolean prev2 = ramPanel.averageConsumption.setVirtuallyEnabled(false);
					boolean prev3 = constantPowerOverhead.setVirtuallyEnabled(false);
					boolean prev4 = variablePowerOverhead.setVirtuallyEnabled(false);
					_EsetTotconFromCPUnetRAM();
					updateSystem(totalConsumption);
					cpuPanel.averageConsumption.setVirtuallyEnabled(prev1);
					ramPanel.averageConsumption.setVirtuallyEnabled(prev2);
					constantPowerOverhead.setVirtuallyEnabled(prev3);
					variablePowerOverhead.setVirtuallyEnabled(prev4);
				} else if (constantPowerOverhead.isVirtuallyEnabled()) {
					boolean prev1 = totalConsumption.setVirtuallyEnabled(false);
					boolean prev2 = cpuPanel.averageConsumption.setVirtuallyEnabled(false);
					boolean prev3 = ramPanel.averageConsumption.setVirtuallyEnabled(false);
					boolean prev4 = variablePowerOverhead.setVirtuallyEnabled(false);
					_EsetConstantPowerFromTotRamNetCPU();
					updateSystem(constantPowerOverhead);
					totalConsumption.setVirtuallyEnabled(prev1);
					cpuPanel.averageConsumption.setVirtuallyEnabled(prev2);						
					ramPanel.averageConsumption.setVirtuallyEnabled(prev3);
					variablePowerOverhead.setVirtuallyEnabled(prev4);
				} else if (variablePowerOverhead.isVirtuallyEnabled()) {
					boolean prev1 = totalConsumption.setVirtuallyEnabled(false);
					boolean prev2 = cpuPanel.averageConsumption.setVirtuallyEnabled(false);
					boolean prev3 = ramPanel.averageConsumption.setVirtuallyEnabled(false);
					boolean prev4 = constantPowerOverhead.setVirtuallyEnabled(false);
					_EsetVariablePowerFromTotRamNetCPU();
					updateSystem(variablePowerOverhead);
					totalConsumption.setVirtuallyEnabled(prev1);
					cpuPanel.averageConsumption.setVirtuallyEnabled(prev2);						
					ramPanel.averageConsumption.setVirtuallyEnabled(prev3);
					constantPowerOverhead.setVirtuallyEnabled(prev4);
				} else if (cpuPanel.averageConsumption.isVirtuallyEnabled()) {
					boolean prev1 = totalConsumption.setVirtuallyEnabled(false);
					boolean prev2 = ramPanel.averageConsumption.setVirtuallyEnabled(false);
					boolean prev3 = constantPowerOverhead.setVirtuallyEnabled(false);
					boolean prev4 = variablePowerOverhead.setVirtuallyEnabled(false);
					_EsetCPUavFromTotRamNetOverheads();
					cpuPanel.updateSystem(cpuPanel.averageConsumption);
					totalConsumption.setVirtuallyEnabled(prev1);
					ramPanel.averageConsumption.setVirtuallyEnabled(prev2);				
					constantPowerOverhead.setVirtuallyEnabled(prev3);
					variablePowerOverhead.setVirtuallyEnabled(prev4);
				} else if (ramPanel.averageConsumption.isVirtuallyEnabled()) {
					boolean prev1 = totalConsumption.setVirtuallyEnabled(false);
					boolean prev2 = cpuPanel.averageConsumption.setVirtuallyEnabled(false);
					boolean prev3 = constantPowerOverhead.setVirtuallyEnabled(false);
					boolean prev4 = variablePowerOverhead.setVirtuallyEnabled(false);
					_EsetRamavFromTotNetCpuOverheads();
					ramPanel.updateSystem(ramPanel.averageConsumption);
					totalConsumption.setVirtuallyEnabled(prev1);
					cpuPanel.averageConsumption.setVirtuallyEnabled(prev2);					
					constantPowerOverhead.setVirtuallyEnabled(prev3);
					variablePowerOverhead.setVirtuallyEnabled(prev4);
				} else {
					checkE();
				}
				updateSliders();
				networkPanel.averageConsumption.setVirtuallyEnabled(b);
			}
			
			if (f == ramPanel.averageConsumption) {
				boolean b = ramPanel.averageConsumption.setVirtuallyEnabled(false);
				calculate();
				if (totalConsumption.isVirtuallyEnabled()) {
					boolean prev1 = cpuPanel.averageConsumption.setVirtuallyEnabled(false);
					boolean prev2 = networkPanel.averageConsumption.setVirtuallyEnabled(false);
					boolean prev3 = constantPowerOverhead.setVirtuallyEnabled(false);
					boolean prev4 = variablePowerOverhead.setVirtuallyEnabled(false);
					_EsetTotconFromCPUnetRAM();
					updateSystem(totalConsumption);
					cpuPanel.averageConsumption.setVirtuallyEnabled(prev1);
					networkPanel.averageConsumption.setVirtuallyEnabled(prev2);
					constantPowerOverhead.setVirtuallyEnabled(prev3);
					variablePowerOverhead.setVirtuallyEnabled(prev4);
				} else if (constantPowerOverhead.isVirtuallyEnabled()) {
					boolean prev1 = totalConsumption.setVirtuallyEnabled(false);
					boolean prev2 = cpuPanel.averageConsumption.setVirtuallyEnabled(false);
					boolean prev3 = networkPanel.averageConsumption.setVirtuallyEnabled(false);
					boolean prev4 = variablePowerOverhead.setVirtuallyEnabled(false);
					_EsetConstantPowerFromTotRamNetCPU();
					updateSystem(constantPowerOverhead);
					totalConsumption.setVirtuallyEnabled(prev1);
					cpuPanel.averageConsumption.setVirtuallyEnabled(prev2);						
					networkPanel.averageConsumption.setVirtuallyEnabled(prev3);
					variablePowerOverhead.setVirtuallyEnabled(prev4);
				} else if (variablePowerOverhead.isVirtuallyEnabled()) {
					boolean prev1 = totalConsumption.setVirtuallyEnabled(false);
					boolean prev2 = cpuPanel.averageConsumption.setVirtuallyEnabled(false);
					boolean prev3 = networkPanel.averageConsumption.setVirtuallyEnabled(false);
					boolean prev4 = constantPowerOverhead.setVirtuallyEnabled(false);
					_EsetVariablePowerFromTotRamNetCPU();
					updateSystem(variablePowerOverhead);
					totalConsumption.setVirtuallyEnabled(prev1);
					cpuPanel.averageConsumption.setVirtuallyEnabled(prev2);						
					networkPanel.averageConsumption.setVirtuallyEnabled(prev3);
					constantPowerOverhead.setVirtuallyEnabled(prev4);
				} else if (cpuPanel.averageConsumption.isVirtuallyEnabled()) {
					boolean prev1 = totalConsumption.setVirtuallyEnabled(false);
					boolean prev2 = networkPanel.averageConsumption.setVirtuallyEnabled(false);
					boolean prev3 = constantPowerOverhead.setVirtuallyEnabled(false);
					boolean prev4 = variablePowerOverhead.setVirtuallyEnabled(false);
					_EsetCPUavFromTotRamNetOverheads();
					cpuPanel.updateSystem(cpuPanel.averageConsumption);
					totalConsumption.setVirtuallyEnabled(prev1);
					networkPanel.averageConsumption.setVirtuallyEnabled(prev2);				
					constantPowerOverhead.setVirtuallyEnabled(prev3);
					variablePowerOverhead.setVirtuallyEnabled(prev4);
				} else if (networkPanel.averageConsumption.isVirtuallyEnabled()) {
					boolean prev1 = totalConsumption.setVirtuallyEnabled(false);
					boolean prev2 = cpuPanel.averageConsumption.setVirtuallyEnabled(false);
					boolean prev3 = constantPowerOverhead.setVirtuallyEnabled(false);
					boolean prev4 = variablePowerOverhead.setVirtuallyEnabled(false);
					_EsetNetavFromTotRamCpuOverheads();
					networkPanel.updateSystem(networkPanel.averageConsumption);
					totalConsumption.setVirtuallyEnabled(prev1);
					cpuPanel.averageConsumption.setVirtuallyEnabled(prev2);		
					constantPowerOverhead.setVirtuallyEnabled(prev3);
					variablePowerOverhead.setVirtuallyEnabled(prev4);
				}  else {
					checkE();
				}
				updateSliders();
				ramPanel.averageConsumption.setVirtuallyEnabled(b);
			}	
			
			if (f == constantPowerOverhead) {
				boolean b = constantPowerOverhead.setVirtuallyEnabled(false);
				calculate();
				if (totalConsumption.isVirtuallyEnabled()) {
					boolean prev1 = cpuPanel.averageConsumption.setVirtuallyEnabled(false);
					boolean prev2 = networkPanel.averageConsumption.setVirtuallyEnabled(false);
					boolean prev3 = ramPanel.averageConsumption.setVirtuallyEnabled(false);
					boolean prev4 = variablePowerOverhead.setVirtuallyEnabled(false);
					_EsetTotconFromCPUnetRAM();
					updateSystem(totalConsumption);
					cpuPanel.averageConsumption.setVirtuallyEnabled(prev1);
					networkPanel.averageConsumption.setVirtuallyEnabled(prev2);						
					ramPanel.averageConsumption.setVirtuallyEnabled(prev3);
					variablePowerOverhead.setVirtuallyEnabled(prev4);
				} else if (variablePowerOverhead.isVirtuallyEnabled()) {
					boolean prev1 = cpuPanel.averageConsumption.setVirtuallyEnabled(false);
					boolean prev2 = networkPanel.averageConsumption.setVirtuallyEnabled(false);
					boolean prev3 = ramPanel.averageConsumption.setVirtuallyEnabled(false);
					boolean prev4 = totalConsumption.setVirtuallyEnabled(false);
					_EsetVariablePowerFromTotRamNetCPU();
					updateSystem(variablePowerOverhead);
					cpuPanel.averageConsumption.setVirtuallyEnabled(prev1);
					networkPanel.averageConsumption.setVirtuallyEnabled(prev2);						
					ramPanel.averageConsumption.setVirtuallyEnabled(prev3);
					totalConsumption.setVirtuallyEnabled(prev4);
				}else if (cpuPanel.averageConsumption.isVirtuallyEnabled()) {
					boolean prev1 = networkPanel.averageConsumption.setVirtuallyEnabled(false);
					boolean prev2 = ramPanel.averageConsumption.setVirtuallyEnabled(false);
					boolean prev3 = totalConsumption.setVirtuallyEnabled(false);
					boolean prev4 = variablePowerOverhead.setVirtuallyEnabled(false);
					_EsetCPUavFromTotRamNetOverheads();
					cpuPanel.updateSystem(cpuPanel.averageConsumption);
					networkPanel.averageConsumption.setVirtuallyEnabled(prev1);
					ramPanel.averageConsumption.setVirtuallyEnabled(prev2);
					totalConsumption.setVirtuallyEnabled(prev3);
					variablePowerOverhead.setVirtuallyEnabled(prev4);
				} else if (networkPanel.averageConsumption.isVirtuallyEnabled()) {
					boolean prev1 = cpuPanel.averageConsumption.setVirtuallyEnabled(false);
					boolean prev2 = ramPanel.averageConsumption.setVirtuallyEnabled(false);
					boolean prev3 = totalConsumption.setVirtuallyEnabled(false);
					boolean prev4 = variablePowerOverhead.setVirtuallyEnabled(false);
					_EsetNetavFromTotRamCpuOverheads();
					networkPanel.updateSystem(networkPanel.averageConsumption);
					cpuPanel.averageConsumption.setVirtuallyEnabled(prev1);
					ramPanel.averageConsumption.setVirtuallyEnabled(prev2);				
					totalConsumption.setVirtuallyEnabled(prev3);
					variablePowerOverhead.setVirtuallyEnabled(prev4);
				} else if (ramPanel.averageConsumption.isVirtuallyEnabled()) {
					boolean prev1 = cpuPanel.averageConsumption.setVirtuallyEnabled(false);
					boolean prev2 = networkPanel.averageConsumption.setVirtuallyEnabled(false);
					boolean prev3 = totalConsumption.setVirtuallyEnabled(false);
					boolean prev4 = variablePowerOverhead.setVirtuallyEnabled(false);
					_EsetRamavFromTotNetCpuOverheads();
					ramPanel.updateSystem(ramPanel.averageConsumption);
					cpuPanel.averageConsumption.setVirtuallyEnabled(prev1);
					networkPanel.averageConsumption.setVirtuallyEnabled(prev2);				
					totalConsumption.setVirtuallyEnabled(prev3);
					variablePowerOverhead.setVirtuallyEnabled(prev4);
				} else {
					checkE();
				}				
				constantPowerOverhead.setVirtuallyEnabled(b);
			}
			
			if (f == variablePowerOverhead) {
				boolean b = variablePowerOverhead.setVirtuallyEnabled(false);
				calculate();
				if (totalConsumption.isVirtuallyEnabled()) {
					boolean prev1 = cpuPanel.averageConsumption.setVirtuallyEnabled(false);
					boolean prev2 = networkPanel.averageConsumption.setVirtuallyEnabled(false);
					boolean prev3 = ramPanel.averageConsumption.setVirtuallyEnabled(false);
					boolean prev4 = constantPowerOverhead.setVirtuallyEnabled(false);
					_EsetTotconFromCPUnetRAM();
					updateSystem(totalConsumption);
					cpuPanel.averageConsumption.setVirtuallyEnabled(prev1);
					networkPanel.averageConsumption.setVirtuallyEnabled(prev2);						
					ramPanel.averageConsumption.setVirtuallyEnabled(prev3);
					constantPowerOverhead.setVirtuallyEnabled(prev4);
				} else if (constantPowerOverhead.isVirtuallyEnabled()) {
					boolean prev1 = cpuPanel.averageConsumption.setVirtuallyEnabled(false);
					boolean prev2 = networkPanel.averageConsumption.setVirtuallyEnabled(false);
					boolean prev3 = ramPanel.averageConsumption.setVirtuallyEnabled(false);
					boolean prev4 = totalConsumption.setVirtuallyEnabled(false);
					_EsetConstantPowerFromTotRamNetCPU();
					updateSystem(constantPowerOverhead);
					cpuPanel.averageConsumption.setVirtuallyEnabled(prev1);
					networkPanel.averageConsumption.setVirtuallyEnabled(prev2);						
					ramPanel.averageConsumption.setVirtuallyEnabled(prev3);
					totalConsumption.setVirtuallyEnabled(prev4);
				} else if (cpuPanel.averageConsumption.isVirtuallyEnabled()) {
					boolean prev1 = networkPanel.averageConsumption.setVirtuallyEnabled(false);
					boolean prev2 = ramPanel.averageConsumption.setVirtuallyEnabled(false);
					boolean prev3 = totalConsumption.setVirtuallyEnabled(false);
					boolean prev4 = constantPowerOverhead.setVirtuallyEnabled(false);
					_EsetCPUavFromTotRamNetOverheads();
					cpuPanel.updateSystem(cpuPanel.averageConsumption);
					networkPanel.averageConsumption.setVirtuallyEnabled(prev1);
					ramPanel.averageConsumption.setVirtuallyEnabled(prev2);
					totalConsumption.setVirtuallyEnabled(prev3);
					constantPowerOverhead.setVirtuallyEnabled(prev4);
				} else if (networkPanel.averageConsumption.isVirtuallyEnabled()) {
					boolean prev1 = cpuPanel.averageConsumption.setVirtuallyEnabled(false);
					boolean prev2 = ramPanel.averageConsumption.setVirtuallyEnabled(false);
					boolean prev3 = totalConsumption.setVirtuallyEnabled(false);
					boolean prev4 = constantPowerOverhead.setVirtuallyEnabled(false);
					_EsetNetavFromTotRamCpuOverheads();
					networkPanel.updateSystem(networkPanel.averageConsumption);
					cpuPanel.averageConsumption.setVirtuallyEnabled(prev1);
					ramPanel.averageConsumption.setVirtuallyEnabled(prev2);				
					totalConsumption.setVirtuallyEnabled(prev3);
					constantPowerOverhead.setVirtuallyEnabled(prev4);
				} else if (ramPanel.averageConsumption.isVirtuallyEnabled()) {
					boolean prev1 = cpuPanel.averageConsumption.setVirtuallyEnabled(false);
					boolean prev2 = networkPanel.averageConsumption.setVirtuallyEnabled(false);
					boolean prev3 = totalConsumption.setVirtuallyEnabled(false);
					boolean prev4 = constantPowerOverhead.setVirtuallyEnabled(false);
					_EsetRamavFromTotNetCpuOverheads();
					ramPanel.updateSystem(ramPanel.averageConsumption);
					cpuPanel.averageConsumption.setVirtuallyEnabled(prev1);
					networkPanel.averageConsumption.setVirtuallyEnabled(prev2);				
					totalConsumption.setVirtuallyEnabled(prev3);
					constantPowerOverhead.setVirtuallyEnabled(prev4);
				} else {
					checkE();
				}				
				variablePowerOverhead.setVirtuallyEnabled(b);
			}			
		}
		finally {
			inUpdateMode = previousMode;
		}
		
		// missing for each of the individual powers
	}
	

	private void updateSliders() {
		cpuPanel.updateSlider();
		ramPanel.updateSlider();
		networkPanel.updateSlider();
	}







	private MyJTextField _EsetTotconFromCPUnetRAM() {
		if (cpuPanel.hasAverageConsumption() && 
			networkPanel.hasAverageConsumption() && 
			ramPanel.hasAverageConsumption() &&
			hasConstantPowerOverhead() &&
			hasVariablePowerOverhead()) {
			
			double totTemp = cpuPanel.getAverageConsumption() + networkPanel.getAverageConsumption() +
					ramPanel.getAverageConsumption()+ getConstantPowerOverhead();
			totTemp = (totTemp)*(1+getVariablePowerOverhead()) ;
			
			return setTotalConsumption(totTemp);
		}
		return null;
	}

	private void _EsetRamavFromTotNetCpuOverheads() {
		if (hasTotalConsumption() && 
			cpuPanel.hasAverageConsumption() && 
			networkPanel.hasAverageConsumption() &&
			hasConstantPowerOverhead() &&
			hasVariablePowerOverhead()) {
			
			double totA = getTotalConsumption()/(1+getVariablePowerOverhead());
			double res = totA - networkPanel.getAverageConsumption() 
					- cpuPanel.getAverageConsumption() - getConstantPowerOverhead();
			
			ramPanel.setAverageConsumption(res);
		}
	}

	private void _EsetNetavFromTotRamCpuOverheads() {
		if (hasTotalConsumption() && 
			cpuPanel.hasAverageConsumption() && 
			ramPanel.hasAverageConsumption() &&
			hasConstantPowerOverhead() &&
			hasVariablePowerOverhead()) {
			
			double totA = getTotalConsumption()/(1+getVariablePowerOverhead());
			double res = totA - cpuPanel.getAverageConsumption() 
					- ramPanel.getAverageConsumption() - getConstantPowerOverhead();
			
			networkPanel.setAverageConsumption(res);
		}
	}

	private void _EsetCPUavFromTotRamNetOverheads() {
		if (hasTotalConsumption() && 
			networkPanel.hasAverageConsumption() && 
			ramPanel.hasAverageConsumption() &&
			hasConstantPowerOverhead() &&
			hasVariablePowerOverhead()) {

			double totA = getTotalConsumption()/(1+getVariablePowerOverhead());
			double res = totA - networkPanel.getAverageConsumption() 
					- ramPanel.getAverageConsumption() - getConstantPowerOverhead();
				
			cpuPanel.setAverageConsumption(res);
		}
	}
	
	private void _EsetConstantPowerFromTotRamNetCPU () {
		if (hasTotalConsumption() && 
				networkPanel.hasAverageConsumption() && 
				ramPanel.hasAverageConsumption() &&
				cpuPanel.hasAverageConsumption() && 
				hasVariablePowerOverhead()) {
			
			double totTemp = cpuPanel.getAverageConsumption() + networkPanel.getAverageConsumption() + ramPanel.getAverageConsumption();
			double temp = getTotalConsumption()/(1+getVariablePowerOverhead());
			double res = temp - totTemp;
			setConstantPowerOverhead(res);
		}
	}
	
	private void _EsetVariablePowerFromTotRamNetCPU() {
		if (hasTotalConsumption() && 
				networkPanel.hasAverageConsumption() && 
				ramPanel.hasAverageConsumption() &&
				cpuPanel.hasAverageConsumption() && 
				hasConstantPowerOverhead()) {
			
			double up = getTotalConsumption();
			double down = cpuPanel.getAverageConsumption() + networkPanel.getAverageConsumption() + 
					ramPanel.getAverageConsumption()+ getConstantPowerOverhead();
			setVariablePowerOverhead((up/down)-1);
		}
	}

	private void _DsetEnergyGopsFromTotalconDeliveredCPU() {
		if (hasTotalConsumption() && cpuPanel.hasDeliveredMetric()) {
			setEnergyPerGopsServerWide(getTotalConsumption()/cpuPanel.getDeliveredMetric());
		}
	}

	private void _DsetDeliveredCPUfromTotalconEnergyGops() {
		if (hasTotalConsumption() && hasEnergyPerGopsServerWide()) {
			cpuPanel.setDeliveredMetric(getTotalConsumption()/getEnergyPerGopsServerWide());
		}
	}

	private MyJTextField _DsetTotalconFromEnergyPerGopsDeliveredCPU() {
		if (cpuPanel.hasDeliveredMetric() && hasEnergyPerGopsServerWide()) {
			return setTotalConsumption(cpuPanel.getDeliveredMetric()*getEnergyPerGopsServerWide());
		}
		return null;
	}

	private void _CsetNetEffARfromDeliveredCPUdeliveredNet() {
		if (cpuPanel.hasDeliveredMetric() && networkPanel.hasDeliveredMetric()) {
			setNetworkEffectiveAR(networkPanel.getDeliveredMetric()/cpuPanel.getDeliveredMetric());
		}
	}
	
	private void _CsetDeliveredNetfromDeliveredCPUNetEffAR() {
		if (cpuPanel.hasDeliveredMetric() && hasNetworkEffectiveAR()) {
			networkPanel.setDeliveredMetric(cpuPanel.getDeliveredMetric()*gettNetworkEffectiveAR());
		}
	}

	private void _CsetDeliveredCPUfromDeliveredNetNetEffAR() {
		if (networkPanel.hasDeliveredMetric() && hasNetworkEffectiveAR()) {
			cpuPanel.setDeliveredMetric(networkPanel.getDeliveredMetric()/gettNetworkEffectiveAR());
		}
	}

	private void _AsetNetFromCPUdesignAR() {
		if (cpuPanel.hasMetric() && hasNetworkDesignAR()) {
			networkPanel.setMetricTotal(cpuPanel.getMetric()*getNetworkDesignAR());
		}
	}
	
	private void _AsetCPUfromNetDesignAR() {
		if (networkPanel.hasMetric() && hasNetworkDesignAR()) {
			cpuPanel.setMetricTotal(networkPanel.getMetric()/getNetworkDesignAR());		
		}
	}
	
	private void _AsetDesignARfromNetCPU() {
		if (networkPanel.hasMetric() && cpuPanel.hasMetric()) {
			setNetworkDesignAR(networkPanel.getMetric()/cpuPanel.getMetric());
		}
	}
	
	private void _BsetCPUfromRamRAMAR() {
		if (ramPanel.hasMetric() && hasRAMar()) {
			cpuPanel.setMetricTotal(ramPanel.getMetric()/getRAMar());		
		}
	}
	
	private void _BsetRAMfromCPURAMAR() {
		if (cpuPanel.hasMetric() && hasRAMar()) {
			ramPanel.setMetricTotal(cpuPanel.getMetric()*getRAMar());
		}
	}

	private void _BsetRAMARfromCPURam() {
		if (cpuPanel.hasMetric() && ramPanel.hasMetric()) {
			setRAMar(ramPanel.getMetric()/cpuPanel.getMetric());
		}
	}	

	private void checkA() {	
		if (!hasNetworkDesignAR()) return;
		if (!cpuPanel.hasMetric()) return;
		if (!networkPanel.hasMetric()) return;
		double netDesign = getNetworkDesignAR();
		if (compare(networkPanel.getMetric()/cpuPanel.getMetric(), netDesign )) {
			throw new IllegalStateException("A not fulfilled");
		}
		if (netDesign < 0.5) {
			networkDesignAR.setBackground(Color.WHITE);
		} 		
		if (netDesign <= 0.05) {
			networkDesignAR.setBackground(Color.YELLOW);
		}		
		if (netDesign <= 0.005) {
			networkDesignAR.setBackground(Color.RED);
		}
		if (netDesign >= 0.5) {
			networkDesignAR.setBackground(Color.GREEN);
		}
	}
	private void checkB() {	
		if (!hasRAMar()) return;
		if (!cpuPanel.hasMetric()) return;
		if (!ramPanel.hasMetric()) return;
		double ramDesign = getRAMar();
		if (compare(ramPanel.getMetric()/cpuPanel.getMetric(), ramDesign )) {
			throw new IllegalStateException("B not fulfilled");
		}
		if (ramDesign < 0.1) {
			ramAR.setBackground(Color.RED);
		}
		if (ramDesign < 0.5) {
			ramAR.setBackground(Color.YELLOW);
		}
		if (ramDesign < 2) {
			ramAR.setBackground(Color.WHITE);
		} 
		if (ramDesign >= 8) {
			ramAR.setBackground(Color.GREEN);
		}		
	}
	private void checkC() {	
		if (!hasNetworkEffectiveAR()) return;
		if (!networkPanel.hasDeliveredMetric()) return;
		if (!cpuPanel.hasDeliveredMetric()) return;
		double netEff = gettNetworkEffectiveAR();
		if (compare(networkPanel.getDeliveredMetric()/cpuPanel.getDeliveredMetric(), netEff )) {
			throw new IllegalStateException("C not fulfilled");
		}		
	}	
	private void checkD() {
		if (!hasEnergyPerGopsServerWide()) return;
		if (!hasTotalConsumption()) return;
		if (!cpuPanel.hasDeliveredMetric()) return;
		double go = getEnergyPerGopsServerWide();
		if (compare(getTotalConsumption()/cpuPanel.getDeliveredMetric(), go )) {
			throw new IllegalStateException("D not fulfilled");
		}
		if (go < 0.1) {
			energyPerGopsServerWide.setBackground(Color.GREEN);
		}
		if (go < 0.33) {
			energyPerGopsServerWide.setBackground(Color.WHITE);
		}
		if (go < 1) {
			energyPerGopsServerWide.setBackground(Color.YELLOW);
		}			
		if (go < 3.3) {
			energyPerGopsServerWide.setBackground(Color.ORANGE);
		} 
		if (go >= 3.3) {
			energyPerGopsServerWide.setBackground(Color.RED);
		}		
	}
	private void checkE() {	
		if (!hasTotalConsumption()) return;
		if (!networkPanel.hasAverageConsumption()) return;
		if (!cpuPanel.hasAverageConsumption()) return;
		if (!ramPanel.hasAverageConsumption()) return;
		if (!hasConstantPowerOverhead()) return;
		if (!hasVariablePowerOverhead()) return;
		double totalInter = networkPanel.getAverageConsumption() + 
				          cpuPanel.getAverageConsumption() + 
				          ramPanel.getAverageConsumption() + getConstantPowerOverhead();
		double totalCom = (1 + getVariablePowerOverhead())*totalInter;
		double tot = getTotalConsumption();
		if (compare(getTotalConsumption(), totalCom )) {
			System.out.println("Problem with E");
			throw new IllegalStateException("E not fulfilled");
		}
		if (tot < 1 || tot > 500) {
			totalConsumption.setBackground(Color.RED);
		}
	}
	
	protected void calculate() {
		if (!hasEnergyPerGopsServerWide()) {
			if (energyPerGopsServerWide.isVirtuallyEnabled()) {
				_DsetEnergyGopsFromTotalconDeliveredCPU();
			}
		}
		if (!hasNetworkDesignAR()) {
			if (networkDesignAR.isVirtuallyEnabled()) {
				_AsetDesignARfromNetCPU();
			}
		}
		if (!hasNetworkEffectiveAR()) {
			if (networkEffectiveAR.isVirtuallyEnabled()) {
				_CsetNetEffARfromDeliveredCPUdeliveredNet();
			}
		}
		if (!hasRAMar()) {
			_BsetRAMARfromCPURam();
		}
		if (!hasTotalConsumption()) {
			MyJTextField f1 = null;
			MyJTextField f2 = null;
			if (totalConsumption.isVirtuallyEnabled()) {
				_DsetTotalconFromEnergyPerGopsDeliveredCPU();
				_EsetTotconFromCPUnetRAM();
			}
			if (f1 != null && f2 != null) {
				throw new IllegalStateException("to investigate");
			}
			
		}
		if (!hasVariablePowerOverhead()) {
			if (variablePowerOverhead.isVirtuallyEnabled()) {
				_EsetVariablePowerFromTotRamNetCPU();
			}
		}
		if (!hasConstantPowerOverhead()) {
			if (constantPowerOverhead.isVirtuallyEnabled()) {
				_EsetConstantPowerFromTotRamNetCPU();
			}
		}
	}
	
	private boolean compare(double d1, double d2) {
		return (Math.abs((d1/d2) - 1) > 0.001);
	}
	
	private void updateInit(JTextField source) {
		boolean enable = source.isEnabled();
		source.setEnabled(!enable);
		updateEnabled(null, !enable);
	}
	
	int[][] mappings = {{2,6,7},{3,7,8},{4,9,10}, {1,5,10}, {1,11,12,13,14,15}};
	
	private boolean[] getCurrentStatus() {
		boolean[] flags = new boolean[16];
		for (int i = 0 ; i < allGlobalFields.length-2 ; i++) {
			flags[i] = allGlobalFields[i].isEnabled();
		}
		flags[6] = networkPanel.metricTotal.isEnabled();
		flags[7] = cpuPanel.metricTotal.isEnabled();
		flags[8] = ramPanel.metricTotal.isEnabled();
		flags[9] = networkPanel.deliveredMetric.isEnabled();
		flags[10] = cpuPanel.deliveredMetric.isEnabled();
		flags[11] = cpuPanel.averageConsumption.isEnabled();
		flags[12] = networkPanel.averageConsumption.isEnabled();
		flags[13] = ramPanel.averageConsumption.isEnabled();
		flags[14] = constantPowerOverhead.isEnabled();
		flags[15] = variablePowerOverhead.isEnabled();
		return flags;
	}	

	public void updateEnabled(ElementPanel elementPanel, boolean newStatus) {
	//	if (inUpdateMode) return;
		
		boolean net, cpu, ram;
		
		
		int cycle = 0;
		do {
		
			
			modCount++;
			if (elementPanel != null) {
				inUpdateMode = true;
				System.out.print("");
			}
			boolean[] flags = getCurrentStatus();
			int sum, newSum;
			do {
				sum = MoreArrays.sum(flags);		
				for (int i = 0 ; i < mappings.length ; i++) {
					boolean[] nflags = updateEqN(flags, i, newStatus);
				}
				newSum = MoreArrays.sum(flags);
			} while (newSum != sum);
			
			System.out.println("cf  " + Arrays.toString(flags));
			
			for (int i = 0 ; i < allGlobalFields.length-2 ; i++) {
				 allGlobalFields[i].setEnabled(flags[i]);
			}
			networkPanel.setMetricEnabled(flags[6]);
			cpuPanel.setMetricEnabled(flags[7]);
			ramPanel.setMetricEnabled(flags[8]);
			networkPanel.setDeliveredMetricEnabled(flags[9]);
			cpuPanel.setDeliveredMetricEnabled(flags[10]);
			cpuPanel.setAverageEnabled(flags[11]);
			networkPanel.setAverageEnabled(flags[12]);
			ramPanel.setAverageEnabled(flags[13]);	
			constantPowerOverhead.setEnabled(flags[14]);
			variablePowerOverhead.setEnabled(flags[15]);
			
			net = networkPanel.updateInit(null, newStatus);
			cpu = cpuPanel.updateInit(null, newStatus);
			ram = ramPanel.updateInit(null, newStatus);
			System.out.println("cycle " + cycle++);
		}
		while (net != true || cpu != true || ram != true);
		

		inUpdateMode = false;
	}
	
	private int modCount = 0;
	private int[] modCountPerField = new int[16];	
	
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

	
	protected MyJTextField setSystemUtil(double util) {
		return ElementPanel.setTextField(systemUtil, util);
	}		
	protected MyJTextField setTotalConsumption(double con) {
		return ElementPanel.setTextField(totalConsumption, con);
	}	
	protected MyJTextField setNetworkDesignAR(double ar) {
		return ElementPanel.setTextField(networkDesignAR, ar);
	}	
	protected MyJTextField setNetworkEffectiveAR(double ar) {
		return ElementPanel.setTextField(networkEffectiveAR, ar);
	}
	protected MyJTextField setRAMar(double ar) {
		return ElementPanel.setTextField(ramAR, ar);
	}	
	protected MyJTextField setEnergyPerGopsServerWide(double en) {
		return ElementPanel.setTextField(energyPerGopsServerWide, en);
	}
	protected MyJTextField setConstantPowerOverhead(double en) {
		return ElementPanel.setTextField(constantPowerOverhead, en);
	}
	protected MyJTextField setVariablePowerOverhead(double en) {
		return ElementPanel.setTextField(variablePowerOverhead, en);
	}
	
	
	
	protected boolean hasSystemUtil() {
		return ElementPanel.hasAValue(systemUtil);
	}	
	protected boolean hasTotalConsumption() {
		return ElementPanel.hasAValue(totalConsumption);
	}	
	protected boolean hasNetworkDesignAR() {
		return ElementPanel.hasAValue(networkDesignAR);
	}	
	protected boolean hasNetworkEffectiveAR() {
		return ElementPanel.hasAValue(networkEffectiveAR);
	}	
	protected boolean hasRAMar() {
		return ElementPanel.hasAValue(ramAR);
	}	
	protected boolean hasEnergyPerGopsServerWide() {
		return ElementPanel.hasAValue(energyPerGopsServerWide);
	}	
	protected boolean hasConstantPowerOverhead() {
		return ElementPanel.hasAValue(constantPowerOverhead);
	}
	protected boolean hasVariablePowerOverhead() {
		return ElementPanel.hasAValue(variablePowerOverhead);
	}
	
	protected double getSystemUtil() {
		return systemUtil.getValue();
	}
	protected double getTotalConsumption() {
		return totalConsumption.getValue();
	}
	protected double getNetworkDesignAR() {
		return networkDesignAR.getValue();
	}
	protected double gettNetworkEffectiveAR() {
		return networkEffectiveAR.getValue();
	}
	protected double getRAMar() {
		return ramAR.getValue();
	}
	protected double getEnergyPerGopsServerWide() {
		return energyPerGopsServerWide.getValue();
	}
	protected double getConstantPowerOverhead() {
		return constantPowerOverhead.getValue();
	}
	protected double getVariablePowerOverhead() {
		return variablePowerOverhead.getValue();
	}	
	
	
	private void build() {
		JPanel main = new JPanel();
		
		main.setLayout(new GridBagLayout());
		
		this.getContentPane().add(main);

		addTitles(main);
		
		int line = 2;
		
		networkPanel.build(main, line++);
		cpuPanel.build(main, line++);
		ramPanel.build(main, line++);
		
		
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstants.BOTH;
		c.anchor = GridBagConstants.WEST;
		
		main.add(save, c);
		c.gridx++;
		main.add(load, c);
		c.gridx++;
		main.add(fileName, c);
		
		
		c.gridx = 2;
		c.gridy = line;	
		JLabel label = new JLabel("  Constant power overhead (W)");
		label.setFont(font);
		label.setUI(new VerticalLabelUI(false));		
		main.add(label, c);
		
		c.gridy++;
		constantPowerOverhead = new MyJTextField(6);
		main.add(constantPowerOverhead, c);
		
		c.gridx = 3;
		c.gridy = line;
		label = new JLabel("  Variable power overhead (ratio)");
		label.setFont(font);
		label.setUI(new VerticalLabelUI(false));		
		main.add(label, c);
		
		c.gridy++;
		variablePowerOverhead = new MyJTextField(6);
		main.add(variablePowerOverhead, c);		
		
		
		c.gridx = 5;
		c.gridy = line;		
		label = new JLabel("  System utilization");
		label.setFont(font);
		label.setUI(new VerticalLabelUI(false));		
	//	main.add(label, c);
		
		c.gridy++;
		systemUtil = new MyJTextField(6);
	//	main.add(systemUtil, c);	
		
		c.gridx = 9;
		c.gridy = line;
		label = new JLabel("  Total consumption (W)");
		label.setFont(font);
		label.setUI(new VerticalLabelUI(false));		
		main.add(label, c);
		
		c.gridy++;
		totalConsumption = new MyJTextField(6);
		main.add(totalConsumption, c);	
		
		c.gridx = 10;
		c.gridy = line;
		label = new JLabel("  Network aspect ratio (bit/OP)");
		label.setFont(font);
		label.setUI(new VerticalLabelUI(false));		
		main.add(label, c);
		
		c.gridy++;
		networkDesignAR = new MyJTextField(6);
		main.add(networkDesignAR, c);	
		
		c.gridx = 11;
		c.gridy = line;
		label = new JLabel("  Ram aspect ratio (bit/OP)");
		label.setFont(font);
		label.setUI(new VerticalLabelUI(false));		
		main.add(label, c);
		
		c.gridy++;
		ramAR = new MyJTextField(6);
		main.add(ramAR, c);	
		
		c.gridx = 12;
		c.gridy = line;
		label = new JLabel("  Network effective aspect ratio (bit/OP)");
		label.setFont(font);
		label.setUI(new VerticalLabelUI(false));		
		main.add(label, c);
		
		c.gridy++;
		networkEffectiveAR = new MyJTextField(6);
		main.add(networkEffectiveAR, c);
		
		c.gridx = 13;
		c.gridy = line;
		label = new JLabel("  Global energy per Op (nJ/OP)");
		label.setFont(font);
		label.setUI(new VerticalLabelUI(false));		
		main.add(label, c);
		
		c.gridy++;
		energyPerGopsServerWide = new MyJTextField(6);
		main.add(energyPerGopsServerWide, c);			
		
	}
	
	private Font font = new Font(Font.DIALOG, Font.BOLD, 11);

	private void addTitles(JPanel main) {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstants.BOTH;
		c.anchor = GridBagConstants.WEST;
		
		JLabel label = new JLabel("  Element      ");
		label.setFont(font);
		main.add(label, c);
		
		
		c.gridx++;
		c.gridwidth = 4;
		label = new JLabel("  Part in average consumption");
		label.setFont(font);
		label.setUI(new VerticalLabelUI(false));
		main.add(label, c);
		
		c.gridx = 5;
		c.gridwidth = 1;
		label = new JLabel("  Active consumption  (W)");
		label.setFont(font);
		label.setUI(new VerticalLabelUI(false));
		main.add(label, c);		
		
		c.gridx++;
		c.gridwidth = 1;
		label = new JLabel("  Passive consumption  (W)");
		label.setFont(font);
		label.setUI(new VerticalLabelUI(false));
		main.add(label, c);		
				
		c.gridx++;
		c.gridwidth = 1;
		label = new JLabel("  Proportionality  ");
		label.setFont(font);
		label.setUI(new VerticalLabelUI(false));
		main.add(label, c);		
		
		c.gridx++;
		c.gridwidth = 1;
		label = new JLabel("  Utilization  ");
		label.setFont(font);
		label.setUI(new VerticalLabelUI(false));
		main.add(label, c);	
				
		c.gridx++;	
		c.gridwidth = 1;
		label = new JLabel("  Average consumption  (W)");
		label.setFont(font);
		label.setUI(new VerticalLabelUI(false));
		main.add(label, c);		
		
		c.gridx += 2;	
		c.gridwidth = 1;
		label = new JLabel("  Specific metric  ");
		label.setFont(font);
		label.setUI(new VerticalLabelUI(false));
		main.add(label, c);
		
		c.gridx++;	
		c.gridwidth = 1;
		label = new JLabel("  Amount of metric  ");
		label.setFont(font);
		label.setUI(new VerticalLabelUI(false));
		main.add(label, c);		
		
		c.gridx++;	
		c.gridwidth = 1;
		label = new JLabel("  Delivered metric  ");
		label.setFont(font);
		label.setUI(new VerticalLabelUI(false));
		main.add(label, c);		
		
		c.gridx++;	
		c.gridwidth = 1;
		label = new JLabel("  Energy per metric  (for net: nJ/bit, for CPU: nJ/OP)");
		label.setFont(font);
		label.setUI(new VerticalLabelUI(false));
		main.add(label, c);		
		
		c.gridx++;	
		c.gridwidth = 1;
		label = new JLabel("  Energy per delivered metric  ");
		label.setFont(font);
		label.setUI(new VerticalLabelUI(false));
		main.add(label, c);		
		
		c.gridx++;	
		c.gridwidth = 1;
		label = new JLabel("  Energy per metric at 100%  ");
		label.setFont(font);
		label.setUI(new VerticalLabelUI(false));
		main.add(label, c);	
		
				
	}

}
