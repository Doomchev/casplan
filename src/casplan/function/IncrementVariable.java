package casplan.function;

import casplan.object.Context;
import casplan.object.Function;

public class IncrementVariable extends VoidFunctionCall {
  @Override
  public int getPriority() {
    return 3;
  }
  
  @Override
  public Function execute(Context context) {
    params[0].toVariable().addNumber(context, 1, this);
    return null;
  }

  @Override
  public String toString() {
    return params[0].toString() + "++";
  }
}
