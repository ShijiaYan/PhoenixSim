//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ch.epfl.javanco.base;

import java.io.File;

import ch.epfl.general_libraries.logging.Logger;
import ch.epfl.javanco.io.JavancoClassesLoader;
import ch.epfl.javanco.io.JavancoFile;


public class Javanco {

    public static final String DEFAULT_PROPERTIES_LOCATION = "div/javanco.properties.xml";
    public static final String JAVANCO_HOME_ABS_PROPERTY = "JAVANCO_HOME_ABS";
    public static final String JAVANCO_HOME_PROPERTY = "JAVANCO_HOME";
    public static final String JAVANCO_LAUNCH_DIR_PROPERTY = "JAVANCO_LAUNCH_DIR_PROPERTY";
    public static final String JAVANCO_DIR_IMAGES_PROPERTY = "ch.epfl.javanco.imageDir";
    public static final String JAVANCO_DEFAULT_OUTPUT_DIR_PROPERTY = "ch.epfl.javanco.defaultOutputDir";
    public static final String JAVANCO_DEFAULT_CLASSPATH_PREFIXES_PROPERTY = "ch.epfl.javanco.defaultClassPathPrefixes";
    public static final String JAVANCO_GRAPHICS_DEFAULT_BACKGROUND_COLOR_PROPERTY = "ch.epfl.javanco.graphics" +
			".defaultBackgroundColor";
    public static final String JAVANCO_NETWORK_DATA_STRUCTURE_PROPERTY = "ch.epfl.javanco.graphRepresentation";
    public static final String JAVANCO_GROOVY_SCRIPTS_DIR_PROPERTY = "ch.epfl.javanco.groovyScriptsDir";
    public static final String JAVANCO_DEFAULT_NETWORK_PAINTER_PROPERTY = "ch.epfl.javanco.graphics" +
			".defaultPainterClass";
    public static final String JAVANCO_DEFAULT_XML_GRAPH_DIR_PROPERTY = "ch.epfl.javanco.xmlFilesDir";
    public static final IllegalStateException DIRECTED_EXCEPTION = new IllegalStateException(
            "Operation not supported on directed graphs");
    public static final String JAVANCO_GRAPHICS_MAX_LINK_WIDTH_PROPERTY = "ch.epfl.javanco.graphics.maxLinkWidth";
    public static boolean VERBOSE = false;
    private static GraphHandlerFactory defFactory = null;
    private static boolean initialised = false;

    public Javanco() {
    }

    public static boolean isInJar() {
        String protocol = Javanco.class.getResource("").getProtocol();
        return protocol.equals("jar");
    }

    public static void initJavanco(String propertiesFile) throws Exception {
        initJavanco(null, propertiesFile, null, false, null);
    }

    public static void initJavanco() throws Exception {
        initJavanco(null, DEFAULT_PROPERTIES_LOCATION, null, true, null);
    }

    public static void initJavanco(String propertiesFile, String logFile, boolean relativeLogFile) throws Exception {
        if (propertiesFile == null) {
            propertiesFile = DEFAULT_PROPERTIES_LOCATION;
        }

        initJavanco(null, propertiesFile, logFile, relativeLogFile, null);
    }

    public static void initJavancoUnsafe() {
        try {
            initJavanco();
        } catch (Exception var1) {
            System.out.println("Exiting due to exception " + var1);
            var1.printStackTrace();
            System.exit(-1);
        }

    }

    public static void initJavancoSafe() {
        try {
            initJavanco();
        } catch (Throwable var1) {
            System.out.println("Javanco not fully initialised due to " + var1);
        }

    }

    public static void initJavanco(String javancoHome, String propertiesFile, String logFile, boolean logRelativeFile,
                                    String outputDir) throws Exception {
        if (!initialised) {
            javancoHome = setJavancoHome(javancoHome);
            initialised = true;
            if (propertiesFile == null) {
                JavancoFile.findAndProcessPropertiesFile(DEFAULT_PROPERTIES_LOCATION);
            } else {
                JavancoFile.findAndProcessPropertiesFile(propertiesFile);
            }

            JavancoFile.initDefaultOutputDir(outputDir);
            initLog(logFile, logRelativeFile, javancoHome);
        }

    }

