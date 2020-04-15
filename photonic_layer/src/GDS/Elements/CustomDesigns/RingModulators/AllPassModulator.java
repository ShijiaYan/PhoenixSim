package GDS.Elements.CustomDesigns.RingModulators;

import GDS.Elements.AbstractElement;
import GDS.Elements.BasicElements.CurvedWg;
import GDS.Elements.BasicElements.Ring;
import GDS.Elements.BasicElements.StraightWg;
import GDS.Elements.BasicElements.VIA;
import GDS.Elements.Positioning.Port;
import GDS.Elements.Positioning.Position;
import GDS.Elements.Positioning.Coordinates.Polar;
import GDS.Elements.Shapes.Polygon;
import GDS.PDK.AbstractLayerMap;
import GDS.PDK.AIMLayerMap.PostSiliconLevelMasks.CBAM;
import GDS.PDK.AIMLayerMap.PostSiliconLevelMasks.M1AM;
import GDS.PDK.AIMLayerMap.SiliconLevelMasks.NDAM;
import GDS.PDK.AIMLayerMap.SiliconLevelMasks.NNNAM;
import GDS.PDK.AIMLayerMap.SiliconLevelMasks.PDAM;
import GDS.PDK.AIMLayerMap.SiliconLevelMasks.PPPAM;
import GDS.PDK.AIMLayerMap.SiliconLevelMasks.REAM;
import GDS.PDK.AIMLayerMap.SiliconLevelMasks.SEAM;
import PhotonicElements.Utilities.MathLibraries.MoreMath;
import ch.epfl.general_libraries.clazzes.ParamName;
import flanagan.io.FileOutput;

public class AllPassModulator extends AbstractElement {

	double inputGap_nm, inputGap_um, radius_um, wg1Width_um, ringWidth_um, length_um ;
	StraightWg wg1 ;
	Ring ring ;
	double lx_um, ly_um ;
	public Port port1, port2 ;
	Port objectPort ;
	String portNumber ;
	Position P1, P2;
	Position center ;
	// shallow etch silicon
	double w0_um, w1_um, w2_um, w3_um, L1_um ;
	Polygon leftEtch, centerEtch, rightEtch ;
	// dopings
	double alpha0_degree, alpha1_degree, alpha2_degree, alpha3_degree, alpha4_degree ;
	double alpha0_rad, alpha1_rad, alpha2_rad, alpha3_rad, alpha4_rad ;
	double w4_um, w5_um, w6_um, w7_um, w8_um, w9_um ;
	CurvedWg NPPJunction, NJunction, PJunction, PPPJunction ;
	// heater
	CurvedWg NPPHeaterInside ;
	Polygon NPPHeaterOutside, NHeater ;
	double L6_um, L3_um ;
	// metal
	CurvedWg leftG, rightG, centerS ;
	Polygon leftGExtend, rightGExtend, centerSExtend ;
	Polygon leftHeaterExtend, rightHeaterExtend ;
	
	double gamma0_degree = 10, gamma1_degree = 40 ;
	VIA[][] NPPJunctionVias;
	// junction vias
	VIA[][] PPPJunctionVias, PPPJunctionViasLeft, leftHeaterVias ;
	double beta0_degree, beta0_rad, beta1_degree, beta1_rad ;

