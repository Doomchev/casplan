package external.function.integer;

import casplan.function.IntegerFunction;
import javax.swing.JOptionPane;
import casplan.object.CasObject;
import casplan.object.Context;
import casplan.object.Field;
import casplan.object.UserObject;

public class SelectOption extends IntegerFunction {
  private static final Field captionField = Field.get("caption");
  
  private static class Item {
    String caption;
    int index;

    public Item(String caption, int index) {
      this.caption = caption;
      this.index = index;
    }

    @Override
    public String toString() {
      return caption;
    }
  }
  
  @Override
  public int toInteger(Context context) {
    CasObject[] items = params[2].toValue(context).toList().items;
    Item[] options = new Item[items.length];
    for(int index = 0; index < items.length; index++) {
      UserObject object = items[index].toValue(context).toUserObject();
      String text;
      if(object != null) {
        CasObject fieldObject = object.getField(captionField, this);
        if(fieldObject == null) error("Field not found");
        text = fieldObject.toStr(context);
      } else {
        text = items[index].toStr(context);
      }
      options[index] = new Item(text, index);
    }

    if(params[3].toInteger(context) == 1) {
        Item item = (Item) JOptionPane.showInputDialog(null
            , params[1].toStr(context)
            , params[0].toStr(context), JOptionPane.QUESTION_MESSAGE
            , null, options, options[0]);
        return item == null ? -1 : item.index;
    } else {
      return JOptionPane.showOptionDialog(null, params[1].toStr(context)
          , params[0].toStr(context), JOptionPane.DEFAULT_OPTION
          , JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
    }
  }
}
