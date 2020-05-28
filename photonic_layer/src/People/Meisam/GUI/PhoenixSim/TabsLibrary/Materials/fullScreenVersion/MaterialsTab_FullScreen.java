package People.Meisam.GUI.PhoenixSim.TabsLibrary.Materials.fullScreenVersion;

import java.io.IOException;
import org.controlsfx.control.StatusBar;
import People.Meisam.GUI.PhoenixSim.MainModule.PhoenixSimModule;
import People.Meisam.GUI.PhoenixSim.TabsLibrary.AbstractTab;
import People.Meisam.GUI.PhoenixSim.TabsLibrary.Materials.MaterialsTabController;
import People.Meisam.GUI.Utilities.SimulationDataBase;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;

public class MaterialsTab_FullScreen extends AbstractTab {

	FXMLLoader loader = new FXMLLoader(Class.class.getResource("/People/Meisam/GUI/PhoenixSim/TabsLibrary/Materials/fullScreenVersion/materials_fullscreen.fxml")) ;
	MaterialsTabController controller ;

	public MaterialsTab_FullScreen(SimulationDataBase simDataBase){
		try {
			loader.load() ;
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.controller = loader.getController()  ;
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
	public MaterialsTabController getController() {
		return controller;
	}

	@Override
	public String getName() {
		return "MaterialsTab" ;
	}

	@Override
	public void popUpTab() {
		PhoenixSimModule module = new PhoenixSimModule(controller.getSimDataBase()) ;
		module.getController().closeCurrentTabPressed(); // removing the "welcome tab"
		module.getController().addMaterialsTab();
	}


}
