package People.Rui.Extension.OptSimExtension;

public abstract class AbstractPropertyTree {
    public OptSimProperty rootProperty;
    public String propertyString;
    public String position;

    // Constructor 1
    AbstractPropertyTree(){
        rootProperty = null;
    }

    // Constructor 2
    AbstractPropertyTree(String name) {
        rootProperty = new OptSimProperty(name);
    }
    
    // Constructor 3
    AbstractPropertyTree(String name, String className) {
        rootProperty = new OptSimProperty(name,className);
    }


    OptSimProperty getProperty() {
        return null;
    }
    
    public void setParameters() {
    	
    }

    
    public void switchWrite(OptSimProperty singleProperty) {
    	
    }
    
    
    public void setWriteFilePropertyString(){
    
    }
    
    public void setPosition(double x, double y) {
    	String xPosition = new String(Double.toString(x));
    	String yPosition = new String(Double.toString(y));

    	position = xPosition+", "+yPosition;
    }
    
    //PrintWriter pw = new PrintWriter(new FileOutputStream(
    //        new File("persons.txt"),
    //        true /* append = true */));

    // PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, "UTF-8"));

    //try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("writePath", true)))) {
    //    out.println("the text");
    //}catch (IOException e) {
    //    System.err.println(e);
    //}


    //try (OutputStreamWriter writer =
    //             new OutputStreamWriter(new FileOutputStream(PROPERTIES_FILE), StandardCharsets.UTF_8))
    //    // do stuff
    //}

}
