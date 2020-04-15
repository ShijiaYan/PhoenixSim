package Simulations.rings.addDrop;

import PhotonicElements.Heater.SimpleHeater;
import PhotonicElements.RingStructures.AddDrop.AddDropFirstOrder;
import PhotonicElements.Utilities.MathLibraries.Complex;
import PhotonicElements.Utilities.Wavelength;
import PhotonicElements.Waveguides.CurvedWaveguide.CurvedWg;
import PhotonicElements.Waveguides.StraightWaveguide.StraightWg;
import PhotonicElements.Waveguides.WaveguideProperties.WgProperties;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;

public class TestAddDropRingBus implements Experiment {

	Wavelength inputLambda ;
	CurvedWg Cwg1, Cwg2, Cwg3, Cwg4 ;
	StraightWg wg1, wg2, wg3, wg4, wg5, wg6 ;
	AddDropFirstOrder DR1, DR2 ;
	SimpleHeater H ;
	int steps ;
	Complex one = new Complex(1,0) ; Complex zero = new Complex(0,0) ;
	
	Complex S11, S12, S13, S14 ;
	Complex S21, S22, S23, S24 ;
	Complex S31, S32, S33, S34 ;
	Complex S41, S42, S43, S44 ;
	
	public TestAddDropRingBus(
			@ParamName(name="Wavelength (nm)") Wavelength inputLambda,
			@ParamName(name="Waveguide Properties") WgProperties wgProp,
			@ParamName(name="Bending Radius (micron)") double bendRadiusMicron,
			@ParamName(name="Ring radius (micron)") double radiusMicron,
			@ParamName(name="input Kappa") double inputKappa,
			@ParamName(name="output Kappa") double outputKappa,
			@ParamName(name="Heater Phase Shifter") SimpleHeater H,
			@ParamName(name="Waveguide Length (micron)") double lengthMicron
			){
		this.steps = wgProp.getConvergenceSteps() ;
		this.inputLambda = inputLambda ;
		this.Cwg1 = new CurvedWg(inputLambda, wgProp, bendRadiusMicron, 90, false, null, false, null) ;
		this.Cwg2 = new CurvedWg(inputLambda, wgProp, bendRadiusMicron, 90, false, null, false, null) ;
		this.Cwg3 = new CurvedWg(inputLambda, wgProp, bendRadiusMicron, 90, false, null, false, null) ;
		this.Cwg4 = new CurvedWg(inputLambda, wgProp, bendRadiusMicron, 90, false, null, false, null) ;
		this.wg1 = new StraightWg(inputLambda, wgProp, lengthMicron, false, null, true, H) ;
		this.wg2 = new StraightWg(inputLambda, wgProp, lengthMicron/2, false, null, false, null) ;
		this.wg3 = new StraightWg(inputLambda, wgProp, lengthMicron/2, false, null, false, null) ;
		this.wg4 = new StraightWg(inputLambda, wgProp, lengthMicron, false, null, false, null) ;
		this.wg5 = new StraightWg(inputLambda, wgProp, lengthMicron/2, false, null, false, null) ;
		this.wg6 = new StraightWg(inputLambda, wgProp, lengthMicron/2, false, null, false, null) ;
		this.DR1 = new AddDropFirstOrder(inputLambda, wgProp, radiusMicron, 180, inputKappa, outputKappa, false, null) ;
		this.DR2 = new AddDropFirstOrder(inputLambda, wgProp, radiusMicron, 180, inputKappa, outputKappa, false, null) ;
		// Now calculate the scattering parameters
/*		Complex[] Sx1 = excitePorts(one, zero, zero, zero) ;
		this.S11 = Sx1[0] ; this.S21 = Sx1[1] ; this.S31 = Sx1[2] ; this.S41 = Sx1[3] ;
		
		Complex[] Sx2 = excitePorts(zero, one, zero, zero) ;
		this.S12 = Sx2[0] ; this.S22 = Sx2[1] ; this.S32 = Sx2[2] ; this.S42 = Sx2[3] ;
		
		Complex[] Sx3 = excitePorts(zero, zero, one, zero) ;
		this.S13 = Sx3[0] ; this.S23 = Sx3[1] ; this.S33 = Sx3[2] ; this.S43 = Sx3[3] ;
		
		Complex[] Sx4 = excitePorts(zero, zero, zero, one) ;
		this.S14 = Sx4[0] ; this.S24 = Sx4[1] ; this.S34 = Sx4[2] ; this.S44 = Sx4[3] ;*/
	}
	
