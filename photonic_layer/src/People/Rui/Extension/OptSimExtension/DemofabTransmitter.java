package People.Rui.Extension.OptSimExtension;

import java.util.ArrayList;


/* This code is modified to a non-scanned pdk to make it work */
// This time we put both project and compound component code in this class.
public class DemofabTransmitter extends AbstractOptSimSystem{
    int num, inputPortNum;
    
    static private Double[] laserWavelengths = {1520.5E-9, 1524.4E-9, 1510.0E-9, 1534.1E-9,
    		1520.5E-9, 1524.4E-9, 1510.0E-9, 1534.1E-9, 1520.5E-9, 1524.4E-9, 1510.0E-9, 
    		1534.1E-9, 1520.5E-9, 1524.4E-9, 1510.0E-9, 1534.1E-9};
    static private Double[] pdkRingRadius = {4.2, 4.22, 4.25, 4.27,
    		4.2, 4.22, 4.25, 4.27, 4.2, 4.22, 4.25, 4.27, 
    		4.2, 4.22, 4.25, 4.27};
    
    private String writeFileString;
    private String writeFileStringProj;
    private String linkString;
    private String linkStringProj;
    private String portString;
    
    
    private GlobalParametersCC globalParameters;
    private GlobalParameters   globalParametersProj; // global parameters of project
    private OptMuxComponent    optMuxComponent;
    private ArrayList<CWLaserComponent> cwlasers = new ArrayList<>();
    private ArrayList<SpectrumAnalyzerComponent> spectrumAnalyzerComponents = new ArrayList<>();
    private ArrayList<DemofabRingFilter> ringFilterArray = new ArrayList<>();
    private ArrayList<DemofabStraightWg> waveguideArray = new ArrayList<>();
    private ArrayList<DemofabArc>        arcArray		= new ArrayList<>();
    
    //  Constructor, input numbers of rings, center wavelength, fsr, 
    //  we give it a default setting, the center wavelength is 1.55
    public DemofabTransmitter(int num){
        this.num = num;
        this.globalParameters      = new GlobalParametersCC("Demofab_Transmitter");
        this.globalParametersProj = new GlobalParameters("Demofab_transmitter_proj");
        this.optMuxComponent       = new OptMuxComponent("Mux", num, 320, 320);
        
        for (int i=0; i<num; i++) {
        	cwlasers.add(new CWLaserComponent("CWLaser" + (i + 1),
            		Double.toString(laserWavelengths[i%16]),250.0,240+80*i));
            ringFilterArray.add(new DemofabRingFilter(i+1, 400.0+160.0*i, 400.0, pdkRingRadius[i%16], 1.5+i*0.05));
            arcArray.add(new DemofabArc(i+1, 320.0+160.0*i, 480.0, 6.0, 3.0, 90.0));
            spectrumAnalyzerComponents.add(new SpectrumAnalyzerComponent("Spec"+ (i + 1),
            		330.0+160*i, 560.0));
        }
        	// DemofabRingFilter(Integer index, Double x, Double y, Double radius, Double resonanceWavelength)
        
        for (int i=0; i<num-1; i++)
        	// DemofabStraightWg(Integer index, Double x, Double y, Double length, Double loss, Double width)
            waveguideArray.add(new DemofabStraightWg(i+1, 480.0+160.0*i, 400.0, 30.0, 1.0));
        
        setAllParameters();
        setPortString(1, num);
        setLinkString();
        setLinkStringProj();
        setWriteFileString();  
        setWriteFileStringProj();
    }

    // Each time you change something in the table, you need to change here
    // Set up all $propertyString$.
    // these are the same for compound component and project
    public void setAllParameters() {
    	globalParameters.setWriteFilePropertyString();
    	globalParametersProj.setWriteFilePropertyString();
    	optMuxComponent.setWriteFilePropertyString();
    	for (DemofabRingFilter ring: ringFilterArray)
    		ring.setWriteFilePropertyString();
    	for (DemofabStraightWg wg:waveguideArray)
    		wg.setWriteFilePropertyString();
    	for (DemofabArc arc:arcArray)
    		arc.setWriteFilePropertyString();
    	for (CWLaserComponent cw: cwlasers)
    		cw.setWriteFilePropertyString();
    	for (SpectrumAnalyzerComponent spec: spectrumAnalyzerComponents)
    		spec.setWriteFilePropertyString();
    }
    
