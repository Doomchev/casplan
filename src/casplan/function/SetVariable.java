package casplan.function;

import casplan.object.CasObject;
import casplan.object.Context;
import casplan.object.Function;

public class SetVariable extends VoidFunctionCall {
  public boolean let = false;

  public SetVariable() {
  }

  public SetVariable(boolean let) {
    this.params = new CasObject[2];
    this.let = let;
  }
  
  @Override
  public int getPriority() {
    return 3;
  }
  
  @Override
  public Function execute(Context context) {
    params[0].setValue(context, params[1].toValue(context), this);
    return null;
  }

  
  
  @Override
  public String toString() {
    return (let ? "let " : "") + params[0].toString() + " = "
        + params[1].toString();
  }
}
