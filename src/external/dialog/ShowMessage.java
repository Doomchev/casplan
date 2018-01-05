package external.dialog;

import casplan.object.Function;
import javax.swing.JOptionPane;
import casplan.object.CasObject;
import casplan.object.Context;

public class ShowMessage extends Function {
  @Override
  public Function execute(Context context, CasObject[] params) {
    JOptionPane.showMessageDialog(null, params[0].toStr(context));
    return null;
  }
}
