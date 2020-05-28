package People.Meisam.GUI.PhoenixSim.ModulesLibrary.PlotterModule;

import java.io.IOException;
import People.Meisam.GUI.Builders.WindowBuilder;
import People.Meisam.GUI.Plotters.MainGUI.PlotterController;
import People.Meisam.GUI.Utilities.SimulationDataBase;
import javafx.fxml.FXMLLoader;

public class PlotterModule {

	public PlotterModule(SimulationDataBase simDataBase){
        FXMLLoader loader = new FXMLLoader(Class.class.getResource("/People/Meisam/GUI/Plotters/MainGUI/plotter.fxml")) ;
        WindowBuilder plotter = new WindowBuilder(loader) ;
        plotter.setIcon("/People/Meisam/GUI/Plotters/MainGUI/Extras/plotter.png");
        try {
			plotter.build_NoModality("Plotter v0.5 Beta", true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        PlotterController controller = (PlotterController) plotter.getController() ;
        controller.setDataBase(simDataBase);
        controller.initialize();
	}

}