	public Complex[] excitePorts(Complex port1_in, Complex port2_in, Complex port3_in, Complex port4_in){
		Complex port1_out = zero, port2_out = zero, port3_out = zero, port4_out = zero ;
		Complex wg1_port1_out = zero, wg1_port2_out = zero ;
		Complex wg2_port1_out = zero, wg2_port2_out = zero ;
		Complex wg3_port1_out = zero, wg3_port2_out = zero ;
		Complex wg4_port1_out = zero, wg4_port2_out = zero ;
		Complex wg5_port1_out = zero, wg5_port2_out = zero ;
		Complex wg6_port1_out = zero, wg6_port2_out = zero ;
		Complex Cwg1_port1_out = zero, Cwg1_port2_out = zero ;
		Complex Cwg2_port1_out = zero, Cwg2_port2_out = zero ;
		Complex Cwg3_port1_out = zero, Cwg3_port2_out = zero ;
		Complex Cwg4_port1_out = zero, Cwg4_port2_out = zero ;
		Complex DR1_port1_out = zero, DR1_port2_out = zero, DR1_port3_out = zero, DR1_port4_out = zero ;
		Complex DR2_port1_out = zero, DR2_port2_out = zero, DR2_port3_out = zero, DR2_port4_out = zero ;
		
		for(int i=0; i<steps; i++){
			DR1_port1_out = DR1.getPort1(wg5_port2_out, wg6_port1_out, port1_in, port2_in) ;
			DR1_port2_out = DR1.getPort2(wg5_port2_out, wg6_port1_out, port1_in, port2_in) ;
			DR1_port3_out = DR1.getPort3(wg5_port2_out, wg6_port1_out, port1_in, port2_in) ;
			DR1_port4_out = DR1.getPort4(wg5_port2_out, wg6_port1_out, port1_in, port2_in) ;
			
			DR2_port1_out = DR2.getPort1(wg2_port1_out, wg3_port2_out, port3_in, port4_in) ;
			DR2_port2_out = DR2.getPort2(wg2_port1_out, wg3_port2_out, port3_in, port4_in) ;
			DR2_port3_out = DR2.getPort3(wg2_port1_out, wg3_port2_out, port3_in, port4_in) ;
			DR2_port4_out = DR2.getPort4(wg2_port1_out, wg3_port2_out, port3_in, port4_in) ;
			
			wg1_port1_out = wg1.getPort1(Cwg1_port2_out, Cwg2_port2_out) ;
			wg1_port2_out = wg1.getPort2(Cwg1_port2_out, Cwg2_port2_out) ;
			
			wg2_port1_out = wg2.getPort1(DR2_port1_out, Cwg2_port1_out) ;
			wg2_port2_out = wg2.getPort2(DR2_port1_out, Cwg2_port1_out) ;
			
			wg3_port1_out = wg3.getPort1(Cwg3_port1_out, DR2_port2_out) ;
			wg3_port2_out = wg3.getPort2(Cwg3_port1_out, DR2_port2_out) ;
			
			wg4_port1_out = wg4.getPort1(Cwg4_port2_out, Cwg3_port2_out) ;
			wg4_port2_out = wg4.getPort2(Cwg4_port2_out, Cwg3_port2_out) ;
			
			wg5_port1_out = wg5.getPort1(Cwg4_port1_out, DR1_port1_out) ;
			wg5_port2_out = wg5.getPort2(Cwg4_port1_out, DR1_port1_out) ;
			
			wg6_port1_out = wg6.getPort1(DR1_port2_out, Cwg1_port1_out) ;
			wg6_port2_out = wg6.getPort2(DR1_port2_out, Cwg1_port1_out) ;
			
			Cwg1_port1_out = Cwg1.getPort1(wg6_port2_out, wg1_port1_out) ;
			Cwg1_port2_out = Cwg1.getPort2(wg6_port2_out, wg1_port1_out) ;
			
			Cwg2_port1_out = Cwg2.getPort1(wg2_port2_out, wg1_port2_out) ;
			Cwg2_port2_out = Cwg2.getPort2(wg2_port2_out, wg1_port2_out) ;
			
			Cwg3_port1_out = Cwg3.getPort1(wg3_port1_out, wg4_port2_out) ;
			Cwg3_port2_out = Cwg3.getPort2(wg3_port1_out, wg4_port2_out) ;
			
			Cwg4_port1_out = Cwg4.getPort1(wg5_port1_out, wg4_port1_out) ;
			Cwg4_port2_out = Cwg4.getPort2(wg5_port1_out, wg4_port1_out) ;
			// turn the excitation off
			port1_in = port2_in = port3_in = port4_in = zero ;
			// calculate output ports
			port1_out = port1_out.plus(DR1_port3_out) ;
			port2_out = port2_out.plus(DR1_port4_out) ;
			port3_out = port3_out.plus(DR2_port3_out) ;
			port4_out = port4_out.plus(DR2_port4_out) ;
		}
		Complex[] outputPorts = {port1_out, port2_out, port3_out, port4_out} ;
		return outputPorts ;
	}
	

	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {
		DataPoint dp = new DataPoint() ;
		Complex[] Sx1 = excitePorts(one, zero, zero, zero) ;
		S41 = Sx1[3] ;
		dp.addProperty("Wavelength (nm)", inputLambda.getWavelengthNm());
		dp.addResultProperty("[Port 1 --> Port 4] (dB)", 10*Math.log10(S41.absSquared()));
		man.addDataPoint(dp);
	}
}
