package PhotonicElements.WavelengthLocking;

import PhotonicElements.Heater.SimpleHeater;
import PhotonicElements.Utilities.Wavelength;
import PhotonicElements.Waveguides.CurvedWaveguide.BendLossMode.ConstantBendLossModel;
import PhotonicElements.Waveguides.StraightWaveguide.StraightWg;
import PhotonicElements.Waveguides.WaveguideProperties.WgProperties;
import ch.epfl.general_libraries.clazzes.ParamName;
import flanagan.roots.RealRoot;
import flanagan.roots.RealRootDerivFunction;

public class ResonanceLock {

	double radiusMicron ;
	double lockedLambdaNm ;
	WgProperties wgProp ;

	public ResonanceLock(
			@ParamName(name="Ring Radius (micron)") double radiusMicron,
			@ParamName(name="Locked Wavelength (nm)") double lockedLambdaNm
			){
		this.radiusMicron = radiusMicron ;
		this.lockedLambdaNm = lockedLambdaNm ;
		this.wgProp = new WgProperties(10, 1, 0.8, new ConstantBendLossModel(1)) ;
	}
	
	public ResonanceLock(
			@ParamName(name="Ring Radius (micron)") double radiusMicron,
			@ParamName(name="Locked Wavelength (nm)") double lockedLambdaNm,
			WgProperties wgProp
			){
		this.radiusMicron = radiusMicron ;
		this.lockedLambdaNm = lockedLambdaNm ;
		this.wgProp = wgProp ;
	}

	private double findLockPhaseShiftRadian(){
		Wavelength inputLambda = new Wavelength(lockedLambdaNm) ;
//		WgProperties wgProp = new WgProperties(10, 1, 0.8, new ConstantBendLossModel(1)) ; // this is not important. we just need to pass it to wg model.
		StraightWg wg = new StraightWg(inputLambda, wgProp, 10, false, null, false, null) ; // length does not matter. We need only neff. (We can also use the Mode solver for the strip Wg...)
		final double neff = wg.getEffectiveIndex() ;

		RealRootDerivFunction func = new RealRootDerivFunction() {
			@Override
			public double[] function(double x) {
				double F = 1 - Math.cos(2*Math.PI/(lockedLambdaNm*1e-9) * neff * 2*Math.PI* (radiusMicron*1e-6) + x) ;
				double dF = Math.sin(2*Math.PI/(lockedLambdaNm*1e-9) * neff * 2*Math.PI* (radiusMicron*1e-6) + x) ;
				return new double[] {F, dF};
			}
		};
		RealRoot root = new RealRoot() ;
		double phaseShiftRadian = root.newtonRaphson(func, Math.PI);
		return phaseShiftRadian ;
	}

	private double findLockPhaseShiftDegree(){
		double phaseRadian = findLockPhaseShiftRadian() ;
		double phaseDegree = 180/(Math.PI) * phaseRadian ;
		return phaseDegree ;
	}

	public SimpleHeater getLockHeater(){
		SimpleHeater H = new SimpleHeater(findLockPhaseShiftDegree()) ;
		return H ;
	}

}
