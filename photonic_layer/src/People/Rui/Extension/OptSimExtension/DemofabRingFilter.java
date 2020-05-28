package People.Rui.Extension.OptSimExtension;

import java.util.ArrayList;

public class DemofabRingFilter {
	
	private String index;
	private String location;
	private String radius;
	private String resonanceWavelength_um;
	
	private String propertyString;
	
	// Constructor should include definition of 
	// * location
	// * radius
	// * resonanceWavelength_um
	// 
	public DemofabRingFilter(Integer index, Double x, Double y, Double radius, Double resonanceWavelength) {
		setIndex(index);
		setLocation(x, y);
		setRadius(radius);
		setResonance(resonanceWavelength);
		setWriteFilePropertyString();
	}
	
	
    public void setWriteFilePropertyString(){
        this.propertyString = 
        		"    <entity name=\"demofabTunableFilter"+ index +"\" class=\"demofab.v1_0.CC.demofabTunableFilter\">\r\n" + 
        		"        <property name=\"model_instance\" class=\"com.rsoftdesign.simworks.link.PsiIntParameter\" value=\"1\">\r\n" + 
        		"        </property>\r\n" + 
        		"        <property name=\"Director Type\" class=\"ptolemy.data.expr.Parameter\" value=\"&quot;None&quot;\">\r\n" + 
        		"        </property>\r\n" + 
        		"        <property name=\"_createdBy\" class=\"ptolemy.kernel.attributes.VersionAttribute\" value=\"OptSim-2018.12-beta2-build_1\">\r\n" + 
        		"        </property>\r\n" + 
        		"        <property name=\"_vergilSize\" class=\"ptolemy.actor.gui.SizeAttribute\" value=\"[1613, 963]\">\r\n" + 
        		"        </property>\r\n" + 
        		"        <property name=\"_vergilGridSize\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"20\">\r\n" + 
        		"        </property>\r\n" + 
        		"        <property name=\"_icon\" class=\"ptolemy.vergil.icon.XMLIcon\">\r\n" + 
        		"        </property>\r\n" + 
        		"        <property name=\"_iconDescription\" class=\"ptolemy.kernel.util.SingletonConfigurableAttribute\">\r\n" + 
        		"            <configure>\r\n" + 
        		"            <svg>\r\n" + 
        		"            <rect height=\"40\" style=\"fill:white;stroke:black;stroke-width:0.65\" width=\"40\" x=\"-20\" y=\"-20\"/>\r\n" + 
        		"            <rect height=\"3\" style=\"fill:white;stroke:#632289;stroke-width:1\" width=\"31\" x=\"-15.5\" y=\"-17\"/>\r\n" + 
        		"            <circle cx=\"0\" cy=\"0\" r=\"12\" style=\"fill:white;stroke:#632289;stroke-width:1\"/>\r\n" + 
        		"            <rect height=\"3\" style=\"fill:white;stroke:#632289;stroke-width:1\" width=\"31\" x=\"-15.5\" y=\"14\"/>\r\n" + 
        		"            <line style=\"fill:none;stroke:#632289;stroke-width:1.0\" x1=\"-8.5\" x2=\"-5\" y1=\"-8\" y2=\"-8\"/>\r\n" + 
        		"            <line style=\"fill:none;stroke:#632289;stroke-width:1.0\" x1=\"-8.5\" x2=\"-5\" y1=\"8\" y2=\"8\"/>\r\n" + 
        		"            <line style=\"fill:none;stroke:#632289;stroke-width:1.0\" x1=\"-5\" x2=\"-5\" y1=\"-8\" y2=\"-4.5\"/>\r\n" + 
        		"            <line style=\"fill:none;stroke:#632289;stroke-width:1.0\" x1=\"-5\" x2=\"-5\" y1=\"4.5\" y2=\"8\"/>\r\n" + 
        		"            <polyline points=\"-5,-4.5 -2.5,-3.75 -5,-3.0 -7.5,-2.25 -5,-1.5 -2.5,-0.75 -5,0 -7.5,0.75 -5,1.5 -2.5,2.25 -5,3.0 -7.5,3.75 -5,4.5\" style=\"stroke:#632289;stroke-width:1.0\"/>\r\n" + 
        		"            <text style=\"font-size:5; font-family:sanserif; fill:#632289\" x=\"-6\" y=\"-9\">DEMOFAB</text>\r\n" + 
        		"         </svg>\r\n" + 
        		"        </configure>\r\n" + 
        		"        </property>\r\n" + 
        		"        <property name=\"_vergilProxyIcon\" class=\"ptolemy.kernel.util.SingletonConfigurableAttribute\">\r\n" + 
        		"            <configure>svg/demofab_ring_tunable_si_lambdaX.svg</configure>\r\n" + 
        		"        </property>\r\n" + 
        		"        <property name=\"_vergilZoomFactor\" class=\"ptolemy.data.expr.Parameter\" value=\"2.5940569439264\">\r\n" + 
        		"        </property>\r\n" + 
        		"        <property name=\"_vergilCenter\" class=\"ptolemy.data.expr.Parameter\" value=\"{514.7998705904909, 444.9158119248466}\">\r\n" + 
        		"        </property>\r\n" + 
        		"        <property name=\"L\" class=\"ptolemy.data.expr.Parameter\" value=\"2*pi*radius\">\r\n" + 
        		"        </property>\r\n" + 
        		"        <property name=\"radius\" class=\"ptolemy.data.expr.Parameter\" value=\"" + radius + "\">\r\n" + 
        		"        </property>\r\n" + 
        		"        <property name=\"resonanceWavelength_um\" class=\"ptolemy.data.expr.Parameter\" value=\"" + resonanceWavelength_um + "\">\r\n" + 
        		"        </property>\r\n" + 
        		"        <property name=\"_vergilPDK\" class=\"ptolemy.kernel.util.StringAttribute\">\r\n" + 
        		"        </property>\r\n" + 
        		"        <property name=\"_vergilPDKModelType\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"demofabTunableFilter\">\r\n" + 
        		"        </property>\r\n" + 
        		"        <property name=\"_vergilHybridPDK\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"demofabTunableFilter:1\">\r\n" + 
        		"        </property>\r\n" + 
        		"        <property name=\"_vergilPDKName\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"demofabV1.0\">\r\n" + 
        		"        </property>\r\n" + 
        		"        <property name=\"_location\" class=\"ptolemy.kernel.util.Location\" value=\"" + location +"\">\r\n" + 
        		"        </property>\r\n" + 
        		"    </entity>\n";
    }
	
