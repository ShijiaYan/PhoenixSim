package edu.columbia.lrl.experiments.topology_radix.routing_study.structures;


public class PetersenStructure extends AbstractAxisStructure {
	
	static {
		AbstractAxisStructure.registerSubClass('P', new int[]{10}, PetersenStructure.class);
	}
	
	private final static int[][] connectivity = {{0, 4 ,5},   // 0
		                                         {0, 1, 6},   // 1
		                                         {1, 2, 7},   // 2
		                                         {2, 3, 8},   // 3
		                                         {3, 4, 9},   // 4
		                                         {5, 10, 13},   // 5
		                                         {6, 11, 14},   // 6
		                                         {7, 10, 12},   // 7
		                                         {8, 11, 13},   // 8
		                                         {9, 12, 14},   // 9
		                                         };
	
	
	private final static int[][] reverse = {{0, 1},   // 0
        {1,2},   // 1
        {2,3},   // 2
        {3,4},   // 3
        {0,4},   // 4
        {0,5},   // 5
        {1,6},   // 6
        {2,7},   // 7
        {3,8},   // 8
        {4,9},   // 9
        {5,7},   // 10
        {6,8},   // 11
        {7,9},   // 12
        {5,8},   // 13
        {6,9},   // 14       
       
        };	
	
	private final static int[][][] routing = {
		{ {}    , {0}   , {0,1} , {3,4}  , {4}   , {5}    , {0,6}  , {5,10} , {5,13} , {4,9}  },  // 0
		{ {0}   , {}    , {1}   , {1,2}  , {0,4} , {0,5}  , {6}    , {1,7}  , {6,11} , {6,14} },  // 1
		{ {0,1} , {1}   , {}    , {2}    , {2,3} , {7,10} , {1,6}  , {7}    , {2,8}  , {7,12} },
		{ {3,4} , {1,2} , {2}   , {}     , {3}   , {8,13} , {8,11} , {2,7}  , {8}    , {3,9}  },  // 3
		{ {4}   , {0,4} , {2,3} , {3}    , {}    , {4,5}  , {9,14} , {9,12} , {3,8}  , {9}    },
		{ {5}   , {0,5} , {7,10}, {8,13} , {4,5} , {}     , {11,13}, {10}   , {13}   , {10,12}},  // 5
		{ {0,6} , {6}   , {1,6} , {8,11} , {9,14}, {11,13}, {}     , {12,14}, {11}   , {14}   },  // 6
		{ {5,10}, {1,7} , {7}   , {2,7}  , {9,12}, {10}   , {12,14}, {}     , {10,13}, {12}   },  // 7
		{ {5,13}, {6,11}, {2,8} , {8}    , {3,8} , {13}   , {11}   , {10,13}, {}     , {11,14}},  // 8
		{ {4,9} , {6,14}, {7,12}, {3,9}  , {9}   , {10,12}, {14}   , {12}   , {11,14}, {}     }   // 9
	};
	
	private final static int[][][] dirs = {
		{ {}    , {1}   , {1,1} , {-1,-1}, {-1}  , {1}    , {1,1}  , {1,1} ,  {1,-1} , {-1,1}  },  // 0
		{ {-1}  , {}    , {1}   , {1,1}  , {-1,-1},{-1,1} , {1}    , {1,1}  , {1,1}  , {1,-1} },  // 1
		{ {-1,-1} , {-1}, {}    , {1}    , {1,1} , {1,-1} , {-1,1}  , {1}    , {1,1}  , {1,1} },
		{ {1,1} , {-1,-1} , {-1}   , {}     , {1}   , {1,1} , {1,-1} , {-1,1}  , {1}    , {1,1}  },  // 3
		{ {1}   , {1,1} , {-1,-1} , {-1}    , {}    , {1,1}  , {1,1} , {1,-1} , {-1,1}  , {1}    },
		{ {-1}   , {1,-1} , {-1,1}, {-1,-1} , {-1,-1} , {}     , {-1,-1}, {1}   , {-1}   , {-1,-1}},  // 5
		{ {-1,-1} , {-1}   , {1,-1} , {-1,1} , {-1,-1}, {1,1}, {}     , {-1,-1}, {1}   , {-1}   },  // 6
		{ {-1,-1}, {-1,-1} , {-1}   , {1,-1}  , {-1,1}, {-1}   , {1,1}, {}     , {-1,-1}, {1}   },  // 7
		{ {-1,1}, {-1,-1}, {-1,-1} , {-1}    , {1,-1} , {1}   , {-1}   , {1,1}, {}     , {-1,-1}},  // 8
		{ {1,-1} , {-1,1}, {-1,-1}, {-1,-1}  , {-1}   , {-1,-1}, {1}   , {-1}   , {1,1}, {}     }   // 9
	};	
	
	public PetersenStructure() {
		super((short)10);
	}
	
	public PetersenStructure(short i) {
		super(i);
		if (i != 10) throw new IllegalStateException("Petersen must have dimension 10");
	}

	@Override
	public int[][] getIndexesOfLinksUsed(int from, int to) {
		return  new int[][]{routing[from][to], dirs[from][to]};
	}

	@Override
	public int getNumberOfLinksInStructure() {
		return 15;
	}

	@Override
	public int[] getIndexesOfLinksConnectedTo(int nodeIndex) {
		// TODO Auto-generated method stub
		return connectivity[nodeIndex];
	}

	@Override
	public int getRadix() {
		return 3;
	}

	@Override
	public int[] getExtremitiesOfLink(int k) {
		return reverse[k];
	}
	
	@Override
	public int getDiameter() {
		return 2;
	}

	@Override
	public AbstractAxisStructure getInstance(short s) {
		if (s != 10) throw new IllegalStateException();
		return new PetersenStructure();
	}	

}
