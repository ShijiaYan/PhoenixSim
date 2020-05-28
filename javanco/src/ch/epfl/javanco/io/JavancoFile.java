//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ch.epfl.javanco.io;

import ch.epfl.general_libraries.logging.Logger;
import ch.epfl.javanco.base.Javanco;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.security.AccessControlException;
import java.util.Enumeration;
import java.util.Properties;


public class JavancoFile extends File {

	private static final long serialVersionUID = 1L;
	private static Logger logger;
	private static String formerUserDir;

	static {
		initLogger();
		String s = Javanco.isInJar() ? "/PhoenixSim.jar" : "";
		System.setProperty(Javanco.JAVANCO_LAUNCH_DIR_PROPERTY, System.getProperty("user.dir") + s);
		formerUserDir = null;
	}

	private static void initLogger() {
		logger = new Logger(JavancoFile.class);
	}

	public JavancoFile(String pathRelativeToJavancoHome) {
		super(pathRelativeToJavancoHome);
	}

	public JavancoFile(URI uri) {
		super(uri);
	}

	public String getAbsolutePath() {
		if (!this.isAbsolute()) {
			return Javanco.getProperty(Javanco.JAVANCO_HOME_ABS_PROPERTY) + File.separator + super.getPath();
		} else {
			return super.getAbsolutePath();
		}
	}

	public JavancoFile[] listFiles() {
		File[] f = super.listFiles();
		assert f != null;
		JavancoFile[] tab = new JavancoFile[f.length];

		for (int i = 0; i < f.length; ++i) {
			tab[i] = new JavancoFile(f[i].getAbsolutePath());
		}

		return tab;
	}

	public static void initDefaultOutputDir(String s) {
		if (s == null) {
			if (Javanco.getProperty(Javanco.JAVANCO_DEFAULT_OUTPUT_DIR_PROPERTY) == null) {
				try {
					System.setProperty(Javanco.JAVANCO_DEFAULT_OUTPUT_DIR_PROPERTY, System.getProperty("user.dir"));
				} catch (AccessControlException var2) {
					logger.error("Exception in default output dir init " + var2);
				}
			}
		} else {
			System.setProperty(Javanco.JAVANCO_DEFAULT_OUTPUT_DIR_PROPERTY, s);
		}

		initLogger();
		logger.info("Output dir set to : ");
		logger.info("-->   " + Javanco.getProperty(Javanco.JAVANCO_DEFAULT_OUTPUT_DIR_PROPERTY));
	}

	private static void changeUserDir(boolean isFromJavancoHome) {
		initLogger();

		try {
			if (isFromJavancoHome) {
				formerUserDir = System.getProperty("user.dir");
				String s = System.getProperty(Javanco.JAVANCO_HOME_ABS_PROPERTY);
				logger.info("user.dir   set to : " + s);
				System.setProperty("user.dir", s);
			} else {
				logger.info("user.dir   RESTORED to : " + formerUserDir);
				System.setProperty("user.dir", formerUserDir);
			}
		} catch (AccessControlException ace) {
			ace.printStackTrace();
		} catch (Exception e) {
			logger.error("Exception in user dir changing " + e);
		}

	}

	public static boolean graphExists(String string) {
		if (!string.endsWith(".xml")) { string = string + ".xml"; }

		return findFile(string, getDefaultMNDfilesDir()).exists();
	}

	public static JavancoFile findFile(String fileName, String dirName) {
		logger.debug("Searching for file " + fileName + " in directory " + dirName);
		return new JavancoFile(dirName + File.separator + fileName);
	}

	public static JavancoFile findFile(String pathRelativeToJavancoHome) throws Exception {
		logger.debug("Searching for file " + pathRelativeToJavancoHome + " in JAVANCO_HOME");
		return new JavancoFile(findResource(pathRelativeToJavancoHome).toURI());
	}

	public static URL findResource(String pathRelativeToJavancoHome) throws Exception {
		return findResource(pathRelativeToJavancoHome, true);
	}

	public static URL findResource(String path, boolean javancoHomeRelativeOrAbsolute) throws Exception {
		initLogger();
		if (!Javanco.hasBeenInitialised()) { Javanco.initJavanco(); }

		if (javancoHomeRelativeOrAbsolute) {
			URL url = ClassLoader.getSystemResource(path);
			if (url != null) {
				logger.debug("Found file (using ClassLoader) : -->\t" + url);
				return url;
			} else {
				String javancoHome = Javanco.getProperty(Javanco.JAVANCO_HOME_PROPERTY);
				JavancoFile currentDir = new JavancoFile(javancoHome);
				JavancoFile resourceFile = new JavancoFile(currentDir.getPath() + File.separator + path);
				if (resourceFile.exists()) {
					url = resourceFile.toURI().toURL();
					logger.debug("Found file -->\t" + url);
					return url;
				} else {
					if (currentDir.getParentFile() != null) {
						synchronized (System.out) {
							logger.warn("\r\nWARNING: Unable to find file, which is supposed to be : ");
							logger.warn("\r\n-->\t" + resourceFile.getAbsolutePath());
						}

						new JavancoFile(currentDir.getParent());
					}

					throw new IllegalStateException("Cannot find resource " + path);
				}
			}
		} else {
			JavancoFile resourceFile = new JavancoFile(path);
			if (resourceFile.exists()) {
				return resourceFile.toURI().toURL();
			} else {
				throw new IllegalStateException("Cannot find resource " + path);
			}
		}
	}

