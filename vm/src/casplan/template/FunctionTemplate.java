package casplan.template;

import casplan.Parser;
import casplan.target.TargetFunction;

public class FunctionTemplate extends ObjectTemplate {
  public ObjectTemplate[] params;
  public int line, column, startingTextIndex, textLength;
  public boolean inBrackets = false;
  public Source source;
  public TargetFunction func;
  
  public FunctionTemplate() {
  }

  public FunctionTemplate(TargetFunction func) {
    this.func = func;
  }
  
  public FunctionTemplate(String id) {
    this.func = target.getFunc(id);
    if(this.func == null) {
      Parser.parserError("There's no \"" + id + "\" function in target");
    }
  }

  public FunctionTemplate(int paramsQuantity, TargetFunction func) {
    this.params = new ObjectTemplate[paramsQuantity];
    this.func = func;
  }
  
  public FunctionTemplate(int paramsQuantity, String id) {
    this(id);
    this.params = new ObjectTemplate[paramsQuantity];
  }
  
  @Override
  public FunctionTemplate toFunctionTemplate() {
    return this;
  }
  
  @Override
  public String export() {
    return func.export(this);
  }
}
