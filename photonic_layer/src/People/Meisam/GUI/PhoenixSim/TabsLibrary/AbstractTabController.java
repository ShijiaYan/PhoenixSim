package People.Meisam.GUI.PhoenixSim.TabsLibrary;

import org.controlsfx.control.StatusBar;
import People.Meisam.GUI.Plotters.MatlabPlot.MatlabChart;
import People.Meisam.GUI.Utilities.SimulationDataBase;
import javafx.embed.swing.SwingNode;
import javafx.scene.control.Tab;
import javafx.scene.layout.Pane;

public abstract class AbstractTabController {

	public abstract void initialize() ;
	public abstract Tab getTab() ;
	public abstract MatlabChart getFig() ;
	public abstract StatusBar getStatusBar() ;
	public abstract void setSimDataBase(SimulationDataBase simDataBase) ;
	public abstract SimulationDataBase getSimDataBase() ;
	public abstract void generateGDS() ;

	public SwingNode swingNode = new SwingNode() ;

	protected void showPlot(MatlabChart fig, Pane pane){
		  int width = 500 , height = 400 ;
		  swingNode.setContent(fig.getChartPanel(width, height));
		  swingNode.resize(width, height);
		}

//	protected void showPlot(MatlabChart fig, Pane pane){
//	  int width = (int) pane.getWidth(), height = (int) pane.getHeight() ;
//	  swingNode.setContent(fig.getChartPanel(width, height));
//	  swingNode.resize(width, height);
//	  pane.setPrefSize((double) width, (double) height);
//
//	  pane.layoutBoundsProperty().addListener((v, oldValue, newValue) -> {
//    	pane.getChildren().remove(fig.getChartSwingNode()) ;
//        int newWidth = (int) newValue.getWidth() ;
//        int newHeight = (int) newValue.getHeight() ;
//
//	  	swingNode.setContent(fig.getChartPanel(newWidth, newHeight));
//	  	swingNode.resize(newWidth, newHeight);
//	  	pane.setPrefSize((double) newWidth, (double) newHeight);
//	  });
//
//	}

//	protected void showPlot(MatlabChart fig, Pane pane){
//		if(PhoenixSimModuleController.fullScreenEnabled){
//			  int width = (int) pane.getWidth(), height = (int) pane.getHeight() ;
//			  swingNode.setContent(fig.getChartPanel(width, height));
//			  swingNode.resize(width, height);
//			  pane.setPrefSize((double) width, (double) height);
//
//			  pane.layoutBoundsProperty().addListener((v, oldValue, newValue) -> {
//		    	pane.getChildren().remove(fig.getChartSwingNode()) ;
//		        int newWidth = (int) newValue.getWidth() ;
//		        int newHeight = (int) newValue.getHeight() ;
//
//			  	swingNode.setContent(fig.getChartPanel(newWidth, newHeight));
//			  	swingNode.resize(newWidth, newHeight);
//			  	pane.setPrefSize((double) newWidth, (double) newHeight);
//			  });
//		}
//		else{
//			  int width = 500 , height = 400 ;
//			  swingNode.setContent(fig.getChartPanel(width, height));
//			  swingNode.resize(width, height);
//		}
//	}



}
