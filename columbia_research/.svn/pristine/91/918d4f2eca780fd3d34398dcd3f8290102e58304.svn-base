package edu.columbia.lrl.experiments.topology_radix.routing_study.structures;

public class DirectLinkStructure extends AbstractAxisStructure {

	static {
		AbstractAxisStructure.registerSubClass('-', emptyA1, DirectLinkStructure.class);
	}	
	
	public DirectLinkStructure(short size) {
		super(size);
		if (size != 2) {
			throw new IllegalStateException("Cannot use size different than 2");
		}
	}
	
	@Override
	protected int[][] getIndexesOfLinksUsed(int from, int to) {
		if (from == 1) {
			return new int[][]{{0},{-1}};
		} else {
			return new int[][]{{0},{1}};
		}
	}

	@Override
	public int getNumberOfLinksInStructure() {
		return 1;
	}

	@Override
	public int[] getIndexesOfLinksConnectedTo(int nodeIndex) {
		return new int[]{0};
	}

	@Override
	public int[] getExtremitiesOfLink(int k) {
		return new int[]{0,1};
	}

	@Override
	public int getRadix() {
		return 1;
	}

	@Override
	public int getDiameter() {
		return 1;
	}

	@Override
	public AbstractAxisStructure getInstance(short s) {
		return new DirectLinkStructure(s);
	}

}
