package edu.columbia.sebastien.autobooksim.specific_config.PINE;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import ch.epfl.general_libraries.utils.ExtendedStringBuilder;

public abstract class AbstractPINEconstruct {
	
	public abstract String getConfigurableFilePath();
	public abstract String getTypesOfNodeFilePath();

	
	public abstract  int getNumberOfCPUs();
	public abstract  int getNumberOfGPUs();
	public abstract  int getNumberOfMEMs();
	public abstract  int getNumberOfOthers();
	
	public abstract ArrayList<Integer> getConnectivityOfCPU(int index);
	public abstract ArrayList<Integer> getConnectivityOfGPU(int index);
	public abstract ArrayList<Integer> getConnectivityOfMEM(int index);
	public abstract ArrayList<Integer> getConnectivityOfOther(int index);
	
	
	public void writeConfigFiles() {
		writeTypesOfNodesFile();
		writeConnectionFile();
	}
	
	private String convertListToString(ArrayList<Integer> array) {
		ExtendedStringBuilder sb = new ExtendedStringBuilder();
		int size = array.size();
		for (int i = 0 ; i < size-1 ; i++) {
			sb.append(array.get(i));
			sb.append(" ");
		}
		if (size > 0) {
			sb.append(array.get(size-1));
		}
		return sb.toString();
	}
	
	private void writeConnectionFile() {
		try {
			FileWriter fw = new FileWriter(getConfigurableFilePath());
			
			// connecting cpus
			for (int i = 0 ; i < getNumberOfCPUs() ; i++) {
				fw.append(convertListToString(getConnectivityOfCPU(i))+"\r\n");
			}
			for (int i = 0 ; i < getNumberOfGPUs() ; i++) {
				fw.append(convertListToString(getConnectivityOfGPU(i))+"\r\n");
			}
			for (int i = 0 ; i < getNumberOfMEMs() ; i++) {
				fw.append(convertListToString(getConnectivityOfMEM(i))+"\r\n");
			}
			for (int i = 0 ; i < getNumberOfOthers() ; i++) {
				fw.append(convertListToString(getConnectivityOfOther(i))+"\r\n");
			}
			
			fw.close();
			
		}
		catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
	private void writeTypesOfNodesFile() {
		try {
			FileWriter fw = new FileWriter(getTypesOfNodeFilePath());
			
			fw.append("numcpus" + getNumberOfCPUs() + "\r\n");
			fw.append("numgpus" + getNumberOfGPUs() + "\r\n");
			fw.append("nummems" + getNumberOfMEMs() + "\r\n");
			fw.append("numothers" + getNumberOfOthers() + "\r\n");
			fw.append("Table    cpus   gpus   mems   others\r\n");
			
			// for now the "traffic matrix" is hard coded. Need to find a way to
			// generate it automatically in a way that makes sense
			fw.append("cpus     0.1    0.05    0.08    0.00\r\n");			
			fw.append("gpus     0.05    0.005    0.12    0.00\r\n");			
			fw.append("mems     0.04    0.06    0.001      0.00\r\n");			
			fw.append("others   0.00    0.000    0.00      0.00\r\n");			
			
			fw.close();
		}
		catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
}
