package casplan.structure;

import casplan.object.CasObject;
import casplan.object.Context;
import casplan.object.Function;

public class If extends Function {
  CasObject condition;
  Function[] thenCode;
  public Function[] elseCode = new Function[0];

  public If(CasObject condition, Function[] thenCode) {
    this.condition = condition;
    this.thenCode = thenCode;
  }
  
  @Override
  public Function execute(Context context) {
    return executeCode(context, condition.toBoolean(context) ? thenCode : elseCode);
  }
  
  

  @Override
  public String toString() {
    return "if(" + condition.toString() + ") {\n" + codeToString(thenCode)
        + tabString + (elseCode.length == 0 ? "" : "} else {\n"
        + codeToString(elseCode) + tabString) + "}";
  }
}
