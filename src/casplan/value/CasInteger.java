package casplan.value;

import casplan.object.CasObject;
import casplan.object.Context;
import casplan.object.Function;

public class CasInteger extends CasObject {
  public int value;

  public CasInteger(int value) {
    this.value = value;
  }

  @Override
  public CasObject.Type getType(Context context) {
    return CasObject.Type.INTEGER;
  }
  
  @Override
  public CasObject toValue(Context context) {
    return this;
  }
  
  @Override
  public int toInteger(Context context) {
    return value;
  }
  
  @Override
  public String toStr(Context context) {
    return String.valueOf(value);
  }
  
  

  @Override
  public void addNumber(Context context, int number, Function caller) {
    value += number;
  }
  
  

  @Override
  public String toString() {
    return toStr(null);
  }
}
