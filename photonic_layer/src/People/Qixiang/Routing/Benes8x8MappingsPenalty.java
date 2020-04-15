package People.Qixiang.Routing;

import People.Qixiang.Benes.CrossingCells.Crossing8x8BenesType1;
import People.Qixiang.Benes.CrossingCells.Crossing8x8BenesType2;
import People.Qixiang.Benes.CrossingCells.Crossing8x8BenesType3;
import People.Qixiang.Benes.MZI2x2.Switch2x2MZI_qixiang;
import PhotonicElements.DirectionalCoupler.CompactCoupler;
import PhotonicElements.Junctions.Crossings.SimpleCrossing;
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

public class Benes8x8MappingsPenalty implements Experiment {

	int port1Map, port2Map, port3Map, port4Map, port5Map, port6Map, port7Map, port8Map ;
	
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
		
	public Benes8x8MappingsPenalty(
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
		this.swCross = new Switch2x2MZI_qixiang(inputLambda, wgProp, 300, DC1, DC2, true, false) ;
		this.swBar = new Switch2x2MZI_qixiang(inputLambda, wgProp, 300, DC1, DC2, false, true) ;
		crossingType1 = new Crossing8x8BenesType1(inputLambda, wgProp, wgCrossing) ;
		crossingType2 = new Crossing8x8BenesType2(inputLambda, wgProp, wgCrossing) ;
		crossingType3 = new Crossing8x8BenesType3(inputLambda, wgProp, wgCrossing) ;
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
	
	public Complex getOutputPort(int inPortNumber, int outPortNumber){
		return getAllOutputPorts(inPortNumber)[outPortNumber-1] ;
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
	
	public String getSwitchState(){
		String stateSwitches = state[0]+state[5]+state[10]+state[15]+ "|" +
					           state[1]+state[6]+state[11]+state[16]+ "|" +
					           state[2]+state[7]+state[12]+state[17]+ "|" + 
					           state[3]+state[8]+state[13]+state[18]+ "|" +
					           state[4]+state[9]+state[14]+state[19] ;
		return stateSwitches ;
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
		// finding a possible mapping --> then best possible mapping
		if(port1dBm>-10 && port2dBm>-10 && port3dBm>-10 && port4dBm>-10 && port5dBm>-10 && port6dBm>-10 && port7dBm>-10 && port8dBm>-10){	
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
			dp.addResultProperty("Root Min Square Error", err);
			double[] ppArray = {pp1, pp2, pp3, pp4, pp5, pp6, pp7, pp8} ;
			dp.addResultProperty("Max PP - Min PP", MoreMath.Arrays.FindMaximum.getValue(ppArray)-MoreMath.Arrays.FindMinimum.getValue(ppArray));
			dp.addProperty("State of Switch", getSwitchState());
			// power penalty
			dp.addResultProperty("Port "+ port1Map +" out PP ", pp1);
			dp.addResultProperty("Port "+ port2Map +" out PP", pp2);
			dp.addResultProperty("Port "+ port3Map +" out PP", pp3);
			dp.addResultProperty("Port "+ port4Map +" out PP", pp4);
			dp.addResultProperty("Port "+ port5Map +" out PP", pp5);
			dp.addResultProperty("Port "+ port6Map +" out PP", pp6);
			dp.addResultProperty("Port "+ port7Map +" out PP", pp7);
			dp.addResultProperty("Port "+ port8Map +" out PP", pp8);
			// loss (dB)
			dp.addResultProperty("Port "+ port1Map +" out Loss (dB)", -port1dBm);
			dp.addResultProperty("Port "+ port2Map +" out Loss (dB)", -port2dBm);
			dp.addResultProperty("Port "+ port3Map +" out Loss (dB)", -port3dBm);
			dp.addResultProperty("Port "+ port4Map +" out Loss (dB)", -port4dBm);
			dp.addResultProperty("Port "+ port5Map +" out Loss (dB)", -port5dBm);
			dp.addResultProperty("Port "+ port6Map +" out Loss (dB)", -port6dBm);
			dp.addResultProperty("Port "+ port7Map +" out Loss (dB)", -port7dBm);
			dp.addResultProperty("Port "+ port8Map +" out Loss (dB)", -port8dBm);
			// crosstalk
			dp.addResultProperty("Port "+ port1Map +" out xtalkPP (dB)", getXtalkPenalty(1, port1Map));
			dp.addResultProperty("Port "+ port2Map +" out xtalkPP (dB)", getXtalkPenalty(2, port2Map));
			dp.addResultProperty("Port "+ port3Map +" out xtalkPP (dB)", getXtalkPenalty(3, port3Map));
			dp.addResultProperty("Port "+ port4Map +" out xtalkPP (dB)", getXtalkPenalty(4, port4Map));
			dp.addResultProperty("Port "+ port5Map +" out xtalkPP (dB)", getXtalkPenalty(5, port5Map));
			dp.addResultProperty("Port "+ port6Map +" out xtalkPP (dB)", getXtalkPenalty(6, port6Map));
			dp.addResultProperty("Port "+ port7Map +" out xtalkPP (dB)", getXtalkPenalty(7, port7Map));
			dp.addResultProperty("Port "+ port8Map +" out xtalkPP (dB)", getXtalkPenalty(8, port8Map));
			
			dp.addResultProperty("Port "+ port1Map +" out xtalk (dBm)", getXtalkPowerdBm(1, port1Map));
			dp.addResultProperty("Port "+ port2Map +" out xtalk (dBm)", getXtalkPowerdBm(2, port2Map));
			dp.addResultProperty("Port "+ port3Map +" out xtalk (dBm)", getXtalkPowerdBm(3, port3Map));
			dp.addResultProperty("Port "+ port4Map +" out xtalk (dBm)", getXtalkPowerdBm(4, port4Map));
			dp.addResultProperty("Port "+ port5Map +" out xtalk (dBm)", getXtalkPowerdBm(5, port5Map));
			dp.addResultProperty("Port "+ port6Map +" out xtalk (dBm)", getXtalkPowerdBm(6, port6Map));
			dp.addResultProperty("Port "+ port7Map +" out xtalk (dBm)", getXtalkPowerdBm(7, port7Map));
			dp.addResultProperty("Port "+ port8Map +" out xtalk (dBm)", getXtalkPowerdBm(8, port8Map));
		}
		man.addDataPoint(dp);
	}
	

}
