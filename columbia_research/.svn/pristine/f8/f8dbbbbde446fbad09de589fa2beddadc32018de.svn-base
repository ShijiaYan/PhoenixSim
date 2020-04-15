package archives.common.physical_layer;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.traffic.Rate;
import ch.epfl.general_libraries.utils.SimpleMap;


public abstract class GlobalConstantSet {
	
	boolean wavelengthsFixed;
	boolean sitesFixed;
//	NetworkInfo networkInfo;
	
	int switchRadix;
	
	public int getSwitchRadix() {
		return switchRadix;
	}
	public void setSwitchRadix(int switchRadix) {
		this.switchRadix = switchRadix;
	}
	//public NetworkInfo getNetworkInfo() {
//		return networkInfo;
//	}
//	public void setNetworkInfo(NetworkInfo networkInfo) {
//		this.networkInfo = networkInfo;
//	}

	double crossTalk; //Actually set by models--a function of (at least) the cross talk from switches
	
	public double getCrossTalk() {
		return crossTalk;
	}
	public void setCrossTalk(double crossTalk) {
		this.crossTalk = crossTalk;
	}
	
	int nbWavelengths;
	int numberOfCores;
	int sitesPerBranch;
	int numStages;
	public int getNumStages() {
		return numStages;
	}
	public void setNumStages(int numStages) {
		this.numStages = numStages;
	}

	//dual is 1 for the dual waveguide switched architecture; 0 for the single one and 2 for the iSCA
	int dual;
	
	public static class FixedCoreConstantSet extends GlobalConstantSet {
		public FixedCoreConstantSet(@ParamName(name="Number of sites") int numberOfCores) {
			super(numberOfCores, (long)1, (int)1);
		}
		public FixedCoreConstantSet(@ParamName(name="Number of sites") int numberOfCores,
									@ParamName(name="Sites per branch") long sitesPerBranch,
									@ParamName(name="Dual Bus? (2-iSCA;1dual,0single)") int dual) {
			super(numberOfCores, (long)sitesPerBranch,  dual);
	}
	}
	
	public static class FixedCoreConstantSetSingleBus extends GlobalConstantSet {
		public FixedCoreConstantSetSingleBus(@ParamName(name="Number of sites") int numberOfCores) {
			super(numberOfCores);
		}
	}
	public static class AllFixedConstantSetSingleBus extends GlobalConstantSet {
		public AllFixedConstantSetSingleBus(
				@ParamName(name="Number of sites") int nbCores,
				@ParamName(name="Number of wavelengths") int nbWave
				) {
			super(nbCores, nbWave);
		}
	}
	public static class FixedWavelengthsConstantSet extends GlobalConstantSet {
		public FixedWavelengthsConstantSet(@ParamName(name="Number of wavelengths") int wave) {
			super(wave, (int)1, (int)1);
		}
		
		public FixedWavelengthsConstantSet(@ParamName(name="Number of wavelengths") int wave,
									@ParamName(name="Sites per branch") int sitesPerBranch,
									@ParamName(name="Dual Bus?(2-iSCA;1dual,0single)") int dual) {
			super(wave, sitesPerBranch, dual);
		}		
	}
	
	public static class AllFixedConstantSet extends GlobalConstantSet {
		public AllFixedConstantSet(@ParamName(name="Number of sites") int numberOfCores,
								   @ParamName(name="Number of wavelengths") int wave) {
			super(wave, numberOfCores, (int)1,(int)1);
		}
		
		public AllFixedConstantSet(@ParamName(name="Number of sites") int numberOfCores,
								   @ParamName(name="Number of wavelengths") int wave,
									@ParamName(name="Sites per branch") int sitesPerBranch,
									@ParamName(name="Dual Bus? (2-iSCA;1dual,0single)") int dual) {
			super(wave, numberOfCores, sitesPerBranch, dual);
		}		
	}


	//the constructor for P-SCAN dual bus with fixed number of sites
	//with "branches" sites per branch and branching if "branching is true and clustering otherwise 
	protected GlobalConstantSet(int nbCores, long branches, int dual) {
		this.numberOfCores = nbCores;
		this.sitesPerBranch = (int)branches;
		this.sitesFixed = true;
		this.dual = dual;
	}
//The constructor for P-SCAN single bus with fixed number of sites
	protected GlobalConstantSet(int nbCores) {
		this.numberOfCores = nbCores;
		this.sitesPerBranch = nbCores;
		this.sitesFixed = true;
		this.dual = 1;
	}
	//Constructor for P-SCAN single bus with fixed number of sites and wavelengths
	protected GlobalConstantSet(int nbCores, int nbWave) {
		this.numberOfCores = nbCores;
		this.sitesPerBranch = nbCores;
		this.nbWavelengths = nbWave;
		this.sitesFixed = true;
		this.wavelengthsFixed = true;
		this.dual = 1;
	}
	