    public void setWriteFileString() {
    	setAllParameters();
    	writeFileString="";
    	writeFileString = writeFileString + globalParameters.propertyString;
    	writeFileString = writeFileString + portString;
    	// you need to update all the components before write
    	for (DemofabRingFilter ring: ringFilterArray)
    		writeFileString = writeFileString + ring.getPropertyString();
    	for (DemofabStraightWg wg: waveguideArray)
    		writeFileString = writeFileString + wg.getPropertyString();
    	for (DemofabArc arc:arcArray)
    		writeFileString = writeFileString + arc.getPropertyString();
    	
    	writeFileString = writeFileString + linkString;
    	writeFileString = writeFileString + "</class>\n";
    }
    
    public void setWriteFileStringProj() {
    	setAllParameters();
    	writeFileStringProj="";
    	writeFileStringProj = writeFileStringProj + globalParametersProj.propertyString;
    	writeFileStringProj = writeFileStringProj + optMuxComponent.propertyString;
    	// you need to update all the components before write
    	for (DemofabRingFilter ring: ringFilterArray)
    		writeFileStringProj = writeFileStringProj + ring.getPropertyString();
    	for (DemofabStraightWg wg: waveguideArray)
    		writeFileStringProj = writeFileStringProj + wg.getPropertyString();
    	for (DemofabArc arc:arcArray)
    		writeFileStringProj = writeFileStringProj + arc.getPropertyString();
    	for (CWLaserComponent laser: cwlasers)
    		writeFileStringProj = writeFileStringProj + laser.propertyString;
    	for (SpectrumAnalyzerComponent spec: spectrumAnalyzerComponents)
    		writeFileStringProj = writeFileStringProj + spec.propertyString;
    	
    	
    	writeFileStringProj = writeFileStringProj + linkStringProj;
    	writeFileStringProj = writeFileStringProj + "</entity>\n";
    }
    
