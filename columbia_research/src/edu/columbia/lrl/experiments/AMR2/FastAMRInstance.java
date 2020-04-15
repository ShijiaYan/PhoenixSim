package edu.columbia.lrl.experiments.AMR2;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;

import ch.epfl.general_libraries.simulation.Time;
import edu.columbia.lrl.LWSim.application.ActionManager;
import edu.columbia.lrl.experiments.AMR2.io.AbstractAMRInputStreamManager;

public class FastAMRInstance {

	HashSet<String> receivedCodes;
	int rank;
	int[] inverseBoxMapping;
	ActionManager c;
	RankElementSet set;
	
	AMRApplicationImp parent;
	
	FileWriter fw;
	
	public FastAMRInstance(AMRApplicationImp parent, int rank, ActionManager c, AbstractAMRInputStreamManager instream) {	
		this.set = new RankElementSet(rank, instream);
		this.rank = rank;
		this.inverseBoxMapping = parent.inverseBoxMapping;
		this.c = c;
		this.receivedCodes = new HashSet<String>();
		this.parent = parent;
		try {
			fw = new FileWriter("dump" + rank);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	int go(Time ref) throws IOException, InterruptedException, ClassNotFoundException {
		set.resetRead();
		if (rank == 14) {
		//	int gg = 0;
		}
		while (!set.isFullyRead()) {
			boolean awaiting = true;
			int fileNb = set.getFileNumber();
			for (int i = 0 ; i < fileNb ; i++) {
				if (set.isExtinguished(i)) continue;
					boolean ok = tryProcessElement(set.getLastRead(i), ref);
					if (ok) { // nextElement needs something to be executed
						set.readAtomic(i);
						awaiting = false;
					}
				}
				if (awaiting) {
					String s = (String)(c.blockingReadFromAny(ref, true).getObject());
					if (rank == 8) {
					//	int gg = 0;
					}
					if (s == null && c.isToBeTerminated()) {
						return printBlocking();
					}
					receivedCodes.add(s);
					if (s.equals("CGS2148D2159T0.0")) {
					//	int gh = 0;
					}
				}
			}
		return 0;
	}
	
	private synchronized int printBlocking() throws IOException, ClassNotFoundException {
		AbstractAMRXMLElement[] lastread = set.getLastReadsCopy();

		int rest = set.readAllUntilEnd();
		if (rest > 0) {
		//	System.out.println("Rank " + this.rank + " done but with " + rest + " events unexecuted");
			fw.write("Rank " + this.rank + " done but with " + rest + " events unexecuted");
			fw.write("\r\n");
			int fileNb = lastread.length;
			try {	
				for (int i = 0 ; i < fileNb ; i++) {	
					if (rank == 8) {
					//	int gh = 0;
					}
					AbstractAMRXMLElement el = lastread[i];
					if (el == null) continue;
		//			System.out.print("Blocked in file " + i + " : " + el.id + "  by ");
					fw.write("Blocked in file " + i + " : " + el.id + "  by ");
					int sum = 0;
					for (String s : el.deps) {
						if (receivedCodes.contains(s) == false) {
		//					System.out.print(" " + s);
							fw.write(" " + s);
							sum++;
						}
					}
					if (sum == 0) {
					//	int gh = 0;
					}
		//			System.out.println();
					fw.write("\r\n");
				}
			}
			catch (IOException e) {

			}			
			fw.flush();
			fw.close();
		}
		return rest;
	}
	
	private boolean tryProcessElement(AbstractAMRXMLElement m, Time ref) {
		if (m.id.equals("CGS8D201T0.0")) {
		//	int fuhh = 0;
		}
		
		for (String s : m.deps) {
			if (receivedCodes.contains(s) == false)
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
		if (m.at == 2599) {
			System.out.println(m.id);
		}
		c.doSomeJob(ref, m.time, m.id);
		parent.jobsPerBox[m.at]++;
		receivedCodes.add(m.id);
	//	System.out.println("Rank " + rank + " running box " + m.at + " executed task " + m.id);
	}

	private void performComm(AMRXMLCommElement m, Time ref) {
		if (m.sat) return;
		int dest = inverseBoxMapping[m.to];
		if (dest == rank) {
			receivedCodes.add(m.id);
		} else {
		//	System.out.println("Rank " + rank + " running box " + m.from + " send " + m.id + " to box " + m.to + " at node " + dest);
			c.send(ref, m.id, m.size*512, dest, m.id);
		}
	}
	
	
}
