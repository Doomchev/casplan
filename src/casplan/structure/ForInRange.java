package casplan.structure;

import casplan.object.Context;
import casplan.object.Function;
import casplan.object.Parameter;
import casplan.object.Range;
import casplan.value.CasInteger;

public class ForInRange extends Function {
  Range range;
  Parameter index;
  Function[] code;

  public ForInRange(Parameter index, Range range, Function[] code) {
    this.range = range;
    this.index = index;
    this.code = code;
  }  

  @Override
  public Function execute(Context context) {
    CasInteger i = new CasInteger(range.params[0].toInteger(context));
    index.setValue(context, i, this);
    int value2 = range.params[1].toInteger(context);
    while(i.value < value2) {
      for(Function call : code) {
        Function marker = call.execute(context);
        if(marker == Return.instance) return Return.instance;
        if(marker == Continue.instance) break;
        if(marker == Break.instance) return null;
      }
      i.value++;
    }
    return null;
  }
  
  

  @Override
  public String toString() {
    String varName = index.name;
    return "for(" + varName + " = " + range.params[0].toString() + "; "
        + varName + " < " + range.params[1].toString() + "; " + varName
        + "++) {\n" + codeToString(code) + tabString + "}";
  }
}
