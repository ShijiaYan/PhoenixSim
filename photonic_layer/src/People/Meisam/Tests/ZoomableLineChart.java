package People.Meisam.Tests;

import java.util.Collections;
import javafx.application.Application;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class ZoomableLineChart extends Application {

    private static final int NUM_DATA_POINTS = 10000 ;

	@Override
	public void start(Stage primaryStage) {
		final LineChart<Number, Number> chart = createChart();

		final SplitPane chartContainer = new SplitPane() ;
//		chartContainer.getChildren().add(chart);
		chartContainer.getItems().add(chart) ;

		final Rectangle zoomRect = new Rectangle();
		zoomRect.setManaged(false);
		zoomRect.setFill(Color.LIGHTSEAGREEN.deriveColor(0, 1, 1, 0.5));
		chartContainer.getItems().add(zoomRect);

		setUpZooming(zoomRect, chart);

		final HBox controls = new HBox(10);
		controls.setPadding(new Insets(10));
		controls.setAlignment(Pos.CENTER);

		final Button zoomButton = new Button("Zoom");
		final Button resetButton = new Button("Reset");
		zoomButton.setOnAction(event -> doZoom(zoomRect, chart));
		resetButton.setOnAction(event -> {
            final NumberAxis xAxis = (NumberAxis)chart.getXAxis();
            xAxis.setLowerBound(0);
            xAxis.setUpperBound(1000);
            final NumberAxis yAxis = (NumberAxis)chart.getYAxis();
            yAxis.setLowerBound(0);
            yAxis.setUpperBound(1000);

            zoomRect.setWidth(0);
            zoomRect.setHeight(0);
        });
		final BooleanBinding disableControls =
		        zoomRect.widthProperty().lessThan(5)
		        .or(zoomRect.heightProperty().lessThan(5));
		zoomButton.disableProperty().bind(disableControls);
		controls.getChildren().addAll(zoomButton, resetButton);

		final BorderPane root = new BorderPane();
		root.setCenter(chartContainer);
		root.setBottom(controls);

		final Scene scene = new Scene(root, 600, 400);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private LineChart<Number, Number> createChart() {
	    final NumberAxis xAxis = createAxis();
	    final NumberAxis yAxis = createAxis();
	    final LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
	    chart.setAnimated(true);
	    chart.setCreateSymbols(false);
	    chart.setData(generateChartData());
	    return chart ;
	}

    private NumberAxis createAxis() {
        final NumberAxis xAxis = new NumberAxis();
	    xAxis.setAutoRanging(true);
//	    xAxis.setLowerBound(0);
//	    xAxis.setUpperBound(1000);
        return xAxis;
    }

    private ObservableList<Series<Number, Number>> generateChartData() {
        final Series<Number, Number> series = new Series<>();
        series.setName("Data");
        for (int i=0; i<NUM_DATA_POINTS; i++) {
            Data<Number, Number> dataPoint = new Data<>(4 * Math.PI / NUM_DATA_POINTS * i, Math.sin(4 * Math.PI / NUM_DATA_POINTS * i));
            series.getData().add(dataPoint);
        }
        return FXCollections.observableArrayList(Collections.singleton(series));
    }

    private void setUpZooming(final Rectangle rect, final Node zoomingNode) {
        final ObjectProperty<Point2D> mouseAnchor = new SimpleObjectProperty<>();
        zoomingNode.setOnMousePressed(event -> {
            mouseAnchor.set(new Point2D(event.getX(), event.getY()));
            rect.setWidth(0);
            rect.setHeight(0);
        });
        zoomingNode.setOnMouseDragged(event -> {
            double x = event.getX();
            double y = event.getY();
            rect.setX(Math.min(x, mouseAnchor.get().getX()));
            rect.setY(Math.min(y, mouseAnchor.get().getY()));
            rect.setWidth(Math.abs(x - mouseAnchor.get().getX()));
            rect.setHeight(Math.abs(y - mouseAnchor.get().getY()));
        });
    }

    private void doZoom(Rectangle zoomRect, LineChart<Number, Number> chart) {
        Point2D zoomTopLeft = new Point2D(zoomRect.getX(), zoomRect.getY());
        Point2D zoomBottomRight = new Point2D(zoomRect.getX() + zoomRect.getWidth(), zoomRect.getY() + zoomRect.getHeight());
        final NumberAxis yAxis = (NumberAxis) chart.getYAxis();
        Point2D yAxisInScene = yAxis.localToScene(0, 0);
        final NumberAxis xAxis = (NumberAxis) chart.getXAxis();
        Point2D xAxisInScene = xAxis.localToScene(0, 0);
        double xOffset = zoomTopLeft.getX() - yAxisInScene.getX() ;
        double yOffset = zoomBottomRight.getY() - xAxisInScene.getY();
        double xAxisScale = xAxis.getScale();
        double yAxisScale = yAxis.getScale();
        xAxis.setLowerBound(xAxis.getLowerBound() + xOffset / xAxisScale);
        xAxis.setUpperBound(xAxis.getLowerBound() + zoomRect.getWidth() / xAxisScale);
        yAxis.setLowerBound(yAxis.getLowerBound() + yOffset / yAxisScale);
        yAxis.setUpperBound(yAxis.getLowerBound() - zoomRect.getHeight() / yAxisScale);
        System.out.println(yAxis.getLowerBound() + " " + yAxis.getUpperBound());
        zoomRect.setWidth(0);
        zoomRect.setHeight(0);
    }

	public static void main(String[] args) {
		launch(args);
	}
}
