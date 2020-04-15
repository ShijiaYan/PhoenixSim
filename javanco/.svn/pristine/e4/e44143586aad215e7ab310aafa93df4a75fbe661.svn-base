package ch.epfl.javancox.execdeamon.server.monitor;

import java.io.File;
import java.net.ServerSocket;


import simple.http.connect.ConnectionFactory;
import simple.http.load.MapperEngine;
import simple.http.serve.FileContext;

public class DeamonMonitor {
	
	public DeamonMonitor() {
		

		try {
			// these lines here are to start a minimal web server on the port 8081
			// check this direction - "./" should refer to javanco home
			File configDir = new File("./web/deamon_monitor_online").getCanonicalFile();
			File rootDir = new File(".").getCanonicalFile();
			
			// this context will try to locate file "Mapper.xml"
			FileContext context = new FileContext(rootDir, configDir);
			
			MapperEngine engine;			
			engine = new MapperEngine(context);
			ConnectionFactory.getConnection(engine).connect(new ServerSocket(8081));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}
	
	

}