	public AllPassModulator(
			@ParamName(name="Object Name") String objectName,
			// parameters of silicon ring-wg
			@ParamName(name="Port Number [portX]", default_="port1") String portNumber,
			@ParamName(name="Choose Port") Port objectPort,
			@ParamName(name="R (um)") Entry radius_um,
			@ParamName(name="g (nm)") Entry inputGap_nm,
			// shallow etch parameters
			@ParamName(name="w0 (um)", default_="0.6") double w0_um,
			@ParamName(name="w1 (um)", default_="2") double w1_um,
			@ParamName(name="w2 (um)", default_="20") double w2_um,
			@ParamName(name="w3 (um)", default_="20") double w3_um,
			@ParamName(name="L1 (um)", default_="5") double L1_um,
			// doping parameters (PN junction)
			@ParamName(name="alpha0 (degree)", default_="35") double alpha0_degree,
			@ParamName(name="alpha1 (degree)", default_="20") double alpha1_degree,
			@ParamName(name="alpha3 (degree)", default_="60") double alpha3_degree,
			@ParamName(name="w4 (um)", default_="1") double w4_um,
			@ParamName(name="w5 (um)", default_="3") double w5_um,
			@ParamName(name="w6 (um)", default_="3") double w6_um,
			// metal parameters (PN junction)
			@ParamName(name="w8 (um)", default_="2") double w8_um,
			@ParamName(name="w9 (um)", default_="1") double w9_um,
			@ParamName(name="L3 (um)", default_="2") double L3_um,
			@ParamName(name="L6 (um)", default_="2") double L6_um,
			@ParamName(name="gamma0 (degree)", default_="30") double gamma0_degree,
			@ParamName(name="gamma1 (degree)", default_="60") double gamma1_degree ,
			@ParamName(name="beta0 (degree)", default_="40") double beta0_degree ,
			@ParamName(name="beta1 (degree)", default_="20") double beta1_degree
			){
		this.objectName = objectName ;
		this.portNumber = portNumber ;
		this.objectPort = objectPort ;
		this.wg1Width_um = objectPort.getWidthMicron() ;
		this.ringWidth_um = objectPort.getWidthMicron() ;
		this.radius_um = radius_um.getValue() ;
		this.inputGap_nm = inputGap_nm.getValue() ;
		this.inputGap_um = inputGap_nm.getValue()/1000d ;
		length_um = 2*L1_um + w2_um ;
		// shallow etch silicon
		this.w0_um = w0_um ;
		this.w1_um = w1_um ;
		this.w2_um = w2_um ;
		this.w3_um = w3_um ;
		this.L1_um = L1_um ;
		// angles
		this.alpha0_degree = alpha0_degree ;
		this.alpha0_rad = MoreMath.Conversions.Angles.toRadian(alpha0_degree) ;
		this.alpha1_degree = alpha1_degree ;
		this.alpha1_rad = MoreMath.Conversions.Angles.toRadian(alpha1_degree) ;
		this.alpha2_degree = 90 - (alpha0_degree + alpha1_degree) ;
		this.alpha2_rad = MoreMath.Conversions.Angles.toRadian(alpha2_degree) ;
		this.alpha3_degree = alpha3_degree ;
		this.alpha3_rad = MoreMath.Conversions.Angles.toRadian(alpha3_degree) ;
		this.alpha4_degree = 90 - (alpha3_degree) ;
		this.alpha4_rad = MoreMath.Conversions.Angles.toRadian(alpha4_degree) ;

		this.w4_um = w4_um ;
		this.w5_um = w5_um ;
		this.w6_um = w6_um ;
		this.w8_um = w8_um ;
		this.w9_um = w9_um ;
		this.w7_um = radius_um.getValue()-(w4_um/2+w5_um) ;

		this.L6_um = L6_um ;
		this.L3_um = L3_um ;
		
		this.gamma0_degree = gamma0_degree ;
		this.gamma1_degree = gamma1_degree ;

		this.beta0_degree = beta0_degree ;
		this.beta0_rad = MoreMath.Conversions.Angles.toRadian(beta0_degree) ;
		this.beta1_degree = beta1_degree ;
		this.beta1_rad = MoreMath.Conversions.Angles.toRadian(beta1_degree) ;
		
		createObject() ;
		setPorts() ;
		saveProperties() ;
	}

	private void createObject(){
		// create Ring-Wg
		createRingWg() ;
		// create Doping layers
		createPNJunction() ;
		// create integrated heater
		createHeater() ;
		// create Metal Contacts
		createMetalContacts() ;
		// create via contacts
		createNPPJunctionVias() ;
		createPPPJunctionVias() ;
	}

