package edu.columbia.sebastien.link_util.models.linkdimmodel;

import java.util.Arrays;
import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;
import edu.columbia.sebastien.link_util.LinkUtilisationExperiment;
import edu.columbia.sebastien.link_util.models.AbstractLinkDimensioningModel;
import edu.columbia.sebastien.link_util.models.AbstractSiPLinkConsumptionModel;

public class ConstantLinkDimensioning extends AbstractLinkDimensioningModel {
	
	private int lanesPerLink;
	private AbstractSiPLinkConsumptionModel linkConMod;
	
	private LinkUtilisationExperiment lue;
	
	public ConstantLinkDimensioning(@ParamName(name="Lanes per link", default_="10")  int lanesPerLink, AbstractSiPLinkConsumptionModel linkConMod) {
		this.lanesPerLink = lanesPerLink;
		this.linkConMod = linkConMod;
	}

	@Override
	public Map<String, String> getAllParameters() {
		return SimpleMap.getMap("lanes per link", lanesPerLink+"");
	}

	@Override
	public void build(LinkUtilisationExperiment lue) {
		this.lue = lue;
	}

	@Override
	public double getTransmissionTimeNS(int sizeInBits, int linkId) {
		return (double)sizeInBits / (10*lanesPerLink);
	}

	@Override
	public double getJoules(double transmissionTimeNS, int linkId) {
		return linkConMod.getJoules(transmissionTimeNS, getLanes(linkId));
	}

	@Override
	public int getLanes(int linkId) {
		return lanesPerLink;
	}

	@Override
	public int[] getLanes() {
		int nbLinks = lue.getNbLinks();
		int[] lanes = new int[nbLinks];
		Arrays.fill(lanes, lanesPerLink);
		return lanes;
	}

	@Override
	public double getNumberOfLanes() {
		return lanesPerLink;
	}

}
