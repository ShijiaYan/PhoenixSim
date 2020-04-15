package People.Meisam.GUI.ThermalHeater.MainGUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Created by meisam on 7/1/17.
 */
public class HeaterGUI extends Application{

    public static void main(String[] args){
        launch(args);
    }

    Stage window ;

    @Override
    public void start(Stage primaryStage) throws Exception {
        window = primaryStage ;
        FXMLLoader loader = new FXMLLoader(Object.class.getClass().getResource("/People/Meisam/GUI/ThermalHeater/MainGUI/heater.fxml")) ;
        Parent root = loader.load() ;
        Scene scene = new Scene(root) ;
        window.setScene(scene);
        window.setTitle("Thermo-Optic Analyzer v1.0");
        window.setResizable(false);
        window.getIcons().add(new Image("/People/Meisam/GUI/ThermalHeater/Extras/PhoenixSim_logo.png")) ;
        window.show();
        
        window.setOnCloseRequest(e -> {
        	System.exit(0);
        });
        
        HeaterController controller = loader.getController() ;
        controller.closeApp.setOnAction(e -> {
        	window.close();
        	System.exit(0);
        });

    }

}