package People.Qixiang.Mapping;

import java.io.File;

import People.Qixiang.Benes.CrossingCells.Crossing4x4BenesType1;
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

public class Benes4x4MZISwitch_AllConfig_RMSE implements Experiment {

	Wavelength inputLambda ;
	SimpleCrossing wgCrossing ;
	Switch2x2MZI_qixiang swR1C1, swR1C2, swR1C3, swR2C1, swR2C2, swR2C3;

	String[] state = new String[6] ; // shows the state of each switch
	String[] stateSource = new String[4] ; // state of sources
	Crossing4x4BenesType1 crossing ;
	int port1Map, port2Map, port3Map, port4Map ;
	Complex zero = new Complex(0,0), one = new Complex(1,0) ;

	public Benes4x4MZISwitch_AllConfig_RMSE(
			@ParamName(name="Set Wavelength (nm)") Wavelength inputLambda,
			@ParamName(name="Set Waveguide Properties") WgProperties wgProp,
			@ParamName(name="Input Coupler") CompactCoupler DC1,
			@ParamName(name="Output Coupler") CompactCoupler DC2,
			@ParamName(name="Waveguide Crossing Model") SimpleCrossing wgCrossing,
			@ParamName(name="State of Switch Row 1 Column 1 (true = CROSS, false = BAR)") boolean swR1C1isCross,
			@ParamName(name="State of Switch Row 1 Column 2 (true = CROSS, false = BAR)") boolean swR1C2isCross,
			@ParamName(name="State of Switch Row 1 Column 3 (true = CROSS, false = BAR)") boolean swR1C3isCross,
			@ParamName(name="State of Switch Row 2 Column 1 (true = CROSS, false = BAR)") boolean swR2C1isCross,
			@ParamName(name="State of Switch Row 2 Column 2 (true = CROSS, false = BAR)") boolean swR2C2isCross,
			@ParamName(name="State of Switch Row 2 Column 3 (true = CROSS, false = BAR)") boolean swR2C3isCross
			){
		this.inputLambda = inputLambda ;
		this.wgCrossing = wgCrossing ;
		this.swR1C1 = new Switch2x2MZI_qixiang(inputLambda, wgProp, 300, DC1, DC2, swR1C1isCross, !swR1C1isCross) ;
		this.swR1C2 = new Switch2x2MZI_qixiang(inputLambda, wgProp, 300, DC1, DC2, swR1C2isCross, !swR1C2isCross) ;
		this.swR1C3 = new Switch2x2MZI_qixiang(inputLambda, wgProp, 300, DC1, DC2, swR1C3isCross, !swR1C3isCross) ;
		this.swR2C1 = new Switch2x2MZI_qixiang(inputLambda, wgProp, 300, DC1, DC2, swR2C1isCross, !swR2C1isCross) ;
		this.swR2C2 = new Switch2x2MZI_qixiang(inputLambda, wgProp, 300, DC1, DC2, swR2C2isCross, !swR2C2isCross) ;
		this.swR2C3 = new Switch2x2MZI_qixiang(inputLambda, wgProp, 300, DC1, DC2, swR2C3isCross, !swR2C3isCross) ;
		if(swR1C1isCross){this.state[0] = "C";} else{this.state[0] = "B";} ;
		if(swR1C2isCross){this.state[1] = "C";} else{this.state[1] = "B";} ;
		if(swR1C3isCross){this.state[2] = "C";} else{this.state[2] = "B";} ;
		if(swR2C1isCross){this.state[3] = "C";} else{this.state[3] = "B";} ;
		if(swR2C2isCross){this.state[4] = "C";} else{this.state[4] = "B";} ;
		if(swR2C3isCross){this.state[5] = "C";} else{this.state[5] = "B";} ;
		crossing = new Crossing4x4BenesType1(inputLambda, wgProp, wgCrossing) ;
	}


	public Complex[] getAllOutputPorts(int inPortNumber){
		// Stage 0 ports
		Complex[] ports0 = new Complex[4] ;
		for(int i=0; i<4; i++){
			if(i==inPortNumber-1){ports0[i] = one ;}
			else{ports0[i] = zero ;}
		}
		Complex port10 = ports0[0] ;
		Complex port20 = ports0[1] ;
		Complex port30 = ports0[2] ;
		Complex port40 = ports0[3] ;
		// Stage 1
		Complex port11 = swR1C1.getPort2(port10, zero, zero, port20) ;
		Complex port21 = swR1C1.getPort3(port10, zero, zero, port20) ;
		Complex port31 = swR2C1.getPort2(port30, zero, zero, port40) ;
		Complex port41 = swR2C1.getPort3(port30, zero, zero, port40) ;
		// Stage 2
		Complex port12 = crossing.getCrossingPorts(new Complex[] {port11, port21, port31, port41})[0] ;
		Complex port22 = crossing.getCrossingPorts(new Complex[] {port11, port21, port31, port41})[1] ;
		Complex port32 = crossing.getCrossingPorts(new Complex[] {port11, port21, port31, port41})[2] ;
		Complex port42 = crossing.getCrossingPorts(new Complex[] {port11, port21, port31, port41})[3] ;
		// Stage 3
		Complex port13 = swR1C2.getPort2(port12, zero, zero, port22) ;
		Complex port23 = swR1C2.getPort3(port12, zero, zero, port22) ;
		Complex port33 = swR2C2.getPort2(port32, zero, zero, port42) ;
		Complex port43 = swR2C2.getPort3(port32, zero, zero, port42) ;
		// Stage 4
		Complex port14 = crossing.getCrossingPorts(new Complex[] {port13, port23, port33, port43})[0] ;
		Complex port24 = crossing.getCrossingPorts(new Complex[] {port13, port23, port33, port43})[1] ;
		Complex port34 = crossing.getCrossingPorts(new Complex[] {port13, port23, port33, port43})[2] ;
		Complex port44 = crossing.getCrossingPorts(new Complex[] {port13, port23, port33, port43})[3] ;
		// Stage 5 (Final)
		Complex port15 = swR1C3.getPort2(port14, zero, zero, port24) ; // output port 1
		Complex port25 = swR1C3.getPort3(port14, zero, zero, port24) ; // output port 2
		Complex port35 = swR2C3.getPort2(port34, zero, zero, port44) ; // output port 3
		Complex port45 = swR2C3.getPort3(port34, zero, zero, port44) ; // output port 4

		return new Complex[] {port15, port25, port35, port45} ;
	}

