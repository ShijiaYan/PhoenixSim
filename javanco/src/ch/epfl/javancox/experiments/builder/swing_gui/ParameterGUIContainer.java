//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ch.epfl.javancox.experiments.builder.swing_gui;

import ch.epfl.javancox.experiments.builder.tree_model.AbstractParameterChooseNode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;


public class ParameterGUIContainer extends AbstractGUIContainer {

    private static final long serialVersionUID = 1L;
    private static final Color PARAMGUI_BACKGROUND_COLOR = new Color(240, 240, 240);
    public static final JFileChooser fc = new JFileChooser();

    public ParameterGUIContainer(final AbstractParameterChooseNode node, LayoutManager man, int prefix) {
        super(node, man, prefix);
        this.refresh();
        this.setBackground(PARAMGUI_BACKGROUND_COLOR);
        fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
        if (node.isRoot()) {
            JButton save = new JButton("Save");
            ActionListener a = e -> node.getContainingTreeModel().saveFile();
            save.setFocusable(false);
            save.addActionListener(a);
            JButton saveAs = new JButton("Save As...");
            a = e -> {
                int returnVal = ParameterGUIContainer.fc.showSaveDialog(null);
                if (returnVal == 0) {
                    try {
                        String directory = ParameterGUIContainer.fc.getSelectedFile().getAbsolutePath();
                        node.getContainingTreeModel().saveFile(directory);
                    } catch (Exception var4) {
                        var4.printStackTrace();
                    }
                }

            };
            saveAs.setFocusable(false);
            saveAs.addActionListener(a);
            JButton deploy = new JButton("Deploy tree");
            a = e -> node.setAllExpanded();
            deploy.setFocusable(false);
            deploy.addActionListener(a);
            this.add(save, new Placement(200, 300, false));
            this.add(saveAs, new Placement(100, 200, false));
            this.add(deploy, new Placement(0, 100, false));
        }

        this.setMaximumSize(new Dimension(3000, lineHeight));
    }

    protected JButton getAddButton() {
        JButton add = new JButton("Add");
        add.setPreferredSize(new Dimension(60, 20));
        add.addActionListener(arg0 -> ParameterGUIContainer.this.absNode.actionPerformed("Add"));
        add.setFocusable(false);
        return add;
    }

    public void refreshImpl() {
        if (this.absNode.getChildCount() > 0) {
            if (this.absNode.isExpanded()) {
                super.setIcon("openfolder.png");
            } else {
                super.setIcon("closedfolder.png");
            }
        } else {
            super.setIcon("void.png");
        }

    }
}
