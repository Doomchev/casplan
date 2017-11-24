package casplan.function;

import casplan.object.CasObject;
import casplan.object.Context;
import casplan.object.Function;

public class VoidFunctionCall extends Function {
  @Override
  public CasObject.Type getType(Context context) {
    runtimeError("Function does not return value");
    return null;
  }
  
  @Override
  public boolean returnsValue() {
    return false;
  }

  @Override
  public CasObject toValue(Context context) {
    runtimeError("Function does not return value");
    return null;
  }
}
