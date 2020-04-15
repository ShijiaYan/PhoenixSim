package ch.epfl.javancox.execdeamon.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

public class ClassLoaderObjectInputStream extends ObjectInputStream {

	RemoteClassLoader remoteClassLoader;
	
	public ClassLoaderObjectInputStream(RemoteClassLoader ld, InputStream in) throws IOException {
		super(in);
		this.remoteClassLoader = ld;
	}
	
	@Override
	protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
		try {
			String name = desc.getName();
			Class<?> c = Class.forName(name, false, remoteClassLoader);
			return c;
		}
		catch(ClassNotFoundException e) {
			throw new IllegalStateException(e);
		//	return super.resolveClass(desc);
		}
	}
}
