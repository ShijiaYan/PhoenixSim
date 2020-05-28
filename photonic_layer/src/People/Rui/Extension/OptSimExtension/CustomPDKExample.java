package People.Rui.Extension.OptSimExtension;

import java.util.ArrayList;

public class CustomPDKExample extends AbstractPropertyTree {
	//###################### follow the same step ###########################
	// 1 build up constructor
	// 2 build up three main methods, where method 1 is where you should work on first, and then method 2, method 3 normally don't need any change. 
	// 	- method 1: setParameters()
	//  	 This method set up the whole component tree.
	// 	- method 2: switchWrite(OptSimProperty)
	//  	 This method defines the format of how you write down each property
	// 	- method 3: setWriteFilePropertyString()
	//  	 This method defines the propertyString, which has been defined in super class(AbstractPropertyTree).
	// 3 for pdk file, you also need to pass through variable(s) which are scanned during the generation
	//   	pay attention to the line with ***, change that into your parameter, later we will find a way to do that automatically.
	
	
	// for now, only support single scanned parameter
	ArrayList<String> scannedVariables = new ArrayList<>();
	String pdkName;
	String scannedParameter;
	String scannedParameterValue;
	int inputPortNumber;
	int outputPortNumber;
	    // Constructor we should use, to manipulate
	    // Name
	    // Wavelength
	    // power
	    // position
	    // Finally we all let it automatically generate, but in this stage we need these things
	    public CustomPDKExample(String name, String pdkName, int inputNum, int outputNum, double positionx, double positiony){
	        super(name);
	        this.rootProperty.className="${WRKGRP}/"+pdkName;
	        this.setPosition(positionx, positiony);
	        setScannedParameter("");
	        setPdkName(pdkName);
	        setInputPortNumber(inputNum);
	        setOutputPortNumber(outputNum);
	        setScannedParameterValue("");
	        setParameters();
	    }
	    
	    public CustomPDKExample(String name, String pdkName, String parameterName, String parameterValue, double positionx, double positiony){
	        super(name);
	        this.rootProperty.className="${WRKGRP}/"+pdkName;
	        this.setPosition(positionx, positiony);
	        setPdkName(pdkName);
	        setScannedParameter(parameterName);
	        setScannedParameterValue(parameterValue);
	        setInputPortNumber(2);
	        setOutputPortNumber(2);
	        setParameters();

	    }
	    
	    public CustomPDKExample(String name, String pdkName, String parameterName, String parameterValue, 
	    		int inputNum, int outputNum, double positionx, double positiony){
	        super(name);
	        this.rootProperty.className="${WRKGRP}/"+pdkName;
	        this.setPosition(positionx, positiony);
	        setPdkName(pdkName);
	        setInputPortNumber(inputNum);
	        setOutputPortNumber(outputNum);
	        setScannedParameter(parameterName);
	        setScannedParameterValue(parameterValue);
	        setParameters();

	    }