    public void setPortString(int inPortNum, int outPortNum) {
    	portString = "";
    	for (int i=0;i<inPortNum;i++) { // configure inPortNum
    		portString = portString+
    				"    <port name=\"Input"+ (i + 1) +"\" class=\"ptolemy.actor.TypedIOPort\">\r\n" +
    				"        <property name=\"input\"/>\r\n" + 
    				"        <property name=\"_location\" class=\"ptolemy.kernel.util.Location\" value=\""+ 75 +","+
                    (125 + 80 * i) +"\">\r\n" +
    				"        </property>\r\n" + 
    				"        <property name=\"_vergil_ID\" class=\"ptolemy.kernel.util.SingletonConfigurableAttribute\">\r\n" + 
    				"            <configure>"+ (i + 2) +"</configure>\r\n" +
    				"        </property>\r\n" + 
    				"        <property name=\"_vergilHiddenPort\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"Output_"+ (i + 1)
    				+"\">\r\n"                + 
    				"        </property>\r\n" + 
    				"    </port>\n";
    	}
    	
    	for (int i=0; i<outPortNum; i++) { // inPortNum+outPortNum
    		portString = portString+
    				"    <port name=\"Output"+ (i + 1) +"\" class=\"ptolemy.actor.TypedIOPort\">\r\n" +
    				"        <property name=\"output\"/>\r\n" + 
    				"        <property name=\"multiport\"/>\r\n" + 
    				"        <property name=\"_location\" class=\"ptolemy.kernel.util.Location\" value=\""+ (400 + 160 * i) +","+
                    560 +"\">\r\n" +
    				"        </property>\r\n" + 
    				"        <property name=\"_vergil_ID\" class=\"ptolemy.kernel.util.SingletonConfigurableAttribute\">\r\n" + 
    				"            <configure>"+ (i + 2 + inPortNum) +"</configure>\r\n" +
    				"        </property>\r\n" + 
    				"        <property name=\"_vergilHiddenPort\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"Input_"+ (i + 1) +"\">\r\n" +
    				"        </property>\r\n" + 
    				"    </port>\n";
    	}
		
    	for (int i=0;i<inPortNum;i++) { // configure inPortNum
    		portString = portString+
    				"    <port name=\"Output_"+ (i + 1) +"\" class=\"ptolemy.actor.TypedIOPort\">\r\n" +
    				"        <property name=\"output\"/>\r\n" + 
    				"        <property name=\"multiport\"/>\r\n" + 
    				"        <property name=\"_hide\" class=\"ptolemy.kernel.util.Attribute\">\r\n" + 
    				"        </property>\r\n" + 
    				"        <property name=\"_vergilVisiblePort\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"Input"+ (i + 1) +"\">\r\n" +
    				"        </property>\r\n" + 
    				"    </port>\n";    
    	}
    	
    	for (int i=0; i<outPortNum; i++) { // inPortNum+outPortNum
    		portString = portString+
    				"    <port name=\"Input_"+ (i + 1) +"\" class=\"ptolemy.actor.TypedIOPort\">\r\n" +
    				"        <property name=\"input\"/>\r\n" + 
    				"        <property name=\"_hide\" class=\"ptolemy.kernel.util.Attribute\">\r\n" + 
    				"        </property>\r\n" + 
    				"        <property name=\"_vergilVisiblePort\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"Output"+ (i + 1) +"\">\r\n" +
    				"        </property>\r\n" + 
    				"    </port>\n";
    	}
    	
	}
    