	public static void findAndProcessPropertiesFile(String path) throws Exception {
		File f = new File(path);
		if (f.exists()) processPropertiesFile(f.toURI().toURL());
		else processPropertiesFile(findResource(path));

	}

	public static void processPropertiesFile(URL url) throws Exception {
		initLogger();
		changeUserDir(true);
		Properties prop = new Properties();
		logger.info("Processing properties file : ");
		logger.info("-->\t" + url);
		prop.loadFromXML(url.openStream());
		Enumeration e = prop.propertyNames();

		while (e.hasMoreElements()) {
			String name = (String) e.nextElement();
			String property = prop.getProperty(name);
			URL url2 = null;
			logger.debug("Processing property " + name + " = " + property);
			JavancoFile dir;
			if (name.contains("toCreate")) {
				dir = new JavancoFile(property + "/");
				logger.debug("toCreate: Creating dirs toward " + dir);
				dir.mkdirs();
				name = name.replace(".toCreate", "");
			}

			if (name.contains("toURL")) {
				url2 = findResource(property);
				logger.debug("toURL: corresponding resource is " + url2);
				if (url2 != null) { property = url2.toString(); }

				name = name.replace(".toURL", "");
			}

			if (name.endsWith("Dir")) {
				try {
					if (url2 != null) {
						dir = new JavancoFile(url2.toURI());
					} else if (property.equals(".")) {
						dir = new JavancoFile("");
					} else {
						dir = new JavancoFile(property);
					}

					if (!dir.exists()) { dir.getAbsoluteFile().mkdirs(); }

					logger.debug("Dir: creating dirs toward : " + dir);
					property = dir.getAbsolutePath();
				} catch (AccessControlException var8) {
					property = getDefaultOutputDir() + property;
					logger.warn("Cannot create " + name + " for security reasons.");
					logger.warn("Set instead at " + property);
				}
			}

			logger.debug("Property " + name + " set to : " + property);
			System.setProperty(name, property);
		}

		changeUserDir(false);
	}

	public static String getDefaultMNDfilesDir() {
		logger.debug("Trying to find MND directory");
		String[] locations = new String[] { "ch.epfl.javanco.xmlFilesDir", "ch.epfl.javanco.startDir", "user" +
				".dir" };
		return internalGetDir(locations);
	}

	public static String getDefaultGroovyScriptDir() {
		logger.debug("Trying to find groovy scripts directory");
		String[] locations = new String[] { "ch.epfl.javanco.groovyScriptsDir", "ch.epfl.javanco.startDir",
				"user.dir" };
		return internalGetDir(locations);
	}

	public static String getDefaultOutputDir() {
		logger.debug("Trying to find output directory");
		String[] locations = new String[] { "ch.epfl.javanco.defaultOutputDir", "ch.epfl.javanco.startDir",
				"user.dir" };
		return internalGetDir(locations);
	}

	private static String internalGetDir(String[] locations) {
		String ret;
		for (String location : locations) {
			ret = location;
			logger.debug("Looking for property " + ret);
			String currentDir = System.getProperty(ret);
			if (currentDir != null) {
				logger.debug("Corresponding dir is : " + currentDir);
				JavancoFile dirr = new JavancoFile(currentDir);
				String ret1;
				if (dirr.isAbsolute()) {
					ret1 = dirr.getAbsolutePath() + "/";
					logger.debug("--> Final dir " + ret1);
					return ret1;
				}

				logger.debug("Directory isn't absolute, turning it into absolute one");
				dirr = new JavancoFile(System.getProperty(Javanco.JAVANCO_HOME_PROPERTY) + currentDir);
				if (dirr.exists()) {
					ret1 = dirr.getAbsolutePath() + "/";
					logger.debug("--> Final directory : " + ret1);
					return ret1;
				}

				logger.debug("No absolute dir at given address : " + dirr);
			} else {
				logger.debug("No corresponding dir");
			}
		}

		ret = new JavancoFile("").getAbsolutePath() + "/";
		logger.debug("No dir found, returning HOME : " + ret);
		return ret;
	}
}
