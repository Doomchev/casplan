package casplan.object;

import casplan.Base;
import static casplan.Base.output;
import casplan.function.object.CreateObject;
import external.editor.TreeView;
import java.util.HashMap;
import javax.swing.tree.DefaultMutableTreeNode;
import static casplan.Base.classToName;

public class UserObject extends CasObject {
  public HashMap<Field, CasObject> values = new HashMap<>();
  public UserFunction constructor;
  public UserObject objClass = Null.instance;
  
  @Override
  public boolean toBoolean(Context context) {
    return this != Null.instance;
  }

  @Override
  public Function toFunction() {
    return new CreateObject(null, null);
  }
  
  @Override
  public UserObject toUserObject() {
    return this;
  }
  
  
  
  @Override
  public boolean isUserObject() {
    return true;
  }
  

  
  @Override
  public void copyTo(Context context, UserObject object) {
    object.constructor = constructor;
    for(HashMap.Entry<Field, CasObject> entry : values.entrySet()) {
      object.values.put(entry.getKey(), entry.getValue().toObject(context));
    }
  }
  
  @Override
  public Function setFunctionObject(Context context) {
    context.functionObject = this;
    return toFunction();
  }
  
  @Override
  public CasObject getItemAtIndex(Context context, CasObject index
      , Function caller) {
    String fieldId = index.toStr(context);
    Field field = Field.all.get(fieldId);
    if(field != null) {
      CasObject value = values.get(field);
      if(value != null) return value;
    }
    error("There's no field \"" + fieldId + "\" in user object");
    return null;
  }
  
  @Override
  public void setItemAtIndex(Context context, CasObject index
      , CasObject toValue, Function caller) {
    values.put(Field.get(index.toStr(context)), toValue);
  }
  
  @Override
  public CasObject getField(Field field, Function caller) {
    if(field == classField) return objClass;
    if(values.containsKey(field)) return values.get(field);
    return super.getField(field, caller);
  }
  
  @Override
  public void setField(Field field, CasObject value, Function caller) {
    values.put(field, value);
  }

  @Override
  public void initLink() {
    addLink();
  }
  
  @Override
  public void initContentLinks() {
    for(CasObject object : values.values()) object.initLink();
  }
  
  
  public String toString(boolean onlyContents) {
    String str = onlyContents ? "" : (objClass == Null.instance ? ""
        : classToName.get(objClass) + " ") + "{\n";
    if(!onlyContents) tabString += "\t";
    for(HashMap.Entry<Field, CasObject> entry: values.entrySet()) {
      if(entry.getValue().isUserFunction()) continue;
      str += tabString + entry.getKey().name + ": "
          + entry.getValue().wrapLink() + "\n";
    }
    if(!onlyContents) tabString = tabString.substring(1);
    return str + (onlyContents ? "" : tabString + "}");
  }

  @Override
  public String toString() {
    return output == Base.Output.CASPLAN ? toString(false) : getName();
  }

  @Override
  public String getName() {
    return (objClass == Null.instance ? "" : classToName.get(objClass) + " ")
        + values.get(captionField);
  }

  
  Field contentsField = Field.get("contents");
  @Override
  public void fillNode(DefaultMutableTreeNode parentNode) {
    String className = classToName.get(objClass);
    if(className != null && className.equals("Layer")) {
      getField(contentsField, null).fillNode(parentNode);
    } else {
      for(HashMap.Entry<Field, CasObject> entry : values.entrySet()) {
        Field field = entry.getKey();
        if(Field.hidden.contains(field)) continue;
        CasObject value = entry.getValue();
        DefaultMutableTreeNode node = new TreeView.Node(
            entry.getKey().name + ": " + value.getName(), this, field, value);
        value.fillNode(node);
        parentNode.add(node);
      }
    }
  }
}

