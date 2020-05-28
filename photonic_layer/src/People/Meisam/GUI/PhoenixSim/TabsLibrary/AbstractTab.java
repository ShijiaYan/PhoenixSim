package People.Meisam.GUI.PhoenixSim.TabsLibrary;

import People.Meisam.GUI.Utilities.SimulationDataBase;
import javafx.scene.control.Tab;
import org.controlsfx.control.StatusBar;

public abstract class AbstractTab {

    public abstract Tab getTab();

    public abstract StatusBar getStatusBar();

    public abstract void setSimDataBase(SimulationDataBase simDataBase);

    public abstract SimulationDataBase getSimDataBase();

    public abstract AbstractTabController getController();

    public abstract String getName();

    public abstract void popUpTab();

}
