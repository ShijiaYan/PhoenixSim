package People.Meisam.GUI.PhoenixSim.TabsLibrary.Welcome;

import People.Meisam.GUI.PhoenixSim.MainModule.PhoenixSimModule;
import People.Meisam.GUI.PhoenixSim.TabsLibrary.AbstractTab;
import People.Meisam.GUI.Utilities.SimulationDataBase;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import org.controlsfx.control.StatusBar;

import java.io.IOException;

public class WelcomeTab extends AbstractTab {

    FXMLLoader loader = new FXMLLoader(Class.class.getResource("/People/Meisam/GUI/PhoenixSim/TabsLibrary/Welcome" +
            "/welcome_tab.fxml"));

    WelcomeTabController controller;

    public WelcomeTab(SimulationDataBase simDataBase) {
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.controller = loader.getController();
        controller.setSimDataBase(simDataBase);
//		controller.initialize();
    }

    public Tab getTab() {
        return controller.getTab();
    }

    public StatusBar getStatusBar() {
        return controller.getStatusBar();
    }

    public WelcomeTabController getController() {
        return controller;
    }

    public void setSimDataBase(SimulationDataBase simDataBase) {
        controller.setSimDataBase(simDataBase);
    }

    public SimulationDataBase getSimDataBase() {
        return controller.getSimDataBase();
    }

    @Override
    public String getName() {
        return "WelcomeTab";
    }

    @Override
    public void popUpTab() {
        PhoenixSimModule module = new PhoenixSimModule(controller.getSimDataBase());
        module.getController().closeCurrentTabPressed(); // removing the "welcome tab"
        module.getController().addWelcomeTab();
    }

}
