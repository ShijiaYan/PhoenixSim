package ch.epfl.javancox.experiments.builder.swing_gui;

import ch.epfl.general_libraries.utils.TypeParser;
import ch.epfl.javanco.base.Javanco;
import ch.epfl.javancox.experiments.builder.tree_model.AbstractChooseNode;
import ch.epfl.javancox.experiments.builder.tree_model.AbstractChooseNode.ActionItem;
import ch.epfl.javancox.experiments.builder.tree_model.AbstractChooseNode.ActionStructure;
import ch.epfl.javancox.experiments.builder.tree_model.AbstractChooseNode.SeparatorItem;
import ch.epfl.javancox.experiments.builder.tree_model.AbstractParameterChooseNode;
import ch.epfl.javancox.experiments.builder.tree_model.LeafChooseNode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;


public abstract class AbstractGUIContainer extends JPanel {

    protected static int lineHeight = 22;
    static final String FONT_DEFAULT = "Helvetica Neue";
    private final JLabel textLabel;
    private static final Font menuFont = new Font("Helvetica Neue", Font.PLAIN, 12);
    private static final long serialVersionUID = 1L;
    protected static transient Font defaultFontBold = new Font("Helvetica Neue", Font.BOLD, 13);
    protected static transient Font defaultFontPlain = new Font("Helvetica Neue", Font.PLAIN, 13);
    protected transient FontMetrics defaultFontMetrics;
    protected final AbstractChooseNode absNode;
    protected transient int lines;
    protected transient int prefix;
    private static final int maxLength = 150;

    public abstract void refreshImpl();

    public AbstractGUIContainer(final AbstractChooseNode absNode, LayoutManager man, int prefix) {
        super(man);
        this.absNode = absNode;
        this.prefix = prefix;
        this.setBackground(new Color(240, 240, 240));
        this.textLabel = new JLabel();
        this.textLabel.setBackground(new Color(240, 240, 240));
        if (absNode instanceof LeafChooseNode) {
            Font newFont = new Font("Helvetica Neue", Font.PLAIN, 12);
            this.textLabel.setFont(newFont);
            this.defaultFontMetrics = this.textLabel.getFontMetrics(newFont);
        } else {
            this.textLabel.setFont(defaultFontBold);
            this.defaultFontMetrics = this.textLabel.getFontMetrics(defaultFontBold);
        }

        this.add(this.textLabel);
        this.setBorder(BorderFactory.createEtchedBorder(1));
        MouseListener mouseL = new MouseListener() {

            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == 3) {
                    JPopupMenu menu = AbstractGUIContainer.this.buildMenu(absNode);
                    menu.setFont(new Font(FONT_DEFAULT, Font.BOLD, 11));
                    menu.show((JComponent) e.getSource(), e.getX(), e.getY());
                }

            }

            public void mouseClicked(MouseEvent arg0) {
                if (arg0.getClickCount() == 2) {
                    absNode.setExpanded(!absNode.isExpanded());
                }

            }

            public void mouseEntered(MouseEvent arg0) {
            }

            public void mouseExited(MouseEvent arg0) {
            }

