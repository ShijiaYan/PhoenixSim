package archives.common.physical_layer.die;

import java.util.Map;

import ch.epfl.general_libraries.utils.SimpleMap;

public class NoCsInterfaceWaveguideModel extends AbstractDieModel {

	@Override
	public double getWaveguideLengthInCm() {
		//Estimated ring device width (incl. doping) from Cornell's "Ultra high bandwidth WDM using silicon microring modulators"
		double widthPerRing_um = gloco.getRingDopingPaddingMicrometers(); //um
		double widthPerBank_um = widthPerRing_um * gloco.getNumberOfWavelengths();
		double widthPerChip_um = widthPerBank_um * gloco.getNumberOfCores();
		
		return (widthPerChip_um/10000) * 2; //convert to cm and multiply by 2 (Tx and Rx)
	}

	@Override
	public Map<String, String> getAllParameters() {
		Map<String, String> map = new SimpleMap<>();
		return map;
	}
	


}
