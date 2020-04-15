package PhotonicElements.Switches.Omega;

import PhotonicElements.DirectionalCoupler.CompactCoupler;
import PhotonicElements.InputSources.AbstractInputSource;
import PhotonicElements.Junctions.Crossings.SimpleCrossing;
import PhotonicElements.Switches.Switch2x2.Switch2x2MZI;
import PhotonicElements.Utilities.MathLibraries.Complex;
import PhotonicElements.Utilities.Wavelength;
import PhotonicElements.Waveguides.WaveguideProperties.WgProperties;
import ch.epfl.general_libraries.clazzes.ParamName;

public class Omega8x8MZISwitch {

	Wavelength inputLambda ;
	SimpleCrossing wgCrossing ;
	Switch2x2MZI swCross, swBar ;
	Complex port1In, port2In, port3In, port4In, port5In, port6In, port7In, port8In   ;
	String[] state = new String[12] ; // shows the state of each switch
	String[] stateSource = new String[8] ; // state of sources
	Complex[][] tCross = new Complex[4][3] ;
	Complex[][] tBar = new Complex[4][3] ;
	Complex zero = new Complex(0,0), one = new Complex(1,0) ;
	
	public Omega8x8MZISwitch(
			@ParamName(name="Set Wavelength (nm)") Wavelength inputLambda,
			@ParamName(name="Set Waveguide Properties") WgProperties wgProp,
			@ParamName(name="Port 1 Input Source") AbstractInputSource inputSource1,
			@ParamName(name="Port 2 Input Source") AbstractInputSource inputSource2,
			@ParamName(name="Port 3 Input Source") AbstractInputSource inputSource3,
			@ParamName(name="Port 4 Input Source") AbstractInputSource inputSource4,
			@ParamName(name="Port 5 Input Source") AbstractInputSource inputSource5,
			@ParamName(name="Port 6 Input Source") AbstractInputSource inputSource6,
			@ParamName(name="Port 7 Input Source") AbstractInputSource inputSource7,
			@ParamName(name="Port 8 Input Source") AbstractInputSource inputSource8,
			@ParamName(name="Set the length of MZI (micron)", default_="300") double lengthMicron,
			@ParamName(name="Input Coupler") CompactCoupler DC1,
			@ParamName(name="Output Coupler") CompactCoupler DC2,
			@ParamName(name="Set Waveguide Crossing") SimpleCrossing wgCrossing, 
			@ParamName(name="State of Switch Row 1 Column 1 (true = CROSS, false = BAR)") boolean swR1C1isCross,
			@ParamName(name="State of Switch Row 1 Column 2 (true = CROSS, false = BAR)") boolean swR1C2isCross,
			@ParamName(name="State of Switch Row 1 Column 3 (true = CROSS, false = BAR)") boolean swR1C3isCross,
			@ParamName(name="State of Switch Row 2 Column 1 (true = CROSS, false = BAR)") boolean swR2C1isCross,
			@ParamName(name="State of Switch Row 2 Column 2 (true = CROSS, false = BAR)") boolean swR2C2isCross,
			@ParamName(name="State of Switch Row 2 Column 3 (true = CROSS, false = BAR)") boolean swR2C3isCross,
			@ParamName(name="State of Switch Row 3 Column 1 (true = CROSS, false = BAR)") boolean swR3C1isCross,
			@ParamName(name="State of Switch Row 3 Column 2 (true = CROSS, false = BAR)") boolean swR3C2isCross,
			@ParamName(name="State of Switch Row 3 Column 3 (true = CROSS, false = BAR)") boolean swR3C3isCross,
			@ParamName(name="State of Switch Row 4 Column 1 (true = CROSS, false = BAR)") boolean swR4C1isCross,
			@ParamName(name="State of Switch Row 4 Column 2 (true = CROSS, false = BAR)") boolean swR4C2isCross,
			@ParamName(name="State of Switch Row 4 Column 3 (true = CROSS, false = BAR)") boolean swR4C3isCross
			){
		this.inputLambda = inputLambda ;
		this.wgCrossing = wgCrossing ;
		this.port1In = inputSource1.getElectricFieldAtInputWavelength(inputLambda) ;
		this.port2In = inputSource2.getElectricFieldAtInputWavelength(inputLambda) ;
		this.port3In = inputSource3.getElectricFieldAtInputWavelength(inputLambda) ;
		this.port4In = inputSource4.getElectricFieldAtInputWavelength(inputLambda) ;
		this.port5In = inputSource5.getElectricFieldAtInputWavelength(inputLambda) ;
		this.port6In = inputSource6.getElectricFieldAtInputWavelength(inputLambda) ;
		this.port7In = inputSource7.getElectricFieldAtInputWavelength(inputLambda) ;
		this.port8In = inputSource8.getElectricFieldAtInputWavelength(inputLambda) ;
		this.swCross = new Switch2x2MZI(inputLambda, wgProp, lengthMicron, DC1, DC2, true, false) ;
		this.swBar = new Switch2x2MZI(inputLambda, wgProp, lengthMicron, DC1, DC2, false, true) ;
		if(swR1C1isCross){this.state[0] = "C";} else{this.state[0] = "B";} ;
		if(swR1C2isCross){this.state[1] = "C";} else{this.state[1] = "B";} ;
		if(swR1C3isCross){this.state[2] = "C";} else{this.state[2] = "B";} ;


		if(swR2C1isCross){this.state[3] = "C";} else{this.state[3] = "B";} ;
		if(swR2C2isCross){this.state[4] = "C";} else{this.state[4] = "B";} ;
		if(swR2C3isCross){this.state[5] = "C";} else{this.state[5] = "B";} ;


		if(swR3C1isCross){this.state[6] = "C";} else{this.state[6] = "B";} ;
		if(swR3C2isCross){this.state[7] = "C";} else{this.state[7] = "B";} ;
		if(swR3C3isCross){this.state[8] = "C";} else{this.state[8] = "B";} ;
	
	
		if(swR4C1isCross){this.state[9] = "C";} else{this.state[9] = "B";} ;
		if(swR4C2isCross){this.state[10] = "C";} else{this.state[10] = "B";} ;
		if(swR4C3isCross){this.state[11] = "C";} else{this.state[11] = "B";} ;
	
	
		this.stateSource[0] = inputSource1.getInputSourceState() ;
		this.stateSource[1] = inputSource2.getInputSourceState() ;
		this.stateSource[2] = inputSource3.getInputSourceState() ;
		this.stateSource[3] = inputSource4.getInputSourceState() ;
		this.stateSource[4] = inputSource5.getInputSourceState() ;
		this.stateSource[5] = inputSource6.getInputSourceState() ;
		this.stateSource[6] = inputSource7.getInputSourceState() ;
		this.stateSource[7] = inputSource8.getInputSourceState() ;
		for(int i=0; i<4; i++){
			for(int j=0; j<3; j++){
				if(this.state[3*i+j]=="C") {
					this.tCross[i][j] = this.swCross.S31 ;
					this.tBar[i][j] = this.swCross.S21 ;
				}
				else{
					this.tCross[i][j] = this.swBar.S31 ;
					this.tBar[i][j] = this.swBar.S21 ;
				}
			}
		}
	}
	
	
	public Complex[] getColumnSwitchPorts(Complex[] portsIn, int columnNum){
		int n = columnNum ;
		Complex[] portsOut = new Complex[8] ;
		portsOut[0] = portsIn[0].times(tBar[0][n-1]).plus(portsIn[1].times(tCross[0][n-1])) ;
		portsOut[1] = portsIn[1].times(tBar[0][n-1]).plus(portsIn[0].times(tCross[0][n-1])) ;
		portsOut[2] = portsIn[2].times(tBar[1][n-1]).plus(portsIn[3].times(tCross[1][n-1])) ;
		portsOut[3] = portsIn[3].times(tBar[1][n-1]).plus(portsIn[2].times(tCross[1][n-1])) ;
		portsOut[4] = portsIn[4].times(tBar[2][n-1]).plus(portsIn[5].times(tCross[2][n-1])) ;
		portsOut[5] = portsIn[5].times(tBar[2][n-1]).plus(portsIn[4].times(tCross[2][n-1])) ;
		portsOut[6] = portsIn[6].times(tBar[3][n-1]).plus(portsIn[7].times(tCross[3][n-1])) ;
		portsOut[7] = portsIn[7].times(tBar[3][n-1]).plus(portsIn[6].times(tCross[3][n-1])) ;
		return portsOut ;
	}
	

