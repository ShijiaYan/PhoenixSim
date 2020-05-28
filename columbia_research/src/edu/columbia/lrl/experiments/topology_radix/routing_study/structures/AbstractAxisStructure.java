package edu.columbia.lrl.experiments.topology_radix.routing_study.structures;

import java.util.HashMap;
import java.util.Map;

import cern.colt.Arrays;
import ch.epfl.general_libraries.clazzes.ClassRepository;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.utils.MoreArrays;
import ch.epfl.general_libraries.utils.Pair;
import ch.epfl.general_libraries.utils.SimpleMap;

public abstract class AbstractAxisStructure {
	
	public static HashMap<Character, Pair<Integer[], Class<? extends AbstractAxisStructure>>> subClassRegister;
	public static HashMap<Class<? extends AbstractAxisStructure>, Character> charRegister;
	protected final static int[][] emptyA = new int[0][];
	protected final static int[] emptyA1 = new int[0];
	
	static {
		subClassRegister = new HashMap<>();
		charRegister = new HashMap<>();
		
		// enumerating the subclass will oblige to have them loaded
		ClassRepository repo = Experiment.globals.classRepo;
		if (repo == null) {
			repo = new ClassRepository(new String[]{"edu.columbia.lrl.experiments.topology"});
		}
		try {
			repo.getClasses(AbstractAxisStructure.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	
	protected short size;
	
	protected short onDimension;
	protected short[] coord;
	
	protected double[] usagesFront;
	protected double[] usagesBack;
	
	public AbstractAxisStructure(short size) {
		this.size = size;
	}
	
	public static void registerSubClass(char c, int[] imposed, Class<? extends AbstractAxisStructure> cl) {
		if (subClassRegister.get(c) != null) {
			throw new IllegalStateException("Cannot register class " + 
										    cl.getSimpleName() + 
										    " with char code " + c + 
										    ", already use by class " + 
										    subClassRegister.get(c).getSecond().getSimpleName());
		}
		Integer[] iMposed = MoreArrays.toIntegerArray(imposed);
		subClassRegister.put(c, new Pair<>(iMposed, cl));
		charRegister.put(cl, c);
	}
	
	public static Class<? extends AbstractAxisStructure> getCorrespondingAxis(char c) {
		Pair<Integer[], Class<? extends AbstractAxisStructure>> pair = subClassRegister.get(c);
		if (pair == null) {
			throw new IllegalStateException("No class registered for char " + c);
		}
		return pair.getSecond();
	}
	
	public static Integer[] getImposedSizes(Class<? extends AbstractAxisStructure> cl) {
		return getImposedSizes(getCorrespondingChar(cl));
	}
	
	public static Integer[] getImposedSizes(char c) {
		Pair<Integer[], Class<? extends AbstractAxisStructure>> pair = subClassRegister.get(c);
		if (pair == null) {
			throw new IllegalStateException("No class registered for char " + c);
		}
		return pair.getFirst();		
	}
	
	public static char getCorrespondingChar(Class<? extends AbstractAxisStructure> cl) {
		Character c = charRegister.get(cl);
		if (c == null) {
			throw new IllegalStateException("Class " + cl.getSimpleName() + "not registered");
		}
		return c;
	}
	
	/**
	 * Should return an array with two lines. On each column, the index of the link used in the first line.
	 * In the second line, 1 if the link is used in the "front" direction or -1 in the back direction 
	 * @param from
	 * @param to
	 * @return
	 */
	protected abstract int[][] getIndexesOfLinksUsed(int from, int to);
	public abstract int getNumberOfLinksInStructure();
	public abstract int[] getIndexesOfLinksConnectedTo(int nodeIndex);
	public abstract int[] getExtremitiesOfLink(int k);
	public abstract int getRadix();
	public abstract int getDiameter();
	public abstract AbstractAxisStructure getInstance(short s);
	
	public void setSize(short size) {
		this.size = size;
	}
	
	public double[] getUsagesBack() {
		return usagesBack;
	}
	
	public double[] getUsagesFront() {
		return usagesFront;
	}
	
	public void resetUsages() {
		this.usagesFront = new double[getNumberOfLinksInStructure()];
		this.usagesBack = new double[getNumberOfLinksInStructure()];		
	}
	
	public int addTraffic(float intensity, int from, int to) {
		int[][] usedLinks = getIndexesOfLinksUsed(from, to);
		for (int i = 0 ; i < usedLinks[0].length ; i++) {
			if (usedLinks[1][i] > 0) {
				usagesFront[usedLinks[0][i]] += intensity;
			} else {
				usagesBack[usedLinks[0][i]] += intensity;
			}
		}
		return usedLinks[0].length;
	}
	
	public int getNumberOfHops(int fromLoc, int toLoc) {
		try {
			return getIndexesOfLinksUsed(fromLoc, toLoc)[0].length;
		}
		catch (Exception e) {
			return 0;
		}
	}	

	public void setCoord(int dimId, short[] state) {
		onDimension = (short)dimId;
		coord = new short[state.length];
		System.arraycopy(state, 0, coord, 0, state.length);
	}
	
	public int[] getCoordOfNodeIndex(int index) {
		int[] cc = new int[coord.length + 1];
		int j = 0;
		for (int i = 0 ; i < coord.length + 1 ; i++) {
			if (i == onDimension) {
				cc[i] = index;
			} else {
				cc[i] = coord[j];
				j++;
			}
		}	
		return cc;
	}
	
	public String toString() {
		int index = 0;
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		for (int i = 0 ; i < coord.length + 1 ; i++) {
			if (i == onDimension) {
				sb.append("-");
			} else {
				sb.append(coord[index]);
				index++;
			}
			if (i < coord.length) {
				sb.append(", ");
			}
		}
		sb.append(") : ->");
		sb.append(Arrays.toString(usagesFront));
		sb.append("|<-");
		sb.append(Arrays.toString(usagesBack));
		return sb.toString();
	}



	public Map<? extends String, ? extends String> getParameters() {
		return SimpleMap.getMap();
	}





}
