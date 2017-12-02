package casplan.function;

import casplan.object.Context;
import casplan.object.Function;

public class ObjectFunction extends Function {
  @Override
  public Function execute(Context context) {
    toValue(context);
    return null;
  }
  
  @Override
  public Type getType(Context context) {
    return toValue(context).getType(context);
  }
  
  @Override
  public int toInteger(Context context) {
    return toValue(context).toInteger(context);
  }
  
  @Override
  public String toStr(Context context) {
    return toValue(context).toStr(context);
  }
}
