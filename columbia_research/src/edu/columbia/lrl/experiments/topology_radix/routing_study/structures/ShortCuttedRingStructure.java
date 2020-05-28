package edu.columbia.lrl.experiments.topology_radix.routing_study.structures;

import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javancox.inputs.topology.PolygonGenerator;

public class ShortCuttedRingStructure extends JavancoBasedAxisStructure {
	
	static {
		AbstractAxisStructure.registerSubClass('S', emptyA1, ShortCuttedRingStructure.class);
	}
	
	private static String[] cuts = new String[]{"","","","1",
												"1,2", //4
												"1,2", //5
												"1,2", //6
												"1,2", //7
												"1,3", //8
												"1,3", //9
												"1,4", //10
												"1,4", //11
												"1,4", //12
												"1,5", // 13
												"1,4", // 14
												"1,4", // 15
												"1,6", // 16
												"1,4", // 17
												"1,4", // 18
												"1,4", // 19
												"1,8", // 20
												"1,6", // 21
												"1,6", // 22
												"1,5", // 23
												"1, 10", // 24
												"1, 7", // 25
												"1, 10", // 26
												"1, 6", // 27
												"1, 6", // 28
												"1, 8", // 29
												"1, 6", // 30
												"1, 7", // 31
												"1, 7", // 32
												"1, 6", // 33
												"1, 6", // 34
												"1, 10", // 35
												"1, 8", // 36
												"1, 8", // 37
												"1, 7", // 38
												"1, 7", // 39
												"1, 6", // 40
												"1, 9", // 41
												"1, 12", // 42
												"1, 12", // 43
												"1, 8", //44
												"1,8", //45
												"1, 10", //46
												"1, 13", // 47
												"1, 14", // 48
												"1, 9", // 49
												"1, 9", // 50
												"1, 14","1, 8","1, 8","1, 12","1, 12", // 51-55
												"1, 10","1, 16","1, 16","1, 13","1, 8", // 56-60
												"1, 11","1, 11","1, 14", // 61-63
												"1, 14"}; // 64
	


	public ShortCuttedRingStructure(short size) {
		super(size);
		PolygonGenerator polyGen;
		if (size > 64) {
			System.out.println("Warning: beyond 64, not optimized");
			polyGen = new PolygonGenerator(size, "1,15");
		} else {
			polyGen = new PolygonGenerator(size, cuts[size]);
		}
		AbstractGraphHandler agh = polyGen.generate();
		super.interpret(agh, this);
	}
	
	

	@Override
	public int getRadix() {
		return 4;
	}
	
	@Override
	public int getDiameter() {
		return -1;
	}



	@Override
	public AbstractAxisStructure getInstance(short s) {
		return new ShortCuttedRingStructure(s);
	}	

}
