package casplan.function;

import casplan.object.*;

public class Breakpoint extends Function {
  @Override
  public Function execute(Context context, CasObject[] params) {
    setBreakpointInsideParent(context, BPType.STEP);
    return null;
  }
  
  
  
  
  @Override
  public void removeBreakpoint() {
  }
  
  

  @Override
  public String toString() {
    return "Breakpoint";
  }
}
