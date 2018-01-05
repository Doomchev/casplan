package casplan;

import casplan.target.Target;
import casplan.template.UserFunctionTemplate;
import casplan.template.FunctionTemplate;
import java.util.HashMap;
import javax.swing.JOptionPane;
import java.util.LinkedList;
import javax.swing.UIManager;

public class Base {
  public static final HashMap<Class, String> functionAddresses = new HashMap<>();
  public static final HashMap<String, UserFunctionTemplate> userFunctions
      = new HashMap<>();
  public static String workingDirectory;
  public static LinkedList<Source> sources = new LinkedList<>();
  public static HashMap<String, LinkedList<String>> aliases = new HashMap<>();

  public static class Source {
    public StringBuffer text;
    public String fileName;

    public Source(String fileName, StringBuffer text) {
      this.text = text;
      this.fileName = fileName;
    }
  }
  
  static {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception ex) {
    }
  }
  
  public static Target target;
  
  public <K, V> K keyForValue(HashMap<K, V> map, V value) {
    for(HashMap.Entry<K, V> entry : map.entrySet()) {
      if(entry.getValue() == value) return entry.getKey();
    }
    return null;
  }
  
  public void stop() {
    int a = 0;
  }
  
  public static void runtimeError(String message) {
    JOptionPane.showMessageDialog(null, message, "Runtime error"
        , JOptionPane.ERROR_MESSAGE);
    throw new RuntimeException();
  }
  
  public static void parserError(String message) {
    JOptionPane.showMessageDialog(null, message, "Parsing error"
        , JOptionPane.ERROR_MESSAGE);
    throw new RuntimeException();
  }
}