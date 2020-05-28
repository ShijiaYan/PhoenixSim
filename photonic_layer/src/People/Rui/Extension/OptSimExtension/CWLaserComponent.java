package People.Rui.Extension.OptSimExtension;

/**
 *  Restore parameters of a CW Laser source.
 */
public class CWLaserComponent extends AbstractPropertyTree {

    String wavelength;
    String powerInWatt;

    // Constructor we should use, to manipulate
    // Name
    // Wavelength
    // power
    // position
    // Finally we all let it automatically generate, but in this stage we need these things
    public CWLaserComponent(String name, String wavelength, double positionx, double positiony){
        super(name);
        this.rootProperty.className="linksim.src.stars.CWLaser";
        this.wavelength = wavelength;
        this.powerInWatt = "1.0E-3";
        this.setPosition(positionx, positiony);
        setParameters();
    }

    // Let's build up the constructor and try
    public CWLaserComponent(String name) {
        super(name);
        this.rootProperty.className="linksim.src.stars.CWLaser";
        this.wavelength = "1.55E-6";
        this.powerInWatt = "1.0E-3";
        this.setPosition(-2800, -240);
        setParameters();
    }
    // Constructor 2
    public CWLaserComponent(){
        super();
        setParameters();
    }


    @Override
    public void setParameters(){
        this.rootProperty.property.add(new OptSimProperty("model_instance",
                "com.rsoftdesign.simworks.link.PsiIntParameter", "1"));
        this.rootProperty.property.add(new OptSimProperty("peakPower",
                "ptolemy.data.expr.Parameter",powerInWatt));
        this.rootProperty.property.add(new OptSimProperty("wavelength",
                "ptolemy.data.expr.Parameter",wavelength));
        this.rootProperty.property.add(new OptSimProperty("mode",
                "ptolemy.data.expr.Parameter","&quot;Single&quot;"));
        this.rootProperty.property.add(new OptSimProperty("multiNodeOutput",
                "ptolemy.data.expr.Parameter","&quot;NO&quot;"));
        this.rootProperty.property.add(new OptSimProperty("noSources",
                "ptolemy.data.expr.Parameter","1"));
        this.rootProperty.property.add(new OptSimProperty("deltaFreq",
                "ptolemy.data.expr.Parameter","0"));
        this.rootProperty.property.add(new OptSimProperty("filename",
                "com.rsoftdesign.simworks.apps.query.parameter.OptSimFileParameter","&quot;&quot;"));
        this.rootProperty.property.add(new OptSimProperty("azimuth",
                "ptolemy.data.expr.Parameter","0.0"));
        this.rootProperty.property.add(new OptSimProperty("ellipticity",
                "ptolemy.data.expr.Parameter","0.0"));
        
        
        this.rootProperty.property.add(new OptSimProperty("force_Ey",
                "ptolemy.data.expr.Parameter", "&quot;no&quot;"));
        this.rootProperty.property.add(new OptSimProperty("linewidth_model",
                "ptolemy.data.expr.Parameter","&quot;none&quot;"));
        this.rootProperty.property.add(new OptSimProperty("linewidth_units",
                "ptolemy.data.expr.Parameter","&quot;frequency&quot;"));
        this.rootProperty.property.add(new OptSimProperty("linewidth",
                "ptolemy.data.expr.Parameter","1.0E8"));
        this.rootProperty.property.add(new OptSimProperty("RIN",
                "ptolemy.data.expr.Parameter","-150.0"));
        this.rootProperty.property.add(new OptSimProperty("signalType",
                "ptolemy.data.expr.Parameter","&quot;TimeSequence&quot;"));
        this.rootProperty.property.add(new OptSimProperty("timeStep",
                "ptolemy.data.expr.Parameter","1/(bitRate * 2^pointsPerBit)"));
        this.rootProperty.property.add(new OptSimProperty("nominalBitRate",
                "ptolemy.data.expr.Parameter","bitRate"));
        this.rootProperty.property.add(new OptSimProperty("noSamples",
                "ptolemy.data.expr.Parameter","noSamples"));
        this.rootProperty.property.add(new OptSimProperty("randomPhase",
                "ptolemy.data.expr.Parameter","&quot;NO&quot;"));
        
        this.rootProperty.property.add(new OptSimProperty("phase",
                "ptolemy.data.expr.Parameter","0.0"));
        this.rootProperty.property.add(new OptSimProperty("phaseSeed",
                "ptolemy.data.expr.Parameter","0"));
        this.rootProperty.property.add(new OptSimProperty("number_output_ports",
                "ptolemy.data.expr.Parameter","1"));
        
        this.rootProperty.property.add(new OptSimProperty("CWLaserDesc"));
        
        this.rootProperty.property.add(new OptSimProperty("_location",
                "ptolemy.kernel.util.Location",position));
        
        this.rootProperty.property.add(new OptSimProperty("output#1",
                "com.rsoftdesign.simworks.link.PsiTypedIOPort"));   
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
        	propertyString=propertyString+"<doc name=\""+singleProperty.propertyName+"\">Multiline CW Laser</doc>\n";
        	break;
        case 2:
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
    

    
}
