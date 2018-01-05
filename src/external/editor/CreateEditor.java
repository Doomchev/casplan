package external.editor;

import casplan.function.ObjectFunction;
import casplan.object.CasObject;
import casplan.object.Context;

public class CreateEditor extends ObjectFunction {
  @Override
  public CasObject toValue(Context context) {
    return new Editor(EditorFrame.execute());
  }
}
