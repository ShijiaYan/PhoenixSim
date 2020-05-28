package ch.epfl.javancox.experiments.builder.tree_model;

import ch.epfl.general_libraries.clazzes.ClassRepository;
import ch.epfl.general_libraries.clazzes.ClassUtils;
import ch.epfl.general_libraries.clazzes.ObjectRecipe;
import ch.epfl.general_libraries.logging.Logger;
import ch.epfl.general_libraries.utils.Pair;
import ch.epfl.javancox.experiments.builder.swing_gui.InstanceDynamicTreeListener;
import ch.epfl.javancox.experiments.builder.swing_gui.SwingObjectConfigurationAndEnumerator;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.*;


public class ObjectConstructionTreeModel<X> extends DefaultTreeModel implements Serializable {

    private static transient Logger logger = new Logger(ObjectConstructionTreeModel.class);
    private static final long serialVersionUID = 1L;
    public static String defaultFileName = "NewTreeConfiguration";
    private transient ClassRepository classRepo;
    private transient List<InstanceDynamicTreeListener> listeners = new ArrayList<>(1);
    private transient Map<ConstructorChooseNode, Integer> configuredConstructors;
    private transient int nextConstructorIndex = 0;
    private transient boolean ready = false;
    private transient ObjectConstructionTreeModel.TreeModelUIManager toUI;

    public static <W> ObjectConstructionTreeModel<W> loadFromFile(ClassRepository classRepo,
                                                                  Class<? extends W> template)
            throws Exception {
        return loadFromFile(classRepo, template, defaultFileName);
    }