    // Manually set link connections
    // After version 2019.3, one have to set the connection type
    public void setLinkString() {
    	linkString="";
    	// here we would set all link type to hard, which means DO NOT auto-routing.
    	for (int i=0; i<8*num-2; i++) {// 
    		linkString = linkString +
    				"    <relation name=\"link" + (i + 1) + "\" class=\"ptolemy.actor.TypedIORelation\">\r\n" +
    		        "    <property name=\"connectionType\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"Hard\">\n" +
                    "    </property>\n";
    		
    		linkString = linkString +
    				"    </relation>\n";
    	}
    	
    	// Link input and ring
		linkString = linkString + 
				"    <link port=\"Input"+ 1 +"\" relation=\"link"+ 1 +"\"/>\r\n" +
				"    <link port=\"demofabTunableFilter"+ 1 +".in1\" relation=\"link"+ 1 +"\"/>\n" +
				
				"    <link port=\"Output_"+ 1 +"\" relation=\"link"+ 2 +"\"/>\r\n" +
				"    <link port=\"demofabTunableFilter"+ 1 +".in1_hidden\" relation=\"link"+ 2 +"\"/>\n";
		
		
		for (int i=0; i<num; i++) {
			linkString = linkString + 
					"    <link port=\"demofabArc"+ (i + 1) +".in0\" relation=\"link"+ (3 + 2 * i) +"\"/>\r\n" +
					"    <link port=\"demofabTunableFilter"+ (i + 1) +".in0_hidden\" relation=\"link"+ (3 + 2 * i) +"\"/>\n" +
					"    <link port=\"demofabArc"+ (i + 1) +".in0_hidden\" relation=\"link"+ (4 + 2 * i) +"\"/>\r\n" +
					"    <link port=\"demofabTunableFilter"+ (i + 1) +".in0\" relation=\"link"+ (2 * i + 4) +"\"/>\n";
		}
		
		
		
		// Link ring -> waveguide, link 2*num+2*i+3 and 2*num+2*i+4
		for (int i=0; i<num-1; i++) {
			linkString = linkString + 
					"    <link port=\"demofabStraight"+ (i + 1) +".in0_hidden\" relation=\"link"+ (2 * num + 2 * i + 3) +"\"/>\r\n" +
					"    <link port=\"demofabTunableFilter"+ (i + 1) +".out1_hidden\" relation=\"link"+ (2 * num + 2 * i + 3) +"\"/>\n" +
					"    <link port=\"demofabStraight"+ (i + 1) +".in0\" relation=\"link"+ (2 * num + 2 * i + 4) +"\"/>\r\n" +
					"    <link port=\"demofabTunableFilter"+ (i + 1) +".out1\" relation=\"link"+ (2 * num + 2 * i + 4) +"\"/>\n";
		}
		
		// link waveguide -> ring, link 4*num+1+2*i and 4*num+2+2*i
		for (int i=0; i<num-1; i++) {
			linkString = linkString + 
					"    <link port=\"demofabStraight"+ (i + 1) +".out0\" relation=\"link"+ (4 * num + 1 + 2 * i) +"\"/>\r\n" +
					"    <link port=\"demofabTunableFilter"+ (i + 2) +".in1\" relation=\"link"+ (4 * num + 1 + 2 * i) +"\"/>\n" +
					"    <link port=\"demofabStraight"+ (i + 1) +".out0_hidden\" relation=\"link"+ (4 * num + 2 + 2 * i) +"\"/>\r\n" +
					"    <link port=\"demofabTunableFilter"+ (i + 2) +".in1_hidden\" relation=\"link"+ (4 * num + 2 + 2 * i) +"\"/>\n";
		}
    	
		// link arc -> output, start from 6*num-1
		for (int i=0; i<num; i++) {
			linkString = linkString + 
					"    <link port=\"Output"+ (i + 1) +"\" relation=\"link"+ (6 * num - 1 + 2 * i) +"\"/>\r\n" +
					"    <link port=\"demofabArc"+ (i + 1) +".out0\" relation=\"link"+ (6 * num - 1 + 2 * i) +"\"/>\n" +
					"    <link port=\"Input_"+ (i + 1) +"\" relation=\"link"+ (6 * num + 2 * i) +"\"/>\r\n" +
					"    <link port=\"demofabArc"+ (i + 1) +".out0_hidden\" relation=\"link"+ (2 * i + 6 * num) +"\"/>\n";
		}
    }
    
