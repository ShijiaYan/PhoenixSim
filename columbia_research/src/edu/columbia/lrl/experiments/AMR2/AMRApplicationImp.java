package edu.columbia.lrl.experiments.AMR2;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;



import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.general_libraries.simulation.Time;
import ch.epfl.general_libraries.utils.SimpleMap;
import ch.epfl.javancox.experiments.builder.object_enum.ExperimentExecutionManager;
import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.LWSim.application.AbstractApplication;
import edu.columbia.lrl.LWSim.application.ActionManager;
import edu.columbia.lrl.experiments.AMR2.io.AbstractAMRInputStreamManager;

public class AMRApplicationImp extends AbstractApplication {
	
	public static enum MODE {
		RAM,
		FILE;
	}
	
	ArrayList<ArrayList<Integer>> boxMapping = new ArrayList<ArrayList<Integer>>();	
	int[] inverseBoxMapping = new int[0];	
	ArrayList<File> eventFiles = new ArrayList<File>();
	String mappingFile;
	int highestBoxNumber;
	
	AbstractAMRInputStreamManager instream;
	String fullId;
	MODE mode;
	int id;
	String prefix;	
	
	static HashMap<String, AbstractAMRInputStreamManager> repo;
	
	// for analysis and debug
	int[] jobsPerBox;
	int[] undonePerRank;
	
	static {
		repo = new HashMap<String, AbstractAMRInputStreamManager>();
		ExperimentExecutionManager.registerAsCachedClass(AMRApplicationImp.class);
	}
	
	public static void clearCache() {
		repo.clear();
	}
	
	public AMRApplicationImp(String filePrefix, String mappingName, int id) {
		this(filePrefix, mappingName, id, MODE.RAM, false);
	}	
	
	
	public AMRApplicationImp(String filePrefix, String mappingFile, int id, MODE mode, boolean details) {	
		super(details);
		this.mode = mode;
		fullId = filePrefix + id + mappingFile + mode;		
		this.id = id;
		this.prefix = filePrefix;
		this.mappingFile = mappingFile;
		try {
			File directory = new File(filePrefix);
			File boxes = new File(filePrefix + File.separator + mappingFile);
			SAXParserFactory factory = SAXParserFactory.newInstance();		
			SAXParser parser = factory.newSAXParser();
			parser.parse(boxes, new DefaultHandler() {			
			    public void startElement (String uri, String localName,
	                    String qName, final Attributes attributes) throws SAXException {
			    	 if (qName.equals("box")) {
			    		parseBox(attributes);
			    	}
			    }		
			});	
			for (File f : directory.listFiles()) {
				if (f.getName().equals("boxes.xml") == false && f.getName().startsWith(""+id)) {
					eventFiles.add(f);
				}
			}
			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	private void parseBox(Attributes attributes) {
		String id = attributes.getValue("id");
		String loc = attributes.getValue("loc");
		int box = Integer.parseInt(id.substring(1));
		int locI = Integer.parseInt(loc);
		while(boxMapping.size() <= locI) {
			boxMapping.add(new ArrayList<Integer>());
		}
		ArrayList<Integer> locArray = boxMapping.get(locI);
		locArray.add(box);		
		if (inverseBoxMapping.length <= box) {
			int[] newTab = new int[box+1];
			System.arraycopy(inverseBoxMapping, 0, newTab, 0, inverseBoxMapping.length);
			inverseBoxMapping = newTab;
		}
		inverseBoxMapping[box] = locI;

		if (box > highestBoxNumber) {
			highestBoxNumber = box;
		}
		
		jobsPerBox = new int[inverseBoxMapping.length];
		undonePerRank = new int[boxMapping.size()];
	}	
	
	public InitFeedback  init(LWSIMExperiment exp) {
		super.init(getNumberOfClients());
		instream = repo.get(fullId);
		
		if (instream == null) {
			AMRTransformer trans = new AMRTransformer(boxMapping.size(), inverseBoxMapping, eventFiles, mappingFile);
			instream = trans.transform(mode);
			repo.put(fullId, instream);
		}		
		
		return null;
	} 
	
	@Override
	public Map<String, String> getAllParameters() {
		Map<String, String> hh = SimpleMap.getMap("AMR prefix", prefix, "Event set id", id+"", "Box mapping", mappingFile);
		hh.put("Read event mode", mode+"");
		return hh;
	}	
	
	@Override
	public void runImpl(ActionManager c, int rank, Time ref)
			throws InterruptedException {
		try {	
			FastAMRInstance instance = new FastAMRInstance(this, rank, c, instream);	
			undonePerRank[rank] = instance.go(ref);
		}
		catch (IOException e) {
			System.out.println("IOException" + e);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public int getNumberOfClients() {
		return boxMapping.size();
	}

	@Override
	public void addApplicationInfoImpl(LWSIMExperiment lwSimExp, double ref,
			boolean analyseNodes) {
		for (int i = 0 ; i < jobsPerBox.length ; i++) {
			DataPoint box = lwSimExp.getDerivedDatapoint();
			box.addProperty("Box nb", i);
			box.addResultProperty("Number of jobs", jobsPerBox[i]);
			lwSimExp.getExecutionObject().addDataPoint(box);
		}
		
		for (int i = 0 ; i < undonePerRank.length ; i++) {
			DataPoint box =  lwSimExp.getDerivedDatapoint();
			box.addProperty("rank", i);
			box.addResultProperty("Undone per rank", undonePerRank[i]);
			lwSimExp.getExecutionObject().addDataPoint(box);
		}	
		
	}	
}
