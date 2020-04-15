package archives.common.physical_layer.general;


public class RingResonance {
	
	public static double[] ringResonance(double[] frequencyVector, double centerFrequency, double FSR, double Q) {
		double dv = centerFrequency/Q;
		
		double[] toret = new double[frequencyVector.length];
		double a = Math.pow(2*FSR/(Math.PI*dv), 2);
		for (int i = 0 ; i < frequencyVector.length ; i++) {
			double b = Math.sin(Math.PI*frequencyVector[i]/FSR);
			double c = b*b;
			double d = a*c;
			toret[i] = 1/ (1 + d);
		}
		return toret;
	}
	
	public static double ringResonance(double frequency, double centerFrequency, double FSR, double Q) {
		return ringResonance(new double[]{frequency}, centerFrequency, FSR, Q)[0];
	}	
	
}
