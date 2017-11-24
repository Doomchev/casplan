package casplan.function.operator;

import casplan.object.CasObject;
import casplan.object.Context;
import casplan.object.Function;
import casplan.value.CasInteger;

public class Subtraction extends Function {
  @Override
  public int getPriority() {
    return 13;
  }
  
  @Override
  public CasObject.Type getType(Context context) {
    return CasObject.Type.INTEGER;
  }

  @Override
  public CasObject toValue(Context context) {
    return new CasInteger(params[0].toInteger(context)
        - params[1].toInteger(context));
  }

  @Override
  public int toInteger(Context context) {
    return params[0].toInteger(context) - params[1].toInteger(context);
  }

  
  
  @Override
  public String toString() {
    return addBrackets(params[0].toString() + " - " + params[1].toString());
  }
}
