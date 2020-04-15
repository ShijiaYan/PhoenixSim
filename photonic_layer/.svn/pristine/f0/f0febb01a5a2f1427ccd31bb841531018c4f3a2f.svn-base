package PhotonicElements.EffectiveIndexMethod.ModeProfile.StripWg;

import PhotonicElements.EffectiveIndexMethod.ModeSolver.SlabWg.ModeSlabWgTE;
import PhotonicElements.EffectiveIndexMethod.ModeSolver.SlabWg.ModeSlabWgTM;
import PhotonicElements.EffectiveIndexMethod.Structures.SlabWg;
import PhotonicElements.EffectiveIndexMethod.Structures.StripWg;
import PhotonicElements.EffectiveIndexMethod.Structures.Regions.Region2D;
import PhotonicElements.Utilities.PhysicalConstants;
import PhotonicElements.Utilities.Wavelength;
import PhotonicElements.Utilities.MathLibraries.Complex;

/**
 * Based on the "improved Ey method" in the paper
 * @author Meisam
 *
 */

public class ProfileStripWgTE {
	
	Complex zero = new Complex(0,0), one = new Complex(1,0), plusJ = new Complex(0,1), minusJ = new Complex(0,-1) ;
	
	StripWg stripWg ; 
	SlabWg slabX ; // TM_m mode
	SlabWg slabY ; // TE_n mode
	Wavelength inputLambda ;
	int mIndex, nIndex ;
	double widthNm, heightNm ;
	double k0, omega, mu0, eps0 ;
	double kx, ky, betax, betay, beta, gamma2, gamma3, gamma4, gamma5 ;
	double xi_x, xi_y ;
	double n1, n2, n3, n4, n5 ;
	double d, b ;
	double xNm, yNm ;
	double A1, A2, A3, A4, A5, A6, A7, A8, A9, A10 ;
	Region2D region1, region2, region3, region4, region5 ;
	
	// by default the center of waveguide is at (0,0) 
	public ProfileStripWgTE(
			StripWg stripWg,
			int mIndex,
			int nIndex,
			double xNm,
			double yNm
			){
		this.stripWg = stripWg ;
		inputLambda = stripWg.getInputLambda() ;
		this.mIndex = mIndex ;
		this.nIndex = nIndex ;
		this.xNm = xNm ;
		this.yNm = yNm ;
		// setting up parameters
		setUpParameters();
		// finding xi and eta
		findXi();
		// finding field amplitudes
		findFieldAmplitudes_EzHz();
	}
	
	public void setX(double xNm){
		this.xNm = xNm ;
	}
	
	public void setY(double yNm){
		this.yNm = yNm ;
	}
	
	public void setXY(double xNm, double yNm){
		this.xNm = xNm ;
		this.yNm = yNm ;
	}
	
