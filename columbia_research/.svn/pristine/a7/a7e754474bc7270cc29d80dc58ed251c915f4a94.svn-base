package edu.columbia.lrl.experiments.AMR2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.channels.IllegalSelectorException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.epfl.general_libraries.utils.SimpleMap;
import edu.columbia.lrl.experiments.AMR2.io.AbstractAMRInputStreamManager;
import edu.columbia.lrl.experiments.AMR2.io.AbstractAMROutputStreamManager;
import edu.columbia.lrl.experiments.AMR2.io.FileAMROutputStream;
import edu.columbia.lrl.experiments.AMR2.io.RamAMROutputStream;

public class AMRTransformer {
	
	private int rankNb;
	private int[] inverseMapping;
	private List<File> files;
	private String mappingId;
	
	public AMRTransformer(int rankNb, int[] inverseMapping, List<File> files, String mappingId) {
		this.rankNb = rankNb;
		this.inverseMapping = inverseMapping;
		this.files = files;
		this.mappingId = mappingId;
	}
	
	public AbstractAMRInputStreamManager transform(AMRApplicationImp.MODE mode) {
		
		String dir = files.get(0).getParent();

		try {
			AbstractAMROutputStreamManager outManager;
			switch (mode) {
			case RAM:
				outManager = new RamAMROutputStream(files.size(), rankNb);
				break;
			case FILE:
			default:
				outManager = new FileAMROutputStream(dir, mappingId, files.size(), rankNb);
			}
			
			if (!outManager.alreadyExists()) {
				for (int i = 0 ; i < files.size() ; i++) {
					FileReader fr = new FileReader(files.get(i));
					BufferedReader br = new BufferedReader(fr);
					br.readLine();
					boolean continueRead = true;
					while (continueRead) {
						AbstractAMRXMLElement lastRead = readElement(br.readLine());
						if (lastRead == null) {
							continueRead = false;
						} else {
							int rank = inverseMapping[lastRead.at];
							outManager.addElement(i, rank, lastRead);
						}
					}
					br.close();
				}
				outManager.writeEnds();
			}
			return outManager.getCorrespondingInputStream();
		}
		catch (IOException ex) {
			return null;
		}
	}

	private AbstractAMRXMLElement readElement(String s) {
		if (s == null) return null;
		if (s.startsWith("</events")) {
			return null;
		}
		SimpleMap<String, String> sm = new SimpleMap<String, String>();
		String[] dd = s.split(" ");
		for (int i = 1 ; i < dd.length - 1 ; i++) {
			String[] att = parseAttribute(dd[i]);
			if (att != null) 
				sm.put(att[0], att[1]);
		}
		if (s.startsWith("<comm")) {
			return new AMRXMLCommElement(sm);
		} else {
			return new AMRXMLCompElement(sm);
		}
	}
	
	private static Pattern pattern = Pattern.compile("([^=]*)=\"([^\"]*)\"");	
	
	String[] parseAttribute(String s) {
		Matcher m = pattern.matcher(s);
		if (m.matches()) {
			return new String[]{m.group(1), m.group(2)};
		} else {
			throw new IllegalSelectorException();
		}
	}	
	
	
}
