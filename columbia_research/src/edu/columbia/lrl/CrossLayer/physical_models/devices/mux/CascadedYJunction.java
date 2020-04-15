package edu.columbia.lrl.CrossLayer.physical_models.devices.mux;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.math.MoreMaths;
import ch.epfl.general_libraries.utils.MoreArrays;
import ch.epfl.general_libraries.utils.SimpleMap;
import edu.columbia.lrl.CrossLayer.PowerConsumption;
import edu.columbia.lrl.CrossLayer.physical_models.PhysicalParameterAndModelsSet;
import edu.columbia.lrl.CrossLayer.physical_models.PowerPenalty;
import edu.columbia.lrl.CrossLayer.physical_models.util.AbstractLinkFormat;

public class CascadedYJunction extends AbstractMux {
	
	/* The 0.28 dB value is based on the paper
	 * A compact and low loss Y-junction for submicron silicon waveguide
	 */
	
	private double loss;
	
	public CascadedYJunction(
			@ParamName(name = "Y junction loss (dB)", default_ = "0.28") double loss) {
		this.loss = loss;
	}
	
	public CascadedYJunction() {
		this.loss = -1;
	}	

	@Override
	public ArrayList<PowerPenalty> getMuxPowerPenalties(
			PhysicalParameterAndModelsSet modelSet,
			AbstractLinkFormat linkFormat) {
		if (loss < 0) {
			loss = modelSet.getJunctionLoss();
		}
		
		int stages = (int)Math.ceil(MoreMaths.log2(linkFormat.getNumberOfChannels()));
		PowerPenalty lo = new PowerPenalty("cascaded y junction", MUX, stages * loss) ;
		
		return MoreArrays.getArrayList(lo) ;
	}

	@Override
	public Map<String, String> getAllParameters() {
		Map<String, String> map = new SimpleMap<String, String>();
		if (loss >= 0) {
			map.put("Y junction loss (dB)", loss + "");
		}
		return map;
	}

	@Override
	public List<PowerConsumption> getDevicePowerConsumptions(
			PhysicalParameterAndModelsSet modelSet,
			AbstractLinkFormat linkFormat) {
		ArrayList<PowerConsumption> pc = new ArrayList<PowerConsumption>(0);
		return pc;
	}

	@Override
	public double getMuxTotalPenalty() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMuxChannel() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getMuxChannelPenalty() {
		// TODO Auto-generated method stub
		return 0;
	}

}
