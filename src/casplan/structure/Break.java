package casplan.structure;

import casplan.object.Context;
import casplan.object.Function;

public class Break extends Function {
  public static final Function instance = new Break();

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
