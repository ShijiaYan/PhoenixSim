package People.Rui.Extension.OptSimExtension;

public class SpectrumAnalyzerComponent extends AbstractPropertyTree{
	
	public SpectrumAnalyzerComponent(String name) {
		super(name);
		this.rootProperty.className = "linksim.src.stars.SpectrumAnalyzer";
		setPosition(-2640, 0);
		setParameters();
	}
	
	public SpectrumAnalyzerComponent(String name, double x, double y) {
		super(name);
		this.rootProperty.className = "linksim.src.stars.SpectrumAnalyzer";
		setPosition(x, y);
		setParameters();
	}
	
	@Override
	public void setParameters(){
		this.rootProperty.property.add(new OptSimProperty("model_instance",
                "com.rsoftdesign.simworks.link.PsiIntParameter","3"));
		
		this.rootProperty.property.add(new OptSimProperty("FilenameRoot",
                "ptolemy.data.expr.Parameter","&quot;&quot;"));
		
		this.rootProperty.property.add(new OptSimProperty("Spec_Representation",
                "ptolemy.data.expr.Parameter","&quot;Power&quot;"));
		
		this.rootProperty.property.add(new OptSimProperty("Domain",
                "ptolemy.data.expr.Parameter","&quot;Default&quot;"));
		
		this.rootProperty.property.add(new OptSimProperty("Polarization",
                "ptolemy.data.expr.Parameter","&quot;comb_x_and_y&quot;")); 
		
		this.rootProperty.property.add(new OptSimProperty("Spec_ShowNoise",
                "ptolemy.data.expr.Parameter","&quot;Combined&quot;"));
		
		this.rootProperty.property.add(new OptSimProperty("seed",
                "ptolemy.data.expr.Parameter","0"));
		
		this.rootProperty.property.add(new OptSimProperty("PowerThreshold",
                "ptolemy.data.expr.Parameter","0.0")); 
		
		this.rootProperty.property.add(new OptSimProperty("PowerUnits",
                "ptolemy.data.expr.Parameter","&quot;Watt&quot;"));
		
		this.rootProperty.property.add(new OptSimProperty("IncludeDCoffset",
                "ptolemy.data.expr.Parameter","&quot;Yes&quot;"));
		
		
		
		this.rootProperty.property.add(new OptSimProperty("Normalize",
                "ptolemy.data.expr.Parameter","&quot;No&quot;"));
		
		this.rootProperty.property.add(new OptSimProperty("Plot",
                "ptolemy.data.expr.Parameter","&quot;Save&quot;"));
		
		this.rootProperty.property.add(new OptSimProperty("Resolution",
                "ptolemy.data.expr.Parameter","&quot;Limited&quot;"));
		
		this.rootProperty.property.add(new OptSimProperty("ResolutionLimit",
                "ptolemy.data.expr.Parameter","2049"));
		
		this.rootProperty.property.add(new OptSimProperty("filterType",
                "ptolemy.data.expr.Parameter","&quot;None&quot;")); 
		
		this.rootProperty.property.add(new OptSimProperty("filterSpecMode",
                "ptolemy.data.expr.Parameter","&quot;Frequency&quot;"));
		
		this.rootProperty.property.add(new OptSimProperty("filterBW",
                "ptolemy.data.expr.Parameter","1.0E10"));
		
		this.rootProperty.property.add(new OptSimProperty("filterBW0dB",
                "ptolemy.data.expr.Parameter","1.0E8")); 
		
		this.rootProperty.property.add(new OptSimProperty("filterOrder",
                "ptolemy.data.expr.Parameter","1"));
		
		this.rootProperty.property.add(new OptSimProperty("filterThreshold",
                "ptolemy.data.expr.Parameter","-100"));
		
		
		
		this.rootProperty.property.add(new OptSimProperty("OSAsensitivityMode",
                "ptolemy.data.expr.Parameter","&quot;Unlimited&quot;"));
		
		this.rootProperty.property.add(new OptSimProperty("OSAsensitivity",
                "ptolemy.data.expr.Parameter","-65.0")); 
		
		this.rootProperty.property.add(new OptSimProperty("number_input_ports",
                "ptolemy.data.expr.Parameter","1"));
		
		this.rootProperty.property.add(new OptSimProperty("SpectrumAnalyzerDesc"));
		
		this.rootProperty.property.add(new OptSimProperty("_location",
                "ptolemy.kernel.util.Location",position)); 
		
		this.rootProperty.property.add(new OptSimProperty("input#1",
                "com.rsoftdesign.simworks.link.PsiTypedIOPort",2));// special property for flag 2		
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
	        	propertyString=propertyString+"<doc name=\""+singleProperty.propertyName+"\">Spectrum Analyzer</doc>\n";
	        	break;
	        case 2:
	        	propertyString = propertyString+"<port name=\""+singleProperty.propertyName+"\" class=\""+
	                    singleProperty.className+"\">\n";
	        	propertyString = propertyString + "<property name=\"input\"/>";
	        	propertyString = propertyString+"</port>\n";
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
