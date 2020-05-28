package edu.columbia.lrl.switch_arch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.network.LinkContainer;
import ch.epfl.javanco.network.NodeContainer;
import ch.epfl.general_libraries.clazzes.*;
import ch.epfl.general_libraries.gui.reflected.MethodDef;
import ch.epfl.general_libraries.gui.reflected.ParameterDef;
import ch.epfl.general_libraries.utils.SimpleMap;

public class SpankeBenes extends AbstractSwitchArchitectureGenerator {

	int radix;
	int numCells;
	int[] inputIDs;
	int[] outputIDs;
	int[] switchIDs;
	
	public SpankeBenes(@ParamName(name = "Radix") int radix) {
		this.radix = radix;
	}

	@Override
	public List<Integer> getInputNodesIndexes() {
		List<Integer> list = new ArrayList<>();
		for( int id : inputIDs)
			list.add(id);
		return list;
	}

	@Override
	public List<Integer> getOutputNodesIndexes() {
		List<Integer> list = new ArrayList<>();
		for( int id : outputIDs)
			list.add(id);
		return list;
	}

	@Override
	public List<Integer> getSwitchingNodesIndexes() {
		List<Integer> list = new ArrayList<>();
		for( int id : switchIDs)
			list.add(id);
		return list;
	}

	@Override
	public int getMaxNumberOfStages() {
		throw new NotImplementedException(); // Appears this is only used by SPINet stuff
	}

	@Override
	public boolean hasOwnRouting() {
		throw new NotImplementedException(); //SPINet
	}

	@Override
	public int[][] getRouting() {
		throw new NotImplementedException(); //Used only by SPINet
	}

	@Override
	public void generate(AbstractGraphHandler agh) {
		ensureLayer(agh);
		numCells = radix * (radix - 1) / 2;

		switchIDs = new int[numCells];
		inputIDs = new int[radix];
		outputIDs = new int[radix];

		int switchIndex = 0;
		for (int i = 0; i < radix; i++) {
			// Build switch fabric
			int cellsInStage = i % 2 == 0 ? radix / 2 : radix / 2 - 1;
			for (int j = 0; j < cellsInStage; j++) {
				NodeContainer node = agh.newNode(i * 30, j * 30 + (i % 2 == 0 ? 0 : 15));
				node.attribute("switch_id").setValue(String.format("%1$-3d", i) + "-" + String.format("%1$3d", j));
				node.attribute("type").setValue(TYPE_2x2);
				switchIDs[switchIndex++] = node.getIndex();
			}
		}
		
		for( int i = 0; i < radix; i++ ) {
			// Build inputs and outputs
			NodeContainer input = agh.newNode(-30, i * 15 - 7);
			input.attribute("input").setValue(radix + "");
			inputIDs[i] = input.getIndex();
		}
		
		for( int i = 0; i < radix; i++ ) {
			NodeContainer output = agh.newNode(30 * radix, i * 15 - 7);
			output.attribute("output").setValue(radix + "");
			outputIDs[i] = output.getIndex();
		}

		// Make connections
		for (int i = 0; i < radix / 2; i++) {
			// inputs
			agh.newLink(inputIDs[i * 2], switchIDs[i]).attribute("directed").setValue("true");
			agh.newLink(inputIDs[i * 2 + 1], switchIDs[i]).attribute("directed").setValue("true");

			// outputs
			if (i == 0) {
				agh.newLink(switchIDs[numCells - radix / 2], outputIDs[radix - 1]).attribute("directed")
						.setValue("true");
				agh.newLink(switchIDs[numCells - radix + 1], outputIDs[0]).attribute("directed").setValue("true");
			} else {
				agh.newLink(switchIDs[numCells - radix / 2 + i], outputIDs[i * 2 - 1]).attribute("directed")
						.setValue("true");
				agh.newLink(switchIDs[numCells - radix / 2 + i], outputIDs[i * 2]).attribute("directed")
						.setValue("true");
			}
		}
		switchIndex = 0;
		for (int i = 0; i < radix - 1; i++) {
			int cellsInStage = i % 2 == 0 ? radix / 2 : radix / 2 - 1;
			for (int j = 0; j < cellsInStage; j++) {

				// "up" link
				if (!(j == cellsInStage - 1 && i == radix - 2)) {
					if (j == cellsInStage - 1 && cellsInStage == radix / 2) {
						agh.newLink(switchIDs[switchIndex], switchIDs[switchIndex + radix - 1]).attribute("directed")
								.setValue("true");
					} else {
						agh.newLink(switchIDs[switchIndex], switchIDs[switchIndex + radix / 2]).attribute("directed")
								.setValue("true");
					}
				}

				// "down" link
				if (!(j == 0 && i == radix - 2)) {
					if (j == 0 && cellsInStage == radix / 2) {
						agh.newLink(switchIDs[switchIndex], switchIDs[switchIndex + radix - 1]).attribute("directed")
								.setValue("true");
					} else {
						agh.newLink(switchIDs[switchIndex], switchIDs[switchIndex + radix / 2 - 1])
								.attribute("directed").setValue("true");
					}
				}

				switchIndex++;
			}
		}
	}

	@Override
	public String getName() {
		return "SpankeBenes";
	}

	@Override
	public Map<String, String> getGeneratorParameters() {
		return SimpleMap.getMap("Radix", radix + "");
	}

	@Override
	public int getNumberOfNodes() {
		return numCells + 2 * radix;
	}

	@Override
	public int getNumberOfLinksInvolved() {
		throw new NotImplementedException();
	}

	// For GUI
	public static class SpankeBenesSwitch extends WebTopologyGeneratorStub {

		@MethodDef
		public String generateSpankeBenes(AbstractGraphHandler agh, @ParameterDef(name = "Radix") int radix)
		// @ParameterDef (name="Extra stages")int extraStages)
		{

			SpankeBenes gen = new SpankeBenes(radix);
			gen.generate(agh);
			int index = 0;
			for (LinkContainer lc : agh.getLinkContainers()) {
				;
				lc.attribute("label").setValue(index);
				index++;
			}

			return null;
		}
	}
	
	
}
