package external.dialog;

import casplan.function.StringFunction;
import casplan.object.Context;
import javax.swing.JOptionPane;

public class EnterString extends StringFunction {
  @Override
  public String toStr(Context context) {
    return JOptionPane.showInputDialog(params[0].toStr(context));
  }
}
