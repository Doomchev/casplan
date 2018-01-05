package external.math;

import casplan.object.CasObject;
import casplan.object.Context;
import casplan.object.Function;
import casplan.value.CasInteger;

public class Abs extends Function {
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
    return Math.abs(params[0].toInteger(context));
  }
  
  

  @Override
  public String toString() {
    return "Math.abs(" + params[0].toString() + " / " + params[1].toString()
        + ")";
  }
}