package external.function.object;

import external.object.Window;
import casplan.function.ObjectFunction;
import java.awt.Canvas;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import javax.swing.JFrame;
import casplan.object.CasObject;
import casplan.object.Context;
import casplan.value.CasInteger;
import java.awt.Frame;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class CreateWindow extends ObjectFunction {
  @Override
  public CasObject toValue(Context context) {
    Window window = new Window();
    Frame frame = new Frame();
    frame.addWindowListener(new WindowAdapter() {  
      @Override
      public void windowClosing(WindowEvent e) {  
        frame.dispose();  
      }  
    });  
    
    Toolkit tk = Toolkit.getDefaultToolkit();  
    int xSize = ((int) tk.getScreenSize().getWidth());  
    int ySize = ((int) tk.getScreenSize().getHeight());  
    frame.setSize(xSize, ySize);
    
    Insets insets = frame.getInsets();
    
    Canvas canvas = new Canvas();
    
    canvas.addMouseListener(new MouseListener() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if(window.onClick != null) {
          CasObject[] funcParams = new CasObject[2];
          funcParams[0] = new CasInteger(e.getX());
          funcParams[1] = new CasInteger(e.getY());
          window.onClick.execute(new Context(null, window, 0), funcParams);
        }
      }

      @Override
      public void mousePressed(MouseEvent e) {
      }

      @Override
      public void mouseReleased(MouseEvent e) {
      }

      @Override
      public void mouseEntered(MouseEvent e) {
      }

      @Override
      public void mouseExited(MouseEvent e) {
      }
    });
    
    frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
    frame.setVisible(true);
    
    canvas.setSize(frame.getWidth() - insets.left + insets.right
        , frame.getHeight() - insets.top + insets.bottom);
    frame.add(canvas);
    canvas.createBufferStrategy(2);
    window.frame = frame;
    window.canvas = canvas;
    
    new Thread() {
      {
        setDaemon(true);
      }

      @Override
      public void run() {
        long start = System.nanoTime();
        while (true) {
          long now = System.nanoTime();
          float elapsed = (now - start) / 1000000000f;
          start = now;
          if (1000000000 / 60f - (System.nanoTime() - start) > 1000000) {
            BufferStrategy bf = canvas.getBufferStrategy();
            Graphics2D g = null;
            try {
              g = (Graphics2D) bf.getDrawGraphics();
              Window.graphics = g;
              g.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
              g.drawRect(1, 1, canvas.getWidth() - 3, canvas.getHeight() - 3);
              if(window.render != null) {
                window.render.execute(new Context(null, window, 0));
              }
            } finally {
              g.dispose();
            }
            bf.show();
            Toolkit.getDefaultToolkit().sync();      
            try {
              Thread.sleep(0, 999999);
            } catch (InterruptedException ex) {
            }
          }
        }
      }
    }.start();
    return window;
  }
}
