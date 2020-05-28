package People.Rui.Extension.OptSimExtension;

import java.util.ArrayList;
// Notice this part doesn't show up in table. You will need to modify the code to make the user can change the data in table
// but we will start from 
/* This code is modified to a non-scanned pdk to make it work */
public class PDKReceiverCC extends AbstractOptSimSystem{
    int num, inputPortNum;
    public String writeFileString;
    public String linkString;
    public String portString;
    
    
    static private String pdkRingName = "ScanRingPDK_v3";
    // static private String pdkRingName = "ring_pdk_1v2";
    
    static private String pdkWaveguideName = "testWaveguidePDK_v2";
    //static private String pdkWaveguideName = "wg_v4";
    public GlobalParametersCC globalParameters;
    public ArrayList<CustomPDKExample> pdkRing = new ArrayList<>();
    public ArrayList<CustomPDKExample> pdkWaveguide = new ArrayList<>();



    //  Constructor, input numbers of rings, center wavelength, fsr, 
    //  we give it a default setting, the center wavelength is 1.55
    public PDKReceiverCC(int num){
        this.num = num;
        this.globalParameters = new GlobalParametersCC("pdk_Receiver");
        // set up pdk components
        // for (int i=0; i<num; i++)
            //pdkRing.add(new CustomPDKExample("pdkRing"+ Integer.toString(i), pdkRingName, 
    	    //		2, 2,380.0+i*160 , 320.0));
        
        //	pdkRing.add(new CustomPDKExample("pdkRing"+ Integer.toString(i), pdkRingName, String parameterValue, 
    	//    		int inputNum, int outputNum, double positionx, double positiony);
        //for (int i=0; i<num-1; i++)
        //    pdkWaveguide.add(new CustomPDKExample("pdkWg"+ Integer.toString(i), pdkWaveguideName,
    	//    		1, 1,460.0+i*160 , 320.0));
        for (int i=0; i<num; i++)
        	pdkRing.add(new CustomPDKExample("pdkRing"+ i, pdkRingName, "r", "4.25",
	    		2, 2,440.0+i*160 , 320.0));
    
        for (int i=0; i<num-1; i++)
        	pdkWaveguide.add(new CustomPDKExample("pdkWg"+ i, pdkWaveguideName, "L", "20",
	    		1, 1,520.0+i*160 , 320.0));
        
        setAllParameters();
        setPortString(1, num);
        setLinkString();
        setWriteFileString();  
    }

    // When set up a new system, remember to set up it here
    public void setAllParameters() {
    	globalParameters.setWriteFilePropertyString();
    	for (CustomPDKExample ring: pdkRing)
    		ring.setWriteFilePropertyString();
    	for (CustomPDKExample wg:pdkWaveguide)
    		wg.setWriteFilePropertyString();
    	
    }
    
    public void setWriteFileString() {
    	setAllParameters();
    	writeFileString="";
    	writeFileString = writeFileString + globalParameters.propertyString;
    	writeFileString = writeFileString + portString;
    	// you need to update all the components before write
    	for (CustomPDKExample ring: pdkRing)
    		writeFileString = writeFileString + ring.propertyString;
    	for (CustomPDKExample wg: pdkWaveguide)
    		writeFileString = writeFileString + wg.propertyString;

    	writeFileString = writeFileString + linkString;
    	writeFileString = writeFileString + "</class>\n";

    }
    
    
    
