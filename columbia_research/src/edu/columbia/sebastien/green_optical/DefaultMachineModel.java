package edu.columbia.sebastien.green_optical;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;

public class DefaultMachineModel {
	
	private double totalPetaflops;
	private double nodeTeraflops;
	private double bytePerFlop;
	
	public DefaultMachineModel(@ParamName(name="System petaflop", default_="1") double totalPetaflops,
							   @ParamName(name="Node teraflop", default_="0.5") double nodeTeraflops,
							   @ParamName(name="Byte per flop", default_="0.05") double bytePerFlop) {
		this.totalPetaflops = totalPetaflops;
		this.nodeTeraflops = nodeTeraflops;
		this.bytePerFlop = bytePerFlop;
	}
	
	public double getNodeBandwidthInGbs() {
		return nodeTeraflops * bytePerFlop * 8 * 1000;
	}
	
	public int getNumberOfNodes() {
		return (int)Math.ceil(totalPetaflops*1000/nodeTeraflops);
	}
	
	public double getTotalBandwidthInGbs() {
		return getNumberOfNodes()*getNodeBandwidthInGbs();
	}

	public Map<String, String> getAllProperties() {
		Map<String, String> m = new SimpleMap<String, String>();
		m.put("Total comp. pow. PF", totalPetaflops+"");
		m.put("Node pow TF", nodeTeraflops+"");
		m.put("Byte per flop", bytePerFlop+"");
		m.put("Node bandwidth in Gbs", getNodeBandwidthInGbs()+"");
		return m;
	}

	public double getGFlops() {
		return totalPetaflops*1e6d;
	}

}
