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

public class PILOSS8x8MZISwitch implements Experiment {

	Wavelength inputLambda ;
	SimpleCrossing wgCrossing ;
	Switch2x2MZI swCross, swBar ;
	String[] state = new String[20] ; // shows the state of each switch
	Complex[][] tCross = new Complex[4][4] ;
	Complex[][] tBar = new Complex[4][4] ;
	Complex zero = new Complex(0,0), one = new Complex(1,0) ;
	int inPortNumber ;
	Crossing8x8PILOSS crossingCell ;
	
	public PILOSS8x8MZISwitch(
			@ParamName(name="Set Wavelength (nm)") Wavelength inputLambda,
			@ParamName(name="Set Waveguide Properties") WgProperties wgProp,
			@ParamName(name="Input Coupler") CompactCoupler DC1,
			@ParamName(name="Output Coupler") CompactCoupler DC2,
			@ParamName(name="Waveguide Crossing Model") SimpleCrossing wgCrossing,
			@ParamName(name="Input Port Number") int inPortNumber,
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
		this.inputLambda = inputLambda ;
		this.wgCrossing = wgCrossing ;
		this.swCross = new Switch2x2MZI(inputLambda, wgProp, 300, DC1, DC2, true, false) ;
		this.swBar = new Switch2x2MZI(inputLambda, wgProp, 300, DC1, DC2, false, true) ;
		this.inPortNumber = inPortNumber ;
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
	

	public Complex[] getAllOutputPorts(){
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


	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {
		DataPoint dp = new DataPoint() ;
		dp.addProperty("Switch state", getSwitchState());
		if(MoreMath.Conversions.todB(getAllOutputPorts()[0].absSquared()) >= -15){
		dp.addResultProperty("Port 1 out (dBm)", MoreMath.Conversions.todB(getAllOutputPorts()[0].absSquared())); }
		if(MoreMath.Conversions.todB(getAllOutputPorts()[1].absSquared()) >= -15){
		dp.addResultProperty("Port 2 out (dBm)", MoreMath.Conversions.todB(getAllOutputPorts()[1].absSquared()));}
		if(MoreMath.Conversions.todB(getAllOutputPorts()[2].absSquared()) >= -15){
		dp.addResultProperty("Port 3 out (dBm)", MoreMath.Conversions.todB(getAllOutputPorts()[2].absSquared()));}
		if(MoreMath.Conversions.todB(getAllOutputPorts()[3].absSquared()) >= -15){
		dp.addResultProperty("Port 4 out (dBm)", MoreMath.Conversions.todB(getAllOutputPorts()[3].absSquared()));}
		if(MoreMath.Conversions.todB(getAllOutputPorts()[4].absSquared()) >= -15){
		dp.addResultProperty("Port 5 out (dBm)", MoreMath.Conversions.todB(getAllOutputPorts()[4].absSquared()));}
		if(MoreMath.Conversions.todB(getAllOutputPorts()[5].absSquared()) >= -15){
		dp.addResultProperty("Port 6 out (dBm)", MoreMath.Conversions.todB(getAllOutputPorts()[5].absSquared()));}
		if(MoreMath.Conversions.todB(getAllOutputPorts()[6].absSquared()) >= -15){
		dp.addResultProperty("Port 7 out (dBm)", MoreMath.Conversions.todB(getAllOutputPorts()[6].absSquared()));}
		if(MoreMath.Conversions.todB(getAllOutputPorts()[7].absSquared()) >= -15){
		dp.addResultProperty("Port 8 out (dBm)", MoreMath.Conversions.todB(getAllOutputPorts()[7].absSquared()));}
		man.addDataPoint(dp);
	}

	
}
