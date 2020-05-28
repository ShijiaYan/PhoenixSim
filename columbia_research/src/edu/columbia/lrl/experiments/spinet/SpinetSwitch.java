package edu.columbia.lrl.experiments.spinet;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.general_libraries.path.Path;
import ch.epfl.javanco.algorithms.BFS;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.network.LinkContainer;
import ch.epfl.javanco.network.NodeContainer;

import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.LWSim.LWSimComponent;
import edu.columbia.lrl.LWSim.TrafficDestination;
import edu.columbia.lrl.LWSim.TrafficTransitPoint;
import edu.columbia.lrl.experiments.spinet.variants.SpinnetVariant;
import edu.columbia.lrl.switch_arch.AbstractSwitchArchitectureGenerator;

public class SpinetSwitch {
	
	AbstractSwitchArchitectureGenerator gen;
	SpinnetVariant variant;
	
	// this variable should be removed in the future
	AbstractGraphHandler agh;
	
	public AbstractGraphHandler getagh(){
		return agh;
	}
	
	public SpinetSwitch(AbstractSwitchArchitectureGenerator gen, SpinnetVariant variant) {
		this.gen = gen;
		this.variant = variant;
	}

	public void build(LWSIMExperiment lwSimExperiment,
			ArrayList<LWSimComponent> dests,
			TrafficTransitPoint[] origins,
			TrafficTransitPoint[] destinations,
			String switchName) {		
		
		agh = gen.generate();
		
		ArrayList<TwoTwoSwitch> switches = new ArrayList<>();
		
		// instanciation of the switches
		for (Integer switchIndex : gen.getSwitchingNodesIndexes()) {
			NodeContainer nc = agh.getNodeContainer(switchIndex);
			String id;
			if (switchName != null) {
				id = switchName + nc.attribute("switch_id").getValue() +"";
			} else {
				id = nc.attribute("switch_id").getValue() +"";
			}
			TwoTwoSwitch sw = variant.getExampleSwitch(id);
			nc.setNode(sw);
			dests.add(sw);
			switches.add(sw);
		}	
		
		// make sure that the switches are connected with links that mentionned the port
		for(Integer switchIndex : gen.getSwitchingNodesIndexes()) {
			NodeContainer nc = agh.getNodeContainer(switchIndex);
			TwoTwoSwitch sw = (TwoTwoSwitch)nc.getNode();
			
			List<LinkContainer> incoming = nc.getAllIncomingLinks();
			if (incoming.size() > 2) throw new IllegalStateException("No more than two inputs per 2x2");
			
			if (incoming.size() == 1) {
				LinkContainer lc1 = incoming.get(0);
				lc1.attribute("dest_port").setValue("up");
				setAsOrigin(lc1, sw, 0, origins);
			}
			if (incoming.size() == 2) {
				LinkContainer lc1 = incoming.get(0);				
				LinkContainer lc2 = incoming.get(1);
				if (lc1.getOtherNodeIndex(nc.getIndex()) > lc2.getOtherNodeIndex(nc.getIndex())) {
					setAsOrigin(lc1, sw, 1, origins);
					setAsOrigin(lc2, sw, 0, origins);
					lc1.attribute("dest_port").setValue("down");
					lc2.attribute("dest_port").setValue("up");
				} else {
					lc2.attribute("dest_port").setValue("down");
					lc1.attribute("dest_port").setValue("up");
					setAsOrigin(lc1, sw, 0, origins);
					setAsOrigin(lc2, sw, 1, origins);
				}
			}			
			
			List<LinkContainer> outgoing = nc.getAllOutgoingLinks();
			
			if (outgoing.size() > 2) throw new IllegalStateException("only two output port max per 2x2");
			
			if (outgoing.size() == 1) {
				LinkContainer lc1 = outgoing.get(0);
				lc1.attribute("orig_port").setValue("up");
				setAsDestination(lc1, sw, 0, destinations);
			}
			if (outgoing.size() == 2) {
				LinkContainer lc1 = outgoing.get(0);				
				LinkContainer lc2 = outgoing.get(1);
				if (lc1.getOtherNodeIndex(nc.getIndex()) > lc2.getOtherNodeIndex(nc.getIndex())) {
					lc1.attribute("orig_port").setValue("down");
					lc2.attribute("orig_port").setValue("up");
					setAsDestination(lc1, sw, 1, destinations);
					setAsDestination(lc2, sw, 0, destinations);
				} else {
					lc2.attribute("orig_port").setValue("down");
					lc1.attribute("orig_port").setValue("up");	
					setAsDestination(lc1, sw, 0, destinations);
					setAsDestination(lc2, sw, 1, destinations);					
				}				
			}
		}
		
		int[][] decisions;		
		if (gen.hasOwnRouting()) { 
			decisions = gen.getRouting();
		} else {
			decisions = getRoutingDecisions2(agh, gen.getSwitchingNodesIndexes(), gen.getOutputNodesIndexes(), destinations);	
		}
		for (int i = 0 ; i < switches.size(); i++) {
			switches.get(i).setRoutingDecisions(decisions[i]);
		}		
	}
	
	private void setAsOrigin(LinkContainer lc1, TwoTwoSwitch sw, int port, TrafficTransitPoint[] origins) {
		NodeContainer other = lc1.getOtherNodeContainer(sw.getNodeContainer().getIndex());
		if (other.getNode() != null && other.getNode() instanceof TwoTwoSwitch) {
			sw.setEventOrigin((TwoTwoSwitch)other.getNode(), port);
		} else if (other.attribute("input", false) != null) {
			int index = other.attribute("input").intValue();
			sw.setEventOrigin(origins[index], port);
			origins[index].setTrafficDestination(sw);
		} else if (sw.getNodeContainer().attribute("type").getValue().equals("1x2")) {
			sw.setEventOrigin((TwoTwoSwitch)other.getNode(), port);
		} else {
			throw new IllegalStateException("Switch " + sw + " is connected to something that is not a switch nor an input");
		}
	}	
	
