package Simulations.switches.piloss;

import PhotonicElements.DirectionalCoupler.CompactCoupler;
import PhotonicElements.Junctions.Crossings.SimpleCrossing;
import PhotonicElements.Switches.PILOSS.Crossings.Crossing8x8PILOSS;
import PhotonicElements.Switches.Switch2x2.Switch2x2MZI;
import PhotonicElements.Utilities.MathLibraries.Complex;
import PhotonicElements.Utilities.MathLibraries.MoreMath;
import PhotonicElements.Utilities.Wavelength;
import PhotonicElements.Waveguides.WaveguideProperties.WgProperties;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;

public class PILOSS8x8MZISwitchMapping implements Experiment {

	Wavelength inputLambda ;
	SimpleCrossing wgCrossing ;
	Switch2x2MZI swCross, swBar ;
	String[] state = new String[20] ; // shows the state of each switch
	Complex[][] tCross = new Complex[4][4] ;
	Complex[][] tBar = new Complex[4][4] ;
	Complex zero = new Complex(0,0), one = new Complex(1,0) ;
	int inPortNumber ;
	Crossing8x8PILOSS crossingCell ;
	int port1Map, port2Map, port3Map, port4Map, port5Map, port6Map, port7Map, port8Map ;
	
	public PILOSS8x8MZISwitchMapping(
			@ParamName(name="Mapping of Port 1") int port1Map,
			@ParamName(name="Mapping of Port 2") int port2Map,
			@ParamName(name="Mapping of Port 3") int port3Map,
			@ParamName(name="Mapping of Port 4") int port4Map,
			@ParamName(name="Mapping of Port 5") int port5Map,
			@ParamName(name="Mapping of Port 6") int port6Map,
			@ParamName(name="Mapping of Port 7") int port7Map,
			@ParamName(name="Mapping of Port 8") int port8Map,
			@ParamName(name="Set Wavelength (nm)") Wavelength inputLambda,
			@ParamName(name="Set Waveguide Properties") WgProperties wgProp,
			@ParamName(name="Input Coupler") CompactCoupler DC1,
			@ParamName(name="Output Coupler") CompactCoupler DC2,
			@ParamName(name="Waveguide Crossing Model") SimpleCrossing wgCrossing,
			@ParamName(name="State of Switch Row 1 Column 1 (true = CROSS, false = BAR)") boolean swR1C1isCross,
			@ParamName(name="State of Switch Row 1 Column 2 (true = CROSS, false = BAR)") boolean swR1C2isCross,
			@ParamName(name="State of Switch Row 1 Column 3 (true = CROSS, false = BAR)") boolean swR1C3isCross,
			@ParamName(name="State of Switch Row 1 Column 4 (true = CROSS, false = BAR)") boolean swR1C4isCross,
			@ParamName(name="State of Switch Row 2 Column 1 (true = CROSS, false = BAR)") boolean swR2C1isCross,
			@ParamName(name="State of Switch Row 2 Column 2 (true = CROSS, false = BAR)") boolean swR2C2isCross,
			@ParamName(name="State of Switch Row 2 Column 3 (true = CROSS, false = BAR)") boolean swR2C3isCross,
			@ParamName(name="State of Switch Row 2 Column 4 (true = CROSS, false = BAR)") boolean swR2C4isCross,
			@ParamName(name="State of Switch Row 3 Column 1 (true = CROSS, false = BAR)") boolean swR3C1isCross,
			@ParamName(name="State of Switch Row 3 Column 2 (true = CROSS, false = BAR)") boolean swR3C2isCross,
			@ParamName(name="State of Switch Row 3 Column 3 (true = CROSS, false = BAR)") boolean swR3C3isCross,
			@ParamName(name="State of Switch Row 3 Column 4 (true = CROSS, false = BAR)") boolean swR3C4isCross,
			@ParamName(name="State of Switch Row 4 Column 1 (true = CROSS, false = BAR)") boolean swR4C1isCross,
			@ParamName(name="State of Switch Row 4 Column 2 (true = CROSS, false = BAR)") boolean swR4C2isCross,
			@ParamName(name="State of Switch Row 4 Column 3 (true = CROSS, false = BAR)") boolean swR4C3isCross,
			@ParamName(name="State of Switch Row 4 Column 4 (true = CROSS, false = BAR)") boolean swR4C4isCross
			){
		this.port1Map = port1Map ;
		this.port2Map = port2Map ;
		this.port3Map = port3Map ;
		this.port4Map = port4Map ;
		this.port5Map = port5Map ;
		this.port6Map = port6Map ;
		this.port7Map = port7Map ;
		this.port8Map = port8Map ;
		this.inputLambda = inputLambda ;
		this.wgCrossing = wgCrossing ;
		this.swCross = new Switch2x2MZI(inputLambda, wgProp, 300, DC1, DC2, true, false) ;
		this.swBar = new Switch2x2MZI(inputLambda, wgProp, 300, DC1, DC2, false, true) ;
		if(swR1C1isCross){this.state[0] = "C";} else{this.state[0] = "B";} ;
		if(swR1C2isCross){this.state[1] = "C";} else{this.state[1] = "B";} ;
		if(swR1C3isCross){this.state[2] = "C";} else{this.state[2] = "B";} ;
		if(swR1C4isCross){this.state[3] = "C";} else{this.state[3] = "B";} ;
		if(swR2C1isCross){this.state[4] = "C";} else{this.state[4] = "B";} ;
		if(swR2C2isCross){this.state[5] = "C";} else{this.state[5] = "B";} ;
		if(swR2C3isCross){this.state[6] = "C";} else{this.state[6] = "B";} ;
		if(swR2C4isCross){this.state[7] = "C";} else{this.state[7] = "B";} ;
		if(swR3C1isCross){this.state[8] = "C";} else{this.state[8] = "B";} ;
		if(swR3C2isCross){this.state[9] = "C";} else{this.state[9] = "B";} ;
		if(swR3C3isCross){this.state[10] = "C";} else{this.state[10] = "B";} ;
		if(swR3C4isCross){this.state[11] = "C";} else{this.state[11] = "B";} ;
		if(swR4C1isCross){this.state[12] = "C";} else{this.state[12] = "B";} ;
		if(swR4C2isCross){this.state[13] = "C";} else{this.state[13] = "B";} ;
		if(swR4C3isCross){this.state[14] = "C";} else{this.state[14] = "B";} ;
		if(swR4C4isCross){this.state[15] = "C";} else{this.state[15] = "B";} ;
		for(int i=0; i<4; i++){
			for(int j=0; j<4; j++){
				if(this.state[4*i+j]=="C") {
					this.tCross[i][j] = this.swCross.S31 ;
					this.tBar[i][j] = this.swCross.S21 ;
				}
				else{
					this.tCross[i][j] = this.swBar.S31 ;
					this.tBar[i][j] = this.swBar.S21 ;
				}
			}
		}
		
		crossingCell = new Crossing8x8PILOSS(inputLambda, wgProp, wgCrossing) ;
		
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
	

	public Complex[] getAllOutputPorts(int inPortNumber){
		// Stage 0 ports
		Complex[] ports0 = new Complex[8] ;
		for(int i=0; i<8; i++){
			if(i==inPortNumber-1){ports0[i] = one ;}
			else{ports0[i] = zero ;}
		}
		// Stage 1
		Complex[] ports1 = getColumnSwitchPorts(ports0, 1) ;
		// Stage 2
		Complex[] ports2 = crossingCell.getCrossingPorts(ports1) ;
		// Stage 3
		Complex[] ports3 = getColumnSwitchPorts(ports2, 2) ;
		// Stage 4
		Complex[] ports4 = crossingCell.getCrossingPorts(ports3) ;
		// Stage 5
		Complex[] ports5 = getColumnSwitchPorts(ports4, 3) ;
		// Stage 6
		Complex[] ports6 = crossingCell.getCrossingPorts(ports5) ;
		// Stage 7
		Complex[] ports7 = getColumnSwitchPorts(ports6, 4) ;
		
		return ports7 ;
	}
	
	public String getSwitchState(){
		String stateSwitches = state[0]+state[4]+state[8]+state[12]+"|"+state[1]+state[5]+state[9]+state[13]+"|"+state[2]+state[6]+state[10]+state[14]+"|"+state[3]+state[7]+state[11]
				+state[15] ;
		return stateSwitches ;
	}

	public Complex getOutputPort(int inPortNumber, int outPortNumber){
		return getAllOutputPorts(inPortNumber)[outPortNumber-1] ;
	}

	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {
		DataPoint dp = new DataPoint() ;
		double port1dBm = MoreMath.Conversions.todB(getOutputPort(1, port1Map).absSquared()) ;
		double port2dBm = MoreMath.Conversions.todB(getOutputPort(2, port2Map).absSquared()) ;
		double port3dBm = MoreMath.Conversions.todB(getOutputPort(3, port3Map).absSquared()) ;
		double port4dBm = MoreMath.Conversions.todB(getOutputPort(4, port4Map).absSquared()) ;
		double port5dBm = MoreMath.Conversions.todB(getOutputPort(5, port5Map).absSquared()) ;
		double port6dBm = MoreMath.Conversions.todB(getOutputPort(6, port6Map).absSquared()) ;
		double port7dBm = MoreMath.Conversions.todB(getOutputPort(7, port7Map).absSquared()) ;
		double port8dBm = MoreMath.Conversions.todB(getOutputPort(8, port8Map).absSquared()) ;
		if(port1dBm>-10 && port2dBm>-10 && port3dBm>-10 && port4dBm>-10 && port5dBm>-10 && port6dBm>-10 && port7dBm>-10 && port8dBm>-10){
			dp.addProperty("State of Switch", getSwitchState());
			dp.addResultProperty("Port "+ port1Map +" out (dBm)", port1dBm);
			dp.addResultProperty("Port "+ port2Map +" out (dBm)", port2dBm);
			dp.addResultProperty("Port "+ port3Map +" out (dBm)", port3dBm);
			dp.addResultProperty("Port "+ port4Map +" out (dBm)", port4dBm);
			dp.addResultProperty("Port "+ port5Map +" out (dBm)", port5dBm);
			dp.addResultProperty("Port "+ port6Map +" out (dBm)", port6dBm);
			dp.addResultProperty("Port "+ port7Map +" out (dBm)", port7dBm);
			dp.addResultProperty("Port "+ port8Map +" out (dBm)", port8dBm);
		}
		man.addDataPoint(dp);
	}

}
