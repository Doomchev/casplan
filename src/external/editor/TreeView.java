package external.editor;

import casplan.list.CasList;
import casplan.object.*;
import casplan.value.CasInteger;
import java.awt.event.MouseEvent;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

public class TreeView extends CasObject {
  public JTree tree;
  UserObject object;
  Node selected;
  UserFunction onRightClick, onSelect;

  public TreeView(JTree tree) {
    this.tree = tree;
    TreeView treeView = this;
    tree.addMouseListener(new java.awt.event.MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if(e.getButton() == MouseEvent.BUTTON3) {
          CasObject[] funcParams = new CasObject[2];
          funcParams[0] = new CasInteger(e.getX());
          funcParams[1] = new CasInteger(e.getY());
          execute(onRightClick, treeView, funcParams);
        }
      }
    });
    
    tree.setModel(new DefaultTreeModel(new Node("root", object)));
    
    tree.getSelectionModel()
        .setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    tree.addTreeSelectionListener((TreeSelectionEvent e) -> {
      selected = (Node) tree.getLastSelectedPathComponent();
      execute(onSelect, treeView);
    });
  }
  
  
      
  final static Field refreshField = Field.get("refresh");
  final static Field selectedField = Field.get("selected");
  @Override
  public CasObject getField(Field field, Function caller) {
    if(field == refreshField) {
      return new RefreshTreeView();
    } else if(field == selectedField) {
      if(selected == null) return Null.instance;
      return selected.value;
    }
    caller.error("TreeView object has no readable field \"" + field.name + "\"");
    return null;
  }

  final static Field objectField = Field.get("object");
  final static Field onRightClickField = Field.get("onRightClick");
  final static Field onSelectField = Field.get("onSelect");
  
  @Override
  public void setField(Field field, CasObject toValue, Function caller) {
    if(field == objectField) {
      object = toValue.toUserObject();
      ((Node) tree.getModel().getRoot()).value = object;
    } else if(field == onRightClickField) {
      onRightClick = toValue.toUserFunction();
    } else if(field == onSelectField) {
      onSelect = toValue.toUserFunction();
    } else {
      caller.error("TreeView object has no writable field \"" + field.name + "\"");
    }
  }
  
  

  @Override
  public String toString() {
    return "TreeView";
  }
  
  
  public static class Node extends DefaultMutableTreeNode {
    public UserObject parentObject;
    public Field field;
    
    public CasList parentList;
    public int index;
    
    public CasObject value;

    public Node(String text, CasObject value) {
      super(text);
      this.value = value;
    }

    public Node(String text, UserObject parentObject, Field field
        , CasObject value) {
      super(text);
      this.parentObject = parentObject;
      this.field = field;
      this.value = value;
    }

    public Node(String text, CasList parentList, int index, CasObject value) {
      super(text);
      this.parentList = parentList;
      this.index = index;
      this.value = value;
    }
  }
  
  
  
  public static class RefreshTreeView extends Function {
    @Override
    public Function execute(Context context, CasObject[] params) {
      TreeView treeObject = (TreeView) context.functionObject;
      JTree tree = treeObject.tree;
      Node rootNode = (Node) tree.getModel().getRoot();
      rootNode.value = treeObject.object;
      
      treeObject.object.fillNode(rootNode);
      
      for(int i = 0; i < tree.getRowCount(); i++) tree.expandRow(i);
      
      return null;
    }
  }
}
