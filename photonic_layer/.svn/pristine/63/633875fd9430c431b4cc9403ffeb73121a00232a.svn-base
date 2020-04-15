package People.Meisam.Tests;

import PhotonicElements.Utilities.MathLibraries.MoreMath;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class TestJfxChart extends Application {

	public static void main(String[] args){
		launch(args);
	}

	Stage stage ;

	@Override
	public void start(Stage primaryStage) throws Exception {
		stage = primaryStage ;
		   final NumberAxis xAxis = new NumberAxis();
		    final NumberAxis yAxis = new NumberAxis();
		    xAxis.setLabel("X");
		    yAxis.setLabel("Y");
		    final LineChart<Number, Number> lineChart = new LineChart<Number, Number>(
		        xAxis, yAxis);

//		    lineChart.setTitle("Line Chart");
		    XYChart.Series<Number, Number> series = new XYChart.Series<Number, Number>();
//		    series.setName("My Data");
		    // populating the series with data
		    int N = 1000 ;
		    double[] x = MoreMath.linspace(-10, 10, N) ;
		    double[] y = MoreMath.Arrays.Functions.sinc(x) ;
		    for(int i=0; i<N; i++){
		    	series.getData().add(new Data<Number, Number>(x[i], y[i])) ;
		    }

		    Scene scene = new Scene(lineChart, 800, 600) ;
		    lineChart.getData().add(series);
		    lineChart.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
		    stage.setScene(scene);
		    stage.show();
	}

}
