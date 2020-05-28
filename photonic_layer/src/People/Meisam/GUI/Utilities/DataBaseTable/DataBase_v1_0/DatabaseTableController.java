package People.Meisam.GUI.Utilities.DataBaseTable.DataBase_v1_0;

import java.io.File;
import java.io.IOException;
import People.Meisam.GUI.Builders.AbstractController;
import People.Meisam.GUI.PhoenixSim.ModulesLibrary.ImportDataModule.ImportDataModule;
import People.Meisam.GUI.Utilities.SimulationDataBase;
import People.Meisam.GUI.Utilities.SimulationVariable;
import People.Meisam.GUI.Utilities.ExportData.ExportVariables;
import PhotonicElements.Utilities.MathLibraries.MoreMath;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.util.Callback;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

/**
 * Created by meisam on 7/7/17.
 */
public class DatabaseTableController extends AbstractController {

    @FXML TableView<SimulationVariable> paramTable ;
    @FXML Button paramCloseButton ;
    @FXML Button clearAllButton ;
    @FXML Button refreshButton ;
    @FXML Button loadButton ;
    @FXML TextField functionField ;
    @FXML public Tab tab ;

    public SimpleBooleanProperty loadCompleted = new SimpleBooleanProperty(false) ;

//    SimulationDataBase dataBase ;
//
//    //**********************creating some simulation data and database ***************************************
    // step 1: create simulation variables
    double[] x = MoreMath.linspace(-20, 20, 5) ;
    SimulationVariable varX = new SimulationVariable("varX", "variable x", x) ;

    double[] y = MoreMath.Arrays.Functions.sin(x) ;
    SimulationVariable varY = new SimulationVariable("varY", "variable y", y) ;

    double[] z = MoreMath.linspace(-10, 10, 3) ;
    SimulationVariable varZ = new SimulationVariable("varZ", "variable z", z) ;

    // step 2: create simulation data base
    SimulationDataBase dataBase = new SimulationDataBase(varX, varY, varZ) ;
    //**************************************************************

    public void setDataBase(SimulationDataBase dataBase){
        this.dataBase = dataBase ;
    }

    public SimulationDataBase getDataBase(){
        return dataBase ;
    }

    @FXML
    public void initialize(){
    	loadCompleted = new SimpleBooleanProperty(false) ;
    }


    @FXML
    public void clearDataBaseAndTable(){
    	if(paramTable.getSelectionModel().getSelectedItems().isEmpty()){
            paramTable.getItems().clear();
            dataBase.clear();
    	}
    	else{
        	for(SimulationVariable var : paramTable.getSelectionModel().getSelectedItems()){
        		dataBase.removeVariable(var);
        	}
        	refreshTable(dataBase);
    	}

    }