	// step 1: find kx, ky, beta, gamma2, gamma3, gamma4, gamma5
	private void setUpParameters(){
		// calculating all materials
		k0 = inputLambda.getK0() ;
		omega = inputLambda.getFreqHz()*2*Math.PI ;
		eps0 = PhysicalConstants.getPermittivityVacuum() ;
		mu0 = PhysicalConstants.getPermeabilityVacuum() ;
		// calculating the index of all regions
		n1 = stripWg.getCoreIndex() ;
		n2 = stripWg.getCladIndex() ;
		n3 = stripWg.getCladIndex() ;
		n4 = stripWg.getSubstrateIndex() ;
		n5 = stripWg.getCladIndex() ;
		// equivalent slabs
		slabX = stripWg.getSlabX() ;
		slabY = stripWg.getSlabY() ;
		// calculating kx, ky, and beta of the mode
		ModeSlabWgTM slabTM = new ModeSlabWgTM(slabX) ;
		double nx = slabTM.findSpecificModeIndex(mIndex) ;
		betax = k0 * nx ;
		kx = k0 * Math.sqrt(n1*n1-nx*nx) ;
		ModeSlabWgTE slabTE = new ModeSlabWgTE(slabY) ;
		double ny = slabTE.findSpecificModeIndex(nIndex) ;
		betay = k0 * ny ;
		ky = k0 * Math.sqrt(n1*n1-ny*ny) ;
		beta = Math.sqrt(betax*betax + betay*betay - k0*k0*n1*n1) ;
		// calculating gamma factors
		gamma2 = Math.sqrt((n1*n1-n2*n2)*k0*k0 - kx * kx) ;
		gamma3 = Math.sqrt((n1*n1-n3*n3)*k0*k0 - kx * kx) ;
		gamma4 = Math.sqrt((n1*n1-n4*n4)*k0*k0 - ky * ky) ;
		gamma5 = Math.sqrt((n1*n1-n5*n5)*k0*k0 - ky * ky) ;
		// finally defining the five regions
		d = widthNm = stripWg.getWidthNm() ;
		b = heightNm = stripWg.getHeightNm() ;
		
		region1 = new Region2D(-d/2, d/2, -b/2, b/2) ;
		region4 = new Region2D(-d/2, d/2, -Double.MAX_VALUE, -b/2) ;
		region5 = new Region2D(-d/2, d/2, b/2, Double.MAX_VALUE) ;
		region2 = new Region2D(-Double.MAX_VALUE, -d/2, -b/2, b/2) ;
		region3 = new Region2D(d/2, Double.MAX_VALUE, -b/2, b/2) ;
		
		d = widthNm * 1e-9 ;
		b = heightNm * 1e-9 ;
	}
	
	// step 2: find xi_x & xi_y
	@SuppressWarnings("unused")
	private void findXi(){
		// for x ************************
		double arg_xi_x1 = Math.atan((n1*n1*gamma2)/(n2*n2*kx)) ;
//		System.out.println(arg_xi_x1);
		double arg_xi_x2 = Math.atan((n1*n1*gamma3)/(n3*n3*kx)) ;
//		System.out.println(arg_xi_x2);
		xi_x = -d/2 + 1/kx * (mIndex*Math.PI/2 + arg_xi_x2) ;
//		System.out.println(xi_x);
//		System.out.println(Math.tan(kx*(xi_x-d/2))+ " = " + Math.tan(-mIndex*Math.PI/2 -arg_xi_x1));
//		System.out.println(Math.tan(kx*(xi_x+d/2))+ " = " + Math.tan(mIndex*Math.PI/2 +arg_xi_x2));
		// for y ************************
		double arg_xi_y1 = Math.atan(gamma4/ky) ;
//		System.out.println(arg_xi_y1);
		double arg_xi_y2 = Math.atan(gamma5/ky) ;
//		System.out.println(arg_xi_y2);
		xi_y = -b/2 + 1/ky * (nIndex*Math.PI/2 + arg_xi_y2) ;
//		System.out.println(xi_y);
//		System.out.println(Math.tan(ky*(xi_y-b/2))+ " = " + Math.tan(-nIndex*Math.PI/2 -arg_xi_y1));
//		System.out.println(Math.tan(ky*(xi_y+b/2))+ " = " + Math.tan(nIndex*Math.PI/2 +arg_xi_y2));
	}
	
	// step 3: find A1, A2, ..., A10
	private void findFieldAmplitudes_EzHz(){
		A1 = 1 ;
		A2 = A1 * beta*ky/(omega*mu0*kx) ;
		A3 = A1 * Math.sin(kx*(xi_x-d/2)) ;
		A4 = A2 * (1+k0*k0*(n1*n1-n2*n2)/(beta*beta))*Math.cos(kx*(xi_x-d/2)) ;
		A5 = A1 * Math.sin(kx*(xi_x+d/2)) ;
		A6 = A2 * (1+k0*k0*(n1*n1-n3*n3)/(beta*beta))*Math.cos(kx*(xi_x+d/2)) ;
		A7 = A1 * Math.cos(ky*(xi_y-b/2)) ;
		A8 = A2 * Math.sin(ky*(xi_y-b/2)) ;
		A9 = A1 * Math.cos(ky*(xi_y+b/2)) ;
		A10 = A2 * Math.sin(ky*(xi_y+b/2)) ;
	}
	
