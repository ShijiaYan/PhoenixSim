package PhotonicElements.Utilities.MathLibraries.RealRoots;

import static java.lang.Math.* ;

import People.Meisam.GUI.Plotters.MatlabPlot.MatlabChart;
import PhotonicElements.Utilities.MathLibraries.MoreMath;
import PhotonicElements.Utilities.MathLibraries.RealRootFinder;
import flanagan.roots.RealRootFunction;

public class SecondOrderPolynomialRoots {

	// p(x) = a * x^2 + b * x + c
	
	double a, b, c ;
	double delta ;
	double root1, root2 ;
	int numRealRoots ;
	
	public SecondOrderPolynomialRoots(
			double a,
			double b,
			double c
			){
		this.a = a ;
		this.b = b ;
		this.c = c ;
		computeRoots();
	}
	
	public void setA(double a){
		this.a = a ;
	}
	
	public void setB(double b){
		this.b = b ;
	}
	
	public void setC(double c){
		this.c = c ;
	}
	
	public double getA(){
		return a ;
	}
	
	public double getB(){
		return b ;
	}
	
	public double getC(){
		return c ;
	}
	
	private void computeRoots(){
//		if(a==0){
//			numRealRoots = 1 ;
//			root1 = -c/b ;
//			root2 = -c/b ;
//		}
		delta = b*b-4*a*c ;
		if(delta > 0){
			numRealRoots = 2 ;
			root1 = (-b + sqrt(delta))/(2*a) ;
			root2 = (-b - sqrt(delta))/(2*a) ;
		}
		else if(delta == 0){
			numRealRoots = 1 ;
			root1 = -b/(2*a) ;
			root2 = -b/(2*a) ;
		}
		else{
			numRealRoots = 0 ;
		}
		
	}
	
	public int getNumRealRoots(){
		return numRealRoots ;
	}
	
	public double[] getRealRoots(){
		if(numRealRoots==0){return new double[]{Double.NaN, Double.NaN} ;}
		else if(numRealRoots == 1){return new double[]{root1, root2} ;}
		else{return new double[]{root1, root2}; }
	}
	
	public double getFirstRealRoot(){
		if(numRealRoots==0){return Double.NaN ;}
		else{return root1; }
	}
	
	public double getSecondRealRoot(){
		if(numRealRoots==0){return Double.NaN ;}
		else{return root2; }
	}
	
	public double getValue(double x){
		return (a*x*x+b*x+c) ;
	}
	
	
	// for test
	public static void main(String[] args){
		SecondOrderPolynomialRoots roots = new SecondOrderPolynomialRoots(-1, 3, 1) ;
		System.out.println(roots.getNumRealRoots());
		System.out.println(roots.getFirstRealRoot());
		System.out.println(roots.getSecondRealRoot());
		
		RealRootFunction func = new RealRootFunction(){

			@Override
			public double function(double x) {
				return roots.getValue(x);
			}
			
		} ;
		
		RealRootFinder rootFinder = new RealRootFinder(func, -10, 10) ;
		rootFinder.findAllRoots(); 
		rootFinder.showAllRoots();
		
		double[] x = MoreMath.linspace(-10, 10, 1000) ;
		double[] y = new double[x.length] ;
		for(int i=0; i<x.length; i++){
//			y[i] = roots.getValue(x[i]) ;
			y[i] = func.function(x[i]) ;
		}
		
		MatlabChart fig = new MatlabChart() ;
		fig.plot(x, y);
		fig.RenderPlot();
		fig.run(true);
		
	}
	
	
	
}
