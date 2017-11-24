package casplan.structure;

import casplan.object.CasObject;
import casplan.object.Context;
import casplan.object.Function;

public class Return extends Function {
  public static Function instance = new Return(null);

  public Return(CasObject valueFunction) {
    this.params = new CasObject[1];
    this.params[0] = valueFunction;
  }
  
  @Override
  public Function execute(Context context) {
    context.returnedValue = params[0].toValue(context);
    return instance;
  }
  
  

  @Override
  public String toString() {
    return "return " + params[0].toString();
  }
}
