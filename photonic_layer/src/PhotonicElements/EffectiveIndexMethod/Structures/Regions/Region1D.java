package PhotonicElements.EffectiveIndexMethod.Structures.Regions;

public class Region1D {

	double xMin, xMax ;
	
	public Region1D(
			double xMin, 
			double xMax
			){
		this.xMin = xMin ;
		this.xMax = xMax ;
	}
	
	public Region1D(){
		xMin = Double.MIN_VALUE ;
		xMax = Double.MAX_VALUE ;
	}
	
	public void setXMin(double xMin){
		this.xMin = xMin ;
	}
	
	public void setXMax(double xMax){
		this.xMax = xMax ;
	}
	
	public void setXMinMax(double xMin, double xMax){
		this.xMin = xMin ;
		this.xMax = xMax ;
	}
	
	public double getWidth(){
		return xMax-xMin;
	}
	
	public boolean isInside(double x){
        return x > xMin && x < xMax;
	}
	
	public boolean isOnTheLeftBorder(double x){
        return x == xMin;
	}
	
	public boolean isOnTheRightBorder(double x){
        return x == xMax;
	}
	
	public boolean isOnTheBorder(double x){
        return isOnTheLeftBorder(x) || isOnTheRightBorder(x);
	}
	
}
