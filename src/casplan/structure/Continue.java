package casplan.structure;

import casplan.object.Context;
import casplan.object.Function;

public class Continue extends Function {
  public static final Function instance = new Continue();

  private Continue() {
  }

  @Override
  public Function execute(Context context) {
    return this;
  }

  @Override
  public String toString() {
    return "continue";
  }
}
