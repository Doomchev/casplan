package casplan.function.object;

import casplan.object.CasObject;
import casplan.object.Context;
import casplan.object.Function;

public class GetFunction extends Function {
  Function function;

  public GetFunction(Function function) {
    this.function = function;
  }
  
  @Override
  public CasObject toValue(Context context) {
    return function;
  }

  @Override
  public Function toFunction() {
    return function;
  }
  
  

  @Override
  public String toString() {
    return "function" + function.getParams() + " {\n" + function.getCode()
        + tabString + "}";
  }
}
