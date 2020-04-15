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
		return (xMax-xMin) ;
	}
	
	public boolean isInside(double x){
		if(x>xMin && x<xMax){
			return true ;
		}
		else{
			return false ;
		}
	}
	
	public boolean isOnTheLeftBorder(double x){
		if(x == xMin){
			return true ;
		}
		else{
			return false ;
		}
	}
	
	public boolean isOnTheRightBorder(double x){
		if(x == xMax){
			return true ;
		}
		else{
			return false ;
		}
	}
	
	public boolean isOnTheBorder(double x){
		if(isOnTheLeftBorder(x) || isOnTheRightBorder(x)){
			return true ;
		}
		else{
			return false ;
		}
	}
	
}
