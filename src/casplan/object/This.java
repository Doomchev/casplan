package casplan.object;

public class This extends CasObject {
  public static final This instance = new This();

  private This() {}
  
  @Override
  public CasObject toObject(Context context) {
    return context.parent;
  }
  
  @Override
  public CasObject toValue(Context context) {
    return context.parent;
  }
  
  @Override
  public Type getType(Context context) {
    return Type.UNKNOWN;
  }
  
  @Override
  public int toInteger(Context context) {
    return context.parent.toInteger(context);
  }
  
  @Override
  public String toStr(Context context) {
    return context.parent.toStr(context);
  }
  
  
  
  @Override
  public void setValue(Context context, CasObject value, Function caller) {
    context.parent.setValue(context, value, caller);
  }
  
  @Override
  public void addNumber(Context context, int number, Function caller) {
    context.parent.addNumber(context, number, caller);
  }
  
  

  @Override
  public String toString() {
    return "this";
  }
}
