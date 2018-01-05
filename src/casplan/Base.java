package casplan;

import casplan.object.*;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.HashSet;
import javax.swing.JOptionPane;
import java.util.LinkedList;
import javax.swing.UIManager;

public class Base {
  public static final HashMap<Class, String> functionAddresses = new HashMap<>();
  public static final HashMap<String, UserFunction> userFunctions = new HashMap<>();
  public static final HashMap<String, Variable> globalVariables = new HashMap<>();
  public static HashMap<String, Parameter> currentParameters = new HashMap<>();
  public static LinkedList<Parameter> currentParametersList = new LinkedList<>();
  public static HashMap<String, LinkedList<String>> aliases = new HashMap<>();
  
  public static HashMap<UserObject, String> classToName = new HashMap<>();
  public static HashMap<String, UserObject> nameToClass = new HashMap<>();
  
  public static HashMap<CasObject, String> objectToLink = new HashMap<>();
  public static HashMap<String, CasObject> linkToObject = new HashMap<>();
  public static HashSet<CasObject> usedObjects = new HashSet<>();
  public static int linkIndex;
      
  public static Graphics graphics;
  public static String workingDirectory;
  public static LinkedList<Source> sources = new LinkedList<>();
  
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
  
  public static Output output = Output.DEBUG;
  public static String tabString = "";
  public enum Output {
    DEBUG,
    JS,
    CASPLAN,
  }
  
  public static String codeToString(Function[] code) {
    String str = "";
    tabString += "\t";
    for(Function call : code) str += tabString + call.toString() + ";\n";
    tabString = tabString.substring(1);
    return str;
  }
  
  public static Variable createGlobalVariable(String id) {
    Variable var = new Variable(id);
    globalVariables.put(id, var);
    return var;
  }
  
  private static void addGlobalVariable(String id, CasObject function) {
    Variable var = createGlobalVariable(id);
    var.value = function;
  }
  
  static Parameter addParameter(String name) {
    Parameter param = currentParameters.get(name);
    if(param != null) return param;
    
    param = new Parameter(name);
    param.index = currentParametersList.size();
    currentParameters.put(name, param);
    currentParametersList.add(param);
    return param;
  }
  
  public static CasObject getVariable(String name) {
    CasObject var = currentParameters.get(name);
    if(var != null) return var;
    
    if(currentParameters != null) {
      var = currentParameters.get(name);
      if(var != null) return var;
    }
    
    var = globalVariables.get(name);
    if(var != null) return var;
    
    return createGlobalVariable(name);
  }
  
  public <K, V> K keyForValue(HashMap<K, V> map, V value) {
    for(HashMap.Entry<K, V> entry : map.entrySet()) {
      if(entry.getValue() == value) return entry.getKey();
    }
    return null;
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