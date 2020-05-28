package People.Meisam.GUI.PhoenixSim.TabsLibrary.SST_simulation;

import People.Meisam.GUI.PhoenixSim.TabsLibrary.AbstractTabController;
import People.Meisam.GUI.Plotters.MatlabPlot.MatlabChart;
import People.Meisam.GUI.Utilities.SimulationDataBase;
import People.Rui.Extension.OptSimExtension.PropertyTableData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import org.controlsfx.control.StatusBar;

import java.io.*;

import static java.lang.Math.floor;

/**
 * @author kristoff
 */
public class SSTTabController extends AbstractTabController {
    @FXML
    Pane pane;
    @FXML
    StatusBar statusBar;
    @FXML
    Tab tab;
    @FXML
    Slider paramSlider1;
    @FXML
    Slider paramSlider2;
    @FXML
    Slider paramSlider3;
    @FXML
    TableView<PropertyTableData> paramTable;
    @FXML
    TableColumn<String, String> paramTableColumn1;
    @FXML
    TableColumn<String, String> paramTableColumn2;
    @FXML
    Button generateButton;
    @FXML
    Button runButton;
    @FXML
    Text textParam1;
    @FXML
    Text textParam2;
    @FXML
    Text textParam3;

    SimulationDataBase simulationDataBase;
    int stopAtCycle;
    double timebase;
    int count;
    private ObservableList<PropertyTableData> paramData = FXCollections.observableArrayList();

    @Override
    public void initialize() {
        paramSlider1.setShowTickLabels(true);
        paramSlider1.setMin(0);
        paramSlider1.setMax(10000);
        paramSlider1.setMajorTickUnit(1000);

        paramSlider2.setShowTickLabels(true);
        paramSlider2.setMin(0);
        paramSlider2.setMax(10);
        paramSlider2.setMajorTickUnit(2);

        paramSlider3.setShowTickLabels(true);
        paramSlider3.setMin(0);
        paramSlider3.setMax(100000);
        paramSlider3.setMajorTickUnit(5000);

        paramTableColumn1.setCellFactory(TextFieldTableCell.forTableColumn());

        // the string here should be the same as the variable name
        paramTableColumn1.setCellValueFactory(new PropertyValueFactory<>("propertyName"));

        paramTableColumn2.setCellFactory(TextFieldTableCell.forTableColumn());
        paramTableColumn2.setCellValueFactory(new PropertyValueFactory<>("propertyValue"));

        paramTable.setItems(paramData);

        updateTab();

    }

    public int getParamOne() {
        return (int) floor(paramSlider1.getValue());
    }

    public double getTimebase() {
        return paramSlider2.getValue();
    }

    public int getCount() {
        return ((int) Math.floor(paramSlider3.getValue()));
    }

    public void updateTab() {
        textParam1.setText(String.format("Timebase: %d", getParamOne()));
        textParam2.setText(String.format("Stop at cycle: %.1f", paramSlider2.getValue()));
        textParam3.setText(String.format("Count: %d", getCount()));
    }

    @Override
    public Tab getTab() {
        return tab;
    }

    @Override
    public MatlabChart getFig() {
        return null;
    }

    public void demoGenerateSSTConfigFile() throws IOException {
        this.stopAtCycle = getParamOne();
        this.timebase = getTimebase();
        this.count = getCount();
        String toWrite = "# Automatically generated SST Python input\n" +
                "import sst\n" +
                "\n" +
                "# Define SST core options\n" +
                "sst.setProgramOption(\"timebase\", \"" + timebase + " ps\")\n" +
                "sst.setProgramOption(\"stopAtCycle\", \"" + stopAtCycle + " s\")\n" +
                "\n" +
                "# Define the simulation components\n" +
                "comp_clocker0 = sst.Component(\"clocker0\", \"simpleElementExample.simpleRNGComponent\")\n" +
                "comp_clocker0.addParams({\n" +
                "      \"count\" : \"\"\"" + count + "\"\"\",\n" +
                "      \"seed\" : \"\"\"1447\"\"\",\n" +
                "      \"verbose\" : \"1\",\n" +
                "      \"rng\" : \"\"\"mersenne\"\"\"\n" +
                "})\n" +
                "\n" +
                "\n" +
                "# Define the simulation links\n" +
                "# End of generated output.";

        BufferedWriter writer = new BufferedWriter(new FileWriter("test1.py"));
        writer.write(toWrite);

        writer.close();
    }

    public void demoRunSSTConfig() {
        try {
            Process p = Runtime.getRuntime().exec("sst " + System.getProperty("user.dir") +
                    "/test1.py");

            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(p.getInputStream()));

            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(p.getErrorStream()));

            // read the output from the command
            String s;

            System.out.println("SST simulation output:\n");
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }

            // read any errors from the attempted command
            System.out.println("Error message(s):\n");
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }

            new Alert(Alert.AlertType.NONE, "Success!", ButtonType.OK).showAndWait();
        } catch (
                IOException e) {
            System.out.println("exception happened - here's what I know: ");
            e.printStackTrace();
            System.exit(-1);
            new Alert(Alert.AlertType.ERROR, "Exception occurred during file IO.", ButtonType.OK).showAndWait();
        }
    }


    @Override
    public StatusBar getStatusBar() {
        return statusBar;
    }

    @Override
    public void setSimDataBase(SimulationDataBase simDataBase) {
        this.simulationDataBase = simDataBase;
    }

    @Override
    public SimulationDataBase getSimDataBase() {
        return simulationDataBase;
    }

    @Override
    public void generateGDS() {

    }
}
