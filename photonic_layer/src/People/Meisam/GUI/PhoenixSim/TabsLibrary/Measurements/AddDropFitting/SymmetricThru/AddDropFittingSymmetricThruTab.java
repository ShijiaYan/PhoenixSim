package People.Meisam.GUI.PhoenixSim.TabsLibrary.Measurements.AddDropFitting.SymmetricThru;

import java.io.IOException;
import org.controlsfx.control.StatusBar;
import People.Meisam.GUI.PhoenixSim.MainModule.PhoenixSimModule;
import People.Meisam.GUI.PhoenixSim.TabsLibrary.AbstractTab;
import People.Meisam.GUI.Utilities.SimulationDataBase;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;

public class AddDropFittingSymmetricThruTab extends AbstractTab {

	FXMLLoader loader = new FXMLLoader(Class.class.getResource("/People/Meisam/GUI/PhoenixSim/TabsLibrary/Measurements/AddDropFitting/SymmetricThru/add_drop_fitting_symmetric_thru.fxml")) ; ;
	AddDropFittingSymmetricThruTabController controller ;

	public AddDropFittingSymmetricThruTab(SimulationDataBase simDataBase){
		try {
			loader.load() ;
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.controller = loader.getController()  ;
		controller.setSimDataBase(simDataBase);
	}

	public Tab getTab(){
		return controller.getTab() ;
	}

	public StatusBar getStatusBar(){
		return controller.getStatusBar() ;
	}

	@SuppressWarnings("unchecked")
	public AddDropFittingSymmetricThruTabController getController(){
		return controller ;
	}

	public void setSimDataBase(SimulationDataBase simDataBase){
		controller.setSimDataBase(simDataBase);
	}

	public SimulationDataBase getSimDataBase(){
		return controller.getSimDataBase() ;
	}

	@Override
	public String getName() {
		return "AddDropFittingSymmetricThruTab" ;
	}

	@Override
	public void popUpTab() {
		PhoenixSimModule module = new PhoenixSimModule(controller.getSimDataBase(), false) ;
		module.getController().addAddDropFittingSymmetricTab();
	}

}
