package edu.columbia.lrl.experiments.apexmap_analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.regex.Matcher;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.general_libraries.results.Execution;
import ch.epfl.general_libraries.statistics.StatisticalDistribution;
import ch.epfl.general_libraries.utils.TypeParser;
import ch.epfl.javancox.results_manager.SmartDataPointCollector;
import ch.epfl.javancox.results_manager.gui.DefaultResultDisplayingGUI;

public class ApexAnalyzer implements Experiment {
	
	private int spaceLocality;
	private double timeLocality;
	private int memoryBW;
	private int clock;
	private int buswidthInByte;
	
	public static class RemoteConnection {

		private String ip;
		private String user;
		private String password;
		
		public RemoteConnection(
				@ParamName(name="Machine IP", default_="192.168.1.103") String ip,
				@ParamName(name="user", default_="sebastien") String user,
				@ParamName(name="password") String pass) {
			this.ip = ip;
			this.user = user;
			this.password = pass;
		}
	}
	
	private RemoteConnection remoteConnection;

	
	private StatisticalDistribution<Integer> accesses;
	private StatisticalDistribution<Integer> pauses;
	
	public static void main(String[] args) {
		SmartDataPointCollector col = new SmartDataPointCollector();
		new ApexAnalyzer(10, 0.2, 5, 2600, 64, null).run(col, null);
		DefaultResultDisplayingGUI.displayDefault(col, "Apex");
		
	}
	
	public ApexAnalyzer(
			@ParamName(name="Space loc", default_="8") int spaceLocality,
			@ParamName(name="Time loc", default_="0.5") double timeLocality,
			@ParamName(name="mem BW", default_="8") int memoryBW,
			@ParamName(name="Clock speed in Mhz", default_="2600") int clock,
			@ParamName(name="Bus width in bits", default_="64") int buswidth,
			RemoteConnection remoteConnection) {
		if (buswidth % 8 != 0) {
			throw new IllegalStateException("Bus width must be multiple of 8");
		}
		this.remoteConnection = remoteConnection;
		this.spaceLocality = spaceLocality;
		this.timeLocality = timeLocality;
		this.memoryBW = memoryBW;
		this.clock = clock;
		this.buswidthInByte = buswidth/8;
	}

	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {

		
		File f = downloadFile();
		processFile(f, man);
	}
	
	
	