	public ComplexMatrix getColumnTransitionMatrix(int columnNum){
		double[][] elements = new double[4][4] ;
		double[] r1 = {1,0,0,0} ;
		double[] r2 = {0,1,0,0} ;
		double[] r3 = {0,0,1,0} ;
		double[] r4 = {0,0,0,1} ;
		int m = columnNum ;
		String sw1 = state[m-1];
		String sw2 = state[3+m-1];
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
		ComplexMatrix T = new ComplexMatrix(elements) ;
		return T ;
	}

	@SuppressWarnings("static-access")
	public ComplexMatrix getSwitchTransitionMatrix(){
		ComplexMatrix T = new ComplexMatrix(4,4).identity(4) ;
		T = getColumnTransitionMatrix(1).times(T) ;
		T = crossing.getTransitionMatrix().times(T) ;
		T = getColumnTransitionMatrix(2).times(T) ;
		T = crossing.getTransitionMatrix().times(T) ;
		T = getColumnTransitionMatrix(3).times(T) ;
		return T ;
	}

	public int[] findMappingArray(){
		ComplexMatrix T = getSwitchTransitionMatrix() ;
		int[] index = new int[4] ;
		for(int j=0; j<4; j++){
			for(int i=0; i<4; i++){
				if(T.getElement(i, j).equals(one)) {index[j] = i+1 ;}
			}
		}
		return index ;
	}

	public String findMapping(){
		String mapping = "" ;
		int[] index = new int[4] ;
		index = findMappingArray() ;
		mapping += "(" + index[0] + "," + index[1] + "," + index[2] + "," + index[3] + ")" ;
		return mapping ;
	}

	public Complex getOutputPort(int inPortNumber, int outPortNumber){
		return getAllOutputPorts(inPortNumber)[outPortNumber-1] ;
	}

	public double getXtalkPower(int inPortNumber, int outPortNumber){
		double xtalkPower = 0 ;
		int M = 4 ;
		for(int i=0; i<M; i++){
			if(i!=inPortNumber-1){xtalkPower += getOutputPort(i+1, outPortNumber).absSquared() ;}
		}
		return xtalkPower ;
	}

	public double getXtalkPenalty(int inPortNumber, int outPortNumber){
		double signalPower = getOutputPort(inPortNumber, outPortNumber).absSquared() ;
		double xtalkPower = getXtalkPower(inPortNumber, outPortNumber) ;
//		double qBER = 6 ; // for BER = 10^(-9)
		double xtalkArg = 6.0 * xtalkPower/signalPower ;
		double xtalkPP = -0.5 * MoreMath.Conversions.todB(1-xtalkArg) ;
		return xtalkPP ;
	}

	public String getSwitchState(){
		String stateSwitches = state[0]+state[3] + "|" +state[1]+state[4] +"|"+ state[2]+state[5] ;
		return stateSwitches ;
	}

	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {
		int port1Map, port2Map, port3Map, port4Map ;
		int[] mapping = findMappingArray() ;
		port1Map = mapping[0] ;
		port2Map = mapping[1] ;
		port3Map = mapping[2] ;
		port4Map = mapping[3] ;

		double port1dBm = MoreMath.Conversions.todB(getOutputPort(1, port1Map).absSquared()) ;
		double port2dBm = MoreMath.Conversions.todB(getOutputPort(2, port2Map).absSquared()) ;
		double port3dBm = MoreMath.Conversions.todB(getOutputPort(3, port3Map).absSquared()) ;
		double port4dBm = MoreMath.Conversions.todB(getOutputPort(4, port4Map).absSquared()) ;

		// Now finding the power penalty for each input-output pair
		double pp1 = -port1dBm + getXtalkPenalty(1, port1Map) ;
		double pp2 = -port2dBm + getXtalkPenalty(2, port2Map) ;
		double pp3 = -port3dBm + getXtalkPenalty(3, port3Map) ;
		double pp4 = -port4dBm + getXtalkPenalty(4, port4Map) ;

		double ppAvg = (pp1 + pp2 + pp3 + pp4)/4 ;
		double er = Math.pow(pp1-ppAvg, 2) + Math.pow(pp2-ppAvg, 2) + Math.pow(pp3-ppAvg, 2) + Math.pow(pp4-ppAvg, 2) ;
		double err = Math.sqrt(er/4) ;

		// step1: create the file name
		String fileName = "Benes4x4AllConfigs" ;
		// step2: create the file
		FileOutput fout = new FileOutput(System.getProperty("user.home") + File.separator +"Desktop" + File.separator + fileName + ".txt", "a") ;
		// step3: defining the line
		String line = getSwitchState() + "     " + findMapping() + "     " ;
		line += String.format("%2.4f", err) + "     " ;
		line += String.format("%2.4f", pp1) + "     " + String.format("%2.4f", pp2) + "     " + String.format("%2.4f", pp3) + "     " + String.format("%2.4f", pp4) + "     " ;
		// step4: writing the switch configuration and all the input-output port power levels
		fout.println(line) ;
		fout.close();
	}

}
