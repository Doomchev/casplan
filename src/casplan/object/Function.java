package casplan.object;

import casplan.Base;
import casplan.Debugger;
import javax.swing.JOptionPane;

public class Function extends CasObject {
  public static final CasObject[] emptyParams = new CasObject[0];
  public CasObject[] params;
  public int line, column, startingTextIndex, textLength;
  public boolean inBrackets = false;
  public Function blockParent, parentFunction;
  public BPType breakpoint = BPType.NONE;

  public static enum BPType {
    NONE,
    STEP,
    STEP_INTO,
    STEP_OUT,
  }  

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

  public Function executeCode(Context context, Function[] code, Function func) {
    for(Function call : code) {
      if(call.breakpoint != BPType.NONE || breakpoint == BPType.STEP_INTO) {
        breakpoint = BPType.NONE;
        call.stop(context);
      }
      Function marker = call.execute(context);
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
    funcContext.function = func;
    funcContext.prevContext = context;

    if(params == null) error("Null params");
    for(int index = 0; index < func.defaultValues.length; index++) {
      if(index >= params.length) {
        funcContext.params[index] = func.defaultValues[index]
            .toValue(context);
      } else {
        funcContext.params[index] = params[index].toValue(context);
      }
    }

    executeCode(funcContext, func.code, func);
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
  
  
  
  public void stop(Context context) {
    Debugger.execute(context, this);
  }
  
  public void setBreakpoint(BPType type) {
    breakpoint = type;
  }
  
  @Override
  public boolean setBreakpointInside() {
    if(params == null) return false;
    for(CasObject param : params) {
      if(param.setBreakpointInside()) return true;
    }
    return false;
  }
  
  public void setNextBreakpoint(Context context, BPType type) {
    if(type == BPType.STEP_OUT) {
      context.functionCall.setNextBreakpoint(context, BPType.STEP);
      return;
    } else if(type == BPType.STEP_INTO) {
      if(setBreakpointInside()) return;
    }
    setBreakpointInsideParent(context, type);
  }
  
  public Function getParent(Context context) {
    if(blockParent != null) return blockParent;
    if(context == null) {
      warning("Failed to set breakpoint");
      return this;
    }
    return context.functionCall;
  }
  
  public void setBreakpointInsideParent(Context context, BPType type) {
    if(parentFunction != null) {
      parentFunction.setBreakpointInsideParent(context, type);
    } else {
      getParent(context).setChildBreakpoint(context, this, type);
    }
  }
  
  public void setChildBreakpoint(Context context, Function func, BPType type) {
    //if(blockParent == null && c)
    //error("Failed to set breakpoint");
  }
  
  public void setCodeBreakpoint(Context context, Function[] code
      , Function afterItem, BPType type, boolean isLoop) {
    for(int index = 0; index < code.length; index++) {
      if(code[index] == afterItem) {
        if(index == code.length - 1) {
          if(isLoop) {
            code[0].setBreakpoint(type);
          } else {
            getParent(context).setBreakpointInsideParent(context, type);
          }
        } else {
          code[index + 1].setBreakpoint(type);
        }
        return;
      }
    }
    //warning("Failed to set breakpoint");
  }
  
  public void removeBreakpoint() {
    breakpoint = BPType.NONE;
  }
  
  public boolean arrayContains(Function[] array, Function item) {
    for(Function func : array) {
      if(func == item) return true;
    }
    return false;
  }
  
  
  @Override
  public void setParent(Function func) {
    parentFunction = func;
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
  
  public void warning(String message) {
    JOptionPane.showMessageDialog(null, message + " in line " + line
        + " column " + column, "Runtime error", JOptionPane.ERROR_MESSAGE);
  }
  
  @Override
  public void error(String message) {
    Base.runtimeError(message + " in line " + line + " column " + column);
  }
  
  @Override
  public String getCaption() {
    return getParams();
  }
}
