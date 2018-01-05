package casplan.template;

import java.util.HashMap;
import java.util.LinkedList;

public class UserFunctionTemplate extends FunctionTemplate {
  public String name = "";
  public CodeBlockTemplate codeBlock = null;
  public LinkedList<ParameterTemplate> parameters;
  public HashMap<String, ParameterTemplate> localVariables = new HashMap<>();

  public UserFunctionTemplate(LinkedList<ParameterTemplate> parameters) {
    this.parameters = parameters;
  }

  @Override
  public String export() {
    return target.exportUserFunction(this);
  }
}
