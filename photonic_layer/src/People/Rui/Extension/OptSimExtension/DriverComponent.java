package People.Rui.Extension.OptSimExtension;

public class DriverComponent extends AbstractPropertyTree{
	
	public DriverComponent(String name) {
		super(name);
		this.rootProperty.className="linksim.src.stars.ElectricalSignalGenerator";
		setPosition(-2640.0, -380.0);
		setParameters();
	}
	
	public DriverComponent(String name, double x, double y) {
		super(name);
		this.rootProperty.className="linksim.src.stars.ElectricalSignalGenerator";
		setPosition(x, y);
		setParameters();
	}
	
	@Override
	public void setParameters(){
		this.rootProperty.property.add(new OptSimProperty("model_instance",
                "com.rsoftdesign.simworks.link.PsiIntParameter","1"));
		
		this.rootProperty.property.add(new OptSimProperty("driveType",
                "ptolemy.data.expr.Parameter","&quot;on_off&quot;"));
		
		this.rootProperty.property.add(new OptSimProperty("signalType",
                "ptolemy.data.expr.Parameter","&quot;VOLTAGE&quot;"));
		
		this.rootProperty.property.add(new OptSimProperty("modulationType",
                "ptolemy.data.expr.Parameter","&quot;NRZ&quot;"));
		
		this.rootProperty.property.add(new OptSimProperty("pointsPerBit",
                "ptolemy.data.expr.Parameter","pointsPerBit"));
		
		this.rootProperty.property.add(new OptSimProperty("Vmax",
                "ptolemy.data.expr.Parameter","1"));
		
		this.rootProperty.property.add(new OptSimProperty("Vmin",
                "ptolemy.data.expr.Parameter","0.0"));
		
		this.rootProperty.property.add(new OptSimProperty("tr",
                "ptolemy.data.expr.Parameter","transientTime"));
		
		this.rootProperty.property.add(new OptSimProperty("tf",
                "ptolemy.data.expr.Parameter","transientTime"));
		
		this.rootProperty.property.add(new OptSimProperty("timeJitter",
                "ptolemy.data.expr.Parameter","0"));
		
		this.rootProperty.property.add(new OptSimProperty("filterType",
                "ptolemy.data.expr.Parameter","&quot;None&quot;"));
		
		this.rootProperty.property.add(new OptSimProperty("f0",
				"ptolemy.data.expr.Parameter","1.0E18"));
		
		
		this.rootProperty.property.add(new OptSimProperty("gamma",
                "ptolemy.data.expr.Parameter","0.0"));
		
		this.rootProperty.property.add(new OptSimProperty("alpha",
                "ptolemy.data.expr.Parameter","0.5"));
		
		this.rootProperty.property.add(new OptSimProperty("number_port_sets",
                "ptolemy.data.expr.Parameter","1"));
		
		this.rootProperty.property.add(new OptSimProperty("number_input_ports",
                "ptolemy.data.expr.Parameter","number_port_sets"));
		
		this.rootProperty.property.add(new OptSimProperty("number_output_ports",
				"ptolemy.data.expr.Parameter","number_port_sets"));
		
		
		this.rootProperty.property.add(new OptSimProperty("ElectricalSignalGeneratorDesc"));
		
		this.rootProperty.property.add(new OptSimProperty("_location",
				"ptolemy.kernel.util.Location",position));
		
		this.rootProperty.property.add(new OptSimProperty("_hideName",
				"ptolemy.kernel.util.SingletonAttribute",2));// flag = 2 for special property
		
		this.rootProperty.property.add(new OptSimProperty("input#1",
                "com.rsoftdesign.simworks.link.PsiTypedIOPort",3));// flag = 3 for input port setting
		
		this.rootProperty.property.add(new OptSimProperty("output#1",
                "com.rsoftdesign.simworks.link.PsiTypedIOPort",4));// flag = 4 for output port setting
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
	        	propertyString=propertyString+"<doc name=\""+singleProperty.propertyName+"\">Electrical Signal Generator</doc>\n";
	        	break;
	        case 2:
	        	propertyString = propertyString+"<property name=\""+singleProperty.propertyName+"\" class=\""+
	                    singleProperty.className+"\">\n";
	        	propertyString = propertyString+"</property>\n";
	        	break;
	        case 3:
	        	// input port
	        	propertyString=propertyString+"<port name=\""+singleProperty.propertyName+"\" class=\""+
	                    singleProperty.className+"\">\n" ;
	        	propertyString=propertyString+"<property name=\"input\"/>\n";
	        	propertyString=propertyString+"<property name=\"_cardinal\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"NORTH\">\n";
	        	propertyString=propertyString+"</property>\n";
	        	propertyString=propertyString+"</port>\n";
	        	break;
	        case 4:
	        	// output port
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