	public Complex get_Ez_field(){
		double x = xNm*1e-9 ;
		double y = yNm*1e-9 ;
		double ez = 0 ;
		if(region1.isInside(xNm, yNm) || region1.isOnTheBorder(xNm, yNm)){
			ez = A1 * Math.sin(kx*(x+xi_x)) * Math.cos(ky*(y+xi_y)) ;
		}
		else if(region2.isInside(xNm, yNm) || region2.isOnTheBorder(xNm, yNm)){
			ez = A3 * Math.exp(gamma2*(x+d/2)) * Math.cos(ky*(y+xi_y)) ;
		}
		else if(region3.isInside(xNm, yNm) || region3.isOnTheBorder(xNm, yNm)){
			ez = A5 * Math.exp(-gamma3*(x-d/2)) * Math.cos(ky*(y+xi_y)) ;
		}
		else if(region4.isInside(xNm, yNm) || region4.isOnTheBorder(xNm, yNm)){
			ez = A7 * Math.sin(kx*(x+xi_x)) * Math.exp(gamma4*(y+b/2)) ;
		}
		else if(region5.isInside(xNm, yNm) || region5.isOnTheBorder(xNm, yNm)){
			ez = A9 * Math.sin(kx*(x+xi_x)) * Math.exp(-gamma5*(y-b/2)) ;
		}
		return new Complex(ez, 0) ;
	}
	
	private Complex get_dEz_dx(){
		double x = xNm*1e-9 ;
		double y = yNm*1e-9 ;
		double dez_dx = 0 ;
		if(region1.isInside(xNm, yNm) || region1.isOnTheBorder(xNm, yNm)){
			dez_dx = A1 * kx * Math.cos(kx*(x+xi_x)) * Math.cos(ky*(y+xi_y)) ;
		}
		else if(region2.isInside(xNm, yNm) || region2.isOnTheBorder(xNm, yNm)){
			dez_dx = A3 * gamma2 * Math.exp(gamma2*(x+d/2)) * Math.cos(ky*(y+xi_y)) ;
		}
		else if(region3.isInside(xNm, yNm) || region3.isOnTheBorder(xNm, yNm)){
			dez_dx = A5 * (-gamma3)* Math.exp(-gamma3*(x-d/2)) * Math.cos(ky*(y+xi_y)) ;
		}
		else if(region4.isInside(xNm, yNm) || region4.isOnTheBorder(xNm, yNm)){
			dez_dx = A7 * kx * Math.cos(kx*(x+xi_x)) * Math.exp(gamma4*(y+b/2)) ;
		}
		else if(region5.isInside(xNm, yNm) || region5.isOnTheBorder(xNm, yNm)){
			dez_dx = A9 * kx * Math.cos(kx*(x+xi_x)) * Math.exp(-gamma5*(y-b/2)) ;
		}
		return new Complex(dez_dx, 0) ;
	}
	
	public Complex get_dEz_dy(){
		double x = xNm*1e-9 ;
		double y = yNm*1e-9 ;
		double dez_dy = 0 ;
		if(region1.isInside(xNm, yNm) || region1.isOnTheBorder(xNm, yNm)){
			dez_dy = A1 * Math.sin(kx*(x+xi_x)) * (-ky)* Math.sin(ky*(y+xi_y)) ;
		}
		if(region2.isInside(xNm, yNm) || region2.isOnTheBorder(xNm, yNm)){
			dez_dy = A3 * Math.exp(gamma2*(x+d/2)) * (-ky)* Math.sin(ky*(y+xi_y)) ;
		}
		else if(region3.isInside(xNm, yNm) || region3.isOnTheBorder(xNm, yNm)){
			dez_dy = A5 * Math.exp(-gamma3*(x-d/2)) * (-ky)* Math.sin(ky*(y+xi_y)) ;
		}
		else if(region4.isInside(xNm, yNm) || region4.isOnTheBorder(xNm, yNm)){
			dez_dy = A7 * Math.sin(kx*(x+xi_x)) * (gamma4)* Math.exp(gamma4*(y+b/2)) ;
		}
		else if(region5.isInside(xNm, yNm) || region5.isOnTheBorder(xNm, yNm)){
			dez_dy = A9 * Math.sin(kx*(x+xi_x)) * (-gamma5)* Math.exp(-gamma5*(y-b/2)) ;
		}
		return new Complex(dez_dy, 0) ;
	}
	
