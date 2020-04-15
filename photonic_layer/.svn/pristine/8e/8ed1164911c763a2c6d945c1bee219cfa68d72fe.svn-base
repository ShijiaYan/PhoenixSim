package People.Meisam.GUI.Utilities.InterconnectsGUI.CanvasGUI;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class mainGUI extends Application {

	public static void main(String[] args){
		launch(args);
	}

	Canvas cv ;
	GraphicsContext gc ;
	Stage stage ;
	Image yJunction = new Image("/People/Meisam/GUI/Utilities/InterconnectsGUI/CanvasElements/Extras/Yjunction.png", 200, 200, true, true) ;
	Image straightWg = new Image("/People/Meisam/GUI/Utilities/InterconnectsGUI/CanvasElements/Extras/StraightWg.png", 200, 200, true, true) ;

	double xOffset = 0 ;
	double yOffset = 0 ;

	@Override
	public void start(Stage primaryStage) throws Exception {
		stage = primaryStage ;
		cv = new Canvas(2000,2000) ;
		gc = cv.getGraphicsContext2D() ;
		Pane pane1 = new Pane(cv) ;

		Scene scene = new Scene(pane1, 1300, 850) ;


		pane1.setOnMouseClicked(e->{
			double x = e.getX() + xOffset ;
			double y = e.getY() + yOffset ;
//			gc.drawImage(im, x, y);
//			System.out.println("x = " + x +" ,  y = " + y);
//			Circle cr = new Circle(x+5, y+5, 10) ;
//			cr.setFill(Color.BLACK);
//			pane1.getChildren().add(cr) ;

			Rectangle rec0 = new Rectangle(x, y, 100, 100) ;
			rec0.setFill(Color.WHITE);
			rec0.setStroke(Color.BLACK);
			Line line0 = new Line(x, y+20, x-20, y+20) ;
			Rectangle port1 = new Rectangle(x-26, y+16, 10, 10) ;
			port1.setFill(Color.BLACK);
			Line line1 = new Line(x, y+80, x-20, y+80) ;
			Rectangle port2 = new Rectangle(x-26, y+80-4, 10, 10) ;
			port2.setFill(Color.BLACK);

			pane1.getChildren().addAll(rec0, line0, port1, line1, port2) ;

//			gc.drawImage(im, x+2, y+2, 80, 80);

//			gc.rect(x, y, 84, 84);
//			gc.stroke() ;

//			gc.setFill(Color.YELLOWGREEN);
//			gc.fillRect(x, y, 100, 100);
//			gc.strokeRect(x, y, 100, 100);
//			gc.strokeLine(x, y+20, x-20, y+20);
//			gc.setFill(Color.BLACK);
//			gc.fillRect(x-26, y+20-4, 10, 10);


//			gc.drawImage(yJunction, x, y-50);
//			gc.drawImage(straightWg, x, y-20);
		});

		stage.setScene(scene);
		stage.show();
	}




}
