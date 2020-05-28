//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ch.epfl.javancox.experiments.builder.swing_gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import ch.epfl.javancox.experiments.builder.tree_model.TypableChooseNode;


public class TypableParameterGUIContainer extends ParameterGUIContainer {

	private static final long serialVersionUID = 1L;
	JTextField text = null;

	public TypableParameterGUIContainer(final TypableChooseNode node, LayoutManager man, int prefix) {
		super(node, man, prefix);
		if (this.text == null) { this.text = new JTextField("Please Add Your Value", 30); }

		this.text.setHorizontalAlignment(4);
		this.text.setFont(defaultFontPlain);
		this.text.setSize(150, 30);
		this.text.setPreferredSize(new Dimension(150, lineHeight));
		this.text.setMaximumSize(new Dimension(150, lineHeight));
		this.text.setBackground(Color.WHITE);
		this.text.addFocusListener(new FocusListener() {

			public void focusGained(FocusEvent e) {
				if (TypableParameterGUIContainer.this.text.getText().contains("Please Add Your Value")) {
					TypableParameterGUIContainer.this.text.setText("");
				}

			}

			public void focusLost(FocusEvent e) {
				if (TypableParameterGUIContainer.this.text.getText().isEmpty()) {
					TypableParameterGUIContainer.this.text.setText("Please Add Your Value");
				}

			}
		});
		this.text.addActionListener(arg0 -> {
            node.actionPerformed("Add");
            SwingUtilities.invokeLater(() -> TypableParameterGUIContainer.this.text.requestFocusInWindow());
        });
		this.text.getDocument().addDocumentListener(new DocumentListener() {

			public void removeUpdate(DocumentEvent arg0) {
				node.setTextValue(TypableParameterGUIContainer.this.text.getText());
			}

			public void insertUpdate(DocumentEvent arg0) {
				node.setTextValue(TypableParameterGUIContainer.this.text.getText());
			}

			public void changedUpdate(DocumentEvent arg0) {
				node.setTextValue(TypableParameterGUIContainer.this.text.getText());
			}
		});
		JButton addButton = this.getAddButton();
		this.add(this.text, new Placement(100, 250, false));
		this.add(addButton, new Placement(0, 100, false));
	}
}
