package Simulations.gds;

import flanagan.io.FileChooser;
import flanagan.io.FileOutput;

public class TestIO {
	
	public static void main(String[] args){
		
		String st1 = "Hello World!!!!" ;
		String st2 = "import numpy" ;
		FileOutput fout = new FileOutput("test2.py", "w") ; // writing a new file
		fout.println(st1) ;
		fout.println(st2) ;
		fout.close();
		
		fout = new FileOutput("test2.py", "a") ; // appending to the existing file
		fout.println("My name is Meisam Bahadori haha...") ;
		String[] st3 = {"test 1", "test 2", "test 3"} ;
		fout.println(st3) ;
		fout.close();
		
		
		FileChooser fchooser = new FileChooser() ;
		fchooser.selectFile() ;
		System.out.println(fchooser.getDirPath());
		System.out.println(fchooser.getFileName());
		System.out.println(fchooser.getExtension());

/*		int n = 2 ;
		switch (n){
		case 1:
			System.out.println("hi!");
			break ;
		case 2:
			System.out.println("hello!");
			break ;
		default: 
			System.out.println("default!");
			break ;
		}*/
		
	}

}
