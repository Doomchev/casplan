package casplan.value;

import casplan.object.CasObject;
import casplan.object.Context;

public class CasString extends CasObject {
  String value;

  public CasString(String value) {
    this.value = value;
  }
  
  @Override
  public Type getType(Context context) {
    return Type.STRING;
  }
  
  @Override
  public CasObject toValue(Context context) {
    return this;
  }
  
  @Override
  public int toInteger(Context context) {
    return Integer.parseInt(value);
  }
  
  @Override
  public String toStr(Context context) {
    return value;
  }
  
  
  
  @Override
  public String toString() {
    return "\"" + value.replace("\n", "\\n") + "\"";
  }
}
