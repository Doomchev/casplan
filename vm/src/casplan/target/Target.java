package casplan.target;

import casplan.Parser;
import casplan.template.CodeBlockTemplate;
import casplan.template.ExternalFunctionTemplate;
import casplan.template.FunctionTemplate;
import casplan.template.ObjectTemplate;
import casplan.template.UserFunctionTemplate;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

public abstract class Target extends Parser {
  public static String tabString = "";
  
  public HashMap<String, TargetFunction> functions = new HashMap<>();
  public HashMap<String, ChunkFunction> chunkFunctions = new HashMap<>();
  public ObjectTemplate nullObject, thisObject;

  public Target() {
    nullObject = new ObjectTemplate() {
      @Override
      public String export() {
        return "null";
      }
    };
    thisObject = new ObjectTemplate() {
      @Override
      public String export() {
        return "this";
      }
    };
    
    functions.put("break", new StringTargetFunction("break"));
    functions.put("continue", new StringTargetFunction("continue"));

    functions.put("let", new TargetFunction() {
      @Override
      public String export(FunctionTemplate func) {
        return "let " + func.params[0].export() + " = "
            + func.params[1].export();
      }
    });
    functions.put("return", new TargetFunction() {
      @Override
      public String export(FunctionTemplate func) {
        return "return " + func.params[0].export();
      }
    });
    functions.put("do", new TargetFunction() {
      @Override
      public String export(FunctionTemplate func) {
        return "do " + func.params[0].export();
      }
    });
    functions.put("if", new TargetFunction() {
      @Override
      public String export(FunctionTemplate func) {
        ObjectTemplate elseCode = func.params[2];
        return "if(" + func.params[0].export() + ")" + func.params[1].export()
            + (elseCode == null ? "" : " else " + elseCode.export());
      }
    });
    functions.put("?", new TargetFunction() {
      @Override
      public String export(FunctionTemplate func) {
        return func.params[0].export() + " ? " + func.params[1].export() + " : "
            + func.params[2].export();
      }
    });
  }

  public TargetFunction getFunc(String id) {
    TargetFunction func = functions.get(id);
    if(func == null) {
      parserError("There's no \"" + id + "\" function in the target");
    }
    return func;
  }
  
  final void initFunc(String separator, int priority) {
    chunkFunctions.put(separator, new ChunkFunction(separator, priority));
  }
  
  final void separatorsInit() {
    chunkFunctions.put("[", new ChunkFunction("[", 17) {
      @Override
      public String export(FunctionTemplate func) {
        return func.params[0].export() + "[" + func.params[1].export() + "]";
      }
    });
    chunkFunctions.put("(", new ChunkFunction("(", 17) {
      @Override
      public String export(FunctionTemplate func) {
        return func.params[0].export() + "(" + func.params[1].export() + ")";
      }
    });
    
    initFunc(".", 17);
    initFunc("*", 14);
    initFunc("/", 14);
    initFunc("+", 13);
    initFunc("-", 13);
    initFunc(">=", 8);
    initFunc("<=", 8);
    initFunc("<", 8);
    initFunc(">", 8);
    initFunc("==", 7);
    initFunc("!=", 7);
    initFunc("&&", 6);
    initFunc("..<", 4);
    initFunc("=", 3);
    initFunc("++", 3);
    
    initFunc("?", -1);
    initFunc(":", -1);
    
    functions.put("=", chunkFunctions.get("="));
    
    functions.put("params", new TargetFunction() {
      @Override
      public String export(FunctionTemplate func) {
        String str = "";
        for(ObjectTemplate object : func.params) {
          if(!str.isEmpty()) str += ", ";
          str += object.export();
        }
        return str;
      }      
    });
  }

  public void compile(String source, String output) {
    Parser.importModule(source);
    
    try {
      PrintWriter writer = new PrintWriter(output, "UTF-8");
      writer.print(getHeader());
      
      for(UserFunctionTemplate func : userFunctions.values()) {
        writer.println(tabString + target.exportUserFunction(func));
      }
      
      for(UserFunctionTemplate module : modules) {
        for(FunctionTemplate func : module.codeBlock.code) {
          writer.println(func.export());
        }
      }
      
      writer.print(getFooter());
      writer.close();
    } catch (FileNotFoundException ex) {
      parserError("Cannot write to " + output);
    } catch (UnsupportedEncodingException ex) {
      parserError("Unsupported encoding");
    }
  }

  public String getHeader() {
    return "";
  }
  
  public String getFooter() {
    return "";
  }

  public abstract String exportCode(CodeBlockTemplate codeBlock);

  public String exportInteger(int value) {
    return String.valueOf(value);
  }

  public String exportString(String value) {
    return "\"" + value + "\"";
  }
  
  public abstract String exportUserFunction(UserFunctionTemplate func);

  public String exportExternalFunction(ExternalFunctionTemplate func) {
    return "external." + func.address;
  }

  public static class ChunkFunction extends TargetFunction {
    public String separator;
    public int priority;

    public ChunkFunction(String separator, int priority) {
      this.separator = separator;
      this.priority = priority;
    }

    @Override
    public int getPriority() {
      return priority;
    }
    
    public FunctionTemplate create() {
      return new FunctionTemplate(this);
    }

    @Override
    public String export(FunctionTemplate func) {
      return func.params[0].export() + " " + separator + " "
          + func.params[1].export();
    }
  }
  
  public static class StringTargetFunction extends TargetFunction {
    String string;

    public StringTargetFunction(String string) {
      this.string = string;
    }

    @Override
    public String export(FunctionTemplate func) {
      return string;
    }
  }
}
