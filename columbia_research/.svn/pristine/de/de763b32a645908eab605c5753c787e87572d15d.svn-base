package edu.columbia.lrl.experiments.AMR2;

import java.io.IOException;
import java.io.ObjectInputStream;

import edu.columbia.lrl.experiments.AMR2.io.AbstractAMRInputStreamManager;

public class RankElementSet {
	
	private ObjectInputStream[] ois;
	
	AbstractAMRXMLElement[] lastRead;
	boolean[] extin;
	int nbExtinguished;
	int[] index;
	int rank;
	
	public RankElementSet(int rank, AbstractAMRInputStreamManager manager) {
		ois = manager.getObjectInputsStreamsForRank(rank);
		lastRead = new AbstractAMRXMLElement[manager.getNumberOfFiles()];
		extin = new boolean[manager.getNumberOfFiles()];
		index = new int[manager.getNumberOfFiles()];
		this.rank = rank;
	}
	
	public void readAtomic(int i) throws ClassNotFoundException, IOException {
		if (extin[i]) return;
		Object o = ois[i].readObject();
		index[i]++;
		if (o instanceof String) {
			extin[i] = true;
			nbExtinguished++;
			lastRead[i] = null;
		} else {
			if (rank == 0 && i == 0) {
				System.out.println(o);
			}
			lastRead[i] = (AbstractAMRXMLElement) o;
			
			// debug
			if (lastRead[i] instanceof AMRXMLCompElement) {
				if (((AMRXMLCompElement)lastRead[i]).at == 2599) {
					System.out.println("read" + lastRead[i].id);
				}
			}
		}
	}
	
	public void resetRead() throws IOException, ClassNotFoundException {
		for (int i = 0 ; i < ois.length ; i++) {
			readAtomic(i);
		}
	}
	
	public boolean isFullyRead() {
		return nbExtinguished == ois.length;
	}
	
	public boolean isExtinguished(int i) {
		return extin[i];
	}
	
	public int getIndex(int i) {
		return index[i];
	}
	
	public AbstractAMRXMLElement getLastRead(int i) {
		return lastRead[i];
	}
	
	public int getFileNumber() {
		return lastRead.length;
	}

	public int readAllUntilEnd() throws ClassNotFoundException, IOException {
		int counter = 0;
		for (int i = 0 ; i < ois.length ; i++) {
			while(extin[i] == false) {
				readAtomic(i);
				counter++;
			}
		}
		return counter;
	}

	public AbstractAMRXMLElement[] getLastReadsCopy() {
		AbstractAMRXMLElement[] copy = new AbstractAMRXMLElement[lastRead.length];
		for (int i = 0 ; i < copy.length ; i++) {
			copy[i] = lastRead[i];
		}
		return copy;
	}
}
