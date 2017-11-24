package casplan.function;

import casplan.object.CasObject;
import casplan.object.Context;
import casplan.object.Function;

public class End extends Function {
  @Override
  public Function execute(Context context, CasObject[] params) {
    System.exit(0);
    return null;
  }
}