	private void processFile(File f, AbstractResultsManager man) {
		accesses = new StatisticalDistribution<>();
		pauses = new StatisticalDistribution<>();
		
		double clockLengthPs = 1000d/clock;
		
		StatisticalDistribution accessesPs = new StatisticalDistribution<Double>();
		StatisticalDistribution accessesByte = new StatisticalDistribution<Integer>();
		FileReader fr;
		try {
			fr = new FileReader(f);
			BufferedReader br = new BufferedReader(fr);
			String line = "";
			int lastSecond = 0;
			
			while ((line = br.readLine()) != null) {
				
				Matcher m = TypeParser.twoDecimalCatchingPattern.matcher(line);
				if (m.matches()) {
					int first = Integer.parseInt(m.group(1));
					int second = Integer.parseInt(m.group(2));
					
					accesses.add(second-first);
					accessesPs.add((second-first)*clockLengthPs);
					accessesByte.add((second-first)*buswidthInByte);
					pauses.add(first-lastSecond);
					lastSecond = second;
				} else {
					System.out.println("Err " + line);
				}
			}
			
			DataPoint dp = new DataPoint();
			dp.addProperty("Time loc", timeLocality);
			dp.addProperty("Space loc", spaceLocality);
			dp.addProperty("Bandwidth", memoryBW);
			dp.addProperty("Bandwidth in Tb/s", memoryBW*0.1664);
			
			Execution e = new Execution();
			
			DataPoint glo = dp.getDerivedDataPoint();
			
			glo.addResultProperty("Total time", lastSecond);
			glo.addResultProperty("Utilization",  (lastSecond - pauses.getSum()) /(double)lastSecond);
			glo.addResultProperty("Average activity time (ns)", accessesPs.getMean());
			glo.addResultProperty("Max activity time", accessesPs.getMax());
			glo.addResultProperty("Average transfer size in byte", accessesByte.getMean());
			
			e.addDataPoint(glo);
			
			DataPoint hist = dp.getDerivedDataPoint();
		
			pauses.storeDistribution("Pauses", e, hist);
			accesses.storeDistribution("Accesses", e, hist);
			
			man.addExecution(e);
			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
	}

	private File downloadFile() {
		
		JSch jsch = new JSch();
		
		try {
			Session session = jsch.getSession(remoteConnection.user, remoteConnection.ip);
			
			java.util.Properties config = new java.util.Properties(); 
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			
			session.setPassword(remoteConnection.password);
			session.connect(1000);
			
		//	sendCommand("cd phoenixmem/PhoenixMem/apexmap-c/", session);
			
		//	sendCommand("pwd", session);

			String configApex = spaceLocality + " " + timeLocality + " " + memoryBW;
			String fileNameApex = spaceLocality + "_" + timeLocality + "_" + memoryBW;

			sendCommand__(new String[]{"cd phoenixmem/PhoenixMem/apexmap-c/","pwd","./Apex-dramsim " + configApex}, session);
			
			System.out.println("Downloading");
			
			return downloadFile("phoenixmem/PhoenixMem/apexmap-c/", "channel_trace_" + fileNameApex + ".txt", fileNameApex, session);
			
			
		} catch (JSchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
		
	}
	
	private void sendCommand(String commandm, Session session) throws JSchException, IOException {
		Channel channel = session.openChannel("exec");
		((ChannelExec)channel).setCommand(commandm);

		InputStream in=channel.getInputStream();
		
		channel.connect();	

		byte[] tmp=new byte[1024];
		while(true){
			while(in.available()>0){
				int i=in.read(tmp, 0, 1024);
				if(i<0)break;
				System.out.print(new String(tmp, 0, i));
			}
			if(channel.isClosed()){
				if(in.available()>0) continue; 
				System.out.println("exit-status: "+channel.getExitStatus());
				break;
			}
			try{Thread.sleep(1000);}catch(Exception ee){}
		}
		channel.disconnect();


	}
		
	
	private void sendCommand__(String[] commandm, Session session) throws JSchException, IOException, InterruptedException {
		Channel channel = session.openChannel("shell");
		

		
		InputStream in=channel.getInputStream();
		
		channel.connect();	
		OutputStreamWriter osw = new OutputStreamWriter(channel.getOutputStream());
		for (String s: commandm) {
			osw.write(s + "\r");
			osw.flush();
			Thread.sleep(300);
		}		
		channel.disconnect();
		
	}
	
	private File downloadFile(String pre, String rfile, String localFile, Session session) throws JSchException, IOException {
		String prefix=null;
		if(new File(rfile).isDirectory()){
			prefix=rfile+File.separator;
		}
		
		// exec 'scp -f rfile' remotely
		String command="scp -f "+ pre + rfile;
		Channel channel=session.openChannel("exec");
		((ChannelExec)channel).setCommand(command);

		// get I/O streams for remote scp
		OutputStream out=channel.getOutputStream();
		InputStream in=channel.getInputStream();

		channel.connect();

		byte[] buf=new byte[1024];

		// send '\0'
		buf[0]=0; out.write(buf, 0, 1); out.flush();
		File localF = null;

		while(true){
			int c=checkAck(in);
			if(c!='C'){
				break;
			}

			// read '0644 '
			in.read(buf, 0, 5);

			long filesize=0L;
			while(true){
				if(in.read(buf, 0, 1)<0){
					// error
					break; 
				}
				if(buf[0]==' ')break;
				filesize=filesize*10L+(long)(buf[0]-'0');
			}

			String file=null;
			for(int i=0;;i++){
				in.read(buf, i, 1);
				if(buf[i]==(byte)0x0a){
					file=new String(buf, 0, i);
					break;
				}
			}

			//System.out.println("filesize="+filesize+", file="+file);

			// send '\0'
			buf[0]=0; out.write(buf, 0, 1); out.flush();

			// read a content of lfile
			localF = new File(localFile);
			FileOutputStream fos=new FileOutputStream(localF);
			int foo;
			while(true){
				if(buf.length<filesize) foo=buf.length;
				else foo=(int)filesize;
				foo=in.read(buf, 0, foo);
				if(foo<0){
					// error 
					break;
				}
				fos.write(buf, 0, foo);
				filesize-=foo;
				if(filesize==0L) break;
			}
			fos.close();
			fos=null;

			if(checkAck(in)!=0){
				System.exit(0);
			}

			// send '\0'
			buf[0]=0; out.write(buf, 0, 1); out.flush();
		}

		session.disconnect();
		return localF;
	}

	static int checkAck(InputStream in) throws IOException{
		int b=in.read();
		// b may be 0 for success,
		//          1 for error,
		//          2 for fatal error,
		//          -1
		if(b==0) return b;
		if(b==-1) return b;

		if(b==1 || b==2){
			StringBuffer sb=new StringBuffer();
			int c;
			do {
				c=in.read();
				sb.append((char)c);
			}
			while(c!='\n');
			if(b==1){ // error
				System.out.print(sb.toString());
			}
			if(b==2){ // fatal error
				System.out.print(sb.toString());
			}
		}
		return b;
	}	
}
