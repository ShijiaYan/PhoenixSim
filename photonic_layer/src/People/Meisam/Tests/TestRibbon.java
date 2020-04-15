package People.Meisam.Tests;

import People.Meisam.GUI.Utilities.ExportPlot.JavaFXFileChooser.FileChooserFX;
import People.Meisam.GUI.Utilities.RibbonFX.Ribbon;
import People.Meisam.GUI.Utilities.RibbonFX.RibbonGroup;
import People.Meisam.GUI.Utilities.RibbonFX.RibbonTab;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TestRibbon extends Application {

	public static void main(String[] args){
		launch(args);
	}

	Stage stage ;

	@Override
	public void start(Stage primaryStage) throws Exception {
		stage = primaryStage ;

		Ribbon ribbon = new Ribbon() ;
//		ribbon.setPrefHeight(150);
		RibbonTab tab1 = new RibbonTab("HOME") ;
		RibbonGroup tab1Group = new RibbonGroup() ;
//		tab1Group.setTitle("File");
		RibbonGroup tab2Group = new RibbonGroup() ;
//		tab2Group.setTitle("Document");
		tab1.getRibbonGroups().addAll(tab1Group, tab2Group) ;

		RibbonTab tab2 = new RibbonTab("SIMULATION") ;
		RibbonTab tab3 = new RibbonTab("PROPERTIES") ;
		ribbon.getTabs().addAll(tab1, tab2, tab3) ;

        Image storeImage = new Image(getClass().getResourceAsStream("/People/Meisam/Tests/test_resized.png"));
        ImageView imView = new ImageView(storeImage) ;
        Button storeButton = new Button("Save", imView);
        storeButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        storeButton.setOnAction(e -> {
        	System.out.println("This is just a simple test of the RibbonFX!");
        	FileChooserFX fc = new FileChooserFX() ;
        	fc.saveFile();
        });
        tab1Group.getNodes().add(storeButton);

        ImageView imView1 = new ImageView(storeImage) ;
        Button storeButton1 = new Button("Save1", imView1);
        storeButton1.setContentDisplay(ContentDisplay.TOP);
        storeButton1.setOnAction(e -> {
        	System.out.println("This is just a simple test of the RibbonFX!");
        	FileChooserFX fc = new FileChooserFX() ;
        	fc.saveFile();
        });
        tab2Group.getNodes().add(storeButton1);


		VBox vbox = new VBox() ;
		StackPane stackPane = new StackPane() ;
		Pane pane = new Pane() ;
		vbox.getChildren().addAll(ribbon, pane) ;
		stackPane.getChildren().add(vbox) ;
		Scene scene = new Scene(stackPane, 800, 600) ;
		stage.setScene(scene);
		stage.show();

	}

}
