package People.Rui.Extension.OptSimExtension;
/**
 * This is the data which is going to be shown in the table view.
 */

import javafx.beans.property.SimpleStringProperty;

public class PowerPenaltyTableData {
    private SimpleStringProperty wavelength;
    private SimpleStringProperty powerPenalty;


    // remember the unit is micro and miliwatt
    public PowerPenaltyTableData(Double wavelength, Double powerPenalty) {
        this.wavelength = new SimpleStringProperty(String.format("%.4f", wavelength));
        this.powerPenalty = new SimpleStringProperty(String.format("%.4f", powerPenalty));
    }


    public String getPowerPenalty() {
        return powerPenalty.get();
    }

    public void setPowerPenalty(Double newPower) {
        this.powerPenalty.set(String.format("%.4f", newPower));
    }

    public String getWavelength() {
        return wavelength.get();
    }

    public void setWavelength(Double newWavelength) {
        this.wavelength.set(String.format("%.4f", newWavelength));
    }


}

