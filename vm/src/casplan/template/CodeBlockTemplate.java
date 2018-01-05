package casplan.template;

import java.util.LinkedList;

public class CodeBlockTemplate extends ObjectTemplate {
  public LinkedList<FunctionTemplate> code;

  public CodeBlockTemplate() {
    code = new LinkedList<>();
  }

  public CodeBlockTemplate(LinkedList<FunctionTemplate> code) {
    this.code = code;
  }

  @Override
  public String export() {
    return target.exportCode(this);
  }
}
