package casplan.object;

public class Null extends UserObject {
  public static final Null instance = new Null();

  public Null() {}
  
  @Override
  public CasObject toValue(Context context) {
    return this;
  }
  
  @Override
  public boolean toBoolean(Context context) {
    return false;
  }
  
  
  
  @Override
  public String toString() {
    return "null";
  }
}
