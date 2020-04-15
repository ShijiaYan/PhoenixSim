package archives.common.physical_layer.loss_models;


public abstract class AbstractSwitchingRingModel extends AbstractLossModel {
	
	public abstract void getLossThroughILdropILandSwitchPP(PscanResult result, int numberDrop, int numberThrough);
	
}
