package edu.columbia.lrl.experiments.topology_radix.routing_study.structures;

import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javancox.inputs.topology.McKayMillerSiranGenerator;

public class McKayMillerSiranBasedStructure extends JavancoBasedAxisStructure {

	static {
		AbstractAxisStructure.registerSubClass('s', new int[]{50, 98, 242,338, 578,722,1058, 1682,1922, 2738, 5618, 10658}, McKayMillerSiranBasedStructure.class);
	}
	
	private McKayMillerSiranGenerator gen;
	
	public McKayMillerSiranBasedStructure(short size) {
		super(size);
		gen = new McKayMillerSiranGenerator(size);
		setSize((short)gen.getNumberOfNodes());
		if (gen.isValid()) {
			AbstractGraphHandler agh = gen.generate();
			super.interpret(agh, this);
		} else {
			throw new IllegalStateException("not supported");
		}
	}

	@Override
	public int getRadix() {
		return gen.getRadix();
	}
	
	@Override
	public int getDiameter() {
		return 2;
	}

	@Override
	public AbstractAxisStructure getInstance(short s) {
		return new McKayMillerSiranBasedStructure(s);
	}
	
	

}
