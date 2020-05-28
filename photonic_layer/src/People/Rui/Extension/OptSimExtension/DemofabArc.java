package People.Rui.Extension.OptSimExtension;

import java.util.ArrayList;

public class DemofabArc {
	
	private String index;
	private String location;
	private String radius;
	private String width;
	private String angle;
	private String loss_dB_cm;
	
	private String propertyString;
	
	// Constructor should include definition of 
	// * location
	// * radius
	// * resonanceWavelength_um
	// 
	// constructor with width accessible
	public DemofabArc(Integer index, Double x, Double y, Double radius, Double loss, Double angle, Double width) {
		setIndex(index);
		setLocation(x, y);
		setRadius(radius);
		setLoss(loss);
		setAngle(angle);
		setWidth(width);
		setWriteFilePropertyString();
	}
	
	// constructor having default width
	public DemofabArc(Integer index, Double x, Double y, Double radius, Double loss, Double angle) {
		setIndex(index);
		setLocation(x, y);
		setRadius(radius);
		setLoss(loss);
		setAngle(angle);
		width = "mcsGetWidth()";
		setWriteFilePropertyString();
	}
	
	
    public void setWriteFilePropertyString(){
        this.propertyString = 
        		"     <entity name=\"demofabArc"+index+"\" class=\"demofab.v1_0.CC.demofabArc\">\r\n" + 
        		"        <property name=\"model_instance\" class=\"com.rsoftdesign.simworks.link.PsiIntParameter\" value=\"1\">\r\n" + 
        		"        </property>\r\n" + 
        		"        <property name=\"Director Type\" class=\"ptolemy.data.expr.Parameter\" value=\"&quot;None&quot;\">\r\n" + 
        		"        </property>\r\n" + 
        		"        <property name=\"_createdBy\" class=\"ptolemy.kernel.attributes.VersionAttribute\" value=\"OptSim-2018.12-beta2-build_1\">\r\n" + 
        		"        </property>\r\n" + 
        		"        <property name=\"_vergilSize\" class=\"ptolemy.actor.gui.SizeAttribute\" value=\"[1409, 963]\">\r\n" + 
        		"        </property>\r\n" + 
        		"        <property name=\"_vergilGridSize\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"40\">\r\n" + 
        		"        </property>\r\n" + 
        		"        <property name=\"_vergilZoomFactor\" class=\"ptolemy.data.expr.Parameter\" value=\"4.7111294652075\">\r\n" + 
        		"        </property>\r\n" + 
        		"        <property name=\"_vergilCenter\" class=\"ptolemy.data.expr.Parameter\" value=\"{298.539511746143, 348.0485404978962}\">\r\n" + 
        		"        </property>\r\n" + 
        		"        <property name=\"_icon\" class=\"ptolemy.vergil.icon.XMLIcon\">\r\n" + 
        		"        </property>\r\n" + 
        		"        <property name=\"_iconDescription\" class=\"ptolemy.kernel.util.SingletonConfigurableAttribute\">\r\n" + 
        		"            <configure> \r\n" + 
        		"         <svg>\r\n" + 
        		"            <rect height=\"40\" style=\"fill:white;stroke:black;stroke-width:0.65\" width=\"40\" x=\"-20\" y=\"-20\"/>\r\n" + 
        		"            <polyline points=\"13,-13 12.99,-12.17 12.95,-11.33 12.88,-10.5 12.79,-9.675 12.67,-8.85 12.52,-8.03 12.35,-7.214 12.15,-6.405 11.93,-5.602 11.68,-4.807 11.4,-4.021 11.1,-3.243 10.77,-2.476 10.43,-1.719 10.05,-0.974 9.654,-0.2413 9.234,0.4782 8.79,1.184 8.324,1.875 7.837,2.551 7.328,3.211 6.798,3.854 6.247,4.48 5.677,5.088 5.088,5.677 4.48,6.247 3.854,6.798 3.211,7.328 2.551,7.837 1.875,8.324 1.184,8.79 0.4782,9.234 -0.2413,9.654 -0.974,10.05 -1.719,10.43 -2.476,10.77 -3.243,11.1 -4.021,11.4 -4.807,11.68 -5.602,11.93 -6.405,12.15 -7.214,12.35 -8.03,12.52 -8.85,12.67 -9.675,12.79 -10.5,12.88 -11.33,12.95 -12.17,12.99 -13,13 -13,9 -12.29,8.989 -11.59,8.955 -10.89,8.898 -10.19,8.819 -9.489,8.718 -8.795,8.594 -8.105,8.448 -7.42,8.28 -6.74,8.091 -6.068,7.879 -5.402,7.646 -4.744,7.392 -4.095,7.117 -3.455,6.821 -2.824,6.505 -2.204,6.169 -1.595,5.813 -0.9982,5.438 -0.4134,5.044 0.1584,4.631 0.7168,4.2 1.261,3.752 1.791,3.286 2.305,2.804 2.804,2.305 3.286,1.791 3.752,1.261 4.2,0.7168 4.631,0.1584 5.044,-0.4134 5.438,-0.9982 5.813,-1.595 6.169,-2.204 6.505,-2.824 6.821,-3.455 7.117,-4.095 7.392,-4.744 7.646,-5.402 7.879,-6.068 8.091,-6.74 8.28,-7.42 8.448,-8.105 8.594,-8.795 8.718,-9.489 8.819,-10.19 8.898,-10.89 8.955,-11.59 8.989,-12.29 9,-13 13,-13 \" style=\"stroke:#632289;stroke-width:1.0\"/>\r\n" + 
        		"            <text style=\"font-size:5; font-family:sanserif; fill:#632289\" x=\"-6\" y=\"-15\">DEMOFAB</text>\r\n" + 
        		"         </svg> \r\n" + 
        		"      </configure>\r\n" + 
        		"        </property>\r\n" + 
        		"        <property name=\"_vergilProxyIcon\" class=\"ptolemy.kernel.util.SingletonConfigurableAttribute\">\r\n" + 
        		"            <configure>svg/demofab_waveguide_arc.svg</configure>\r\n" + 
        		"        </property>\r\n" + 
        		"        <property name=\"loss_dB_cm\" class=\"ptolemy.data.expr.Parameter\" value=\""+loss_dB_cm+"\">\r\n" + 
        		"        </property>\r\n" + 
        		"        <property name=\"angle\" class=\"ptolemy.data.expr.Parameter\" value=\""+angle+"\">\r\n" + 
        		"        </property>\r\n" + 
        		"        <property name=\"radius\" class=\"ptolemy.data.expr.Parameter\" value=\""+radius+"\">\r\n" + 
        		"        </property>\r\n" + 
        		"        <property name=\"width\" class=\"ptolemy.data.expr.Parameter\" value=\""+width+"\">\r\n" + 
        		"        </property>\r\n" + 
        		"        <property name=\"_vergilPDK\" class=\"ptolemy.kernel.util.StringAttribute\">\r\n" + 
        		"        </property>\r\n" + 
        		"        <property name=\"_vergilPDKModelType\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"demofabArc\">\r\n" + 
        		"        </property>\r\n" + 
        		"        <property name=\"_vergilHybridPDK\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"demofabArc:1\">\r\n" + 
        		"        </property>\r\n" + 
        		"        <property name=\"_vergilPDKName\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"demofabV1.0\">\r\n" + 
        		"        </property>\r\n" + 
        		"        <property name=\"_location\" class=\"ptolemy.kernel.util.Location\" value=\""+location+"\">\r\n" + 
        		"        </property>\r\n" + 
        		"        <property name=\"_vergilRotatePorts\" class=\"ptolemy.data.expr.Parameter\" value=\"180\">\r\n" + 
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
	
	public void setWidth(Double width) {
		this.width = new String(Double.toString(width));
	}
	
	public void setWidth(String width) {
		this.width= width;
	}
	
	public String getWidth() {
		return width;
	}

	public void setAngle(Double angle) {
		this.angle = new String(Double.toString(angle));
	}
	
	public void setAngle(String angle) {
		this.angle= angle;
	}
	
	public String getAngle() {
		return angle;
	}
	
	public void setLoss(Double loss) {
		this.loss_dB_cm = new String(Double.toString(loss));
	}
	
	public void setLoss(String loss) {
		this.loss_dB_cm= loss;
	}
	
	public String getLoss() {
		return loss_dB_cm;
	}
	
	
	public ArrayList<String> getPropertyNames(){
		ArrayList<String> properties = new ArrayList<>();
		properties.add("location");
		properties.add("radius");
		properties.add("angle");
		properties.add("loss");
		properties.add("width");
		return properties;
	}
	
	
	
	public ArrayList<String> getPropertyValues(){
		ArrayList<String> properties = new ArrayList<>();
		properties.add(location);
		properties.add(radius);
		properties.add(angle);
		properties.add(loss_dB_cm);
		properties.add(width);
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
			setAngle(value);
			break;
		case 3:
			setLoss(value);
			break;
		case 4:
			setWidth(value);
			break;
		default:
			break;
		}
	}
	
	
	
	
}
