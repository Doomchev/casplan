package casplan.function.operator;

import casplan.object.CasObject;
import casplan.value.CasInteger;
import casplan.value.CasString;
import casplan.object.CasObject.Type;
import casplan.object.Context;
import casplan.object.Function;

public class Addition extends Function {
  @Override
  public int getPriority() {
    return 13;
  }
  
  @Override
  public CasObject toValue(Context context) {
    CasObject value0 = params[0].toValue(context);
    CasObject value1 = params[1].toValue(context);
    Type type0 = value0.getType(context);
    Type type1 = value1.getType(context);
    if(type0 == Type.STRING || type1 == Type.STRING) {
      return new CasString(value0.toStr(context) + value1.toStr(context));
    }
    return new CasInteger(value0.toInteger(context) + value1.toInteger(context));
  }

  @Override
  public int toInteger(Context context) {
    CasObject value0 = params[0].toValue(context);
    CasObject value1 = params[1].toValue(context);
    Type type0 = value0.getType(context);
    Type type1 = value1.getType(context);
    if(type0 == Type.STRING || type1 == Type.STRING) {
      return Integer.parseInt(value0.toStr(context) + value1.toStr(context));
    }
    return value0.toInteger(context) + value1.toInteger(context);
  }

  @Override
  public String toStr(Context context) {
    CasObject value0 = params[0].toValue(context);
    CasObject value1 = params[1].toValue(context);
    Type type0 = value0.getType(context);
    Type type1 = value1.getType(context);
    if(type0 == Type.STRING || type1 == Type.STRING) {
      return value0.toStr(context) + value1.toStr(context);
    }
    return String.valueOf(value0.toInteger(context) + value1.toInteger(context));
  }
  
  

  @Override
  public String toString() {
    return addBrackets(params[0].toString() + " + " + params[1].toString());
  }
}
