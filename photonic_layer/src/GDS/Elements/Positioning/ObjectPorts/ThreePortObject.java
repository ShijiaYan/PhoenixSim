package GDS.Elements.Positioning.ObjectPorts;

import GDS.Elements.Positioning.Port;
import GDS.Elements.Positioning.Position;
import ch.epfl.general_libraries.clazzes.ParamName;

public class ThreePortObject {


	/**
	 * One horizontal vectors: hVec12
	 * One vertical vectors: vVec23 ;
	 */
	
	Position hVec12, vVec23 ;
	String portNumber ;
	Port selectedPort ; // this is the port that is selected
	Port port1, port2, port3 ;
	
	public ThreePortObject(
			@ParamName(name="Port Number") String portNumber,
			@ParamName(name="Selected Port") Port selectedPort,
			@ParamName(name="[hVec12, vVec23]") Position[] positionVecs
			){
		this.portNumber = portNumber ;
		this.selectedPort = selectedPort ;
		hVec12 = positionVecs[0] ;
		vVec23 = positionVecs[1] ;
		setPorts() ;
	}
	
	// Checking the port numbers
	private boolean isPort1(){
        return portNumber.equals("port1") || portNumber.equals("Port1") || portNumber.equals("port 1") || portNumber.equals("Port 1");
	}
	
	private boolean isPort2(){
        return portNumber.equals("port2") || portNumber.equals("Port2") || portNumber.equals("port 2") || portNumber.equals("Port 2");
	}
	
	private boolean isPort3(){
        return portNumber.equals("port3") || portNumber.equals("Port3") || portNumber.equals("port 3") || portNumber.equals("Port 3");
	}

	// now calculating the ports 
	private void setPorts(){
		if(isPort1()){
			port1 = selectedPort ;
			port2 = selectedPort.translateXY(hVec12).connect() ;
			port3 = selectedPort.translateXY(hVec12).translateXY(vVec23).connect() ;
		}
		else if(isPort2()){
			port1 = selectedPort.translateXY(hVec12.scale(-1)).connect() ;
			port2 = selectedPort ;
			port3 = selectedPort.translateXY(vVec23) ;
		}
		else if(isPort3()){
			port1 = selectedPort.translateXY(vVec23.scale(-1)).translateXY(hVec12.scale(-1)).connect() ;
			port2 = selectedPort.translateXY(vVec23.scale(-1)) ;
			port3 = selectedPort ;
		}
		
	}
	

	// finally returning the calculated ports --> Becareful about the direction of each port!
	public Port getPort1(){
		return port1 ;
	}
	
	public Port getPort2(){
		return port2 ;
	}
	
	public Port getPort3(){
		return port3 ;
	}
	
	
}
