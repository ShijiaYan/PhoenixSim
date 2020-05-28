package ch.epfl.javancox.execdeamon.server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import ch.epfl.javancox.execdeamon.server.classlist.ClassList;
import ch.epfl.javancox.execdeamon.server.monitor.DeamonMonitor;



public class DeamonMain {

	public static String serverStatus = "running";
	public static ArrayList<ServerConnectionThread> threadList = new ArrayList<>();
	public static ArrayList<String> threadNames = new ArrayList<>();
	public static ArrayList<String> threadStartTimes = new ArrayList<>();
	public static ArrayList<String> serverStatuses = new ArrayList<>();
	public static boolean serverStopped = false;
	public static String threadNameFinal ="none";
	public static String infoFromServer;
	public static ArrayList<String> infosFromServer = new ArrayList<>();
	public static String oldInfoFromServer;
	public static boolean isServerMonitor = true;
	public static int totalTasks;
	public static int tasksToDo;
	public static int allTasks;
	public static int connectionCount = 0;
	public static int timeoutThreshold = 1000 * 60 * 60;
	static int totalTasksLocal = 0;
	static long totalThreadTimes = 0;
	public static ArrayList<Long> threadTimes = new ArrayList<>();
	public static ArrayList<String> serverNames = new ArrayList<>();
	static int threadCount = 0;
	static int totalThreadCount = 0;
	public static int classCount = 0;
	static long allThreadTimes;
	static long allThreadTotals;
	static long averageThreadTime;

	static ServerSocket ss;

	public static void main(String[] args) throws IOException {
		new ClassList();
		new DeamonMonitor();
		startServer();
		watchTimeout();
		
		if (isServerMonitor == true) {
			listenForInfo();
		}
		else {
			sendMyInfo();
			sendNumberOfCores();
		}
	}


