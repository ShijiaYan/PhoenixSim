package People.Meisam.GUI.Utilities.RibbonFX.skin;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.scene.control.SkinBase;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;

import java.util.Collection;

import People.Meisam.GUI.Utilities.RibbonFX.Ribbon;
import People.Meisam.GUI.Utilities.RibbonFX.RibbonTab;

/**
 * Created by pedro_000 on 1/18/14.
 */
public class RibbonSkin extends SkinBase<Ribbon> {
    VBox outerContainer;
    private TabPane tabPane;

    /**
     * Constructor for all SkinBase instances.
     *
     * @param control The control for which this Skin should attach to.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public RibbonSkin(Ribbon control) {
        super(control);
        tabPane = new TabPane();
        outerContainer = new VBox();

        control.getTabs().addListener((ListChangeListener<RibbonTab>) changed -> tabsChanged(changed));
        updateAddedRibbonTabs(control.getTabs());
        outerContainer.getStyleClass().setAll("outer-container");
        outerContainer.getChildren().addAll(control.getQuickAccessBar(), tabPane);
        getChildren().add(outerContainer);
        control.selectedRibbonTabProperty().addListener((observable, oldValue, newValue) -> tabPane.getSelectionModel().select((RibbonTab)newValue));
        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> control.setSelectedRibbonTab((RibbonTab)tabPane.getSelectionModel().getSelectedItem()));
    }

    private void updateAddedRibbonTabs(Collection<? extends RibbonTab> ribbonTabs) {
        for (RibbonTab ribbonTab : ribbonTabs)
            tabPane.getTabs().add(ribbonTab);
    }

    private void tabsChanged(ListChangeListener.Change<? extends RibbonTab> changed) {
        while(changed.next())
        {
            if (changed.wasAdded())
            {
                updateAddedRibbonTabs(changed.getAddedSubList());
            }
            if(changed.wasRemoved())
            {
                for (RibbonTab ribbonTab : changed.getRemoved())
                    tabPane.getTabs().remove(ribbonTab);
            }
        }
    }

}
