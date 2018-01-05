package casplan.stringmap;

import java.util.LinkedList;
import casplan.object.CasObject;
import casplan.object.Context;
import casplan.object.Function;
import casplan.stringmap.StringMap;
import casplan.object.UserFunction;

public class CreateStringMap extends Function {
  public UserFunction constructor;
  public LinkedList<Entry> entries = new LinkedList<>();
  
  public static class Entry {
    public Entry(String key, CasObject value) {
      this.key = key;
      this.value = value;
    }
    
    String key;
    CasObject value;

    @Override
    public String toString() {
      return key + " = " + value.toString();
    }
  }
  
  @Override
  public CasObject toValue(Context context) {
    StringMap map = new StringMap();
    for(Entry entry : entries) map.entries.put(entry.key
        , entry.value.toObject(context));
    return map;
  }
}
