package archives.common.physical_layer.loss_models;

import java.util.Map;

import archives.common.physical_layer.GlobalConstantSet;
import archives.common.physical_layer.general.RingResonance;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.MoreArrays;
import ch.epfl.general_libraries.utils.Pair;
import ch.epfl.general_libraries.utils.SimpleMap;

public class DefaultModDemodulatorLossModel extends AbstractLossModel {
	
	private double modulatorRingRadius;
	private double modulatorQ;
//	private double v_s;
	private double ILdropThroughRing;
	private AbstractModulatorRingTuningModel modRingModel;
	private AbstractExtinctionRatioModel erModel;
	private AbstractTruncationPPModel trunPP;

//	private ResultManager resultManager;
	
	final private static double PI2 = Math.PI * 2;	
		
	public static void main(String[] args) {
		
		GlobalConstantSet set = new GlobalConstantSet.AllFixedConstantSet(6,  //wavelength
													  4  //core
													  );
		
		DefaultModDemodulatorLossModel mod = new DefaultModDemodulatorLossModel(1.87e-6, 12000,
			new KoturaSunModulatorRingTuningModel(),
			new DefaultExtinctionRatioModel(20, 4),
			new LabTruncationPPModel(),
			0.5);
		mod.init(set);
		
		PscanResult result = new PscanResult(set.getNumberOfWavelengths());
		
		mod.getModulatorLossAndExtraArrayLoss(set.getSitesPerBranch(), result);
		mod.getDemultiplexingLossAndPenalty(result);

	}
	
	public DefaultModDemodulatorLossModel() {
		this(1.87e-6, 12000, new KoturaSunModulatorRingTuningModel(),
			new DefaultExtinctionRatioModel(20, 4),
			new LabTruncationPPModel(), 0.5);
	}
	
	public Map<String, String> getAllParameters() {
		Map<String, String> map = SimpleMap.getMap("Modulator ring radius", modulatorRingRadius+"",
								"Modulator Q", modulatorQ+"",
								"Ring tuning model", modRingModel.getClass().getSimpleName(),
								"Modulator truncation model", trunPP.getClass().getSimpleName(),
								"Insertion loss through ring dB", ILdropThroughRing+"");
								
		map.putAll(erModel.getAllParameters());
		return map;
	}	
	
	public DefaultModDemodulatorLossModel(@ParamName(name="Modulator ring radius", default_="1.87e-6") double modulatorRingRadius, 
										  @ParamName(name="Modulator Q", default_="12000") double modulatorQ, 
										  @ParamName(name="Ring tuning model") AbstractModulatorRingTuningModel modRingModel,
										  @ParamName(name="Extinction ratio model") AbstractExtinctionRatioModel erModel,
										  @ParamName(name="Truncation model") AbstractTruncationPPModel trunPP,
										  @ParamName(name="Insertion loss through ring", default_="0.5") double ILdropThroughRing /* 0.5 */) {
		this.modulatorRingRadius = modulatorRingRadius;
		this.modulatorQ = modulatorQ;
		this.modRingModel = modRingModel;
		this.erModel = erModel;
		this.trunPP = trunPP;
		this.ILdropThroughRing = ILdropThroughRing;
	}
	

	double c0;
	double v0;
	double FSRvf;
	
	public void init(GlobalConstantSet gloco) {
		super.init(gloco);
		
		c0 = gloco.getLightSpeed();
		v0 = c0/gloco.getLambdaO();
		FSRvf = c0 / (PI2*modulatorRingRadius*gloco.getNeff());
	}
	