    public static <W> ObjectConstructionTreeModel<W> loadFromFile(ClassRepository classRepo,
                                                                  Class<? extends W> template,
                                                                  String s) throws Exception {
        if (s == null) {
            s = defaultFileName;
        }

        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(s));
            Object or = in.readObject();
            if (or instanceof ObjectConstructionTreeModel) {
                @SuppressWarnings("unchecked")
                ObjectConstructionTreeModel<W> read = (ObjectConstructionTreeModel) or;
                in.close();
                read.classRepo = classRepo;
                read.check();
                return read;
            } else {
                in.close();
                throw new Exception("Wrong format");
            }
        } catch (FileNotFoundException e) {
            System.out.println("Impossible to read file " + new File(s).getAbsolutePath());
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public ObjectConstructionTreeModel(Class<? extends X> newClass, ClassRepository classRepo) throws Exception {
        super(new DefaultMutableTreeNode());
        logger.trace("Initialization of the configuration tree");
        this.configuredConstructors = new HashMap<>();
        this.classRepo = classRepo;
        ClassChooseNode ccn = new ClassChooseNode(newClass,
                AbstractChooseNode.parseAnnotations(newClass.getAnnotations()), this, false);
        this.setRoot(ccn);
    }

    public void removeNodeFromParent(MutableTreeNode node) {
        super.removeNodeFromParent(node);
        this.toUI.removeNode((AbstractChooseNode) node);
    }

    private void check() {
        this.getRootChooseNode().removeInvalidsRecursive();
        this.getRootChooseNode().checkConfiguredRecursive();
    }

    public void setTreeModelUIManager(ObjectConstructionTreeModel.TreeModelUIManager ui) {
        this.toUI = ui;
    }

    public ObjectConstructionTreeModel.TreeModelUIManager getTreeModelUIManager() {
        return this.toUI;
    }

    public void saveFile() {
        String s = SwingObjectConfigurationAndEnumerator.getCurrentFile();
        if (s == "") {
            this.saveFile(defaultFileName);
        } else {
            this.saveFile(s);
        }

    }

    public void saveFile(String file) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
            out.writeObject(this);
            out.flush();
            out.close();
        } catch (Exception var3) {
            var3.printStackTrace();
            this.toUI.showErrorMessage("File error!\nUnable to save file.");
        }

    }

    public boolean isReady() {
        return this.ready;
    }

    private AbstractChooseNode getRootChooseNode() {
        return (AbstractChooseNode) this.getRoot();
    }

    protected boolean containsNode(AbstractChooseNode node) {
        return this.getRootChooseNode().containsNode(node);
    }

    public void addInstanceDynamicTreeListener(InstanceDynamicTreeListener listener) {
        if (this.listeners == null) {
            this.listeners = new ArrayList<>(1);
        }

        this.listeners.add(listener);
        this.getRootChooseNode().checkConfiguredRecursive();
    }

    public void reloadTree() {
        try {
            this.getRootChooseNode().checkConfiguredRecursive();
            this.reload();
            this.toUI.refresh();
        } catch (NullPointerException var2) {
            throw new IllegalStateException(var2);
        }
    }

    protected Collection<AbstractChooseNode> getExpandedNodes() {
        return this.getRootChooseNode().getAllExpandedChildren();
    }

    protected void setExpandedNodes(Collection<AbstractChooseNode> nodes) {
        Iterator var3 = this.getRootChooseNode().getAllChildren().iterator();

        while (var3.hasNext()) {
            AbstractChooseNode node = (AbstractChooseNode) var3.next();
            if (nodes.contains(node)) {
                node.setExpanded(true, false);
            } else {
                node.setExpanded(false, false);
            }
        }

    }

    protected void readyStateChanged() {
        if (this.listeners != null) {
            Iterator var2 = this.listeners.iterator();

            while (var2.hasNext()) {
                InstanceDynamicTreeListener listener = (InstanceDynamicTreeListener) var2.next();
                if (this.getRootChooseNode().isConfigured()) {
                    this.ready = true;
                    listener.treeReadyAction(new ActionEvent(this, (int) System.currentTimeMillis(), "Tree ready"));
                } else if (this.isReady()) {
                    listener.treeNotReadyAction(
                            new ActionEvent(this, (int) System.currentTimeMillis(), "Tree not ready"));
                    this.ready = false;
                }
            }

        }
    }

    protected int addConfiguredConstructor(ConstructorChooseNode toAdd) {
        ++this.nextConstructorIndex;
        if (this.configuredConstructors == null) {
            this.configuredConstructors = new HashMap<>();
        }

        this.configuredConstructors.put(toAdd, this.nextConstructorIndex);
        return this.nextConstructorIndex;
    }

    protected void removeConsrtuctor(ConstructorChooseNode toRemove) {
        if (this.configuredConstructors == null) {
            this.configuredConstructors = new HashMap<>();
        }

        this.configuredConstructors.remove(toRemove);
    }


    protected Map<ConstructorChooseNode, Integer> getConfiguredConstructors() {
        if (this.configuredConstructors == null) {
            this.configuredConstructors = new HashMap<>();
        }

        return this.configuredConstructors;
    }

    protected List<Class<?>> getHeritedClasses(Class<?> c) throws Exception {
        logger.debug("Looking for classes extending " + c.getSimpleName() + "...");
        HashSet<Class<?>> classes = new HashSet<>();
        classes.add(c);
        Iterator ite = this.classRepo.getClasses(c).iterator();

        while (ite.hasNext()) {
            Class<?> cl = (Class) ite.next();
            if (ClassUtils.isHeritingFrom(cl, c)) {
                classes.addAll(this.getHeritedClasses(cl));
            }
        }

        logger.trace("Found " + classes.size() + " ones");
        return new ArrayList<>(classes);
    }

    public DefinitionIterator getObjectDefinitionIterator() {
        return ((ClassChooseNode) this.getRoot()).getObjectDefinitionIterator();
    }

    public ObjectConstructionTreeModel.ObjectIterator<X> getObjectIterator() {
        ObjectConstructionTreeModel.ObjectIterator<X> ret = new ObjectConstructionTreeModel.ObjectIterator<>(
                this.getRootChooseNode());
        return ret;
    }

    public static class ObjectIterator<X> implements Iterator<X> {

        Iterator<Pair<Object, ObjectRecipe>> rootIterator;
        AbstractChooseNode rootClone;

        public ObjectIterator(AbstractChooseNode root) {
            this.rootClone = (AbstractChooseNode) root.clone();
            this.rootClone.resetIterators();
            this.rootIterator = this.rootClone.iterator();
        }

        public boolean hasNext() {
            return this.rootClone.isConfigured() && this.rootIterator.hasNext();
        }

        public X next() {
            if (!this.rootClone.isConfigured()) {
                throw new NoSuchElementException();
            } else {
                Pair<X, ObjectRecipe> pair = (Pair) this.rootIterator.next();
                return pair != null ? pair.getFirst() : null;
            }
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        public void cleanUp() {
            this.rootClone.cleanUp();
        }
    }

    public interface TreeModelUIManager {

        void showErrorMessage(String var1);

        void removeNode(AbstractChooseNode var1);

        void refresh();
    }
}
