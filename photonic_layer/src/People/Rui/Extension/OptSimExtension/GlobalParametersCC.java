package People.Rui.Extension.OptSimExtension;

public class GlobalParametersCC extends AbstractPropertyTree {

    String wavelength;
    String powerInWatt;
    String positionx, positiony;
    String pointsPerBit; 
    
    
    // Constructor we should use, to manipulate
    // Name
    // Wavelength
    // power
    // position
    // Finally we all let it automatically generate, but in this stage we need these things
    public GlobalParametersCC(String name){
        super(name);
        setPointsPerBit("7");
        this.rootProperty.className="ptolemy.actor.TypedCompositeActor";
        setParameters();
    }
    
    @Override
    public void setParameters(){    	
        this.rootProperty.property.add(new OptSimProperty("model_instance",
                "com.rsoftdesign.simworks.link.PsiIntParameter", "1"));
        this.rootProperty.property.add(new OptSimProperty("Director Type",
                "ptolemy.data.expr.Parameter","&quot;None&quot;"));
        this.rootProperty.property.add(new OptSimProperty("_createdBy",
                "ptolemy.kernel.attributes.VersionAttribute","OptSim-2018.03-1-build_1"));
        this.rootProperty.property.add(new OptSimProperty("_vergilSize",
                "ptolemy.actor.gui.SizeAttribute","[1609, 810]"));
        this.rootProperty.property.add(new OptSimProperty("_vergilGridSize",
                "ptolemy.kernel.util.StringAttribute","40"));
        this.rootProperty.property.add(new OptSimProperty("pdkName",
                "ptolemy.data.expr.Parameter","&quot;demofab.v1_0&quot;"));
        this.rootProperty.property.add(new OptSimProperty("foundryName",
                "ptolemy.data.expr.Parameter","&quot;DEMOFAB.SOI&quot;", 3));
        this.rootProperty.property.add(new OptSimProperty("_vergilZoomFactor",
                "ptolemy.data.expr.Parameter","2.1198134640679"));
        this.rootProperty.property.add(new OptSimProperty("_vergilCenter",
                "ptolemy.data.expr.Parameter","{440.5145250451264, 292.0389214801444}"));
    }
    
    // This is where you need to change according to different component
    public void switchWrite(OptSimProperty singleProperty) {
        switch (singleProperty.flag){
        case 0:
        	propertyString = propertyString+"<property name=\""+singleProperty.propertyName+"\" class=\""+
                    singleProperty.className+"\" value=\""+singleProperty.classValue+"\">\n";
        	propertyString = propertyString+"</property>\n";
        	break;
        case 1:
        	propertyString=propertyString+"<doc name=\""+singleProperty.propertyName+"\">Multiline CW Laser</doc>\n";
        	break;
        case 2:
        	propertyString=propertyString+"<port name=\""+singleProperty.propertyName+"\" class=\""+
                    singleProperty.className+"\">\n" ;
        	propertyString=propertyString+"<property name=\"output\"/>\n";
        	propertyString=propertyString+"<property name=\"multiport\"/>\n";
        	propertyString=propertyString+"</port>\n";
        	break;
        case 3:
        	propertyString = propertyString+"<property name=\""+singleProperty.propertyName+"\" class=\""+
                    singleProperty.className+"\" value=\""+singleProperty.classValue+"\">\n";
        	propertyString = propertyString+ "<property name=\"_vergilEncryptParam\" class=\"ptolemy.kernel.util.Attribute\">\r\n" + 
        			"</property>";
        	propertyString = propertyString+"</property>\n";
        	
        	break;	
        	
        default:
        	break;
    }
    }
    
    public void setWriteFilePropertyString(){
    	propertyString="";
    	propertyString=propertyString+"<?xml version=\"1.0\" standalone=\"no\"?>\r\n" + 
    	"<!DOCTYPE entity PUBLIC \"-//UC Berkeley//DTD MoML 1//EN\"\r\n" + 
    	"    \"http://ptolemy.eecs.berkeley.edu/xml/dtd/MoML_1.dtd\">\n";
    	propertyString = propertyString+"<class name=\""+this.rootProperty.propertyName+"\" extends=\""+
                this.rootProperty.className+"\">\n";
        for (OptSimProperty singleProperty : this.rootProperty.property) {
        	if (singleProperty.property.isEmpty()) {
				switchWrite(singleProperty);
        	}
        	else {
	        	propertyString=propertyString+"<property name=\""+singleProperty.propertyName+"\" class=\""+
	                    singleProperty.className+"\">\n";
        		for(OptSimProperty childProperty : singleProperty.property) {
    				switchWrite(childProperty);
        		}
	            propertyString=propertyString+"</property>\n";
        	}
        }
    }
    
    public void setPointsPerBit(String i) {
    	this.pointsPerBit = i;
		
	}
    
}

