package casplan.function.object;

import casplan.function.ObjectFunction;
import casplan.object.CasObject;
import casplan.object.Context;
import casplan.object.Function;

public class ObjectField extends ObjectFunction {
  @Override
  public int getPriority() {
    return 17;
  }

  @Override
  public Function execute(Context context, CasObject[] params) {
    stop(context);
    /*CasObject oldParent = currentParent;
    currentParent = params[0].toUserObject(context);
    Function marker = toFunction().execute(params);
    currentParent = oldParent;
    return marker;*/
    return null;
  }
  
  @Override
  public CasObject toObject(Context context) {
    return toValue(context);
  }
  
  @Override
  public CasObject toValue(Context context) {
    return params[0].toValue(context).getField(params[1].toField(), this);
  }
  
  @Override
  public boolean toBoolean(Context context) {
    return toValue(context).toBoolean(context);
  }
  
  @Override
  public String toStr(Context context) {
    return toValue(context).toStr(context);
  }
  
  @Override
  public CasObject toVariable() {
    return this;
  }
  
  
  
  @Override
  public Function setFunctionObject(Context context) {
    CasObject object = params[0].toValue(context);
    context.functionObject = object;
    return object.getField(params[1].toField(), this).toFunction();
  }
  
  @Override
  public void setValue(Context context, CasObject value, Function caller) {
    params[0].toValue(context).setField(params[1].toField(), value, caller);
  }
  
  
  
  @Override
  public String toString() {
    return params[0].toString() + "." + params[1].toString();
  }
}
