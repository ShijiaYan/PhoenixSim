package PhotonicElements.Materials.Dielectric;

import PhotonicElements.Utilities.Wavelength;

public class Air extends AbstractDielectric {

	@Override
	public double getIndex(Wavelength inputLambda) {
		return 1;
	}
	
	@Override
	public double getGroupIndex(Wavelength inputLambda) {
		return 1;
	}

	@Override
	public double getEpsilon(Wavelength inputLambda) {
		return (eps0*getIndex(inputLambda)*getIndex(inputLambda)) ;
	}

	@Override
	public double getMu(Wavelength inputLambda) {
		return mu0 ;
	}

	@Override
	public String getMaterialName() {
		return "Air" ;
	}

}
