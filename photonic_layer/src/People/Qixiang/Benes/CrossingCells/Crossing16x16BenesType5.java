package People.Qixiang.Benes.CrossingCells;

import PhotonicElements.Junctions.Crossings.SimpleCrossing;
import PhotonicElements.Utilities.MathLibraries.Complex;
import PhotonicElements.Utilities.MathLibraries.ComplexMatrix;
import PhotonicElements.Utilities.Wavelength;
import PhotonicElements.Waveguides.StraightWaveguide.StraightWg;
import PhotonicElements.Waveguides.WaveguideProperties.WgProperties;
import ch.epfl.general_libraries.clazzes.ParamName;

public class Crossing16x16BenesType5 {
	
	Wavelength inputLambda ;
	WgProperties wgProp ;
	SimpleCrossing wgCrossing ;
	StraightWg wg11, wg12, wg21, wg22 ;
	
	Complex zero = new Complex(0,0), one = new Complex(1,0) ;
	
	public Crossing16x16BenesType5(
			@ParamName(name="Wavelength (nm)") Wavelength inputLambda,
			@ParamName(name="Waveguide Properties") WgProperties wgProp,
			@ParamName(name="Crossing Model") SimpleCrossing wgCrossing
			) {
		this.inputLambda = inputLambda ;
		this.wgProp = wgProp ;
		this.wgCrossing = wgCrossing ;
		wg11 = new StraightWg(inputLambda, wgProp, 150, false, null, false, null) ;
		wg12 = new StraightWg(inputLambda, wgProp, 300, false, null, false, null) ;
		wg21 = new StraightWg(inputLambda, wgProp, 300, false, null, false, null) ;
		wg22 = new StraightWg(inputLambda, wgProp, 150, false, null, false, null) ;
	}
	
	
	public Complex[] getCrossingPorts(Complex[] portsIn){
		Complex[] portsOut = new Complex[4] ;
		portsOut[0] = portsIn[0] ;
		portsOut[1] = wgCrossing.getPort2(portsIn[1], zero, zero, portsIn[2]) ;
		portsOut[2] = wgCrossing.getPort3(portsIn[1], zero, zero, portsIn[2]) ;
		portsOut[3] = portsIn[3] ;
		
		portsOut[0]= portsOut[0].times(wg11.getS21()) ;
		portsOut[1]= portsOut[1].times(wg21.getS21()) ;
		portsOut[2]= portsOut[2].times(wg12.getS21()) ;
		portsOut[3]= portsOut[3].times(wg22.getS21()) ;
		
		return portsOut ;
	}
	
	public ComplexMatrix getTransitionMatrix(){
		double[][] elements = {{1,0,0,0}, 
				               {0,0,1,0}, 
				               {0,1,0,0}, 
				               {0,0,0,1}} ;
		ComplexMatrix T = new ComplexMatrix(elements) ;
		return T ;
	}
	
	
}
