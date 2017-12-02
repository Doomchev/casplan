package casplan.structure;

import casplan.object.*;

public class ForIn extends Function {
  public Parameter value, index;
  public CasObject object;
  public Function[] code;

  @Override
  public Function execute(Context context) {
    return object.toValue(context).iterate(context, this);
  }
  
  
  
  @Override
  public void setChildBreakpoint(Context context, Function func, BPType type) {
    setCodeBreakpoint(context, code, func, type, true);
  }
}
