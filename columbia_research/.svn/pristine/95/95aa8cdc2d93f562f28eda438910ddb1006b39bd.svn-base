package edu.columbia.lrl.experiments.AMR2;

public class AMRInstance_Ke {
	/*
	private AMRApplication2 parent;
	
	int rank;
	int fileNumber;
	ArrayList<BufferedReader> readers;
	HashSet<String> receivedCodes;
	AbElement[] lastRead;
	int extinguished;
	ArrayList<Integer> localBoxes;
	boolean[] extin;
	ActionManager c;
	static int linesRead;
	
	public AMRInstance_Ke(AMRApplication2 parent, int rank, ActionManager c) {
		this.c = c;
		this.rank = rank;
		this.parent = parent;
		fileNumber = parent.eventFiles.size();
		readers = new ArrayList<BufferedReader>(fileNumber);
		receivedCodes = new HashSet<String>();
		lastRead = new AbElement[fileNumber];
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
			extinguished += readNext(lastRead, i);
		}
		while (extinguished < fileNumber) {
			boolean awaiting = true;
			for (int i = 0 ; i < fileNumber ; i++) {
				if (extin[i]) continue;
				boolean ok = trySplitProcessElement(lastRead[i], ref);
				if (linesRead > 300000) {
					System.exit(1);
				}
				if (ok) { // nextElement needs something to be executed
					extinguished += readNext(lastRead, i);
					awaiting = false;
				}
			}
			
			// newly added: process spawned queue
			CompElement a = pendingComps.poll();
			if (a != null){
				if (trySplitProcessElement(a, ref))
					awaiting = false;
			}
			
			if (awaiting) {
				String s = (String)c.blockingReadFromAny(ref, true);
				if (s == null && c.isToBeTerminated()) {
					return;
				}
				receivedCodes.add(s);
			}
		}	
		int a = 0;
		a++;
	}
	
	private int readNext(AbElement[] lastRead, int i) throws IOException {
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
	
	private boolean tryProcessElement(AbElement m, Time ref) {
		for (String s : m.deps) {
			if (s.equals("") == false && receivedCodes.contains(s) == false)
				return false;
		}
		if (m instanceof CommElement) {
			performComm((CommElement)m, ref);
		}
		if (m instanceof CompElement) {
			performComputation((CompElement)m, ref);
		}
		return true;
	}
	
	private LinkedList<CompElement> pendingComps = new LinkedList<CompElement>();

	private boolean trySplitProcessElement(AbElement m, Time ref) {

		if (m instanceof CommElement) {
			for (String s : m.deps) {
			String subS = CompElement.getChildId(s, m.id);
			boolean containSub = receivedCodes.contains(subS);
				if (s.equals("") == false
						&& receivedCodes.contains(s) == false
						&& containSub == false)
					return false;
			}
			performComm((CommElement) m, ref);
			//receivedCodes.remove(subS);
		}
		
		if (m instanceof CompElement) {
			CompElement mm = (CompElement) m;
			if (mm.id.startsWith("SUBCP")) {
				performComputationCheckLast(mm, ref);
			} else {
				for (String s : m.deps) {
					if (s.equals("") == false && receivedCodes.contains(s) == false)
						return false;
				}
				LinkedList<CompElement> ll = mm.spawn();
				if (ll == null) {
					performComputation(mm, ref);
				} else {
					performComputationCheckLast( ll.poll(), ref);
					if (ll != null) 
						this.pendingComps.addAll(ll);
				}
			}
		}
		return true;
	}
	
	private void performComputationCheckLast(CompElement m, Time ref) {
		performComputation(m, ref);
		if (m.lastPart) {
			receivedCodes.add(m.parentId);
		}
	}
	
	private void performComputation(CompElement m, Time ref) {
		c.doSomeJob(ref, m.time, m.id);
		receivedCodes.add(m.id);
	//	System.out.println("Rank " + rank + " running box " + m.at + " executed task " + m.id);
	}

	private void performComm(CommElement m, Time ref) {
		if (m.sat.equals("")) return;
		int dest = parent.inverseBoxMapping[m.to];
		if (dest == rank) {
			receivedCodes.add(m.id);
		} else {
		//	System.out.println("Rank " + rank + " running box " + m.from + " send " + m.id + " to box " + m.to + " at node " + dest);
			c.send(ref, m.id, m.size*512, dest, m.id);
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
	
	
	private AbElement readElement(String s) {
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
			return new CommElement(sm);
		} else {
			return new CompElement(sm);
		}
	}
	
	abstract static class AbElement {
		String[] deps;
		String id;
		String sat;
		String[] sats;
		int at = -1;
		
		AbElement(SimpleMap<String, String> map) {
			for (int i = 0 ; i < map.size() ; i++) {
				String key = map.getKey(i);
				String value = map.getValue(i);
				if (key.equals("dep")) {
					deps = value.split(",");
				} else if (key.equals("id")) {
					id = value;
				} else if (key.equals("sat")) {
					sat = value;
					sats = value.split(",");
				}
			}
		}
		
		AbElement(AttributeMap map, int at) {
			deps = map.getNamedItem("dep").getFirstChild().getTextContent().split(",");
			id = map.getNamedItem("id").getFirstChild().getTextContent();
			sat = map.getNamedItem("sat").getFirstChild().getTextContent();
			sats = map.getNamedItem("sat").getFirstChild().getTextContent().split(",");
			this.at = at;
		}
		
		AbElement(){
			
		}
	}
	
	private static class CompElement extends AbElement {
		double time;
		String parentId;
		boolean lastPart = false;
		
		CompElement(SimpleMap<String, String> map) {
			super(map);
			for (int i = 0 ; i < map.size() ; i++) {
				String key = map.getKey(i);
				String value = map.getValue(i);
				if (key.equals("time")) {
					time = Double.parseDouble(value) *1e9;
				} else if (key.equals("at")) {
					at = Integer.parseInt(value.substring(1));
				}
			}
			this.parentId = id;
		}
		
		CompElement(AttributeMap map) {
			super(map, Integer.parseInt(map.getNamedItem("at").getFirstChild().getTextContent().substring(1)));
			time = Double.parseDouble(map.getNamedItem("time").getFirstChild().getTextContent());
			this.parentId = id;
		}
		
		CompElement(CompElement parent, String commS){
			this.parentId = parent.id;
			this.id = getChildId(parent.id, commS);
		}
		
		public static String getChildId(String comp, String comm){
			return "SUBCP,"+comp+","+comm;
		}
		
		public LinkedList<CompElement> spawn(){
			int nComm = 0;
			LinkedList<CompElement> ll = new LinkedList<CompElement>();
			for (String s: this.sats){
				if (s.startsWith("C")) {
					nComm++;
					ll.offer(new CompElement(this, s));
				}
			}
			if (ll.size() == 0)
				return null;
			
			double timeSplit = this.time/nComm;
			for (CompElement a: ll){
				a.time = timeSplit;
			}
			ll.getLast().lastPart = true;
			return ll;
		}
		
		public String toString() {
			return "EXE " + id;
		}		
	}
	
	private static class CommElement extends AbElement {
		int from;
		int to;
		int size;
		
		CommElement(SimpleMap<String, String> map) {
			super(map);
			for (int i = 0 ; i < map.size() ; i++) {
				String key = map.getKey(i);
				String value = map.getValue(i);
				if (key.equals("to")) {
					to = Integer.parseInt(value.substring(1));
				} else if (key.equals("size")) {
					size = Integer.parseInt(value);
				} else if (key.equals("from")) {
					from = Integer.parseInt(value.substring(1));
					at = from;
				}
			}			
		}
		
		CommElement(AttributeMap map) {
			super(map, Integer.parseInt(map.getNamedItem("from").getFirstChild().getTextContent().substring(1)));
			from = at;
			to = Integer.parseInt(map.getNamedItem("to").getFirstChild().getTextContent().substring(1));
			size = Integer.parseInt(map.getNamedItem("size").getFirstChild().getTextContent());
		}
		
		public String toString() {
			return "COM " + id;
		}
	}	
	*/
}
