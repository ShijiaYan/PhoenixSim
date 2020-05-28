package People.Meisam.GUI.PhoenixSim.TabsLibrary.ExtensionOptSim.DemofabTab;

import People.Meisam.GUI.PhoenixSim.TabsLibrary.AbstractTabController;
import People.Meisam.GUI.Plotters.MatlabPlot.MatlabChart;
import People.Meisam.GUI.Utilities.SimulationDataBase;
import People.Rui.Extension.OptSimExtension.DemofabTransmitter;
import People.Rui.Extension.OptSimExtension.OptSimProperty;
import People.Rui.Extension.OptSimExtension.PowerPenaltyTableData;
import People.Rui.Extension.OptSimExtension.PropertyTableData;
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
// Do we need project file for this? 
// Leave it like this and we will see
// Tomorrow, go back to previous works and try to polish the data displayed in the table
// Also, make the CC editable in the table.
public class DemofabTabController extends AbstractTabController {

    int num, changedIndex;

    DemofabTransmitter demofabTransmitter = new DemofabTransmitter(num);

    String pathway, changedValue;
    String filename;
    String optSimPathway;
    int flag; // to determine whether the file has been generated and run
    private int selectedComponent;
    private int selectedIndex;
    String workgroupDir;

    ArrayList<PowerPenaltyTableData> ppDataList = new ArrayList<>();

    private ObservableList<PropertyTableData> data = FXCollections.observableArrayList();

    private ObservableList<PowerPenaltyTableData> powerPenaltyData = FXCollections.observableArrayList();

    private ObservableList<String> componentList = FXCollections.observableArrayList();
    // To change data when edit the table cell, an observableList should be set up
    // firstly add component names to this list
    // secondly show items using this observableList

    @FXML
    private Tab DemofabTab;

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

    // ****************** methods ****************

    @FXML
    void generateCC(ActionEvent event) throws IOException, InterruptedException {
        try (PrintWriter out = new PrintWriter(
                new BufferedWriter(new FileWriter(pathway + "/" + filename + "_CC.moml", false)))) {
            out.println(demofabTransmitter.getWriteString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        /* Typical way to create a dialog */
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("PhoenixSim");
        alert.setHeaderText(null);
        alert.setContentText("OptSim Compound Component file has been generated at\n" + pathway + "\n");

        alert.showAndWait();
        File source = new File(optSimPathway + "/resources.bat");
        File dest = new File(optSimPathway + "/runOptSim.bat");

        // copy file using FileStreams
        copyFileUsingFileStreams(source, dest);

        /* add run optsim file command */
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(dest, true)))) {
            out.println("\n");
            out.println("SET SimFilePath=\"" + pathway + "/" + filename + "_CC.moml\"");
            out.println("SET OPTSIM_PDAFLOW_PDK_ROOT=F:/optSim/WorkGroup");
            // out.println("jetOptSim %SimFilePath%\n"); You can check if you want
            out.println("OptoDesigner %SimFilePath%\n");
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
        demofabTransmitter = new DemofabTransmitter(num);

        /* Generate a list of components after setting up the system */
        setItemComboBox(demofabTransmitter.getComponentsName());
        ComponentComboBox.getItems().addAll(componentList);
        ComponentComboBox.getSelectionModel().select(0); // set default to global parameter setting
        setFlag(0);
    }

