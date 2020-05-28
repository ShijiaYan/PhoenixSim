package People.Meisam.GUI.PhoenixSim.TabsLibrary.CouplerDesigner.silicon_nitride.ring_wg;

import java.io.IOException;
import org.controlsfx.control.StatusBar;
import People.Meisam.GUI.PhoenixSim.MainModule.PhoenixSimModule;
import People.Meisam.GUI.PhoenixSim.TabsLibrary.AbstractTab;
import People.Meisam.GUI.Utilities.SimulationDataBase;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;

public class RingWgCouplerSiNTab extends AbstractTab {

	FXMLLoader loader = new FXMLLoader(Class.class.getResource("/People/Meisam/GUI/PhoenixSim/TabsLibrary/CouplerDesigner/silicon_nitride/ring_wg/ring_wg_coupler.fxml")) ; ;
	RingWgCouplerSiNTabController controller ;

	public RingWgCouplerSiNTab(SimulationDataBase simDataBase){
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
	public RingWgCouplerSiNTabController getController(){
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
		return "RingWgCouplerSiNTab" ;
	}

	@Override
	public void popUpTab() {
		PhoenixSimModule module = new PhoenixSimModule(controller.getSimDataBase(), false) ;
		module.getController().addRingWgCouplerTab();
	}
}
