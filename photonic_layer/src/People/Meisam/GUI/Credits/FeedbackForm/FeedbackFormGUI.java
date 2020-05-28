package People.Meisam.GUI.Credits.FeedbackForm;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class FeedbackFormGUI extends Application {
	
    public static void main(String[] args){
        launch(args);
    }

    Stage window ;

    @Override
    public void start(Stage primaryStage) throws Exception {
        window = primaryStage ;
        FXMLLoader loader = new FXMLLoader(Class.class.getResource("/People/Meisam/GUI/Credits/FeedbackForm/feedback_form.fxml")) ;
        Parent root = loader.load() ;
        Scene scene = new Scene(root) ;
        window.setScene(scene);
        window.setTitle("Feedback Form v1.0");
        window.setResizable(false);
        window.getIcons().add(new Image("/People/Meisam/GUI/Credits/FeedbackForm/Extras/feedback.png")) ;
        window.show();
        
        window.setOnCloseRequest(e -> {
        	System.exit(0);
        });

    }

}
