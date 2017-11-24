package casplan.object;

import casplan.function.object.CreateObject;
import java.util.HashMap;

public class UserObject extends CasObject {
  public HashMap<Field, CasObject> values = new HashMap<>();
  public UserFunction constructor;
  
  @Override
  public boolean toBoolean(Context context) {
    return this != Null.instance;
  }

  @Override
  public Function toFunction() {
    return new CreateObject(null);
  }
  
  @Override
  public UserObject toUserObject() {
    return this;
  }
  
  
  
  @Override
  public boolean isUserObject() {
    return true;
  }
  

  
  @Override
  public void copyTo(Context context, UserObject object) {
    object.constructor = constructor;
    for(HashMap.Entry<Field, CasObject> entry : values.entrySet()) {
      object.values.put(entry.getKey(), entry.getValue().toObject(context));
    }
  }
  
  @Override
  public Function setFunctionObject(Context context) {
    context.functionObject = this;
    return toFunction();
  }

  @Override
  public CasObject getField(Field field, Function caller) {
    if(values.containsKey(field)) return values.get(field);
    caller.error("Field \"" + field.name + "\" not found");
    return null;
  }
  
  @Override
  public void setField(Field field, CasObject value, Function caller) {
    values.put(field, value);
  }
  
  

  @Override
  public String toString() {
    return "Object";
  }
}
