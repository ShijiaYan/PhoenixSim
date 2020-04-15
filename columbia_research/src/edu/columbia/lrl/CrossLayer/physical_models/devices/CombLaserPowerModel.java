package edu.columbia.lrl.CrossLayer.physical_models.devices;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;

public class CombLaserPowerModel extends AbstractLaserModel {
	
	private double maxEfficiency;
	private double powerMaxEffSingleLambda;
	private double powerMaxEff100Lambda;
	
	public CombLaserPowerModel(
			@ParamName(name="Max. efficiency") double maxEfficiency,
			@ParamName(name="Power at wich max eff. is reach with one lambda dBm") double powerMaxEffSingleLambda, 
			@ParamName(name="Power at which max eff. is reach with 100 lambda dBm") double powerMaxEff100Lambda) {
		
		this.powerMaxEffSingleLambda = powerMaxEffSingleLambda;
		this.powerMaxEff100Lambda = powerMaxEff100Lambda;
		this.maxEfficiency = maxEfficiency;
		// TODO Auto-generated constructor stub
	}

	@Override
	public double getLaserEfficiency(int nbChannels, double inputPowerRequiredPerLambdaMW) {
		double powerdB = Math.log10(inputPowerRequiredPerLambdaMW)*10;
		double slope = -99/(powerMaxEffSingleLambda - powerMaxEff100Lambda);
		double offset = 100 - slope*powerMaxEff100Lambda;
		
		double dist = Math.abs(slope*powerdB - nbChannels + offset);
		dist = dist/Math.sqrt(1 + slope*slope);
		
		return maxEfficiency/(Math.abs(dist)+1);
		
	}

	@Override
	public Map<? extends String, ? extends String> getAllParameters() {
		SimpleMap<String, String> m = new SimpleMap<String, String>();
		m.put("Combpowermodel_p1", powerMaxEff100Lambda+"");
		m.put("Combpowermodel_p2", powerMaxEffSingleLambda+"");
		m.put("Combpowermodel_Max. eff", maxEfficiency+"");
		return m;
	}

}