	public void setIndex(Integer index) {
		this.index = new String(Integer.toString(index));
	}
    	
	public String getIndex() {
		return index;
	}
	
    public String getPropertyString() {
    	return propertyString;
    }
	
	public void setLocation(Double x, Double y) {
    	String xPosition = new String(Double.toString(x));
    	String yPosition = new String(Double.toString(y));
    	this.location = xPosition+", "+yPosition;
	}
	
	public void setLocation(String loc) {
		location = loc;
	}
	
	public String getLocation() {
		return location;
	}
	
	public void setRadius(Double r) {
		this.radius = new String(Double.toString(r));
	}
	
	public void setRadius(String r) {
		this.radius = r;
	}
	
	public String getRadius() {
		return radius;
	}
	
	public void setResonance(Double wavelength) {
		this.resonanceWavelength_um = new String(Double.toString(wavelength));
	}
	
	public void setResonance(String wavelength) {
		this.resonanceWavelength_um = wavelength;
	}
	
	public String getResonance() {
		return resonanceWavelength_um;
	}
	
	public ArrayList<String> getPropertyNames(){
		ArrayList<String> properties = new ArrayList<>();
		properties.add("location");
		properties.add("radius");
		properties.add("resonance_um");
		return properties;
	}
	
	public ArrayList<String> getPropertyValues(){
		ArrayList<String> properties = new ArrayList<>();
		properties.add(location);
		properties.add(radius);
		properties.add(resonanceWavelength_um);
		return properties;
	}
	
	// set value according to property index and value
	
	public void setValue(Integer index, String value){
		switch (index) {
		case 0:
			setLocation(value);
			break;
		case 1:
			setRadius(value);
			break;
		case 2:
			setResonance(value);
			break;
		default:
			break;
		}
	}
	
	
	
	
}
