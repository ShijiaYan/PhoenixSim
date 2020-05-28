package People.Meisam.GUI.PhoenixSim.TabsLibrary.ExtensionOptSim;

import People.Meisam.GUI.PhoenixSim.MainModule.PhoenixSimModule;
import People.Meisam.GUI.PhoenixSim.TabsLibrary.AbstractTab;
import People.Meisam.GUI.Utilities.SimulationDataBase;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import org.controlsfx.control.StatusBar;

import java.io.IOException;

/**
 * @author Rui
 */
public class ExtensionOptSimTab extends AbstractTab {

    FXMLLoader loader = new FXMLLoader(Object.class
            .getResource("/People/Meisam/GUI/PhoenixSim/TabsLibrary/ExtensionOptSim/ExtensionOptSim.fxml"));
    ExtensionOptSimTabController controller;

    public ExtensionOptSimTab(SimulationDataBase simDataBase) {
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.controller = loader.getController();
        controller.setSimDataBase(simDataBase);
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

    @SuppressWarnings("unchecked")
    @Override
    public ExtensionOptSimTabController getController() {
        return controller;
    }

    @Override
    public String getName() {
        return "ExtensionOptSimTab";
    }

    @Override
    public void popUpTab() {
        PhoenixSimModule module = new PhoenixSimModule(controller.getSimDataBase());
        module.getController().closeCurrentTabPressed(); // removing the "welcome tab"
        module.getController().addExtensionLinkTab();
    }

}