            public void mousePressed(MouseEvent arg0) {
            }
        };
        this.addMouseListener(mouseL);
    }

    private JPopupMenu buildMenu(AbstractChooseNode node) {
        JPopupMenu root = new JPopupMenu();

        for (ActionItem item : node.getActions()) {
            if (item instanceof ActionStructure) {
                JMenu menu = this.buildSubMenu((ActionStructure) item, node, root);
                menu.setFont(menuFont);
                root.add(menu);
            } else if (item instanceof SeparatorItem) {
                root.addSeparator();
            } else {
                root.add(this.buildItem(item, node));
            }
        }

        return root;
    }

    private JMenu buildSubMenu(final ActionStructure s, final AbstractChooseNode node, final JPopupMenu root) {
        JMenu menu = new JMenu(this.adaptMenuText(s.text));
        menu.setFont(menuFont);
        if (s.actionName != null) {
            menu.addMouseListener(new MouseListener() {

                public void mouseReleased(MouseEvent e) {
                }

                public void mousePressed(MouseEvent e) {
                }

                public void mouseExited(MouseEvent e) {
                }

                public void mouseEntered(MouseEvent e) {
                }

                public void mouseClicked(MouseEvent e) {
                    node.actionPerformed(s.actionName);
                    root.setVisible(false);
                }
            });
        }

        for (ActionItem item : s.childs) {
            if (item instanceof ActionStructure) {
                menu.add(this.buildSubMenu((ActionStructure) item, node, root));
            } else if (item instanceof SeparatorItem) {
                menu.addSeparator();
            } else {
                menu.add(this.buildItem(item, node));
            }
        }

        return menu;
    }

    private JMenuItem buildItem(final ActionItem item, final AbstractChooseNode node) {
        JMenuItem menuItem = new JMenuItem(item.text);
        menuItem.setFont(menuFont);
        menuItem.addActionListener(o -> node.actionPerformed(item.actionName));
        return menuItem;
    }

    String adaptMenuText(String s) {
        return s.length() <= 150 ? s : "<html><table border='1'>" + this.tablizeLinear(s) + "</table></html>";
    }

    String tablizeLinear(String s) {
        ArrayList<String> ret = this.tablize(s, maxLength);
        StringBuilder sb = new StringBuilder();

        for (String str : ret) {
            sb.append(str).append("<br>");
        }

        return sb.toString();
    }

    ArrayList<String> tablize(String s, int maxLength) {
        char[] breakers = new char[]{' ', '.'};
        ArrayList<String> ret = new ArrayList<>();
        if (s.length() <= maxLength) {
            ret.add(s);
            return ret;
        } else {
            int currentIndex = maxLength;
            int previousIndex = 0;
            int breakerIndex = 0;

            while (currentIndex < s.length()) {
                int currentLines = ret.size();

                for (int i = currentIndex; i > previousIndex; --i) {
                    if (s.charAt(i) == breakers[breakerIndex]) {
                        String sub = s.substring(previousIndex, i);
                        ret.add(sub);
                        previousIndex = i;
                        currentIndex = i + maxLength;
                        break;
                    }
                }

                if (currentLines == ret.size()) {
                    ++breakerIndex;
                }
            }

            ret.add(s.substring(previousIndex));
            return ret;
        }
    }

    public void setIcon(String string) {
        try {
            Javanco.initJavanco();
        } catch (Exception var4) {
            throw new IllegalStateException(var4);
        }

        String iconPath = System.getProperty("JAVANCO_HOME") + System.getProperty("file.separator") + "img/" + string;
        ImageIcon image = new ImageIcon(iconPath);
        this.textLabel.setIcon(image);
    }

    public void refresh() {
        this.textLabel.setForeground(TypeParser.parseColor(this.absNode.getColor()));
        String text = this.absNode.getText();
        String display;
        if (text.length() > 130 - 2 * this.prefix) {
            display = text.substring(0, 120 - 2 * this.prefix) + " ...";
            this.textLabel.setToolTipText(text);
        } else {
            display = text;
            this.textLabel.setToolTipText(null);
        }

        if (this.absNode.isConfigured() && this.absNode instanceof AbstractParameterChooseNode) {
            display = display + " (" + this.absNode.getInstancesCount() + ")";
        }

        this.textLabel.setText(display);
        int width = this.defaultFontMetrics.stringWidth(display);
        this.textLabel.setMinimumSize(new Dimension(width, lineHeight - 2));
        this.refreshImpl();
    }

    public void setPrefix(int prefix) {
        this.prefix = prefix;
    }

    public int getPrefix() {
        return this.prefix;
    }

    public boolean isExpanded() {
        return this.absNode.isExpanded();
    }

    public void setExpanded(boolean b) {
        this.absNode.setExpanded(b);
    }
}
