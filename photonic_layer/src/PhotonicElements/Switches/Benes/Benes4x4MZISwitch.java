package PhotonicElements.Switches.Benes;

import PhotonicElements.DirectionalCoupler.CompactCoupler;
import PhotonicElements.InputSources.AbstractInputSource;
import PhotonicElements.Junctions.Crossings.SimpleCrossing;
import PhotonicElements.Switches.Switch2x2.Switch2x2MZI;
import PhotonicElements.Utilities.MathLibraries.Complex;
import PhotonicElements.Utilities.Wavelength;
import PhotonicElements.Waveguides.WaveguideProperties.WgProperties;
import ch.epfl.general_libraries.clazzes.ParamName;

public class Benes4x4MZISwitch {

	Wavelength inputLambda ;
	SimpleCrossing wgCrossing ;
	Switch2x2MZI swR1C1 ;
	Switch2x2MZI swR1C2 ;
	Switch2x2MZI swR1C3 ;
	Switch2x2MZI swR2C1 ;
	Switch2x2MZI swR2C2 ;
	Switch2x2MZI swR2C3 ;
	Complex port1In ;
	Complex port2In ;
	Complex port3In ;
	Complex port4In ;
	String[] state = new String[6] ; // shows the state of each switch
	String[] stateSource = new String[4] ; // state of sources
	
	public Benes4x4MZISwitch(
			@ParamName(name="Set Wavelength (nm)") Wavelength inputLambda,
			@ParamName(name="Set Waveguide Properties") WgProperties wgProp,
			@ParamName(name="Port 1 Input Source") AbstractInputSource inputSource1,
			@ParamName(name="Port 2 Input Source") AbstractInputSource inputSource2,
			@ParamName(name="Port 3 Input Source") AbstractInputSource inputSource3,
			@ParamName(name="Port 4 Input Source") AbstractInputSource inputSource4,
			@ParamName(name="Set the length of MZI (um)", default_="300") double lengthMicron,
			@ParamName(name="Input Coupler") CompactCoupler DC1,
			@ParamName(name="Output Coupler") CompactCoupler DC2,
			@ParamName(name="Set Waveguide Crossing") SimpleCrossing wgCrossing, 
			@ParamName(name="State of Switch Row 1 Column 1 (true = CROSS, false = BAR)") boolean swR1C1isCross,
			@ParamName(name="State of Switch Row 1 Column 2 (true = CROSS, false = BAR)") boolean swR1C2isCross,
			@ParamName(name="State of Switch Row 1 Column 3 (true = CROSS, false = BAR)") boolean swR1C3isCross,
			@ParamName(name="State of Switch Row 2 Column 1 (true = CROSS, false = BAR)") boolean swR2C1isCross,
			@ParamName(name="State of Switch Row 2 Column 2 (true = CROSS, false = BAR)") boolean swR2C2isCross,
			@ParamName(name="State of Switch Row 2 Column 3 (true = CROSS, false = BAR)") boolean swR2C3isCross
			){
		this.inputLambda = inputLambda ;
		this.wgCrossing = wgCrossing ;
		this.port1In = inputSource1.getElectricFieldAtInputWavelength(inputLambda) ;
		this.port2In = inputSource2.getElectricFieldAtInputWavelength(inputLambda) ;
		this.port3In = inputSource3.getElectricFieldAtInputWavelength(inputLambda) ;
		this.port4In = inputSource4.getElectricFieldAtInputWavelength(inputLambda) ;
		this.swR1C1 = new Switch2x2MZI(inputLambda, wgProp, lengthMicron, DC1, DC2, swR1C1isCross, !swR1C1isCross) ;
		this.swR1C2 = new Switch2x2MZI(inputLambda, wgProp, lengthMicron, DC1, DC2, swR1C2isCross, !swR1C2isCross) ;
		this.swR1C3 = new Switch2x2MZI(inputLambda, wgProp, lengthMicron, DC1, DC2, swR1C3isCross, !swR1C3isCross) ;
		this.swR2C1 = new Switch2x2MZI(inputLambda, wgProp, lengthMicron, DC1, DC2, swR2C1isCross, !swR2C1isCross) ;
		this.swR2C2 = new Switch2x2MZI(inputLambda, wgProp, lengthMicron, DC1, DC2, swR2C2isCross, !swR2C2isCross) ;
		this.swR2C3 = new Switch2x2MZI(inputLambda, wgProp, lengthMicron, DC1, DC2, swR2C3isCross, !swR2C3isCross) ;
		if(swR1C1isCross){this.state[0] = "C";} else{this.state[0] = "B";} ;
		if(swR1C2isCross){this.state[1] = "C";} else{this.state[1] = "B";} ;
		if(swR1C3isCross){this.state[2] = "C";} else{this.state[2] = "B";} ;
		if(swR2C1isCross){this.state[3] = "C";} else{this.state[3] = "B";} ;
		if(swR2C2isCross){this.state[4] = "C";} else{this.state[4] = "B";} ;
		if(swR2C3isCross){this.state[5] = "C";} else{this.state[5] = "B";} ;
		this.stateSource[0] = inputSource1.getInputSourceState() ;
		this.stateSource[1] = inputSource2.getInputSourceState() ;
		this.stateSource[2] = inputSource3.getInputSourceState() ;
		this.stateSource[3] = inputSource4.getInputSourceState() ;
	}
	