    @SuppressWarnings("unchecked")
	public void updateParamTable(){
        ObservableList<SimulationVariable> list = FXCollections.observableArrayList() ;
        for(SimulationVariable x : dataBase.getVariableList()){
            list.add(x) ;
        }
        TableColumn<SimulationVariable, String> nameColumn = new TableColumn<>("Name") ;
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<SimulationVariable, String> aliasColumn = new TableColumn<>("Alias") ;
        aliasColumn.setCellValueFactory(new PropertyValueFactory<>("alias"));

        TableColumn<SimulationVariable, String> sizeColumn = new TableColumn<>("Size") ;
        sizeColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getArraySize())) ;

        TableColumn<SimulationVariable, String> typeColumn = new TableColumn<>("Type") ;
        typeColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getType()));

        TableColumn<SimulationVariable, String> valuesColumn = new TableColumn<>("Values") ;
        valuesColumn.setCellValueFactory(new PropertyValueFactory<>("variableList"));

        paramTable.getColumns().addAll(nameColumn, aliasColumn, sizeColumn, typeColumn, valuesColumn) ;
        paramTable.setItems(list);

        /* Rui changed here
         * Try to make the namecolumn editable
         * 
         */
        // This make it editable, and when we double click it will let us input some text.
        paramTable.setEditable(true);
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        
        nameColumn.setOnEditCommit(
        	    (CellEditEvent<SimulationVariable, String> t) -> {
        	        t.getTableView().getItems().get(
        	                t.getTablePosition().getRow())
        	                .setName(t.getNewValue());
        	});
        
        aliasColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        
        aliasColumn.setOnEditCommit(
        	    (CellEditEvent<SimulationVariable, String> t) -> {
        	        t.getTableView().getItems().get(
        	                t.getTablePosition().getRow())
        	                .setAlias(t.getNewValue());
        	});
        // choosing multiple rows
        paramTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }


    public Button getCloseButton(){
        return paramCloseButton ;
    }

    public Button getRefreshButton(){
    	return refreshButton ;
    }

    // saving database to the file
    @FXML
    public void seveToFile() throws IOException{
    	dataBase.saveToFile();
    }

    // loading database from file
    @FXML
    public void laodFromFile(){
    	dataBase.loadFromFile();
    	if(dataBase.dataBaseChanged()){
        	paramTable.getColumns().clear();
        	updateParamTable();
    	}

    }

    public void refreshTable(SimulationDataBase simDataBase){
    	paramTable.getColumns().clear();
    	this.setDataBase(simDataBase);
    	updateParamTable();
    }

    public Button getLoadButton(){
    	return loadButton ;
    }

    public Button getClearAllButton(){
    	return clearAllButton ;
    }

    @FXML
    public void exportPressed(){
    	ObservableList<SimulationVariable> list = paramTable.getSelectionModel().getSelectedItems() ;
    	if(list.isEmpty()){
    		new ExportVariables(dataBase).export();
    	}
    	else{
    		SimulationDataBase selectedVars = new SimulationDataBase() ;
    		for(SimulationVariable x : list){
    			selectedVars.addNewVariable(x);
    		}
    		new ExportVariables(selectedVars).export();
    	}
    }

    @FXML
    public void importPressed(){
    	new ImportDataModule(dataBase) ;
    }

    @FXML
    public void applyFunctionPressed(){
    	for(SimulationVariable var : paramTable.getSelectionModel().getSelectedItems()){
    		applyFunctionToVariable(var);
    	}
    	refreshTable(dataBase);
    }

    private void applyFunctionToVariable(SimulationVariable selectedVar){
    	String st = functionField.getText() ;
    	Expression func = new ExpressionBuilder(st).variable("x").build() ; // assume that variable is "x"
    	double[] funcValues = new double[selectedVar.getLength()] ;
    	for(int i=0; i<funcValues.length; i++){
    		funcValues[i] = func.setVariable("x", selectedVar.getValue(i)).evaluate() ;
    	}
    	String funcName = st.replaceAll("x", selectedVar.getName()) ;
    	String funcAlias = st.replaceAll("x", selectedVar.getAlias()) ;
    	SimulationVariable funcVariable = new SimulationVariable(funcName, funcAlias, funcValues) ;
    	dataBase.addNewVariable(funcVariable);
    }

    
    // implementing drag and drop
    
	@FXML
	private void setOnDragOver(DragEvent event){
		Dragboard db = event.getDragboard();
        if (db.hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY);
        } else {
            event.consume();
        }
	}

	@FXML
	private void setOnDragDropped(DragEvent event){
        Dragboard db = event.getDragboard();
        boolean success = false;
        if (db.hasFiles()) {
            success = true;
            File file = db.getFiles().get(0) ;
        	if(checkExtension(file, "dbsim")){
        		dataBase.loadFromFile(file);
        		paramTable.getColumns().clear();
        		updateParamTable();
        	}
        }
        event.setDropCompleted(success);
        event.consume();
	}
	
	private boolean checkExtension(File file, String ext){
		String filePath = file.getAbsolutePath() ;
		String[] st = filePath.trim().split("\\.") ;
		int M = st.length ;
		String fileExt = st[M-1] ;
        return fileExt.equals(ext);
	}

}
