package edu.columbia.lrl.experiments.topology_radix.routing_study.structures;

import java.io.File;
import java.util.HashMap;

import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.base.Javanco;
import ch.epfl.javanco.imports.AdjacencyListImporter;

public class JavancoAdjacencyFileBasedStructure extends
		JavancoBasedAxisStructure {
	
	private JavancoAdjacencyFileBasedStructure singleton = null;
	
	private static HashMap<String, JavancoAdjacencyFileBasedStructure> map = new
			HashMap<String, JavancoAdjacencyFileBasedStructure>();
	
	private void initSingleton(String path) {
		singleton = map.get(dir+"/"+file);
		if (singleton == null) {
			AbstractGraphHandler agh = Javanco.getDefaultGraphHandler(false);
			AdjacencyListImporter importer = new AdjacencyListImporter();
			try {
				singleton = new JavancoAdjacencyFileBasedStructure();				
				agh.newLayer("physical");
				importer.importTopology(agh, new File(path));
				interpret(agh, singleton);
				singleton.size = (short)agh.getNumberOfNodes();
				map.put(dir+"/"+file, singleton);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	private String dir;
	private String file;

	// for singleton
	private JavancoAdjacencyFileBasedStructure() {
		super((short)-1);
	}
	
	public JavancoAdjacencyFileBasedStructure(String dir, String file) {
		super((short)-1); //  size unknown yet
		this.dir = dir;
		this.file = file;
		if (file == null)
			throw new NullPointerException();
	}
	
	@Override
	public AbstractAxisStructure getInstance(short s) {
		throw new IllegalStateException("Can't be used like this");
	}	
	
	@Override
	protected int[][] getIndexesOfLinksUsed(int from, int to) {
		if (singleton == null) initSingleton(dir + file);
		return singleton.linksUsed[from][to];
	}

	@Override
	public int getNumberOfLinksInStructure() {
		if (singleton == null) initSingleton(dir + file);
		return singleton.nbLinks;
	}

	@Override
	public int[] getIndexesOfLinksConnectedTo(int nodeIndex) {
		if (singleton == null) initSingleton(dir + file);
		return singleton.linksConnectedTo[nodeIndex];
	}	

	@Override
	public int getRadix() {
		if (singleton == null) initSingleton(dir + file);
		int max = 0;
		for (int i = 0 ; i < singleton.linksConnectedTo.length ; i++) {
			max = Math.max(max, singleton.linksConnectedTo[i].length);
		}
		return max;
	}
	
	@Override
	public int[] getExtremitiesOfLink(int k) {
		return singleton.reverse[k];
	}
	
	@Override
	public int getDiameter() {
		return -1;
	}	

}
