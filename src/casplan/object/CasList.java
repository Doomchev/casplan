package casplan.object;

import casplan.object.Function.BPType;
import casplan.structure.*;
import casplan.value.CasInteger;

public class CasList extends CasObject {
  public static final Field lengthField = Field.get("length");
  
  public CasObject[] items;

  public CasList(CasObject[] items) {
    this.items = items;
  }
  
  @Override
  public CasList toList() {
    return this;
  }


  
  @Override
  public boolean isList() {
    return true;
  }
  

  
  @Override
  public CasObject getItemAtIndex(int index, Function caller) {
    if(index >= 0 && index < items.length) return items[index];
    caller.error("Index (" + index + ") is out of range");
    return null;
  }
  
  @Override
  public void setItemAtIndex(int index, CasObject toValue, Function caller) {
    if(index < 0 || index >= items.length) {
      caller.error("Invalid index \"" + index + "\"");
    }
    items[index] = toValue;
  }

  @Override
  public CasObject getField(Field field, Function caller) {
    if(field == lengthField) return new CasInteger(items.length);
    caller.error("There's no field \"" + field.name + "\" in list");
    return null;
  }  
  
  @Override
  public Function iterate(Context context, ForIn loop) {
    for(int index = 0; index < items.length; index++) {
      if(loop.index != null) {
        loop.index.setValue(context, new CasInteger(index), loop);
      }
      if(loop.value != null) loop.value.setValue(context, items[index], loop);
      for(Function call : loop.code) {
        if(call.breakpoint != BPType.NONE) call.stop(context);
        Function marker = call.execute(context);
        if(marker == Return.instance) return Return.instance;
        if(marker == Continue.instance) break;
        if(marker == Break.instance) return null;
      }
    }
    return null;
  }
  
  

  @Override
  public String toString() {
    if(output == Output.DEBUG) return "List(" + items.length + ")";
    String str = "";
    for(CasObject value : items) {
      if(!str.isEmpty()) str += ", ";
      str += value.toString();
    }
    return "[" + str + "]";
  }
}