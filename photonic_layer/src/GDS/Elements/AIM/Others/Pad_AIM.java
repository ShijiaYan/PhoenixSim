package GDS.Elements.AIM.Others;

import GDS.Elements.AbstractElement;
import GDS.Elements.Positioning.Array;
import GDS.Elements.Positioning.Position;
import GDS.Elements.Shapes.Rectangle;
import GDS.PDK.AIMLayerMap.PostSiliconLevelMasks.M2AM;
import GDS.PDK.AIMLayerMap.PostSiliconLevelMasks.MLAM;
import GDS.PDK.AIMLayerMap.PostSiliconLevelMasks.VAAM;
import GDS.PDK.AbstractLayerMap;
import PhotonicElements.Utilities.MathLibraries.MoreMath;
import ch.epfl.general_libraries.clazzes.ParamName;
import flanagan.io.FileOutput;

public class Pad_AIM extends AbstractElement {

	AbstractLayerMap layerMap1 = new MLAM() ;
	AbstractLayerMap layerMap2 = new M2AM() ;
	AbstractLayerMap layerMapVIA = new VAAM() ;
	Position P1, P2, P3, P4 ;
	Position center, P1_via ;
	double width_um = 60, width_VIA_um = 3, dx_um = 2, dy_um = 2 ;
	int numRows = 11, numColumns = 11 ;
	double Xmargin_um = 3.5 , Ymargin_um = 3.5 ;
	Rectangle rect1, rect2, rectVIA ;
	Array viaArray ;
	
	public Pad_AIM(
			@ParamName(name="Object Name") String objectName,
			@ParamName(name="Position of the start corner") Position P1
			){
		this.objectName = objectName ;
		this.P1 = P1 ;
		setCoordinates() ;
		createObject() ;
		saveProperties() ;
	}
	
	@Override
	public void setPorts(){
		// nothing to do here
	}
	
	private void setCoordinates(){
		P2 = P1.translateX(width_um) ;
		P3 = P2.translateY(width_um) ;
		P4 = P1.translateY(width_um) ;
		center = P1.translateXY(width_um/2, width_um/2) ;
		// now calculating the margin
		Xmargin_um = (width_um-numColumns*width_VIA_um-(numColumns-1)*dx_um)/2 ;
		Ymargin_um = (width_um-numRows*width_VIA_um-(numRows-1)*dy_um)/2 ;
		P1_via = P1.translateXY(Xmargin_um, Ymargin_um) ;
	}
	
	private void createObject(){
		// creating a metal 1 rectangle
		rect1 = new Rectangle(objectName+"_rect1", new AbstractLayerMap[] {layerMap1}, P1.translateY(width_um/2), width_um, width_um, 0) ;
		// creating a metal 2 rectangle
		rect2 = new Rectangle(objectName+"_rect2", new AbstractLayerMap[] {layerMap2}, P1.translateY(width_um/2), width_um, width_um, 0) ;
		// creating a VIA rectangle
		rectVIA = new Rectangle(objectName+"_rectVIA", new AbstractLayerMap[] {layerMapVIA}, P1_via.translateY(width_VIA_um/2), width_VIA_um, width_VIA_um, 0) ;
		// now creating an array of VIAs
		viaArray = new Array(objectName+"_VIA_array", rectVIA, numRows, numColumns, dx_um + width_VIA_um, dy_um + width_VIA_um) ;
	}
	
	@Override
	public void saveProperties(){
		
	}
	
	@Override
	public String[] getPythonCode(String fileName, String topCellName) {
		String st0 = "## ---------------------------------------- ##" ;
		String st1 = "##       Adding a new PAD from AIM          ##" ;
		String st2 = "## ---------------------------------------- ##" ;
		String[] args = {st0, st1, st2} ;
		args = MoreMath.Arrays.concat(args, rect1.getPythonCode_no_header(fileName, topCellName)) ;
		args = MoreMath.Arrays.concat(args, rect2.getPythonCode_no_header(fileName, topCellName)) ;
		args = MoreMath.Arrays.concat(args, viaArray.getPythonCode_no_header(fileName, topCellName)) ;
		return args;
	}
	
	@Override
	public String[] getPythonCode_no_header(String fileName, String topCellName) {
		String st0 = "" ;
		String[] args = {st0} ;
		args = MoreMath.Arrays.concat(args, rect1.getPythonCode_no_header(fileName, topCellName)) ;
		args = MoreMath.Arrays.concat(args, rect2.getPythonCode_no_header(fileName, topCellName)) ;
		args = MoreMath.Arrays.concat(args, viaArray.getPythonCode_no_header(fileName, topCellName)) ;
		return args;
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
		Position P1_translated = P1.translateXY(dX, dY) ;
		AbstractElement pad_translated = new Pad_AIM(objectName, P1_translated) ;
		return pad_translated;
	}

}
