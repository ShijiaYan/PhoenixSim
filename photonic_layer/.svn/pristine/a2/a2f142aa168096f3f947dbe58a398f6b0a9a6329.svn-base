package People.Sebastien.LambdaRouter;

import PhotonicElements.InputSources.AbstractInputSource;
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

public class LambdaRouter8x8 implements Experiment {

	/**
	 * need 8 individual 2x2 structures. 
	 */
	
	Wavelength inputLambda ;
	WgProperties wgProp ;
	
	Switch2x2RingPassive sw1, sw2, sw3, sw4, sw5, sw6, sw7, sw8 ;
	Connection8x8 connection ;
	Complex port1In, port2In, port3In, port4In, port5In, port6In, port7In, port8In ;
	Complex zero = new Complex(0,0), one = new Complex(1,0) ;
	
	public LambdaRouter8x8(
			@ParamName(name="Wavelength (nm)") Wavelength inputLambda,
			@ParamName(name="Waveguide Properties") WgProperties wgProp,
			@ParamName(name="Radius (um)") double radiusMicron,
			@ParamName(name="input Kappa") double inputKappa,
			@ParamName(name="output Kappa") double outputKappa,
			@ParamName(name="Crossing Model") SimpleCrossing crossing,
			@ParamName(name="Lambda 1 Resonance (nm)") double resLambda1Nm,
			@ParamName(name="Lambda 2 Resonance (nm)") double resLambda2Nm,
			@ParamName(name="Lambda 3 Resonance (nm)") double resLambda3Nm,
			@ParamName(name="Lambda 4 Resonance (nm)") double resLambda4Nm,
			@ParamName(name="Lambda 5 Resonance (nm)") double resLambda5Nm,
			@ParamName(name="Lambda 6 Resonance (nm)") double resLambda6Nm,
			@ParamName(name="Lambda 7 Resonance (nm)") double resLambda7Nm,
			@ParamName(name="Lambda 8 Resonance (nm)") double resLambda8Nm,
			@ParamName(name="Input source 1") AbstractInputSource inputSource1,
			@ParamName(name="Input source 2") AbstractInputSource inputSource2,
			@ParamName(name="Input source 3") AbstractInputSource inputSource3,
			@ParamName(name="Input source 4") AbstractInputSource inputSource4,
			@ParamName(name="Input source 5") AbstractInputSource inputSource5,
			@ParamName(name="Input source 6") AbstractInputSource inputSource6,
			@ParamName(name="Input source 7") AbstractInputSource inputSource7,
			@ParamName(name="Input source 8") AbstractInputSource inputSource8
			){
		this.inputLambda = inputLambda ;
		this.wgProp = wgProp ;
		sw1 = new Switch2x2RingPassive(inputLambda, wgProp, radiusMicron, inputKappa, outputKappa, crossing, resLambda1Nm) ;
		sw2 = new Switch2x2RingPassive(inputLambda, wgProp, radiusMicron, inputKappa, outputKappa, crossing, resLambda2Nm) ;
		sw3 = new Switch2x2RingPassive(inputLambda, wgProp, radiusMicron, inputKappa, outputKappa, crossing, resLambda3Nm) ;
		sw4 = new Switch2x2RingPassive(inputLambda, wgProp, radiusMicron, inputKappa, outputKappa, crossing, resLambda4Nm) ;
		sw5 = new Switch2x2RingPassive(inputLambda, wgProp, radiusMicron, inputKappa, outputKappa, crossing, resLambda5Nm) ;
		sw6 = new Switch2x2RingPassive(inputLambda, wgProp, radiusMicron, inputKappa, outputKappa, crossing, resLambda6Nm) ;
		sw7 = new Switch2x2RingPassive(inputLambda, wgProp, radiusMicron, inputKappa, outputKappa, crossing, resLambda7Nm) ;
		sw8 = new Switch2x2RingPassive(inputLambda, wgProp, radiusMicron, inputKappa, outputKappa, crossing, resLambda8Nm) ;
		connection = new Connection8x8(inputLambda, wgProp) ;
		port1In = inputSource1.getElectricFieldAtInputWavelength(inputLambda) ;
		port2In = inputSource2.getElectricFieldAtInputWavelength(inputLambda) ;
		port3In = inputSource3.getElectricFieldAtInputWavelength(inputLambda) ;
		port4In = inputSource4.getElectricFieldAtInputWavelength(inputLambda) ;
		port5In = inputSource5.getElectricFieldAtInputWavelength(inputLambda) ;
		port6In = inputSource6.getElectricFieldAtInputWavelength(inputLambda) ;
		port7In = inputSource7.getElectricFieldAtInputWavelength(inputLambda) ;
		port8In = inputSource8.getElectricFieldAtInputWavelength(inputLambda) ;
	}
	
