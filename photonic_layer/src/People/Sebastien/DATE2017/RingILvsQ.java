package People.Sebastien.DATE2017;

import PhotonicElements.Waveguides.CurvedWaveguide.BendLossMode.AbstractBendLossModel;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;

public class RingILvsQ implements Experiment {

	double radiusMicron, Q, alphaDBperCm, alphaPerMeter;
	AbstractBendLossModel bendLoss ;
	
	public RingILvsQ(
			@ParamName(name="Ring Radius (um)") double radiusMicron,
			@ParamName(name="Ring Qaulity Factor") double Q,
			@ParamName(name="Bend Loss Model") AbstractBendLossModel bendLoss
			){
		this.radiusMicron = radiusMicron ;
		this.Q = Q ;
		this.bendLoss = bendLoss ;
		alphaDBperCm = bendLoss.getLossdBperCm(radiusMicron) ;
		alphaPerMeter = bendLoss.getLossPerMeter(radiusMicron) ;
	}
	
	/**
	 * 	Strip waveguide: 450nm X 220nm
	 */

	// Calculating the effective index of the TE00 mode
	public double getEffectiveIndex(double lambdaNm) {
		double lambdaMicron = lambdaNm/1000 ;
		double A4 = 0.3391437655 ;
		double A3 = -1.954733 ;
		double A2 = 4.155727848 ;
		double A1 = -5.089414343 ;
		double A0 = 5.585688163 ;
		double neff = A4*Math.pow(lambdaMicron, 4) + A3*Math.pow(lambdaMicron, 3)+
				A2*Math.pow(lambdaMicron, 2)+A1*lambdaMicron + A0 ;
		return neff ;
	}
	// Calculating the group index of the TE00 mode
	public double getGroupIndex(double lambdaNm) {
		double lambdaMicron = lambdaNm/1000 ;
		double A4 = 0.3391437655 ;
		double A3 = -1.954733 ;
		double A2 = 4.155727848 ;
		double A1 = -5.089414343 ;
		double neff = getEffectiveIndex(lambdaNm) ;
		double dneff_dlambda = 4*A4*Math.pow(lambdaMicron, 3) + 3*A3*Math.pow(lambdaMicron, 2)+
				2*A2*lambdaMicron + A1 ;
		double ng = neff - lambdaMicron * dneff_dlambda ;
		return ng;
	}
	// Calculating the round trip loss inside the ring (power attenuation --> unit less)
	public double getRoundTripLoss(){
		double loss = Math.exp(-2*Math.PI*radiusMicron*1e-6 * alphaPerMeter) ;
		return loss ;
	}
	// Calculating the round trip loss inside the ring (power loss --> dB units)
	public double getRoundTripLossDB(){
		double loss = Math.exp(-2*Math.PI*radiusMicron*1e-6 * alphaPerMeter) ;
		return -10*Math.log10(loss) ;
	}
	// Calculating the resonance of the ring at the proximity of 1550nm
	public double getResonanceLambdaNm(){
		double lambda0 = 1550 ;
		double DlambdaNm = 0.05 ;
		double resLambdaNm = lambda0 ;
		double error = 1e-6 ;
		double neff = getEffectiveIndex(resLambdaNm) ;
		while(resEquation(resLambdaNm, neff)>error){
			resLambdaNm += DlambdaNm ;
			neff = getEffectiveIndex(resLambdaNm) ;
		}
		return resLambdaNm ;
	}
	// mode number: 2*pi*R*neff = modeNumber * lambdaRes
	public int getResonanceNumber(){
		double lambdaResNm = getResonanceLambdaNm() ;
		double neff = getEffectiveIndex(lambdaResNm) ;
		int m = (int) (2*Math.PI*radiusMicron*1e-6 * neff / (lambdaResNm * 1e-9)) ;
		return m ;
	}
	// Need to setup the resonance equation inside the ring
	private double resEquation(double lambdaNm, double neff){
		double F = 1 - Math.cos(2*Math.PI/(lambdaNm*1e-9) * neff * 2*Math.PI* (radiusMicron*1e-6)) ; 
		return Math.abs(F*F) ;
	}
	// Calculating the intrinsic Q of the ring due to the ring's loss
	public double getIntrinsicQ(){
		double resLambdaNm = getResonanceLambdaNm() ;
		double ng = getGroupIndex(resLambdaNm) ;
		double Qi = 2*Math.PI*ng/(alphaPerMeter*resLambdaNm*1e-9) ;
		return Qi ;
	}
	// drop IL at the resonance
	public double getILdB(){
		double Qi = getIntrinsicQ() ;
		double arg = 1- 2*Q/Qi ;
		return -10*Math.log10(arg) ;
	}
	