    @FXML
        /* Based on component, we list the index */
        /* In this case, all the components are n, except waveguides */
    void setIndexComboBox(ActionEvent event) {
        /* we always clear the combo box first */
        ComponentIndexComboBox.getItems().clear();

        /* single components, set single index 0, and pass the property to table */
        if (ComponentComboBox.getValue().equals("Global CC") || ComponentComboBox.getValue().equals("Global Project")
                || ComponentComboBox.getValue().equals("Mux")) {
            ComponentIndexComboBox.getItems().add("0");

            /* show parameters in table here if not a component array */
            switch (ComponentComboBox.getSelectionModel().getSelectedItem()) {
                case "Global CC":
                    setSelectedComponent(0);
                    populate(demofabTransmitter.getGlobalParameters().rootProperty.property);
                    break;
                case "Global Project":
                    setSelectedComponent(1);
                    populate(demofabTransmitter.getGlobalParametersProj().rootProperty.property);
                    break;
                case "Mux":
                    setSelectedComponent(3);
                    populate(demofabTransmitter.getOptMux().rootProperty.property);
                    break;
                default:
                    break;
            }
        } else if (ComponentComboBox.getValue().equals("Waveguide")) {
            for (int i = 1; i < num; i++)
                ComponentIndexComboBox.getItems().add(Integer.toString(i));
        }
        /* otherwise we add "1~num" */
        else {
            for (int i = 1; i < num + 1; i++)
                ComponentIndexComboBox.getItems().add(Integer.toString(i));
        }

        /* give Index a default value of 0 */
        ComponentIndexComboBox.getSelectionModel().select(0);
    }

    /* monitor which component is chosen, in this case i = 0 or 1 */
    // 0: ring filters
    // 1: waveguides
    private void setSelectedComponent(int i) {
        selectedComponent = i;
    }

    @FXML
    void setComponentInTable(ActionEvent event) {

        if (ComponentComboBox.getValue().equals("Global CC") || ComponentComboBox.getValue().equals("Global Project")
                || ComponentComboBox.getValue().equals("Mux")) {
            table.setItems(data);
        } else {
            /* get the value */
            // this ? : sentence here is
            // trying to get rid of a null pointer exception.
            int index = Integer.parseInt(ComponentIndexComboBox.getSelectionModel().getSelectedItem() != null
                    ? ComponentIndexComboBox.getSelectionModel().getSelectedItem()
                    : "1") - 1;
            switch (ComponentComboBox.getSelectionModel().getSelectedItem()) {
                case "CW Lasers":
                    setSelectedComponent(2);
                    populate(demofabTransmitter.getCWLaser(index).rootProperty.property);
                    break;
                case "Ring Filter":
                    setSelectedComponent(4);
                    populate(demofabTransmitter.getRingFilter(index).getPropertyNames(),
                            demofabTransmitter.getRingFilter(index).getPropertyValues());
                    break;
                case "Waveguide":
                    setSelectedComponent(5);
                    populate(demofabTransmitter.getWaveguide(index).getPropertyNames(),
                            demofabTransmitter.getWaveguide(index).getPropertyValues());
                    break;
                case "Bend":
                    setSelectedComponent(6);
                    populate(demofabTransmitter.getArc(index).getPropertyNames(),
                            demofabTransmitter.getArc(index).getPropertyValues());
                    break;
                case "Spectrum Analyzer":
                    setSelectedComponent(7);
                    populate(demofabTransmitter.getSpec(index).rootProperty.property);
                    break;
                default:
                    break;
            }
            table.setItems(data);
        }
    }

