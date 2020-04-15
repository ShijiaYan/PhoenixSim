package People.Meisam.GUI.Plotters.MatlabPlotter.Tests;

import People.Meisam.GUI.Plotters.MatlabPlot.MatlabChart;
import PhotonicElements.Utilities.MathLibraries.MoreMath;

public class DemoMatlabPlotter {

    public static void main(String[] args) {

        // Create some sample data
        double[] x = MoreMath.linspace(-10, 10, 100) ;
        double[] y1 = MoreMath.Arrays.Functions.sin(x) ;
        double[] y2 = MoreMath.Arrays.Functions.asinh(x) ;

        // JAVA:                             // MATLAB:
        MatlabChart fig = new MatlabChart(); // figure('Position',[100 100 640 480]);
        fig.plot(x, y1, "-r", 2.0f, "AAPL"); // plot(x,y1,'-r','LineWidth',2);
        fig.plot(x, y2, ":k", 3.0f, "BAC");  // plot(x,y2,':k','LineWidth',3);
        fig.RenderPlot();                    // First render plot before modifying
        fig.title("Stock 1 vs. Stock 2");    // title('Stock 1 vs. Stock 2');
//        fig.xlim(10, 100);                   // xlim([10 100]);
//        fig.ylim(200, 300);                  // ylim([200 300]);
        fig.xlabel("Days");                  // xlabel('Days');
        fig.ylabel("Price");                 // ylabel('Price');
//        fig.legend("northeast");             // legend('AAPL','BAC','Location','northeast')
//        fig.font("Helvetica",15);            // .. 'FontName','Helvetica','FontSize',15
//        fig.saveas("MyPlot.jpeg",640,480);   // saveas(gcf,'MyPlot','jpeg');

        fig.legendON();
        fig.setFontSize(15);
//        fig.titleOFF();
//        fig.legendOFF();
//        fig.run();
        fig.legendOFF();
//        fig.legendON();
//        fig.markerOFF();
        fig.markerON();
//        fig.setFigColor(0, Color.CYAN);
//        fig.setFigColor(1, Color.ORANGE);
        fig.setFigLineWidth(0, 5f);
        fig.setFigLineWidth(1, 0.5f);
//        fig.setFigLineStyle(1, "-");
        fig.setFigLineStyle(0, ":");
        fig.setFigLineStyle(1, ":");
        fig.run(true);
//        fig.clear();
//        fig.run(true);
//        fig.clear();
//        fig.plot(x, y1);
//        fig.RenderPlot();
//        fig.run(true);

    }
}