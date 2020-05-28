package GDS.Elements.Positioning.ObjectPorts;

import GDS.Elements.Positioning.Port;
import GDS.Elements.Positioning.Position;
import ch.epfl.general_libraries.clazzes.ParamName;

public class TwoPortObject {


	/**
	 * One horizontal vectors: hVec12
	 * Two vertical vectors: vVec14, vVec23 ;
	 */
	
	Position hVec12, vVec14, vVec23 ;
	String portNumber ;
	Port selectedPort ; // this is the port that is selected
	Port port1, port2 ;
	
	public TwoPortObject(
			@ParamName(name="Port Number") String portNumber,
			@ParamName(name="Selected Port") Port selectedPort,
			@ParamName(name="[hVec12]") Position[] positionVecs
			){
		this.portNumber = portNumber ;
		this.selectedPort = selectedPort ;
		hVec12 = positionVecs[0] ;
		setPorts() ;
	}
	
	// Checking the port numbers
	private boolean isPort1(){
        return portNumber.equals("port1") || portNumber.equals("Port1") || portNumber.equals("port 1") || portNumber.equals("Port 1");
	}
	
	private boolean isPort2(){
        return portNumber.equals("port2") || portNumber.equals("Port2") || portNumber.equals("port 2") || portNumber.equals("Port 2");
	}
	
	// now calculating the ports 
	private void setPorts(){
		if(isPort1()){
			port1 = selectedPort ;
			port2 = selectedPort.translateXY(hVec12).connect() ;
		}
		else if(isPort2()){
			port1 = selectedPort.translateXY(hVec12.scale(-1)).connect() ;
			port2 = selectedPort ;
		}
		
	}
	

	// finally returning the calculated ports --> Becareful about the direction of each port!
	public Port getPort1(){
		return port1 ;
	}
	
	public Port getPort2(){
		return port2 ;
	}
	
	
}
