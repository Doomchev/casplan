package casplan.structure;

import casplan.object.Context;
import casplan.object.Function;

public class Break extends Function {
  public static Function instance = new Break();

  private Break() {
  }
  
  @Override
  public Function execute(Context context) {
    return this;
  }

  @Override
  public String toString() {
    return "break";
  }
}
