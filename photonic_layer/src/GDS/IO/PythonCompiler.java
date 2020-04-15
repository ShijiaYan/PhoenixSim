package GDS.IO;

import GDS.Layout.Cells.Cell;
import People.Meisam.GUI.Utilities.OSDetector;

import java.io.IOException;

public class PythonCompiler {

	static String fileName ;

	public PythonCompiler(String fileName){
		PythonCompiler.fileName = fileName ;
	}

	public PythonCompiler(Cell cell){
//		PythonCompiler.fileName = cell.cellName ;
		PythonCompiler.fileName = cell.cellPath ;
	}

	public static void main(String[] args){
		String command = "" ;
		if(OSDetector.isMac()){
			command = "/Library/Frameworks/Python.framework/Versions/3.5/bin/python3 " + fileName + ".py" ;
		}
		else if(OSDetector.isWindows()){
//			command = "python " + fileName + ".py" ;
			command = "python " + "\"" + fileName + ".py" + "\"" ;
		}
		try {
			Runtime.getRuntime().exec(command) ;
			System.gc();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
