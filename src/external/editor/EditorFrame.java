package external.editor;

import javax.swing.JFrame;

public class EditorFrame extends javax.swing.JFrame {
  public EditorFrame() {
    initComponents();
  }

  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    jScrollPane1 = new javax.swing.JScrollPane();
    manager = new javax.swing.JTree();
    canvas = new Editor.Canvas();

    setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
    addWindowListener(new java.awt.event.WindowAdapter() {
      public void windowClosing(java.awt.event.WindowEvent evt) {
        formWindowClosing(evt);
      }
    });

    javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("root");
    manager.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
    jScrollPane1.setViewportView(manager);

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addGap(0, 0, 0)
        .addComponent(canvas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
      .addComponent(canvas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

  private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
    editor.onClose();
  }//GEN-LAST:event_formWindowClosing

  public static EditorFrame execute() {
    EditorFrame frame = new EditorFrame();
    java.awt.EventQueue.invokeLater(() -> {
      frame.setVisible(true);
    });
    
    frame.setExtendedState(frame.getExtendedState()|JFrame.MAXIMIZED_BOTH);
    
    return frame;
  }
  
  public Editor editor;

  // Variables declaration - do not modify//GEN-BEGIN:variables
  public java.awt.Canvas canvas;
  private javax.swing.JScrollPane jScrollPane1;
  public javax.swing.JTree manager;
  // End of variables declaration//GEN-END:variables
}
