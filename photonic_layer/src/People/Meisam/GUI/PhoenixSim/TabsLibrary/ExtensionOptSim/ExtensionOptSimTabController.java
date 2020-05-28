package People.Meisam.GUI.PhoenixSim.TabsLibrary.ExtensionOptSim;

import People.Meisam.GUI.PhoenixSim.TabsLibrary.AbstractTabController;
import People.Meisam.GUI.Plotters.MatlabPlot.MatlabChart;
import People.Meisam.GUI.Utilities.SimulationDataBase;
import People.Rui.Extension.OptSimExtension.OptSimLinkSystem;
import People.Rui.Extension.OptSimExtension.OptSimProperty;
import People.Rui.Extension.OptSimExtension.PropertyTableData;
import People.Rui.Extension.OptSimExtension.PowerPenaltyTableData;
import ch.epfl.javanco.base.Javanco;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import org.controlsfx.control.StatusBar;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.Properties;

/**
 * @author Rui
 */
public class ExtensionOptSimTabController extends AbstractTabController {

    /* Copy these when create a new tab */
    StatusBar statusBar = new StatusBar();
    SimulationDataBase simDataBase = new SimulationDataBase();
    MatlabChart fig = new MatlabChart();

    int num, changedIndex;

    OptSimLinkSystem optSimLinkSystem;

    String pathway, changedValue;
    String filename;
    String optSimPathway;
    int flag;
    private int selectedComponent;
    private int selectedIndex;
    ArrayList<PowerPenaltyTableData> ppDataList = new ArrayList<>();

    private ObservableList<PropertyTableData> data = FXCollections.observableArrayList();

    private ObservableList<PowerPenaltyTableData> powerPenaltyData = FXCollections.observableArrayList();

    private ObservableList<String> componentList = FXCollections.observableArrayList();
    // To change data when edit the table cell, an observableList should be set up

    @Override
    protected void showPlot(MatlabChart fig, Pane pane) {

    }

    /* remember to copy these to method too */
    @Override
    public void setSimDataBase(SimulationDataBase simDataBase) {
        this.simDataBase = simDataBase;
    }

    @Override
    public SimulationDataBase getSimDataBase() {
        return simDataBase;
    }

    @FXML
    private Button generateFileButton;

    @FXML
    private Tab ExtensionOptSimTab;

    @FXML
    private TextField channelNumber;

    @FXML
    private Button setChannelNumberButton;

    @FXML
    private Label ringNumberLabel;

    @FXML
    private Label ringParameter;

    @FXML
    private ComboBox<String> componentComboBox;

    @FXML
    private ComboBox<String> componentIndexComboBox;

    @FXML
    private Pane plotPane;

    @FXML
    private TableView<PropertyTableData> table;

    @FXML
    private TableColumn<PropertyTableData, String> propertyColumn;

    @FXML
    private TableColumn<PropertyTableData, String> valueColumn;

    @FXML
    private ImageView imageView;

    @FXML
    private Button runOptSimButton;

    @FXML
    private Button ppCalculationButton;

    @FXML
    private TableView<PowerPenaltyTableData> calculationTable;

    @FXML
    private TableColumn<PowerPenaltyTableData, String> wavelengthColumn;

    @FXML
    private TableColumn<PowerPenaltyTableData, String> ppColumn;

    @FXML
    private TextField pathwayField;

    @FXML
    private Button setPathwayButton;

    @FXML
    private Text textPathway;

    @FXML
    private Button choosePathwayButton;

    /************************ Methods ***************************/

    @FXML
    void setFilePathway(ActionEvent event) {
        setPathway(pathwayField.getText());
        textPathway.setText(pathway);
    }

    @FXML
    void chooseFilePathway(ActionEvent event) {
        /* code to generate a file chooser and get the pathway of a folder */
        DirectoryChooser fileChooser = new DirectoryChooser();
        fileChooser.setTitle("Open Resource File");
//        System.out.println(setPathwayButton.getScene());
        File openedFolder = fileChooser.showDialog(setPathwayButton.getScene().getWindow());
        pathwayField.setText(openedFolder.getAbsolutePath());
    }

