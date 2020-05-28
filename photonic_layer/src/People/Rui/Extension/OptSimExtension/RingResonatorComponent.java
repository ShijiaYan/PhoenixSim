package People.Rui.Extension.OptSimExtension;

public class RingResonatorComponent extends AbstractPropertyTree{
	
	public double length;
	public double alpha;
	public double wavelength;
	public double ng;
	
	public RingResonatorComponent(String name) {
		super(name);
		this.wavelength = 1.55E-6;
		this.ng = 4.393;
		setVariable();
		this.rootProperty.className = "block_mode.models.moml.RingResonator";
		setPosition(-2640.0, -100.0);
		setParameters();
	}
	
	public RingResonatorComponent(String name, double x, double y) {
		super(name);
		this.wavelength = 1.55E-6;
		this.ng = 4.393;
		setVariable();
		this.rootProperty.className = "block_mode.models.moml.RingResonator";
		setPosition(x, y);
		setParameters();
	}
	
	public RingResonatorComponent(String name, double wavelength, double x, double y) {
		super(name);
		this.wavelength = wavelength;
		this.ng = 4.393;
		setVariable();
		this.rootProperty.className = "block_mode.models.moml.RingResonator";
		setPosition(x, y);
		setParameters();
	}

	@Override
	public void setParameters(){
		this.rootProperty.property.add(new OptSimProperty("model_instance",
                "com.rsoftdesign.simworks.link.PsiIntParameter","3"));
		
		this.rootProperty.property.add(new OptSimProperty("number_input_ports",
                "com.rsoftdesign.simworks.link.PsiIntParameter","2"));
		
		this.rootProperty.property.add(new OptSimProperty("number_output_ports",
                "com.rsoftdesign.simworks.link.PsiIntParameter","2"));
		
		this.rootProperty.property.add(new OptSimProperty("_vergilRotatePorts",
                "ptolemy.data.expr.Parameter","0"));
		
		this.rootProperty.property.add(new OptSimProperty("_vergilFlipPortsVertical",
                "ptolemy.data.expr.Parameter",2)); // special property, flag = 2
		
		this.rootProperty.property.add(new OptSimProperty("_vergilFlipPortsHorizontal",
                "ptolemy.data.expr.Parameter",2));
		
		this.rootProperty.property.add(new OptSimProperty("_vergilShowParamList",
                "ptolemy.data.expr.Parameter",2));
		
		this.rootProperty.property.add(new OptSimProperty("_vergilParameterFile",
                "ptolemy.kernel.util.SingletonConfigurableAttribute",3)); // special property, flag = 3
		
		this.rootProperty.property.add(new OptSimProperty("_vergilIconFile",
                "ptolemy.kernel.util.StringAttribute","ringresonator.svg"));
		
		this.rootProperty.property.add(new OptSimProperty("length",
                "com.rsoftdesign.simworks.link.PsiDoubleParameter",Double.toString(length)));
		
		this.rootProperty.property.add(new OptSimProperty("T",
                "com.rsoftdesign.simworks.link.PsiDoubleParameter","25"));
		
		this.rootProperty.property.add(new OptSimProperty("insertionLoss",
                "com.rsoftdesign.simworks.link.PsiDoubleParameter","0"));
		
		this.rootProperty.property.add(new OptSimProperty("nEff",
				"com.rsoftdesign.simworks.link.PsiDoubleParameter","ng"));
		
		this.rootProperty.property.add(new OptSimProperty("alpha_CouplerPort13",
                "com.rsoftdesign.simworks.link.PsiDoubleParameter","0.00238095238"));
		
		this.rootProperty.property.add(new OptSimProperty("alpha_CouplerPort24",
                "com.rsoftdesign.simworks.link.PsiDoubleParameter","0.00238095238"));
		
		this.rootProperty.property.add(new OptSimProperty("propagationLoss",
                "com.rsoftdesign.simworks.link.PsiDoubleParameter",Double.toString(alpha)));
		

		this.rootProperty.property.add(new OptSimProperty("includeThermalEffects",
                "com.rsoftdesign.simworks.link.PsiStringParameter","&quot;No&quot;"));
		
		this.rootProperty.property.add(new OptSimProperty("T0",
                "com.rsoftdesign.simworks.link.PsiDoubleParameter","25"));
		
		this.rootProperty.property.add(new OptSimProperty("dnEff_dT",
                "com.rsoftdesign.simworks.link.PsiDoubleParameter","0"));

		this.rootProperty.property.add(new OptSimProperty("dLoss_dT",
                "com.rsoftdesign.simworks.link.PsiDoubleParameter","0"));

		
		this.rootProperty.property.add(new OptSimProperty("polarization_diversity",
                "com.rsoftdesign.simworks.link.PsiStringParameter","&quot;No&quot;"));
		
		
		this.rootProperty.property.add(new OptSimProperty("insertionLoss_Y",
				"com.rsoftdesign.simworks.link.PsiDoubleParameter","0"));
		
		
		this.rootProperty.property.add(new OptSimProperty("nEff_Y",
                "com.rsoftdesign.simworks.link.PsiDoubleParameter","3"));
		
		this.rootProperty.property.add(new OptSimProperty("alpha_CouplerPort13_Y",
                "com.rsoftdesign.simworks.link.PsiDoubleParameter","0.5"));
		
		this.rootProperty.property.add(new OptSimProperty("alpha_CouplerPort24_Y",
                "com.rsoftdesign.simworks.link.PsiDoubleParameter","0.5"));
		
		this.rootProperty.property.add(new OptSimProperty("propagationLoss_Y",
                "com.rsoftdesign.simworks.link.PsiDoubleParameter","0"));
		
		this.rootProperty.property.add(new OptSimProperty("includeThermalEffects_Y",
                "com.rsoftdesign.simworks.link.PsiStringParameter","&quot;No&quot;"));
		
		this.rootProperty.property.add(new OptSimProperty("T0_Y",
				"com.rsoftdesign.simworks.link.PsiDoubleParameter","25"));
		
		this.rootProperty.property.add(new OptSimProperty("dnEff_dT_Y",
                "com.rsoftdesign.simworks.link.PsiDoubleParameter","0"));
		
		this.rootProperty.property.add(new OptSimProperty("dLoss_dT_Y",
				"com.rsoftdesign.simworks.link.PsiDoubleParameter","0"));
		
		this.rootProperty.property.add(new OptSimProperty("SignalDomain",
                "com.rsoftdesign.simworks.link.PsiStringParameter","&quot;Time&quot;"));
			
		this.rootProperty.property.add(new OptSimProperty("_location",
				"ptolemy.kernel.util.Location",position));
		
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
	        	propertyString = propertyString+"<property name=\""+singleProperty.propertyName+"\" class=\""+
	                    singleProperty.className+"\">\n";
	        	propertyString = propertyString + "<configure>RingResonator.dta</configure>";
	        	propertyString = propertyString+"</property>\n";
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
	
	
	    public void setVariable() {
	    	length = wavelength*5/ng;
	    	alpha = 1e-8/length;
			
		}
	
	
}
