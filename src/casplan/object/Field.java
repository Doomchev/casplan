package casplan.object;

import java.util.HashMap;

public class Field extends CasObject {
  static HashMap<String, Field> all = new HashMap<>();
  
  public String name;

  private Field(String name) {
    this.name = name;
  }
  
  public static Field get(String name) {
    Field field = all.get(name);
    if(field == null) {
      field = new Field(name);
      all.put(name, field);
    }
    return field;
  }
  
  
  
  @Override
  public Field toField() {
    return this;
  }
  
  
  
  @Override
  public String toString() {
    return name;
  }
}
