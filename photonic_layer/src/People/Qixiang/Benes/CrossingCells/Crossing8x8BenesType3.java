package People.Qixiang.Benes.CrossingCells;

import PhotonicElements.Junctions.Crossings.SimpleCrossing;
import PhotonicElements.Utilities.MathLibraries.Complex;
import PhotonicElements.Utilities.MathLibraries.ComplexMatrix;
import PhotonicElements.Utilities.Wavelength;
import PhotonicElements.Waveguides.StraightWaveguide.StraightWg;
import PhotonicElements.Waveguides.WaveguideProperties.WgProperties;
import ch.epfl.general_libraries.clazzes.ParamName;

public class Crossing8x8BenesType3 {

	Wavelength inputLambda ;
	WgProperties wgProp ;
	SimpleCrossing wgCrossing ;
	
	StraightWg wg11, wg13 ;
	StraightWg wg21, wg23 ;
	StraightWg wg32, wg34 ;
	StraightWg wg42, wg44 ;
	
	Complex zero = new Complex(0,0), one = new Complex(1,0) ;
	
	public Crossing8x8BenesType3(
			@ParamName(name="Wavelength (nm)") Wavelength inputLambda,
			@ParamName(name="Waveguide Properties") WgProperties wgProp,
			@ParamName(name="Crossing Model") SimpleCrossing wgCrossing
			) {
		this.inputLambda = inputLambda ;
		this.wgProp = wgProp ;
		this.wgCrossing = wgCrossing ;
		wg11 = new StraightWg(inputLambda, wgProp, 350, false, null, false, null) ;
		wg13 = new StraightWg(inputLambda, wgProp, 650, false, null, false, null) ;
		wg21 = new StraightWg(inputLambda, wgProp, 350, false, null, false, null) ;
		wg23 = new StraightWg(inputLambda, wgProp, 550, false, null, false, null) ;
		wg32 = new StraightWg(inputLambda, wgProp, 350, false, null, false, null) ;
		wg34 = new StraightWg(inputLambda, wgProp, 550, false, null, false, null) ;
		wg42 = new StraightWg(inputLambda, wgProp, 650, false, null, false, null) ;
		wg44 = new StraightWg(inputLambda, wgProp, 350, false, null, false, null) ;
	}
	
	public Complex[] getCrossingPorts(Complex[] portsIn){
		Complex[] portsOut = {portsIn[0], zero, zero, zero, zero, zero, zero, portsIn[7]}  ;
		SimpleCrossing X1=wgCrossing, X2=wgCrossing, X3=wgCrossing, X4=wgCrossing, X5=wgCrossing, X6=wgCrossing ;
		Complex[] X1_ports_out = {zero, zero, zero, zero} ;
		Complex[] X2_ports_out = {zero, zero, zero, zero} ;
		Complex[] X3_ports_out = {zero, zero, zero, zero} ;
		Complex[] X4_ports_out = {zero, zero, zero, zero} ;
		Complex[] X5_ports_out = {zero, zero, zero, zero} ;
		Complex[] X6_ports_out = {zero, zero, zero, zero} ;
		int steps = 5 ;
		for(int i=0; i<steps; i++){
			X1_ports_out = X1.getAllPorts(portsIn[1], zero, zero, X2_ports_out[1]) ;
			X2_ports_out = X2.getAllPorts(portsIn[2], X1_ports_out[3], X4_ports_out[0], X3_ports_out[1]) ;
			X3_ports_out = X3.getAllPorts(portsIn[3], X2_ports_out[3], X5_ports_out[0], portsIn[4]) ;
			X4_ports_out = X4.getAllPorts(X2_ports_out[2], zero, zero, X5_ports_out[1]) ;
			X5_ports_out = X5.getAllPorts(X3_ports_out[2], X4_ports_out[3], X6_ports_out[0], portsIn[5]) ;
			X6_ports_out = X6.getAllPorts(X5_ports_out[2], zero, zero, portsIn[6]) ;
			for(int j=0; j<portsIn.length; j++){portsIn[j] = zero ;} ;
			portsOut[0]= portsOut[0].plus(portsIn[0]) ;
			portsOut[1]= portsOut[1].plus(X1_ports_out[1]) ;
			portsOut[2]= portsOut[2].plus(X1_ports_out[2]) ;
			portsOut[3]= portsOut[3].plus(X4_ports_out[1]) ;
			portsOut[4]= portsOut[4].plus(X4_ports_out[2]) ;
			portsOut[5]= portsOut[5].plus(X6_ports_out[1]) ;
			portsOut[6]= portsOut[6].plus(X6_ports_out[2]) ;
			portsOut[7]= portsOut[7].plus(portsIn[7]) ;
		}
		portsOut[0]= portsOut[0].times(wg11.getS21()) ;
		portsOut[1]= portsOut[1].times(wg21.getS21()) ;
		portsOut[2]= portsOut[2].times(wg32.getS21()) ;
		portsOut[3]= portsOut[3].times(wg42.getS21()) ;
		portsOut[4]= portsOut[4].times(wg13.getS21()) ;
		portsOut[5]= portsOut[5].times(wg23.getS21()) ;
		portsOut[6]= portsOut[6].times(wg34.getS21()) ;
		portsOut[7]= portsOut[7].times(wg44.getS21()) ;
		return portsOut ;
	}
		
	
	public ComplexMatrix getTransitionMatrix(){
		double[][] elements = {{1,0,0,0,0,0,0,0}, 
				               {0,0,0,0,1,0,0,0}, 
				               {0,1,0,0,0,0,0,0}, 
				               {0,0,0,0,0,1,0,0}, 
				               {0,0,1,0,0,0,0,0}, 
				               {0,0,0,0,0,0,1,0}, 
				               {0,0,0,1,0,0,0,0}, 
				               {0,0,0,0,0,0,0,1}} ;
		ComplexMatrix T = new ComplexMatrix(elements) ;
		return T ;
	}
	
}
