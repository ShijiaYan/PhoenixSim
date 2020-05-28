package People.Rui.Extension.OptSimExtension;
/**
 * This is the data which is going to be shown in the table view.
 */
import javafx.beans.property.SimpleStringProperty;

public class PropertyTableData {
	private SimpleStringProperty propertyName;
	private	SimpleStringProperty propertyValue;
	public int index;
	public String componentName;
	
	// constructor
	public PropertyTableData(OptSimProperty optSimProperty) {
		this.propertyName = new SimpleStringProperty(optSimProperty.getName());
		this.propertyValue= new SimpleStringProperty(optSimProperty.getValue());
	}
	
	// Constructor for demofab, where we don't use optSimProperty any more.
	public PropertyTableData(String name, String value) {
		this.propertyName = new SimpleStringProperty(name);
		this.propertyValue= new SimpleStringProperty(value);
	}
	
	public String getPropertyName() {
		return propertyName.get();
	}
	
	public void setPropertyName(String newName) {
		this.propertyName.set(newName);
	}
	
	public String getPropertyValue() {
		return propertyValue.get();
	}
	
	public int setPropertyValue(String newValue) {
		this.propertyValue.set(newValue);
		return 0;
	}
	
	public void setIndex(int index) {
		this.index=index;
	}
	
	public void setComponent(String component) {
		this.componentName = component;
	}
	
	
}
