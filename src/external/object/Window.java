package external.object;

import java.awt.Frame;
import java.awt.Graphics;
import casplan.object.CasObject;
import casplan.object.Field;
import casplan.object.Function;
import casplan.object.UserFunction;
import casplan.value.CasInteger;
import java.awt.Canvas;

public class Window extends CasObject {
  final static Field widthField = Field.get("width");
  final static Field heightField = Field.get("height");
  final static Field renderField = Field.get("render");
  final static Field onClickField = Field.get("onClick");
  public static Graphics graphics;
      
  public Frame frame;
  public Canvas canvas;
  public UserFunction render = null, onClick = null;

  @Override
  public CasObject getField(Field field, Function caller) {
    if(field == widthField) {
      return new CasInteger(frame.getWidth());
    } else if(field == heightField) {
      return new CasInteger(frame.getHeight());
    }
    caller.error("Window object has no readable field \"" + field.name + "\"");
    return null;
  }

  @Override
  public void setField(Field field, CasObject toValue, Function caller) {
    if(field == renderField) {
      render = toValue.toUserFunction();
      render.name = "window.render" + render.name;
    } else if(field == onClickField) {
      onClick = toValue.toUserFunction();
      onClick.name = "window.onClick" + render.name;
    } else {
      caller.error("Window object has no writable field \"" + field.name + "\"");
    }
  }
  
  

  @Override
  public String toString() {
    return "Window(" + frame.getWidth() + " x " + frame.getHeight() + ")";
  }
}