    public void setLinkStringProj() {
    	linkStringProj="";
    	// First calculate how many links are there
    	// cw   -> mux  :  num
    	// mux  -> ring1:  2
    	// ring -> wg   :  2*num - 2
    	// wg   -> ring :  2*num - 2
    	// ring -> arc  :  2*num
    	// arc  -> spec :  num
    	
    	// total        :  8*num - 3
    	for (int i=0; i<8*num-3; i++) {// 
    		linkStringProj = linkStringProj +
    				"    <relation name=\"link" + (i + 1) + "\" class=\"ptolemy.actor.TypedIORelation\">\r\n" +
    		        "    <property name=\"connectionType\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"Hard\">\n" +
                    "    </property>\n";
    		
    		linkStringProj = linkStringProj +
    				"    </relation>\n";
    	}
    	
    	// Link mux and ring1
    	linkStringProj = linkStringProj + 
				"    <link port=\"Mux.output\" relation=\"link"+ 1 +"\"/>\r\n" +
				"    <link port=\"demofabTunableFilter"+ 1 +".in1\" relation=\"link"+ 1 +"\"/>\n" ;
		
		
		// Link ring and arc, link 2+2*i and 3+2*i, totally num loop

		for (int i=0; i<num; i++) {
			linkStringProj = linkStringProj + 
					"    <link port=\"demofabArc"+ (i + 1) +".in0\" relation=\"link"+ (2 + 2 * i) +"\"/>\r\n" +
					"    <link port=\"demofabTunableFilter"+ (i + 1) +".in0_hidden\" relation=\"link"+ (2 + 2 * i) +"\"/>\n" +
					"    <link port=\"demofabArc"+ (i + 1) +".in0_hidden\" relation=\"link"+ (3 + 2 * i) +"\"/>\r\n" +
					"    <link port=\"demofabTunableFilter"+ (i + 1) +".in0\" relation=\"link"+ (2 * i + 3) +"\"/>\n";
		}
		
		
		
		// Link ring -> waveguide, link 2*num+2*i+2 and 2*num+2*i+3
		for (int i=0; i<num-1; i++) {
			linkStringProj = linkStringProj + 
					"    <link port=\"demofabStraight"+ (i + 1) +".in0_hidden\" relation=\"link"+ (2 * num + 2 * i + 2) +"\"/>\r\n" +
					"    <link port=\"demofabTunableFilter"+ (i + 1) +".out1_hidden\" relation=\"link"+ (2 * num + 2 * i + 2) +"\"/>\n" +
					"    <link port=\"demofabStraight"+ (i + 1) +".in0\" relation=\"link"+ (2 * num + 2 * i + 3) +"\"/>\r\n" +
					"    <link port=\"demofabTunableFilter"+ (i + 1) +".out1\" relation=\"link"+ (2 * num + 2 * i + 3) +"\"/>\n";
		}
		
		// link waveguide -> ring, link 4*num+2*i and 4*num+1+2*i
		for (int i=0; i<num-1; i++) {
			linkStringProj = linkStringProj + 
					"    <link port=\"demofabStraight"+ (i + 1) +".out0\" relation=\"link"+ (4 * num + 2 * i) +"\"/>\r\n" +
					"    <link port=\"demofabTunableFilter"+ (i + 2) +".in1\" relation=\"link"+ (4 * num + 2 * i) +"\"/>\n" +
					"    <link port=\"demofabStraight"+ (i + 1) +".out0_hidden\" relation=\"link"+ (4 * num + 1 + 2 * i) +"\"/>\r\n" +
					"    <link port=\"demofabTunableFilter"+ (i + 2) +".in1_hidden\" relation=\"link"+ (4 * num + 1 + 2 * i) +"\"/>\n";
		}
    	
		// link arc -> spec, start from 6*num-2
		for (int i=0; i<num; i++) {
			linkStringProj = linkStringProj + 
					"    <link port=\"Spec"+ (i + 1) +".input#1\" relation=\"link"+ (6 * num - 2 + i) +"\"/>\r\n" +
					"    <link port=\"demofabArc"+ (i + 1) +".out0\" relation=\"link"+ (6 * num - 2 + i) +"\"/>\n";
		}
		
		// link cw  -> mux, start from 7*num - 2
		for (int i=0; i<num; i++) {
			linkStringProj = linkStringProj + 
					"    <link port=\"CWLaser"+ (i + 1) +".output#1\" relation=\"link"+ (7 * num - 2 + i) +"\"/>\r\n" +
					"    <link port=\"Mux.input#"+ (i + 1) +"\" relation=\"link"+ (7 * num - 2 + i) +"\"/>\n";
		}
		
    }
    
    public String getPortString() {
    	return portString;
		
	}
    
    public String getLinkString() {
    	return linkString;
		
	}
    
    public String getWriteString() {
    	return writeFileString;
		
	}
    
    public String getLinkStringProj() {
		return linkStringProj;
	}
    
    public String getWriteStringProj() {
		return writeFileStringProj;
	}
    
    public ArrayList<String> getComponentsName() {
    	ArrayList<String> componentsNames = new ArrayList<>();
    	componentsNames.add("Global CC");
    	componentsNames.add("Global Project");
    	componentsNames.add("CW Lasers");
    	componentsNames.add("Mux");
    	componentsNames.add("Ring Filter");
    	componentsNames.add("Waveguide");
    	componentsNames.add("Bend");
    	componentsNames.add("Spectrum Analyzer");
    	return componentsNames;
	}
    
