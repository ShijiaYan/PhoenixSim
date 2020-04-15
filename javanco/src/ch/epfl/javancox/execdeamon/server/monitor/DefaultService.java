package ch.epfl.javancox.execdeamon.server.monitor;



import java.io.OutputStream;
/*import org.omg.CORBA.portable.InputStream;

import com.jcraft.jsch.JSch;
*/

import ch.epfl.javancox.execdeamon.server.DeamonMain;
import simple.http.Request;
import simple.http.Response;
import simple.http.load.Service;
import simple.http.serve.Context;

/*import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
 */

public class DefaultService extends Service {
public static String threadName;
String localServerStatus = "running";	

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
		String killButton = req.getParameter("kill");
		String killAllButton = req.getParameter("killall");
		String startServersButton = req.getParameter("startall");
		threadName = req.getParameter("threadnumber");
		
		
		if (stopButton != null && stopButton.equals("Pause Server") && DeamonMain.serverStopped == false) {
			System.out.println("Received command to stop the server");
			localServerStatus = "paused";
			DeamonMain.stopServer();
			DeamonMain.serverStopped = true;
		}
		if (startButton != null && startButton.equals("Unpause Server") && DeamonMain.serverStopped == true) {
			System.out.println("Received command to start the server");
			localServerStatus = "running";
			DeamonMain.startServer();
			DeamonMain.serverStopped = false;
		}
		
		if (killButton != null && killButton.equals("Kill This Server")) {
			System.exit(1);
		}
		
		if (threadName != null) {
			System.out.println("Indication to stop thread " + threadName);
			DeamonMain.threadNameFinal = threadName;
		}
		
		/*if (killAllButton != null && killAllButton.equals("Kill All Servers")) {
			for (String s : DeamonMain.serverNames) {
				stopServers(s);
			}
		}
		
		if (startServersButton != null && startServersButton.equals("Start All Servers")) {
			for (String s : DeamonMain.serverNames) {
				startProgram(s);
			}
		}*/
		
		String voidpage = context.getContent("/web/voidpage.html").toString();
		
		voidpage = voidpage.replace("#REPLACE#", DeamonMain.getStatus() + "<br><br>");
		
		OutputStream s = resp.getOutputStream();

		DeamonMain.serverStatus = localServerStatus;
		s.write(voidpage.getBytes());
		s.flush();
		s.close();
		
		
	}

	/*private void stopServers(String s) {
		String host=s;
        String user="justin";
        String password="c99justin11";
        String command1="killall java";
        try{
             
            java.util.Properties config = new java.util.Properties(); 
            config.put("StrictHostKeyChecking", "no");
            JSch jsch = new JSch();
            Session session=jsch.getSession(user, host, 22);
            session.setPassword(password);
            session.setConfig(config);
            session.connect();
            System.out.println("Connected");
             
            Channel channel=session.openChannel("exec");
            ((ChannelExec)channel).setCommand(command1);
            channel.setInputStream(null);
            ((ChannelExec)channel).setErrStream(System.err);
             
            java.io.InputStream in=channel.getInputStream();
            channel.connect();
            byte[] tmp=new byte[1024];
            while(true){
              while(in.available()>0){
                int i=in.read(tmp, 0, 1024);
                if(i<0)break;
                System.out.print(new String(tmp, 0, i));
              }
              if(channel.isClosed()){
                System.out.println("exit-status: "+channel.getExitStatus());
                break;
              }
              try{Thread.sleep(1000);}catch(Exception ee){}
            }
            channel.disconnect();
            session.disconnect();
            System.out.println("DONE");
        }catch(Exception e){
            e.printStackTrace();
        }
		
	}
	
	private void startProgram(String s) {
		String host=s;
        String user="justin";
        String password="c99justin11";
        String command1="java -cp lib/commons-io-2.4-tests.jar:lib/commons-io-2.4.jar:lib/commons-io-2.4-sources.jar:lib/commons-io-2.4-javadoc.jar:lib/common-io-2.4-test-sources.jar:lib/commons-math-1.2.jar:lib/kxml.jar:lib/simple-upload-0.3.4.jar:output/classes deamon.DeamonMain";
        try{
             
            java.util.Properties config = new java.util.Properties(); 
            config.put("StrictHostKeyChecking", "no");
            JSch jsch = new JSch();
            Session session=jsch.getSession(user, host, 22);
            session.setPassword(password);
            session.setConfig(config);
            session.connect();
            System.out.println("Connected");
             
            Channel channel=session.openChannel("exec");
            ((ChannelExec)channel).setCommand(command1);
            channel.setInputStream(null);
            ((ChannelExec)channel).setErrStream(System.err);
             
            java.io.InputStream in=channel.getInputStream();
            channel.connect();
            byte[] tmp=new byte[1024];
            while(true){
              while(in.available()>0){
                int i=in.read(tmp, 0, 1024);
                if(i<0)break;
                System.out.print(new String(tmp, 0, i));
              }
              if(channel.isClosed()){
                System.out.println("exit-status: "+channel.getExitStatus());
                break;
              }
              try{Thread.sleep(1000);}catch(Exception ee){}
            }
            channel.disconnect();
            session.disconnect();
            System.out.println("DONE");
        }catch(Exception e){
            e.printStackTrace();
        }
		
	}*/
}
