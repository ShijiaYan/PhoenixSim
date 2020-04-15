package People.Meisam.GUI.Plotters.MatlabPlotter.Tests;

import People.Meisam.GUI.Plotters.MatlabPlot.MatlabChart;
import PhotonicElements.Utilities.MathLibraries.MoreMath;

public class TestMatlatPlotClipboard {

	public static void main(String[] args){
	    // Create some sample data
	    double[] x = MoreMath.linspace(-10, 10, 100) ;
	    double[] y1 = MoreMath.Arrays.Functions.sin(x) ;
	    double[] y2 = MoreMath.Arrays.Functions.asinh(x) ;
	    // create and render plot
	    MatlabChart fig = new MatlabChart() ;
	    fig.plot(x, y1, "r-");
	    fig.plot(x, y2, "b-");
	    fig.RenderPlot();
	    fig.markerON();
	    fig.run(true);
	    // create and copy to clipboard
//	    CustomChartPanel cpanel = new CustomChartPanel(fig.getChart(), 640, 450, 640, 450, 640, 450, true, true, true, true, true, true) ;
//	    JFrame frame = new JFrame() ;
//	    frame.add(cpanel) ;
//	    frame.setSize(640, 450);
//	    frame.setVisible(true);

	}


}
