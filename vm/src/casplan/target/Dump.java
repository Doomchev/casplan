package casplan.target;

import casplan.template.CodeBlockTemplate;
import casplan.template.FunctionTemplate;
import casplan.template.ParameterTemplate;
import casplan.template.UserFunctionTemplate;

public class Dump extends Target {
  public Dump() {
    separatorsInit();
  }

  @Override
  public String exportUserFunction(UserFunctionTemplate func) {
    String str = "";
    for(ParameterTemplate parameter : func.parameters) {
      if(!str.isEmpty()) str += ", ";
      if(parameter.isThis) str += "this.";
      str += parameter.name;
      if(parameter.defaultValue != null) str += " = "
          + parameter.defaultValue.export();
    }
    return func.name + "(" + str + ") " + exportCode(func.codeBlock);
  }

  @Override
  public String exportCode(CodeBlockTemplate codeBlock) {
    String str = "{\n";
    tabString += "\t";
    for(FunctionTemplate call : codeBlock.code) {
      if(call.func == null) stop();
      str += tabString + call.func.export(call) + "\n";
    }
    tabString = tabString.substring(1);
    return str + tabString + "}";
  }
}
