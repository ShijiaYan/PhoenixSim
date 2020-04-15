package People.Meisam.Tests.experimental.tabtemplate;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class TestNewTabGUI extends Application {

	public static void main(String[] args){
		launch(args) ;
	}

    Stage window ;

    @Override
    public void start(Stage primaryStage) throws Exception {
        window = primaryStage ;
        Parent root = FXMLLoader.load(getClass().getResource("/People/Meisam/Tests/experimental/tabtemplate/tabsTemplate.fxml")) ;
        Scene scene = new Scene(root) ;
        window.setScene(scene);
        window.setTitle("Configure Export to MATLAB");
        window.getIcons().add(new Image(getClass().getResourceAsStream("/People/Meisam/GUI/Utilities/ExportPlot/ExportToMATLAB/Extras/MatlabIcons/Matlab_Logo.png"))) ;
        window.setResizable(true);
        window.show();
    }

}
