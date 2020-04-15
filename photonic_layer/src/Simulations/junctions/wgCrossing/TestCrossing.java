package Simulations.junctions.wgCrossing;

import PhotonicElements.InputSources.AbstractInputSource;
import PhotonicElements.Junctions.Crossings.SimpleCrossing;
import PhotonicElements.Utilities.MathLibraries.Complex;
import PhotonicElements.Utilities.Wavelength;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;


public class TestCrossing implements Experiment{

	SimpleCrossing crossing ;
	Complex port1In, port2In ;
	String stateSource1, StateSource2 ;
	double phaseSource1, phaseSource2 ;
	
	public TestCrossing(
				@ParamName(name="Set Wavlength (nm)") Wavelength inputLambda,
				@ParamName(name="Choose Input Source 1") AbstractInputSource inputSource1,
				@ParamName(name="Choose Input Source 2") AbstractInputSource inputSource2,
				@ParamName(name="Choose Thru Loss of Crossing dB") double thruLossdB
			){
		this.port1In = inputSource1.getElectricFieldAtInputWavelength(inputLambda) ;
		this.port2In = inputSource2.getElectricFieldAtInputWavelength(inputLambda) ;
		this.crossing = new SimpleCrossing(thruLossdB) ;
		this.stateSource1 = inputSource1.getInputSourceState() ;
		this.StateSource2 = inputSource2.getInputSourceState() ;
		this.phaseSource1 = inputSource1.getPhaseOfElectricFieldDegree(inputLambda) ;
		this.phaseSource2 = inputSource2.getPhaseOfElectricFieldDegree(inputLambda) ;
	}
	
	
	
	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {
		// TODO Auto-generated method stub
		
		
		DataPoint dp = new DataPoint() ;
		Complex zero = new Complex(0,0) ;
		Complex port1Out = crossing.getPort2(port1In, zero, zero, port2In) ;
		Complex port2Out = crossing.getPort3(port1In, zero, zero, port2In) ;
		
		dp.addResultProperty("Port 1 Out", checkNaN(10*Math.log10(port1Out.absSquared())) );
		dp.addResultProperty("Port 2 Out", -checkNaN(10*Math.log10(port2Out.absSquared())) ) ;
		dp.addProperty("State of Sources", stateSource1 + StateSource2);
		dp.addProperty("Phase difference of the input sources", phaseSource1-phaseSource2);
		
		man.addDataPoint(dp);
	}
	
	public double checkNaN(double x){
		if (x<0){
			if(Double.isInfinite(x)||Double.isNaN(x)){
				return -300 ;
			}
			else{
				return x ;
			}
		}
		else if(x>0){
			if(Double.isInfinite(x)||Double.isNaN(x)){
				return 300 ;
			}
			else{
				return x ;
			}
		}
		else{
			return 0 ;
		}
	}

}