	public Complex get_Hz_field(){
		double x = xNm*1e-9 ;
		double y = yNm*1e-9 ;
		double hz = 0 ;
		if(region1.isInside(xNm, yNm) || region1.isOnTheBorder(xNm, yNm)){
			hz = A2 * Math.cos(kx*(x+xi_x)) * Math.sin(ky*(y+xi_y)) ;
		}
		else if(region2.isInside(xNm, yNm) || region2.isOnTheBorder(xNm, yNm)){
			hz = A4 * Math.exp(gamma2*(x+d/2)) * Math.sin(ky*(y+xi_y)) ;
		}
		else if(region3.isInside(xNm, yNm) || region3.isOnTheBorder(xNm, yNm)){
			hz = A6 * Math.exp(-gamma3*(x-d/2)) * Math.sin(ky*(y+xi_y)) ;
		}
		else if(region4.isInside(xNm, yNm) || region4.isOnTheBorder(xNm, yNm)){
			hz = A8 * Math.cos(kx*(x+xi_x)) * Math.exp(gamma4*(y+b/2)) ;
		}
		else if(region5.isInside(xNm, yNm) || region5.isOnTheBorder(xNm, yNm)){
			hz = A10 * Math.cos(kx*(x+xi_x)) * Math.exp(-gamma5*(y-b/2)) ;
		}
		return new Complex(hz, 0) ;
	}
	
	private Complex get_dHz_dx(){
		double x = xNm*1e-9 ;
		double y = yNm*1e-9 ;
		double dhz_dx = 0 ;
		if(region1.isInside(xNm, yNm) || region1.isOnTheBorder(xNm, yNm)){
			dhz_dx = A2 * (-kx)* Math.sin(kx*(x+xi_x)) * Math.sin(ky*(y+xi_y)) ;
		}
		else if(region2.isInside(xNm, yNm) || region2.isOnTheBorder(xNm, yNm)){
			dhz_dx = A4 * (gamma2)*Math.exp(gamma2*(x+d/2)) * Math.sin(ky*(y+xi_y)) ;
		}
		else if(region3.isInside(xNm, yNm) || region3.isOnTheBorder(xNm, yNm)){
			dhz_dx = A6 *(-gamma3)* Math.exp(-gamma3*(x-d/2)) * Math.sin(ky*(y+xi_y)) ;
		}
		else if(region4.isInside(xNm, yNm) || region4.isOnTheBorder(xNm, yNm)){
			dhz_dx = A8 * (-kx)* Math.sin(kx*(x+xi_x)) * Math.exp(gamma4*(y+b/2)) ;
		}
		else if(region5.isInside(xNm, yNm) || region5.isOnTheBorder(xNm, yNm)){
			dhz_dx = A10 * (-kx)* Math.sin(kx*(x+xi_x)) * Math.exp(-gamma5*(y-b/2)) ;
		}
		return new Complex(dhz_dx, 0) ;
	}
	
