package casplan.function.object;

import casplan.function.ObjectFunction;
import casplan.object.CasObject;
import casplan.object.Context;
import casplan.object.Function;

public class ItemAtIndex extends ObjectFunction {
  @Override
  public int getPriority() {
    return 17;
  }
  
  @Override
  public CasObject toObject(Context context) {
    return toValue(context);
  }
  
  @Override
  public CasObject toValue(Context context) {
    return params[0].toValue(context).getItemAtIndex(
        params[1].toInteger(context), this);
  } 
  
  @Override
  public Type getType(Context context) {
    return toValue(context).getType(context);
  }
  
  @Override
  public int toInteger(Context context) {
    return toValue(context).toInteger(context);
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
  public void setValue(Context context, CasObject value, Function caller) {
    params[0].toValue(context).setItemAtIndex(params[1].toInteger(context)
        , value, this);
  }
  
  @Override
  public void addNumber(Context context, int number, Function caller) {
    toValue(context).addNumber(context, number, caller);
  }
  
  
  
  @Override
  public String toString() {
    return params[0].toString() + "[" + params[1].toString() + "]";
  }
}
