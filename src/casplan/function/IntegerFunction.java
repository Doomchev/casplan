package casplan.function;

import casplan.object.CasObject;
import casplan.object.Context;
import casplan.object.Function;
import casplan.value.CasInteger;

public class IntegerFunction extends Function {
  @Override
  public Function execute(Context context) {
    toInteger(context);
    return null;
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
  public String toStr(Context context) {
    return String.valueOf(toInteger(context));
  }
}
