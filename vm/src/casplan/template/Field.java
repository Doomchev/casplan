package casplan.template;

import java.util.HashMap;

public class Field extends ObjectTemplate {
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
  public String export() {
    return name;
  }
}
