package external.function.integer;

import casplan.function.IntegerFunction;
import casplan.object.Context;

public class RandomInteger extends IntegerFunction {
  @Override
  public int toInteger(Context context) {
    return (int) Math.floor(Math.random() * params[0].toInteger(context));
  }
}