	//the constructor for P-SCAN dual bus when the number of wavelengths is fixed  
	// with "branches" sites per branch and branching if "branching is true and clustering otherwise 
	protected GlobalConstantSet(int nbWave, int branches, int dual) {
		this.nbWavelengths = nbWave;
		this.sitesPerBranch = branches;
		this.wavelengthsFixed = true;
		this.dual = dual;
	}
	
	
	//the constructor for P-SCAN dual bus when the number of wavelengths is fixed and  with fixed number of sites 
	// with "branches" sites per branch and branching if "branching is true and clustering otherwise 
	protected GlobalConstantSet(int nbWave, int nbCores, int branches, int dual) {
		this.nbWavelengths = nbWave;
		this.numberOfCores = nbCores;
		this.sitesPerBranch = branches;
		this.wavelengthsFixed = true;
		this.sitesFixed = true;
		this.dual = dual;
	}
	
	
	public Map<String, String> getAllParameters() {
		return SimpleMap.getMap("Number of sites", numberOfCores+"",
								"Number of wavelength channels", nbWavelengths+"",
								"Number of sites per branch", sitesPerBranch+"",
								"Channel spacing", getChannelSpacing() +"",
								"Neff", getNeff()+"",
								"alpha", getAlpha()+"",
								"lambda0", getLambdaO()+"");
	}
	
	public int getDual() {
	return dual;
	}
	
	public int getNumberOfCores() {
		return numberOfCores;
	}
	
	public double getLightSpeed() {
		return 3e8; // m/s
	}
	
	public double getNeff() {
		return 2.52;
	}
	
	public Rate getRate() {
		return Rate.ONE_GBIT_S.multiply(10);
	}
	
	public double getAlpha() {
		return 4;
	}
	
	public double getRingDopingPaddingMicrometers() {
		return 50;
	}
	
	public double getLambdaO() {
		return 1550e-9;
	}
	
	public double getThermalStabConsumptionW() {
		return 0.0001;
	}
 	
	public int getNumberOfWavelengths() {
		return nbWavelengths;
	}
	
	public int getSitesPerBranch() {
		return sitesPerBranch;
	}
	
	public void setNumberOfWavelengths(int w) {
		nbWavelengths = w;
	}
	
	public void setNumberOfSites(int sites) {
		this.numberOfCores = sites;
	}
	
	public boolean isWavelengthFixed() {
		return wavelengthsFixed;
	}
	
	public boolean isSiteFixed() {
		return sitesFixed;
	}
	// returns the channel spacing in Hz
	public double getChannelSpacing() {
		return 50e-9 *getLightSpeed()/ (nbWavelengths*Math.pow(getLambdaO(),2)); 
		// 50e-9 m is the available spectrum largest FSR obtainabale for a reasonable Q ?
	}

	public double getPerBitPowerEfficiencySERDES() {
		return 1e-12;
	}
	//Return H - the tuning power in mW per FSR   corresponds per 3.2 mW per/GHz
	public double getThermalPowerperFSR() {
		return 20;
	}
	//return the number of Gb/s per channel . In a future version this could be a variable as it is related to the power
	public double getGbPerSecondPerChannel()
	{
		return 10;
	}
	//return the multimode laser efficiency in %
	public double getMMlaserEfficiency(){
		return 0.1;
	}
	//return the temperature range for the modulater and switch stabilization
	public double getDeltaT(){
		return 40;
	}
	//return the modulator driver power efficiency in pJ/bit  0.135 pJ/bit and 0.01 pJ/bit
	public double getModDriverPower(){
	return 0.145;
	}
	
	public double getModDriverPower_mW(){
		return 1.35+0.1;
		}
	//return the photodetector and receiver circuitry power efficiency in pJ/bit
		public double getPDandReceiverCircuitry(){
		return 0.4;
		}
		public double getPDandReceiverCircuitry_mW(){
			return 3.95;
			}
		
		
}