	private void createRingWg(){
		// creating input waveguide
		String wg1Name = objectName + "_" + "wg1" ;
		wg1 = new StraightWg(wg1Name, new AbstractLayerMap[]{new SEAM()}, portNumber, objectPort, new Entry(length_um)) ;
		// calculating the ports and center of the ring
		this.port1 = wg1.port1 ;
		this.port2 = wg1.port2 ;
		P1 = port1.getPosition() ;
		P2 = port2.getPosition() ;
		Position Pmiddle = new Position((P1.getX()+P2.getX())/2, (P1.getY()+P2.getY())/2) ;
		Position normalVec = port2.getEdgeVec().scale(inputGap_um + MoreMath.Functions.sign(inputGap_um)*(wg1Width_um/2 + ringWidth_um/2 + radius_um)) ;
		center = Pmiddle.translateXY(normalVec) ;
		// creating the ring
		String ringName = objectName + "_" + "ring" ;
		ring = new Ring(ringName, new AbstractLayerMap[]{new SEAM()}, new Ring.Center(center), new Entry(ringWidth_um), new Entry(radius_um)) ;
		// creating shallow etch silicon
		String centerEtchName = objectName + "_" + "centerEtch" ;
		Position P1 = center.translateXY(-w2_um/2, -w3_um/2) ;
		Position P2 = P1.translateX(w2_um) ;
		Position P3 = P2.translateY(w3_um) ;
		Position P4 = P3.translateX(-w2_um) ;
		centerEtch = new Polygon(centerEtchName, new AbstractLayerMap[]{new REAM()}, new Position[]{P1, P2, P3, P4}) ;

		String leftEtchName = objectName + "_" + "leftEtch" ;
		P1 = port1.getPosition().translateY(w0_um/2) ;
		P2 = P1.translateXY(L1_um, w1_um/2-w0_um/2) ;
		P3 = P2.translateY(-w1_um) ;
		P4 = P1.translateY(-w0_um) ;
		leftEtch = new Polygon(leftEtchName, new AbstractLayerMap[]{new REAM()}, new Position[]{P1, P2, P3, P4}) ;

		String rightEtchName = objectName + "_" + "rightEtch" ;
		P1 = port2.getPosition().translateY(w0_um/2) ;
		P2 = P1.translateXY(-L1_um, w1_um/2-w0_um/2) ;
		P3 = P2.translateY(-w1_um) ;
		P4 = P1.translateY(-w0_um) ;
		rightEtch = new Polygon(rightEtchName, new AbstractLayerMap[]{new REAM()}, new Position[]{P1, P2, P3, P4}) ;
	}

	private void createPNJunction(){
		String NPPName = objectName + "_" + "Junction_NPP" ;
		double nppRadius = radius_um-(w4_um/2+w5_um/2) ;
		Position nppPos = new Position(new Polar(nppRadius, -alpha2_degree)) ;
		Port nppPort = new Port(nppPos.translateXY(center), w5_um, -(90+alpha2_degree)) ;
		NPPJunction = new CurvedWg(NPPName, new AbstractLayerMap[]{new NNNAM()}, "port1",
				nppPort, false, new Entry(nppRadius), new Entry(360-2*(alpha0_degree+alpha1_degree)) ) ;

		String NName = objectName + "_" + "Junction_N" ;
		double nRadius = radius_um-(w4_um/4) ;
		Position nPos = new Position(new Polar(nRadius, -alpha2_degree)) ;
		Port nPort = new Port(nPos.translateXY(center), w4_um/2, -(90+alpha2_degree)) ;
		NJunction = new CurvedWg(NName, new AbstractLayerMap[]{new NDAM()}, "port1",
				nPort, false, new Entry(nRadius), new Entry(360-2*(alpha0_degree+alpha1_degree)) ) ;

		String PPPName = objectName + "_" + "Junction_PPP" ;
		double pppRadius = radius_um+(w4_um/2+w5_um/2) ;
		Position pppPos = new Position(new Polar(pppRadius, -alpha2_degree)) ;
		Port pppPort = new Port(pppPos.translateXY(center), w5_um, -(90+alpha2_degree)) ;
		PPPJunction = new CurvedWg(PPPName, new AbstractLayerMap[]{new PPPAM()}, "port1",
				pppPort, false, new Entry(pppRadius), new Entry(360-2*(alpha0_degree+alpha1_degree)) ) ;

		String PName = objectName + "_" + "Junction_P" ;
		double pRadius = radius_um+(w4_um/4) ;
		Position pPos = new Position(new Polar(pRadius, -alpha2_degree)) ;
		Port pPort = new Port(pPos.translateXY(center), w4_um/2, -(90+alpha2_degree)) ;
		PJunction = new CurvedWg(PName, new AbstractLayerMap[]{new PDAM()}, "port1",
				pPort, false, new Entry(pRadius), new Entry(360-2*(alpha0_degree+alpha1_degree)) ) ;

	}

