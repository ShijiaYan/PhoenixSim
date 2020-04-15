package edu.columbia.lrl.experiments.topology_radix.routing_study.structures;

import java.io.File;

import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;

public class Diameter2Structure extends AbstractAxisStructure {

	static {
		int[] sizes = new int[]{10,15,24,32,50,57,98,104,133,162,183,187,198,242,338, 578,722,1058, 1682,1922, 2738,4802};
		AbstractAxisStructure.registerSubClass('B', sizes, Diameter2Structure.class);
	}	
	
	private AbstractAxisStructure subStruct;
	
	public Diameter2Structure(short size) {
		super(size);
		if (size <= 5) {
			subStruct = new RingStructure(size);
		}
	//	if (size == 98 || size == 162 || size > 198) {
	//		subStruct = new McKayMillerSiranBasedStructure(size);
	//	} else {
			String dir = "../columbia_research/data/graphs/";
			String foundName = null;
			File f = new File(dir);
			for (String sf : f.list()) {
				String[] bits = sf.split("_");
				try {
					if (bits[0].equals("G2") && Integer.parseInt(bits[2]) == size) {
						foundName = sf;
						break;
					}
				}
				catch (NumberFormatException e) {
				}
			}		
			if (foundName == null)
				throw new WrongExperimentException("Cannot build diameter 2 structure of size" + size);
			subStruct = new JavancoAdjacencyFileBasedStructure(dir, foundName);
	//	}
	}	
	
	@Override
	public AbstractAxisStructure getInstance(short s) {
		return new Diameter2Structure(s);
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
		return 2;
	}

}
