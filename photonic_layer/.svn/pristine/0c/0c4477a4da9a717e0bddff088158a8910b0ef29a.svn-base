package People.Meisam.Tests;

import java.io.File;
import java.nio.file.Paths;

import People.Meisam.GUI.Builders.PDFLoader;
import javafx.application.Application;
import javafx.stage.Stage;

public class TestPDFLoader extends Application {

	public static void main(String[] args){
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
//		String path = "/Users/meisam/Desktop/Javanco_env/PhotonicLayer/src/People/Meisam/GUI/PhoenixSim/documentations/Extraction of Coupling Coefficients.pdf" ;
//		Path path = Paths.get("/GUI/PhoenixSim/documentations/Extraction of Coupling Coefficients.pdf") ;
//		Path path = Paths.get(URI.create("file:///Users/meisam/Desktop/Javanco_env/PhotonicLayer/src/People/Meisam/GUI/PhoenixSim/documentations/Extraction of Coupling Coefficients.pdf"));
//		File file = new File("/People/Meisam/GUI/PhoenixSim/documentations/Extraction of Coupling Coefficients.pdf") ;

//		ClassLoader classLoader = getClass().getClassLoader();
//        File file = new File(Object.class.getClass().getClassLoader().getResource("/People/Meisam/GUI/PhoenixSim/documentations/ExtractionofCouplingCoefficients.pdf"));
        File file = Paths.get("../PhotonicLayer/src/People/Meisam/GUI/PhoenixSim/documentations/ExtractionofCouplingCoefficients.pdf").toFile() ;
//		PDFLoader.loadPDF("../PhotonicLayer/src/People/Meisam/GUI/PhoenixSim/documentations/ExtractionofCouplingCoefficients.pdf");
		PDFLoader.loadPDF(file);
	}

}
