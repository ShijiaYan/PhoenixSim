package People.Meisam.GUI.PhoenixSim.ModulesLibrary.DatabaseModule;

import java.io.IOException;
import People.Meisam.GUI.Builders.WindowBuilder;
import People.Meisam.GUI.Utilities.SimulationDataBase;
import People.Meisam.GUI.Utilities.DataBaseTable.DataBase_v1_0.DatabaseTableController;
import javafx.fxml.FXMLLoader;

public class DatabaseModule {

    FXMLLoader loader = new FXMLLoader(Class.class.getResource("/People/Meisam/GUI/Utilities/DataBaseTable/DataBase_v1_0/database_table.fxml")) ;
    WindowBuilder builder = new WindowBuilder(loader) ;
    public DatabaseTableController controller ;

	public DatabaseModule(SimulationDataBase simDataBase){
      builder.setIcon("/People/Meisam/GUI/Utilities/DataBaseTable/Extras/database.png");
      try {
		builder.build_NoModality("Simulation DataBase v1.0", true);
	} catch (IOException e2) {
		e2.printStackTrace();
	}
        controller = loader.getController() ;
        controller.setDataBase(simDataBase);
        controller.updateParamTable();
        controller.getCloseButton().setOnAction(e -> {
            try {
                builder.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
	}

	public DatabaseTableController getController(){
		return controller ;
	}

	public void refreshTable(SimulationDataBase simDataBase){
		controller.getRefreshButton().setOnAction(e -> {
			controller.refreshTable(simDataBase);
        });
	}


}
