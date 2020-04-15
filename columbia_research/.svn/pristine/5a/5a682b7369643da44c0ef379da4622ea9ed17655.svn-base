package edu.columbia.lrl.experiments.topology_radix.routing_study.structures;

import ch.epfl.general_libraries.utils.Pair;

public class Diameter4Structure extends AbstractAxisStructure {

	private JavancoBasedAxisStructure subStruct;		
	
	static {
		int[] sizes = new int[]{38,98,212,390};
		AbstractAxisStructure.registerSubClass('D', sizes, Diameter4Structure.class);
	}	
	
	public Diameter4Structure(short size) {
		super(size);
		
		Pair<Integer, JavancoAdjacencyFileBasedStructure> pair = DiameterNStructure.init(4, size);
		subStruct = pair.getSecond();
	}
	
	@Override
	public AbstractAxisStructure getInstance(short s) {
		return new Diameter4Structure(s);
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
		return 4;
	}	

}
