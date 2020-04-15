package PhotonicElements.Switches.Omega;

import PhotonicElements.DirectionalCoupler.CompactCoupler;
import PhotonicElements.InputSources.AbstractInputSource;
import PhotonicElements.Junctions.Crossings.SimpleCrossing;
import PhotonicElements.Switches.Switch2x2.Switch2x2MZI;
import PhotonicElements.Utilities.MathLibraries.Complex;
import PhotonicElements.Utilities.Wavelength;
import PhotonicElements.Waveguides.WaveguideProperties.WgProperties;
import ch.epfl.general_libraries.clazzes.ParamName;

public class Omega4x4MZISwitch {

	Wavelength inputLambda ;
	SimpleCrossing wgCrossing ;
	Switch2x2MZI swR1C1 ;
	Switch2x2MZI swR1C2 ;
	Switch2x2MZI swR2C1 ;
	Switch2x2MZI swR2C2 ;
	Complex port1In ;
	Complex port2In ;
	Complex port3In ;
	Complex port4In ;
	String[] state = new String[4] ; // shows the state of each switch
	String sourceState ;
	
	public Omega4x4MZISwitch(
			@ParamName(name="Set Wavelength (nm)") Wavelength inputLambda,
			@ParamName(name="Set Waveguide Properties") WgProperties wgProp,
			@ParamName(name="Port 1 Input Source") AbstractInputSource inputSource1,
			@ParamName(name="Port 2 Input Source") AbstractInputSource inputSource2,
			@ParamName(name="Port 3 Input Source") AbstractInputSource inputSource3,
			@ParamName(name="Port 4 Input Source") AbstractInputSource inputSource4,
			@ParamName(name="Set the length of MZI (micron)", default_="300") double lengthMicron,
			@ParamName(name="Input Coupler") CompactCoupler DC1,
			@ParamName(name="Output Coupler") CompactCoupler DC2,
			@ParamName(name="Set Waveguide Crossing") SimpleCrossing wgCrossing, 
			@ParamName(name="State of Switch Row 1 Column 1 (true = CROSS, false = BAR)") boolean swR1C1isCross,
			@ParamName(name="State of Switch Row 1 Column 2 (true = CROSS, false = BAR)") boolean swR1C2isCross,
			@ParamName(name="State of Switch Row 2 Column 1 (true = CROSS, false = BAR)") boolean swR2C1isCross,
			@ParamName(name="State of Switch Row 2 Column 2 (true = CROSS, false = BAR)") boolean swR2C2isCross
			){
		this.inputLambda = inputLambda ;
//		this.wgCrossing = new SimpleCrossing(wgCrossingLossdB) ;
		this.wgCrossing = wgCrossing ;
		this.port1In = inputSource1.getElectricFieldAtInputWavelength(inputLambda) ;
		this.port2In = inputSource2.getElectricFieldAtInputWavelength(inputLambda) ;
		this.port3In = inputSource3.getElectricFieldAtInputWavelength(inputLambda) ;
		this.port4In = inputSource4.getElectricFieldAtInputWavelength(inputLambda) ;
		this.sourceState = inputSource1.getInputSourceState() + inputSource2.getInputSourceState() + inputSource3.getInputSourceState() + inputSource4.getInputSourceState() ;
		this.swR1C1 = new Switch2x2MZI(inputLambda, wgProp, lengthMicron, DC1, DC2, swR1C1isCross, !swR1C1isCross) ;
		this.swR1C2 = new Switch2x2MZI(inputLambda, wgProp, lengthMicron, DC1, DC2, swR1C2isCross, !swR1C2isCross) ;
		this.swR2C1 = new Switch2x2MZI(inputLambda, wgProp, lengthMicron, DC1, DC2, swR2C1isCross, !swR2C1isCross) ;
		this.swR2C2 = new Switch2x2MZI(inputLambda, wgProp, lengthMicron, DC1, DC2, swR2C2isCross, !swR2C2isCross) ;
		if(swR1C1isCross){this.state[0] = "C";} else{this.state[0] = "B";} ;
		if(swR1C2isCross){this.state[1] = "C";} else{this.state[1] = "B";} ;
		if(swR2C1isCross){this.state[2] = "C";} else{this.state[2] = "B";} ;
		if(swR2C2isCross){this.state[3] = "C";} else{this.state[3] = "B";} ;
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
		// Stage 1 ports
		Complex port11 = swR1C1.getPort2(port10, zero, zero, port20) ;
		Complex port21 = swR1C1.getPort3(port10, zero, zero, port20) ;
		Complex port31 = swR2C1.getPort2(port30, zero, zero, port40) ;
		Complex port41 = swR2C1.getPort3(port30, zero, zero, port40) ;
		// Stage 2
		Complex port12 = port11;
		Complex port22 = wgCrossing.getPort2(port21, zero, zero, port31) ;
		Complex port32 = wgCrossing.getPort3(port21, zero, zero, port31) ;
		Complex port42 = port41 ;
		// Stage 3 (Final)
		Complex port13 = swR1C2.getPort2(port12, zero, zero, port22) ;
		Complex port23 = swR1C2.getPort3(port12, zero, zero, port22) ;
		Complex port33 = swR2C2.getPort2(port32, zero, zero, port42) ;
		Complex port43 = swR2C2.getPort3(port32, zero, zero, port42) ;
		
	}


}




























