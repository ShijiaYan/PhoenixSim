package edu.columbia.lrl.experiments.topology_radix.routing_study.structures;

import java.util.Map;

import ch.epfl.general_libraries.math.MoreMaths;
import ch.epfl.general_libraries.random.PRNStream;
import ch.epfl.general_libraries.utils.SimpleMap;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javancox.inputs.topology.LifterGenerator;
import ch.epfl.javancox.inputs.topology.NDimFlattenedButterfly;

public class LiftedFullMeshAxisStructure extends JavancoBasedAxisStructure {
	
	static {
		AbstractAxisStructure.registerSubClass('l', emptyA1, LiftedFullMeshAxisStructure.class);
	}
	
	private int meshSize;
	private int seed;

	public LiftedFullMeshAxisStructure(int meshSize, int seed) {
		super((short)(-1));
		this.meshSize = meshSize;
		this.seed = seed;
	}
	
	private LiftedFullMeshAxisStructure(short meshSize, int lifts, int seed) {
		super((short)(meshSize*lifts));
		this.meshSize = meshSize;
		LifterGenerator gen = new LifterGenerator(new NDimFlattenedButterfly(1, meshSize), lifts, 
				PRNStream.getDefaultStream(seed));
		AbstractGraphHandler agh = gen.generate();
		super.interpret(agh, this);
	}
	
	@Override
	public AbstractAxisStructure getInstance(short s) {
		int nbLifts = MoreMaths.ceilDiv(s, meshSize);
		return new LiftedFullMeshAxisStructure((short)meshSize, nbLifts, seed);
	}
	
	@Override
	public Map<? extends String, ? extends String> getParameters() {
		return SimpleMap.getMap("lifted mesh size", ""+meshSize, "seed", seed +"");
	}


	@Override
	public int getRadix() {
		return meshSize -1;
	}

	@Override
	public int getDiameter() {
		return -1;
	}

}
