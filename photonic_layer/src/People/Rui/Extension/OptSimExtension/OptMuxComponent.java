package People.Rui.Extension.OptSimExtension;

public class OptMuxComponent extends AbstractPropertyTree {
	int portNum;
	
	// Constructor for test
	public OptMuxComponent(String name, int portnum) {
		super(name);      
		this.portNum = portnum;
		this.rootProperty.className="linksim.src.stars.IdealOpticalMUX";
		setPosition(-2720.0, -300.0);
		setParameters();
	}
	
	public OptMuxComponent(String name, int portnum, double x, double y) {
		super(name);      
		this.portNum = portnum;
		this.rootProperty.className="linksim.src.stars.IdealOpticalMUX";
		setPosition(x, y);
		setParameters();
	}
	
	
	@Override
	public void setParameters(){
	        this.rootProperty.property.add(new OptSimProperty("model_instance",
	                "com.rsoftdesign.simworks.link.PsiIntParameter","1"));
	        
	        this.rootProperty.property.add(new OptSimProperty("Representation",
	                "ptolemy.data.expr.Parameter","&quot;SingleBand&quot;"));
	        
	        this.rootProperty.property.add(new OptSimProperty("loss",
	                "ptolemy.data.expr.Parameter","0.0"));
	        
	        this.rootProperty.property.add(new OptSimProperty("filterType",
	                "ptolemy.data.expr.Parameter","&quot;None&quot;"));
	        
	        this.rootProperty.property.add(new OptSimProperty("filename",
	                "com.rsoftdesign.simworks.apps.query.parameter.OptSimFileParameter","&quot;&quot;"));
	        
	        this.rootProperty.property.add(new OptSimProperty("filterSpecMode",
	                "ptolemy.data.expr.Parameter","&quot;Wavelength&quot;"));
	        
	        this.rootProperty.property.add(new OptSimProperty("filterBW",
	                "ptolemy.data.expr.Parameter","1.0E-10"));
	        
	        this.rootProperty.property.add(new OptSimProperty("filterBW0dB",
	                "ptolemy.data.expr.Parameter","0.0"));
	        
	        this.rootProperty.property.add(new OptSimProperty("filterOrder",
	                "ptolemy.data.expr.Parameter","1"));
	        
	        this.rootProperty.property.add(new OptSimProperty("rolloff",
	                "ptolemy.data.expr.Parameter","0.1"));
	        
	        this.rootProperty.property.add(new OptSimProperty("alpha",
	                "ptolemy.data.expr.Parameter","1.0"));
	        
	        this.rootProperty.property.add(new OptSimProperty("filterSpacing",
	                "ptolemy.data.expr.Parameter","8.0E-10"));
	        
	        this.rootProperty.property.add(new OptSimProperty("firstFilterCenter",
	                "ptolemy.data.expr.Parameter","1.55E-6"));
	        
	        this.rootProperty.property.add(new OptSimProperty("filterFSR",
	                "ptolemy.data.expr.Parameter","1.0E-7"));
	        
	        this.rootProperty.property.add(new OptSimProperty("paramXTalk",
	                "ptolemy.data.expr.Parameter","&quot;No&quot;"));
	        
	        this.rootProperty.property.add(new OptSimProperty("useCenterFrequency",
	                "ptolemy.data.expr.Parameter","&quot;No&quot;"));
	        
	        this.rootProperty.property.add(new OptSimProperty("signalWindow",
	                "ptolemy.data.expr.Parameter","1.0E-10"));
	        
	        this.rootProperty.property.add(new OptSimProperty("filterIsolation",
	                "ptolemy.data.expr.Parameter","-25.0"));
	        
	        this.rootProperty.property.add(new OptSimProperty("number_input_ports",
	                "ptolemy.data.expr.Parameter",Integer.toString(portNum)));
	        
	        this.rootProperty.property.add(new OptSimProperty("IdealOpticalMUXDesc"));
	        
	        this.rootProperty.property.add(new OptSimProperty("_location",
	                "ptolemy.kernel.util.Location",position));
	        
	        for (int i=1; i<portNum+1; i++) {
		        this.rootProperty.property.add(new OptSimProperty("input#"+new Integer(i).toString(),
		                "com.rsoftdesign.simworks.link.PsiTypedIOPort",2)); // flag is 2 for input port
	        }
	        this.rootProperty.property.add(new OptSimProperty("output",
	                "com.rsoftdesign.simworks.link.PsiTypedIOPort",3)); // flag=3 : output port
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
	        	propertyString=propertyString+"<doc name=\""+singleProperty.propertyName+"\">Ideal Optical Multiplexer</doc>\n";
	        	break;
	        case 2:
	        	// input port
	        	propertyString=propertyString+"<port name=\""+singleProperty.propertyName+"\" class=\""+
	                    singleProperty.className+"\">\n" ;
	        	propertyString=propertyString+"<property name=\"input\"/>\n";
	        	propertyString=propertyString+"</port>\n";
	        	break;
	        case 3:
	        	// output port
	        	propertyString=propertyString+"<port name=\""+singleProperty.propertyName+"\" class=\""+
	                    singleProperty.className+"\">\n" ;
	        	propertyString=propertyString+"<property name=\"output\"/>\n";
	        	propertyString=propertyString+"<property name=\"multiport\"/>\n";
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

	    public void setPortNum(int num) {
	    	this.portNum = num;
	    }
}
