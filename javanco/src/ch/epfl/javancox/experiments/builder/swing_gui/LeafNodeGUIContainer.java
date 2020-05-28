//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ch.epfl.javancox.experiments.builder.swing_gui;

import java.awt.Color;
import java.awt.LayoutManager;
import ch.epfl.javancox.experiments.builder.tree_model.AbstractChooseNode;
import ch.epfl.javancox.experiments.builder.tree_model.ArrayChooseNode;
import ch.epfl.javancox.experiments.builder.tree_model.BooleanChooseNode;
import ch.epfl.javancox.experiments.builder.tree_model.ClassChooseNode;
import ch.epfl.javancox.experiments.builder.tree_model.EnumChooseNode;
import ch.epfl.javancox.experiments.builder.tree_model.ErrorChooseNode;
import ch.epfl.javancox.experiments.builder.tree_model.LeafChooseNode;
import ch.epfl.javancox.experiments.builder.tree_model.TypableChooseNode;
import ch.epfl.javancox.experiments.builder.tree_model.UntypeableArrayInstanceNode;


public class LeafNodeGUIContainer extends AbstractGUIContainer {

	private static final Color LEAF_COLOR = new Color(235, 240, 240);
	private static final Color ERROR_COLOR = new Color(1.0F, 0.3F, 0.3F);
	private static final long serialVersionUID = 1L;

	public LeafNodeGUIContainer(LeafChooseNode node, LayoutManager man, int prefix) {
		super(node, man, prefix);
		this.setBackground(LEAF_COLOR);
		this.refresh();
	}

	public void refreshImpl() {
		AbstractChooseNode parent = (AbstractChooseNode) this.absNode.getParent();
		if (parent instanceof BooleanChooseNode) {
			this.setIcon("boolean.png");
		} else if (parent instanceof EnumChooseNode) {
			this.setIcon("class.png");
		} else if (parent instanceof UntypeableArrayInstanceNode) {
			this.setIcon("class.png");
		} else if (parent instanceof ArrayChooseNode) {
			this.setIcon("other.png");
		} else if (parent instanceof ClassChooseNode) {
			this.setIcon("other.png");
			if (!(this.absNode instanceof LeafChooseNode)) { throw new IllegalStateException(); }

			if (!((LeafChooseNode) this.absNode).isNull()) {
				if (!(this.absNode instanceof ErrorChooseNode)) { throw new IllegalStateException(); }

				this.setBackground(ERROR_COLOR);
			}
		} else {
			TypableChooseNode p = (TypableChooseNode) this.absNode.getParent();
			if (p.isBoolean()) {
				this.setIcon("boolean.png");
			} else if (p.isInt()) {
				this.setIcon("integer.png");
			} else if (p.isClass()) {
				this.setIcon("class.png");
			} else if (p.isDouble()) {
				this.setIcon("double.png");
			} else if (p.isLong()) {
				this.setIcon("long.png");
			} else {
				this.setIcon("other.png");
			}
		}

	}
}
