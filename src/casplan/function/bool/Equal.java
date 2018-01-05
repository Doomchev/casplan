package casplan.function.bool;

import casplan.function.BooleanFunction;
import casplan.object.CasObject;
import casplan.object.Context;

public class Equal extends BooleanFunction {
  @Override
  public int getPriority() {
    return 10;
  }
  
  @Override
  @SuppressWarnings("null")
  public boolean toBoolean(Context context) {
    CasObject value0 = params[0].toValue(context);
    CasObject value1 = params[1].toValue(context);
    Type type0 = value0.getType(context);
    Type type1 = value1.getType(context);
    if(type0 == Type.STRING || type1 == Type.STRING) {
      return value0.toStr(context).equals(value1.toStr(context));
    } else if(type0 == Type.INTEGER || type1 == Type.INTEGER) {
      return value0.toInteger(context) == value1.toInteger(context);
    } else {
      return value0 == value1;
    }
  }
  
  

  @Override
  public String toString() {
    return params[0].toString() + " == " + params[1].toString();
  }
}