    @FXML
    void setChannelNumber(ActionEvent event) {
        String number = channelNumber.getText();
        num = Integer.parseInt(number);

        /* Generate a link after we set number of channels */
        optSimLinkSystem = new OptSimLinkSystem(num);

        /* Generate a list of components after setting up the system */
        setItemComboBox(optSimLinkSystem.getComponentsName());
        componentComboBox.getItems().addAll(componentList);
        componentComboBox.getSelectionModel().select(0); // set default to global parameter setting
        setFlag(0);
    }

    @FXML
        /* Based on component, we list the index */
    void setIndexComboBox(ActionEvent event) {
        /* we always clear the combo box first */
        componentIndexComboBox.getItems().clear();

        /* single components, set single index 0, and pass the property to table */
        if (componentComboBox.getValue().equals("global param") || componentComboBox.getValue().equals("Mux")
                || componentComboBox.getValue().equals("Fiber")) {
            componentIndexComboBox.getItems().add("0");

            /* show parameters in table here if not a component array */
            switch (componentComboBox.getSelectionModel().getSelectedItem()) {
                case "global param":
                    setSelectedComponent(0);
                    populate(optSimLinkSystem.globalParameters.rootProperty.property);
                    break;
                case "Mux":
                    setSelectedComponent(1);
                    populate(optSimLinkSystem.optMuxComponent.rootProperty.property);
                    break;
                case "Fiber":
                    setSelectedComponent(4);
                    populate(optSimLinkSystem.nonlinearFiberComponent.rootProperty.property);
                    break;
                default:
                    break;
            }
        }

        /* otherwise we add "0~number-1" */
        else {
            for (int i = 0; i < num; i++)
                componentIndexComboBox.getItems().add(Integer.toString(i));
        }

        /* give Index a default value of 0 */
        componentIndexComboBox.getSelectionModel().select(0);
    }

    private void setSelectedComponent(int i) {
        selectedComponent = i;
    }

    @FXML
    void setComponentInTable(ActionEvent event) {
        if (componentComboBox.getValue().equals("global param") || componentComboBox.getValue().equals("Mux")
                || componentComboBox.getValue().equals("Fiber")) {
            table.setItems(data);
        } else {
            /* get the value */
            // this ? : sentence here is
            // trying to get rid of a null pointer exception.
            int index = Integer.parseInt(componentIndexComboBox.getSelectionModel().getSelectedItem() != null
                    ? componentIndexComboBox.getSelectionModel().getSelectedItem()
                    : "0");
            switch (componentComboBox.getSelectionModel().getSelectedItem()) {
                case "CWLasers":
                    setSelectedComponent(2);
                    populate(optSimLinkSystem.cwlasers.get(index).rootProperty.property);
                    break;
                case "Ring Mods":
                    setSelectedComponent(3);
                    populate(optSimLinkSystem.ringModulatorComponents.get(index).rootProperty.property);
                    break;
                case "Ring Res":
                    setSelectedComponent(5);
                    populate(optSimLinkSystem.ringResonatorComponents.get(index).rootProperty.property);
                    break;
                case "Signal Generator":
                    setSelectedComponent(6);
                    populate(optSimLinkSystem.signalGeneratorComponents.get(index).rootProperty.property);
                    break;
                case "Drivers":
                    setSelectedComponent(7);
                    populate(optSimLinkSystem.driverComponents.get(index).rootProperty.property);
                    break;
                case "Spec Analyzer":
                    setSelectedComponent(8);
                    populate(optSimLinkSystem.spectrumAnalyzerComponents.get(index).rootProperty.property);
                    break;
                default:
                    break;
            }
            table.setItems(data);
        }
    }

    @FXML
    void generateFile(ActionEvent event) {
        try (PrintWriter out = new PrintWriter(
                new BufferedWriter(new FileWriter(pathway + "/" + filename + ".moml", false)))) {
            out.println(optSimLinkSystem.writeFileString);
        } catch (IOException e) {
            e.printStackTrace();
        }
        /* Typical way to create a dialog */
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("PheonixSim");
        alert.setHeaderText(null);
        alert.setContentText("OptSim file has been generated at\n" + pathway + "\n");

        alert.showAndWait();
    }