	@SuppressWarnings("unused")
	public void buildSwitch(){
		
		Complex zero = new Complex(0,0) ;
//		Complex one = new Complex(1,0) ;
		// Stage 0 ports
		Complex port10 = port1In ; 
		Complex port20 = port2In ;
		Complex port30 = port3In ;
		Complex port40 = port4In ;
		
/*		dp.addResultProperty("Power at Port 1 Out Stage 0", checkNaN(10*Math.log10(port10.absSquared())) );
		dp.addResultProperty("Power at Port 2 Out Stage 0", checkNaN(10*Math.log10(port20.absSquared())) );
		dp.addResultProperty("Power at Port 3 Out Stage 0", checkNaN(10*Math.log10(port30.absSquared())) );
		dp.addResultProperty("Power at Port 4 Out Stage 0", checkNaN(10*Math.log10(port40.absSquared())) );*/
		// Stage 1 ports
		Complex port11 = swR1C1.getPort2(port10, zero, zero, port20) ;
		Complex port21 = swR1C1.getPort3(port10, zero, zero, port20) ;
		Complex port31 = swR2C1.getPort2(port30, zero, zero, port40) ;
		Complex port41 = swR2C1.getPort3(port30, zero, zero, port40) ;
		
/*		dp.addResultProperty("Power at Port 1 Out Stage 1", checkNaN(10*Math.log10(port11.absSquared())) );
		dp.addResultProperty("Power at Port 2 Out Stage 1", checkNaN(10*Math.log10(port21.absSquared())) );
		dp.addResultProperty("Power at Port 3 Out Stage 1", checkNaN(10*Math.log10(port31.absSquared())) );
		dp.addResultProperty("Power at Port 4 Out Stage 1", checkNaN(10*Math.log10(port41.absSquared())) );*/
		// Stage 2
		Complex port12 = port11;
		Complex port22 = wgCrossing.getPort2(port21, zero, zero, port31) ;
		Complex port32 = wgCrossing.getPort3(port21, zero, zero, port31) ;
		Complex port42 = port41 ;
		
/*		dp.addResultProperty("Power at Port 1 Out Stage 2", checkNaN(10*Math.log10(port12.absSquared())) );
		dp.addResultProperty("Power at Port 2 Out Stage 2", checkNaN(10*Math.log10(port22.absSquared())) );
		dp.addResultProperty("Power at Port 3 Out Stage 2", checkNaN(10*Math.log10(port32.absSquared())) );
		dp.addResultProperty("Power at Port 4 Out Stage 2", checkNaN(10*Math.log10(port42.absSquared())) );*/
		// Stage 3
		Complex port13 = swR1C2.getPort2(port12, zero, zero, port22) ;
		Complex port23 = swR1C2.getPort3(port12, zero, zero, port22) ;
		Complex port33 = swR2C2.getPort2(port32, zero, zero, port42) ;
		Complex port43 = swR2C2.getPort3(port32, zero, zero, port42) ;
		
/*		dp.addResultProperty("Power at Port 1 Out Stage 3", checkNaN(10*Math.log10(port13.absSquared())) );
		dp.addResultProperty("Power at Port 2 Out Stage 3", checkNaN(10*Math.log10(port23.absSquared())) );
		dp.addResultProperty("Power at Port 3 Out Stage 3", checkNaN(10*Math.log10(port33.absSquared())) );
		dp.addResultProperty("Power at Port 4 Out Stage 3", checkNaN(10*Math.log10(port43.absSquared())) );*/
		// Stage 4
		Complex port14 = port13 ;
		Complex port24 = wgCrossing.getPort2(port23, zero, zero, port33) ;
		Complex port34 = wgCrossing.getPort3(port23, zero, zero, port33) ;
		Complex port44 = port43 ;
		
/*		dp.addResultProperty("Power at Port 1 Out Stage 4", checkNaN(10*Math.log10(port14.absSquared())) );
		dp.addResultProperty("Power at Port 2 Out Stage 4", checkNaN(10*Math.log10(port24.absSquared())) );
		dp.addResultProperty("Power at Port 3 Out Stage 4", checkNaN(10*Math.log10(port34.absSquared())) );
		dp.addResultProperty("Power at Port 4 Out Stage 4", checkNaN(10*Math.log10(port44.absSquared())) );*/
		
		// Stage 5 (Final)
		Complex port15 = swR1C3.getPort2(port14, zero, zero, port24) ; // output port 1
		Complex port25 = swR1C3.getPort3(port14, zero, zero, port24) ; // output port 2
		Complex port35 = swR2C3.getPort2(port34, zero, zero, port44) ; // output port 3
		Complex port45 = swR2C3.getPort3(port34, zero, zero, port44) ; // output port 4
		
	}
	
	
}




























