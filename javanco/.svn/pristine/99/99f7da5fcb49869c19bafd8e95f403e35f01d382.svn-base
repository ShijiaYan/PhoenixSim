package ch.epfl.javancox.execdeamon.server.classlist;

import java.io.File;
import java.net.ServerSocket;



import simple.http.connect.ConnectionFactory;
import simple.http.load.MapperEngine;
import simple.http.serve.FileContext;


public class ClassList {

	public ClassList() {


		try {
			// these lines here are to start a minimal web server on the port 8083
			File configDir = new File("./web/classlist_online").getCanonicalFile();
			File rootDir = new File(".").getCanonicalFile();

			// this context will try to locate file "Mapper.xml"
			
			// example Mapper.xml files for the remote execution service are stored in
			// javancox/
			FileContext context = new FileContext(rootDir, configDir);

			MapperEngine engine;			
			engine = new MapperEngine(context);
			ConnectionFactory.getConnection(engine).connect(new ServerSocket(8083));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}



}


