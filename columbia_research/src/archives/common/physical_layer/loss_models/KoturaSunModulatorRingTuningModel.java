package archives.common.physical_layer.loss_models;


public class KoturaSunModulatorRingTuningModel extends AbstractModulatorRingTuningModel {
	
	public double getModulatorQwithCarriers(double modQ) {
		return 0.35/0.75*modQ;
	}
	public double getModulatorShiftVS() {
		return 5.3e9;
	}
}
