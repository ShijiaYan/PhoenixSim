package archives.common.physical_layer.loss_models;

import java.util.Map;

import org.apache.commons.math.complex.Complex;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.MoreArrays;
import ch.epfl.general_libraries.utils.Pair;
import ch.epfl.general_libraries.utils.SimpleMap;
//import edu.columbia.lrl.Rob.results.ResultManager;

public class Balanced1x2CombSwitch extends AbstractSwitchingRingModel {
	
	double combSwitchNeff;
	
//	public ResultManager resultManager;
		
	public Balanced1x2CombSwitch(@ParamName(name="Comb switch Neff", default_="3") double combSwitchNeff) {
		this.combSwitchNeff = combSwitchNeff;
	}
	
	public Map<String, String> getAllParameters() {
		return SimpleMap.getMap("Comb switch Neff", combSwitchNeff+"");
	}

	public void getLossThroughILdropILandSwitchPP(PscanResult result, int numberDrop, int numberThrough) {
		
		//double ch_spacing = gloco.getChannelSpacing();
		double c0 = gloco.getLightSpeed();
		double neff = combSwitchNeff;
		double lambda0 = gloco.getLambdaO();
		
		double radius = c0/(2*Math.PI*neff*gloco.getChannelSpacing());
		double L = 2*Math.PI*radius;
		
		double fsr_wavelength = Math.pow(lambda0,2d)/(neff*L);
		double fsr_freq = (c0/ Math.pow(lambda0,2d)) *fsr_wavelength;
		
		double shift;
		if (fsr_freq < 500e9)
			shift = fsr_freq/2;
		else
			shift = 250e9;
			
		double ring_loss_dB = 2*Math.PI*radius*gloco.getAlpha()*100;
		double alpha1 = Math.sqrt(Math.pow(10, -ring_loss_dB/10));		// <------ check alpha1 = alpha ?
		double half_alpha = Math.sqrt(Math.pow(10, -(ring_loss_dB/2)/10)); // <--- alpha almost equal to half-alpha ?
		
		double delta_alpha = getLossFromInjection(shift);
		double alpha_with_carriers = alpha1*Math.exp(-delta_alpha*L);
		double half_alpha_with_carriers = half_alpha*Math.exp(-delta_alpha*L);
		
		int numSamples = 200;
		
		double[] t_vec = new double[numSamples];
		for (int i = 0 ; i < t_vec.length ; i++) {
			t_vec[i] = 0.5 + i*(.5/(double)numSamples);
		}
		
		double[] total_loss_vec = new double[numSamples];
		double[] through_IL_vec = new double[numSamples];
		double[] drop_IL_vec = new double[numSamples];
		double[] loss_difference_vec = new double[numSamples];
		
		double[] phase_vec = new double[629];
		for (int i = 0 ; i < phase_vec.length ; i++) {
			phase_vec[i] = i*0.005;
		}
		int iterations = t_vec.length;
		
		/*MultiXYResult losses = new MultiXYResult("Filter switch losses");
		XYResult drop = new XYResult("t", "drop loss");
		XYResult through = new XYResult("t", "through loss");
		XYResult diff = new XYResult("t", "difference");
		losses.addResult("Drop loss", drop);
		losses.addResult("Through loss", through);
		losses.addResult("Difference", diff);
		resultManager.add(losses);*/
		
		for (int i = 0 ; i < iterations ; i++) {
			double through_power = getThroughIL(t_vec[i], alpha1, phase_vec[0], shift, 1);
			through_IL_vec[i] = -10*Math.log10(through_power);
			
			double drop_power = getDropIL(t_vec[i], alpha_with_carriers, half_alpha_with_carriers, phase_vec[0]); 
			drop_IL_vec[i] = -10*Math.log10(drop_power);
			
			double[] drop_port_response = new double[phase_vec.length];
			for (int j = 0 ; j < phase_vec.length ; j++) {
				drop_power = getDropIL(t_vec[i], alpha_with_carriers, half_alpha_with_carriers, phase_vec[j]);
				drop_port_response[j] = drop_power;
			}
			
			double max_drop = MoreArrays.max(drop_port_response);
			double[] normalized_drop_port = MoreArrays.product(drop_port_response, 1/max_drop);
			
			int half_ind = 0;
			double half_val = 0;
			
			for (int j = 0 ; j < normalized_drop_port.length ; j++) {
				if (normalized_drop_port[j] < .5) {
					half_ind = j;
					half_val = normalized_drop_port[j];
					break;
				}
			}
			
			double distance_minus = Math.abs(0.5-half_val);
			double distance_plus = Math.abs(0.5-normalized_drop_port[half_ind-1]);
			if (distance_plus < distance_minus) {
				half_ind = half_ind-1;
			}
		
			total_loss_vec[i] = numberDrop* drop_IL_vec[i] + numberThrough* through_IL_vec[i];
			loss_difference_vec[i] = Math.abs(through_IL_vec[i] - drop_IL_vec[i]);
		}
		
		
		//Pair<Double, Integer> min_indexP = MoreArrays.minAndIndex(total_loss_vec);
		Pair<Double, Integer> min_indexP = MoreArrays.minAndIndex(loss_difference_vec);
		int min_index = min_indexP.getSecond();
		//	double min = min_indexP.getFirst();
		
		//drop.add(t_vec, drop_IL_vec);
		//through.add(t_vec, through_IL_vec);
		//diff.add(t_vec, loss_difference_vec);
		
		result.addSpecificData(drop_IL_vec, t_vec, "switch Drop IL", "tau?");
		result.addSpecificData(through_IL_vec, t_vec, "switch Through IL", "tau?");
		result.addSpecificData(total_loss_vec, t_vec, "switch total", "tau?");
		
		//min_index can be -1 if all loss is infinity
		if( min_index >= 0 ) {
			result.addOptimizedParameter(t_vec[min_index], "Tau for comb switches ?");
		
			result.addPowerDissipatedDB(numberThrough*through_IL_vec[min_index], "Switch", "through insertion loss");
			result.addPowerDissipatedDB(numberDrop*drop_IL_vec[min_index], "Switch", "drop insertion loss");
		}
	}
	
