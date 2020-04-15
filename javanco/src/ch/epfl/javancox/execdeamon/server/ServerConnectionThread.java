package ch.epfl.javancox.execdeamon.server;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;



import java.net.SocketException;
import java.util.Timer;
import java.util.TimerTask;

public class ServerConnectionThread extends Thread {

	Socket connectingWithTheClientForReceivingTasks;
	AbstractObjectAttender attender;
	RemoteClassLoader remoteClassLoader;
	
	public ServerConnectionThread(Socket connectinWithTheClientForReceivingTasks,
			AbstractObjectAttender attender) {
		this.connectingWithTheClientForReceivingTasks = connectinWithTheClientForReceivingTasks;
		this.attender = attender;
	}
	boolean timedout = false;
	
	private RemoteClassLoader createClassLoader(InputStream stream) throws IOException {
		ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
		int b= 0;
		while ((b = stream.read()) != 10) {
			byteArray.write(b);
		}
		String URL = new String(byteArray.toByteArray());

		return new RemoteClassLoader(URL);		
	}
	
	public void run() {
		long myStartTime = System.currentTimeMillis();
		//this.setName("Task executor thread");
		DeamonMain.threadCount = DeamonMain.threadCount + 1;
		DeamonMain.totalTasksLocal = DeamonMain.totalTasksLocal + 1;
		// name is the main (primary connection from the client)
		BufferedReader buf;
		try {
			// connection just established 
			// we are trying to read an IP address from the client
			// this IP address will be the place where to get the class from
			remoteClassLoader = createClassLoader(connectingWithTheClientForReceivingTasks.getInputStream());
			
			final ObjectInputStream ois = new ClassLoaderObjectInputStream(remoteClassLoader, connectingWithTheClientForReceivingTasks.getInputStream());
			final ObjectOutputStream oos = new ObjectOutputStream(connectingWithTheClientForReceivingTasks.getOutputStream());
			
			TimerTask checkMonitor = new TimerTask() {
				@Override 
				public void run() {
					try {
						boolean parentAlive = true;
						if (DeamonMain.threadNameFinal.equals(getName())) {
					/*	while (!DeamonMain.threadNameFinal.equals(getName())) {
							sleep(1000);
						}*/
							System.out.println(getName() + " was interrupted");
							oos.close();
							ois.close();
							closeAndKillConnection();
						}
					} 
					catch (IOException e) {
						System.out.println("Exception in Thread timeout");
					}
				}
			};
			
			Timer timer = new Timer();
			timer.schedule(checkMonitor, 1000 *60 * 60);


			//checkMonitor.setDaemon(true);
			//checkMonitor.setName(getName());
			//checkMonitor.start();
			//timeoutThread.start();
			attender.setClassLoader(remoteClassLoader);
			try {
				while (true) {
					Object of = ois.readObject();
					Object tr = attender.attendObject(of);
					if (tr == null) {
						oos.writeObject("null");
					} else {
						oos.writeObject(tr);
						oos.reset();
					}
				}
			}

			catch (SocketException e) {
				return;
			} 

			finally {

				if (timedout == false){
					oos.close();
					ois.close();
					closeAndKillConnection();
				}
				//checkMonitor.interrupt();
				timer.cancel();
				//timeoutThread.interrupt();
				DeamonMain.threadCount = DeamonMain.threadCount - 1;
				System.out.println(DeamonMain.threadCount);
				DeamonMain.threadList.remove(currentThread());
				DeamonMain.threadNames.remove(getName());
				long timeToComplete = System.currentTimeMillis() - myStartTime;
				synchronized (DeamonMain.threadStartTimes) {
					DeamonMain.threadStartTimes.remove(myStartTime);
				}
				DeamonMain.threadTimes.add(timeToComplete);
				
			}	
		} 
		catch (EOFException e) {
			System.out.println("Connection terminated by the client, thread is dying");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void closeAndKillConnection() throws IOException {
		timedout = true;
		connectingWithTheClientForReceivingTasks.close();		
	}
	

	public String getStatus() {
		return/* "Socket open? " + !connectinWithTheClientForReceivingTasks.isClosed() + "   " +*/ remoteClassLoader.getStatus();
	}




}
