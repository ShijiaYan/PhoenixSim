package People.Meisam.GUI.PhoenixSim.ModulesLibrary.SingleVariableCalculus;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class SingleVariableCalculusGUI extends Application {
	
    public static void main(String[] args){
        launch(args);
    }

    Stage window ;

    @Override
    public void start(Stage primaryStage) throws Exception {
        window = primaryStage ;
        FXMLLoader loader = new FXMLLoader(Object.class.getClass().getResource("/People/Meisam/GUI/SingleVariableCalculus/single_variable_calculus.fxml")) ;
        Parent root = loader.load() ;
        Scene scene = new Scene(root) ;
        window.setScene(scene);
        window.setTitle("Single Variable Calculus");
        window.setResizable(false);
        window.getIcons().add(new Image("/People/Meisam/GUI/SingleVariableCalculus/Extras/function.png")) ;
        window.show();
        
        window.setOnCloseRequest(e -> {
        	System.exit(0);
        });
        
//        HeaterController controller = loader.getController() ;
//        controller.closeApp.setOnAction(e -> {
//        	window.close();
//        	System.exit(0);
//        });

    }

}
