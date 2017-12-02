package casplan.structure;

import casplan.object.*;

public class If extends Function {
  public CasObject condition;
  public Function[] thenCode;
  public Function[] elseCode = new Function[0];
  
  @Override
  public Function execute(Context context) {
    return executeCode(context, condition.toBoolean(context)
        ? thenCode : elseCode, this);
  }
  
  
  
  @Override
  public void setNextBreakpoint(Context context, BPType type) {
    breakpoint = BPType.STEP_INTO;
  }
  
  @Override
  public void setChildBreakpoint(Context context, Function func, BPType type) {
    setCodeBreakpoint(context, arrayContains(thenCode, func) ? thenCode
        : elseCode, func, type, false);
  }
  
  

  @Override
  public String toString() {
    return "if(" + condition.toString() + ") {\n" + codeToString(thenCode)
        + tabString + (elseCode.length == 0 ? "" : "} else {\n"
        + codeToString(elseCode) + tabString) + "}";
  }
}
