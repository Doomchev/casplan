package casplan.function.bool;

import casplan.function.BooleanFunction;
import casplan.object.Context;

public class And extends BooleanFunction {
  @Override
  public int getPriority() {
    return 6;
  }
  
  @Override
  public boolean toBoolean(Context context) {
    return params[0].toBoolean(context) && params[1].toBoolean(context);
  }

  
  
  @Override
  public String toString() {
    return params[0].toString() + " && " + params[1].toString();
  }
}
