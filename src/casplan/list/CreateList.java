package casplan.list;

import casplan.function.ListFunction;
import casplan.object.CasObject;
import casplan.object.Context;
import casplan.object.Null;

public class CreateList extends ListFunction {
  public static CreateList instance = new CreateList(null);
  
  public CasObject[] values;

  public CreateList(CasObject[] values) {
    this.values = values;
  }

  @Override
  public CasObject toValue(Context context) {
    if(values == null) {
      if(params == null) return this;
      int quantity = params[0].toInteger(context);
      CasObject[] items = new CasObject[quantity];
      if(params.length > 1) {
        CasObject value = params[1].toValue(context);
        for(int index = 0; index < quantity; index++) items[index] = value;
      } else {
        for(int index = 0; index < quantity; index++) {
          items[index] = Null.instance;
        }
      }
      return new CasList(items);
    } else {
      CasObject[] items = new CasObject[values.length];
      for(int index = 0; index < values.length; index++) {
        items[index] = values[index].toValue(context);
      }
      return new CasList(items);
    }
  }
  
  

  @Override
  public String toString() {
    if(values != null) {
      String str = "";
      for(CasObject value : values) {
        if(!str.isEmpty()) str += ", ";
        str += value.toString();
      }
      return "[" + str + "]";
    }
    return "createList_";
  }
}
