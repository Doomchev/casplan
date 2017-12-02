package casplan.structure;

import casplan.object.CasObject;
import casplan.object.Context;
import casplan.object.Function;

public class Return extends Function {
  public static final Function instance = new Return();

  public Return() {
    this.params = new CasObject[1];
  }
  
  @Override
  public Function execute(Context context) {
    context.returnedValue = params[0].toValue(context);
    return instance;
  }

  

  @Override
  public void setNextBreakpoint(Context context, BPType type) {
    super.setNextBreakpoint(context, BPType.STEP_OUT);
  }
  
  

  @Override
  public String toString() {
    return "return " + params[0].toString();
  }
}
