package ch.epfl.javancox.experiments.builder.swing_gui;

import ch.epfl.general_libraries.clazzes.ClassRepository;
import ch.epfl.general_libraries.experiment_aut.Experiment.globals;
import ch.epfl.general_libraries.gui.ProgressBarDialog;
import ch.epfl.general_libraries.logging.Logger;
import ch.epfl.javancox.experiments.builder.object_enum.AbstractEnumerator;
import ch.epfl.javancox.experiments.builder.tree_model.ObjectConstructionTreeModel;
import ch.epfl.javancox.experiments.builder.tree_model.ObjectConstructionTreeModel.ObjectIterator;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;


public class SwingObjectConfigurationAndEnumerator<X> implements InstanceDynamicTreeListener {

    private static final long serialVersionUID = 1L;
    private static Logger logger;
    private JButton go;
    private JTextField readThreads;
    protected JPanel info;
    protected HouseMadeTree<X> tree;
    private Class<? extends X> someClass;
    private ClassRepository classRepo;
    private AbstractEnumerator<X> manager;
    private JLabel isReadyLabel;
    private final JFileChooser fc;
    private static String currentFile = "";

    public SwingObjectConfigurationAndEnumerator(Class<? extends X> c, AbstractEnumerator<X> m) {
        this(c, m, null);
    }

    public SwingObjectConfigurationAndEnumerator(Class<? extends X> c, AbstractEnumerator<X> m, String[] prefixes) {
        this.go = new JButton("Run");
        this.readThreads = new JTextField();
        this.info = new JPanel();
        this.fc = new JFileChooser();
        this.fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
        logger = new Logger(SwingObjectConfigurationAndEnumerator.class);
        this.manager = m;
        logger.info("Creation of the class repository");
        this.classRepo = new ClassRepository(prefixes);
        logger.info("Done with class repo");
        if (globals.classRepo == null) {
            globals.classRepo = this.classRepo;
        }

        this.someClass = c;
    }

    public void show() {
        this.show(null);
    }

