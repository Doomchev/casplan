package casplan.object;

import java.util.HashMap;
import java.util.HashSet;

public class Field extends CasObject {
  public static final HashMap<String, Field> all = new HashMap<>();
  public static final HashSet<Field> hidden = new HashSet<>();
  
  public String name;

  static {
    String[] fieldNames = ("x,y,halfWidth,halfHeight,texture,textures"
        + ",caption,sprites,width,height").split(",");
    for(String fieldName : fieldNames) hidden.add(get(fieldName));
  }
  
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
