package edu.columbia.lrl.experiments.spinet.variants;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.path.Path;
import ch.epfl.general_libraries.path.ShortestPathSet;
import ch.epfl.general_libraries.utils.SimpleMap;
import ch.epfl.javanco.algorithms.GraphColouring;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.base.Javanco;
import ch.epfl.javanco.io.JavancoFile;
import ch.epfl.javanco.network.LayerContainer;
import ch.epfl.javanco.network.NodeContainer;
import edu.columbia.lrl.LWSim.components.Receiver;
import edu.columbia.lrl.experiments.spinet.AbstractSpinetBuilder;
import edu.columbia.lrl.experiments.spinet.SpinnetBuffer;
import edu.columbia.lrl.experiments.spinet.TwoTwoSwitch;
import edu.columbia.lrl.switch_arch.AbstractSwitchArchitectureGenerator;

public class TDM_Variant extends SpinnetVariant {
	
	protected double slotDuration;
	
	protected HashMap<Integer, TDMSpinnetBuffer> bufferMap = new HashMap<Integer, TDMSpinnetBuffer>();

	public TDM_Variant(@ParamName(name="Maximum buffer depth (in packets)", default_="10000") int maxBufferSize, 
			@ParamName(name="Buffer zero-load latency in ns", default_="0") double bufferLatency,
			@ParamName(name="Switching time in ns", default_="1") double switchingTime,  
			@ParamName(name="Slot duration") double slotDuration) {
		super(maxBufferSize, bufferLatency, switchingTime, true);
		this.slotDuration = slotDuration;
	}

	@Override
	public Receiver getExampleReceiver() {

		return new Receiver(-1);
		
		/* for bursts
		 */
	//	return new BurstReceiver(-1);
	}

	@Override
	public TwoTwoSwitch getExampleSwitch(String id) {
		return new TwoTwoSwitch(id, switchingTime, doubleWay);
	}

	@Override
	public SpinnetBuffer getExampleBuffer(int index) {
		TDMSpinnetBuffer buf = new TDMSpinnetBuffer(maxBufferSize, bufferLatency, switchingTime, slotDuration, index);
		bufferMap.put(index, buf);
		return buf;
	}

	@Override
	public Map<String, String> getVariantSpecificParameters() {
		SimpleMap<String, String> map = new SimpleMap<String, String>(1);
		map.put("slot duration", slotDuration+"");
		return map;
	}

	@Override
	public String getVariantName() {
		return "TDM variant";
	}
	
	@Override
	public double getMaxPacketDurationNS() {
		return slotDuration;
	}	
	
	
	public void configureTDM(AbstractSpinetBuilder buil, AbstractSwitchArchitectureGenerator gen) {
		// generating the graph
		AbstractGraphHandler agh = gen.generate();
		List<Integer> inputs = gen.getInputNodesIndexes();
		List<Integer> outputs = gen.getOutputNodesIndexes();		
		
		AbstractGraphHandler conflictGraph;
		
		int colors;
		
		String graphName = gen.getFullName() + "_" + gen.getNumberOfNodes() + "_" + buil.trafToSelf;
		
		if (JavancoFile.graphExists(graphName)) {
			conflictGraph = Javanco.loadGraph(graphName);
			colors = conflictGraph.getXML().attribute("color").intValue();
		} else {

			
			conflictGraph = getConflictGraph(agh, inputs, outputs, !buil.trafToSelf, false);
		
			GraphColouring colouring = new GraphColouring();
			colors = colouring.solve(conflictGraph);
			conflictGraph.getXML().addAttribute("color", colors+"");
			// assigning the slotNumber
			// saving graph
			try {
				for (NodeContainer lc : conflictGraph.getNodeContainers()) {
					lc.linkAllAttributes();
				}
				conflictGraph.saveNetwork(graphName);
			}
			catch (IOException e) {}
		}
		for (TDMSpinnetBuffer buf : bufferMap.values()) {
			buf.setSlotNumber(colors);
		}		
		
		int[][] slotId = new int[inputs.size()][inputs.size()];
		
		
		for (NodeContainer n : conflictGraph.getNodeContainers()) {
			int inputNb = n.attribute("start").intValue();
			int realInputNb = inputs.indexOf(new Integer(inputNb));
			int outputNb = n.attribute("end").intValue();
			int realOutputNb = outputs.indexOf(new Integer(outputNb));
			TDMSpinnetBuffer buf = bufferMap.get(realInputNb);
			slotId[realInputNb][realOutputNb] = n.attribute("color").intValue()-1;			
			buf.setSlotIdForDestination(n.attribute("color").intValue()-1, realOutputNb);

		}
		
	}
	
