package ch.epfl.javancox.experiments.builder.swing_gui;

import ch.epfl.javancox.experiments.builder.tree_model.ConstructorNodeChooserPointer;

import javax.swing.*;
import java.awt.*;


public class ConstructorPointerGUIContainer extends ConstructorGUIContainer {

    private static final long serialVersionUID = 1L;

    public ConstructorPointerGUIContainer(final ConstructorNodeChooserPointer node, LayoutManager man, int prefix) {
        super(node.superInstance, man, prefix);
        JLabel lab = null;

        for (int i = 0; i < this.getComponentCount(); i++) {
            Component current = this.getComponent(i);
            if (current instanceof JLabel) {
                lab = (JLabel) current;
            }
        }

        if (lab != null) {
            String text = lab.getText();
            String newText = "";
            int index = text.indexOf(62, text.indexOf("font")) + 1;
            newText = newText + text.substring(0, index);
            newText = newText + "@ ->" + text.substring(index);
            JLabel newLabel = new JLabel(newText);
            this.add(newLabel);
            final JCheckBox completeCartesian = new JCheckBox("Complete Cartesian Product");
            completeCartesian.setBackground(Color.WHITE);
            completeCartesian.addActionListener(e -> {
                node.isCartesianEnabled = completeCartesian.isSelected();
                node.getContainingTreeModel().reloadTree();
            });
            this.add(completeCartesian);
        }

    }
}
