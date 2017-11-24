package casplan.function;

import casplan.object.CasObject;
import casplan.object.Context;
import casplan.object.Function;
import casplan.structure.ForIn;

public class ListFunction extends Function {
  @Override
  public Function execute(Context context) {
    toList();
    return null;
  }
  
  @Override
  public CasObject.Type getType(Context context) {
    return Type.OBJECT;
  }

  @Override
  public CasObject toValue(Context context) {
    return toList();
  }
  
  @Override
  public int toInteger(Context context) {
    return toList().toInteger(context);
  }
  
  @Override
  public String toStr(Context context) {
    return toList().toStr(context);
  }
}
