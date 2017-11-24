package casplan.function;

import casplan.Base;
import casplan.object.CasObject;
import casplan.object.Context;
import casplan.object.Function;

public class Stop extends Function {
  @Override
  public Function execute(Context context, CasObject[] params) {
    Base.stop(context);
    return null;
  }
}
