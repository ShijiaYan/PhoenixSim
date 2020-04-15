package edu.columbia.lrl.experiments.topology_radix.routing_study.structures;

import java.util.Map;

import ch.epfl.general_libraries.utils.SimpleMap;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javancox.inputs.topology.DragonflyGenerator;

public class DragonflyStructure extends JavancoBasedAxisStructure {

	static {
		AbstractAxisStructure.registerSubClass('d', emptyA1, DragonflyStructure.class);
	}
	
	private int nodesPerLevel;
	private int levels;

	public DragonflyStructure(int nodesPerLevel, int levels) {
		super((short)(-1));
		this.nodesPerLevel = nodesPerLevel;
		this.levels = levels;
	}	
	
	private DragonflyStructure(int nodesPerLevel, int levels, boolean dummy) {
		super((short)(nodesPerLevel*(nodesPerLevel+1)));
		this.levels = levels;
		this.nodesPerLevel = nodesPerLevel;
		DragonflyGenerator gen = new DragonflyGenerator(nodesPerLevel, levels);
		AbstractGraphHandler agh = gen.generate();
		super.interpret(agh, this);
	}
	
	@Override
	public int getRadix() {
		if (levels == 2) {
			return nodesPerLevel;
		}
		throw new IllegalStateException("Unsupported yet");
	}

	@Override
	public int getDiameter() {
		if (levels == 2) {
			return 3;
		}
		throw new IllegalStateException("Unsupported yet");
	}
	
	public Map<? extends String, ? extends String> getParameters() {
		return SimpleMap.getMap("Dragonfly nodes at level 1", ""+nodesPerLevel, "levels", levels +"");
	}	
	
	public AbstractAxisStructure getInstance(short s) {
		int target = 0;
		int i = 2;
		while (target < s) {
			target = i*(i+1);
			i++;
		}
		return new DragonflyStructure(i-1, 2, true);
	}

}