    @FXML
    void generateFile(ActionEvent event) throws InterruptedException, IOException {
        try (PrintWriter out = new PrintWriter(
                new BufferedWriter(new FileWriter(pathway + "/" + filename + ".moml", false)))) {
            out.println(demofabTransmitter.getWriteStringProj());
        } catch (IOException e) {
            e.printStackTrace();
        }

        /* Typical way to create a dialog */
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("PhoenixSim");
        alert.setHeaderText(null);
        alert.setContentText("OptSim file has been generated at\n" + pathway + "\n");

        alert.showAndWait();

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
            e1.printStackTrace();
        }
        /* use runtime run the .bat file to open the file */
        Runtime runtime = Runtime.getRuntime();
        try {
            Process process = runtime.exec("cmd /c start " + optSimPathway + "/runOptSim.bat");
            process.waitFor();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        setFlag(1);
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
            alert.setContentText("You must generate the file and run it first!\n Use \"OptSim-Proj\"button");
            alert.showAndWait();
        } else {
            ppDataList.clear();
            for (int i = 0; i < num; i++) {
                frequency.clear();
                power.clear();
                // if no files then do nothing
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
                        .parseDouble(demofabTransmitter.getCWLaser(i).rootProperty.property.get(1).getValue());
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
                new BufferedWriter(new FileWriter(pathway + "/" + filename + "_CC.moml", false)))) {
            out.println(demofabTransmitter.getWriteString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // show a dialog here, and based on users choice, continue or stop here
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("PhoenixSim");
        alert.setHeaderText(null);
        alert.setContentText("You OptSim file has generated at\n" + "\t" + pathway + "/" + filename + "_CC.moml.\n"
                + "Do you want to continue running the file?");
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
                    out.println("\n");
                    out.println("SET SimFilePath=\"" + pathway + "/" + filename + "_CC.moml\"");
                    out.println("SET OPTSIM_PDAFLOW_PDK_ROOT=" + workgroupDir);
                    out.println("jetOptSim %SimFilePath%\n");
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
                    ioException.printStackTrace();
                }
            }
        } else {
            System.out.println("DemofabTabController:\n\trunFile: result is not present.");
        }

    }

    @Override
    public void initialize() {
        setSelectedComponent(0);
        setSelectedIndex(0);
        setFlag(0);
        setPathway("/Desktop/test");
        setFileName("demofab_transmitter");

        // read optsim pathway from txt file
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
        System.out.println(optSimPathway + "/resources.bat");// TODO if there's no resources.bat there should be an
        // error generated at the GUI

        pathwayText.setText(pathway);
        table.setEditable(true);
        // calculationTable.setEditable(false);

        propertyColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        // the string here should be the same as the variable name in propertyTableData
        propertyColumn.setCellValueFactory(new PropertyValueFactory<>("propertyName"));

        valueColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        valueColumn.setCellValueFactory(new PropertyValueFactory<>("propertyValue"));

        valueColumn.setOnEditCommit((TableColumn.CellEditEvent<PropertyTableData, String> t) -> {
                    TablePosition<PropertyTableData, String> pos = t.getTablePosition();
                    // get changed value in table
                    changedValue = t.getNewValue();
                    // get changed index in table
                    changedIndex = pos.getRow();
                    // change the value in table
                    t.getTableView().getItems().get(changedIndex).setPropertyValue(changedValue);
                    // change the value in propertyTableData
                    data.get(changedIndex).setPropertyValue(changedValue);
                    System.out.println("Commit successfully");
                    // get the index of component we selected
                    setSelectedIndex(Integer.parseInt(ComponentIndexComboBox.getSelectionModel().getSelectedItem() != null
                            ? ComponentIndexComboBox.getSelectionModel().getSelectedItem()
                            : "1") - 1);
                    // update data in table
                    table.setItems(data);

                    //
                    demofabTransmitter.setEdittedPorperty(selectedComponent, selectedIndex, changedIndex, changedValue);
                    demofabTransmitter.setWriteFileString();
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

    // Use two arrays to represent name and value, respectively
    private void populate(ArrayList<String> propertyName, ArrayList<String> propertyValue) {
        data.clear();
        int number = propertyName.size();
        for (int i = 0; i < number; i++)
            data.add(new PropertyTableData(propertyName.get(i), propertyValue.get(i)));
    }

    private void populate(ArrayList<OptSimProperty> properties) {
        data.clear();
        properties.forEach(t -> data.add(new PropertyTableData(t)));
    }

    private void setItemComboBox(ArrayList<String> components) {
        componentList.clear();
        componentList.addAll(components);
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
        return DemofabTab;
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

    @Override
    public void setSimDataBase(SimulationDataBase simDataBase) {
        // TODO Auto-generated method stub

    }

    @Override
    public SimulationDataBase getSimDataBase() {
        // TODO Auto-generated method stub
        return null;
    }

    // cite: https://stackoverflow.com/questions/326390/how-do-i-create-a-java-string-from-the-contents-of-a-file
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
