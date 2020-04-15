package People.Natalie;

import PhotonicElements.Utilities.MathLibraries.Complex;
import PhotonicElements.Utilities.PhysicalConstants;
import ch.epfl.general_libraries.clazzes.ParamName;

/**
 * The purpose of this class is to model graphene from  
 * "Electrical Control of Silicon Photonic Crystal Cavity by Graphene".
 * @author nsjan
 *
 */

public class Graphene_Natalie {

	// Ignoring the phsae change --> comes from imaginary part of sigma
	
	// Fermi as a function of voltage
	// conductivity as a function of Fermi
	// epsilon as a function of conductivity
	
	
	//Start here:
	PhysicalConstants pc = new PhysicalConstants();
	
	//Inputs/constants including values from paper
	double C = 20e-3, n0, vf = Math.pow(10,6), Vin, omega, gamma = 0.15,  tau, dg=1e-9;
	double q = PhysicalConstants.getElementaryCharge();
	double h = PhysicalConstants.getPlanckConstant();
	double epsilon0 = PhysicalConstants.getPermittivityVacuum();
	double Ef, sigmaReal;
	Complex epsilonSqrt;
	
	/**
	 * Complete constructor
	 * @param C is the effective capacitance per unit area (~20mF/m^2)
	 * @param n0 the intrinsic carrier concentration
	 * @param vf Fermi velocity
	 * @param Vin Voltage applied to the graphene layer-->INPUT
	 * @param gamma interband transition broadening (~150 mV)
	 * @param tau 1/tau is the free carrier scattering (negligible in conductivity )
	 * @param omega frequency
	 * @param dg thickness of graphene layer (~1nm)
	 */
	public Graphene_Natalie(
			@ParamName(name="Capacitance", default_="20e-3") double C, 
			@ParamName(name = "Intrinsic Carrier Concentration") double n0, 
			@ParamName(name = "Fermi Velocity") double vf, 
			@ParamName(name = "Applied Voltage") double Vin, 
			@ParamName(name = "Interband transistion broadening") double gamma, 
			@ParamName(name = "Inverse of free carrier scattering") double tau,
			@ParamName(name = "frequency")double omega, 
			@ParamName(name = "Thickness of graphene")double dg)
	{
		
		//Change global variables to match what user chooses
		this.C = C;
		this.vf = vf;
		this.dg = dg;
		this.gamma = gamma;
		this.Vin = Vin ;
		this.n0 = n0;
		this.tau=tau;
		this.omega = omega;
	}
	
	/**
	 * Constructor based on paper constraints
	 * @param n0 the intrinsic carrier concentration
	 * @param Vin Vin Voltage applied to the graphene layer-->INPUT
	 * @param tau 1/tau is the free carrier scattering (negligible)
	 * @param omega frequency
	 */
	public Graphene_Natalie( 
			@ParamName(name = "Intrinsic Carrier Concentration")double n0, 
			@ParamName(name = "Applied Voltage")double Vin, 
			@ParamName(name = "Inverse of free carrier scattering")double tau,
			@ParamName(name = "frequency")double omega){
	
		this.Vin = Vin ;
		this.n0 = n0;
		this.tau=tau;
		this.omega = omega;
		
	}
	
	public double getVin(){
		return Vin ;
	}
	
	public double getFermiLevel(){
		double Ef =  h/(2*Math.PI)*vf*Math.sqrt(Math.PI*(n0+C*Math.abs(Vin)/q));
		return (Ef/q * 1e3) ;
	}
	
	
	public double getSigma(){
		double Ef_temp = getFermiLevel();
		
		//Input to sigma1: f(omega)
		double f1 = Math.tanh((h*omega+2*Math.abs(Ef_temp))/gamma);
		double f2 = Math.tanh((h*omega-2*Math.abs(Ef_temp))/gamma);
		double f = q*q/(8*h)*(f1+f2);
		
		//sigmaReal = f+s1*s2
		double s1 = (q*q*gamma/(2*Math.PI*h*h))*(1/tau)/(omega*omega+(1/tau)); 
		double s2 = Math.log10(2*Math.cosh(2*Ef_temp/gamma));
		return (f + s1*s2);//sigmaReal
	}
	 
	public Complex getEpsilonSqrt(){
		double e1 = getSigma()/(omega*dg*epsilon0);
		Complex epsilon = new Complex(1,e1);
		return epsilon.sqrt();
	}
	
}