	    // One problem with this method is if the user changes something he/she shouldn't have changed, such as model_instance
	    // may lead to fail to open in OptSim
	    // way to solve that can be set two sets of parameters, one editable, the other is not.
	    // how to commit change to the class is the problem if one uses this method
	    // add an index in OptSimProperty to pass information can be one of the solutions
	    @Override
	    public void setParameters(){
	        this.rootProperty.property.add(new OptSimProperty("model_instance",
	                "com.rsoftdesign.simworks.link.PsiIntParameter", "1"));
	        this.rootProperty.property.add(new OptSimProperty("number_input_ports", //***
	                "com.rsoftdesign.simworks.link.PsiIntParameter", Integer.toString(inputPortNumber)));
	        this.rootProperty.property.add(new OptSimProperty("number_output_ports", //***
	                "com.rsoftdesign.simworks.link.PsiIntParameter",Integer.toString(outputPortNumber)));
	        this.rootProperty.property.add(new OptSimProperty("_vergilRotatePorts",
	                "ptolemy.data.expr.Parameter","90")); // might need to change according to the real pdk look
	        this.rootProperty.property.add(new OptSimProperty("_vergilFlipPortsVertical",
	                "ptolemy.data.expr.Parameter",2));
	        this.rootProperty.property.add(new OptSimProperty("_vergilFlipPortsHorizontal",
	                "ptolemy.data.expr.Parameter",2));
	        this.rootProperty.property.add(new OptSimProperty("_vergilShowParamList",
	                "ptolemy.data.expr.Parameter",2));
	        this.rootProperty.property.add(new OptSimProperty("_vergilParameterFile",
	                "ptolemy.kernel.util.SingletonConfigurableAttribute",3));
	        this.rootProperty.property.add(new OptSimProperty("_vergilIconFile",
	                "ptolemy.kernel.util.StringAttribute","${WRKGRP}/"+pdkName+".svg"));
	        
	        // add "for" loop here if you want more parameters
	        if (!scannedParameter.isEmpty())
	        	this.rootProperty.property.add(new OptSimProperty(scannedParameter,
	                "com.rsoftdesign.simworks.link.PsiDoubleParameter",scannedParameterValue));  //***
	        
	        if (scannedParameter.isEmpty())
	        	this.rootProperty.property.add(new OptSimProperty("FileName",
	                "com.rsoftdesign.simworks.apps.query.parameter.OptSimFileParameter", "${WRKGRP}/"+pdkName+"_optsim.dat"));
	        else
	        	this.rootProperty.property.add(new OptSimProperty("FileName",
		                "com.rsoftdesign.simworks.apps.query.parameter.OptSimFileParameter", "${WRKGRP}/"+pdkName+"_optsim_data"));
	        this.rootProperty.property.add(new OptSimProperty("CommandLine",
	                "com.rsoftdesign.simworks.link.PsiStringParameter","&quot;&quot;"));
	        this.rootProperty.property.add(new OptSimProperty("SignalBandwidth",
	                "com.rsoftdesign.simworks.link.PsiStringParameter","&quot;Wide&quot;"));
	        this.rootProperty.property.add(new OptSimProperty("SignalDomain",
	                "com.rsoftdesign.simworks.link.PsiStringParameter","&quot;Auto&quot;"));
	        this.rootProperty.property.add(new OptSimProperty("parameterList",
	                "com.rsoftdesign.simworks.link.PsiStringParameter","&quot;"+this.scannedParameter+"&quot;"));//***
	        this.rootProperty.property.add(new OptSimProperty("modelDesc",
	                "RSoft Custom PDK Model "+pdkName,1));
	        this.rootProperty.property.add(new OptSimProperty("_vergilPDK",
	                "ptolemy.kernel.util.SingletonConfigurableAttribute",2));
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
	        	propertyString=propertyString+"<doc name=\""+singleProperty.propertyName+"\">RSoft Custom PDK Model "+pdkName+"</doc>\n";
	        	break;
	        case 2:
	        	propertyString = propertyString+"<property name=\""+singleProperty.propertyName+"\" class=\""+
	                    singleProperty.className+"\">\n";	
	        	propertyString = propertyString+"</property>\n";
	        	break;
	        case 3:
	        	propertyString = propertyString+"<property name=\""+singleProperty.propertyName+"\" class=\""+
	                    singleProperty.className+"\">\n";	
	        	propertyString = propertyString+"<configure>"+pdkName+".dta</configure>\n";
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
	        //############## this is specially for pdk file icon setting, remove it when don't use pdk
	        //propertyString = propertyString+"        <property name=\"_iconDescription\" class=\"ptolemy.kernel.util.SingletonConfigurableAttribute\">\r\n" + 
	        //		"            <configure> <svg>  <rect x=\"-20\" y=\"-20\" width=\"40\" height=\"40\" style=\"fill:white;stroke:black;stroke-width:0.65\"></rect>  <polyline points=\"-15.5,-8 -11.5,-8 -9.5,-6 -6.5,-6 -4.5,-8 4.5,-8 6.5,-6 9.5,-6 11.5,-8 15.5,-8 15.5,-4 12.5,-4 10.5,-2 5.5,-2 3.5,-4 -3.5,-4 -5.5,-2 -10.5,-2 -12.5,-4 -15.5,-4 -15.5,-8\" style=\"stroke:#3F9F6A;stroke-width:1.0\"></polyline>  <polyline points=\"-15.5,8 -11.5,8 -9.5,6 -6.5,6 -4.5,8 4.5,8 6.5,6 9.5,6 11.5,8 15.5,8 15.5,4 12.5,4 10.5,2 5.5,2 3.5,4 -3.5,4 -5.5,2 -10.5,2 -12.5,4 -15.5,4 -15.5,8\" style=\"stroke:#3F9F6A;stroke-width:1.0\"></polyline>  <text x=\"4\" style=\"font-size:5; font-family:sanserif; fill:red\" y=\"-12.7\">     AIM  </text></svg> </configure>\r\n" + 
	        //		"        </property>";
	    	propertyString = propertyString+"</entity>\n";
	    }
	    
	    public void setPdkName(String name) {
			this.pdkName = name;
		}
	    
	    public String getPdkName() {
	    	return this.pdkName;
	    }
	    
	    public void setScannedParameter(String parameterName) {
	    	this.scannedParameter=parameterName;
	    }
	    
	    public void setScannedParameterValue(String parameterValue) {
	    	this.scannedParameterValue=parameterValue;
		}
	    
	    public String getScannedParameter() {
	    	return this.scannedParameter;
			
		}
	    
	    public String getScannedParameterValue() {
	    	return this.scannedParameterValue;
			
		}
	    
	    public void setInputPortNumber(int num) {
	    	this.inputPortNumber=num;
		}
	    
	    public void setOutputPortNumber(int num) {
	    	this.outputPortNumber=num;
		}
	    
	    public int getInputPortNumber() {
	    	return this.inputPortNumber;
			
		}
	    
	    public int getOutputPortNumber() {
	    	return this.outputPortNumber;
			
		}
	    
	    
}
