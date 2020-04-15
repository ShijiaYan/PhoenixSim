package People.Meisam.GUI.Plotters.MatlabPlotter.Tests;

import People.Meisam.GUI.Plotters.MatlabPlot.MatlabChart;
import PhotonicElements.Utilities.MathLibraries.MoreMath;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Created by meisam on 7/2/17.
 */
public class MatlabPlotTest extends Application {

    public static void main(String[] args){
        launch(args);
    }

    Stage window ;

    @Override
    public void start(Stage primaryStage) throws Exception {
        window = primaryStage ;
        StackPane pane = new StackPane() ;
        double[] x = MoreMath.linspace(-10, 10, 1000) ;
        double[] y = MoreMath.Arrays.Functions.sinc(x) ;
        MatlabChart fig = new MatlabChart() ;
        fig.plot(x, y, 3f);
        fig.RenderPlot();
        fig.xlabel("wavelength (nm)");
        fig.ylabel("Transmission (dB)");
        fig.setFontSize(15);
//        fig.markerON();
//        pane.getChildren().add(fig.getChartSwingNode(640, 450)) ;
        pane.getChildren().add(fig.getChartSwingNode()) ;
        pane.setPrefSize(640, 450);
        Scene scene = new Scene(pane) ;
        window.setScene(scene);
        window.show();
    }
}
