package GDS.Elements.AIM.Waveguides;

import GDS.Elements.AbstractElement;
import GDS.Elements.BasicElements.Sbend;
import GDS.Elements.Positioning.Port;
import GDS.PDK.AIMLayerMap.SiliconLevelMasks.SEAM;
import GDS.PDK.AbstractLayerMap;
import PhotonicElements.Utilities.MathLibraries.MoreMath;
import ch.epfl.general_libraries.clazzes.ParamName;
import flanagan.io.FileOutput;

public class Si_offset_50um extends AbstractElement {

	/**
	 * For this class, we use the parametric PATH element to create an S-bend.
	 * X(t) = W*t , Y(t) = V/2 * (1-cos(pi*t)), where 0<t<1 is the curve parameter.
	 * W is the horizontal offset. V is the vertical offset. W and V can be positive or negative.
	 */
	
	
	public Port port1, port2 ;
	Port port ;
	double H_um = 100, V_um = 50 , width_um = 0.4 ;
	boolean isUpward ;
	Sbend bend ;
	
	public Si_offset_50um(
			@ParamName(name="Object Name") String objectName,
			@ParamName(name="Choose Start Port") Port port1,
			@ParamName(name="Is Upward?") boolean isUpward
			){
		this.objectName = objectName ;
		this.port1 = port1 ;
		if(isUpward){V_um = +1 * V_um;} else{V_um = -1 * V_um ;}
		
		createObject() ;
		setPorts() ;
		saveProperties() ;
	}
	
	@Override
	public void setPorts(){
		objectPorts.put(objectName+".port1", port1) ;
		objectPorts.put(objectName+".port2", port2) ;
	}
	
	private void createObject(){
		// need to create an Sbend
		AbstractLayerMap[] layerMap = new AbstractLayerMap[] {new SEAM()} ;
		bend = new Sbend(objectName, layerMap, port1, new Entry(H_um), new Entry(V_um)) ;
		port2 = bend.port2 ;
	}
	
	@Override
	public void saveProperties(){
		
	}
	
	@Override
	public String[] getPythonCode(String fileName, String topCellName) {
		String st0 = "## ---------------------------------------- ##" ;
		String st1 = "##     Adding a Si OFFSET-50um from AIM     ##" ;
		String st2 = "## ---------------------------------------- ##" ;
		String[] args1 = {st0, st1, st2} ;
		String[] args = bend.getPythonCode_no_header(fileName, topCellName) ;
		args = MoreMath.Arrays.concat(args1, args) ;
		return args ;
	}
	
	@Override
	public String[] getPythonCode_no_header(String fileName, String topCellName) {
		String[] args1 = new String[0] ;
		String[] args = bend.getPythonCode_no_header(fileName, topCellName) ;
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
		Port port_translated = port1.translateXY(dX, dY) ;
		AbstractElement si_offset_50um_translated = new Si_offset_100um(objectName, port_translated, isUpward) ;
		return si_offset_50um_translated;
	}
	
	
}