	@SuppressWarnings("unused")
	public Complex[] getCrossingPorts(Complex[] portsIn){
		Complex[] portsOut = {portsIn[0], zero, zero, zero, zero, zero, zero, portsIn[7]} ;
		SimpleCrossing X1 = wgCrossing, X2 = wgCrossing, X3 = wgCrossing, X4 = wgCrossing, X5=wgCrossing, X6=wgCrossing ;
		Complex X1_port1_out = zero, X1_port2_out = zero, X1_port3_out = zero, X1_port4_out = zero ;
		Complex X2_port1_out = zero, X2_port2_out = zero, X2_port3_out = zero, X2_port4_out = zero ;
		Complex X3_port1_out = zero, X3_port2_out = zero, X3_port3_out = zero, X3_port4_out = zero ;
		Complex X4_port1_out = zero, X4_port2_out = zero, X4_port3_out = zero, X4_port4_out = zero ;
		Complex X5_port1_out = zero, X5_port2_out = zero, X5_port3_out = zero, X5_port4_out = zero ;
		Complex X6_port1_out = zero, X6_port2_out = zero, X6_port3_out = zero, X6_port4_out = zero ;
		int steps = 10 ;
		for(int i=0; i<steps; i++){
			X1_port1_out = X1.getPort1(portsIn[1], zero, zero, X2_port2_out) ;
			X1_port2_out = X1.getPort2(portsIn[1], zero, zero, X2_port2_out) ;
			X1_port3_out = X1.getPort3(portsIn[1], zero, zero, X2_port2_out) ;
			X1_port4_out = X1.getPort4(portsIn[1], zero, zero, X2_port2_out) ;
			
			X2_port1_out = X2.getPort1(portsIn[2], X1_port4_out, X3_port1_out, X4_port2_out) ;
			X2_port2_out = X2.getPort2(portsIn[2], X1_port4_out, X3_port1_out, X4_port2_out) ;
			X2_port3_out = X2.getPort3(portsIn[2], X1_port4_out, X3_port1_out, X4_port2_out) ;
			X2_port4_out = X2.getPort4(portsIn[2], X1_port4_out, X3_port1_out, X4_port2_out) ;
			
			X3_port1_out = X3.getPort1(X2_port3_out, zero, zero, X5_port2_out) ;
			X3_port2_out = X3.getPort2(X2_port3_out, zero, zero, X5_port2_out) ;
			X3_port3_out = X3.getPort3(X2_port3_out, zero, zero, X5_port2_out) ;
			X3_port4_out = X3.getPort4(X2_port3_out, zero, zero, X5_port2_out) ;
			
			X4_port1_out = X4.getPort1(portsIn[3], X2_port4_out, X5_port1_out, portsIn[4]) ;
			X4_port2_out = X4.getPort2(portsIn[3], X2_port4_out, X5_port1_out, portsIn[4]) ;
			X4_port3_out = X4.getPort3(portsIn[3], X2_port4_out, X5_port1_out, portsIn[4]) ;
			X4_port4_out = X4.getPort4(portsIn[3], X2_port4_out, X5_port1_out, portsIn[4]) ;
			
			X5_port1_out = X5.getPort1(X4_port3_out, X3_port4_out, X6_port1_out, portsIn[5]) ;
			X5_port2_out = X5.getPort2(X4_port3_out, X3_port4_out, X6_port1_out, portsIn[5]) ;
			X5_port3_out = X5.getPort3(X4_port3_out, X3_port4_out, X6_port1_out, portsIn[5]) ;
			X5_port4_out = X5.getPort4(X4_port3_out, X3_port4_out, X6_port1_out, portsIn[5]) ;
			
			X6_port1_out = X6.getPort1(X5_port3_out, zero, zero, portsIn[6]) ;
			X6_port2_out = X6.getPort2(X5_port3_out, zero, zero, portsIn[6]) ;
			X6_port3_out = X6.getPort3(X5_port3_out, zero, zero, portsIn[6]) ;
			X6_port4_out = X6.getPort4(X5_port3_out, zero, zero, portsIn[6]) ;
			
			for(int j=0; j<portsIn.length; j++){portsIn[j] = zero;} ;
			
			portsOut[0] = portsOut[0].plus(portsIn[0]) ; 
			portsOut[1] = portsOut[1].plus(X1_port2_out) ;
			portsOut[2] = portsOut[2].plus(X1_port3_out) ;
			portsOut[3] = portsOut[3].plus(X3_port2_out) ;
			portsOut[4] = portsOut[4].plus(X3_port3_out) ;
			portsOut[5] = portsOut[5].plus(X6_port2_out) ;
			portsOut[6] = portsOut[6].plus(X6_port3_out) ;
			portsOut[7] = portsOut[7].plus(portsIn[7]) ;
			
		}
		
		return portsOut ;
	}

	@SuppressWarnings("unused")
	public void buildSwitch(){
		
		String stateSwitches = state[0]+state[1]+state[2]+state[3]+state[4]+state[5]+state[6]+state[7]+state[8]+state[9]+state[10]+state[11];
		
		String stateOfSources = stateSource[0]+stateSource[1]+stateSource[2]+stateSource[3]+stateSource[4]+stateSource[5]+stateSource[6]+stateSource[7] ;
		
		// Stage 0 ports
		Complex[] ports0 = {port1In, port2In, port3In, port4In, port5In, port6In, port7In, port8In} ;
		
		// Stage 1
		Complex[] ports1 = getCrossingPorts(ports0) ;
		
		// Stage 2
		Complex[] ports2 = getColumnSwitchPorts(ports1, 1) ;
		
		// Stage 3
		Complex[] ports3 = getCrossingPorts(ports2) ;
		
		// Stage 4
		Complex[] ports4 = getColumnSwitchPorts(ports3, 2) ;
		
		// Stage 5
		Complex[] ports5 = getCrossingPorts(ports4) ;
		
		// Stage 6
		Complex[] ports6 = getColumnSwitchPorts(ports5, 3) ;
		
	}
	
	
}




























