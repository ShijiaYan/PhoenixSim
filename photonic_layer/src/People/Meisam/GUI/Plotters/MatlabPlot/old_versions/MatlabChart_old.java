package People.Meisam.GUI.Plotters.MatlabPlot.old_versions;

import javafx.embed.swing.SwingNode;
import javafx.fxml.FXMLLoader;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTitleAnnotation;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import org.jfree.graphics2d.svg.SVGUtils;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleEdge;
import People.Meisam.GUI.Builders.WindowBuilder;
import People.Meisam.GUI.Utilities.SimulationDataBase;
import People.Meisam.GUI.Utilities.SimulationVariable;
import People.Meisam.GUI.Utilities.ExportData.ExportVariables;
import People.Meisam.GUI.Utilities.ExportPlot.ExportToMATLAB.ExportToMatlabController;
import People.Meisam.GUI.Utilities.ExportPlot.JavaFXFileChooser.FileChooserFX;
import PhotonicElements.Utilities.MathLibraries.MoreMath;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MatlabChart_old {

    Font font;
    JFreeChart chart;
    LegendTitle legend;
    ArrayList<Color> colors;
    ArrayList<Stroke> strokes;
    XYSeriesCollection dataset;
    XYLineAndShapeRenderer plotRenderer = new XYLineAndShapeRenderer() ;

    int counter = 0 ;

    public MatlabChart_old() {
        font = JFreeChart.DEFAULT_TITLE_FONT;
        colors = new ArrayList<>();
        strokes = new ArrayList<>();
        dataset = new XYSeriesCollection();
    }
    //*********************************************************
    public void plot(double[] x, double[] y, String spec, float lineWidth, String title) {
        final XYSeries series = new XYSeries(title);
        for (int i = 0; i < x.length; i++)
            series.add(x[i],y[i]);
        dataset.addSeries(series);
        FindColor(spec,lineWidth);
    }

    public void plot(double[] x, double[] y, String spec, float lineWidth) {
    	String title = "fig " + counter ;
    	counter++ ;
        final XYSeries series = new XYSeries(title);
        for (int i = 0; i < x.length; i++)
            series.add(x[i],y[i]);
        dataset.addSeries(series);
        FindColor(spec,lineWidth);
    }

    public void plot(double[] x, double[] y, float lineWidth) {
    	String title = "fig " + counter ;
    	counter++ ;
        String spec = "-b" ;
        final XYSeries series = new XYSeries(title);
        for (int i = 0; i < x.length; i++)
            series.add(x[i],y[i]);
        dataset.addSeries(series);
        FindColor(spec,lineWidth);
    }

    public void plot(double[] x, double[] y, String spec) {
    	String title = "fig " + counter ;
    	counter++ ;
        float lineWidth = 1 ;
        final XYSeries series = new XYSeries(title);
        for (int i = 0; i < x.length; i++)
            series.add(x[i],y[i]);
        dataset.addSeries(series);
        FindColor(spec,lineWidth);
    }

    public void plot(double[] x, double[] y) {
    	String title = "fig " + counter ;
    	counter++ ;
        String spec = "-b" ;
        float lineWidth = 1 ;
        final XYSeries series = new XYSeries(title);
        for (int i = 0; i < x.length; i++)
            series.add(x[i],y[i]);
        dataset.addSeries(series);
        FindColor(spec,lineWidth);
    }
    //*********************************************************
//    public void RenderPlot() {
//        // Create chart
//        JFreeChart chart = null;
//        if (dataset != null && dataset.getSeriesCount() > 0)
//            chart = ChartFactory.createXYLineChart(null,null,null,dataset,PlotOrientation.VERTICAL,true, false, false);
////        	chart = ChartFactory.createXYLineChart(null,null,null,dataset,PlotOrientation.VERTICAL,true, true, false);
//        else
//            System.out.println(" [!] First create a chart and add data to it. The plot is empty now!");
//        // Add customization options to chart
//        XYPlot plot = chart.getXYPlot();
//        for (int i = 0; i < colors.size(); i++) {
//            plot.getRenderer().setSeriesPaint(i, colors.get(i));
//            plot.getRenderer().setSeriesStroke(i, strokes.get(i));
//            // adding markers
//        }
//        ((NumberAxis)plot.getDomainAxis()).setAutoRangeIncludesZero(false);
//        ((NumberAxis)plot.getRangeAxis()).setAutoRangeIncludesZero(false);
//        plot.setBackgroundPaint(Color.WHITE);
//        legend = chart.getLegend();
//        chart.removeLegend();
//        this.chart = chart;
//    }

    public void RenderPlot() {
        // Create chart
        JFreeChart chart = null;
        if (dataset != null && dataset.getSeriesCount() > 0)
//            chart = ChartFactory.createXYLineChart(null,null,null,dataset,PlotOrientation.VERTICAL,true, false, false);
        	chart = ChartFactory.createXYLineChart(null,null,null,dataset,PlotOrientation.VERTICAL,true, true, false);
        else
            System.out.println(" [!] First create a chart and add data to it. The plot is empty now!");
        // Add customization options to chart
        XYPlot plot = chart.getXYPlot();
        for (int i = 0; i < colors.size(); i++) {
            // adding markers
            plotRenderer.setSeriesStroke(i, strokes.get(i));
            plotRenderer.setSeriesPaint(i, colors.get(i));
            plotRenderer.setSeriesShapesVisible(i, true);
        }
        plot.setRenderer(plotRenderer);

        ((NumberAxis)plot.getDomainAxis()).setAutoRangeIncludesZero(false);
        ((NumberAxis)plot.getRangeAxis()).setAutoRangeIncludesZero(false);
        plot.setBackgroundPaint(Color.WHITE);
        legend = chart.getLegend();
        chart.removeLegend();
        this.chart = chart;
    }

    //*********************************************************
    private void CheckExists() {
        if (chart == null) {
            throw new IllegalArgumentException("First plot something in the chart before you modify it.");
        }
    }

    @SuppressWarnings("unused")
	private boolean getExists() {
        return chart != null;
    }

    //*********************************************************

    public void setXAxis_to_Log(){
    	NumberAxis xLogAxis = new LogarithmicAxis(getXLabel()) ;
    	chart.getXYPlot().setDomainAxis(xLogAxis);
    }

    public void setXAxis_to_Linear(){
    	NumberAxis xLinearAxis = new NumberAxis(getXLabel()) ;
    	chart.getXYPlot().setDomainAxis(xLinearAxis);
    }

    public void setYAxis_to_Log(){
    	NumberAxis yLogAxis = new LogarithmicAxis(getYLabel()) ;
    	chart.getXYPlot().setRangeAxis(yLogAxis);
    }

    public void setYAxis_to_Linear(){
    	NumberAxis yLinearAxis = new LogarithmicAxis(getYLabel()) ;
    	chart.getXYPlot().setRangeAxis(yLinearAxis);
    }

    //*********************************************************
    public void font(String name, int fontSize) {
        CheckExists();
        font = new Font(name, Font.PLAIN, fontSize);
//        chart.getTitle().setFont(font);
        chart.getXYPlot().getDomainAxis().setLabelFont(font);
        chart.getXYPlot().getDomainAxis().setTickLabelFont(font);
        chart.getXYPlot().getRangeAxis().setLabelFont(font);
        chart.getXYPlot().getRangeAxis().setTickLabelFont(font);
        legend.setItemFont(font);
    }

    public void font(String name) {
        CheckExists();
        font = new Font(name, Font.PLAIN, font.getSize());
//        chart.getTitle().setFont(font);
        chart.getXYPlot().getDomainAxis().setLabelFont(font);
        chart.getXYPlot().getDomainAxis().setTickLabelFont(font);
        chart.getXYPlot().getRangeAxis().setLabelFont(font);
        chart.getXYPlot().getRangeAxis().setTickLabelFont(font);
        legend.setItemFont(font);
    }


    public void font(int fontSize) {
        CheckExists();
        font = new Font(font.getName(), Font.PLAIN, fontSize);
//        chart.getTitle().setFont(font);
        chart.getXYPlot().getDomainAxis().setLabelFont(font);
        chart.getXYPlot().getDomainAxis().setTickLabelFont(font);
        chart.getXYPlot().getRangeAxis().setLabelFont(font);
        chart.getXYPlot().getRangeAxis().setTickLabelFont(font);
        legend.setItemFont(font);
    }

    public void setFontSize(int fontSize) {
        CheckExists();
        font = new Font(font.getFontName(), Font.PLAIN, fontSize);
//        chart.getTitle().setFont(font);
        chart.getXYPlot().getDomainAxis().setLabelFont(font);
        chart.getXYPlot().getDomainAxis().setTickLabelFont(font);
        chart.getXYPlot().getRangeAxis().setLabelFont(font);
        chart.getXYPlot().getRangeAxis().setTickLabelFont(font);
        legend.setItemFont(font);
    }

    //*********************************************************
    public void title(String title) {
        CheckExists();
        chart.setTitle(title);
    }

    public String getTitle() {
        CheckExists();
        return chart.getTitle().getText() ;
    }

    public void setTitle(String title) {
        CheckExists();
        chart.setTitle(title);
    }

    public void titleOFF(){
        chart.setTitle("");
    }

    public void xlim(double l, double u) {
        CheckExists();
        chart.getXYPlot().getDomainAxis().setRange(l, u);
    }

    public void setXlim(double l, double u) {
        CheckExists();
        chart.getXYPlot().getDomainAxis().setRange(l, u);
    }

    public double[] getXlim() {
        CheckExists();
        double xMin = chart.getXYPlot().getDomainAxis().getRange().getLowerBound() ;
        double xMax = chart.getXYPlot().getDomainAxis().getRange().getUpperBound() ;
        return new double[] {xMin, xMax} ;
    }

    public void ylim(double l, double u) {
        CheckExists();
        chart.getXYPlot().getRangeAxis().setRange(l, u);
    }

    public void setYlim(double l, double u) {
        CheckExists();
        chart.getXYPlot().getRangeAxis().setRange(l, u);
    }

    public double[] getYlim() {
        CheckExists();
        double yMin = chart.getXYPlot().getRangeAxis().getRange().getLowerBound() ;
        double yMax = chart.getXYPlot().getRangeAxis().getRange().getUpperBound() ;
        return new double[] {yMin, yMax} ;
    }

    public void xlabel(String label) {
        CheckExists();
        chart.getXYPlot().getDomainAxis().setLabel(label);
    }

    public void setXLabel(String label) {
        CheckExists();
        chart.getXYPlot().getDomainAxis().setLabel(label);
    }

    public void ylabel(String label) {
        CheckExists();
        chart.getXYPlot().getRangeAxis().setLabel(label);
    }

    public void setYLabel(String label) {
        CheckExists();
        chart.getXYPlot().getRangeAxis().setLabel(label);
    }

    public String getXLabel(){
    	CheckExists();
    	return chart.getXYPlot().getDomainAxis().getLabel() ;
    }

    public String getYLabel(){
    	CheckExists();
    	return chart.getXYPlot().getRangeAxis().getLabel() ;
    }
    //*********************************************************
    public void legend(String position) {
        CheckExists();
        legend.setItemFont(font);
        legend.setBackgroundPaint(Color.WHITE);
        legend.setFrame(new BlockBorder(Color.BLACK));
        if (position.toLowerCase().equals("northoutside")) {
            legend.setPosition(RectangleEdge.TOP);
            chart.addLegend(legend);
        } else if (position.toLowerCase().equals("eastoutside")) {
            legend.setPosition(RectangleEdge.RIGHT);
            chart.addLegend(legend);
        } else if (position.toLowerCase().equals("southoutside")) {
            legend.setPosition(RectangleEdge.BOTTOM);
            chart.addLegend(legend);
        } else if (position.toLowerCase().equals("westoutside")) {
            legend.setPosition(RectangleEdge.LEFT);
            chart.addLegend(legend);
        } else if (position.toLowerCase().equals("north")) {
            legend.setPosition(RectangleEdge.TOP);
            XYTitleAnnotation ta = new XYTitleAnnotation(0.50,0.98,legend,RectangleAnchor.TOP);
            chart.getXYPlot().addAnnotation(ta);
        } else if (position.toLowerCase().equals("northeast")) {
            legend.setPosition(RectangleEdge.TOP);
            XYTitleAnnotation ta = new XYTitleAnnotation(0.98,0.98,legend,RectangleAnchor.TOP_RIGHT);
            chart.getXYPlot().addAnnotation(ta);
        } else if (position.toLowerCase().equals("east")) {
            legend.setPosition(RectangleEdge.RIGHT);
            XYTitleAnnotation ta = new XYTitleAnnotation(0.98,0.50,legend,RectangleAnchor.RIGHT);
            chart.getXYPlot().addAnnotation(ta);
        } else if (position.toLowerCase().equals("southeast")) {
            legend.setPosition(RectangleEdge.BOTTOM);
            XYTitleAnnotation ta = new XYTitleAnnotation(0.98,0.02,legend,RectangleAnchor.BOTTOM_RIGHT);
            chart.getXYPlot().addAnnotation(ta);
        } else if (position.toLowerCase().equals("south")) {
            legend.setPosition(RectangleEdge.BOTTOM);
            XYTitleAnnotation ta = new XYTitleAnnotation(0.50,0.02,legend,RectangleAnchor.BOTTOM);
            chart.getXYPlot().addAnnotation(ta);
        } else if (position.toLowerCase().equals("southwest")) {
            legend.setPosition(RectangleEdge.BOTTOM);
            XYTitleAnnotation ta = new XYTitleAnnotation(0.02,0.02,legend,RectangleAnchor.BOTTOM_LEFT);
            chart.getXYPlot().addAnnotation(ta);
        } else if (position.toLowerCase().equals("west")) {
            legend.setPosition(RectangleEdge.LEFT);
            XYTitleAnnotation ta = new XYTitleAnnotation(0.02,0.50,legend,RectangleAnchor.LEFT);
            chart.getXYPlot().addAnnotation(ta);
        } else if (position.toLowerCase().equals("northwest")) {
            legend.setPosition(RectangleEdge.TOP);
            XYTitleAnnotation ta = new XYTitleAnnotation(0.02,0.98,legend,RectangleAnchor.TOP_LEFT);
            chart.getXYPlot().addAnnotation(ta);
        }
    }

    @SuppressWarnings("unused")
	public void legendON() {
        CheckExists();
        String position = "northeast" ;
        legend.setItemFont(font);
        legend.setBackgroundPaint(Color.WHITE);
        legend.setFrame(new BlockBorder(Color.BLACK));
        legend.setPosition(RectangleEdge.TOP);
        XYTitleAnnotation ta = new XYTitleAnnotation(0.98,0.98,legend,RectangleAnchor.TOP_RIGHT);
        chart.getXYPlot().addAnnotation(ta);
    }

    public void legendOFF(){
        chart.removeLegend();
        chart.getXYPlot().clearAnnotations();
    }

    //*********************************************************
    public void grid(String xAxis, String yAxis) {
        CheckExists();
        if (xAxis.equalsIgnoreCase("on")){
            chart.getXYPlot().setDomainGridlinesVisible(true);
            chart.getXYPlot().setDomainMinorGridlinesVisible(true);
            chart.getXYPlot().setDomainGridlinePaint(Color.GRAY);
        } else {
            chart.getXYPlot().setDomainGridlinesVisible(false);
            chart.getXYPlot().setDomainMinorGridlinesVisible(false);
        }
        if (yAxis.equalsIgnoreCase("on")){
            chart.getXYPlot().setRangeGridlinesVisible(true);
            chart.getXYPlot().setRangeMinorGridlinesVisible(true);
            chart.getXYPlot().setRangeGridlinePaint(Color.GRAY);
        } else {
            chart.getXYPlot().setRangeGridlinesVisible(false);
            chart.getXYPlot().setRangeMinorGridlinesVisible(false);
        }
    }


    //*********************************************************
    public void saveas(String fileName, int width, int height) {
        CheckExists();
        File file = new File(fileName);
        try {
            ChartUtilities.saveChartAsJPEG(file,this.chart,width,height);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveAsJPEG(String fileName, int width, int height) {
        CheckExists();
        File file = new File(fileName);
        try {
            ChartUtilities.saveChartAsJPEG(file, this.chart, width, height);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveAsPNG(String fileName, int width, int height) {
        CheckExists();
        File file = new File(fileName);
        try {
            ChartUtilities.saveChartAsPNG(file, this.chart, width, height);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveAsJPEG(int width, int height) {
        CheckExists();
        float quality = 1 ;
        FileChooserFX fc = new FileChooserFX() ;
        fc.setExtension("jpeg");
        fc.saveFile();
        File file = fc.getSelectedFile() ;
        try {
            ChartUtilities.saveChartAsJPEG(file, quality, this.chart, width, height);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveAsPNG(int width, int height) {
        CheckExists();
        FileChooserFX fc = new FileChooserFX() ;
        fc.setExtension("png");
        fc.saveFile();
        File file = fc.getSelectedFile() ;
        try {
            ChartUtilities.saveChartAsPNG(file, this.chart, width, height);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void saveAsSVG(int width, int height){
        CheckExists();
    	SVGGraphics2D g2 = new SVGGraphics2D(width, height);
    	chart.draw(g2, new Rectangle(width, height));
    	String svgElement = g2.getSVGElement() ;
        FileChooserFX fc = new FileChooserFX() ;
        fc.setExtension("svg");
        fc.saveFile();
        File file = fc.getSelectedFile() ;
        try {
			SVGUtils.writeToSVG(file, svgElement);
		} catch (IOException e) {
			e.printStackTrace();
		}

    }

    //***************Colors and Specs of the plot**************************

    public void FindColor(String spec, float lineWidth) {
        float[] dash = {5.0f};
        float[] dot = {lineWidth};
        Color color = Color.RED;                    // Default color is red
        Stroke stroke = new BasicStroke(lineWidth); // Default stroke is line
        if (spec.contains("-"))
            stroke = new BasicStroke(lineWidth);
        else if (spec.contains(":"))
            stroke = new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f);
        else if (spec.contains("."))
            stroke = new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 2.0f, dot, 0.0f);
        if (spec.contains("y"))
            color = Color.YELLOW;
        else if (spec.contains("m"))
            color = Color.MAGENTA;
        else if (spec.contains("c"))
            color = Color.CYAN;
        else if (spec.contains("r"))
            color = Color.RED;
        else if (spec.contains("g"))
            color = Color.GREEN;
        else if (spec.contains("b"))
            color = Color.BLUE;
        else if (spec.contains("k"))
            color = Color.BLACK;
        colors.add(color);
        strokes.add(stroke);
    }

    // color from java.awt
    public void FindColor(String spec, float lineWidth, Color color) {
        float[] dash = {5.0f};
        float[] dot = {lineWidth};
        Stroke stroke = new BasicStroke(lineWidth); // Default stroke is line
        if (spec.contains("-"))
            stroke = new BasicStroke(lineWidth);
        else if (spec.contains(":"))
            stroke = new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f);
        else if (spec.contains("."))
            stroke = new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 2.0f, dot, 0.0f);
        colors.add(color);
        strokes.add(stroke);
    }

    // color from javafx
    public void FindColor(String spec, float lineWidth, javafx.scene.paint.Color colorfx) {
        float[] dash = {5.0f};
        float[] dot = {lineWidth};
        Stroke stroke = new BasicStroke(lineWidth); // Default stroke is line
        if (spec.contains("-"))
            stroke = new BasicStroke(lineWidth);
        else if (spec.contains(":"))
            stroke = new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f);
        else if (spec.contains("."))
            stroke = new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 2.0f, dot, 0.0f);
        Color color = new Color((float) colorfx.getRed(), (float) colorfx.getGreen(), (float) colorfx.getBlue(), (float) colorfx.getOpacity()) ;
        colors.add(color);
        strokes.add(stroke);
    }

    public void setAllColors(javafx.scene.paint.Color colorfx){
    	Color color = new Color((float) colorfx.getRed(), (float) colorfx.getGreen(), (float) colorfx.getBlue(), (float) colorfx.getOpacity()) ;
    	int M = colors.size() ;
    	for(int i=0; i<M; i++){
    		colors.set(i, color) ;
    	}
    }

    public void setAllColors(Color color){
    	int M = colors.size() ;
    	for(int i=0; i<M; i++){
    		colors.set(i, color) ;
    	}
    }

    public void setSpecificChartColor(int datasetNumber, javafx.scene.paint.Color colorfx){
    	Color color = new Color((float) colorfx.getRed(), (float) colorfx.getGreen(), (float) colorfx.getBlue(), (float) colorfx.getOpacity()) ;
    	colors.set(datasetNumber, color) ;
    }

    public void setSpecificChartColor(int datasetNumber, Color color){
    	colors.set(datasetNumber, color) ;
    }

    public void setLastChartColor(javafx.scene.paint.Color colorfx){
    	Color color = new Color((float) colorfx.getRed(), (float) colorfx.getGreen(), (float) colorfx.getBlue(), (float) colorfx.getOpacity()) ;
    	int M = colors.size() ;
    	colors.set(M-1, color) ;
    }

    // setting up markers




    //*********************************************************
//    public void clear(){
//    	dataset.removeAllSeries();
//    }



    //*********************************************************
    public JFreeChart getRawChart(){
        return chart ;
    }

    public JFreeChart getChart(){
        return this.chart ;
    }

    public XYPlot getRawXYPlot(){
        return chart.getXYPlot() ;
    }

    public void run(){
        JFrame chartFrame = new JFrame() ;
        chartFrame.setSize(640, 450);
        chartFrame.getContentPane().add(new ChartPanel(chart)) ;
        Image image = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/People/Meisam/GUI/Plotters/MatlabPlotter/Extras/presentation.png"));
        chartFrame.setIconImage(image);
        chartFrame.setTitle("Plot Viewer v1.0");
        chartFrame.setVisible(true);
    }

    public void run(boolean systemExit){
        JFrame chartFrame = new JFrame() ;
        chartFrame.setSize(640, 450);
        chartFrame.getContentPane().add(new ChartPanel(chart)) ;
        Image image = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/People/Meisam/GUI/Plotters/MatlabPlotter/Extras/presentation.png"));
        chartFrame.setIconImage(image);
        chartFrame.setTitle("Plot Viewer v1.0");
        chartFrame.setVisible(true);
        if(systemExit){
        	chartFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }
    }

    public JFrame getChartJFrame(int width, int height){
        JFrame chartFrame = new JFrame() ;
        chartFrame.setSize(width, height);
        chartFrame.getContentPane().add(new ChartPanel(chart)) ;
        return chartFrame ;
    }

    public JFrame getChartJFrame(){
        JFrame chartFrame = new JFrame() ;
        chartFrame.setSize(640, 450);
        chartFrame.getContentPane().add(new ChartPanel(chart)) ;
        return chartFrame ;
    }

    public ChartPanel getChartPanel(){
        return new ChartPanel(chart) ;
    }

    public ChartPanel getChartPanel(int width, int height){
        ChartPanel panel = new ChartPanel(chart) ;
        panel.setPreferredSize(new Dimension(width, height));
        return panel ;
    }

    public SwingNode getChartSwingNode(){
        SwingNode chartSwingNode = new SwingNode() ;
        chartSwingNode.setContent(new ChartPanel(chart));
        return chartSwingNode ;
    }

    public SwingNode getChartSwingNode(int width, int height){
        SwingNode chartSwingNode = new SwingNode() ;
        chartSwingNode.resize(width, height);
        chartSwingNode.setContent(getChartPanel(width, height));
        return chartSwingNode ;
    }

    // export to MATLAB
    public void exportToMatlab() throws IOException{
        FXMLLoader loader = new FXMLLoader(Class.class.getResource("/People/Meisam/GUI/Utilities/ExportPlot/ExportToMATLAB/exportToMatlab.fxml")) ;
        WindowBuilder builder = new WindowBuilder(loader) ;
        builder.setIcon("/People/Meisam/GUI/Utilities/ExportPlot/ExportToMATLAB/Extras/MatlabIcons/Matlab_Logo.png");
        builder.build("Configure Export To Matlab", false);
        ExportToMatlabController controller = loader.getController() ;
        double[] x = {} ;
        double[] y = {} ;
        int M = dataset.getSeries(0).getItemCount() ;
        for(int i=0; i<M; i++){
        	x = MoreMath.Arrays.append(x, dataset.getSeries(0).getX(i).doubleValue()) ;
        	y = MoreMath.Arrays.append(y, dataset.getSeries(0).getY(i).doubleValue()) ;
        }
        SimulationVariable xVar = new SimulationVariable(getXLabel(), x) ;
        SimulationVariable yVar = new SimulationVariable(getYLabel(), y) ;
        controller.setVariables(xVar, yVar);
        controller.initialize();
    }

    // export to text file
    public void exportToFile(){
        double[] x = {} ;
        double[] y = {} ;
        int M = dataset.getSeries(0).getItemCount() ;
        for(int i=0; i<M; i++){
        	x = MoreMath.Arrays.append(x, dataset.getSeries(0).getX(i).doubleValue()) ;
        	y = MoreMath.Arrays.append(y, dataset.getSeries(0).getY(i).doubleValue()) ;
        }
        SimulationVariable xVar = new SimulationVariable(getXLabel(), x) ;
        SimulationVariable yVar = new SimulationVariable(getYLabel(), y) ;
        ExportVariables exp = new ExportVariables(xVar, yVar) ;
        exp.export();
    }

    public void exportToFile(SimulationDataBase simDataBase){
        SimulationVariable xVar = simDataBase.getVariableFromAlias(getXLabel()) ;
        SimulationVariable yVar = simDataBase.getVariableFromAlias(getYLabel()) ;
        ExportVariables exp = new ExportVariables(xVar, yVar) ;
        exp.export();
    }


    //*********************************************************

}