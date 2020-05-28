package People.Rui.Extension.OptSimExtension;

public class GlobalParametersDefault extends AbstractPropertyTree {

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
    public GlobalParametersDefault(String name){
        super(name);
        setPointsPerBit("7");
        this.rootProperty.className="ptolemy.actor.TypedCompositeActor";
        setParameters();
    }
    
    public GlobalParametersDefault(String name, String pointsPerBit){
        super(name);
        setPointsPerBit(pointsPerBit);
        this.rootProperty.className="ptolemy.actor.TypedCompositeActor";
        setParameters();
    }

    @Override
    public void setParameters(){
        this.rootProperty.property.add(new OptSimProperty("model_instance",
                "com.rsoftdesign.simworks.link.PsiIntParameter", "1"));
        this.rootProperty.property.add(new OptSimProperty("Director Type",
                "ptolemy.data.expr.Parameter","&quot;LinkSIM&quot;"));
        this.rootProperty.property.add(new OptSimProperty("_createdBy",
                "ptolemy.kernel.attributes.VersionAttribute","OptSim-2018.03-1-build_1"));
        this.rootProperty.property.add(new OptSimProperty("_vergilSize",
                "ptolemy.actor.gui.SizeAttribute","[1373, 813]"));
        this.rootProperty.property.add(new OptSimProperty("LinkSIM Director",
                "com.rsoftdesign.simworks.link.LinkSimDirector",3));
        //this.rootProperty.property.get(4).property.add(new OptSimProperty("Scheduler",
         //       "ptolemy.domains.sdf.kernel.SDFScheduler",3));
        this.rootProperty.property.get(4).property.add(new OptSimProperty("iterations",
                "ptolemy.data.expr.Parameter","1"));
        this.rootProperty.property.get(4).property.add(new OptSimProperty("Animation Delay",
                "ptolemy.data.expr.Parameter","0"));
        this.rootProperty.property.get(4).property.add(new OptSimProperty("Output Prefix",
                "ptolemy.data.expr.Parameter","&quot;lstmp&quot;"));
        this.rootProperty.property.get(4).property.add(new OptSimProperty("Plotting Mode",
                "ptolemy.data.expr.Parameter","&quot;Save Only&quot;"));
        this.rootProperty.property.get(4).property.add(new OptSimProperty("Save Input or Output Signal",
                "ptolemy.data.expr.Parameter","&quot;Output&quot;"));
        this.rootProperty.property.get(4).property.add(new OptSimProperty("Calculation Domain",
                "ptolemy.data.expr.Parameter","&quot;Auto&quot;"));
        this.rootProperty.property.get(4).property.add(new OptSimProperty("Criterion",
                "ptolemy.data.expr.Parameter","&quot;RMS Error&quot;"));
        this.rootProperty.property.get(4).property.add(new OptSimProperty("Maximum Number of Iterations",
                "ptolemy.data.expr.Parameter","1000"));
        this.rootProperty.property.get(4).property.add(new OptSimProperty("Simulation Mode",
                "ptolemy.data.expr.Parameter","&quot;Steady State&quot;"));
        this.rootProperty.property.get(4).property.add(new OptSimProperty("Results Generation",
                "ptolemy.data.expr.Parameter","&quot;Final&quot;"));
        this.rootProperty.property.get(4).property.add(new OptSimProperty("Results Interval",
                "ptolemy.data.expr.Parameter","1"));
        this.rootProperty.property.get(4).property.add(new OptSimProperty("Inner Variable Name",
                "ptolemy.kernel.util.StringAttribute","(No_Scan)"));
        this.rootProperty.property.get(4).property.add(new OptSimProperty("Inner Starting Value",
                "ptolemy.data.expr.Parameter","10e9"));
        this.rootProperty.property.get(4).property.add(new OptSimProperty("Inner Ending Value",
                "ptolemy.data.expr.Parameter","40e9"));
        this.rootProperty.property.get(4).property.add(new OptSimProperty("Inner increment",
                "ptolemy.data.expr.Parameter","10e9"));
        this.rootProperty.property.get(4).property.add(new OptSimProperty("Outer Variable Name",
                "ptolemy.kernel.util.StringAttribute","(No_Scan)"));
        this.rootProperty.property.get(4).property.add(new OptSimProperty("Outer Starting Value",
                "ptolemy.data.expr.Parameter","10e9"));
        this.rootProperty.property.get(4).property.add(new OptSimProperty("Outer Ending Value",
                "ptolemy.data.expr.Parameter","4e10"));
        this.rootProperty.property.get(4).property.add(new OptSimProperty("Outer increment",
                "ptolemy.data.expr.Parameter","10e9"));
        this.rootProperty.property.get(4).property.add(new OptSimProperty("Statistical Meta Prefix",
                "ptolemy.data.expr.Parameter","&quot;stat_&quot;"));
        this.rootProperty.property.get(4).property.add(new OptSimProperty("Scan Meta Prefix",
                "ptolemy.data.expr.Parameter","&quot;meta_&quot;"));
        this.rootProperty.property.get(4).property.add(new OptSimProperty("Inner Iteration Meta Prefix",
                "ptolemy.data.expr.Parameter","&quot;inner_&quot;"));
        this.rootProperty.property.get(4).property.add(new OptSimProperty("Outer Iteration Meta Prefix",
                "ptolemy.data.expr.Parameter","&quot;outer_&quot;"));
        this.rootProperty.property.get(4).property.add(new OptSimProperty("Number of Statistical Runs",
                "ptolemy.data.expr.Parameter","0"));
        this.rootProperty.property.get(4).property.add(new OptSimProperty("Time Step Per Iteration",
                "ptolemy.data.expr.Parameter","0.0"));
        this.rootProperty.property.get(4).property.add(new OptSimProperty("Maximum Crosstalk Elements",
                "ptolemy.data.expr.Parameter","4"));
        this.rootProperty.property.get(4).property.add(new OptSimProperty("Number of Moments",
                "ptolemy.data.expr.Parameter","2"));
        this.rootProperty.property.get(4).property.add(new OptSimProperty("Simulation Accuracy",
                "ptolemy.data.expr.Parameter","&quot;Standard&quot;"));
        this.rootProperty.property.get(4).property.add(new OptSimProperty("Tolerance",
                "ptolemy.data.expr.Parameter","0.001"));
        this.rootProperty.property.get(4).property.add(new OptSimProperty("Verbosity Level",
                "ptolemy.data.expr.Parameter","0"));
        this.rootProperty.property.get(4).property.add(new OptSimProperty("_location",
                "ptolemy.kernel.util.Location","70.0, 35.0"));
        this.rootProperty.property.add(new OptSimProperty("bitRate",
                "ptolemy.data.expr.Parameter","1e10"));
        this.rootProperty.property.add(new OptSimProperty("pointsPerBit",
                "ptolemy.data.expr.Parameter",pointsPerBit));
        this.rootProperty.property.add(new OptSimProperty("patternLength",
                "ptolemy.data.expr.Parameter","7"));
        this.rootProperty.property.add(new OptSimProperty("bandwidth",
                "ptolemy.data.expr.Parameter","bitRate * 2^pointsPerBit"));
        this.rootProperty.property.add(new OptSimProperty("timeStep",
                "ptolemy.data.expr.Parameter","1.0/bandwidth"));
        this.rootProperty.property.add(new OptSimProperty("noSamples",
                "ptolemy.data.expr.Parameter", "pointsPerBit + patternLength"));
        this.rootProperty.property.add(new OptSimProperty("timeSpan",
                "ptolemy.data.expr.Parameter","timeStep * 2^noSamples"));
        this.rootProperty.property.add(new OptSimProperty("_vergilGridSize",
                "ptolemy.kernel.util.StringAttribute","40"));
        //this.rootProperty.property.add(new OptSimProperty("deltaLambda",
        //        "ptolemy.data.expr.Parameter","3.2e-9"));
        //this.rootProperty.property.add(new OptSimProperty("ng",
        //        "ptolemy.data.expr.Parameter","4.393"));
        this.rootProperty.property.add(new OptSimProperty("_vergilZoomFactor",
                "ptolemy.data.expr.Parameter","1.4889634856477"));
        this.rootProperty.property.add(new OptSimProperty("_vergilCenter",
                "ptolemy.data.expr.Parameter","{-2525.578911771202, -222.9354893624853}"));
        this.rootProperty.property.add(new OptSimProperty("RingLength",
                "ptolemy.data.expr.Parameter","-1"));
        //this.rootProperty.property.add(new OptSimProperty("N",
        //        "ptolemy.data.expr.Parameter","20"));
        this.rootProperty.property.add(new OptSimProperty("foundryName",
                "ptolemy.data.expr.Parameter","&quot;DEMOFAB&quot;"));
        //this.rootProperty.property.add(new OptSimProperty("transientTime",
        //        "ptolemy.data.expr.Parameter","1e-11"));
        //this.rootProperty.property.add(new OptSimProperty("FullFSR",
        //        "ptolemy.data.expr.Parameter","6.4e-9"));
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
                    singleProperty.className+"\">\n";
        	propertyString = propertyString+"</property>\n";
        default:
        	break;
    }
    }
    

    
    public void setWriteFilePropertyString(){
    	propertyString="";
    	propertyString=propertyString+"<?xml version=\"1.0\" standalone=\"no\"?>\r\n" + 
    	"<!DOCTYPE entity PUBLIC \"-//UC Berkeley//DTD MoML 1//EN\"\r\n" + 
    	"    \"http://ptolemy.eecs.berkeley.edu/xml/dtd/MoML_1.dtd\">\n";
    	propertyString = propertyString+"<entity name=\""+this.rootProperty.propertyName+"\" class=\""+
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

