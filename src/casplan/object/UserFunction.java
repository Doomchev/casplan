package casplan.object;

public class UserFunction extends Function {
  public String name = "";
  public Function[] code = null;
  public Parameter[] vars;
  public CasObject[] defaultValues;
  public boolean[] thisValues;
  
  @Override
  public Function execute(Context context, CasObject[] params) {
    stop(context);
    return null;
  }

  @Override
  public CasObject toObject(Context context) {
    return this;
  }
  
  @Override
  public CasObject toValue(Context context) {
    stop(context);
    return null;
  }
  
  @Override
  public int toInteger(Context context) {
    stop(context);
    return 0;
  }
  
  @Override
  public String toStr(Context context) {
    stop(context);
    return "";
  }
  
  @Override
  public UserFunction toUserFunction() {
    return this;
  }


  
  @Override
  public boolean isUserFunction() {
    return true;
  }
  
  
  
  @Override
  public boolean setBreakpointInside() {
    if(code.length == 0) return false;
    code[0].setBreakpoint(BPType.STEP_INTO);
    return true;
  }
  
  @Override
  public void setChildBreakpoint(Context context, Function func, BPType type) {
    setCodeBreakpoint(context, code, func, type, false);
  }

  

  @Override
  public String toString() {
    return "function" + name + (output == Output.JS ? name + " {\n"
        + getCode() + tabString + "}" : "");
  }
  
  @Override
  public String getCode() {
    String str = "";
    for(int index = 0; index < vars.length; index++) {
      CasObject value = index < defaultValues.length ? defaultValues[index]
          : Null.instance;
      if(value != Null.instance) str += tabString + "\tif(" + vars[index].name
          + " === undefined) " + vars[index].name + " = " + value + ";\n";
    }
    return str + codeToString(code);
  }
  
  @Override
  public String getParams() {
    String str = "";
    for(Parameter var : vars) {
      if(!str.isEmpty()) str += ", ";
      str += var.name;
    }
    return "(" + str + ")";
  }
  
  @Override
  public String getCaption() {
    return name;
  }
}