    // setter and getter for global parameters
    public void setGlobalParameters(String name) {
    	globalParameters = new GlobalParametersCC(name);
    }
    
    public void setGlobalParametersProj(String name) {
    	globalParametersProj = new GlobalParameters(name);
    }
    
    public GlobalParametersCC getGlobalParameters() {
    	return globalParameters;
	}

    public GlobalParameters getGlobalParametersProj() {
    	return globalParametersProj;
	}
    
    // setter and getter for cw lasers
    // CWLaserComponent("CWLaser" + Integer.toString(i+1),
	//     Double.toString(laserWavelengths[i%16]),250.0,240+80*i)
    // here index 0~num-1
    public void setCWLasers(Integer index, Double wavelength) {
    	cwlasers.set(index, new CWLaserComponent("CWLaser"+ (index + 1), Double.toString(wavelength), 250.0, 240+80*index));
    }
    
    public CWLaserComponent getCWLaser(Integer i) {
    	return cwlasers.get(i);
	}
    

    // setter and getter for mux
    public void setOptMux(Integer portNum) {
    	optMuxComponent.setPortNum(portNum);
    }
    
    public OptMuxComponent getOptMux() {
    	return optMuxComponent;
	}
    
    // setter and getter for spectrum analyzer
    public void setSpec(Integer index) {
    	spectrumAnalyzerComponents.set(index, new SpectrumAnalyzerComponent("Spec"+ (index + 1),
            		440+160*index, 560));
    }
    
    public SpectrumAnalyzerComponent getSpec(Integer i) {
    	return spectrumAnalyzerComponents.get(i);
	}
    
    // setter and getter for ring filters
    public void setRingFilter(Integer index, Double r, Double resonance) {
    	ringFilterArray.set(index, new DemofabRingFilter(index+1, 400.0+160.0*index, 400.0, r, resonance));
    }
    
    public DemofabRingFilter getRingFilter(Integer i) {
    	return ringFilterArray.get(i);
	}
    
    // setter and getter for waveguides
    public void setWaveguide(Integer index, Double length, Double loss) {
    	waveguideArray.set(index, new DemofabStraightWg(index+1, 480.0+160.0*index, 400.0,length, loss));
    }
    
    public DemofabStraightWg getWaveguide(Integer i) {
    	return waveguideArray.get(i);
	}
    
    // setter and getter for arc
    // DemofabArc(Integer index, Double x, Double y, Double radius, Double loss, Double angle)
    public void setArc(Integer index, Double radius, Double loss, Double angle) {
    	arcArray.set(index, new DemofabArc(index+1, 320.0+160.0*index, 480.0, radius, loss, angle));
    }
    
    public void setArc(Integer index, Double radius, Double loss, Double angle, Double width) {
    	arcArray.set(index, new DemofabArc(index+1, 320.0+160.0*index, 480.0, radius, loss, angle, width));
    }
    
    public DemofabArc getArc(Integer i) {
    	return arcArray.get(i);
	}
    
    public void setEdittedPorperty(int component, int index, int propertyIndex, String newValue) {
    	switch (component) {
    		case 0:
				this.globalParameters.rootProperty.property.get(propertyIndex).setValue(newValue);
				break;
			case 1:
				this.globalParametersProj.rootProperty.property.get(propertyIndex).setValue(newValue);
				break;
		    case 2:
		    	this.cwlasers.get(index).rootProperty.property.get(propertyIndex).setValue(newValue);
		    	break;
    	    case 3:
    			this.optMuxComponent.rootProperty.property.get(propertyIndex).setValue(newValue);
    			break;
    		case 4:
    			this.waveguideArray.get(index).setValue(propertyIndex, newValue);
    			break;
    		case 5:
    			this.arcArray.get(index).setValue(propertyIndex, newValue);
    			break;
    		case 6:
    			this.spectrumAnalyzerComponents.get(index).rootProperty.property.get(propertyIndex).setValue(newValue);
    			break;   		
    		default:
    			break;
    			
    	}
    }
    


}