	public static AbstractGraphHandler getConflictGraph(
			AbstractGraphHandler agh, 
			List<Integer> inputs, 
			List<Integer> outputs,
			boolean avoidSelfPaths,
			boolean addRoutingAsAttribute) {
		// in javanco links and nodes are organised in layers
		// here we retrieved the one that has been last edited (where the generator put the network elements)
		LayerContainer layC = agh.getEditedLayer();

		// from a layer, we can obtain the incidence matrix
		boolean[][] incidenceMatrix = layC.getIncidenceMatrix();

		// from this matrix, we can compute a pathset
		ShortestPathSet pathSet = new ShortestPathSet(incidenceMatrix);
		
		AbstractGraphHandler conflictGraph = Javanco.getDefaultGraphHandler(false);
		conflictGraph.activateMainDataHandler();
		conflictGraph.newLayer("physical");

		HashMap<Path, NodeContainer> pathMap = new HashMap<Path, NodeContainer>();
		
		
		
		for (int input : inputs) {
			for (int output : outputs) {
				if (avoidSelfPaths && agh.getNodeContainer(input).attribute("input").intValue() == agh.getNodeContainer(output).attribute("output").intValue()) {
					// skip
				} else {
					NodeContainer nc = conflictGraph.newNode();
					nc.attribute("start").setValue(input);
					nc.attribute("end").setValue(output);
					Path p = pathSet.getPath(input, output);
					pathMap.put(p, nc);
				}
			}
		}
		
		for (Map.Entry<Path, NodeContainer> e1 : pathMap.entrySet()) {
			for (Map.Entry<Path, NodeContainer> e2 : pathMap.entrySet()) {
				if (e1.getKey() != e2.getKey()) {
					if (e1.getKey().isSharingOneSegment(e2.getKey(), true)) {
						conflictGraph.newLink(e1.getValue(), e2.getValue());
					}
				}
			}
		}
		
		

		// now we compare the paths and see if they have common segments		
	/*	for (int input1 : inputs) {
			for (int output1 : outputs) {
				Path p1 = pathSet.getPath(input1,  output1);

				NodeContainer n1 = pathMap.get(p1);
				if (n1 == null) {
					if (avoidSelfPaths && agh.getNodeContainer(p1.getFirst()).attribute("input").intValue() == agh.getNodeContainer(p1.getLast()).attribute("output").intValue()) {
					} else {
						n1 = conflictGraph.newNode();
						n1.attribute("start").setValue(p1.get(0));
						n1.attribute("end").setValue(p1.getLast());
						if (addRoutingAsAttribute) {
							n1.attribute("routing").setValue(p1.toString());
						}
						pathMap.put(p1, n1);
					}
				}

				for (int input2 : inputs) {
					if (input2 < input1) continue;
					for (int output2 : outputs) {
						if (input1 == input2 && output1 == output2) continue;
						Path p2 = pathSet.getPath(input2, output2);
						if (p1.isSharingOneSegment(p2, true)) {

							NodeContainer n2 = pathMap.get(p2);
							if (n2 == null) {	
								if (avoidSelfPaths && agh.getNodeContainer(p2.getFirst()).attribute("input").intValue() == agh.getNodeContainer(p2.getLast()).attribute("output").intValue()) {
								} else {								
									n2 = conflictGraph.newNode();
									n2.attribute("start").setValue(p2.get(0));
									n2.attribute("end").setValue(p2.getLast());								
									pathMap.put(p2, n2);
									if (addRoutingAsAttribute) {
										n2.attribute("routing").setValue(p2.toString());
									}									
								}
							}

							// from there I let you go on
						//	System.out.println(p1 + " is conflicting with " + p2);
							if (n1 != null && n2 != null) {
								conflictGraph.newLink(n1, n2);
							}
						}						
					}
				}

			}
		}*/
		return conflictGraph;
	}
	
	
	
	
	

}
