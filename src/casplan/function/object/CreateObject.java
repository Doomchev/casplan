package casplan.function.object;

import java.util.LinkedList;
import casplan.object.CasObject;
import casplan.object.Context;
import casplan.object.Field;
import casplan.object.Function;
import casplan.object.UserFunction;
import casplan.object.UserObject;

public class CreateObject extends Function {
  public UserFunction constructor;
  public String className;
  public LinkedList<Entry> entries = new LinkedList<>();

  public CreateObject(CasObject[] params, String className) {
    this.className = className;
    this.params = params;
  }
  
  public static class Entry {
    public Entry(Field field, CasObject value) {
      this.field = field;
      this.value = value;
    }
    
    Field field;
    CasObject value;

    @Override
    public String toString() {
      return field.toString() + " = " + value.toString();
    }
  }
  
  @Override
  public CasObject toValue(Context context) {
    UserObject object = new UserObject();
    object.constructor = constructor;
    
    CasObject functionObject = context.functionObject;
    if(functionObject != null) {
      functionObject.copyTo(context, object);
      object.objClass = functionObject.toUserObject();
      
      if(object.constructor != null) {
        context.functionObject = object;
        executeUserFunction(context, object.constructor, params);
      }
    }
    
    if(className != null) {
      classToName.put(object, className);
      nameToClass.put(className, object);
    }
    
    for(Entry entry : entries) object.values.put(entry.field
        , entry.value.toObject(context));
    
    return object;
  }
  
  @Override
  public CreateObject toCreator() {
    return this;
  }
  
  public String getAppliedObject() {
    String str = "{\n";
    tabString += "\t";
    if(constructor != null) {
      str += tabString + "create_: function" + constructor.getParams()
          + " {\n" + constructor.getCode() + tabString + "},\n";
    }
    for(Entry entry : entries) {
      str += tabString + entry.field.name + ": " + entry.value.toString() + ",\n";
    }
    tabString = tabString.substring(1);
    return str + tabString + "}";
  }

  @Override
  public String toString() {
    return "createObject_(null, " + getAppliedObject() + ")";
  }
}
