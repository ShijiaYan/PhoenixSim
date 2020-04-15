package archives.common.physical_layer;

import java.util.Map;

public abstract class AbstractPhyModel {

	protected GlobalConstantSet gloco;
	
	public void init(GlobalConstantSet gloco) {
		this.gloco = gloco;
	}
	
	public abstract Map<String, String> getAllParameters();
}
