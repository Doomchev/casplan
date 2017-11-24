package casplan.object;

public class Parameter extends CasObject {
  public String name;
  public int index;

  public Parameter(String name) {
    this.name = name;
  }
  
  @Override
  public CasObject toValue(Context context) {
    return context.params[index];
  }
  
  @Override
  public CasObject.Type getType(Context context) {
    return context.params[index].getType(context);
  }
  
  @Override
  public boolean toBoolean(Context context) {
    return context.params[index].toBoolean(context);
  }
  
  @Override
  public int toInteger(Context context) {
    return context.params[index].toInteger(context);
  }
  
  @Override
  public String toStr(Context context) {
    return context.params[index].toStr(context);
  }
  
  @Override
  public CasObject toVariable() {
    return this;
  }
  
  
  
  @Override
  public Function setFunctionObject(Context context) {
    return context.params[index].setFunctionObject(context);
  }
  
  @Override
  public void setValue(Context context, CasObject value, Function caller) {
    context.params[index] = value;
  }
  
  @Override
  public void addNumber(Context context, int number, Function caller) {
    context.params[index].addNumber(context, number, caller);
  }
  
  
  
  @Override
  public String toString() {
    return name;
  }
}
