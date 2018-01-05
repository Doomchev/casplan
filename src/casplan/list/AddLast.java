package casplan.list;

import casplan.object.CasObject;
import casplan.object.Context;
import casplan.object.Function;

public class AddLast extends Function {
  @Override
  public Function execute(Context context, CasObject[] params) {
    CasList list = context.functionObject.toList();
    CasObject[] items = list.items;
    CasObject[] newItems = new CasObject[items.length + 1];
    System.arraycopy(items, 0, newItems, 0, items.length);
    newItems[items.length] = params[0].toValue(context);
    list.items = newItems;
    return null;
  }
}
