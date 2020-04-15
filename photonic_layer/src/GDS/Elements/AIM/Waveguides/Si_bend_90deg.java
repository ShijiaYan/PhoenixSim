package GDS.Elements.AIM.Waveguides;

import GDS.Elements.AbstractElement;
import GDS.Elements.BasicElements.CurvedWg;
import GDS.Elements.Positioning.Port;
import GDS.Elements.Positioning.Position;
import GDS.PDK.AIMLayerMap.SiliconLevelMasks.SEAM;
import GDS.PDK.AbstractLayerMap;
import PhotonicElements.Utilities.MathLibraries.MoreMath;
import ch.epfl.general_libraries.clazzes.ParamName;
import flanagan.io.FileOutput;

public class Si_bend_90deg extends AbstractElement {

	/**
	 * 90 degree bend: radius = 4.8 um (from center to the middle of waveguide) and width = 0.4 um
	 */
	
	Position P1, P2 ;
	public Port port1, port2 ;
	Position center;
	double radius_um = 4.8 ;
	double width_um = 0.4 ;
	double angleDegree = 90 ;
	double angleRad = angleDegree * Math.PI/180 ;
	double startAngleDegree, startAngleRad;
	AbstractLayerMap[] layerMap = new AbstractLayerMap[] {new SEAM()} ; // just a silicon layer
	CurvedWg bend_90deg ;
	boolean toRight ;
	
	public Si_bend_90deg(
			@ParamName(name="Object Name") String objectName,
			@ParamName(name="Choose Start Port") Port port1,
			@ParamName(name="Bend to RIGHT?") boolean toRight
			){
		this.objectName = objectName ;
		this.port1 = port1 ;
		this.toRight = toRight ;
		createObject() ;
		setPorts() ;
		saveProperties() ;
	}
	
	@Override
	public void setPorts(){
		P1 = port1.getPosition() ;
		P2 = port2.getPosition() ;
		objectPorts.put(objectName+".port1", port1) ;
		objectPorts.put(objectName+".port2", port2) ;
	}
	
	public void createObject(){
		bend_90deg = new CurvedWg(objectName, layerMap, "port1", port1, toRight, new Entry(radius_um), new Entry(angleDegree)) ;
		port2 = bend_90deg.port2 ;
	}
	
	@Override
	public void saveProperties(){
		
	}
	
	@Override
	public String[] getPythonCode(String fileName, String topCellName) {
		String st0 = "## ---------------------------------------- ##" ;
		String st1 = "##     Adding a Si 45deg-BEND from AIM      ##" ;
		String st2 = "## ---------------------------------------- ##" ;
		String[] args1 = {st0, st1, st2} ;
		String[] args = bend_90deg.getPythonCode_no_header(fileName, topCellName) ;
		args = MoreMath.Arrays.concat(args1, args) ;
		return args ;
	}
	
	@Override
	public String[] getPythonCode_no_header(String fileName, String topCellName) {
		String[] args1 = new String[0] ;
		String[] args = bend_90deg.getPythonCode_no_header(fileName, topCellName) ;
		args = MoreMath.Arrays.concat(args1, args) ;
		return args ;
	}

	@Override
	public void writeToFile(String fileName, String topCellName) {
		FileOutput fout = new FileOutput(fileName + ".py","w") ;
		fout.println(getPythonCode(fileName, topCellName));
		fout.close();
	}

	@Override
	public void appendToFile(String fileName, String topCellName) {
		FileOutput fout = new FileOutput(fileName + ".py","a") ;
		fout.println(getPythonCode(fileName, topCellName));
		fout.close();
	}

	@Override
	public AbstractElement translateXY(double dX, double dY) {
		AbstractElement bend_90deg_translated = bend_90deg.translateXY(dX, dY) ;
		return bend_90deg_translated ;
	}
}
