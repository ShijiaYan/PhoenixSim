package edu.columbia.lrl.CrossLayer.physical_models.devices.demux.ring_array;

import java.util.ArrayList;
import java.util.Map;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.results.Execution;
import ch.epfl.general_libraries.utils.MoreArrays;
import edu.columbia.lrl.CrossLayer.physical_models.PhysicalParameterAndModelsSet;
import edu.columbia.lrl.CrossLayer.physical_models.PowerPenalty;
import edu.columbia.lrl.CrossLayer.physical_models.devices.demux.ring_array.truncation.Abstract_SincSquare_Lorentzian_TruncationModel;
import edu.columbia.lrl.CrossLayer.physical_models.devices.demux.ring_array.truncation.PolynomBased_SincSquare_Lorentzian_TruncationModel;
import edu.columbia.lrl.CrossLayer.physical_models.generic_models.xtalk.AbstractXtalkPPModel;
import edu.columbia.lrl.CrossLayer.physical_models.generic_models.xtalk.MeisamJLTXtalkModel;
import edu.columbia.lrl.CrossLayer.physical_models.util.AbstractLinkFormat;


public class FixedQFilterArrayModel extends FilterArrayModel {

	public double q;

	public FixedQFilterArrayModel(
			@ParamName(name = "Passive drop insertion loss (dB)", default_ = "0.5") double passiveDropIL,
			@ParamName(name = "Ring radius (m)", default_ = "1.87e-6") double ringRadius,
			@ParamName(name = "Effective Index", default_ = "4.393") double Neff, @ParamName(name = "Q") double q,
			@ParamName(name = "Track details", default_ = "false") boolean trackDetails,
			@ParamName(name = "Xtalk model", defaultClass_ = MeisamJLTXtalkModel.class) AbstractXtalkPPModel xtalkModel,
			@ParamName(name = "Truncation model to use", defaultClass_ = PolynomBased_SincSquare_Lorentzian_TruncationModel.class) Abstract_SincSquare_Lorentzian_TruncationModel truncModel) {
		super(passiveDropIL, ringRadius, Neff, truncModel, xtalkModel, trackDetails);
		this.q = q;
	}

	public Map<String, String> getAllParameters() {
		Map<String, String> map = super.getAllParameters();
		map.put("Detector Q", String.valueOf(this.q));
		return map;
	}

	public ArrayList<PowerPenalty> getPowerPenalties(PhysicalParameterAndModelsSet modelSet,
			AbstractLinkFormat linkFormat, Execution ex, double modulationER) {
		double[] results = this.calculateForQ(modelSet, linkFormat, this.q, modulationER);
		double xtalk_pp = results[0];
		double trunc_pp = results[1];
		PowerPenalty trunc = new PowerPenalty("Truncation", "Demux", trunc_pp);
		PowerPenalty xtalk = new PowerPenalty("Crosstalk", "Demux", xtalk_pp);
		PowerPenalty ilpp = new PowerPenalty("Insertion loss", "Demux", this.passiveDropIL);
		return MoreArrays.getArrayList(trunc, xtalk, ilpp);
	}
}
