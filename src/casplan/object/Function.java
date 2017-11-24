package casplan.object;

import casplan.Base;

public class Function extends CasObject {
  public static final CasObject[] emptyParams = new CasObject[0];
  public CasObject[] params;
  public int line, column;
  public boolean inBrackets = false;

  public int getPriority() {
    parserError("Priority of " + getClass().getSimpleName() + " is not set.");
    return 0;
  }
  
  public Function execute(Context context) {
    return execute(context, emptyParams);
  }  

  public Function execute(Context context, CasObject[] params) {
    return null;
  }

  public static Function executeCode(Context context, Function[] code) {
    for(Function functionCall : code) {
      Function marker = functionCall.execute(context);
      if(marker != null) return marker;
    }
    return null;
  }
  
  public CasObject executeUserFunction(Context context, UserFunction func
      , CasObject[] params) {
    if(func.code == null) error("Function is undefined");
    Context funcContext = new Context(context, context.parent
        , func.vars.length);
    funcContext.parent = context.functionObject;
    funcContext.functionCall = this;
    funcContext.prevContext = context;

    if(params == null) stop(context);
    for(int index = 0; index < func.defaultValues.length; index++) {
      if(index >= params.length) {
        funcContext.params[index] = func.defaultValues[index]
            .toValue(context);
      } else {
        funcContext.params[index] = params[index].toValue(context);
      }
    }

    executeCode(funcContext, func.code);
    context.functionObject = null;
    return funcContext.returnedValue;
  }
  
  
  
  @Override
  public Function toFunction() {
    return this;
  }
  
  public boolean returnsValue() {
    return true;
  }
  
  

  @Override
  public String toString() {
    if(params != null) {
      return getParams();
    } else {
      String address = functionAddresses.get(getClass());
      return address == null ? "" : "external." + address;
    }
  }
  
  public String getParams() {
    String str = "";
    if(params != null) {
      for(CasObject object : params) {
        if(!str.isEmpty()) str += ", ";
        str += object.toString();
      }
    }
    return "(" + str + ")";
  }
  
  public String addBrackets(String str) {
    return inBrackets ? "(" + str + ")" : str;
  }
  
  public String getCode() {
    return "";
  }
  
  @Override
  public void error(String message) {
    Base.runtimeError(message + " in line " + line + " column " + column);
  }
}
