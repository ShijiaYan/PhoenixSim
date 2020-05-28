package People.Qixiang.Mapping;

import java.io.File;

import People.Qixiang.Benes.CrossingCells.Crossing8x8BenesType1;
import People.Qixiang.Benes.CrossingCells.Crossing8x8BenesType2;
import People.Qixiang.Benes.CrossingCells.Crossing8x8BenesType3;
import People.Qixiang.Benes.MZI2x2.Switch2x2MZI_qixiang;
import PhotonicElements.DirectionalCoupler.CompactCoupler;
import PhotonicElements.Junctions.Crossings.SimpleCrossing;
import PhotonicElements.Utilities.MathLibraries.Complex;
import PhotonicElements.Utilities.MathLibraries.ComplexMatrix;
import PhotonicElements.Utilities.MathLibraries.MoreMath;
import PhotonicElements.Utilities.Wavelength;
import PhotonicElements.Waveguides.WaveguideProperties.WgProperties;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import flanagan.io.FileOutput;

public class Benes8x8MZISwitch_AllConfig_RMSE implements Experiment{

	Wavelength inputLambda ;
	SimpleCrossing wgCrossing ;
	Switch2x2MZI_qixiang swCross, swBar ;
	String[] state = new String[20] ; // shows the state of each switch
	Complex[][] tCross = new Complex[4][5] ;
	Complex[][] tBar = new Complex[4][5] ;
	Complex zero = new Complex(0,0), one = new Complex(1,0) ;
	Crossing8x8BenesType1 crossingType1 ;
	Crossing8x8BenesType2 crossingType2 ;
	Crossing8x8BenesType3 crossingType3 ;

