package casplan.template;

import java.util.LinkedList;

public class CreateObject extends FunctionTemplate {
  public UserFunctionTemplate constructor;
  public LinkedList<Entry> entries = new LinkedList<>();

  public CreateObject(ObjectTemplate[] params) {
    super(params.length, "createObject");
    this.params = params;
  }
  
  public static class Entry {
    public Entry(Field field, ObjectTemplate value) {
      this.field = field;
      this.value = value;
    }
    
    Field field;
    ObjectTemplate value;
  }
}
