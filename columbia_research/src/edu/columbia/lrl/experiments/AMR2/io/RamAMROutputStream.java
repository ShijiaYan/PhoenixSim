package edu.columbia.lrl.experiments.AMR2.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class RamAMROutputStream extends AbstractAMROutputStreamManager {
	
	private ByteArrayOutputStream[][] byteStreams;

	public RamAMROutputStream(int fileNumber, int rankNumber) throws IOException {
		super();
		byteStreams = new ByteArrayOutputStream[rankNumber][fileNumber];
		objectStreams = new ObjectOutputStream[rankNumber][fileNumber];
		for (int i = 0 ; i < rankNumber ; i++) {
			for (int j = 0 ; j < fileNumber ; j++) {
				byteStreams[i][j] = new ByteArrayOutputStream();
				objectStreams[i][j] = new ObjectOutputStream(byteStreams[i][j]);
			}
		}
	}

	@Override
	public AbstractAMRInputStreamManager getCorrespondingInputStream() {
		return new AbstractAMRInputStreamManager() {
			
			@Override
			public ObjectInputStream[] getObjectInputsStreamsForRank(int rank) {
				try {
					ObjectInputStream[] ois = new ObjectInputStream[byteStreams[0].length];
					for (int i = 0 ; i < ois.length ; i++) {
						ois[i] = new ObjectInputStream(new ByteArrayInputStream(byteStreams[rank][i].toByteArray()));
					}
					return ois;
				}
				catch(Exception e) {
					return null;
				}
			}
			
			@Override
			public int getNumberOfFiles() {
				return byteStreams[0].length;
			}
		};
	}
	
	@Override
	public void flushAndClose() throws IOException {
		for (int i = 0 ; i < byteStreams.length ; i++) {
			for (int j = 0 ; j < byteStreams[i].length ; j++) {
				byteStreams[i][j].flush();
				byteStreams[i][j].close();
			}
		}
	}	

}