    @FXML
    void calculatePP(ActionEvent event) throws IOException {
        String line;
        String[] columns;
        ArrayList<Double> power = new ArrayList<>();
        ArrayList<Double> frequency = new ArrayList<>();
        ArrayList<Double> maxPower = new ArrayList<>();
        ArrayList<Double> resWavelength = new ArrayList<>();
        Double ppDouble, inputPower;

        // get the file position
        // check file exist, if not return error
        // else output each channel

        if (flag == 0) {
            // error message
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("You must generate the file and run it first!\n Use \"run\"button");
            alert.showAndWait();
        } else {
            ppDataList.clear();
            for (int i = 0; i < num; i++) {
                frequency.clear();
                power.clear();
                BufferedReader br = new BufferedReader(
                        new FileReader(pathway + "/lstmpSpec" + i + ".frq"));
                while ((line = br.readLine()) != null) {
                    columns = line.split(" ");
                    frequency.add(Double.parseDouble(columns[0]));
                    power.add(Double.parseDouble(columns[1]));
                }
                br.close();

                maxPower.add(Collections.max(power));
                resWavelength.add(frequency.get(power.indexOf(maxPower.get(i))));
                inputPower = Double
                        .parseDouble(optSimLinkSystem.cwlasers.get(i).rootProperty.property.get(1).getValue());
                ppDouble = -10 * Math.log(maxPower.get(i) / inputPower);
                // System.out.println(ppDouble);
                ppDataList.add(new PowerPenaltyTableData(resWavelength.get(i) * 1E6, ppDouble));
                // System.out.println(ppDataList.get(i).getPowerPenalty());
            }

            /* now we have all the resonance wavelengths and powers in position */
            /* a good idea would be show them all in a table */
            setPowerPenaltyTable(ppDataList);
            calculationTable.setItems(powerPenaltyData);
            // System.out.println(powerPenaltyData.get(0).getPowerPenalty());

        }
    }

