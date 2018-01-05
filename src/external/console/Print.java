package external.console;

import casplan.object.CasObject;
import casplan.object.Context;
import casplan.object.Function;

public class Print extends Function {
  @Override
  public Function execute(Context context, CasObject[] params) {
    System.out.println(params[0].toStr(context));
    return null;
  }  
}