	public double getOutputKappa(){
		double Qi = getIntrinsicQ() ;
		double tauI = Qi/(Math.PI * getResFreqHz()) ;
		double Qin = 2*Q ;
		double Qout = 1/(1/Qin - 1/Qi) ;
		double tauOut = Qout/(Math.PI * getResFreqHz()) ;
		double y = tauI/tauOut ;
		double arg = 2*Math.PI*radiusMicron*1e-6*alphaPerMeter * y ;
		return Math.sqrt(arg) ;
	}
	
	public double getOutputT(){
		double kappaOut = getOutputKappa() ;
		double tOut = Math.sqrt(1- kappaOut * kappaOut) ;
		return tOut ;
	}
	
	public double getInputKappa(){
		double kappaOut = getOutputKappa() ;
		double tOut = Math.sqrt(1 - kappaOut * kappaOut) ;
		double loss = getRoundTripLoss() ;
		double tIn = tOut * Math.sqrt(loss) ;
		double kappaIn = Math.sqrt(1 - tIn * tIn) ;
		return kappaIn ;
	}
	
	public double getInputT(){
		double kappaIn = getInputKappa() ;
		double tIn = Math.sqrt(1- kappaIn * kappaIn) ;
		return tIn ;
	}
	
	public double getFSRnm(){
		double lambdaResNm = getResonanceLambdaNm() ;
		double ng = getGroupIndex(lambdaResNm) ;
		double fsrNm = lambdaResNm * lambdaResNm*1e-3 / (2*Math.PI*radiusMicron*ng) ;
		return fsrNm ;
	}
	
	public double getFSRhz(){
		double lambdaResNm = getResonanceLambdaNm() ;
		double ng = getGroupIndex(lambdaResNm) ;
		double groupVelocity = 3e8/ng ;
		double fsrHz = groupVelocity/(2*Math.PI * radiusMicron * 1e-6) ;
		return fsrHz ;
	}
	
	public double getBWnm(){
		double resLambdaNm = getResonanceLambdaNm() ;
		return (resLambdaNm/Q) ;
	}
	
	public double getBWhz(){
		double c = 3e8 ;
		double resLambdaNm = getResonanceLambdaNm() ;
		double resFreqHz = c/(resLambdaNm*1e-9) ;
		return (resFreqHz/Q) ;
	}
	
	public double getBWGhz(){
		return getBWhz()/1e9 ;
	}

	public double getResFreqHz(){
		double c = 3e8 ;
		double resLambdaNm = getResonanceLambdaNm() ;
		double resFreqHz = c/(resLambdaNm*1e-9) ;
		return resFreqHz ;
	}

	// now run the experiment
	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {
		DataPoint dp = new DataPoint() ;
		dp.addProperty("radius (um)", radiusMicron);
		dp.addProperty("Q", Q);
		dp.addProperty("Bend Loss (dB/cm)", alphaDBperCm);
		dp.addResultProperty("Neff", getEffectiveIndex(getResonanceLambdaNm()));
		dp.addResultProperty("Ng", getGroupIndex(getResonanceLambdaNm()));
		dp.addResultProperty("Res Lambda (nm)", getResonanceLambdaNm());
		dp.addResultProperty("Res Mode Number", getResonanceNumber());
		dp.addResultProperty("FSR (nm)", getFSRnm());
		dp.addResultProperty("BW (GHz)", getBWGhz());
		dp.addResultProperty("Round Trip Loss (dB)", getRoundTripLossDB());
		dp.addResultProperty("Output Kappa", getOutputKappa());
		dp.addResultProperty("Input Kappa", getInputKappa());
		dp.addResultProperty("IL drop (dB)", getILdB());
		dp.addResultProperty("Qi", getIntrinsicQ());
		dp.addResultProperty("Finesse", getFSRhz()/(getBWGhz()*1e9));		
		man.addDataPoint(dp);
	}
	
}
