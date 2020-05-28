package People.Rui.Extension.OptSimExtension;

public class SignalGeneratorComponent extends AbstractPropertyTree{
	
	public SignalGeneratorComponent(String name) {
		super(name);
		this.rootProperty.className="linksim.src.stars.PRBSGenerator";
		setPosition(-2640, -460);
		setParameters();
	}
	
	public SignalGeneratorComponent(String name, double x, double y) {
		super(name);
		this.rootProperty.className="linksim.src.stars.PRBSGenerator";
		setPosition(x, y);
		setParameters();
	}

	
	@Override
	public void setParameters(){
		this.rootProperty.property.add(new OptSimProperty("model_instance",
                "com.rsoftdesign.simworks.link.PsiIntParameter","1"));
		
		this.rootProperty.property.add(new OptSimProperty("patternType",
                "ptolemy.data.expr.Parameter","&quot;alternating&quot;"));
		
		this.rootProperty.property.add(new OptSimProperty("filename",
                "com.rsoftdesign.simworks.apps.query.parameter.OptSimFileParameter","C:/PSIM/PRBS/PRBS_b.dat"));
		
		this.rootProperty.property.add(new OptSimProperty("bitRate",
                "ptolemy.data.expr.Parameter","bitRate"));
		
		this.rootProperty.property.add(new OptSimProperty("patternLength",
                "ptolemy.data.expr.Parameter","patternLength"));
		
		this.rootProperty.property.add(new OptSimProperty("startTime",
                "ptolemy.data.expr.Parameter","0.0"));
		
		this.rootProperty.property.add(new OptSimProperty("offset",
                "ptolemy.data.expr.Parameter","0"));
		
		this.rootProperty.property.add(new OptSimProperty("shift",
                "ptolemy.data.expr.Parameter","0"));
		
		this.rootProperty.property.add(new OptSimProperty("preBits",
                "ptolemy.data.expr.Parameter","2"));
		
		this.rootProperty.property.add(new OptSimProperty("postBits",
                "ptolemy.data.expr.Parameter","3"));
		
		this.rootProperty.property.add(new OptSimProperty("number_output_ports",
                "ptolemy.data.expr.Parameter","1"));
		
		this.rootProperty.property.add(new OptSimProperty("PRBSGeneratorDesc"));
		
		this.rootProperty.property.add(new OptSimProperty("_location",
                "ptolemy.kernel.util.Location",position));
		
		this.rootProperty.property.add(new OptSimProperty("output#1",
                "com.rsoftdesign.simworks.link.PsiTypedIOPort",2));// flag = 2 for output port setting
	    }
	
	 @Override
	 public void switchWrite(OptSimProperty singleProperty) {
	     switch (singleProperty.flag){
	        case 0:
	        	propertyString = propertyString+"<property name=\""+singleProperty.propertyName+"\" class=\""+
	                    singleProperty.className+"\" value=\""+singleProperty.classValue+"\">\n";
	        	propertyString = propertyString+"</property>\n";
	        	break;
	        case 1:
	        	propertyString=propertyString+"<doc name=\""+singleProperty.propertyName+"\">PRBS Generator</doc>\n";
	        	break;
	        case 2:
	        	// input port
	        	propertyString=propertyString+"<port name=\""+singleProperty.propertyName+"\" class=\""+
	                    singleProperty.className+"\">\n" ;
	        	propertyString=propertyString+"<property name=\"output\"/>\n";
	        	propertyString=propertyString+"<property name=\"multiport\"/>\n";
	        	propertyString=propertyString+"<property name=\"_cardinal\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"SOUTH\">\n";
	        	propertyString=propertyString+"</property>\n";
	        	propertyString=propertyString+"</port>\n";
	        	break;
	        default:
	        	break;
	        }
	    }
	    
	    @Override
	    // For this component, we only have one level property, so just print them out
	    public void setWriteFilePropertyString(){
	    	propertyString="";
	    	propertyString = propertyString+"<entity name=\""+this.rootProperty.propertyName+"\" class=\""+
	                this.rootProperty.className+"\">\n";
	        for (OptSimProperty singleProperty : this.rootProperty.property) 
	      		switchWrite(singleProperty);
	    	propertyString = propertyString+"</entity>\n";
	    }
	
	
}