	private static void watchTimeout() {

		TimerTask timeThread = new TimerTask() {
			@Override 
			public void run() {
				try {
					while (true) {
						int k = 0;
						if (threadNames.size() == 0) {
							Thread.sleep(100);
						}
						else {
							synchronized (threadStartTimes) {
								for (String l : threadStartTimes) {
									if (System.currentTimeMillis() - Long.valueOf(l) > timeoutThreshold) {
										if (threadNames.size() != 0) {
											threadNameFinal = threadNames.get(k);
										}

										k = 0;
									}
									else {
										k++;
									}
								}
							}
						}
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				finally {
				}
			}

		};

		Timer timer = new Timer();

		timer.scheduleAtFixedRate(timeThread, 1000, 1000);
		//timeThread.start();


	}


	public static void startServer() {
		Thread serverThread = new Thread() {
			public void run() {
				try {
					ss = new ServerSocket(22111);

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				boolean online = true;
				while (online) {
					try {
						createThread(ss.accept()); 
					} catch (IOException e) {
						online = false;
					}
				}
			}
		};
		serverThread.setName("Task executor server thread");
		serverThread.start();
	}

	public static void sendNumberOfCores() {
		Thread coresThread = new Thread() {
			public void run() {
				ServerSocket welcomeSocket = null;
				try {
					int numberOfCores;
					welcomeSocket = new ServerSocket(6696);
					boolean condition = true;
					while (condition) {
						Socket connectionSocket = welcomeSocket.accept();
						DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
						numberOfCores = (Runtime.getRuntime().availableProcessors() - threadCount)/2;
						outToClient.write(numberOfCores);
						System.out.println("Number of cores sent");
					}
					welcomeSocket.close();

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		};
	    coresThread.setName("Wait for core request thread");
		coresThread.start();
	}

	public static void sendMyInfo() {
		Thread infoSender = new Thread() {
			public void run() {
				try {
					ArrayList<String> serverInfo = new ArrayList<>();
					String status;

					while (true) {
						status ="<b>Number of threads currently running on " + InetAddress.getLocalHost().getHostName() + ": </b>" + Thread.activeCount();
						serverInfo.add("0");
						serverInfo.add(status);
						serverInfo.add(Integer.toString(totalTasksLocal));
						serverInfo.add(Integer.toString(threadCount));
						for (Long l : threadTimes) {
							totalThreadTimes = totalThreadTimes + l;
						}
						serverInfo.add(Long.toString(totalThreadTimes));
						serverInfo.add(Integer.toString(threadTimes.size()));
						serverInfo.add(Integer.toString(classCount));
						serverInfo.add(InetAddress.getLocalHost().getHostName());
						Socket sendInfoSocket = new Socket("128.59.65.38", 6696);
						ObjectOutputStream o = new ObjectOutputStream(sendInfoSocket.getOutputStream());
						o.writeObject(serverInfo);
						sendInfoSocket.close();
						Thread.sleep(1000);
						serverInfo.clear();
						totalThreadTimes = 0;
					}

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		};
		infoSender.setName("Send information about server");
		infoSender.start();
	}


	public static void listenForInfo() {
		Thread serverInfo = new Thread() {
			public void run() {
				try {
					ServerSocket welcomeSocket = new ServerSocket(6696);
					int k = 0;
					while (true) {
						Socket connectionSocket = welcomeSocket.accept();
						
						ObjectInputStream i = new ObjectInputStream(connectionSocket.getInputStream());
						Object o = i.readObject();
						ArrayList<String> info = (ArrayList<String>) o;
						
						
						if (Integer.parseInt(info.get(0)) == 0) {
							ArrayList<Integer> threadCounts = new ArrayList<>();
							infoFromServer = info.get(1);
							totalTasks = totalTasksLocal + Integer.parseInt(info.get(2));
							threadCounts.add(Integer.parseInt(info.get(3)));
							allThreadTimes = totalThreadTimes + Long.parseLong(info.get(4));
							allThreadTotals = threadTimes.size() + Integer.parseInt(info.get(5));
							if (serverNames.contains(info.get(7)) == false) {
								serverNames.add(info.get(7));
								System.out.println(serverNames);
							}
							for (Integer j : threadCounts) {
								totalThreadCount = totalThreadCount + j;
							}							
							
							
							if (allThreadTotals != 0) {
								averageThreadTime = allThreadTimes/allThreadTotals;	
							}
							if (allThreadTotals == 0) {
								averageThreadTime = 0;
							}
							if (serverStatuses.contains(infoFromServer) != true) {
								serverStatuses.add(infoFromServer);
								/*for (String e : serverStatuses) {
									if (e.equals(oldInfoFromServer)) {
										serverStatuses.remove(e);
									}
								}*/
								oldInfoFromServer = infoFromServer;			
							}
							
							if (serverStatuses.size() > 4) {
								serverStatuses.clear();
							}
							k++;

						}
						
						if (Integer.parseInt(info.get(0)) == 1) {
							tasksToDo = Integer.parseInt(info.get(1));
							allTasks = Integer.parseInt(info.get(2));
							connectionCount = Integer.parseInt(info.get(3));
							
						}
						
						if (Integer.parseInt(info.get(0)) == 2) {
							DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
							int numberOfCores = Runtime.getRuntime().availableProcessors() - threadCount;
							outToClient.write(numberOfCores);
						}
						
					}
				}catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

		};
		serverInfo.setName("Listen for info from other servers ");
		serverInfo.start();
	}

	public static void stopServer() throws IOException {
		ss.close();
	}


	public static void createThread(final Socket s) {
		ServerConnectionThread t = new ServerConnectionThread(s, new ExperimentRunner());	
		threadList.add(t);
		threadNames.add(t.getName());
		synchronized (threadStartTimes) {
			threadStartTimes.add(String.valueOf(System.currentTimeMillis()));
		}
		t.start();

	}

	public static String updateClassList() {
		Date date = new Date();
		StringBuilder sb = new StringBuilder();
		sb.append("<h1>Classes Downloaded by Server</h1><br>");
		for (int i = 0;i<RemoteClassLoader.classNames.size();i++ ) {
			sb.append("<br>");
			sb.append(date.toString() + " " + RemoteClassLoader.classNames.get(i));
		}
		sb.append("Server is currently " + serverStatus);
		return sb.toString();
	}

	public static String getStatus() throws UnknownHostException {
		StringBuilder sb = new StringBuilder();
		synchronized (threadStartTimes) {
			for (int i=0; i<threadList.size(); i++) {
				sb.append(threadNames.subList(i, i+1));
				sb.append(" was initiated ");
				sb.append(System.currentTimeMillis()-Long.valueOf(threadStartTimes.get(i)));
				sb.append(" milliseconds ago");
				sb.append("<br>");
			}
		}

		sb.append("<br>");
		sb.append("<b> Number of Classes Downloaded by Local Server: </b>" + classCount);
		sb.append("<br>");
		sb.append("<b> Number of connections currently opened: </b>" + connectionCount);
		sb.append("<br>");
		sb.append("<b> Tasks Left: </b>" + tasksToDo);
		sb.append("<br>");
		if (isServerMonitor == true) {
		sb.append("<b>Number of threads currently running on " + InetAddress.getLocalHost().getHostAddress() + ": </b>" + Thread.activeCount() + "<br>");
		}
		for (String m : serverStatuses) {
			sb.append(m);
			sb.append("<br>");
		}

		if (averageThreadTime == 0) {
			sb.append("<b> Average task execution time: </b>" + "No data yet");	
		}

		if (averageThreadTime != 0) {
			sb.append("<b> Average task execution time: </b>" + averageThreadTime);
			averageThreadTime = 0;
			allThreadTimes = 0;
			allThreadTotals = 0;
		}

		sb.append("<br>");
		if (isServerMonitor == false) {
			sb.append("**Not main server monitor**");
		}
		sb.append("<br><br><br>");
		sb.append("<h2>Manage Server</h2>");
		sb.append("Server is currently " + serverStatus);
		return sb.toString();
	}




}