    private static void initLog(String logFile, boolean logRelativeFile, String javancoHome) {
        if (logFile != null) {
            if (logRelativeFile) {
                Logger.initLogger(new File(javancoHome + "/" + logFile));
            } else {
                Logger.initLogger(new File(logFile));
            }
        } else {
            Logger.initLogger(null);
        }

    }

    public static boolean hasBeenInitialised() {
        return initialised;
    }

    public static GraphHandlerFactory getDefaultGraphHandlerFactory() {
        if (!hasBeenInitialised()) {
            initJavancoUnsafe();
        }

        if (defFactory == null) {
            JavancoClassesLoader cl = null;

            try {
                cl = new JavancoClassesLoader();
            } catch (IllegalStateException var2) {
                System.out.println("WARNING: Classloader hasn't initialized correctly:\n" + var2.toString());
            }

            defFactory = new GraphHandlerFactory(DefaultGraphHandler.class, cl);
        }

        return defFactory;
    }

    public static AbstractGraphHandler getDefaultGraphHandler(boolean notify) {
        if (!hasBeenInitialised()) initJavancoUnsafe();

        return getDefaultGraphHandlerFactory().getNewGraphHandler(notify);
    }

    public static AbstractGraphHandler getGraphHandler(Class<? extends AbstractGraphHandler> cl) {
        if (!hasBeenInitialised()) {
            initJavancoUnsafe();
        }

        return getDefaultGraphHandlerFactory().getNewGraphHandler(cl, true);
    }

    public static void registerAgh(AbstractGraphHandler agh) {
        getDefaultGraphHandlerFactory().registerAgh(agh);
    }

    private static String setJavancoHome(String javancoHome) {
        if (javancoHome == null) {
            System.out.println("Searching for JAVANCO_HOME...\r\n");
            System.out.println("in java.lang.System properties...");
            javancoHome = System.getProperty(JAVANCO_HOME_PROPERTY);
            if (javancoHome == null) {
                System.out.println("... in environment variables...");
                javancoHome = System.getenv(JAVANCO_HOME_PROPERTY);
                if (javancoHome == null) {
                    System.out.println("JAVANCO_HOME is undefined.");
                    System.out.println("To define it, launch java virtual machine with argument: ");
                    System.out.println("-JAVANCO_HOME=<a directory location (for instance \"Desktop/Javanco/\")>");
                    javancoHome = System.getProperty("user.dir") + (isInJar() ? "/PhoenixSim.jar" : "");
                    System.out.println(
                            "Running in: " + (isInJar() ? "Jar..." : "IDE...\nAutomatically Setting JAVANCO_HOME..."));
                    System.out.println("JAVANCO_HOME set as:\r\n" + javancoHome);
                } else {
                    System.out.println("--> JAVANCO_HOME found in ENVIRONMENT VARIABLES, and is \r\n");
                }
            } else {
                System.out.println("--> JAVANCO_HOME found as a java.lang.System property, and is \r\n");
            }
        }

        if (!javancoHome.endsWith(File.separator)) {
            javancoHome = javancoHome + File.separator;
        }

        JavancoFile homeDir = new JavancoFile(javancoHome);

        String homeDirCano;
        try {
            homeDirCano = homeDir.getCanonicalPath();
        } catch (Exception var4) {
            homeDirCano = javancoHome;
        }

        System.out.println("-->  " + homeDirCano + "\r\n");
        System.setProperty(JAVANCO_HOME_ABS_PROPERTY, homeDirCano);
        System.setProperty(JAVANCO_HOME_PROPERTY, javancoHome);
        return homeDirCano;
    }

    public static AbstractGraphHandler loadGraph(String string) {
        if (!string.endsWith(".xml")) {
            string = string + ".xml";
        }

        AbstractGraphHandler agh = getDefaultGraphHandler(true);
        agh.openNetworkFile(string);
        return agh;
    }

    public static String getProperty(String name) {
        String prop = System.getProperty(name);
        if (prop == null) {
            throw new IllegalStateException("No property " + name + " defined in javanco.properties.xml");
        } else {
            return prop;
        }
    }
}