	public Benes8x8MZISwitch_AllConfig_RMSE(
			@ParamName(name="Set Wavelength (nm)") Wavelength inputLambda,
			@ParamName(name="Set Waveguide Properties") WgProperties wgProp,
			@ParamName(name="Input Coupler") CompactCoupler DC1,
			@ParamName(name="Output Coupler") CompactCoupler DC2,
			@ParamName(name="Waveguide Crossing Model") SimpleCrossing wgCrossing,
			@ParamName(name="State of Switch Row 1 Column 1 (true = CROSS, false = BAR)") boolean swR1C1isCross,
			@ParamName(name="State of Switch Row 1 Column 2 (true = CROSS, false = BAR)") boolean swR1C2isCross,
			@ParamName(name="State of Switch Row 1 Column 3 (true = CROSS, false = BAR)") boolean swR1C3isCross,
			@ParamName(name="State of Switch Row 1 Column 4 (true = CROSS, false = BAR)") boolean swR1C4isCross,
			@ParamName(name="State of Switch Row 1 Column 5 (true = CROSS, false = BAR)") boolean swR1C5isCross,
			@ParamName(name="State of Switch Row 2 Column 1 (true = CROSS, false = BAR)") boolean swR2C1isCross,
			@ParamName(name="State of Switch Row 2 Column 2 (true = CROSS, false = BAR)") boolean swR2C2isCross,
			@ParamName(name="State of Switch Row 2 Column 3 (true = CROSS, false = BAR)") boolean swR2C3isCross,
			@ParamName(name="State of Switch Row 2 Column 4 (true = CROSS, false = BAR)") boolean swR2C4isCross,
			@ParamName(name="State of Switch Row 2 Column 5 (true = CROSS, false = BAR)") boolean swR2C5isCross,
			@ParamName(name="State of Switch Row 3 Column 1 (true = CROSS, false = BAR)") boolean swR3C1isCross,
			@ParamName(name="State of Switch Row 3 Column 2 (true = CROSS, false = BAR)") boolean swR3C2isCross,
			@ParamName(name="State of Switch Row 3 Column 3 (true = CROSS, false = BAR)") boolean swR3C3isCross,
			@ParamName(name="State of Switch Row 3 Column 4 (true = CROSS, false = BAR)") boolean swR3C4isCross,
			@ParamName(name="State of Switch Row 3 Column 5 (true = CROSS, false = BAR)") boolean swR3C5isCross,
			@ParamName(name="State of Switch Row 4 Column 1 (true = CROSS, false = BAR)") boolean swR4C1isCross,
			@ParamName(name="State of Switch Row 4 Column 2 (true = CROSS, false = BAR)") boolean swR4C2isCross,
			@ParamName(name="State of Switch Row 4 Column 3 (true = CROSS, false = BAR)") boolean swR4C3isCross,
			@ParamName(name="State of Switch Row 4 Column 4 (true = CROSS, false = BAR)") boolean swR4C4isCross,
			@ParamName(name="State of Switch Row 4 Column 5 (true = CROSS, false = BAR)") boolean swR4C5isCross
			){
		this.inputLambda = inputLambda ;
		this.wgCrossing = wgCrossing ;
		this.swCross = new Switch2x2MZI_qixiang(inputLambda, wgProp, 300, DC1, DC2, true, false) ;
		this.swBar = new Switch2x2MZI_qixiang(inputLambda, wgProp, 300, DC1, DC2, false, true) ;
		if(swR1C1isCross){this.state[0] = "C";} else{this.state[0] = "B";} ;
		if(swR1C2isCross){this.state[1] = "C";} else{this.state[1] = "B";} ;
		if(swR1C3isCross){this.state[2] = "C";} else{this.state[2] = "B";} ;
		if(swR1C4isCross){this.state[3] = "C";} else{this.state[3] = "B";} ;
		if(swR1C5isCross){this.state[4] = "C";} else{this.state[4] = "B";} ;
		if(swR2C1isCross){this.state[5] = "C";} else{this.state[5] = "B";} ;
		if(swR2C2isCross){this.state[6] = "C";} else{this.state[6] = "B";} ;
		if(swR2C3isCross){this.state[7] = "C";} else{this.state[7] = "B";} ;
		if(swR2C4isCross){this.state[8] = "C";} else{this.state[8] = "B";} ;
		if(swR2C5isCross){this.state[9] = "C";} else{this.state[9] = "B";} ;
		if(swR3C1isCross){this.state[10] = "C";} else{this.state[10] = "B";} ;
		if(swR3C2isCross){this.state[11] = "C";} else{this.state[11] = "B";} ;
		if(swR3C3isCross){this.state[12] = "C";} else{this.state[12] = "B";} ;
		if(swR3C4isCross){this.state[13] = "C";} else{this.state[13] = "B";} ;
		if(swR3C5isCross){this.state[14] = "C";} else{this.state[14] = "B";} ;
		if(swR4C1isCross){this.state[15] = "C";} else{this.state[15] = "B";} ;
		if(swR4C2isCross){this.state[16] = "C";} else{this.state[16] = "B";} ;
		if(swR4C3isCross){this.state[17] = "C";} else{this.state[17] = "B";} ;
		if(swR4C4isCross){this.state[18] = "C";} else{this.state[18] = "B";} ;
		if(swR4C5isCross){this.state[19] = "C";} else{this.state[19] = "B";} ;
		for(int i=0; i<4; i++){
			for(int j=0; j<5; j++){
				if(this.state[5*i+j]=="C") {
					this.tCross[i][j] = this.swCross.getS31() ;
					this.tBar[i][j] = this.swCross.getS21() ;
				}
				else{
					this.tCross[i][j] = this.swBar.getS31() ;
					this.tBar[i][j] = this.swBar.getS21() ;
				}
			}
		}

		crossingType1 = new Crossing8x8BenesType1(inputLambda, wgProp, wgCrossing) ;
		crossingType2 = new Crossing8x8BenesType2(inputLambda, wgProp, wgCrossing) ;
		crossingType3 = new Crossing8x8BenesType3(inputLambda, wgProp, wgCrossing) ;

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

	// I need to generate the transition matrix for each switch column

	public ComplexMatrix getColumnTransitionMatrix(int columnNum){
		double[][] elements = new double[8][8] ;
		double[] r1 = {1,0,0,0,0,0,0,0} ;
		double[] r2 = {0,1,0,0,0,0,0,0} ;
		double[] r3 = {0,0,1,0,0,0,0,0} ;
		double[] r4 = {0,0,0,1,0,0,0,0} ;
		double[] r5 = {0,0,0,0,1,0,0,0} ;
		double[] r6 = {0,0,0,0,0,1,0,0} ;
		double[] r7 = {0,0,0,0,0,0,1,0} ;
		double[] r8 = {0,0,0,0,0,0,0,1} ;
		int m = columnNum ;
		String sw1 = state[m-1];
		String sw2 = state[5+m-1];
		String sw3 = state[10+m-1];
		String sw4 = state[15+m-1] ;
		// for the first switch
		if(sw1.equals("B")){
			elements[0] = r1 ;
			elements[1] = r2 ;
		}
		else{
			elements[0] = r2 ;
			elements[1] = r1 ;
		}
		// for the second switch
		if(sw2.equals("B")){
			elements[2] = r3 ;
			elements[3] = r4 ;
		}
		else{
			elements[2] = r4 ;
			elements[3] = r3 ;
		}
		// for the third switch
		if(sw3.equals("B")){
			elements[4] = r5 ;
			elements[5] = r6 ;
		}
		else{
			elements[4] = r6 ;
			elements[5] = r5 ;
		}
		// for the fourth switch
		if(sw4.equals("B")){
			elements[6] = r7 ;
			elements[7] = r8 ;
		}
		else{
			elements[6] = r8 ;
			elements[7] = r7 ;
		}

		ComplexMatrix T = new ComplexMatrix(elements) ;
		return T ;
	}

	@SuppressWarnings("static-access")
	public ComplexMatrix getSwitchTransitionMatrix(){
		ComplexMatrix T = new ComplexMatrix(8,8).identity(8) ;
		T = T.times(getColumnTransitionMatrix(5)).times(crossingType3.getTransitionMatrix()) ;
		T = T.times(getColumnTransitionMatrix(4)).times(crossingType2.getTransitionMatrix()) ;
		T = T.times(getColumnTransitionMatrix(3)).times(crossingType2.getTransitionMatrix()) ;
		T = T.times(getColumnTransitionMatrix(2)).times(crossingType1.getTransitionMatrix()) ;
		T = T.times(getColumnTransitionMatrix(1)) ;
		return T ;
	}

	public String findMapping(){
		String mapping = "" ;
		ComplexMatrix T = getSwitchTransitionMatrix() ;
		int[] index = new int[8] ;
		for(int j=0; j<8; j++){
			for(int i=0; i<8; i++){
				if(T.getElement(i, j).equals(one)) {index[j] = i+1 ;}
			}
		}
		mapping += "(" + index[0] + "," + index[1] + "," + index[2] + "," + index[3] + "," +
						 index[4] + "," + index[5] + "," + index[6] + "," + index[7] + ")" ;
		return mapping ;
	}

	public int[] findMappingArray(){
		ComplexMatrix T = getSwitchTransitionMatrix() ;
		int[] index = new int[8] ;
		for(int j=0; j<8; j++){
			for(int i=0; i<8; i++){
				if(T.getElement(i, j).equals(one)) {index[j] = i+1 ;}
			}
		}
		return index ;
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
		Complex[] ports2 = crossingType1.getCrossingPorts(ports1) ;
		// Stage 3
		Complex[] ports3 = getColumnSwitchPorts(ports2, 2) ;
		// Stage 4
		Complex[] ports4 = crossingType2.getCrossingPorts(ports3) ;
		// Stage 5
		Complex[] ports5 = getColumnSwitchPorts(ports4, 3) ;
		// Stage 6
		Complex[] ports6 = crossingType2.getCrossingPorts(ports5) ;
		// Stage 7
		Complex[] ports7 = getColumnSwitchPorts(ports6, 4) ;
		// Stage 8
		Complex[] ports8 = crossingType3.getCrossingPorts(ports7) ;
		// Stage 9
		Complex[] ports9 = getColumnSwitchPorts(ports8, 5) ;

		return ports9 ;
	}

	public String getSwitchState(){
		String stateSwitches = state[0]+state[5]+state[10]+state[15]+ "|" +
					           state[1]+state[6]+state[11]+state[16]+ "|" +
					           state[2]+state[7]+state[12]+state[17]+ "|" +
					           state[3]+state[8]+state[13]+state[18]+ "|" +
					           state[4]+state[9]+state[14]+state[19] ;
		return stateSwitches ;
	}

	public String getSwitchStateBinary(){
		char[] state = getSwitchState().toCharArray() ;
		String stateBinary = "" ;
        for (char c : state) {
            if (c == 'B') {
                stateBinary += "0";
            }
            if (c == 'C') {
                stateBinary += '1';
            }
        }
		return stateBinary ;
	}

	public Complex getOutputPort(int inPortNumber, int outPortNumber){
		return getAllOutputPorts(inPortNumber)[outPortNumber-1] ;
	}

	public double getOutputPort_dB(int inPortNumber, int outPortNumber){
		double powerdB = MoreMath.Conversions.todB(getOutputPort(inPortNumber, outPortNumber).absSquared()) ;
		return powerdB ;
	}


	public double getXtalkPower(int inPortNumber, int outPortNumber){
		double xtalkPower = 0 ;
		int M = 8 ;
		for(int i=0; i<M; i++){
			if(i!=inPortNumber-1){xtalkPower += getOutputPort(i+1, outPortNumber).absSquared() ;}
		}
		return xtalkPower ;
	}

	public double getXtalkPowerdBm(int inPortNumber, int outPortNumber){
		double xtalkPower = 0 ;
		int M = 8 ;
		for(int i=0; i<M; i++){
			if(i!=inPortNumber-1){xtalkPower += getOutputPort(i+1, outPortNumber).absSquared() ;}
		}
		return MoreMath.Conversions.todB(xtalkPower) ;
	}

	public double getXtalkPenalty(int inPortNumber, int outPortNumber){
		double signalPower = getOutputPort(inPortNumber, outPortNumber).absSquared() ;
		double xtalkPower = getXtalkPower(inPortNumber, outPortNumber) ;
		double xtalkArg = 6 * xtalkPower/signalPower ;
		double xtalkPP = -MoreMath.Conversions.todB(1-xtalkArg)/2 ;
		return xtalkPP ;
	}

	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {

		int port1Map, port2Map, port3Map, port4Map, port5Map, port6Map, port7Map, port8Map ;
		int[] mapping = findMappingArray() ;
		port1Map = mapping[0] ;
		port2Map = mapping[1] ;
		port3Map = mapping[2] ;
		port4Map = mapping[3] ;
		port5Map = mapping[4] ;
		port6Map = mapping[5] ;
		port7Map = mapping[6] ;
		port8Map = mapping[7] ;

		double port1dBm = MoreMath.Conversions.todB(getOutputPort(1, port1Map).absSquared()) ;
		double port2dBm = MoreMath.Conversions.todB(getOutputPort(2, port2Map).absSquared()) ;
		double port3dBm = MoreMath.Conversions.todB(getOutputPort(3, port3Map).absSquared()) ;
		double port4dBm = MoreMath.Conversions.todB(getOutputPort(4, port4Map).absSquared()) ;
		double port5dBm = MoreMath.Conversions.todB(getOutputPort(5, port5Map).absSquared()) ;
		double port6dBm = MoreMath.Conversions.todB(getOutputPort(6, port6Map).absSquared()) ;
		double port7dBm = MoreMath.Conversions.todB(getOutputPort(7, port7Map).absSquared()) ;
		double port8dBm = MoreMath.Conversions.todB(getOutputPort(8, port8Map).absSquared()) ;

		// Now finding the power penalty for each input-output pair
		double pp1 = -port1dBm + getXtalkPenalty(1, port1Map) ;
		double pp2 = -port2dBm + getXtalkPenalty(2, port2Map) ;
		double pp3 = -port3dBm + getXtalkPenalty(3, port3Map) ;
		double pp4 = -port4dBm + getXtalkPenalty(4, port4Map) ;
		double pp5 = -port5dBm + getXtalkPenalty(5, port5Map) ;
		double pp6 = -port6dBm + getXtalkPenalty(6, port6Map) ;
		double pp7 = -port7dBm + getXtalkPenalty(7, port7Map) ;
		double pp8 = -port8dBm + getXtalkPenalty(8, port8Map) ;
		double ppAvg = (pp1 + pp2 + pp3 + pp4 + pp5 + pp6 + pp7 + pp8)/8 ;
		double er = Math.pow(pp1-ppAvg, 2) + Math.pow(pp2-ppAvg, 2) + Math.pow(pp3-ppAvg, 2) + Math.pow(pp4-ppAvg, 2) + Math.pow(pp5-ppAvg, 2) + Math.pow(pp6-ppAvg, 2) +
				Math.pow(pp7-ppAvg, 2) + Math.pow(pp8-ppAvg, 2) ;
		double err = Math.sqrt(er/8) ;

		// step1: create the file name
		String fileName = "Benes8x8AllConfigs" ;
		// step2: create the file
		FileOutput fout = new FileOutput(System.getProperty("user.home") + File.separator + "Desktop" + File.separator + fileName + ".txt", "a") ;
		// step3: defining the line
		String line = getSwitchState() + "     " + findMapping() + "     " ;
		line += String.format("%2.4f", err) + "     " ;
		line += String.format("%2.4f", pp1) + "     " + String.format("%2.4f", pp2) + "     " + String.format("%2.4f", pp3) + "     " + String.format("%2.4f", pp4) + "     " +
				String.format("%2.4f", pp5) + "     " + String.format("%2.4f", pp6) + "     " + String.format("%2.4f", pp7) + "     " + String.format("%2.4f", pp8) + "     " ;
		// step4: writing the switch configuration and all the input-output port power levels
		fout.println(line) ;
		fout.close();

	}


}