	private double getLossFromInjection(double resonanceShift) {
		double v0 = gloco.getLightSpeed()/gloco.getLambdaO();
		double dn_eff = -combSwitchNeff/v0*resonanceShift;
		
		double[] x = new double[10000];
		double[] y = new double[x.length];
		for (int i = 0 ; i < x.length ; i++) {
			x[i] = (i+1)*1e15;
			y[i] = dn_eff + 8.8e-22*x[i] + 8.5e-18*Math.pow(x[i], 0.8); // <----------- ?!?!?!   power 0.8 ??
		}
		
		int greater_inds = -1;
		int smaller_inds = Integer.MAX_VALUE;
		for (int i = 0 ; i < x.length ; i++) {		
			if (y[i] > 0 && greater_inds == -1) {
				greater_inds = i;
			}
			if (y[i] < 0) {
				smaller_inds = i;
			}
		}
		
		double[] x_refined = new double[1000];
		double diff = (x[greater_inds] - x[smaller_inds]) / (double) x_refined.length;

		for (int i = 0 ; i < x_refined.length ; i++) {
			x_refined[i] = x[smaller_inds] + i*diff;
		}
		double[] y_refined = new double[x_refined.length];
		for (int i = 0 ; i < y_refined.length ; i++) {
			y_refined[i] = dn_eff + 8.8e-22*x_refined[i] + 8.5e-18*Math.pow(x_refined[i], 0.8);
		}
		int biggerIn = -1;
		for (int i = 0 ; i < y_refined.length ; i++) {
			if (y_refined[i] < 0) {
				biggerIn = i;
			}
		}
		
		
		double dN = x_refined[biggerIn];
		
		return 8.5e-18*dN + 6e-18*dN;
	}

	private double getThroughIL(double t, double alpha, double phase, double shift, int do_shift) {
		Complex one = new Complex(1, 0);		
		Complex alphaC = new Complex(alpha, 0);
		Complex tC = new Complex(t, 0);
		Complex t2C = new Complex(t*t, 0);
		
		Complex et1;
		if (do_shift == 1) {
			double theta = (shift/gloco.getChannelSpacing()) * 2 * Math.PI + phase;


			Complex thetaI = new Complex(0, theta);
			Complex expThetaI = thetaI.exp();			
			Complex alphaExpThetaI = expThetaI.multiply(alphaC);			
			et1 = one.subtract(alphaExpThetaI).multiply(tC).divide(
				one.subtract(t2C.multiply(alphaExpThetaI))
			);
		} else {
			Complex phaseI = new Complex(0, phase);
			Complex expPhaseI = phaseI.exp();
			Complex alphaExpPhaseI = expPhaseI.multiply(alphaC);
			et1 = one.subtract(alphaExpPhaseI).multiply(tC).divide(
				one.subtract(t2C.multiply(alphaExpPhaseI))
			);
		}
		return Math.pow(et1.abs(), 2);
	}
	
	private double getDropIL(double t, double alpha, double half_alpha, double phase) {
		
		Complex one = new Complex(1, 0);

		double minusK2 = -(1-Math.pow(t,2));
		Complex a = new Complex(minusK2*half_alpha, 0);
		
		Complex b = new Complex(Math.pow(t,2)*alpha, 0);

		Complex phaseI = new Complex(0, phase);
		Complex expPhaseI = phaseI.exp();
		
		Complex et2 = a.multiply(expPhaseI).divide(
			one.subtract(b.multiply(expPhaseI))
		);
		return Math.pow(et2.abs(),2);
	}

}