	private void createHeater(){
		String NPPHeaterInsideName = objectName + "_" + "Heater_NPP_Inside" ;
		double nppInsideRadius = radius_um-(w4_um/2+w5_um/2) ;
		Position nppPos = new Position(new Polar(nppInsideRadius, -(alpha1_degree+alpha2_degree))) ;
		Port nppPort = new Port(nppPos.translateXY(center), w5_um, alpha0_degree) ;
		NPPHeaterInside = new CurvedWg(NPPHeaterInsideName, new AbstractLayerMap[]{new NNNAM()}, "port1",
				nppPort, true, new Entry(nppInsideRadius), new Entry(2*alpha0_degree) ) ;

		String NPPHeaterOutsideName = objectName + "_" + "Heater_NPP_Outside" ;
		double d_um = radius_um + wg1Width_um/2 + inputGap_um + wg1Width_um/2 + w4_um/2 ;
		double h_um = d_um * Math.tan(alpha0_rad) ;
		double L4_um = 2*h_um ;
		Position P1 = center.translateXY(-L4_um + (radius_um-w4_um/2)*Math.sin(alpha0_rad), -d_um) ;
		Position P2 = P1.translateX(L4_um) ;
		Position P3 = P2.translateY(-L6_um) ;
		Position P4 = P1.translateY(-L6_um) ;
		NPPHeaterOutside = new Polygon(NPPHeaterOutsideName, new AbstractLayerMap[]{new NNNAM()}, new Position[]{P1,P2,P3,P4}) ;

		String NHeaterName = objectName + "_" + "Heater_N" ;
		Position P1n = new Position(new Polar(radius_um-w4_um/2, -90+alpha0_degree)) ;
		P1n = P1n.translateXY(center) ;
		Position P2n = new Position(new Polar(radius_um-w4_um/2, -90-alpha0_degree)) ;
		P2n = P2n.translateXY(center) ;
		Position P3n = new Position(P2n.getX(), P1.getY()) ;
		Position P4n = new Position(P1n.getX(), P1.getY()) ;
		NHeater = new Polygon(NHeaterName, new AbstractLayerMap[]{new NDAM()}, new Position[]{P1n,P2n,P3n,P4n}) ;
		
		String leftHeaterExtendName = objectName + "_" + "leftHeaterExtend" ;
		Position P1l = P1.translateXY(-3, 0) ;
		Position P2l = new Position(P3n.getX(), P1.getY()) ;
		Position P3l = P2l.translateY(-L6_um-1) ;
		Position P4l = new Position(P1l.getX(), P3l.getY()) ;
		leftHeaterExtend = new Polygon(leftHeaterExtendName, new AbstractLayerMap[]{new M1AM()}, new Position[]{P1l, P2l, P3l, P4l}) ;
		
		String leftHeaterViasName = objectName + "_" + "leftHeaterVias" ;
		double dx = 1 ;
		double dy = 1 ;
		int M = 4 ;
		int N = 3 ;
		leftHeaterVias = new VIA[M][N] ;
		for(int i=0; i<M; i++){
			for(int j=0; j<N; j++){
				Position P = P1.translateXY(dx/2 + (i-2)*dx, -j*dy) ;
				Port port = new Port(P, 1, 90) ;
				leftHeaterVias[i][j] = new VIA(leftHeaterViasName, new NNNAM(), new M1AM(), new CBAM(), port, new Entry(0.4)) ;
			}
		}
	}

