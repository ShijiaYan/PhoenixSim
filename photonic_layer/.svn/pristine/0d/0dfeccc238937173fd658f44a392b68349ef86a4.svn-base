package Simulations.junctions.wgCrossing;

import PhotonicElements.Junctions.Crossings.SimpleCrossing;
import PhotonicElements.Utilities.MathLibraries.Complex;
import PhotonicElements.Utilities.Wavelength;
import PhotonicElements.Waveguides.CurvedWaveguide.CurvedWg;
import PhotonicElements.Waveguides.WaveguideProperties.WgProperties;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;

public class TestCrossingLoop implements Experiment {

	Complex zero = new Complex(0,0), one = new Complex(1,0) ;
	
	Wavelength inputLambda ;
	WgProperties wgProp ;
	SimpleCrossing X ;
	CurvedWg Cwg ;
	
	int steps ;
	
	Complex S11, S21 ;
	Complex S12, S22 ;
	
	public TestCrossingLoop(
			@ParamName(name="Wavelength (nm)") Wavelength inputLambda,
			@ParamName(name="Waveguide Properties") WgProperties wgProp,
			@ParamName(name="Crossing Model") SimpleCrossing X,
			@ParamName(name="Radius of curved waveguide (micron)") double radiusMicron,
			@ParamName(name="Angle of curvature (degree)") double angleDegree
			){
		this.inputLambda = inputLambda ;
		this.wgProp = wgProp ;
		this.X = X ;
		this.Cwg = new CurvedWg(inputLambda, wgProp, radiusMicron, angleDegree, false, null, false, null) ;
		steps = wgProp.getConvergenceSteps() ;
		// now calculate the S-parameters
		Complex[] Sx1 = excitePorts(one, zero) ;
		S11 = Sx1[0] ; S21 = Sx1[1] ;
		Complex[] Sx2 = excitePorts(zero, one) ;
		S12 = Sx2[0] ; S22 = Sx2[1] ;
	}
	
	Complex[] excitePorts(Complex port1_in, Complex port2_in){
		Complex port1_out = zero, port2_out = zero ;
		Complex X_port1_out = zero, X_port2_out = zero, X_port3_out = zero, X_port4_out = zero ;
		Complex Cwg_port1_out = zero, Cwg_port2_out = zero ;
		
		for(int i=0; i<steps; i++){
			X_port1_out = X.getPort1(port1_in, Cwg_port1_out, Cwg_port2_out, port2_in) ; // this is output port 1
			X_port2_out = X.getPort2(port1_in, Cwg_port1_out, Cwg_port2_out, port2_in) ;
			X_port3_out = X.getPort3(port1_in, Cwg_port1_out, Cwg_port2_out, port2_in) ;
			X_port4_out = X.getPort4(port1_in, Cwg_port1_out, Cwg_port2_out, port2_in) ; // this is output port 2
			
			Cwg_port1_out = Cwg.getPort1(X_port2_out, X_port3_out) ;
			Cwg_port2_out = Cwg.getPort2(X_port2_out, X_port3_out) ;
			// set the excitation to zero
			port1_in = port2_in = zero ;
			// calculate the total output
			port1_out = port1_out.plus(X_port1_out) ;
			port2_out = port2_out.plus(X_port4_out) ;
		}
		Complex[] outPorts = {port1_out, port2_out} ;
		return outPorts ;
	}
	
	
	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {
		DataPoint dp = new DataPoint() ;
		dp.addProperty("Wavelength (nm)", inputLambda.getWavelengthNm());
		dp.addProperty("iteration steps", steps);
		dp.addProperty("Crossing Thru Loss (dB)", 10*Math.log10(X.getS31().absSquared()));
		dp.addResultProperty("Crossing Thru Loss (dB)", 10*Math.log10(X.getS31().absSquared()));
		dp.addProperty("Crossing Crosstalk (dB)", 10*Math.log10(X.getS21().absSquared()));
		dp.addResultProperty("Crossing Thru Loss (dB)", 10*Math.log10(X.getS21().absSquared()));
		dp.addResultProperty("|S21|^2 (dB)", 10*Math.log10(S21.absSquared()));
		dp.addResultProperty("|S11|^2", 10*Math.log10(S11.absSquared()));
		man.addDataPoint(dp); 
		
	}

}