    public void show(String file) {
        logger.info("Loading initial interface (can take time...)");

        try {
            this.isReadyLabel = new JLabel("Not ready");
            if (file != null) {
                logger.debug("Loading experiment tree from file " + file);
                this.tree = new HouseMadeTree<>(
                        ObjectConstructionTreeModel.loadFromFile(this.classRepo, this.someClass, file));
            } else {
                this.setNewTreeWithDialog();
            }

            if (this.tree == null) return;

            this.tree.getModel().addInstanceDynamicTreeListener(this);
            this.info = new JPanel();
            logger.trace("Create frame");
            final JFrame f = new JFrame();
            f.setSize(1000, 700);
            f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT) {

                private static final long serialVersionUID = -8179923347528972679L;

                public int getLastDividerLocation() {
                    return 620;
                }

                public int getDividerLocation() {
                    return 620;
                }
            };
            split.setDividerLocation(620);
            split.add(this.tree);
            this.readThreads.setColumns(2);
            this.readThreads.setHorizontalAlignment(0);
            JLabel nbThreads = new JLabel("Threads to use:");
            this.go = new JButton("Run");
            this.go.addActionListener(e -> {
                if (SwingObjectConfigurationAndEnumerator.this.go.getText().equals("Stop")) {
                    SwingObjectConfigurationAndEnumerator.this.manager.stopTreeManager();
                    SwingObjectConfigurationAndEnumerator.this.go.setText("Run");
                } else if (SwingObjectConfigurationAndEnumerator.this.go.getText().equals("Run")) {
                    SwingObjectConfigurationAndEnumerator.this.launchExecution();
                }

            });
            this.info.add(this.isReadyLabel);
            this.info.add(this.go);
            this.info.add(nbThreads);
            this.info.add(this.readThreads);
            JButton clearDb = new JButton("ClearDB");
            clearDb.addActionListener(e -> SwingObjectConfigurationAndEnumerator.this.manager.clearEnumerationResults());
            clearDb.setFocusable(false);
            this.info.add(clearDb);
            JButton clearCache = new JButton("Clear Cache");
            clearCache.addActionListener(e -> SwingObjectConfigurationAndEnumerator.this.manager.clearCaches());
            clearCache.setFocusable(false);
            this.info.add(clearCache);
            JButton loadTree = new JButton("Load Tree");
            loadTree.addActionListener(e -> {
                f.setVisible(false);
                SwingObjectConfigurationAndEnumerator.this.show(null);
            });
            loadTree.setFocusable(false);
            this.info.add(loadTree);
            split.add(this.info);
            f.add(split);
            logger.trace("Setting the frame visible");
            f.setVisible(true);
            f.setResizable(false);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public synchronized void treeReadyAction(ActionEvent e) {
        this.isReadyLabel.setText("Tree ready for : " + this.tree.getInstancesCount() + " experiments");
        this.go.setVisible(true);
    }

    public synchronized void treeNotReadyAction(ActionEvent e) {
        this.isReadyLabel.setText("No result, Tree not ready.");
        this.go.setVisible(false);
    }

    private void delayedDisplay(final String message) {
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, message, "Error",
                JOptionPane.ERROR_MESSAGE));
    }

    private void launchExecution() {
        int t = 1;

        try {
            String readThread = this.readThreads.getText();
            t = Integer.parseInt(readThread);
        } catch (NumberFormatException nfe) {
            if (!readThreads.getText().equals("")) {
                nfe.printStackTrace();
                System.out.println("\n\t:" + readThreads.getText());
            }
        }

        ObjectIterator ite;
        try {
            ite = this.tree.getModel().getObjectIterator();
            if (!ite.hasNext()) {
                this.delayedDisplay("No experiment to run.");
                return;
            }
        } catch (Exception e) {
            this.delayedDisplay(
                    "Error (" + e.getClass() + ": " + e.getLocalizedMessage() + ") in batch creation, no action");
            e.printStackTrace();
            return;
        }

        ProgressBarDialog progress = new ProgressBarDialog(this.tree.getInstancesCount());
        progress.setDialogVisible();
        this.go.setText("Stop");
        this.manager.runInNewThread(this.getCallBack(), ite, progress, t);
    }

    private Runnable getCallBack() {
        return () -> SwingObjectConfigurationAndEnumerator.this.go.setText("Run");
    }

    private void setNewTreeWithDialog() {
        Object[] options = new Object[]{"New", "Load Default File", "Load File...", "Load Default Config"};
        int n = JOptionPane.showOptionDialog(null,
                "Load an existing configuration tree, or create a new one?", "New Tree Configuration",
                JOptionPane.DEFAULT_OPTION
                , JOptionPane.QUESTION_MESSAGE,
                null // do not use a custom icon
                , options // the titles of buttons
                , options[0] // default button title
        );
        if (n >= 0) {
            logger.trace("User selected option: " + options[n]);
            switch (n) {
                default:
                    System.exit(0);
                    break;
                case 0:
                    this.createFullNewTree();
                    break;
                case 1:
                    try {
                        ObjectConstructionTreeModel<X> treeMod =
                                ObjectConstructionTreeModel.loadFromFile(this.classRepo, this.someClass);
                        this.tree = new HouseMadeTree<>(treeMod);
                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(null,
                                "File error!\nA new configuration tree will be created.", "Error",
                                JOptionPane.ERROR_MESSAGE);
                        this.createFullNewTree();
                    }
                    break;
                case 2:
                    try {
                        String s = null;
                        this.fc.setDialogTitle("Load File...");
                        this.fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                        int ret = this.fc.showOpenDialog(null);
                        if (ret == 1) {
                            setNewTreeWithDialog();
                            break;
                        }
                        if (ret == 0) {
                            s = this.fc.getSelectedFile().getCanonicalPath();
                            currentFile = s;
                        }
                        System.out.println("Loading file:\n\t" + currentFile);
                        this.tree = new HouseMadeTree<>(
                                ObjectConstructionTreeModel.loadFromFile(this.classRepo, this.someClass, s));
                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(null,
                                "File error!\nA new configuration tree will be created.", "Error",
                                JOptionPane.ERROR_MESSAGE);
                        this.createFullNewTree();
                    }
                    break;
                case 3:
                    try {
                        this.tree = new HouseMadeTree<>(
                                ObjectConstructionTreeModel.loadFromFile(this.classRepo, this.someClass));
                        this.launchExecution();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }

        }
    }

    private void createFullNewTree() {
        try {
            this.tree = new HouseMadeTree<>(new ObjectConstructionTreeModel<>(this.someClass, this.classRepo));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static String getCurrentFile() {
        return currentFile;
    }
}
