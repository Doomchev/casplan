package external.dialog;

import casplan.function.StringFunction;
import casplan.object.Context;
import java.io.File;
import javax.swing.JFileChooser;

public class ChooseFile extends StringFunction {
  @Override
  public String toStr(Context context) {
    final JFileChooser fc = new JFileChooser();
    fc.setCurrentDirectory(new File("."));
    int returnVal = fc.showOpenDialog(null);
    if(returnVal == JFileChooser.APPROVE_OPTION) {
      return fc.getSelectedFile().getPath().substring(workingDirectory.length());
    }
    return "";
  }
}
