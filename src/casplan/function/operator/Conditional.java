package casplan.function.operator;

import casplan.object.CasObject;
import casplan.object.Context;
import casplan.object.Function;
import casplan.value.CasBoolean;
import casplan.value.CasInteger;
import casplan.value.CasString;

public class Conditional extends Function {
  public Conditional(CasObject condition, CasObject forTrue, CasObject forFalse) {
    this.params = new CasObject[3];
    this.params[0] = condition;
    this.params[1] = forTrue;
    this.params[2] = forFalse;
  }
  
  @Override
  public Type getType(Context context) {
    return params[1].getType(context);
  }

  @Override
  public CasObject toValue(Context context) {
    CasObject value = params[params[0].toBoolean(context) ? 1 : 2];
    switch(value.getType(context)) {
      case STRING: 
        return new CasString(value.toStr(context));
      case INTEGER:
        return new CasInteger(value.toInteger(context));
      case BOOLEAN:
        return new CasBoolean(value.toBoolean(context));
    }
    return value;
  }

  @Override
  public int toInteger(Context context) {
    return params[params[0].toBoolean(context) ? 1 : 2].toInteger(context);
  }

  @Override
  public String toStr(Context context) {
    return params[params[0].toBoolean(context) ? 1 : 2].toStr(context);
  }
  
  

  @Override
  public String toString() {
    return addBrackets(params[0].toString() + " ? " + params[1].toString()
        + " : " + params[2].toString());
  }
}
