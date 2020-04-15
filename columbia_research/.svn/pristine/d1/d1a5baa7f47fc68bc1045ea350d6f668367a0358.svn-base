package edu.columbia.lrl.experiments.AMR2.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class FileAMROutputStream extends AbstractAMROutputStreamManager {
	
	File[][] files;
	FileOutputStream[][] fos;
	
	boolean exists;
	
	public FileAMROutputStream(String id, String mappingId, int fileNumber, int rankNumber) throws IOException {
		
		exists = true;

		String mappingDir = id  + File.separator+ "_" + mappingId;
		File mappingDirFile = new File(mappingDir);
		if (!mappingDirFile.exists()) {
			mappingDirFile.mkdirs();
			exists = false;
		}

		objectStreams = new ObjectOutputStream[rankNumber][fileNumber];
		fos = new FileOutputStream[rankNumber][fileNumber];
		files = new File[rankNumber][fileNumber];
		for (int i = 0 ; i < rankNumber ; i++) {
			for (int j = 0 ; j < fileNumber ; j++) {
				files[i][j] = new File(mappingDirFile.getPath() + File.separator + "rank" + i + "id" + j);
				if (!files[i][j].exists()) {
					exists = false;
					fos[i][j] = new FileOutputStream(files[i][j]);
					objectStreams[i][j] = new ObjectOutputStream(fos[i][j]);
				}
			}
		}		
	}
	
	public boolean alreadyExists() {
		return exists;
	}	


	@Override
	public AbstractAMRInputStreamManager getCorrespondingInputStream() {
		return new AbstractAMRInputStreamManager() {
			
			@Override
			public ObjectInputStream[] getObjectInputsStreamsForRank(int rank) {
				try {
					ObjectInputStream[] ois = new ObjectInputStream[files[0].length];
					for (int i = 0 ; i < files[0].length ; i++) {
						ois[i] = new ObjectInputStream(new FileInputStream(files[rank][i]));
					}
					return ois;
				}
				catch (IOException e) {
					return null;
				}
			}
			
			@Override
			public int getNumberOfFiles() {
				return files[0].length;
			}
		};
	}

	@Override
	public void flushAndClose() throws IOException {
		for (int i = 0 ; i < fos.length ; i++) {
			for (int j = 0 ; j < fos[i].length ; j++) {
				fos[i][j].flush();
				fos[i][j].close();
			}
		}
	}

}
