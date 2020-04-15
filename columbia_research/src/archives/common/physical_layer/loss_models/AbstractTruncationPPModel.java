package archives.common.physical_layer.loss_models;


public abstract class AbstractTruncationPPModel {
	
	public abstract double getTruncationPowerPenalty(double dvOVERbitrate);
}
