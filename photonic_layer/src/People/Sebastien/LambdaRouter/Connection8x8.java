package People.Sebastien.LambdaRouter;

import PhotonicElements.Utilities.MathLibraries.Complex;
import PhotonicElements.Utilities.Wavelength;
import PhotonicElements.Waveguides.StraightWaveguide.StraightWg;
import PhotonicElements.Waveguides.WaveguideProperties.WgProperties;
import ch.epfl.general_libraries.clazzes.ParamName;

public class Connection8x8 {

	Wavelength inputLambda ;
	WgProperties wgProp ;
	StraightWg wg1, wg2 ;
	
	Complex zero = new Complex(0,0), one = new Complex(1,0) ;
	
	public Connection8x8(
			@ParamName(name="Wavelength (nm)") Wavelength inputLambda,
			@ParamName(name="Waveguide Properties") WgProperties wgProp
			){
		this.inputLambda = inputLambda ;
		this.wgProp = wgProp ;
		wg1 = new StraightWg(inputLambda, wgProp, 100, false, null, false, null) ;
		wg2 = new StraightWg(inputLambda, wgProp, 150, false, null, false, null) ;
	}
	
	public Complex[] getCrossingPorts(Complex[] portsIn){
		Complex[] portsOut = new Complex[8] ;
		for(int i=0; i<8; i++){
			portsOut[i] = zero ;
		}
		
		portsOut[0] = portsIn[0].times(wg1.getS21()) ;
		portsOut[1] = portsIn[1].times(wg2.getS21()) ;
		portsOut[2] = portsIn[2].times(wg2.getS21()) ;
		portsOut[3] = portsIn[3].times(wg2.getS21()) ;
		portsOut[4] = portsIn[4].times(wg2.getS21()) ;
		portsOut[5] = portsIn[5].times(wg2.getS21()) ;
		portsOut[6] = portsIn[6].times(wg2.getS21()) ;
		portsOut[7] = portsIn[7].times(wg1.getS21()) ;
		
		return portsOut ;
	}
	
}
