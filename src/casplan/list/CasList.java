package casplan.list;

import casplan.object.CasObject;
import casplan.object.Context;
import casplan.object.Field;
import casplan.object.Function;
import casplan.object.Function.BPType;
import casplan.structure.*;
import casplan.value.CasInteger;
import external.editor.TreeView;
import javax.swing.tree.DefaultMutableTreeNode;

public class CasList extends CasObject {
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
  public CasObject getItemAtIndex(Context context, CasObject index
      , Function caller) {
    int intIndex = index.toInteger(context);
    if(intIndex >= 0 && intIndex < items.length) return items[intIndex];
    caller.error("Index (" + intIndex + ") is out of range");
    return null;
  }
  
  @Override
  public void setItemAtIndex(Context context, CasObject index
      , CasObject toValue, Function caller) {
    int intIndex = index.toInteger(context);
    if(intIndex < 0 || intIndex >= items.length) {
      caller.error("Invalid index \"" + intIndex + "\"");
    }
    items[intIndex] = toValue;
  }

  public static final Field lengthField = Field.get("length");
  public static final Field addLastField = Field.get("addLast");
  @Override
  public CasObject getField(Field field, Function caller) {
    if(field == classField) return CreateList.instance;
    if(field == lengthField) return new CasInteger(items.length);
    if(field == addLastField) return new AddLast();
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
  public void initLink() {
    addLink();
  }

  @Override
  public void initContentLinks() {
    for(CasObject object : items) object.initLink();
  }

  @Override
  public void fillNode(DefaultMutableTreeNode parentNode) {
    for(int index = 0; index < items.length; index++) {
      CasObject value = items[index];
      DefaultMutableTreeNode node = new TreeView.Node(value.getName()
          , this, index, value);
      value.fillNode(node);
      parentNode.add(node);
    }
  }

  @Override
  public String getName() {
    return "List(" + items.length + ")";
  }
  
  

  @Override
  public String toString() {
    if(output == Output.DEBUG) return "List(" + items.length + ")";
    String str = "";
    for(CasObject value : items) {
      if(!str.isEmpty()) str += ", ";
      str += value.wrapLink();
    }
    return "[" + str + "]";
  }
}