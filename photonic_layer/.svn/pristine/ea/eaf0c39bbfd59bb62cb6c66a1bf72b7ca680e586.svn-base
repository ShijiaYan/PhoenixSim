package GDS.Elements.Shapes.PathElements;

import GDS.Elements.Positioning.Port;
import GDS.Elements.Positioning.Position;
import GDS.PDK.AbstractLayerMap;
import ch.epfl.general_libraries.utils.SimpleMap;

import java.util.Map;

public abstract class AbstractPathElement {
		
	public Port port1 = new Port(new Position(0,0), 0, 180) ;
	public Port port2 = port1.connect() ; // each path element has exactly two port
	
	public static int numElements = 0 ;
	public static Map<String, Port> elementPorts = new SimpleMap<String, Port>() ;
	
	public abstract String[] getPythonCode(String pathName, AbstractLayerMap layerMap) ;
	public abstract String[] getPythonCode_no_header(String pathName, AbstractLayerMap layerMap) ;
	public abstract String getElementName() ;
	
}
