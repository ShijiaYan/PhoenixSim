//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ch.epfl.javancox.experiments.builder.swing_gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.Hashtable;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.CellEditorListener;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import ch.epfl.general_libraries.logging.Logger;
import ch.epfl.javancox.experiments.builder.tree_model.*;
import ch.epfl.javancox.experiments.builder.tree_model.ObjectConstructionTreeModel;
import ch.epfl.javancox.experiments.builder.tree_model.ObjectConstructionTreeModel.TreeModelUIManager;


public class InstanceDynamicJTree extends JTree
		implements Serializable, KeyListener, TreeCellRenderer, TreeModelUIManager {

	private static Logger logger = new Logger(InstanceDynamicJTree.class);
	private static final long serialVersionUID = -235363405915261784L;
	private ArrayList<JComponent> focusable = new ArrayList<>();
	private JTextField focused;
	private int index = 0;
	private Hashtable<AbstractChooseNode, AbstractGUIContainer> guiComponentMap = new Hashtable<>();
	private boolean ctrlPressed = false;

	public InstanceDynamicJTree(ObjectConstructionTreeModel<?> mod) {
		logger.trace("Initialisation of the JTree");
		this.addKeyListener(this);
		this.setFocusTraversalKeysEnabled(false);
		mod.setTreeModelUIManager(this);
		this.setModel(mod);
		this.focusable.clear();
		this.index = 0;
		this.setEditable(true);
		this.setFocusable(true);
		this.setCellRenderer(this);
		this.setCellEditor(new InstanceDynamicJTree.MyEditor());
	}

	public void showErrorMessage(String string) {
		JOptionPane.showMessageDialog(null, string, "Error", 0);
	}

	public void registerAsFocusable(JComponent comp) {
		this.focusable.add(comp);
	}

	public boolean isPathEditable(TreePath treePath) {
		return ((AbstractChooseNode) treePath.getLastPathComponent()).isEditable();
	}

	public int getInstancesCount() {
		return ((ObjectConstructionTreeModel) this.getModel()).isReady()
				? ((AbstractChooseNode) this.getModel().getRoot()).getInstancesCount()
				: 0;
	}

	public void removeNode(AbstractChooseNode node) {
		this.guiComponentMap.remove(node);
	}

	protected Container getContainer(AbstractChooseNode node) {
		AbstractGUIContainer c = null;
		c = this.guiComponentMap.get(node);
		if (c == null) {
			if (node instanceof ClassChooseNode) {
				c = new ParameterGUIContainer((AbstractParameterChooseNode) node, null, 0);
			} else if (node instanceof BooleanChooseNode) {
				c = new BooleanParameterGUIContainer((BooleanChooseNode) node, null, 0);
			} else if (node instanceof ArrayChooseNode) {
				c = new ParameterGUIContainer((AbstractParameterChooseNode) node, null, 0);
			} else if (node instanceof TypableChooseNode) {
				c = new TypableParameterGUIContainer((TypableChooseNode) node, null, 0);
			} else if (node instanceof ConstructorChooseNode) {
				c = new ConstructorGUIContainer((ConstructorChooseNode) node, null, 0);
			} else if (node instanceof LeafChooseNode) {
				c = new LeafNodeGUIContainer((LeafChooseNode) node, null, 0);
			} else {
				if (!(node instanceof ConstructorNodeChooserPointer)) {
					throw new IllegalStateException("Should not be here");
				}

				c = new ConstructorPointerGUIContainer((ConstructorNodeChooserPointer) node, null, 0);
			}

			this.guiComponentMap.put(node, c);
		} else {
			c.refresh();
		}

		return c;
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf,
			int row, boolean hasFocus) {
		if (!(value instanceof AbstractChooseNode)) {
			return new JPanel();
		} else {
			AbstractChooseNode node = (AbstractChooseNode) value;

			try {
				Component c = this.getContainer(node);
				c.doLayout();
				return c;
			} catch (Throwable var10) {
				throw new IllegalStateException(var10);
			}
		}
	}

	private void focusOn(JComponent c) {
		if (c instanceof JTextField) { this.focused = (JTextField) c; this.focused.setText(""); }

	}

	private void deFocus() {
		if (this.focused != null) { this.focused.setText("Put value in here"); }

	}

	public void keyTyped(KeyEvent e) {
	}

	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == 17) {
			this.ctrlPressed = true;
		} else if (e.getKeyCode() != 16) {
			if (this.ctrlPressed && e.getKeyCode() == 68) {
				Object root = this.getModel().getRoot();
				ClassChooseNode node = (ClassChooseNode) root;
				node.setAllExpanded();
			}

			if (this.focusable.size() != 0) {
				char ch = e.getKeyChar();
				if (ch == '\t') {
					JComponent c;
					if (this.focused == null) {
						c = this.focusable.get(this.index);
						this.focusOn(c);
					} else {
						++this.index;
						if (this.index >= this.focusable.size()) { this.index = 0; }

						this.deFocus();
						c = this.focusable.get(this.index);
						this.focusOn(c);
					}
				} else if (ch != '\n') {
					if (this.focused != null) { this.focused.setText(this.focused.getText() + ch); }
				} else {
					ActionListener[] var6;
					int var5 = (var6 = this.focused.getActionListeners()).length;

					for (int var4 = 0; var4 < var5; ++var4) {
						ActionListener l = var6[var4];
						l.actionPerformed(new ActionEvent(this.focused, 0, ""));
					}

					this.focused.setText("");
				}

				this.repaint();
			}
		}
	}

	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == 17) { this.ctrlPressed = false; }

	}

	public void refresh() {
	}

	private class MyEditor implements TreeCellEditor, Serializable {

		private static final long serialVersionUID = 8980517165686533926L;
		private AbstractChooseNode current;

		private MyEditor() {
		}

		public Object getCellEditorValue() {
			return null;
		}

		public boolean isCellEditable(EventObject anEvent) {
			return true;
		}

		public boolean shouldSelectCell(EventObject anEvent) {
			return false;
		}

		public boolean stopCellEditing() {
			return false;
		}

		public void cancelCellEditing() {
			if (this.current instanceof TypableChooseNode) { ((TypableChooseNode) this.current).checkValues(); }

		}

		public void addCellEditorListener(CellEditorListener l) {
		}

		public void removeCellEditorListener(CellEditorListener l) {
		}

		public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded,
				boolean leaf, int row) {
			return InstanceDynamicJTree.this.getContainer((AbstractChooseNode) value);
		}
	}

}
