package archives.common.physical_layer.loss_models;

import java.util.Map;

import archives.common.physical_layer.GlobalConstantSet;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.traffic.Rate;
import ch.epfl.general_libraries.utils.SimpleMap;

public class DefaultExtinctionRatioModel extends AbstractExtinctionRatioModel {
	
	private double referenceER;
	private double passiveIL;
	
	public DefaultExtinctionRatioModel(
		@ParamName(name="Reference extinction ratio", default_="20") double referenceER /*20*/, 
		@ParamName(name="Passive insertion loss", default_="1") double passiveIL/*4*/) {
		this.referenceER = referenceER;
		this.passiveIL = passiveIL;
	}
	
	public Map<String, String> getAllParameters() {
		return SimpleMap.getMap("Reference extinction ratio", referenceER+"",
								"Passive modulator insertion loss", passiveIL+"");
	}
	
	public void getExtinctionRatioPowerPenalty(int sitesPerBranch, GlobalConstantSet gloco, PscanResult result) {
		
		Rate rate = gloco.getRate();
		
		double ref_r = Math.pow(10, referenceER/10);
		double ref_er_pp = getFindAnameForThisMethodNoam(ref_r);
		double er_pp = getFindAnameForThisMethodNoam(getRateRelatedExtinctionRatio(rate)) - ref_er_pp;
		double ook_il = -10*Math.log10(0.5 + (0.5/getRateRelatedExtinctionRatio(rate)));
		
		result.addPowerDissipatedDB(er_pp, "Mod","Extinction ratio power penalty");
		result.addPowerDissipatedDB(ook_il, "Mod","OOK Insertion loss");
		result.addPowerDissipatedDB(passiveIL*sitesPerBranch, "Mod","passive modulation insertion loss");
		
		/*SimpleResult erPPResult = new SimpleResult("ModERPP");
		SimpleResult ookILResult = new SimpleResult("OOK_IL");
		SimpleResult passiveILResult = new SimpleResult("PassiveIL");
		
		erPPResult.setValue(er_pp);
		ookILResult.setValue(ook_il);
		passiveILResult.setValue(passiveIL*sitesPerBranch);
		
		MultiResult erResults = new MultiResult("ER Results");
		erResults.addResult("ER PP", erPPResult);
		erResults.addResult("OOK IL", ookILResult);
		erResults.addResult("passive IL", passiveILResult);
		resultManager.add(erResults);
		*/
		//gloco.getResultManager().add(erPPResult);
		//gloco.getResultManager().add(ookILResult);
		//gloco.getResultManager().add(passiveILResult);
	}
	
	public double getRateRelatedExtinctionRatio(Rate r) {
		double valueFor10Gbs = 8;
		return Math.pow(10, valueFor10Gbs/10);
	}
	
	private double getFindAnameForThisMethodNoam(double r) {
		double a = r-1;
		double b = r+1;
		double c = Math.sqrt(b);
		double d = Math.sqrt(r)+1;
		return -10*Math.log10(a/b*c/d);		
	}
	
}
