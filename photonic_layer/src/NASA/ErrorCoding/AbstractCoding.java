package NASA.ErrorCoding;

public abstract class AbstractCoding {

	public abstract double getUncodedOSNR(double BER) ;
	public abstract double getCodedOSNR(double BER) ;
	public abstract double getCodingGain(double BER) ;
	public abstract String getCodingName() ;
	
}
