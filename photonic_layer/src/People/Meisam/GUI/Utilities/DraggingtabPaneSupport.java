//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package People.Meisam.GUI.Utilities;

import java.util.concurrent.atomic.AtomicLong;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;


public class DraggingtabPaneSupport {

	private Tab currentDraggingTab;
	private static final AtomicLong idGenerator = new AtomicLong();
	private final String draggingID;

	public DraggingtabPaneSupport() {
		this.draggingID = "DraggingTabPaneSupport-" + idGenerator.incrementAndGet();
	}

	public void addSupport(TabPane tabPane) {
		tabPane.getTabs().forEach(this::addDragHandlers);
		tabPane.getTabs().addListener((ListChangeListener<? super Tab>) c -> {
			while (c.next()) {
				if (c.wasAdded()) { c.getAddedSubList().forEach(this::addDragHandlers); }

				if (c.wasRemoved()) { c.getRemoved().forEach(this::removeDragHandlers); }
			}

		});
		tabPane.setOnDragOver(e -> {
			if (this.draggingID.equals(e.getDragboard().getString()) && this.currentDraggingTab != null
					&& this.currentDraggingTab.getTabPane() == tabPane) {
				e.acceptTransferModes(TransferMode.MOVE);
			}

		});
		tabPane.setOnDragDropped(e -> {
			if (this.draggingID.equals(e.getDragboard().getString()) && this.currentDraggingTab != null
					&& this.currentDraggingTab.getTabPane() == tabPane) {
				this.currentDraggingTab.getTabPane().getTabs().remove(this.currentDraggingTab);
				tabPane.getTabs().add(this.currentDraggingTab);
				this.currentDraggingTab.getTabPane().getSelectionModel().select(this.currentDraggingTab);
			}

		});
	}

	private void addDragHandlers(Tab tab) {
		if (tab.getText() != null && !tab.getText().isEmpty()) {
			Label label = new Label(tab.getText(), tab.getGraphic());
			tab.setText(null);
			tab.setGraphic(label);
		}

		Node graphic = tab.getGraphic();
		graphic.setOnDragDetected(e -> {
			Dragboard dragboard = graphic.startDragAndDrop(TransferMode.MOVE);
			ClipboardContent content = new ClipboardContent();
			content.putString(this.draggingID);
			dragboard.setContent(content);
			dragboard.setDragView(graphic.snapshot(null, null));
			this.currentDraggingTab = tab;
		});
		graphic.setOnDragOver(e -> {
			if (this.draggingID.equals(e.getDragboard().getString()) && this.currentDraggingTab != null
					&& this.currentDraggingTab.getGraphic() != graphic) {
				e.acceptTransferModes(TransferMode.MOVE);
			}

		});
		graphic.setOnDragDropped(e -> {
			if (this.draggingID.equals(e.getDragboard().getString()) && this.currentDraggingTab != null
					&& this.currentDraggingTab.getGraphic() != graphic) {
				int index = tab.getTabPane().getTabs().indexOf(tab);
				this.currentDraggingTab.getTabPane().getTabs().remove(this.currentDraggingTab);
				tab.getTabPane().getTabs().add(index, this.currentDraggingTab);
				this.currentDraggingTab.getTabPane().getSelectionModel().select(this.currentDraggingTab);
			}

		});
		graphic.setOnDragDone(e -> {
			this.currentDraggingTab = null;
		});
	}

	private void removeDragHandlers(Tab tab) {
		tab.getGraphic().setOnDragDetected(null);
		tab.getGraphic().setOnDragOver(null);
		tab.getGraphic().setOnDragDropped(null);
		tab.getGraphic().setOnDragDone(null);
	}
}
