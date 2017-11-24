package casplan.function.operator;

import casplan.object.CasObject;
import casplan.object.Context;
import casplan.object.Function;
import casplan.value.CasInteger;

public class Division extends Function {
  @Override
  public int getPriority() {
    return 14;
  }
  
  @Override
  public CasObject.Type getType(Context context) {
    return CasObject.Type.INTEGER;
  }

  @Override
  public CasObject toValue(Context context) {
    return new CasInteger(toInteger(context));
  }

  @Override
  public int toInteger(Context context) {
    return (int) Math.floor(params[0].toInteger(context)
        / params[1].toInteger(context));
  }
  
  

  @Override
  public String toString() {
    return "Math.floor(" + params[0].toString() + " / " + params[1].toString()
        + ")";
  }
}