	private void createMetalContacts(){
		// left G
		double leftGRadius_um = radius_um+w4_um/2+w5_um-w8_um/2 ;
		Position P0 = new Position(new Polar(leftGRadius_um, alpha3_degree+2*alpha4_degree)) ;
		P0 = P0.translateXY(center) ;
		Port portLeftGInside = new Port(P0, w8_um, alpha3_degree+2*alpha4_degree-90) ;
		leftG = new CurvedWg(objectName+"_leftG_inside", new AbstractLayerMap[]{new M1AM()} , "port1", portLeftGInside, false, new Entry(leftGRadius_um), new Entry(alpha3_degree+alpha2_degree)) ;
	
		Position P1 = P0.translateXY(portLeftGInside.getEdgeVec().resize(w8_um/2)) ;
		Position P2 = new Position(new Polar(leftGRadius_um+w8_um/2, alpha3_degree+2*alpha4_degree+alpha3_degree/2)) ;
		P2 = P2.translateXY(center) ;
		Position P3 = new Position(-(leftGRadius_um+w8_um/2), 0) ;
		P3 = P3.translateXY(center) ;
		Position P4 = P3.translateXY(-10*Math.cos(gamma0_degree*Math.PI/180), 10*Math.sin(gamma0_degree*Math.PI/180)) ;
		Position P5 = new Position(P4.getX(), P1.getY()) ;
		leftGExtend = new Polygon(objectName+"_leftG_extend", new AbstractLayerMap[]{new M1AM()} , new Position[]{P1, P2, P3, P4, P5}) ;
		
		
		// right G
		double rightGRadius_um = radius_um+w4_um/2+w5_um-w8_um/2 ;
		Position P0r = new Position(new Polar(rightGRadius_um, -alpha2_degree)) ;
		P0r = P0r.translateXY(center) ;
		Port portRightGInside = new Port(P0r, w8_um, -alpha2_degree-90) ;
		rightG = new CurvedWg(objectName+"_rightG_inside", new AbstractLayerMap[]{new M1AM()} , "port1", portRightGInside, false, new Entry(rightGRadius_um), new Entry(alpha3_degree+alpha2_degree)) ;
	
		Position P1r = new Position(new Polar(rightGRadius_um+w8_um/2, alpha3_degree)) ;
		P1r = P1r.translateXY(center) ;
		Position P2r = new Position(new Polar(rightGRadius_um+w8_um/2, alpha3_degree/2)) ;
		P2r = P2r.translateXY(center) ;
		Position P3r = new Position(rightGRadius_um+w8_um/2, 0) ;
		P3r = P3r.translateXY(center) ;
		Position P4r = P3r.translateXY(10*Math.cos(gamma0_degree*Math.PI/180), 10*Math.sin(gamma0_degree*Math.PI/180)) ;
		Position P5r = new Position(P4r.getX(), P1r.getY()) ;
		rightGExtend = new Polygon(objectName+"_rightG_extend", new AbstractLayerMap[]{new M1AM()} , new Position[]{P1r, P2r, P3r, P4r, P5r}) ;
		
		// center S
		double radiusS_um =  radius_um-(w4_um/2+w5_um/2)- w9_um ;
		Position Prs = new Position(new Polar(radiusS_um, -alpha2_degree)) ;
		Prs = Prs.translateXY(center) ;
		Port centerSPort = new Port(Prs , w6_um	, -alpha2_degree-90) ;
		centerS = new CurvedWg(objectName+"_centerS_curved", new AbstractLayerMap[]{new M1AM()}, "port1", centerSPort, 
								false, new Entry(radiusS_um), new Entry(2*(alpha2_degree+alpha3_degree+alpha4_degree))) ;
		
		double L2_um = radius_um + w4_um/2 + w5_um + 2 - (w9_um+w6_um/2) ;
		Position P0s = new Position(L3_um/2, w9_um+w6_um/2) ;
		P0s = P0s.translateXY(center) ;
		Position P1s = P0s.translateX(-L3_um) ;
		Position P2s = P1s.translateY(L2_um) ;
		Position P3s = P2s.translateX(L3_um) ;
		Position P4s = P2s.translateXY(-5*Math.cos(gamma1_degree*Math.PI/180), 5*Math.sin(gamma1_degree*Math.PI/180)) ;
		Position P5s = P3s.translateXY(5*Math.cos(gamma1_degree*Math.PI/180), 5*Math.sin(gamma1_degree*Math.PI/180)) ;
		centerSExtend = new Polygon(objectName+"centerS_extend",  new AbstractLayerMap[]{new M1AM()}, new Position[]{P0s, P1s, P2s, P4s, P5s, P3s} ) ;
	
	}
	
	private void createNPPJunctionVias(){
		double dtheta_degree = beta0_degree ;
//		double dtheta_rad = MoreMath.Conversions.Angles.toRadian(dtheta_degree) ;
		double drho_um = 1 ; // assume 100 nm
		double rho0_um =  (radius_um-w4_um/2-w5_um) ;
		double theta0_degree = -(alpha2_degree-20) ;
		int M = 2 ; // num rows
		int N = (int) (2*((alpha2_degree-alpha1_degree)+alpha3_degree+alpha4_degree)/dtheta_degree + 1) ;
		NPPJunctionVias = new VIA[M][N] ;
		for(int i=0; i<M; i++){
			for(int j=0; j<N; j++){
				double theta_degree = theta0_degree + j*dtheta_degree ;
				double rho_um = rho0_um + i * drho_um ;
				Position P = new Position(new Polar(rho_um, theta_degree)) ;
				P = P.translateXY(center) ;
				Port port = new Port(P, 1, 180+theta_degree) ;
				NPPJunctionVias[i][j] = new VIA(objectName, new NNNAM(), new M1AM(), new CBAM(), port, new Entry(0.4)) ;
			}
		}
		
	}
	
