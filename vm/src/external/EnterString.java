package external;

import javax.swing.JOptionPane;
import casplan.vm.Command;

public class EnterString extends Command {
  @Override
  public void execute() {
    stringStack[stringStackPos]
        = JOptionPane.showInputDialog(stringStack[stringStackPos]);
  }
}