	private void setAsDestination(LinkContainer lc1, TwoTwoSwitch sw, int port, TrafficTransitPoint[] destinations) {
		NodeContainer other = lc1.getOtherNodeContainer(sw.getNodeContainer().getIndex());
		if (other.getNode() != null && other.getNode() instanceof TwoTwoSwitch) {
			sw.setTrafficDestination((TwoTwoSwitch)other.getNode(), port);
		} else if (other.attribute("output", false) != null) {
			int index = other.attribute("output").intValue();
			sw.setTrafficDestination(destinations[index], port);
			destinations[index].setTrafficOrigin(sw);
		} else {
			// in this case we have aggregating outputs, looking for the next node
			NodeContainer nextNode = lc1.getEndNodeContainer().getOutgoingLinks().get(0).getEndNodeContainer();
			if (nextNode.attribute("output", false) != null) {
				int index = nextNode.attribute("output").intValue();
				sw.setTrafficDestination(destinations[index], port);
				destinations[index].setTrafficOrigin(sw);
			} else {
				throw new IllegalStateException("Switch " + sw + " is connected to something that is not a switch nor an output");
			}
		}
	}
	
	private int[][] getRoutingDecisions2(AbstractGraphHandler agh, 
										 List<Integer> swIndexes, 
										 List<Integer> outputIndexes,
										 TrafficDestination[] destinations) {
		int clients = outputIndexes.size();
		//boolean[][] b = agh.getLayerContainer("physical").getIncidenceMatrix();
		int[][] decisions = new int[swIndexes.size()][clients];		
		//ShortestPathAlgorithm alg = new ShortestPathAlgorithm(b, true);
		//alg.computeAll();
		//System.gc();
		//PathSet ps = new PathSet(nodeNumber)
		
		
		//PathSet pathSet = alg.getResults().getPathSet();
		
		for (int i = 0 ; i < swIndexes.size() ; i++) {
			int switchIndex = swIndexes.get(i);
		
	//	for (Integer switchIndex__ : swIndexes) {
			TwoTwoSwitch currentSwitch = (TwoTwoSwitch)agh.getNodeContainer(switchIndex).getNode();
			for (Integer outputIndex : outputIndexes) {
				int effectiveOutputIndex = agh.getNodeContainer(outputIndex).attribute("output").intValue();
				Path[] pathsToOutput = BFS.getShortestPathsTo(agh, outputIndex);
				
				Path path = pathsToOutput[switchIndex]; //pathSet.getPath(switchIndex, outputIndex);
				if (path == null) {
					decisions[i][effectiveOutputIndex] = -1;
				} else {
					int nextIndex = path.get(1);
					if (outputIndexes.contains(nextIndex)) {
						// nextNode (nextIndex) is contained in the outputlist --> nextIndex is THE output destination				
						if (currentSwitch.upDest == destinations[effectiveOutputIndex]) {
							decisions[i][effectiveOutputIndex] = 0;							
						} else if (currentSwitch.downDest == destinations[effectiveOutputIndex]) {
							decisions[i][effectiveOutputIndex] = 1;
						} else {
							throw new IllegalStateException();
						}
					} else {
						if (currentSwitch.upDest instanceof TwoTwoSwitch && currentSwitch.downDest instanceof TwoTwoSwitch) {
							// both next destinations are switches
							int alternativeIndex;
							if (((TwoTwoSwitch)currentSwitch.upDest).getIndex() == nextIndex) {
							/*	if (switchIndex == 12 || effectiveOutputIndex == 12) {
									int erfeur = 0;
								}*/
								decisions[i][effectiveOutputIndex] = 0;
								alternativeIndex = ((TwoTwoSwitch)currentSwitch.downDest).getIndex();
							} else {
								decisions[i][effectiveOutputIndex] = 1;
								alternativeIndex = ((TwoTwoSwitch)currentSwitch.upDest).getIndex();
							}
							Path p2 = pathsToOutput[alternativeIndex]; //pathSet.getPath(alternativeIndex, outputIndex);
							if (p2 != null) {
								decisions[i][effectiveOutputIndex] = 2;
							}		
						} else {
							if (currentSwitch.upDest instanceof TwoTwoSwitch) {
								decisions[i][effectiveOutputIndex] = 0;
							} else {
								// check if node is not just a proxy. If yes, route toward the end node
								List<LinkContainer> out = agh.getNodeContainer(nextIndex).getOutgoingLinks();
								if (out.size() == 1 && outputIndexes.contains(out.get(0).getEndNodeIndex())) {
									if (currentSwitch.upDest == destinations[effectiveOutputIndex]) {
										decisions[i][effectiveOutputIndex] = 0;		
										if (currentSwitch.downDest == destinations[effectiveOutputIndex]) {
											decisions[i][effectiveOutputIndex] = 2;	
										}
									} else if (currentSwitch.downDest == destinations[effectiveOutputIndex]) {
										decisions[i][effectiveOutputIndex] = 1;
									} else {
										throw new IllegalStateException();
									}
								} else {
									decisions[i][effectiveOutputIndex] = 1;
								}
							}
						}
					}
				}
			}
		}	
		return decisions;
	}
}