	private void createPPPJunctionVias(){
		double dtheta_degree = beta1_degree ;
//		double dtheta_rad = MoreMath.Conversions.Angles.toRadian(dtheta_degree) ;
		double drho_um = 1 ; // assume 100 nm
		double rho0_um = radius_um+w4_um/2+w5_um - w8_um  ;
		double theta0_degree = -(alpha2_degree-10) ;
		int M = 2 ; // num rows
		int N = (int) ((alpha2_degree+alpha3_degree)/dtheta_degree) ;
		PPPJunctionVias = new VIA[M][N] ;
		for(int i=0; i<M; i++){
			for(int j=0; j<N; j++){
				double theta_degree = theta0_degree + j*dtheta_degree ;
				double rho_um = rho0_um + i * drho_um ;
				Position P = new Position(new Polar(rho_um, theta_degree)) ;
				P = P.translateXY(center) ;
				Port port = new Port(P, 1, 180+theta_degree) ;
				PPPJunctionVias[i][j] = new VIA(objectName, new PPPAM(), new M1AM(), new CBAM(), port, new Entry(0.4)) ;
			}
		}
		PPPJunctionViasLeft = new VIA[M][N] ;
		for(int i=0; i<M; i++){
			for(int j=0; j<N; j++){
				double theta_degree = theta0_degree + j*dtheta_degree + 2*alpha4_degree+alpha3_degree+alpha2_degree ;
				double rho_um = rho0_um + i * drho_um ;
				Position P = new Position(new Polar(rho_um, theta_degree)) ;
				P = P.translateXY(center) ;
				Port port = new Port(P, 1, 180+theta_degree) ;
				PPPJunctionViasLeft[i][j] = new VIA(objectName, new PPPAM(), new M1AM(), new CBAM(), port, new Entry(0.4)) ;
			}
		}
		
	}

	@Override
	public void setPorts() {
		objectPorts.put(objectName+".port1", port1) ;
		objectPorts.put(objectName+".port2", port2) ;
	}

	@Override
	public void saveProperties() {
		objectProperties.put(objectName+".wg.width_um", wg1Width_um) ;
		objectProperties.put(objectName+".ring.width_um", ringWidth_um) ;
		objectProperties.put(objectName+".ring.radius_um", radius_um) ;
		objectProperties.put(objectName+".gap_nm", inputGap_um*1000) ;
		objectProperties.put(objectName+".gap_um", inputGap_um) ;
		// now adding port 1
		objectProperties.put(objectName+".port1.angle_degree", port1.getAngleDegree()) ;
		objectProperties.put(objectName+".port1.angle_rad", port1.getAngleRad()) ;
		objectProperties.put(objectName+".port1.normal_degree", port1.getNormalDegree()) ;
		objectProperties.put(objectName+".port1.normal_rad", port1.getNormalRad()) ;
		// now adding port 2
		objectProperties.put(objectName+".port2.angle_degree", port2.getAngleDegree()) ;
		objectProperties.put(objectName+".port2.angle_rad", port2.getNormalRad()) ;
		objectProperties.put(objectName+".port2.normal_degree", port2.getNormalDegree()) ;
		objectProperties.put(objectName+".port2.normal_rad", port2.getNormalRad()) ;
	}