	public void getModulatorLossAndExtraArrayLoss(int sitesPerBranch, PscanResult results) {
		double linewidthQ = v0/modulatorQ;
		double mod_Q_with_carriers = modRingModel.getModulatorQwithCarriers(modulatorQ);
		
		double[] v = getAuxiliaryVector(FSRvf, linewidthQ, v0);
		double[] I1 = RingResonance.ringResonance(v, v0, FSRvf, modulatorQ);
		int max = Integer.MAX_VALUE;
		for (int i = I1.length -1 ; i >= 0 ; i--) {
			if (I1[i] >= 0.5) {			// > 0.5 ? other values possible ?
				max = i;
				break;
			}
		}
		double v_laser = v[max];
		
		results.addComputedParameter(v_laser, "v_laser");
		
		double signal_mul_IL = 1;
		
		double avg_loss = 0;
		
		int ext = (int)Math.ceil(gloco.getNumberOfWavelengths()/2d);
		
		double[] losses = new double[2*ext];
		double[] accum = new double[2*ext];
		double[] ringRes1 = new double[2*ext];
		double[] ringRes2 = new double[2*ext];
		double[] indexes = new double[2*ext];
		
		//XYResult ringRes1Result = new XYResult("Wavelength", "R1 Transmission");
		//XYResult ringRes2Result = new XYResult("Wavelength", "R2 Transmission");
		//gloco.getResultManager().add(ringRes1Result);
		//gloco.getResultManager().add(ringRes2Result);
		
		for (int i = -ext ; i < ext ; i++) {
			indexes[i+ext] = i;
			if (i != 0) {
				ringRes1[i+ext] = RingResonance.ringResonance(v_laser + i*gloco.getChannelSpacing(), v0, FSRvf, modulatorQ);
				ringRes2[i+ext] = RingResonance.ringResonance(v_laser + i*gloco.getChannelSpacing() + modRingModel.getModulatorShiftVS(),
														v0, FSRvf, mod_Q_with_carriers);
				
				//ringRes1Result.add(i+ext, ringRes1[i+ext]);
				//ringRes2Result.add(i+ext, ringRes2[i+ext]);
				
				avg_loss = (ringRes1[i+ext]+ringRes2[i+ext])/2;
				losses[i+ext] = (1-avg_loss);
				signal_mul_IL *= (1-avg_loss);
				accum[i+ext] = signal_mul_IL;
				//System.out.println(l1 + " \t " + l2 + "\t" + signal_mul_IL);
			} else {
				losses[i+ext] = 1;
				accum[i+ext] = signal_mul_IL;
			}
		}
		
		double extra_mod_array_IL = -10*Math.log10(signal_mul_IL)*sitesPerBranch;
		
		results.addComputedParameter(extra_mod_array_IL, "losses inflicted by other channel during modulation - per site");
		
		results.addPowerDissipatedDB(extra_mod_array_IL, "Mod","losses inflicted by other channels during modulation");
		results.addSpecificData(losses, indexes, "mod losses per other channel","channel id");
		results.addSpecificData(ringRes1, indexes, "mod resonance 1","channel id");
		results.addSpecificData(ringRes2, indexes, "mod resonance 2","channel id");
		results.addSpecificData(accum, indexes, "mod accumulated loss among the channels","channel id");	
		
		
	//	SimpleResult modLoss = new SimpleResult("Modulator losses from other channels");
	//	modLoss.setValue(extra_mod_array_IL);
		//gloco.getResultManager().add(modLoss);
		
	//	XYResult otherChannels = new XYResult("Channel ID", "Other channel losses");
	//	XYResult modResonance1 = new XYResult("Channel ID", "Mod resonance 1");
	//	XYResult modResonance2 = new XYResult("Channel ID", "Mod resonance 2");
	//	XYResult modAccum = new XYResult("Channel ID", "Accumulated loss");
		
	//	otherChannels.add(indexes, losses);
	//	modResonance1.add(indexes, ringRes1);
	//	modResonance2.add(indexes, ringRes2);
	//	modAccum.add(indexes, accum);
		
		//gloco.getResultManager().add(otherChannels);
		//gloco.getResultManager().add(modResonance1);
		//gloco.getResultManager().add(modResonance2);
		//gloco.getResultManager().add(modAccum);
	//	resultManager.add(modLoss);
	//	resultManager.add(otherChannels);		
		
	//	((DefaultExtinctionRatioModel)(erModel)).resultManager = resultManager;
		erModel.getExtinctionRatioPowerPenalty(sitesPerBranch,gloco, results);
		
	//	SimpleResult eppResult = new SimpleResult("ExtinctionRatioPowerPenalty");
	//	resultManager.add(eppResult);
	//	eppResult.setValue(epp);
			
	}
	
