package PhotonicElements.TimeDomain.TimeSimulation;

import PhotonicElements.Utilities.MathLibraries.MoreMath;
import ch.epfl.general_libraries.clazzes.ParamName;

public class TimeEO {
	
	double tmin_nsec ;
	double tmax_nsec ;
	double Dt_nsec ;
	int numPoints ;
	double[] time_nsec ;
	
	public TimeEO(
			@ParamName(name="Start Point (nsec)", default_="0") double tmin_nsec,
			@ParamName(name="End Point (nsec)") double tmax_nsec,
			@ParamName(name="Step size (nsec)") double Dt_nsec
			){
		this.tmin_nsec = tmin_nsec ;
		this.tmax_nsec = tmax_nsec ;
		this.Dt_nsec = Dt_nsec ;
		this.time_nsec = MoreMath.linspace(tmin_nsec, tmax_nsec, Dt_nsec) ;
		this.numPoints = (int) ((tmax_nsec-tmin_nsec)/Dt_nsec) ;
	}
	
	
	
	

}
