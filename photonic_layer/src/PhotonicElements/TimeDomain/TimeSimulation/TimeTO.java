package PhotonicElements.TimeDomain.TimeSimulation;

import PhotonicElements.Utilities.MathLibraries.MoreMath;
import ch.epfl.general_libraries.clazzes.ParamName;

public class TimeTO {
	
	double tmin_usec ;
	double tmax_usec ;
	double Dt_usec ;
	int numPoints ;
	double[] time_usec ;
	
	public TimeTO(
			@ParamName(name="Start Point (usec)", default_="0") double tmin_usec,
			@ParamName(name="End Point (usec)") double tmax_usec,
			@ParamName(name="Step size (usec)") double Dt_usec
			){
		this.tmin_usec = tmin_usec ;
		this.tmax_usec = tmax_usec ;
		this.Dt_usec = Dt_usec ;
		this.time_usec = MoreMath.linspace(tmin_usec, tmax_usec, Dt_usec) ;
		this.numPoints = (int) ((tmax_usec-tmin_usec)/Dt_usec) ;
	}
	
	
	
	

}