	public void getDemultiplexingLossAndPenalty(PscanResult result) {
		double Q_min = v0/gloco.getRate().getInBitsSeconds()/3;		// 3 ?
		double Q_max = v0/gloco.getRate().getInBitsSeconds()/0.7;	// 0.7 ?
		double dQ = (Q_max - Q_min)/99;
		int size = (int)Math.ceil((Q_max - Q_min)/dQ);
		double[] Q_vec = new double[size];
		for (int i = 0 ; i < Q_vec.length ; i++) {
			Q_vec[i] = Q_min + i*dQ;
		}
		double[] xtalk_pp1 = new double[size];
		double[] trunc_pp1 = new double[size];
		double[] sum_pp = new double[size];
		
		for (int i = 0 ; i < Q_vec.length ; i++) {
			double Q_demux = Q_vec[i];
			double dv = v0/Q_demux;
			double[] v = getAuxiliaryVector(FSRvf, dv, v0);
			double[] I2 = RingResonance.ringResonance(v, v0, FSRvf, Q_demux);
			Pair<Double,Integer> p = MoreArrays.maxAndIndex(I2);
		//	double aux = p.getFirst();
			double center_v = v[p.getSecond()];
			
			double add_xtalk1 = 0;
			for (int j = (int)-Math.ceil(gloco.getNumberOfWavelengths()/2) ; j <= (int)Math.ceil(gloco.getNumberOfWavelengths()/2) ; j++) {
				if (j != 0) {
					double p1 = RingResonance.ringResonance(center_v + (j*gloco.getChannelSpacing()), v0, FSRvf, Q_demux);
	//				System.out.println(p1);
					add_xtalk1 += p1;
				}
			}
			
			xtalk_pp1[i] = -10*Math.log10(1-Math.sqrt(add_xtalk1));
			trunc_pp1[i] = trunPP.getTruncationPowerPenalty(dv/gloco.getRate().getInBitsSeconds());
			sum_pp[i] = xtalk_pp1[i] + trunc_pp1[i];
		}
		
		result.addSpecificData(xtalk_pp1, Q_vec, "demux Crosstalk PP", "Q_vec");
		result.addSpecificData(trunc_pp1, Q_vec, "demux Trunc PP", "Q_vec");
		result.addSpecificData(sum_pp, Q_vec, "demux Total PP", "Q_vec");		
		
		int optIndex = 0;
		double min = Double.MAX_VALUE;
		for (int i = 0 ; i < sum_pp.length ; i++) {
			if (sum_pp[i] < min) {
				min = sum_pp[i];
				optIndex = i;
			}
		}
		
		result.addPowerDissipatedDB(min, "Demux","crosstalk from other channels");
		result.addPowerDissipatedDB(ILdropThroughRing, "Demux","IL drop through ring");
		
		result.addOptimizedParameter(Q_vec[optIndex], "Optimal Q for demux");	
	}
	
	private double[] getAuxiliaryVector(double vf, double dv, double v0) {
		// creation of the auxiliary vector
		int indexes = (int)(Math.ceil(vf + 40*dv)/(dv/10));	//   divided by 10 ? granularity ?
		double[] v = new double[indexes];
		for (int i = 0 ; i < indexes ; i++) {
			v[i] = v0 + (i*dv/10);
		}
		return v;
	}
	
	
	
}
