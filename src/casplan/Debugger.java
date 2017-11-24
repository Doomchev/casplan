package casplan;

import casplan.object.CasObject;
import casplan.object.Context;
import casplan.object.Field;
import casplan.object.UserFunction;
import casplan.object.Variable;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.LinkedList;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;

public class Debugger {
  static JFrame frame = null;
  static JTree tree;
  static DefaultMutableTreeNode root;
  
  private static void init() {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception ex) {
    }
    
    frame = new JFrame("Debugger");
    GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
    Rectangle bounds = env.getMaximumWindowBounds();
    frame.setSize(300, bounds.height);
    frame.setLocation(bounds.width - 300, 0);
    
    root = new DefaultMutableTreeNode("Stack");
    tree = new JTree(root);
    
    JScrollPane treeView = new JScrollPane(tree);
    Insets insets = frame.getInsets();
    treeView.setSize(frame.getWidth() - insets.left - insets.right,
        frame.getHeight() - insets.top - insets.right);
    
    frame.add(treeView);
    frame.setVisible(true);
  }
  
  static DefaultMutableTreeNode addNode(DefaultMutableTreeNode parent
      , String caption) {
    DefaultMutableTreeNode node = new DefaultMutableTreeNode(caption);
    parent.add(node);
    return node;
  }
  
  static void addFieldNode(DefaultMutableTreeNode parent, String caption
      , CasObject value) {
    if(value == null || value.toString().equals("")) return;
    DefaultMutableTreeNode node = addNode(parent, caption + ": "
        + value.toString());
    if(value.isUserObject()) {
      for(HashMap.Entry<Field, CasObject> entry : value.toUserObject()
          .values.entrySet()) {
        addFieldNode(node, entry.getKey().name, entry.getValue());
      }
    }
    
    if(value.isList()) {
      int index = 0;
      for(CasObject item : value.toList().items) {
        addFieldNode(node, String.valueOf(index), item);
        index++;
      }
    }
  }

  
  public static void execute(Context context) {
    if(frame == null) init();
    
    DefaultMutableTreeNode global = addNode(root, "Global variables");
    for(Variable var : Base.globalVariables.values()) {
      addFieldNode(global, var.name, var.value);
    }
    
    /*LinkedList<Context> contexts = new LinkedList<>();
    while(context != null) {
      UserFunction userFunction
          = context.functionCall.params[0].toUserFunction();
      DefaultMutableTreeNode stack = addNode(null
          , context.functionCall.toString());
      for(int index = 0; index < context.params.length; index++) {
        addNode(stack, userFunction.vars[index].name + ": "
            + context.params[index].toString());
      }
      
      contexts.addFirst(context);
      context.prevContext = context;
    }*/
    
    for(int i = 0; i < tree.getRowCount(); i++) tree.expandRow(i);
  }
}
