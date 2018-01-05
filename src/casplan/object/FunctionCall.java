package casplan.object;

import casplan.function.object.CreateObject;

public class FunctionCall extends Function {
  @Override
  public int getPriority() {
    return 17;
  }
  
  private CasObject getFunc(Context context, boolean execute) {
    Function func = params[0].setFunctionObject(context);
    func.params = params[1].toFunction().params;
    CreateObject newCreator = func.toCreator();
    CreateObject oldCreator = params[1].toCreator();
    if(newCreator != null && oldCreator != null) {
      newCreator.entries = oldCreator.entries;
    }
    
    if(breakpoint == BPType.STEP_INTO) {
      if(!func.setBreakpointInside()) {
        setBreakpointInsideParent(context, BPType.STEP);      
      }
      breakpoint = BPType.NONE;
    }
    
    if(func.isUserFunction()) {
      return executeUserFunction(context, func.toUserFunction()
          , params[1].toFunction().params);
    } else {
      if(execute) func.execute(context, params[1].toFunction().params);
      return func;
    }
  }
  
  @Override
  public Function execute(Context context) {
    getFunc(context, true);
    return null;
  }

  @Override
  public CasObject toObject(Context context) {
    return getFunc(context, false).toValue(context);
  }

  @Override
  public CasObject toValue(Context context) {
    return getFunc(context, false).toValue(context);
  }
  
  @Override
  public boolean toBoolean(Context context) {
    return getFunc(context, false).toBoolean(context);
  }
  
  @Override
  public int toInteger(Context context) {
    return getFunc(context, false).toInteger(context);
  }
  
  @Override
  public String toStr(Context context) {
    return getFunc(context, false).toStr(context);
  }
  
  
  
  @Override
  public void setNextBreakpoint(Context context, BPType type) {
    if(type == BPType.STEP_INTO) {
      breakpoint = BPType.STEP_INTO;
    } else {
      super.setNextBreakpoint(context, type);
    }
  }
  
  @Override
  public boolean setBreakpointInside() {
    breakpoint = BPType.STEP_INTO;
    return true;
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
  
  @Override
  public String getCaption() {
    return params[0].getCaption();
  }
}
