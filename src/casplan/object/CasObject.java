package casplan.object;

import casplan.Base;
import casplan.function.object.CreateObject;
import casplan.structure.ForIn;
import javax.swing.JOptionPane;

public class CasObject extends Base {
  public static final UserObject root = new UserObject();
  public static final Field constructorField = Field.get("constructor_");
  
  public CasObject toObject(Context context) {
    return toValue(context);
  }
  
  public CasObject toValue(Context context) {
    return this;
  }
  
  public boolean toBoolean(Context context) {
    runtimeError("Object cannot be converted to boolean");
    return false;
  }
  
  public int toInteger(Context context) {
    runtimeError("Object cannot be converted to integer");
    return 0;
  }
  
  public String toStr(Context context) {
    runtimeError("Object cannot be converted to string");
    return "";
  }
  
  public CasList toList() {
    runtimeError("Cannot convert object to list");
    return null;
  }
  
  public Function toFunction() {
    runtimeError("Cannot convert object to function");
    return null;
  }
  
  public UserFunction toUserFunction() {
    runtimeError("Cannot convert object to user object");
    return null;
  }
  
  public UserObject toUserObject() {
    runtimeError("Cannot convert object to user object");
    return null;
  }
  
  public CasObject toVariable() {
    runtimeError("Cannot convert object to variable");
    return null;
  }
  
  public Field toField() {
    runtimeError("Cannot convert object to field id");
    return null;
  }
  
  public Range toRange() {
    return null;
  }
  
  public CreateObject toCreator() {
    return null;
  }
  
  public Type getType(Context context) {
    return Type.UNKNOWN;
  }
  
  public enum Type {
    UNKNOWN,
    BOOLEAN,
    INTEGER,
    STRING,
    OBJECT,
    FUNCTION,
    FIELD,
    VOID
  }
  
  
  
  public boolean isList() {
    return false;
  }
  
  public boolean isUserFunction() {
    return false;
  }
  
  public boolean isUserObject() {
    return false;
  }
  
  
  
  public boolean setBreakpointInside() {
    return false;
  }
  
  
  
  public void setParent(Function func) {
  }
  
  public void applyConstructor(Context context, UserObject object
      , CasObject[] params) {
  }
    
  public void copyTo(Context context, UserObject object) {
  }
  
  public Function setFunctionObject(Context context) {
    return toFunction();
  }
  
  public void setValue(Context context, CasObject value, Function caller) {
    caller.error("Object is not variable");
  }

  public void addNumber(Context context, int number, Function caller) {
    caller.error("Object is not incrementable");
  }
  
  public CasObject getItemAtIndex(int index, Function caller) {
    caller.error("Object is not indexable");
    return null;
  }
  
  public void setItemAtIndex(int index, CasObject toValue, Function caller) {
    caller.error("Object is not indexable");
  }
  
  public CasObject getField(Field field, Function caller) {
    caller.error("Object field \"" + field.name + "\" not found");
    return null;
  }  
  
  public void setField(Field field, CasObject toValue, Function caller) {
    caller.error("Object has no fields");
  }

  
  public Function iterate(Context context, ForIn loop) {
    loop.error("Object is not iterable.");
    return null;
  }
  
  
  
  public void error(String message) {
    Base.runtimeError(message);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }
  
  public String getCaption() {
    return "";
  }
}
