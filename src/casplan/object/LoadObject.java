package casplan.object;

import casplan.Parser;
import java.io.File;

public class LoadObject extends Function {
  @Override
  public CasObject toValue(Context context) {
    String fileName = params[0].toStr(context);
    if(!new File(fileName).exists()) return Null.instance;
    return Parser.importObject(fileName);
  }
}
