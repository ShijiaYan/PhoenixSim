package People.Rui.Extension.OptSimExtension;


public class NonlinearFiberComponent extends AbstractPropertyTree{
	
	public NonlinearFiberComponent(String name) {
		super(name);
		this.rootProperty.className = "linksim.src.stars.NonlinearFiber";
		setPosition(-2080, -200);
		setParameters();
	}
	
	public NonlinearFiberComponent(String name, double x, double y) {
		super(name);
		this.rootProperty.className = "linksim.src.stars.NonlinearFiber";
		setPosition(x, y);
		setParameters();
	}
	
	@Override
	public void setParameters(){
		this.rootProperty.property.add(new OptSimProperty("model_instance",
                "com.rsoftdesign.simworks.link.PsiIntParameter","1"));
		
		this.rootProperty.property.add(new OptSimProperty("length",
                "ptolemy.data.expr.Parameter","1e3"));
		
		this.rootProperty.property.add(new OptSimProperty("zStepFac",
                "ptolemy.data.expr.Parameter","0.1"));
		
		this.rootProperty.property.add(new OptSimProperty("zStepSize",
                "ptolemy.data.expr.Parameter","-1.0"));
		
		this.rootProperty.property.add(new OptSimProperty("decreasingStepSize",
                "ptolemy.data.expr.Parameter","&quot;No&quot;")); 
		
		this.rootProperty.property.add(new OptSimProperty("loss_model",
                "ptolemy.data.expr.Parameter","&quot;Constant&quot;"));
		
		this.rootProperty.property.add(new OptSimProperty("loss",
                "ptolemy.data.expr.Parameter","0.25"));
		
		this.rootProperty.property.add(new OptSimProperty("loss_filename",
                "com.rsoftdesign.simworks.apps.query.parameter.OptSimFileParameter","&quot;&quot;")); 
		
		this.rootProperty.property.add(new OptSimProperty("dispersionModel",
                "ptolemy.data.expr.Parameter","&quot;defined&quot;"));
		
		this.rootProperty.property.add(new OptSimProperty("n1",
                "ptolemy.data.expr.Parameter","1.4682"));
		
		this.rootProperty.property.add(new OptSimProperty("beta2",
                "ptolemy.data.expr.Parameter","0"));
		
		this.rootProperty.property.add(new OptSimProperty("beta3",
                "ptolemy.data.expr.Parameter","0"));
		
		this.rootProperty.property.add(new OptSimProperty("beta4",
                "ptolemy.data.expr.Parameter","0"));
		
		this.rootProperty.property.add(new OptSimProperty("beta5",
                "ptolemy.data.expr.Parameter","0"));
		
		this.rootProperty.property.add(new OptSimProperty("beta6",
                "ptolemy.data.expr.Parameter","0")); 
		
		this.rootProperty.property.add(new OptSimProperty("beta7",
                "ptolemy.data.expr.Parameter","0"));
		
		this.rootProperty.property.add(new OptSimProperty("dispersionLambda0",
                "ptolemy.data.expr.Parameter","1.312E-6"));
		
		this.rootProperty.property.add(new OptSimProperty("dispersionS0",
                "ptolemy.data.expr.Parameter","90.0")); 
		
		this.rootProperty.property.add(new OptSimProperty("dispersionOffset",
                "ptolemy.data.expr.Parameter","0.0"));
		
		this.rootProperty.property.add(new OptSimProperty("nonlinearity_model",
                "ptolemy.data.expr.Parameter","&quot;Constant&quot;"));
		
		this.rootProperty.property.add(new OptSimProperty("n2",
                "ptolemy.data.expr.Parameter","2.6E-20"));
		
		this.rootProperty.property.add(new OptSimProperty("nonlinearity_filename",
                "com.rsoftdesign.simworks.apps.query.parameter.OptSimFileParameter","&quot;&quot;")); 
		
		this.rootProperty.property.add(new OptSimProperty("diameter",
                "ptolemy.data.expr.Parameter","8.2E-6"));
		
		this.rootProperty.property.add(new OptSimProperty("aEff","ptolemy.data.expr.Parameter","1.425"));
		
		this.rootProperty.property.add(new OptSimProperty("pmd_method",
                "ptolemy.data.expr.Parameter","&quot;None&quot;"));
		
		this.rootProperty.property.add(new OptSimProperty("pmd_coef",
                "ptolemy.data.expr.Parameter","3.16E-15"));
		
		this.rootProperty.property.add(new OptSimProperty("pmd_corlen",
                "ptolemy.data.expr.Parameter","10.0"));
		
		this.rootProperty.property.add(new OptSimProperty("pmd_seed",
                "ptolemy.data.expr.Parameter","0"));
		
		this.rootProperty.property.add(new OptSimProperty("birefringence",
                "ptolemy.data.expr.Parameter","0.0")); 
		
		this.rootProperty.property.add(new OptSimProperty("raman_effects",
                "ptolemy.data.expr.Parameter","&quot;On&quot;"));
		
		this.rootProperty.property.add(new OptSimProperty("raman_strength_model",
                "ptolemy.data.expr.Parameter","&quot;Absolute&quot;"));
		
		this.rootProperty.property.add(new OptSimProperty("raman_profile",
                "ptolemy.data.expr.Parameter","&quot;Default&quot;")); 
		
		this.rootProperty.property.add(new OptSimProperty("raman_profile_filename",
                "com.rsoftdesign.simworks.apps.query.parameter.OptSimFileParameter","&quot;&quot;"));
		
		this.rootProperty.property.add(new OptSimProperty("raman_max_gain",
                "ptolemy.data.expr.Parameter","9.8E-14"));
		
		this.rootProperty.property.add(new OptSimProperty("raman_response_fraction",
                "ptolemy.data.expr.Parameter","0.18"));
		
		this.rootProperty.property.add(new OptSimProperty("raman_pump_ref_lambda",
                "ptolemy.data.expr.Parameter","1.0E-6"));
		
		this.rootProperty.property.add(new OptSimProperty("raman_analytic_sig1",
                "ptolemy.data.expr.Parameter","1.22E-14"));
		
		this.rootProperty.property.add(new OptSimProperty("raman_analytic_sig2",
                "ptolemy.data.expr.Parameter","3.2E-14"));
		
		this.rootProperty.property.add(new OptSimProperty("raman_pump_scaling",
                "ptolemy.data.expr.Parameter","&quot;Default&quot;")); 
		
		this.rootProperty.property.add(new OptSimProperty("raman_pump_scaling_filename",
                "com.rsoftdesign.simworks.apps.query.parameter.OptSimFileParameter","&quot;&quot;"));
		
		this.rootProperty.property.add(new OptSimProperty("raman_self_interaction",
                "ptolemy.data.expr.Parameter","&quot;Off&quot;"));
		
		this.rootProperty.property.add(new OptSimProperty("raman_sigsig_interaction",
                "ptolemy.data.expr.Parameter","&quot;Simple&quot;")); 
		
		this.rootProperty.property.add(new OptSimProperty("include_sbs",
                "ptolemy.data.expr.Parameter","&quot;No&quot;"));
		
		this.rootProperty.property.add(new OptSimProperty("g_B",
                "ptolemy.data.expr.Parameter","3.0E-11"));
		
		this.rootProperty.property.add(new OptSimProperty("k_pol",
                "ptolemy.data.expr.Parameter","1.5"));
		
		this.rootProperty.property.add(new OptSimProperty("gain_ref_freq",
                "ptolemy.data.expr.Parameter","193.0")); 
		
		this.rootProperty.property.add(new OptSimProperty("Delta_fB",
                "ptolemy.data.expr.Parameter","40.0"));
		
		this.rootProperty.property.add(new OptSimProperty("f_B",
				"ptolemy.data.expr.Parameter","11.0"));
		
		this.rootProperty.property.add(new OptSimProperty("force_linewidth",
                "ptolemy.data.expr.Parameter","&quot;No&quot;"));
		
		this.rootProperty.property.add(new OptSimProperty("channel_linewidth",
                "ptolemy.data.expr.Parameter","100.0"));
		
		this.rootProperty.property.add(new OptSimProperty("include_spm",
                "ptolemy.data.expr.Parameter","&quot;Yes&quot;"));
		
		this.rootProperty.property.add(new OptSimProperty("include_xpm_el",
                "ptolemy.data.expr.Parameter","&quot;Yes&quot;"));
		
		this.rootProperty.property.add(new OptSimProperty("include_xpm_mol",
                "ptolemy.data.expr.Parameter","&quot;Yes&quot;")); 
		
		this.rootProperty.property.add(new OptSimProperty("include_dispersion",
                "ptolemy.data.expr.Parameter","&quot;Yes&quot;"));
		
		this.rootProperty.property.add(new OptSimProperty("skew",
                "ptolemy.data.expr.Parameter","0.0"));
		
		this.rootProperty.property.add(new OptSimProperty("optimizationlevel",
                "ptolemy.data.expr.Parameter","3")); 
		
		this.rootProperty.property.add(new OptSimProperty("showstatus",
                "ptolemy.data.expr.Parameter","&quot;Yes&quot;"));
		
		this.rootProperty.property.add(new OptSimProperty("logstatus",
                "ptolemy.data.expr.Parameter","&quot;No&quot;"));
		
		this.rootProperty.property.add(new OptSimProperty("physpropFilename",
                "com.rsoftdesign.simworks.apps.query.parameter.OptSimFileParameter","&quot;&quot;"));
		
		this.rootProperty.property.add(new OptSimProperty("physpropSteps",
                "ptolemy.data.expr.Parameter","20"));
		
		this.rootProperty.property.add(new OptSimProperty("frequency_units",
                "ptolemy.data.expr.Parameter","&quot;um&quot;"));
		
		this.rootProperty.property.add(new OptSimProperty("test_raman_pump_lambda",
                "ptolemy.data.expr.Parameter","1.4E-6"));
		
		this.rootProperty.property.add(new OptSimProperty("test_output",
                "ptolemy.data.expr.Parameter","&quot;raman_gain&quot;")); 
		
		this.rootProperty.property.add(new OptSimProperty("NonlinearFiberDesc")); 
		
		this.rootProperty.property.add(new OptSimProperty("_location",
                "ptolemy.kernel.util.Location",position)); 
		
		this.rootProperty.property.add(new OptSimProperty("_vergilRotatePorts",
                "ptolemy.data.expr.Parameter","90")); 
		
		this.rootProperty.property.add(new OptSimProperty("input#1",
                "com.rsoftdesign.simworks.link.PsiTypedIOPort",2));// special property for flag 2
		
		this.rootProperty.property.add(new OptSimProperty("output",
                "com.rsoftdesign.simworks.link.PsiTypedIOPort",3));// special property for flag 3	
		
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
	        	propertyString=propertyString+"<doc name=\""+singleProperty.propertyName+"\">Nonlinear Fiber</doc>\n";
	        	break;
	        case 2:
	        	propertyString = propertyString+"<port name=\""+singleProperty.propertyName+"\" class=\""+
	                    singleProperty.className+"\">\n";
	        	propertyString = propertyString + "<property name=\"input\"/>\n";
	        	propertyString = propertyString+"</port>\n";
	        	break;
	        case 3:
	        	propertyString = propertyString+"<port name=\""+singleProperty.propertyName+"\" class=\""+
	                    singleProperty.className+"\">\n";
	        	propertyString = propertyString + "<property name=\"output\"/>\n";
	        	propertyString = propertyString + "<property name=\"multiport\"/>\n";
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
