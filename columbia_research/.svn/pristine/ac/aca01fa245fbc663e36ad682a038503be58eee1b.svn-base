package edu.columbia.sebastien.autobooksim.specific_config.PINE;

import java.util.ArrayList;

import ch.epfl.general_libraries.clazzes.ParamName;

public class TestPINEconstruct extends AbstractPINEconstruct {
	
	private int scale;
	
	public TestPINEconstruct(@ParamName(name="scale", default_="4") int scale) {
		this.scale = scale;
	}
	
	public int getNumberOfCPUs() {
		return scale;
	}
	
	public int getNumberOfGPUs() {
		return scale;
	}
	
	public int getNumberOfMEMs() {
		return scale;
	}
	
	public int getNumberOfOthers() {
		return scale;
	}

	@Override
	public String getConfigurableFilePath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTypesOfNodeFilePath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Integer> getConnectivityOfCPU(int index) {
		ArrayList<Integer> list = new ArrayList<Integer>();
		list.add(index); //  the cpu index
		list.add(index + getNumberOfCPUs());
		list.add(index + getNumberOfCPUs() + getNumberOfGPUs());
		list.add(index + getNumberOfCPUs() + getNumberOfGPUs() + getNumberOfMEMs());
		
		return list;
		
	}

	@Override
	public ArrayList<Integer> getConnectivityOfGPU(int index) {
		int realIndex = index + getNumberOfCPUs();
		ArrayList<Integer> list = new ArrayList<Integer>();
		list.add(realIndex);
		list.add(realIndex - getNumberOfCPUs());
		return list;
	}

	@Override
	public ArrayList<Integer> getConnectivityOfMEM(int index) {
		int realIndex = index + getNumberOfCPUs() + getNumberOfGPUs();
		ArrayList<Integer> list = new ArrayList<Integer>();
		list.add(realIndex);
		list.add(realIndex - getNumberOfCPUs() - getNumberOfGPUs());
		return list;
	}

	@Override
	public ArrayList<Integer> getConnectivityOfOther(int index) {
		int realIndex = index + getNumberOfCPUs() + getNumberOfGPUs() + getNumberOfMEMs();
		ArrayList<Integer> list = new ArrayList<Integer>();
		list.add(realIndex);
		list.add(realIndex - getNumberOfCPUs() - getNumberOfGPUs() - getNumberOfMEMs());
		if (index > 0) {
			list.add(realIndex-1);
		}
		if (index < getNumberOfOthers()-1) {
			list.add(realIndex + 1);
		}
		return list;
	}

}
