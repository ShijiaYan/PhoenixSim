package ch.epfl.javancox.execdeamon.server.classlist;

import java.io.OutputStream;

import ch.epfl.javancox.execdeamon.server.DeamonMain;
import simple.http.Request;
import simple.http.Response;
import simple.http.load.Service;
import simple.http.serve.Context;

public class DefaultService extends Service {
	String localServerStatus;
	
	public DefaultService(Context context) {
		super(context);
	}

	@Override
	protected void process(Request req, Response resp) throws Exception {
		
		//System.out.println(req);
		
	//	resp.set("Content-Type", "text/plain");
		resp.set("Content-Type", "text/html");
		resp.set("Cache-Control", "public, no-cache");
		
		String stopButton = req.getParameter("stop");
		String startButton = req.getParameter("start");
		boolean serverStopped = false;
		
		if (stopButton != null && stopButton.equals("Stop") && serverStopped == false) {
			System.out.println("Received command to stop the server");
			localServerStatus = "stopped";
			DeamonMain.stopServer();
			serverStopped = true;
		}
		if (startButton != null && startButton.equals("Start") && serverStopped == true) {
			System.out.println("Received command to start the server");
			localServerStatus = "running";
			DeamonMain.startServer();
			serverStopped = false;
		}		
		
		String voidpage = context.getContent("/web/classlist_online/voidpage.html").toString();
		
		voidpage = voidpage.replace("#REPLACE#", DeamonMain.updateClassList() + "<br><br>");
		
		OutputStream s = resp.getOutputStream();

		
		s.write(voidpage.getBytes());
		DeamonMain.serverStatus = localServerStatus;
		s.flush();
		s.close();
		
		
	}
}
