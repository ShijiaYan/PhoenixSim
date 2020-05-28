package People.Meisam.GUI.PhoenixSim.TabsLibrary.ExtensionOptSim.ExtensionPDK;

import People.Meisam.GUI.PhoenixSim.TabsLibrary.AbstractTabController;
import People.Meisam.GUI.Plotters.MatlabPlot.MatlabChart;
import People.Meisam.GUI.Utilities.SimulationDataBase;
import People.Rui.Extension.OptSimExtension.*;
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

// When apply change to the table, we should change both elements in project and compound component


public class ExtensionPDKDemuxTabController extends AbstractTabController {

    StatusBar statusBar = new StatusBar();
    SimulationDataBase simDataBase = new SimulationDataBase();
    MatlabChart fig = new MatlabChart();
    String workgroupDir;

    int num, changedIndex, test;

    PDKReceiver pdkReceiver = new PDKReceiver(num);
    PDKReceiverCC pdkReceiverCC = new PDKReceiverCC(num);
    String pathway, changedValue;
    String filename;
    String optSimPathway;
    int flag; // to determine whether the file has been generated and run
    private int selectedComponent;
    private int selectedIndex;

    ArrayList<PowerPenaltyTableData> ppDataList = new ArrayList<>();

    private ObservableList<PropertyTableData> data = FXCollections.observableArrayList();

    private ObservableList<PowerPenaltyTableData> powerPenaltyData = FXCollections.observableArrayList();

    private ObservableList<String> componentList = FXCollections.observableArrayList();
    // To change data when edit the table cell, an observableList should be set up

    @FXML
    private Tab ExtensionPDKDemuxTab;

    @FXML
    private TextField channelNumber;

    @FXML
    private Button setChannelNumberButton;

    @FXML
    private Button setWorkgroupButton;

    @FXML
    private ComboBox<String> ComponentComboBox;

    @FXML
    private ComboBox<String> ComponentIndexComboBox;

    @FXML
    private TextField pathwayField;

    @FXML
    private Button chooseFileButton;

    @FXML
    private Button setPathwayButton;

    @FXML
    private ImageView imageView;

    @FXML
    private Button generateFileButton;

    @FXML
    private Button runFileButton;

    @FXML
    private Pane plotPane;

    @FXML
    private TableView<PropertyTableData> table;

    @FXML
    private TableColumn<PropertyTableData, String> propertyColumn;

    @FXML
    private TableColumn<PropertyTableData, String> valueColumn;

    @FXML
    private Text pathwayText;

    @FXML
    private TableView<PowerPenaltyTableData> calculationTable;

    @FXML
    private TableColumn<PowerPenaltyTableData, String> wavelengthColumn;

    @FXML
    private TableColumn<PowerPenaltyTableData, String> ppColumn;

    @FXML
    private Button ppCalculationButton;

    @FXML
    private Button generateCompoundComponentButton;

