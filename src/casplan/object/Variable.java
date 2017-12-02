package casplan.object;

public class Variable extends CasObject {
  public CasObject value;
  public String name;

  public Variable(String name) {
    this.name = name;
  }
  
  @Override
  public CasObject toObject(Context context) {
    return value;
  }
  
  @Override
  public CasObject toValue(Context context) {
    return value;
  }
  
  @Override
  public Type getType(Context context) {
    return value.getType(context);
  }
  
  @Override
  public boolean toBoolean(Context context) {
    return value.toBoolean(context);
  }
  
  @Override
  public int toInteger(Context context) {
    return value.toInteger(context);
  }
  
  @Override
  public String toStr(Context context) {
    return value.toStr(context);
  }
  
  @Override
  public CasObject toVariable() {
    return this;
  }
  
  
  
  @Override
  public Function setFunctionObject(Context context) {
    return value.setFunctionObject(context);
  }
  
  @Override
  public void setValue(Context context, CasObject value, Function caller) {
    this.value = value;
  }
  
  @Override
  public void addNumber(Context context, int number, Function caller) {
    value.addNumber(context, number, caller);
  }
  
  

  @Override
  public String toString() {
    return name;
  }
  
  @Override
  public String getCaption() {
    return name;
  }
}
