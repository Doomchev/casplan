package external;

import javax.swing.JOptionPane;
import casplan.vm.Command;

public class ShowMessage extends Command {
  @Override
  public void execute() {
    JOptionPane.showMessageDialog(null, stringStack[stringStackPos]);
    stringStackPos--;
  }
}