	@Override
	public String[] getPythonCode(String fileName, String topCellName) {
		String st0 = "## ---------------------------------------- ##" ;
		String st1 = "##       Adding a RING-WG MODULATOR         ##" ;
		String st2 = "## ---------------------------------------- ##" ;
		String[] st3 = wg1.getPythonCode_no_header(fileName, topCellName) ;
		String[] st4 = ring.getPythonCode_no_header(fileName, topCellName) ;
		String[] st5 = leftEtch.getPythonCode_no_header(fileName, topCellName) ;
		String[] st6 = centerEtch.getPythonCode_no_header(fileName, topCellName) ;
		String[] st7 = rightEtch.getPythonCode_no_header(fileName, topCellName) ;
		String[] st8 = NPPJunction.getPythonCode_no_header(fileName, topCellName) ;
		String[] st9 = PPPJunction.getPythonCode_no_header(fileName, topCellName) ;
		String[] st10 = NJunction.getPythonCode_no_header(fileName, topCellName) ;
		String[] st11 = PJunction.getPythonCode_no_header(fileName, topCellName) ;
		String[] st12 = NPPHeaterInside.getPythonCode_no_header(fileName, topCellName) ;
		String[] st13 = NPPHeaterOutside.getPythonCode_no_header(fileName, topCellName) ;
		String[] st14 = NHeater.getPythonCode_no_header(fileName, topCellName) ;
		String[] st15 = leftG.getPythonCode_no_header(fileName, topCellName) ;
		String[] st16 = rightG.getPythonCode_no_header(fileName, topCellName) ;
		String[] st17 = leftGExtend.getPythonCode_no_header(fileName, topCellName) ;
		String[] st18 = rightGExtend.getPythonCode_no_header(fileName, topCellName) ;
		String[] st19 = centerS.getPythonCode_no_header(fileName, topCellName) ;
		String[] st20 = centerSExtend.getPythonCode_no_header(fileName, topCellName) ;
		String[] st21 = leftHeaterExtend.getPythonCode_no_header(fileName, topCellName) ;
		// now we need to append all the strings together
		String[] args = {st0, st1, st2} ;
		args = MoreMath.Arrays.concat(args, st3) ;
		args = MoreMath.Arrays.concat(args, st4) ;
		args = MoreMath.Arrays.concat(args, st5) ;
		args = MoreMath.Arrays.concat(args, st6) ;
		args = MoreMath.Arrays.concat(args, st7) ;
		args = MoreMath.Arrays.concat(args, st8) ;
		args = MoreMath.Arrays.concat(args, st9) ;
		args = MoreMath.Arrays.concat(args, st10) ;
		args = MoreMath.Arrays.concat(args, st11) ;
		args = MoreMath.Arrays.concat(args, st12) ;
		args = MoreMath.Arrays.concat(args, st13) ;
		args = MoreMath.Arrays.concat(args, st14) ;
		args = MoreMath.Arrays.concat(args, st15) ;
		args = MoreMath.Arrays.concat(args, st16) ;
		args = MoreMath.Arrays.concat(args, st17) ;
		args = MoreMath.Arrays.concat(args, st18) ;
		args = MoreMath.Arrays.concat(args, st19) ;
		args = MoreMath.Arrays.concat(args, st20) ;
		args = MoreMath.Arrays.concat(args, st21) ;
		int M = NPPJunctionVias.length ;
		int N = NPPJunctionVias[0].length ;
		for(int i=0; i<M; i++){
			for(int j=0; j<N; j++){
				args = MoreMath.Arrays.concat(args, NPPJunctionVias[i][j].getPythonCode_no_header(fileName, topCellName)) ;
			}
		}
		int M0 = PPPJunctionVias.length ;
		int N0 = PPPJunctionVias[0].length ;
		for(int i=0; i<M0; i++){
			for(int j=0; j<N0; j++){
				args = MoreMath.Arrays.concat(args, PPPJunctionVias[i][j].getPythonCode_no_header(fileName, topCellName)) ;
			}
		}
		int M1 = PPPJunctionViasLeft.length ;
		int N1 = PPPJunctionViasLeft[0].length ;
		for(int i=0; i<M1; i++){
			for(int j=0; j<N1; j++){
				args = MoreMath.Arrays.concat(args, PPPJunctionViasLeft[i][j].getPythonCode_no_header(fileName, topCellName)) ;
			}
		}
		int M2 = leftHeaterVias.length ;
		int N2 = leftHeaterVias[0].length ;
		for(int i=0; i<M2; i++){
			for(int j=0; j<N2; j++){
				args = MoreMath.Arrays.concat(args, leftHeaterVias[i][j].getPythonCode_no_header(fileName, topCellName)) ;
			}
		}
		return args;
	}

	@Override
	public String[] getPythonCode_no_header(String fileName, String topCellName) {
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		return null;
	}

}
