package GDS.Elements.Positioning;

import GDS.Elements.BasicElements.Text;
import GDS.Elements.DataBase.Entry;
import GDS.PDK.AIMLayerMap.AbstractLevels.ABSTRACTAM;
import GDS.PDK.AbstractLayerMap;
import ch.epfl.general_libraries.clazzes.ParamName;

public class Label {

	AbstractLayerMap[] layerMap = {new ABSTRACTAM()} ;
	String label ;
	Position P ;
	Text text ;
	double angle_degree ;
	double size = 0.25 ;
	
	public Label(
			@ParamName(name="Position") Position P,
			@ParamName(name="Angle (degree)") double angle_degree,
			@ParamName(name="text") String objectName
			){
		this.P = P ;
		this.angle_degree = angle_degree ;
		this.label = objectName ;
		createLabel() ;
	}
	
	private void createLabel(){
		text = new Text(label+"_label", layerMap, label, P, new Entry(size), true, new Entry(angle_degree)) ;
	}
	
	public String[] getLabel(String fileName, String topCellName){
		return text.getPythonCode_no_header(fileName, topCellName) ;
	}
	
	public void setLabel(String newLabel){
		this.label = newLabel ;
	}
	
	public Label changeLabel(String newLabel){
		return new Label(P, angle_degree, newLabel) ;
	}
	
	public Label rotate(double angleDegree){
		return new Label(P, angle_degree + angleDegree, label) ;
	}
	
	public Label translateXY(double DX, double DY){
		Position P1 = P.translateXY(DX, DY) ;
		return new Label(P1, angle_degree, label) ;
	}
	
	public Label translateXY(Position DP){
		Position P1 = P.translateXY(DP) ;
		return new Label(P1, angle_degree, label) ;
	}
	
	
}
