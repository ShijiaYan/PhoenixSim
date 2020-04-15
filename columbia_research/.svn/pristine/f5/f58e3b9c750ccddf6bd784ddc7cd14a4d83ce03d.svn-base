package edu.columbia.lrl.experiments.topology_radix.routing_study.structures;

import java.util.Map;

import javancox.topogen.AbstractTopologyGenerator;
import ch.epfl.javanco.base.AbstractGraphHandler;

public class JavancoGeneratorBasedStructure extends JavancoBasedAxisStructure {
	
	private static JavancoBasedAxisStructure singleton;
	
	private AbstractTopologyGenerator generator;
	
	static {
		AbstractAxisStructure.registerSubClass('$', emptyA1, JavancoGeneratorBasedStructure.class);
	}
	
	// used by singleton
	private JavancoGeneratorBasedStructure() {
		super((short)-1);
	}
	
	// used by getInstance method
	private JavancoGeneratorBasedStructure(short i) {
		super(i);
	}
	
	public JavancoGeneratorBasedStructure(AbstractTopologyGenerator generator) {
		super((short)generator.getNumberOfNodes());
		this.generator = generator;
		final AbstractGraphHandler agh = generator.generate();
		int maxRadix = 0;
		for (int i = 0; i <= agh.getHighestNodeIndex() ; i++) {
			int c = agh.getNodeContainer(i).getConnectedLinks().size();
			if (c > maxRadix) {
				maxRadix = c;
			}
		}
		final int mr = maxRadix;
		singleton = new JavancoBasedAxisStructure((short)-1) {

			@Override
			public int getRadix() {
				return mr;
			}

			@Override
			public int getDiameter() {
				// something as "agh.getDiatemer()
				return 0;
			}

			@Override
			public AbstractAxisStructure getInstance(short s) {
				throw new IllegalStateException();
			}
			
		};
		super.interpret(agh, singleton);
	}
	
	public AbstractAxisStructure getInstance(short s) {
		return new JavancoGeneratorBasedStructure(s);
	}
	
	public Map<? extends String, ? extends String> getParameters() {
		return generator.getAllParameters();
	}	

	@Override
	protected int[][] getIndexesOfLinksUsed(int from, int to) {
		return singleton.getIndexesOfLinksUsed(from, to);
	}

	@Override
	public int getNumberOfLinksInStructure() {
		return singleton.getNumberOfLinksInStructure();
	}

	@Override
	public int[] getIndexesOfLinksConnectedTo(int nodeIndex) {
		return singleton.getIndexesOfLinksConnectedTo(nodeIndex);
	}

	@Override
	public int[] getExtremitiesOfLink(int k) {
		return singleton.getExtremitiesOfLink(k);
	}

	@Override
	public int getRadix() {
		return singleton.getRadix();
	}	
	
	@Override
	public int getDiameter() {
		return singleton.getDiameter();
	}
	

}
