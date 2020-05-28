package People.Meisam.GUI.PhoenixSim.MainModule.FullScreenMode;

import java.io.IOException;
import People.Meisam.GUI.Utilities.DataBaseTable.DataBase_v1_0.DatabaseTableController;
import People.Meisam.GUI.Utilities.ExportPlot.JavaFXFileChooser.FileChooserFX;
import People.Meisam.GUI.Utilities.fileviewer.FileListView;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class PhoenixSimGUI extends Application {

    public static void main(String[] args){
        launch(args);
    }

    Stage window ;

    @Override
    public void start(Stage primaryStage) throws Exception {
        window = primaryStage ;
        FXMLLoader loader = new FXMLLoader(Object.class.getResource("/People/Meisam/GUI/PhoenixSim/MainModule/FullScreenMode/phoenixSim_module_fullScreen.fxml")) ;
        Parent root = loader.load() ;
        Scene scene = new Scene(root) ;
        window.setScene(scene);
        window.setTitle("PhoenixSim v1.0 - Main Window");
        window.setResizable(false);
        window.getIcons().add(new Image("/People/Meisam/GUI/ThermalHeater/Extras/PhoenixSim_logo.png")) ;

        PhoenixSimModuleController controller = loader.getController() ;
        controller.addWelcomeTab();

        window.show();

        window.setOnCloseRequest(e -> System.exit(0));

        controller.closeApp.setOnAction(e -> {
        	window.close();
        	System.exit(0);
        });

        controller.fullScreenMode.selectedProperty().addListener((v, ov, nv)->{
        	controller.fullScreenMode.setDisable(true);
        	controller.tabs.setSide(Side.TOP);

        	window.setResizable(nv);
        	FileListView fileViewer = new FileListView() ;
        	fileViewer.setPath(FileChooserFX.path);
        	fileViewer.refreshFileViewer();

        	TabPane leftPane = new TabPane() ;
        	Tab workDir = new Tab("Workspace") ;
        	workDir.setContent(fileViewer.getListViewer());
        	leftPane.getTabs().add(workDir) ;
        	leftPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
        	leftPane.setSide(Side.LEFT);

        	TabPane rightPane = new TabPane() ;
        	FXMLLoader databaseTab = new FXMLLoader(Object.class.getResource("/People/Meisam/GUI/Utilities/DataBaseTable/database_fullscreen_mode/database_table_fullscreen_mode.fxml")) ;
        	try {
				databaseTab.load() ;
			} catch (IOException e1) {
				e1.printStackTrace();
			}
        	DatabaseTableController databaseTabController = databaseTab.getController() ;
        	databaseTabController.setDataBase(controller.simDataBase);
        	databaseTabController.updateParamTable();
        	databaseTabController.getRefreshButton().setOnAction(e -> databaseTabController.refreshTable(controller.simDataBase));
        	rightPane.getTabs().add(databaseTabController.tab) ;
        	rightPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
        	rightPane.setSide(Side.RIGHT);

        	controller.splitPane.getItems().add(0, leftPane);
        	controller.splitPane.getItems().add(2, rightPane);
        });

    }

}