	public Complex get_dHz_dy(){
		double x = xNm*1e-9 ;
		double y = yNm*1e-9 ;
		double dhz_dy = 0 ;
		if(region1.isInside(xNm, yNm) || region1.isOnTheBorder(xNm, yNm)){
			dhz_dy = A2 * Math.cos(kx*(x+xi_x)) * (ky)* Math.cos(ky*(y+xi_y)) ;
		}
		else if(region2.isInside(xNm, yNm) || region2.isOnTheBorder(xNm, yNm)){
			dhz_dy = A4 * Math.exp(gamma2*(x+d/2)) * (ky)* Math.cos(ky*(y+xi_y)) ;
		}
		else if(region3.isInside(xNm, yNm) || region3.isOnTheBorder(xNm, yNm)){
			dhz_dy = A6 * Math.exp(-gamma3*(x-d/2)) * (ky)* Math.cos(ky*(y+xi_y)) ;
		}
		else if(region4.isInside(xNm, yNm) || region4.isOnTheBorder(xNm, yNm)){
			dhz_dy = A8 * Math.cos(kx*(x+xi_x)) * (gamma4)* Math.exp(gamma4*(y+b/2)) ;
		}
		else if(region5.isInside(xNm, yNm) || region5.isOnTheBorder(xNm, yNm)){
			dhz_dy = A10 * Math.cos(kx*(x+xi_x)) * (-gamma5)* Math.exp(-gamma5*(y-b/2)) ;
		}
		return new Complex(dhz_dy, 0) ;
	}
	
	public Complex get_Ex_field(){
		Complex coeff = minusJ.times(1/getKsquared()) ;
		Complex E_term = get_dEz_dx().times(beta) ;
		Complex H_term = get_dHz_dy().times(omega*mu0) ;
		Complex Ex = (E_term.plus(H_term)).times(coeff) ;
		return Ex ;
	}
	
	public Complex get_Ey_field(){
		Complex coeff = minusJ.times(1/getKsquared()) ;
		Complex E_term = get_dEz_dy().times(beta) ;
		Complex H_term = get_dHz_dx().times(-omega*mu0) ;
		Complex Ey = (E_term.plus(H_term)).times(coeff) ;
		return Ey ;
	}
	
	public Complex get_Hx_field(){
		Complex coeff = minusJ.times(1/getKsquared()) ;
		Complex E_term = get_dHz_dx().times(beta) ;
		Complex H_term = get_dEz_dy().times(-getIndex()*getIndex()*omega*eps0) ;
		Complex Hx = (E_term.plus(H_term)).times(coeff) ;
		return Hx ;
	}
	
	public Complex get_Hy_field(){
		Complex coeff = minusJ.times(1/getKsquared()) ;
		Complex E_term = get_dHz_dy().times(beta) ;
		Complex H_term = get_dEz_dx().times(getIndex()*getIndex()*omega*eps0) ;
		Complex Hy = (E_term.plus(H_term)).times(coeff) ;
		return Hy ;
	}
	
	private double getKsquared(){
		if(region1.isInside(xNm, yNm) || region1.isOnTheBorder(xNm, yNm)){
			return (n1*n1*k0*k0-beta*beta) ;
		}
		else if(region2.isInside(xNm, yNm) || region2.isOnTheBorder(xNm, yNm)){
			return (n2*n2*k0*k0-beta*beta) ;
		}
		else if(region3.isInside(xNm, yNm) || region3.isOnTheBorder(xNm, yNm)){
			return (n3*n3*k0*k0-beta*beta) ;
		}
		else if(region4.isInside(xNm, yNm) || region4.isOnTheBorder(xNm, yNm)){
			return (n4*n4*k0*k0-beta*beta) ;
		}
		else{
			return (n5*n5*k0*k0-beta*beta) ;
		}
	}
	
	public double getIndex(){
		if(region1.isInside(xNm, yNm) || region1.isOnTheBorder(xNm, yNm)){
			return (n1) ;
		}
		else if(region2.isInside(xNm, yNm) || region2.isOnTheBorder(xNm, yNm)){
			return (n2) ;
		}
		else if(region3.isInside(xNm, yNm) || region3.isOnTheBorder(xNm, yNm)){
			return (n3) ;
		}
		else if(region4.isInside(xNm, yNm) || region4.isOnTheBorder(xNm, yNm)){
			return (n4) ;
		}
		else{
			return (n5) ;
		}
	}
	

}
