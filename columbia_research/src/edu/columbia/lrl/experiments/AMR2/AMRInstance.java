package edu.columbia.lrl.experiments.AMR2;

public class AMRInstance {
	/*
	private AMRApplication2 parent;
	
	int rank;
	int fileNumber;
	ArrayList<BufferedReader> readers;
	HashSet<String> receivedCodes;
	AbstractAMRXMLElement[] lastRead;
	int extinguished;
	ArrayList<Integer> localBoxes;
	boolean[] extin;
	ActionManager c;
	static int linesRead;
	
	public AMRInstance(AMRApplication2 parent, int rank, ActionManager c) {
		this.c = c;
		this.rank = rank;
		this.parent = parent;
		fileNumber = parent.eventFiles.size();
		readers = new ArrayList<BufferedReader>(fileNumber);
		receivedCodes = new HashSet<String>();
		lastRead = new AbstractAMRXMLElement[fileNumber];
		extinguished = 0;
		localBoxes = parent.boxMapping.get(rank);
		extin = new boolean[fileNumber];
	}
	
	void go(Time ref) throws IOException, InterruptedException {
		for (int i = 0 ; i < fileNumber ; i++) {
			if (extin[i]) continue;
			FileReader fr = new FileReader(parent.eventFiles.get(i));
			BufferedReader br = new BufferedReader(fr);
			br.readLine();
			readers.add(br);
			extinguished += readNext(i);
		}
		while (extinguished < fileNumber) {
			boolean awaiting = true;
			for (int i = 0 ; i < fileNumber ; i++) {
				if (extin[i]) continue;
				boolean ok = tryProcessElement(lastRead[i], ref);
				if (linesRead > 300000) {
					System.exit(1);
				}
				if (ok) { // nextElement needs something to be executed
					extinguished += readNext(i);
					awaiting = false;
				}
			}
			if (awaiting) {
				String s = (String)c.blockingReadFromAny(ref, true);
				if (s == null && c.isToBeTerminated()) {
					return;
				}
				receivedCodes.add(s);
			}
		}		
	}
	
	private int readNext(int i) throws IOException {
		lastRead[i] = null;
		while (lastRead[i] == null && extin[i] == false) {
			lastRead[i] = readElement(readers.get(i).readLine());
			if (lastRead[i] == null) {
				extin[i] = true;
				return 1;
			}
			if (localBoxes.contains(lastRead[i].at) == false) {
				lastRead[i] = null;
			}
		}
		return 0;
	}	
	
	private boolean tryProcessElement(AbstractAMRXMLElement m, Time ref) {
		for (String s : m.deps) {
			if (s.equals("") == false && receivedCodes.contains(s) == false)
				return false;
		}
		if (m instanceof AMRXMLCommElement) {
			performComm((AMRXMLCommElement)m, ref);
		}
		if (m instanceof AMRXMLCompElement) {
			performComputation((AMRXMLCompElement)m, ref);
		}
		return true;
	}
	
	private void performComputation(AMRXMLCompElement m, Time ref) {
		c.doSomeJob(ref, m.time, m.id);
		receivedCodes.add(m.id);
	//	System.out.println("Rank " + rank + " running box " + m.at + " executed task " + m.id);
	}

	private void performComm(AMRXMLCommElement m, Time ref) {
		if (m.sat) return;
		int dest = parent.inverseBoxMapping[m.to];
		if (dest == rank) {
			receivedCodes.add(m.id);
		} else {
		//	System.out.println("Rank " + rank + " running box " + m.from + " send " + m.id + " to box " + m.to + " at node " + dest);
			c.send(ref, m.id, m.size*512, dest, m.id);
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
	}	*/
}
