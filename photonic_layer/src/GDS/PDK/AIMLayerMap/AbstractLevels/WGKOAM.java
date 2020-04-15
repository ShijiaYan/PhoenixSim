package GDS.PDK.AIMLayerMap.AbstractLevels;

import GDS.PDK.AIMLayerMap.AIMLayerMap;

public class WGKOAM extends AIMLayerMap {

	@Override
	public String getLayerName() {
		return "WGKOAM" ;
	}

	@Override
	public int getLayerNumber() {
		return 802 ;
	}

	@Override
	public int getDataType() {
		return 727 ;
	}

}
