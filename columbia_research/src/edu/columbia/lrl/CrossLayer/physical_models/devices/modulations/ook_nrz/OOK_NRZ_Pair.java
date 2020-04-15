//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package edu.columbia.lrl.CrossLayer.physical_models.devices.modulations.ook_nrz;

import edu.columbia.lrl.CrossLayer.physical_models.devices.modulations.AbstractModDemodPair;


public class OOK_NRZ_Pair extends AbstractModDemodPair {

	public OOK_NRZ_Pair(Abstract_OOK_NRZ_Modulator ookMod, Abstract_OOK_NRZ_Receiver ookReceiver) {
		super(ookMod, ookReceiver);
	}

	public String getModulationFormat() {
		return "OOK-NRZ";
	}
}
