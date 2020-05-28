package edu.columbia.lrl.CrossLayer.physical_models.util;

public class RingResonance {

	double[] resonance;
	double[] frequencies;
	
	public RingResonance(double centerFrequency, double fsr, double q) {
		
		double dv = centerFrequency/q;  
	
		
		// Generate vector of samples in frequency to use to generate resonance
		int indexes = (int)(Math.ceil(fsr + 160*dv)/(dv/10));
		
		resonance = new double[indexes];
		frequencies = new double[indexes];		
		
		for (int i = 0 ; i < indexes ; i++) {
			frequencies[i] = centerFrequency + i*dv/10;
			resonance[i] = getResonance(centerFrequency, fsr, q, frequencies[i]);
		}
		
		// Generate ring resonance
	//	double a = Math.pow(2*fsr/(Math.PI*dv), 2);
	//	for (int i = 0 ; i < indexes ; i++) {
	//		double b = Math.sin(Math.PI*frequencies[i]/fsr);
		//	double c = b*b;
		//	double d = a*c;
		//	resonance[i] = 1/ (1 + d);
	//	
	}
	
	public static double getResonance(double centerFrequency, double fsr, double q, double altFreq) {
		double dv = centerFrequency/q;
		
		double ratio = altFreq/fsr;
		
		double a = Math.pow(2*fsr/(Math.PI*dv), 2);
		double b = Math.sin(Math.PI*ratio);
		double c = b*b;
		double d = a*c;
		return 1/ (1 + d);	
	}
	
/*	public double getResonanceAt(double frequency) {
		// Find transmission at closest value to frequency
		double percent = (frequency - frequencies[0])/(frequencies[frequencies.length-1] - frequencies[0]);
		if( percent > 1.0 ) percent = 1.0;
		int approxIndex = (int)Math.floor(percent * (frequencies.length-1));
		return resonance[approxIndex];
	}	*/
	
	public double at(int index) {
		return resonance[index];
	}
	
	public double getFrequencyAtIndex(int index) {
		return frequencies[index];
	}
	
	public int getNumSamples() {
		return resonance.length;
	}
	
	public double[] getResonance() {
		return resonance;
	}
	public double[] getFrequencies() {
		return frequencies;
	}
	public double[] getFrequenciesGHz() {
		double[] frequenciesGHz = new double[frequencies.length];
		int index = 0;
		for( double d : frequencies ) {
			frequenciesGHz[index++] = d/1e9;
		}
		return frequenciesGHz;
	}
}
