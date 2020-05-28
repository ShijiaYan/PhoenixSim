package People.Rui.Extension.OptSimExtension;

import java.util.ArrayList;

public class DemofabStraightWg {
	
	private String index;
	private String location;
	private String length;
	private String loss_dB_cm;
	private String width;
	
	private String propertyString;
	
	// Constructor should include definition of 
	// * location
	// * radius
	// * resonanceWavelength_um
	// 
	public DemofabStraightWg(Integer index, Double x, Double y, Double length, Double loss, Double width) {
		setIndex(index);
		setLocation(x, y);
		setLength(length);
		setLoss(loss);
		setWidth(width);
		setWriteFilePropertyString();
	}
	
	public DemofabStraightWg(Integer index, Double x, Double y, Double length, Double loss) {
		setIndex(index);
		setLocation(x, y);
		setLength(length);
		setLoss(loss);
		width = "mcsGetWidth()";
		setWriteFilePropertyString();
	}
	
	
    public void setWriteFilePropertyString(){
        this.propertyString = 
        		"    <entity name=\"demofabStraight"+index+"\" class=\"demofab.v1_0.CC.demofabStraight\">\r\n" + 
        		"        <property name=\"model_instance\" class=\"com.rsoftdesign.simworks.link.PsiIntParameter\" value=\"1\">\r\n" + 
        		"        </property>\r\n" + 
        		"        <property name=\"Director Type\" class=\"ptolemy.data.expr.Parameter\" value=\"&quot;None&quot;\">\r\n" + 
        		"        </property>\r\n" + 
        		"        <property name=\"_createdBy\" class=\"ptolemy.kernel.attributes.VersionAttribute\" value=\"OptSim-2018.12-beta2-build_1\">\r\n" + 
        		"        </property>\r\n" + 
        		"        <property name=\"_vergilSize\" class=\"ptolemy.actor.gui.SizeAttribute\" value=\"[1613, 963]\">\r\n" + 
        		"        </property>\r\n" + 
        		"        <property name=\"_vergilGridSize\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"40\">\r\n" + 
        		"        </property>\r\n" + 
        		"        <property name=\"_vergilZoomFactor\" class=\"ptolemy.data.expr.Parameter\" value=\"4.9640732809898\">\r\n" + 
        		"        </property>\r\n" + 
        		"        <property name=\"_vergilCenter\" class=\"ptolemy.data.expr.Parameter\" value=\"{326.0850028757669, 323.7031968941718}\">\r\n" + 
        		"        </property>\r\n" + 
        		"        <property name=\"length\" class=\"ptolemy.data.expr.Parameter\" value=\""+ length +"\">\r\n" + 
        		"        </property>\r\n" + 
        		"        <property name=\"_icon\" class=\"ptolemy.vergil.icon.XMLIcon\">\r\n" + 
        		"        </property>\r\n" + 
        		"        <property name=\"_iconDescription\" class=\"ptolemy.kernel.util.SingletonConfigurableAttribute\">\r\n" + 
        		"            <configure> \r\n" + 
        		"         <svg>\r\n" + 
        		"            <rect height=\"40\" style=\"fill:white;stroke:black;stroke-width:0.65\" width=\"40\" x=\"-20\" y=\"-20\"/>\r\n" + 
        		"            <rect height=\"7\" style=\"fill:white;stroke:#632289;stroke-width:1\" width=\"31\" x=\"-15.5\" y=\"-3.5\"/>\r\n" + 
        		"            <text style=\"font-size:5; font-family:sanserif; fill:#632289\" x=\"-6\" y=\"-15\">DEMOFAB</text>\r\n" + 
        		"         </svg> \r\n" + 
        		"      </configure>\r\n" + 
        		"        </property>\r\n" + 
        		"        <property name=\"_vergilProxyIcon\" class=\"ptolemy.kernel.util.SingletonConfigurableAttribute\">\r\n" + 
        		"            <configure>svg/demofab_waveguide.svg</configure>\r\n" + 
        		"        </property>\r\n" + 
        		"        <property name=\"loss_dB_cm\" class=\"ptolemy.data.expr.Parameter\" value=\""+loss_dB_cm+"\">\r\n" + 
        		"        </property>\r\n" + 
        		"        <property name=\"width\" class=\"ptolemy.data.expr.Parameter\" value=\""+width+"\">\r\n" + 
        		"        </property>\r\n" + 
        		"        <property name=\"_vergilPDK\" class=\"ptolemy.kernel.util.StringAttribute\">\r\n" + 
        		"        </property>\r\n" + 
        		"        <property name=\"_vergilPDKModelType\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"demofabStraight\">\r\n" + 
        		"        </property>\r\n" + 
        		"        <property name=\"_vergilHybridPDK\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"demofabStraight:1\">\r\n" + 
        		"        </property>\r\n" + 
        		"        <property name=\"_vergilPDKName\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"demofabV1.0\">\r\n" + 
        		"        </property>\r\n" + 
        		"        <property name=\"_location\" class=\"ptolemy.kernel.util.Location\" value=\""+location+"\">\r\n" + 
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
		location=loc;
	}
	
	public String getLocation() {
		return location;
	}
	
	public void setLength(Double r) {
		this.length = new String(Double.toString(r));
	}
	
	public void setLength(String l) {
		length = l;
		
	}
	
	public String getLength() {
		return length;
	}
	
	public void setLoss(Double loss) {
		this.loss_dB_cm = new String(Double.toString(loss));
	}
	
	public void setLoss(String loss) {
		loss_dB_cm=loss;
	}
	
	public String getLoss() {
		return loss_dB_cm;
	}
	
	public void setWidth(Double width) {
		this.width = new String(Double.toString(width));
	}
	
	public void setWidth(String width) {
		this.width = width;
	}
	
	public String getWidth() {
		return width;
	}
	
	public ArrayList<String> getPropertyNames(){
		ArrayList<String> properties = new ArrayList<>();
		properties.add("location");
		properties.add("length");
		properties.add("width");
		properties.add("loss_db_cm");
		return properties;
	}
	
	public ArrayList<String> getPropertyValues(){
		ArrayList<String> properties = new ArrayList<>();
		properties.add(location);
		properties.add(length);
		properties.add(width);
		properties.add(loss_dB_cm);
		return properties;
	}
	
	public void setValue(Integer index, String value){
		switch (index) {
		case 0:
			setLocation(value);
			break;
		case 1:
			setLength(value);
			break;
		case 2:
			setWidth(value);
			break;
		case 3:
			setLoss(value);
			break;
		default:
			break;
		}
	}
	
}
