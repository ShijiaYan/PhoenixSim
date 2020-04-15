package ch.epfl.general_libraries.io;

import java.io.File;

public class MoreFile {
	
	
	public static File incrementFileName(String path, String extension) { 
		int test = 0;
		String fileName;
		File f = null;
		do {
			if (extension == null || extension.length() == 0) {
				fileName = path + String.format("%03d", test);
			} else {
				fileName = path + String.format("%03d", test) + "." + extension;
			}
			f = new File(fileName);
			test++;
		} while (f.exists());
		return f;
	}

}
