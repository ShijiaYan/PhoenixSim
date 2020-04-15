package People.Meisam.GUI.Plotters.MatlabPlotter.Tests;

import PhotonicElements.Utilities.MathLibraries.MoreMath;
import org.jfree.chart.ChartPanel;

import People.Meisam.GUI.Plotters.MatlabPlot.MatlabChart;

import javax.swing.*;

/**
 * Created by meisam on 6/29/17.
 */
public class MatlabPlotterTest extends JFrame {


    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	private MatlabPlotterTest(){

        MatlabChart fig = new MatlabChart() ;

        double[] x = MoreMath.linspace(-10, 10, 100) ;
        double[] y = MoreMath.Arrays.Functions.abs(x) ;

        fig.plot(x, y, "-r", 3.0f, "sdf");
        fig.RenderPlot();
        fig.xlabel("wavelength (nm)");
        fig.ylabel("Transmission (dB)");
//        fig.saveas("test.jpeg",640, 450);
        ChartPanel panel = new ChartPanel(fig.getChart()) ;
        getContentPane().add(panel) ;
    }


    public static void main(String[] args){
        MatlabPlotterTest plot = new MatlabPlotterTest() ;
        plot.setVisible(true);
    }





}
