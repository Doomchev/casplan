package casplan.function;

import casplan.object.CasObject;
import casplan.object.Context;
import casplan.object.Function;
import casplan.value.CasString;

public class StringFunction extends Function {
  @Override
  public Function execute(Context context) {
    toStr(context);
    return null;
  }

  @Override
  public Type getType(Context context) {
    return Type.STRING;
  }

  @Override
  public CasObject toValue(Context context) {
    return new CasString(toStr(context));
  }
}
