package edu.columbia.lrl.experiments.topology_radix.routing_study.structures;

import java.io.File;

import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.utils.Pair;

public class DiameterNStructure extends AbstractAxisStructure {
	
	private JavancoBasedAxisStructure subStruct;
	private int diameter;

	static {
		int[] sizes = new int[]{-1};
		AbstractAxisStructure.registerSubClass('x', sizes, DiameterNStructure.class);
	}		
	
	/**
	 *  Unsafe, could be used by under class. Exist to allow config from the cockpit
	 */
	public DiameterNStructure() {
		super((short)-1);
	}
	
	public DiameterNStructure(short size) {
		super(size);
		Pair<Integer, JavancoAdjacencyFileBasedStructure> pair = init(-1, size);
		subStruct = pair.getSecond();
		diameter = pair.getFirst();
	}	
	
	@Override
	public AbstractAxisStructure getInstance(short s) {
		return new DiameterNStructure(s);
	}

	
	static Pair<Integer, JavancoAdjacencyFileBasedStructure> init(int diameter__, int size) {
		String dir = "../columbia_research/data/graphs/";
		String foundName = null;
		File f = new File(dir);
		for (String sf : f.list()) {
			String[] bits = sf.split("_");
			try {
				if (Integer.parseInt(bits[2]) == size) {
					if (diameter__ == -1) {
						diameter__ = Integer.parseInt(bits[0].replaceAll("G", ""));
					} else {
						if (!bits[0].equals("G"+diameter__)) {
							continue;
						}
					}
					foundName = sf;
					break;
				}
			}
			catch (NumberFormatException e) {}
		}		
		if (foundName == null)
			throw new WrongExperimentException("Cannot build diameter " + diameter__ + " structure of size" + size);
		Pair<Integer, JavancoAdjacencyFileBasedStructure> pair = new Pair<>();
		pair.setFirst(diameter__);
		pair.setSecond(new JavancoAdjacencyFileBasedStructure(dir, foundName));
		return pair;
	}

	@Override
	protected int[][] getIndexesOfLinksUsed(int from, int to) {
		return subStruct.getIndexesOfLinksUsed(from, to);
	}

	@Override
	public int getNumberOfLinksInStructure() {
		return subStruct.getNumberOfLinksInStructure();
	}

	@Override
	public int[] getIndexesOfLinksConnectedTo(int nodeIndex) {
		return subStruct.getIndexesOfLinksConnectedTo(nodeIndex);
	}

	@Override
	public int[] getExtremitiesOfLink(int k) {
		return subStruct.getExtremitiesOfLink(k);
	}

	@Override
	public int getRadix() {
		return subStruct.getRadix();
	}
	
	@Override
	public int getDiameter() {
		return diameter;
	}	

}
