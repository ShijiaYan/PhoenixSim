package archives.common.physical_layer.loss_models;


public class LabTruncationPPModel extends AbstractTruncationPPModel {
	
	public static void main(String[] args) {
		System.out.println((new LabTruncationPPModel()).getTruncationPowerPenalty(2));
		
	}
	
//	double[] polynomParam = {0.0931, -1.34, 7.99, -25.1, 44.4, -42.3, 17.2};
	
	
	public double getTruncationPowerPenalty(double dvOVER) {
		return 17.228  
		-42.29*dvOVER
		+44.443*Math.pow(dvOVER,2)		
		-25.148*Math.pow(dvOVER,3)
		+7.9945*Math.pow(dvOVER,4)
		-1.3447*Math.pow(dvOVER,5)
		+0.093144*Math.pow(dvOVER,6);
	}
}
