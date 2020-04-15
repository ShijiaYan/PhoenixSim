package edu.columbia.lrl.experiments.topology_radix.topogen;

import java.util.Arrays;
import java.util.Map;

import javancox.topogen.AbstractTopologyGenerator;

import ch.epfl.javanco.algorithms.BFS;
import ch.epfl.javanco.base.AbstractGraphHandler;

public class TopologyDescription {
	
	
	public TopologyDescription(int nodeDegree, String desc, AbstractTopologyGenerator generator) {
		this.nodeDegree = nodeDegree;
		this.desc = desc;
		this.generator = generator;
	}
	
	private int nodeDegree;
	
	public int getNodeDegree() {
		return nodeDegree;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	private int[] distances;
	private String desc;
	AbstractTopologyGenerator generator;
	private AbstractGraphHandler agh;
	
	
	
	
	public int[] getSortedDistances() {
		if (distances == null) {
			distances = BFS.getDistancesFromUndirected(getAgh(), 0);
			Arrays.sort(distances);	
		}
		return distances;
	}
	
	protected AbstractGraphHandler getAgh() {
		if (agh == null)
			agh = generator.generate();
		return agh;
	}
	
	public Map<String, String> getAllParameters() {
		return generator.getAllParameters();
	}
	
}
