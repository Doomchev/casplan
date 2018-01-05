package casplan.template;

import casplan.Base;

public class ObjectTemplate extends Base {
  public static final Field constructorField = Field.get("constructor_");
  
  public FunctionTemplate toFunctionTemplate() {
    return null;
  }
  
  public String export() {
    return "";
  }
}
