package casplan;

import casplan.object.UserFunction;
import casplan.object.CasObject;
import casplan.object.Parameter;
import casplan.object.Variable;
import casplan.function.End;
import external.function.string.EnterString;
import external.function.Print;
import external.function.ShowMessage;
import external.function.integer.RandomInteger;
import casplan.object.Context;
import casplan.object.Function;
import external.function.integer.SelectOption;
import java.util.HashMap;
import javax.swing.JOptionPane;
import java.util.LinkedList;

public class Base {
  public static final HashMap<Class, String> functionAddresses = new HashMap<>();
  static final HashMap<String, UserFunction> userFunctions = new HashMap<>();
  static final HashMap<String, Variable> globalVariables = new HashMap<>();
  public static HashMap<String, Parameter> currentParameters = new HashMap<>();
  public static LinkedList<Parameter> currentParametersList = new LinkedList<>();
  
  static {
    addGlobalVariable("print", new Print());
    addGlobalVariable("enterString", new EnterString());
    addGlobalVariable("randomInteger", new RandomInteger());
    addGlobalVariable("showMessage", new ShowMessage());
    addGlobalVariable("selectOption", new SelectOption());
    addGlobalVariable("end", new End());
  }
  
  public static Output output = Output.DEBUG;
  public static String tabString = "";
  public enum Output {
    DEBUG,
    JS
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
  
  public static void executeCodeBlock(Function[] code) {
    Context context = new Context(null, null, currentParametersList.size());
    for(Function call : code) call.execute(context);
  }
  
  public static void stop(Context context) {
    Debugger.execute(context);
  }
  
  public static void runtimeError(String message) {
    JOptionPane.showMessageDialog(null, message, "Runtime error", JOptionPane.ERROR_MESSAGE);
    System.exit(1);
  }
  
  public static void parserError(String message) {
    JOptionPane.showMessageDialog(null, message, "Parsing error", JOptionPane.ERROR_MESSAGE);
    System.exit(1);
  }
}