	Complex[] getColumnSwitchType1(Complex[] portsIn, Switch2x2RingPassive sw){
		int n = portsIn.length ;
		Complex[] portsOut = new Complex[n] ;
		portsOut[0] = sw.getPort2(portsIn[0], zero, zero, portsIn[1]) ;
		portsOut[1] = sw.getPort3(portsIn[0], zero, zero, portsIn[1]) ;
		portsOut[2] = sw.getPort2(portsIn[2], zero, zero, portsIn[3]) ;
		portsOut[3] = sw.getPort3(portsIn[2], zero, zero, portsIn[3]) ;
		portsOut[4] = sw.getPort2(portsIn[4], zero, zero, portsIn[5]) ;
		portsOut[5] = sw.getPort3(portsIn[4], zero, zero, portsIn[5]) ;
		portsOut[6] = sw.getPort2(portsIn[6], zero, zero, portsIn[7]) ;
		portsOut[7] = sw.getPort3(portsIn[6], zero, zero, portsIn[7]) ;
		return portsOut ;
	}
	
	Complex[] getColumnSwitchType2(Complex[] portsIn, Switch2x2RingPassive sw){
		int n = portsIn.length ;
		Complex[] portsOut = new Complex[n] ;
		portsOut[0] = portsIn[0] ;
		portsOut[1] = sw.getPort2(portsIn[1], zero, zero, portsIn[2]) ;
		portsOut[2] = sw.getPort3(portsIn[1], zero, zero, portsIn[2]) ;
		portsOut[3] = sw.getPort2(portsIn[3], zero, zero, portsIn[4]) ;
		portsOut[4] = sw.getPort3(portsIn[3], zero, zero, portsIn[4]) ;
		portsOut[5] = sw.getPort2(portsIn[5], zero, zero, portsIn[6]) ;
		portsOut[6] = sw.getPort3(portsIn[5], zero, zero, portsIn[6]) ;
		portsOut[7] = portsIn[7] ;
		return portsOut ;
	}

	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {
		DataPoint dp = new DataPoint() ;
		
		// stage 0
		Complex[] ports0 = {port1In, port2In, port3In, port4In, port5In, port6In, port7In, port8In} ;
		// stage 1
		Complex[] ports1 = connection.getCrossingPorts(ports0) ;
		// stage 2
		Complex[] ports2 = getColumnSwitchType1(ports1, sw1) ;
		// stage 3
		Complex[] ports3 = connection.getCrossingPorts(ports2) ;
		// stage 4
		Complex[] ports4 = getColumnSwitchType2(ports3, sw2) ;
		// stage 5
		Complex[] ports5 = connection.getCrossingPorts(ports4) ;
		// stage 6
		Complex[] ports6 = getColumnSwitchType1(ports5, sw3) ;
		// stage 7
		Complex[] ports7 = connection.getCrossingPorts(ports6) ;
		// stage 8
		Complex[] ports8 = getColumnSwitchType2(ports7, sw4) ;
		// stage 9
		Complex[] ports9 = connection.getCrossingPorts(ports8) ;
		// stage 10
		Complex[] ports10 = getColumnSwitchType1(ports9, sw5) ;
		// stage 11
		Complex[] ports11 = connection.getCrossingPorts(ports10) ;
		// stage 12
		Complex[] ports12 = getColumnSwitchType2(ports11, sw6) ;
		// stage 13
		Complex[] ports13 = connection.getCrossingPorts(ports12) ;
		// stage 14
		Complex[] ports14 = getColumnSwitchType1(ports13, sw7) ;
		// stage 15
		Complex[] ports15 = connection.getCrossingPorts(ports14) ;
		// stage 16
		Complex[] ports16 = getColumnSwitchType2(ports15, sw8) ;
		// stage 17
		Complex[] ports17 = connection.getCrossingPorts(ports16) ;
		
		dp.addProperty("Wavelength (nm)", inputLambda.getWavelengthNm());
		dp.addResultProperty("Port 1 out (dBm)", MoreMath.Conversions.todB(ports17[0].absSquared()));
		dp.addResultProperty("Port 2 out (dBm)", MoreMath.Conversions.todB(ports17[1].absSquared()));
		dp.addResultProperty("Port 3 out (dBm)", MoreMath.Conversions.todB(ports17[2].absSquared()));
		dp.addResultProperty("Port 4 out (dBm)", MoreMath.Conversions.todB(ports17[3].absSquared()));
		dp.addResultProperty("Port 5 out (dBm)", MoreMath.Conversions.todB(ports17[4].absSquared()));
		dp.addResultProperty("Port 6 out (dBm)", MoreMath.Conversions.todB(ports17[5].absSquared()));
		dp.addResultProperty("Port 7 out (dBm)", MoreMath.Conversions.todB(ports17[6].absSquared()));
		dp.addResultProperty("Port 8 out (dBm)", MoreMath.Conversions.todB(ports17[7].absSquared()));
		man.addDataPoint(dp);
	}
	
	
	
	
	
	
	
	
	
	
}


