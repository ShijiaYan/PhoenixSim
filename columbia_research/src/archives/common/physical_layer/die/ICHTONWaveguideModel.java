package archives.common.physical_layer.die;

import java.util.Map;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;

public class ICHTONWaveguideModel extends AbstractDieModel {

	//All these are in [cm]
	double crossingPadding;
	double switchPadding;
	
	public ICHTONWaveguideModel(
			@ParamName(name="Crossing padding [cm]") double crossingPadding,
			@ParamName(name="Switch padding [cm]") double switchPadding
			) {
		this.crossingPadding = crossingPadding;
		this.switchPadding = switchPadding;
	}
	
	@Override
	public double getWaveguideLengthInCm() {
		
		double radius = gloco.getLightSpeed()/(2*Math.PI*gloco.getNeff()*gloco.getChannelSpacing());
		radius += gloco.getRingDopingPaddingMicrometers()/10000; //convert to cm
		
		double numCrossings = 2*(gloco.getSwitchRadix() - Math.log(gloco.getSwitchRadix())/Math.log(2) - 1);
		
		//Waveguide length per chip = Nr(2*Dr + Pr) + NxPx
		double lengthPerChip = gloco.getNumStages()*(2*(radius*2 + switchPadding)) + numCrossings*crossingPadding;
		
		return lengthPerChip;// * info.getNumChips();
	}

	@Override
	public Map<String, String> getAllParameters() {
		
		Map<String, String> map = new SimpleMap<>();
		
		map.put("Crossing padding [cm]", crossingPadding+"");
		map.put("Switch padding [cm]", switchPadding+"");
		
		return map;
	}

}
