package casplan.function.bool;

import casplan.object.Context;
import casplan.object.Function;

public class LessOrEqual extends Function {
  @Override
  public int getPriority() {
    return 10;
  }
  
  @Override
  public boolean toBoolean(Context context) {
    return params[0].toInteger(context) <= params[1].toInteger(context);
  }
  
  

  @Override
  public String toString() {
    return params[0].toString() + " <= " + params[1].toString();
  }
}
