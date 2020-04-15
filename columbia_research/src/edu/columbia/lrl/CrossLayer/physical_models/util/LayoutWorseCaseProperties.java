package edu.columbia.lrl.CrossLayer.physical_models.util;

import java.util.ArrayList;

import edu.columbia.lrl.CrossLayer.physical_models.PowerPenalty;

public class LayoutWorseCaseProperties {

	public ArrayList<PowerPenalty> ppList;
	public double propagationLatencyNS;
	public int wavelengths;	
	
	public LayoutWorseCaseProperties(int wavelengths) {
		this.wavelengths = wavelengths;
		this.ppList = new ArrayList<PowerPenalty>(1);
	}
	
	public LayoutWorseCaseProperties(LayoutWorseCaseProperties... multiProperties) {
		if (multiProperties.length < 2) throw new IllegalStateException("Should aggregate at least 2 layouts");
		this.ppList = new ArrayList<PowerPenalty>(1);
		int wavelength0 = multiProperties[0].wavelengths;
		for (int i = 1 ; i < multiProperties.length ; i++) {
			if (multiProperties[i].wavelengths != wavelength0) {
				throw new IllegalStateException("Inconsistent number of wavelengths");
			}
		}
		for (LayoutWorseCaseProperties lp : multiProperties) {
			if (lp != null) {
				this.ppList.addAll(lp.ppList);
				this.propagationLatencyNS += lp.propagationLatencyNS;
			}
		}
	}

	public void addPowerPenalty(PowerPenalty pp) {
		ppList.add(pp);
	}

	public void setLinkLatency(double linkPropLatencyNS) {
		propagationLatencyNS = linkPropLatencyNS;	
	}

	public double getTotalPowerPenalty() {
		double powerPenalty = 0;
		for (PowerPenalty pp : ppList) {
			powerPenalty += pp.getTotalPowerPenalty();
		}
		return powerPenalty;
	}

	public void addPowerPenalties(ArrayList<PowerPenalty> powerPenalties) {
		ppList.addAll(powerPenalties);
	}

	public ArrayList<PowerPenalty> getPowerPenalties() {
		return new ArrayList<PowerPenalty>(ppList);
	}
	
}