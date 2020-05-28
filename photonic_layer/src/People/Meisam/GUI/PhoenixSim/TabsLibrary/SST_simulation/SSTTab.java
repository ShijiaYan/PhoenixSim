package People.Meisam.GUI.PhoenixSim.TabsLibrary.SST_simulation;

import People.Meisam.GUI.PhoenixSim.MainModule.PhoenixSimModule;
import People.Meisam.GUI.PhoenixSim.TabsLibrary.AbstractTab;
import People.Meisam.GUI.Utilities.SimulationDataBase;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import org.controlsfx.control.StatusBar;

import java.io.IOException;

/**
 * @author kristoff
 */
public class SSTTab extends AbstractTab {

    FXMLLoader loader = new FXMLLoader(Class.class.getResource("/People/Meisam/GUI/PhoenixSim/TabsLibrary" +
            "/SST_simulation/sst_simulation_tab.fxml"));

    SSTTabController controller;

    public SSTTab(SimulationDataBase simDataBase) {
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.controller = loader.getController();
        setSimDataBase(simDataBase);
//		controller.initialize();
    }

    @Override
    public Tab getTab() {
        return controller.getTab();
    }

    @Override
    public StatusBar getStatusBar() {
        return controller.getStatusBar();
    }

    @Override
    public void setSimDataBase(SimulationDataBase simDataBase) {
        controller.setSimDataBase(simDataBase);
    }

    @Override
    public SimulationDataBase getSimDataBase() {
        return controller.getSimDataBase();
    }

    @Override
    public SSTTabController getController() {
        return this.controller;
    }

    @Override
    public String getName() {
        return "SSTTab";
    }

    @Override
    public void popUpTab() {
        PhoenixSimModule module = new PhoenixSimModule(controller.getSimDataBase());
        module.getController().closeCurrentTabPressed(); // removing the "welcome tab"
        module.getController().addWelcomeTab();
    }
}
