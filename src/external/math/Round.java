package external.math;

import casplan.function.IntegerFunction;
import casplan.object.Context;

public class Round extends IntegerFunction {
  @Override
  public int toInteger(Context context) {
    return (int) Math.round(params[0].toInteger(context));
  }
}
