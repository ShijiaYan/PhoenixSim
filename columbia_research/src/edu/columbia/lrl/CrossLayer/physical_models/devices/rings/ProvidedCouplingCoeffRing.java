//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package edu.columbia.lrl.CrossLayer.physical_models.devices.rings;

public class ProvidedCouplingCoeffRing extends RingResonatorModel {

	public ProvidedCouplingCoeffRing(double radiusMicron, double inputGapNm, double outputGapNm, double detuningNm,
			AbstractBendingLossModel bendLossMod) {
		super(radiusMicron, inputGapNm, outputGapNm, detuningNm, bendLossMod);
	}

	public double getInputGapNm() {
		throw new IllegalStateException();
	}

	public double getOutputGapNm() {
		throw new IllegalStateException();
	}

	public double getInputTcoeff() {
		throw new IllegalStateException();
	}

	public double getOutputTcoeff() {
		throw new IllegalStateException();
	}

	public double getInputKappacoeff() {
		throw new IllegalStateException();
	}

	public double getOutputKappacoeff() {
		throw new IllegalStateException();
	}
}
