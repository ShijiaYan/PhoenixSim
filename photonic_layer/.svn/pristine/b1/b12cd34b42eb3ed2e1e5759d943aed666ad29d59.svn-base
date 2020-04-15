package PhotonicElements.EffectiveIndexMethod.Structures.Regions;

public class Region2D {

	double xMin, xMax ;
	double yMin, yMax ;
	
	public Region2D(
			double xMin, 
			double xMax,
			double yMin,
			double yMax
			){
		this.xMin = xMin ;
		this.xMax = xMax ;
		this.yMin = yMin ;
		this.yMax = yMax ;
	}
	
	public Region2D(){
		xMin = -Double.MAX_VALUE ;
		xMax = Double.MAX_VALUE ;
		yMin = -Double.MAX_VALUE ;
		yMax = Double.MAX_VALUE ;
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
	
	public void setYMin(double yMin){
		this.yMin = xMin ;
	}
	
	public void setYMax(double yMax){
		this.yMax = yMax ;
	}
	
	public void setYMinMax(double yMin, double yMax){
		this.yMin = yMin ;
		this.yMax = yMax ;
	}
	
	public double getXWidth(){
		return (xMax-xMin) ;
	}
	
	public double getYWidth(){
		return (yMax-yMin) ;
	}
	
	public boolean isInside(double x, double y){
		if(x>xMin && x<xMax && y>yMin && y<yMax){
			return true ;
		}
		else{
			return false ;
		}
	}
	
	public boolean isOnTheLeftBorder(double x, double y){
		if(x == xMin){
			return true ;
		}
		else{
			return false ;
		}
	}
	
	public boolean isOnTheRightBorder(double x, double y){
		if(x == xMax){
			return true ;
		}
		else{
			return false ;
		}
	}
	
	public boolean isOnTheBottomBorder(double x, double y){
		if(y == yMin){
			return true ;
		}
		else{
			return false ;
		}
	}
	
	public boolean isOnTheTopBorder(double x, double y){
		if(y == yMax){
			return true ;
		}
		else{
			return false ;
		}
	}
	
	public boolean isOnTheBorder(double x, double y){
		if(isOnTheLeftBorder(x, y) || isOnTheRightBorder(x, y) || isOnTheBottomBorder(x, y) || isOnTheTopBorder(x, y)){
			return true ;
		}
		else{
			return false ;
		}
	}
	
	//****** for test
	public static void main(String[] args){
		double x = -10 ;
		double y = -2 ;
		Region2D region = new Region2D(-10, Double.MAX_VALUE, -2, 10) ;
		System.out.println(region.isInside(x, y));
		System.out.println(region.isOnTheBorder(x, y));
//		System.out.println(Double.MAX_VALUE);
//		System.out.println(-Double.MAX_VALUE);
	}
	
}