    @FXML
        /* To run this code, one has to make sure that have resources.bat in position */
    void runFile(ActionEvent event) throws IOException, InterruptedException {

        /* first generate the file */
        try (PrintWriter out = new PrintWriter(
                new BufferedWriter(new FileWriter(pathway + "/" + filename + ".moml", false)))) {
            out.println(optSimLinkSystem.writeFileString);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // show a dialog here, and based on users choice, continue or stop here
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("PheonixSim");
        alert.setHeaderText(null);
        alert.setContentText("You OptSim file has generated at\n" + "\t" + pathway + "/" + filename + ".moml.\n"
                + "Do you want to continue autorun the file?");
        ButtonType buttonTypeOne = new ButtonType("Yes!");
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeCancel);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == buttonTypeOne) {
                /* we copy the file bin/resources.bat to setup the running environment */
                File source = new File(optSimPathway + "/resources.bat");
                File dest = new File(optSimPathway + "/runOptSim.bat");

                // copy file using FileStreams
                copyFileUsingFileStreams(source, dest);

                /* add two lines to run optsim file */
                try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(dest, true)))) {
                    out.println("");
                    out.println("SET SimFilePath=\"" + pathway + "/" + filename + ".moml\"");
                    out.println("jetOptSim -noGUI %SimFilePath%\n");
                    out.println("cmd /k");
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                /* use runtime run the .bat file to open the file */
                Runtime runtime = Runtime.getRuntime();
                try {
                    Process process = runtime.exec("cmd /c start " + optSimPathway + "/runOptSim.bat");
                    process.waitFor();
                } catch (IOException ioException) {
                    System.out.println(ioException.getMessage());
                }
                setFlag(1);
            }
        } else {
            throw new NullPointerException("Result is not present.");
        }
    }

    @Override
    public void initialize() {
        setSelectedComponent(0);
        setSelectedIndex(0);
        setFlag(0);
        setPathway("/Desktop/test");
        setFileName("Link_System");

        try {
            Properties props = new Properties();
            String basePath = System.getProperty("user.dir")
                    + (Javanco.isInJar() ? "/tmp" : "/src/People/Rui/Extension/OptSimExtension");
            InputStream inputStream = new FileInputStream(basePath + "/optsim_pathway.txt");
            System.out.println("Loading OptSim pathway file in:\n\t" + basePath);
            props.load(inputStream);
            String optSimPathWay = props.getProperty("OptSimPathway");
            setOptSimPathway(optSimPathWay);
            System.out.println("OptSim pathway successfully set and saved:\n\t" + optSimPathWay);
        } catch (IOException e1) {
            e1.printStackTrace();
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("PheonixSim Error");
            alert.setHeaderText(null);
            alert.setContentText("Set your Optsim patyway in\n extension->setting->optsimpathway ");
            alert.showAndWait();
        }
        System.out.println(optSimPathway + "/resources.bat");

        textPathway.setText(pathway);
        table.setEditable(true);
        calculationTable.setEditable(false);

        propertyColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        // the string here should be the same as the variable name
        propertyColumn.setCellValueFactory(new PropertyValueFactory<>("propertyName"));

        valueColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        valueColumn.setCellValueFactory(new PropertyValueFactory<>("propertyValue"));
        valueColumn.setOnEditCommit((TableColumn.CellEditEvent<PropertyTableData, String> t) -> {
                    TablePosition<PropertyTableData, String> pos = t.getTablePosition();
                    changedValue = t.getNewValue();
                    changedIndex = pos.getRow();
                    t.getTableView().getItems().get(changedIndex).setPropertyValue(changedValue);
                    data.get(changedIndex).setPropertyValue(changedValue);
                    System.out.println("Commit successfully");
                    setSelectedIndex(Integer.parseInt(componentIndexComboBox.getSelectionModel().getSelectedItem() != null
                            ? componentIndexComboBox.getSelectionModel().getSelectedItem()
                            : "0"));
                    table.setItems(data);
                    optSimLinkSystem.setEditedProperty(selectedComponent, selectedIndex, changedIndex, changedValue);
                    optSimLinkSystem.setWriteFileString();
                }
                /*
                 * simpler version, but can't retrieve more data. But this can't really change the value in the
                 * observable list?
                 * (t.getTableView().getItems().get(changedIndex = t.getTablePosition().getRow())).
                 * setPropertyValue(changedValue = t.getNewValue())
                 */

        );

        /* set power penalty table */
        wavelengthColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        // the string here should be the same as the variable name
        wavelengthColumn.setCellValueFactory(new PropertyValueFactory<>("wavelength"));

        ppColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        ppColumn.setCellValueFactory(new PropertyValueFactory<>("powerPenalty"));
    }

    private void populate(ArrayList<OptSimProperty> property) {
        data.clear();
        property.forEach(p -> data.add(new PropertyTableData(p)));
    }

    private void setItemComboBox(ArrayList<String> property) {
        componentList.clear();
        componentList.addAll(property);
    }

    private void setPowerPenaltyTable(ArrayList<PowerPenaltyTableData> ppData) {
        powerPenaltyData.clear();
        powerPenaltyData.addAll(ppData);
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    @Override
    public StatusBar getStatusBar() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void generateGDS() {
    }

    @Override
    public MatlabChart getFig() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Tab getTab() {
        return ExtensionOptSimTab;
    }

    public void setPathway(String pathway) {
        this.pathway = pathway;
    }

    public void setFileName(String fileName) {
        this.filename = fileName;
    }

    public void setOptSimPathway(String pathway) {
        this.optSimPathway = pathway;
    }

    public void setSelectedIndex(int index) {
        this.selectedIndex = index;
    }

    public int getSelectedIndex() {
        return this.selectedIndex;
    }

    public int getSelectedComponent() {
        return this.selectedComponent;
    }

    private static void copyFileUsingFileStreams(File source, File dest) throws IOException {
        try (InputStream input = new FileInputStream(source); OutputStream output = new FileOutputStream(dest)) {
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(buf)) > 0) {
                output.write(buf, 0, bytesRead);
            }
        }
    }

    static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

}
