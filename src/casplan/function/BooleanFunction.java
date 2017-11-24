package casplan.function;

import casplan.object.CasObject;
import casplan.object.Context;
import casplan.object.Function;
import casplan.value.CasBoolean;

public class BooleanFunction extends Function {
  @Override
  public Function execute(Context context) {
    toBoolean(context);
    return null;
  }

  @Override
  public CasObject.Type getType(Context context) {
    return CasObject.Type.BOOLEAN;
  }

  @Override
  public CasObject toValue(Context context) {
    return new CasBoolean(toBoolean(context));
  }
}
