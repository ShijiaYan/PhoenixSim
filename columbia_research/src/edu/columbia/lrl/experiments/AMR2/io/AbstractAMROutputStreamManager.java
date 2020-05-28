package edu.columbia.lrl.experiments.AMR2.io;

import java.io.IOException;
import java.io.ObjectOutputStream;

import edu.columbia.lrl.experiments.AMR2.AbstractAMRXMLElement;

public abstract class AbstractAMROutputStreamManager {

	protected ObjectOutputStream[][] objectStreams;
	
	public abstract AbstractAMRInputStreamManager getCorrespondingInputStream();
	public abstract void flushAndClose() throws IOException;
	

	public void addElement(int fileId, int rank, AbstractAMRXMLElement element) throws IOException {
	/*	if (element.at == 2599 && element instanceof AMRXMLCompElement) {
			System.out.println("Written" + element.id);
		}*/
		
		objectStreams[rank][fileId].writeObject(element);
		
	}

	
	public void writeEnds() throws IOException {
        for (ObjectOutputStream[] objectStream : objectStreams) {
            for (int j = 0; j < objectStreams[0].length; j++) {
                objectStream[j].writeObject("END");
                objectStream[j].flush();
                objectStream[j].close();
            }
        }
		flushAndClose();
	}


	public boolean alreadyExists() {
		// TODO Auto-generated method stub
		return false;
	}
	
}
