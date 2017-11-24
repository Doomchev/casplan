package casplan.value;

import casplan.object.CasObject;
import casplan.object.Context;

public class CasBoolean extends CasObject {
  boolean value;

  public CasBoolean(boolean value) {
    this.value = value;
  }

  @Override
  public CasObject.Type getType(Context context) {
    return CasObject.Type.BOOLEAN;
  }
  
  @Override
  public CasObject toValue(Context context) {
    return this;
  }
  
  @Override
  public boolean toBoolean(Context context) {
    return value;
  }
  
  @Override
  public int toInteger(Context context) {
    return value ? 1 : 0;
  }
  
  @Override
  public String toStr(Context context) {
    return value ? "true" : "false";
  }
  
  

  @Override
  public String toString() {
    return toStr(null);
  }
}
