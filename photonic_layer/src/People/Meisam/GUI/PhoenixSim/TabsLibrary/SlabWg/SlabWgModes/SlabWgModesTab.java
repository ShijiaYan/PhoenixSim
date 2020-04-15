package People.Meisam.GUI.PhoenixSim.TabsLibrary.SlabWg.SlabWgModes;

import java.io.IOException;
import org.controlsfx.control.StatusBar;
import People.Meisam.GUI.PhoenixSim.MainModule.PhoenixSimModule;
import People.Meisam.GUI.PhoenixSim.TabsLibrary.AbstractTab;
import People.Meisam.GUI.Utilities.SimulationDataBase;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;

public class SlabWgModesTab extends AbstractTab {

	FXMLLoader loader = new FXMLLoader(Object.class.getClass().getResource("/People/Meisam/GUI/PhoenixSim/TabsLibrary/SlabWg/SlabWgModes/slab_wg_modes.fxml")) ; ;
	SlabWgModesTabController controller ;

	public SlabWgModesTab(SimulationDataBase simDataBase){
		try {
			loader.load() ;
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.controller = loader.getController()  ;
		controller.setSimDataBase(simDataBase);
//		controller.initialize();
	}

	@Override
	public Tab getTab() {
		return controller.getTab() ;
	}

	@Override
	public StatusBar getStatusBar() {
		return controller.getStatusBar() ;
	}

	@Override
	public void setSimDataBase(SimulationDataBase simDataBase) {
		controller.setSimDataBase(simDataBase);
	}

	@Override
	public SimulationDataBase getSimDataBase() {
		return controller.getSimDataBase() ;
	}

	@SuppressWarnings("unchecked")
	@Override
	public SlabWgModesTabController getController() {
		return controller;
	}

	@Override
	public String getName() {
		return "SlabWgModesTab" ;
	}

	@Override
	public void popUpTab() {
		PhoenixSimModule module = new PhoenixSimModule(controller.getSimDataBase()) ;
		module.getController().closeCurrentTabPressed(); // removing the "welcome tab"
		module.getController().addSlabWgModesTab();
	}

}
