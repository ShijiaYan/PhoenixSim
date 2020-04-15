package edu.columbia.lrl.CrossLayer.simulator.phy_builders;

import ch.epfl.general_libraries.traffic.Rate;
import edu.columbia.lrl.CrossLayer.physical_models.layout.PhysicalLayout;
import edu.columbia.lrl.CrossLayer.physical_models.util.AbstractLinkFormat;

public interface PhyWrapper {

	public abstract int getNumberOfOpticalInterfacesPerClient();
	public abstract PhysicalLayout getPhysicalLayoutImpl(int clients);
	public abstract void potentiallyImposeFormat(int nbClients, AbstractLinkFormat linkFormat);
	public abstract double getTotalpowerMW();
	public abstract Rate getSrcToDestRate(AbstractLinkFormat linkFormat);
}
