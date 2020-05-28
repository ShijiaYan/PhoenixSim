package edu.columbia.lrl.CrossLayer.physical_models.generic_models.xtalk;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;
import umontreal.iro.lecuyer.probdist.NormalDist;

import java.util.Map;


public class MeisamJLTXtalkModel extends AbstractXtalkPPModel {

    private boolean ap;

    public MeisamJLTXtalkModel(@ParamName(name = "Apply mod er?", default_ = "true") boolean ap) {
        this.ap = ap;
    }

    public double getXtalkPP_DB(double xtalkPower, int berIndex, double modulationER) {
        double qBER = -NormalDist.inverseF01(Math.pow(10, -berIndex));
        double erfactor = 1;
        if (this.ap && !Double.isInfinite(modulationER)) {
            erfactor = (modulationER + 1.0D) / (modulationER - 1.0D);
        }

        double crosstalkPowerPenaltyFactor = 0.5 * qBER * xtalkPower * erfactor;
        return -10 * Math.log10(1 / (1 + crosstalkPowerPenaltyFactor));
    }

    public Map<String, String> getAllParameters() {
        return SimpleMap.getMap("apply er from mod at demux", this.ap);
    }
}