package People.Rui.Extension.OptSimExtension;

import java.util.ArrayList;

public class OptSimProperty {
    public String propertyName;
    public String className;
    public String classValue;
    int flag;
    // Node array
    public ArrayList<OptSimProperty> property = new ArrayList<>();

    // Constructor for property type label, flag =0;
    public OptSimProperty(String name, String className, String value){
        this.propertyName = name;
        this.className = className;
        this.classValue = value;
        this.flag = 0;
    }

    // Constructor for doc type label, flag = 1;
    public OptSimProperty(String name){
        this.propertyName = name;
        this.classValue=null;
        this.className=null;
        this.flag = 1;
    }

    // Constructor for port type label, flag =2;
    public OptSimProperty(String name, String className){
        this.propertyName = name;
        this.className = className;
        this.classValue=null;
        this.flag = 2;
    }
    
    
    // Constructor for special property type label, set flag;
    public OptSimProperty(String name, String className, int flag){
        this.propertyName = name;
        this.className = className;
        this.classValue=null;
        this.flag = flag;
    }
    
    public OptSimProperty(String name, String className, String value, int flag){
        this.propertyName = name;
        this.className = className;
        this.classValue = value;
        this.flag = flag;
    }
    
    public String getName() {
    	return propertyName;
    }
    
    public String getClassName() {
    	return className;
    }
    
    public String getValue() {
    	return classValue;
    }
    
    
    public void setName(String name) {
    	propertyName=name;
    }
    
    
    public void setClassName(String newClassName) {
    	 className=newClassName;
    }
    
    
    public void setValue(String newValue) {
   	 classValue=newValue;
   }
   
    
    
    
    
    
    
}
