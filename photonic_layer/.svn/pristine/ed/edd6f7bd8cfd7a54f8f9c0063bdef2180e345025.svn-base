package Simulations.switches.thesis.benes8x8;

import People.Qixiang.Benes.CrossingCells.Crossing8x8BenesType1;
import People.Qixiang.Benes.CrossingCells.Crossing8x8BenesType2;
import People.Qixiang.Benes.CrossingCells.Crossing8x8BenesType3;
import PhotonicElements.Junctions.Crossings.SimpleCrossing;
import PhotonicElements.Switches.Switch2x2.Switch2x2MZIDistributedCoupler;
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
import ch.epfl.javancox.experiments.builder.ExperimentConfigurationCockpit;

public class Benes8x8MZISwitch implements Experiment{

	Wavelength inputLambda ;
	SimpleCrossing wgCrossing ;
	Switch2x2MZIDistributedCoupler swCross, swBar ;
	String[] state = new String[20] ; // shows the state of each switch
	Complex[][] tCross = new Complex[4][5] ;
	Complex[][] tBar = new Complex[4][5] ;
	Complex zero = new Complex(0,0), one = new Complex(1,0) ;
	int inPortNumber, outPortNumber ;
	Crossing8x8BenesType1 crossingType1 ;
	Crossing8x8BenesType2 crossingType2 ;
	Crossing8x8BenesType3 crossingType3 ;

	public Benes8x8MZISwitch(
			@ParamName(name="Set Wavelength (nm)") Wavelength inputLambda,
			@ParamName(name="Set Waveguide Properties") WgProperties wgProp,
			@ParamName(name="Length of arm (um)") double lengthMicron,
			@ParamName(name="coupler length (um)") double couplerLengthMicron,
			@ParamName(name="coupler gap (nm)") double couplerGapNm,
			@ParamName(name="coupler loss (dB)") double couplerLossdB,
			@ParamName(name="Delta Alpha (1/cm)") double DalphaPerCm,
			@ParamName(name="Waveguide Crossing Model") SimpleCrossing wgCrossing,
			@ParamName(name="Input Port Number") int inPortNumber,
			@ParamName(name="Output Port Number") int outPortNumber,
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
		this.swCross = new Switch2x2MZIDistributedCoupler(inputLambda, wgProp, lengthMicron, couplerLengthMicron, couplerGapNm, couplerLossdB, true, DalphaPerCm) ;
		this.swBar = new Switch2x2MZIDistributedCoupler(inputLambda, wgProp, lengthMicron, couplerLengthMicron, couplerGapNm, couplerLossdB, false, DalphaPerCm) ;
		this.inPortNumber = inPortNumber ;
		this.outPortNumber = outPortNumber ;
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
					this.tCross[i][j] = this.swCross.S31 ;
					this.tBar[i][j] = this.swCross.S21 ;
				}
				else{
					this.tCross[i][j] = this.swBar.S31 ;
					this.tBar[i][j] = this.swBar.S21 ;
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

	public Complex getOutputPort(){
		return getAllOutputPorts()[outPortNumber-1] ;
	}


	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {
		DataPoint dp = new DataPoint() ;
		double powerOutdBm = MoreMath.Conversions.todB(getOutputPort().absSquared()) ;
		if(powerOutdBm>-10){
			dp.addProperty("Wavelength (nm)", inputLambda.getWavelengthNm());
			dp.addProperty("State of Switch", getSwitchState());
			dp.addResultProperty("Port "+outPortNumber+" out (dBm)", powerOutdBm);
		}
		man.addDataPoint(dp);

//		// step1: create the file name
//		String fileName = "Benes8x8Switch" ;
//		// step2: create the file
//		FileOutput fout = new FileOutput(System.getProperty("user.home") + File.separator +"Desktop" + File.separator + fileName + ".txt", "a") ;
//		// step3: defining the line
//		String line = getSwitchState() + "     " ;
//		line += String.format("%2.4f", powerOutdBm) ;
//		// step4: writing the switch configuration and all the input-output port power levels
//		fout.println(line) ;
//		fout.close();
//		System.gc();
	}

	public static void main(String[] args){
		String pacakgeString = "PhotonicElements" ;
		String classString = "Simulations.switches.thesis.benes8x8.Benes8x8MZISwitch" ;
		ExperimentConfigurationCockpit.main(new String[]{"-p", pacakgeString, "-c", classString});
		// -DJAVANCO_HOME=/Users/meisam/Desktop/Javanco_env/javanco
	}

}


