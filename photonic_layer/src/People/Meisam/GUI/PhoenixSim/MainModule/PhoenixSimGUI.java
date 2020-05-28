package People.Meisam.GUI.PhoenixSim.MainModule;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


public class PhoenixSimGUI extends Application {

    private Stage window;

    /**
     * !!! Using the ChartFactory may cause unexpected crash on starting the app
     * <p>
     * if the main window does not show after "Run" in Eclipse IDE,
     * 1. try to check the fxml
     * 2. try to uncheck the "use the
     * -XstartOnFirstThread" in Run Configurations -- Arguments.
     */
    public PhoenixSimGUI() {

    }

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) throws Exception {
        window = primaryStage;
        FXMLLoader loader = new FXMLLoader(
                Object.class.getResource("/People/Meisam/GUI/PhoenixSim/MainModule/phoenixSim_module.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 800.0D, 600.0D);
        window.setScene(scene);
        window.setTitle("PhoenixSim v1.1 - Main Window");
        window.setMinWidth(800);
        window.setMinHeight(600);
        window.setResizable(true);
        window.getIcons().add(new Image("/People/Meisam/GUI/ThermalHeater/Extras/PhoenixSim_logo.png"));
        PhoenixSimModuleController controller = loader.getController();
        controller.addWelcomeTab();
        window.show();
        window.setOnCloseRequest(e -> System.exit(0));
        controller.closeApp.setOnAction(e -> {
            window.close();
            System.exit(0);
        });
        controller.fullScreenMode.selectedProperty().addListener((v, ov, nv) -> window.setResizable(nv));
    }
}
