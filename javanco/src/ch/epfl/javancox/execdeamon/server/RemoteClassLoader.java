package ch.epfl.javancox.execdeamon.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import org.apache.commons.io.output.ByteArrayOutputStream;

public class RemoteClassLoader extends ClassLoader {

	static int tasksCount = 0; 
	static ArrayList<String> classNames = new ArrayList<>();
	
	private String host;
	
	public RemoteClassLoader(String host) {
		this.host = host;
	}
	
	@Override
	public InputStream getResourceAsStream(String name) {
		try {		
			// test
			byte[] by = getRessourceFromRemote(name, "properties");
			return new ByteArrayInputStream(by);
			
		//	return getStreamFromRemote(name, "properties");
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
	
	private byte[] getRessourceFromRemote(String name, String type) throws UnknownHostException, IOException {

		Socket connectionTowardClassServer = new Socket(host, 21210);
		connectionTowardClassServer.getOutputStream().write(type.getBytes());
		connectionTowardClassServer.getOutputStream().write('@');
		connectionTowardClassServer.getOutputStream().write(name.getBytes());
		connectionTowardClassServer.getOutputStream().write(10);
		InputStream stream = connectionTowardClassServer.getInputStream();	

		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();	
		// this means (upper line) "write into byteStream as much as you can get from the stream"
		byteStream.write(stream);	

		byteStream.close();
		stream.close();
		connectionTowardClassServer.close();
		return byteStream.toByteArray();
	}
	
	// Instead of find the "code" onto the hard, this part will ask the class server for the code
	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		String hello = name;
		if (name.contains("Localization")) {
			int i = 0;
		}
		try {
			return super.loadClass(name);
		}
		catch (ClassNotFoundException e)  {
			try {
				byte[] b = getRessourceFromRemote(name, "class");
				if (b.length == 4 && b[0] == 0 && b[1] == 0 && b[2] == 0 && b[3] == 0) {	
					throw new ClassNotFoundException("No class " + name);
				} else {
					DeamonMain.classCount++;
					Class<?> c = null;
					try {
						c = defineClassLocal(name, b);
					}
					catch (Throwable exc) {
						int index = name.lastIndexOf(".");
						String newName = name.substring(0, index);
						newName = newName + "$" + name.substring(index+1, name.length());
						c = defineClassLocal(newName, b);
					}
					if (Flags.logging)
						System.out.println("Downloaded " + c);
					tasksCount = tasksCount + 1;
					classNames.add(name);
					//System.out.println(classNames);
				//	super.resolveClass(c);
					return c;					
				}
			} catch (UnknownHostException ex) {
				throw new ClassNotFoundException("Unable to download class " + name);
			} catch (IOException ex) {
				ex.printStackTrace();
				throw new ClassNotFoundException("Unable to download class " + name);
			}
		}
	}
	
	private Class<?> defineClassLocal(String name, byte[] b) {
		return super.defineClass(name, b, 0, b.length);
	}
	
	public String getStatus() {
		return "Server has received";
	}
}
