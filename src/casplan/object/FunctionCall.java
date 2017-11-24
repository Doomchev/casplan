package casplan.object;

import casplan.function.object.CreateObject;

public class FunctionCall extends Function {
  @Override
  public int getPriority() {
    return 17;
  }
  
  private Function getFunc(Context context) {
    Function func = params[0].setFunctionObject(context);
    func.params = params[1].toFunction().params;
    CreateObject newCreator = func.toCreator();
    CreateObject oldCreator = params[1].toCreator();
    if(newCreator != null && oldCreator != null) {
      newCreator.entries = oldCreator.entries;
    }
    return func;
  }
  
  @Override
  public Function execute(Context context) {
    Function func = params[0].setFunctionObject(context);
    if(func.isUserFunction()) {
      executeUserFunction(context, func.toUserFunction()
          , params[1].toFunction().params);
      return null;
    } else {
      return func.execute(context, params[1].toFunction().params);
    }
  }

  @Override
  public CasObject toObject(Context context) {
    return toValue(context);
  }

  @Override
  public CasObject toValue(Context context) {
    Function func = getFunc(context);
    if(func.isUserFunction()) {
      return executeUserFunction(context, func.toUserFunction()
          , params[1].toFunction().params);
    } else {
      return func.toValue(context);
    }
  }
  
  @Override
  public int toInteger(Context context) {
    Function func = getFunc(context);
    if(func.isUserFunction()) {
      return executeUserFunction(context, func.toUserFunction()
          , params[1].toFunction().params).toInteger(context);
    } else {
      return func.toInteger(context);
    }
  }
  
  @Override
  public String toStr(Context context) {
    Function func = getFunc(context);
    if(func.isUserFunction()) {
      execute(context, params);
      return executeUserFunction(context, func.toUserFunction()
          , params[1].toFunction().params).toStr(context);
    } else {
      return func.toStr(context);
    }
  }
  
  

  @Override
  public String toString() {
    if(params == null) return this.getClass().getSimpleName();
    CreateObject creator = params[1].toCreator();
    if(creator == null) {
      String str = "";
      if(params.length > 1) {
        CasObject[] funcParams = params[1].toFunction().params;
        for(CasObject object : funcParams) {
          if(!str.isEmpty()) str += ", ";
          str += object.toString();
        }
      }
      return params[0].toString() + "(" + str + ")";
    } else {
      return "createObject_(" + params[0].toString() + ", "
          + creator.getAppliedObject() + ")";
    }
  }
}
