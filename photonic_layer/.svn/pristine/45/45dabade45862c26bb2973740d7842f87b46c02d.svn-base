package PhotonicElements.Switches.PILOSS.Crossings;

import PhotonicElements.Junctions.Crossings.SimpleCrossing;
import PhotonicElements.Utilities.MathLibraries.Complex;
import PhotonicElements.Utilities.Wavelength;
import PhotonicElements.Waveguides.StraightWaveguide.StraightWg;
import PhotonicElements.Waveguides.WaveguideProperties.WgProperties;
import ch.epfl.general_libraries.clazzes.ParamName;

public class Crossing8x8PILOSS {

	Wavelength inputLambda ;
	WgProperties wgProp ;
	SimpleCrossing wgCrossing ;
	
	StraightWg wg00, wg12, wg21, wg34, wg43, wg56, wg65, wg77 ;
	
	Complex zero = new Complex(0,0), one = new Complex(1,0) ;
	
	public Crossing8x8PILOSS(
			@ParamName(name="Wavelength (nm)") Wavelength inputLambda,
			@ParamName(name="Waveguide Properties") WgProperties wgProp,
			@ParamName(name="Crossing Model") SimpleCrossing wgCrossing
			) {
		this.inputLambda = inputLambda ;
		this.wgProp = wgProp ;
		this.wgCrossing = wgCrossing ;
		wg00 = new StraightWg(inputLambda, wgProp, 100, false, null, false, null) ;
		wg12 = new StraightWg(inputLambda, wgProp, 200, false, null, false, null) ;
		wg21 = new StraightWg(inputLambda, wgProp, 200, false, null, false, null) ;
		wg34 = new StraightWg(inputLambda, wgProp, 200, false, null, false, null) ;
		wg43 = new StraightWg(inputLambda, wgProp, 200, false, null, false, null) ;
		wg56 = new StraightWg(inputLambda, wgProp, 200, false, null, false, null) ;
		wg65 = new StraightWg(inputLambda, wgProp, 200, false, null, false, null) ;
		wg77 = new StraightWg(inputLambda, wgProp, 100, false, null, false, null) ;
	}
	
	public Complex[] getCrossingPorts(Complex[] portsIn){
		Complex[] portsOut = {zero, zero, zero, zero, zero, zero, zero, zero}  ;
		SimpleCrossing X1 = wgCrossing, X2 = wgCrossing, X3 = wgCrossing ;
		portsOut[0] = portsIn[0].times(wg00.getS21()) ;
		portsOut[1] = X1.getPort2(portsIn[1], zero, zero, portsIn[2]).times(wg21.getS21()) ;
		portsOut[2] = X1.getPort3(portsIn[1], zero, zero, portsIn[2]).times(wg12.getS21()) ;
		portsOut[3] = X2.getPort2(portsIn[3], zero, zero, portsIn[4]).times(wg34.getS21()) ;
		portsOut[4] = X2.getPort3(portsIn[3], zero, zero, portsIn[4]).times(wg43.getS21()) ;
		portsOut[5] = X3.getPort2(portsIn[5], zero, zero, portsIn[6]).times(wg56.getS21()) ;
		portsOut[6] = X3.getPort3(portsIn[5], zero, zero, portsIn[6]).times(wg65.getS21()) ;
		portsOut[0] = portsIn[7].times(wg77.getS21()) ;
		return portsOut ;
	}
	
}
