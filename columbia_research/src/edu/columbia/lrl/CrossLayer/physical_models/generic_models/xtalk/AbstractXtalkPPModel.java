//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package edu.columbia.lrl.CrossLayer.physical_models.generic_models.xtalk;

import java.util.Map;


public abstract class AbstractXtalkPPModel {

    public AbstractXtalkPPModel() {
    }

    public abstract double getXtalkPP_DB(double xTalkPower, int berIndex, double modulationER);

    public abstract Map<String, String> getAllParameters();
}
