package external.editor;

import static casplan.Base.graphics;
import casplan.object.CasObject;
import casplan.object.Context;
import casplan.object.Field;
import casplan.object.Function;
import casplan.object.UserFunction;
import casplan.value.CasInteger;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferStrategy;

public class Editor extends CasObject {
  public EditorFrame frame;
  public UserFunction onClose, onMouseDown, onMouseUp, onMouseDrag;

  @SuppressWarnings("LeakingThisInConstructor")
  public Editor(EditorFrame frame) {
    this.frame = frame;
    frame.editor = this;
    frame.canvas.addMouseMotionListener(new MouseMotionListener() {
      @Override
      public void mouseDragged(MouseEvent e) {
        CasObject[] funcParams = new CasObject[2];
        funcParams[0] = new CasInteger(e.getX());
        funcParams[1] = new CasInteger(e.getY());
        execute(onMouseDrag, frame.editor, funcParams);
      }

      @Override
      public void mouseMoved(MouseEvent e) {
      }
    });
    
    frame.canvas.addMouseListener(new MouseListener() {
      @Override
      public void mouseClicked(MouseEvent e) {
      }

      @Override
      public void mousePressed(MouseEvent e) {
        CasObject[] funcParams = new CasObject[2];
        funcParams[0] = new CasInteger(e.getX());
        funcParams[1] = new CasInteger(e.getY());
        execute(onMouseDown, frame.editor, funcParams);
      }

      @Override
      public void mouseReleased(MouseEvent e) {
        CasObject[] funcParams = new CasObject[2];
        funcParams[0] = new CasInteger(e.getX());
        funcParams[1] = new CasInteger(e.getY());
        execute(onMouseUp, frame.editor, funcParams);
      }

      @Override
      public void mouseEntered(MouseEvent e) {
      }

      @Override
      public void mouseExited(MouseEvent e) {
      }
    });
    
    frame.canvas.createBufferStrategy(2);
  }
  
  
  public static class Canvas extends java.awt.Canvas {
    public UserFunction render;
    
    @Override
    public void paint(Graphics g) {
      BufferStrategy bf = getBufferStrategy();
      graphics = bf.getDrawGraphics();
      graphics.clearRect(0, 0, getWidth(), getHeight());
      execute(render, null);
      graphics.dispose();
      bf.show();
    }    
  }
  
      
  final static Field managerField = Field.get("manager");
  final static Field refreshField = Field.get("refresh");
  @Override
  public CasObject getField(Field field, Function caller) {
    if(field == refreshField) {
      return new RefreshEditor();
    } else if(field == managerField) {
      return new TreeView(frame.manager);
    }
    caller.error("Editor object has no readable field \"" + field.name + "\"");
    return null;
  }

  final static Field onCloseField = Field.get("onClose");
  final static Field onMouseDownField = Field.get("onMouseDown");
  final static Field onMouseUpField = Field.get("onMouseUp");
  final static Field onMouseDragField = Field.get("onMouseDrag");
  final static Field renderField = Field.get("render");
  @Override
  public void setField(Field field, CasObject toValue, Function caller) {
    if(field == onCloseField) {
      onClose = toValue.toUserFunction();
    } else if(field == onMouseDownField) {
      onMouseDown = toValue.toUserFunction();
    } else if(field == onMouseUpField) {
      onMouseUp = toValue.toUserFunction();
    } else if(field == onMouseDragField) {
      onMouseDrag = toValue.toUserFunction();
    } else if(field == renderField) {
      ((Canvas) frame.canvas).render = toValue.toUserFunction();
    } else {
      caller.error("Editor object has no writable field \"" + field.name + "\"");
    }
  }

  void onClose() {
    execute(onClose, this);
  }
  
  

  @Override
  public String toString() {
    return "Editor()";
  }
  
  
  
  public static class RefreshEditor extends Function {
    @Override
    public Function execute(Context context, CasObject[] params) {
      ((Editor) context.functionObject).frame.canvas.repaint();
      return null;
    }
  }
}