    public void setPortString(int inPortNum, int outPortNum) {
    	portString = "";
    	for (int i=0;i<inPortNum;i++) { // configure inPortNum
    		portString = portString+"    <port name=\"Input"+ (i + 1) +"\" class=\"ptolemy.actor.TypedIOPort\">\r\n" +
    				"        <property name=\"input\"/>\r\n" + 
    				"        <property name=\"_location\" class=\"ptolemy.kernel.util.Location\" value=\""+ 75 +","+
                    (125 + 80 * i) +"\">\r\n" +
    				"        </property>\r\n" + 
    				"        <property name=\"_vergil_ID\" class=\"ptolemy.kernel.util.SingletonConfigurableAttribute\">\r\n" + 
    				"            <configure>"+ (i + 2) +"</configure>\r\n" +
    				"        </property>\r\n" + 
    				"        <property name=\"_vergilHiddenPort\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"Output_"+ (i + 1)
    				+"\">\r\n" + 
    				"        </property>\r\n" + 
    				"    </port>\n";
    	}
    	
    	for (int i=0; i<outPortNum; i++) { // inPortNum+outPortNum
    		portString = portString+"    <port name=\"Output"+ (i + 1) +"\" class=\"ptolemy.actor.TypedIOPort\">\r\n" +
    				"        <property name=\"output\"/>\r\n" + 
    				"        <property name=\"multiport\"/>\r\n" + 
    				"        <property name=\"_location\" class=\"ptolemy.kernel.util.Location\" value=\""+ (400 + 160 * i) +","+
                    480 +"\">\r\n" +
    				"        </property>\r\n" + 
    				"        <property name=\"_vergil_ID\" class=\"ptolemy.kernel.util.SingletonConfigurableAttribute\">\r\n" + 
    				"            <configure>"+ (i + 2 + inPortNum) +"</configure>\r\n" +
    				"        </property>\r\n" + 
    				"        <property name=\"_vergilHiddenPort\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"Input_"+ (i + 1) +"\">\r\n" +
    				"        </property>\r\n" + 
    				"    </port>\n";
    	}
		
    	for (int i=0;i<inPortNum;i++) { // configure inPortNum
    		portString = portString+"    <port name=\"Output_"+ (i + 1) +"\" class=\"ptolemy.actor.TypedIOPort\">\r\n" +
    				"        <property name=\"output\"/>\r\n" + 
    				"        <property name=\"multiport\"/>\r\n" + 
    				"        <property name=\"_hide\" class=\"ptolemy.kernel.util.Attribute\">\r\n" + 
    				"        </property>\r\n" + 
    				"        <property name=\"_vergilVisiblePort\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"Input"+ (i + 1) +"\">\r\n" +
    				"        </property>\r\n" + 
    				"    </port>\n";    
    	}
    	
    	for (int i=0; i<outPortNum; i++) { // inPortNum+outPortNum
    		portString = portString+"    <port name=\"Input_"+ (i + 1) +"\" class=\"ptolemy.actor.TypedIOPort\">\r\n" +
    				"        <property name=\"input\"/>\r\n" + 
    				"        <property name=\"_hide\" class=\"ptolemy.kernel.util.Attribute\">\r\n" + 
    				"        </property>\r\n" + 
    				"        <property name=\"_vergilVisiblePort\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"Output"+ (i + 1) +"\">\r\n" +
    				"        </property>\r\n" + 
    				"    </port>\n";
    	}
    	
	}
    
    
    // manually set link connections
    // after version 2019.3, one have to set the connection type
    public void setLinkString() {
    	linkString="";
    	// here we would set all link type to hard, which means DO NOT auto-routing.
    	for (int i=0;i<6*num-2;i++) {// 
    		linkString = linkString +
    				"    <relation name=\"link" + (i + 1) + "\" class=\"ptolemy.actor.TypedIORelation\">\r\n" +
    		        "    <property name=\"connectionType\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"Hard\">\n" +
                    "    </property>\n";
    		linkString = linkString +"    </relation>\n";
    	}
    	
    	// link input and ring, link 1 and 2(they must be nearby
		linkString = linkString + 
				"    <link port=\"Input"+ 1 +"\" relation=\"link"+ 1 +"\"/>\r\n" +
				"    <link port=\"pdkRing"+ 0 +".input#1\" relation=\"link"+ 1 +"\"/>\n" +
						"    <link port=\"Output_"+ 1 +"\" relation=\"link"+ 2 +"\"/>\r\n" +
						"    <link port=\"pdkRing"+ 0 +".output#1\" relation=\"link"+ 2 +"\"/>\n";
		
		// link ring and output, link 3+2*i and 4+2*i, totally num loop
		for (int i=0; i<num; i++) {
			linkString = linkString + 
					"    <link port=\"Output"+ (i + 1) +"\" relation=\"link"+ (3 + 2 * i) +"\"/>\r\n" +
					"    <link port=\"pdkRing"+ i +".output#2\" relation=\"link"+ (3 + 2 * i) +"\"/>\n" +
							"    <link port=\"Input_"+ (i + 1) +"\" relation=\"link"+ (4 + 2 * i) +"\"/>\r\n" +
							"    <link port=\"pdkRing"+ i +".input#2\" relation=\"link"+ (2 * i + 4) +"\"/>\n";
		}
		
		// link ring -> waveguide, link 2*num+2*i+3 and 2*num+2*i+4
		for (int i=0; i<num-1; i++) {
			linkString = linkString + 
					"    <link port=\"pdkWg"+ i +".input#1\" relation=\"link"+ (2 * num + 2 * i + 3) +"\"/>\r\n" +
					"    <link port=\"pdkRing"+ i +".output#4\" relation=\"link"+ (2 * num + 2 * i + 3) +"\"/>\n" +
							"    <link port=\"pdkWg"+ i +".output#1\" relation=\"link"+ (2 * num + 2 * i + 4) +"\"/>\r\n" +
							"    <link port=\"pdkRing"+ i +".input#4\" relation=\"link"+ (2 * num + 2 * i + 4) +"\"/>\n";
		}
		
		// link waveguide -> ring, link 4*num+1+2*i and 4*num+2+2*i
		for (int i=0; i<num-1; i++) {
			linkString = linkString + 
					"    <link port=\"pdkWg"+ i +".output#2\" relation=\"link"+ (4 * num + 1 + 2 * i) +"\"/>\r\n" +
					"    <link port=\"pdkRing"+ (i + 1) +".input#1\" relation=\"link"+ (4 * num + 1 + 2 * i) +"\"/>\n" +
							"    <link port=\"pdkWg"+ i +".input#2\" relation=\"link"+ (4 * num + 2 + 2 * i) +
                    "\"/>\r\n" +
							"    <link port=\"pdkRing"+ (i + 1) +".output#1\" relation=\"link"+ (4 * num + 2 + 2 * i) +"\"/>\n";
		}
    	
    	
    }
    

    public void setEdittedPorperty(int component, int index, int propertyIndex, String newValue) {
    	switch (component) {
    		case 3:
    			this.pdkRing.get(index).rootProperty.property.get(propertyIndex).setValue(newValue);
    			break;
    		case 5:
    			this.pdkWaveguide.get(index).rootProperty.property.get(propertyIndex).setValue(newValue);
    			break;
    		default:
    			break;
    			
    	}
    }


}
