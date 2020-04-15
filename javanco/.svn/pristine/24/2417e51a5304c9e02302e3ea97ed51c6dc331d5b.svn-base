package ch.epfl.javancox.execdeamon.server;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class AbstractObjectAttender {
	
	ClassLoader special;
	
	public abstract Object attendObject(Object o);
	
	public void setClassLoader(ClassLoader loader) {
		special = loader;
	}
	
	protected Object getObjectFromClass(String className, Object ... params) {
		try {
			Class<?> c = special.loadClass(className);
			if (params.length == 0) {
				return c.newInstance();
			} else {
				return c.getConstructor(getTypes(params)).newInstance(params);
			}
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();			
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	protected Object runMethodOnObject(Object o, String methodName, Object[] params) {
		try {
			for (Method m : o.getClass().getMethods()) {
				if (m.getName().equals(methodName)) {
					return m.invoke(o, params);
				}
			}
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();		
		}
		return null;
	}
	
	private Class[] getTypes(Object[] params) {
		Class[] clazz = new Class[params.length];
		for (int i = 0 ; i < clazz.length ; i++) {
			clazz[i] = params[i].getClass();
		}
		return clazz;
	}
	
	protected void runStaticMethodOnClass(String className, String methodName, Object[] params) {
		try {
			Class<?> c = special.loadClass(className);
			Method m = c.getMethod(methodName, getTypes(params));
			m.invoke(null, params);
		}
		catch (ClassNotFoundException e) {
			
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
