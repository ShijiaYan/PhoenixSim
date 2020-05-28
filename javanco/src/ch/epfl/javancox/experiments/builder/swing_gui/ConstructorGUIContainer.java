package ch.epfl.javancox.experiments.builder.swing_gui;

import ch.epfl.javancox.experiments.builder.tree_model.ConstructorChooseNode;

import java.awt.*;


public class ConstructorGUIContainer extends AbstractGUIContainer {

    private static final Color CONSTRUCTOR_COLOR = new Color(255, 255, 204);
    private static final long serialVersionUID = 1L;

    public ConstructorGUIContainer(ConstructorChooseNode node, LayoutManager man, int prefix) {
        super(node, man, prefix);
        this.setBackground(CONSTRUCTOR_COLOR);
        this.refresh();
    }

    public void refreshImpl() {
        super.setIcon("constructoricon.png");
    }
}