    @FXML
    void generateCC(ActionEvent event) throws IOException, InterruptedException {
        try (PrintWriter out = new PrintWriter(
                new BufferedWriter(new FileWriter(pathway + "/" + filename + "_CC.moml", false)))) {
            out.println(pdkReceiverCC.writeFileString);
        } catch (IOException e) {
            System.err.println(e);
        }
        /* Typical way to create a dialog */
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("PheonixSim");
        alert.setHeaderText(null);
        alert.setContentText("OptSim Compound Component file has been generated at\n" + pathway + "\n");

        alert.showAndWait();
        File source = new File(optSimPathway + "/resources.bat");
        File dest = new File(optSimPathway + "/runOptSim.bat");

        // copy file using FileStreams
        copyFileUsingFileStreams(source, dest);

        /* add two lines to run optsim file */
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(dest, true)))) {
            out.println("\n");
            out.println("SET SimFilePath=\"" + pathway + "/" + filename + "_CC.moml\"");
            out.println("SET OPTSIM_PDAFLOW_PDK_ROOT=" + workgroupDir);
            // out.println("jetOptSim %SimFilePath%\n"); You can check if you want
            out.println("OptoDesigner %SimFilePath%\n");
            out.println("cmd /k");
        } catch (IOException e1) {
            System.err.println(e1);
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

    @FXML
    void setFilePathway(ActionEvent event) {
        setPathway(pathwayField.getText());
        pathwayText.setText(pathway);
    }

    @FXML
    void chooseFilePathway(ActionEvent event) {
        /* code to generate a file chooser and get the pathway of a folder */
        DirectoryChooser fileChooser = new DirectoryChooser();
        fileChooser.setTitle("Open Resource File");
        System.out.println(setPathwayButton.getScene());
        File openedFolder = fileChooser.showDialog(setPathwayButton.getScene().getWindow());
        pathwayField.setText(openedFolder.getAbsolutePath());
    }

    @FXML
    void setChannelNumber(ActionEvent event) {
        String number = channelNumber.getText();
        num = Integer.parseInt(number);

        /* Generate a link after we set number of channels */
        pdkReceiver = new PDKReceiver(num);
        pdkReceiverCC = new PDKReceiverCC(num);

        /* Generate a list of components after setting up the system */
        setItemComboBox(pdkReceiver.getComponentsName());
        ComponentComboBox.getItems().addAll(componentList);
        ComponentComboBox.getSelectionModel().select(0); // set default to global parameter setting
        setFlag(0);
    }

    @FXML
        /* Based on component, we list the index */
    void setIndexComboBox(ActionEvent event) {
        /* we always clear the combo box first */
        ComponentIndexComboBox.getItems().clear();

        /* single components, set single index 0, and pass the property to table */
        if (ComponentComboBox.getValue().equals("global param") || ComponentComboBox.getValue().equals("Mux")) {
            ComponentIndexComboBox.getItems().add("0");

            /* show parameters in table here if not a component array */
            switch (ComponentComboBox.getSelectionModel().getSelectedItem()) {
                case "global param":
                    setSelectedComponent(0);
                    populate(pdkReceiver.globalParameters.rootProperty.property);
                    break;
                case "Mux":
                    setSelectedComponent(1);
                    populate(pdkReceiver.optMuxComponent.rootProperty.property);
                    break;
                default:
                    break;
            }
        } else if (ComponentComboBox.getValue().equals("pdk Waveguide")) {
            for (int i = 0; i < num - 1; i++)
                ComponentIndexComboBox.getItems().add(Integer.toString(i));
        }
        /* otherwise we add "0~number-1" */
        else {
            for (int i = 0; i < num; i++)
                ComponentIndexComboBox.getItems().add(Integer.toString(i));
        }

        /* give Index a default value of 0 */
        ComponentIndexComboBox.getSelectionModel().select(0);
    }

    private void setSelectedComponent(int i) {
        selectedComponent = i;
    }

    @FXML
    void setComponentInTable(ActionEvent event) {
        if (ComponentComboBox.getValue().equals("global param") || ComponentComboBox.getValue().equals("Mux")) {
            table.setItems(data);
        } else {
            /* get the value */
            // this ? : sentence here is
            // trying to get rid of a null pointer exception.
            int index = Integer.parseInt(ComponentIndexComboBox.getSelectionModel().getSelectedItem() != null
                    ? ComponentIndexComboBox.getSelectionModel().getSelectedItem()
                    : "0");
            switch (ComponentComboBox.getSelectionModel().getSelectedItem()) {
                case "CWLasers":
                    setSelectedComponent(2);
                    populate(pdkReceiver.cwlasers.get(index).rootProperty.property);
                    break;
                case "pdk Ring":
                    setSelectedComponent(3);
                    populate(pdkReceiver.pdkRing.get(index).rootProperty.property);
                    break;
                case "Spec Analyzer":
                    setSelectedComponent(4);
                    populate(pdkReceiver.spectrumAnalyzerComponents.get(index).rootProperty.property);
                    break;
                case "pdk Waveguide":
                    setSelectedComponent(5);
                    populate(pdkReceiver.pdkWaveguide.get(index).rootProperty.property);
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
            out.println(pdkReceiver.writeFileString);
        } catch (IOException e) {
            System.err.println(e);
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
                inputPower = Double.parseDouble(pdkReceiver.cwlasers.get(i).rootProperty.property.get(1).getValue());
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
            out.println(pdkReceiver.writeFileString);
        } catch (IOException e) {
            System.err.println(e);
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
        if (result.get() == buttonTypeOne) {
            /* we copy the file bin/resources.bat to setup the running environment */
            File source = new File(optSimPathway + "/resources.bat");
            File dest = new File(optSimPathway + "/runOptSim.bat");

            // copy file using FileStreams
            copyFileUsingFileStreams(source, dest);

            /* add two lines to run optsim file */
            try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(dest, true)))) {
                out.println("\n");
                out.println("SET SimFilePath=\"" + pathway + "/" + filename + ".moml\"");
                out.println("SET OPTSIM_PDAFLOW_PDK_ROOT=" + workgroupDir);
                out.println("jetOptSim %SimFilePath%\n");
                out.println("cmd /k");
            } catch (IOException e1) {
                System.err.println(e1);
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
        } else {
            ;
        }

    }

    @Override
    public void initialize() {
        setSelectedComponent(0);
        setSelectedIndex(0);
        setFlag(0);
        setPathway("/Desktop/test");
        setFileName("PDK_Receiver");

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

        pathwayText.setText(pathway);
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
                    System.out.println("Commit in table data class successfully");
                    setSelectedIndex(Integer.parseInt(ComponentIndexComboBox.getSelectionModel().getSelectedItem() != null
                            ? ComponentIndexComboBox.getSelectionModel().getSelectedItem()
                            : "0"));
                    table.setItems(data);
                    pdkReceiver.setEdittedPorperty(selectedComponent, selectedIndex, changedIndex, changedValue);
                    pdkReceiver.setWriteFileString();
                    pdkReceiverCC.setEdittedPorperty(selectedComponent, selectedIndex, changedIndex, changedValue);
                    pdkReceiverCC.setWriteFileString();
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

    /**
     *
     */

    private void populate(ArrayList<OptSimProperty> property) {
        data.clear();
        property.forEach(p -> data.add(new PropertyTableData(p)));
    }

    private void setItemComboBox(ArrayList<String> property) {
        componentList.clear();
        property.forEach(p -> componentList.add(p));
    }

    private void setPowerPenaltyTable(ArrayList<PowerPenaltyTableData> ppData) {
        powerPenaltyData.clear();
        ppData.forEach(p -> powerPenaltyData.add(p));
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    @Override
    public StatusBar getStatusBar() {
        return statusBar;
    }

    @Override
    public void generateGDS() {
    }

    @Override
    public MatlabChart getFig() {
        return fig;
    }

    @Override
    public Tab getTab() {
        return ExtensionPDKDemuxTab;
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
        InputStream input = null;
        OutputStream output = null;
        try {
            input = new FileInputStream(source);
            output = new FileOutputStream(dest);
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(buf)) > 0) {
                output.write(buf, 0, bytesRead);
            }
        } finally {
            assert input != null;
            input.close();
            assert output != null;
            output.close();
        }
    }

    @Override
    public void setSimDataBase(SimulationDataBase simDataBase) {
        this.simDataBase = simDataBase;
    }

    @Override
    public SimulationDataBase getSimDataBase() {
        return simDataBase;
    }

    static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    public void setOptSimWorkgroupDir() {
        DirectoryChooser fileChooser = new DirectoryChooser();
        fileChooser.setTitle("Choose OptSim Workgroup Folder");
        File openedFolder = fileChooser.showDialog(setPathwayButton.getScene().getWindow());
        this.workgroupDir = openedFolder.getAbsolutePath();
        System.out.println(this.workgroupDir);
    }

}
