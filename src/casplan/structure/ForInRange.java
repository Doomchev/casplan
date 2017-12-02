package casplan.structure;

import casplan.object.*;
import casplan.value.CasInteger;

public class ForInRange extends Function {
  Range range;
  Parameter index;
  public Function[] code;

  public ForInRange(Parameter index, Range range) {
    this.range = range;
    this.index = index;
  }  

  @Override
  public Function execute(Context context) {
    CasInteger i = new CasInteger(range.params[0].toInteger(context));
    index.setValue(context, i, this);
    int value2 = range.params[1].toInteger(context);
    main: while(i.value < value2) {
      for(Function call : code) {
        if(call.breakpoint != BPType.NONE) call.stop(context);
        Function marker = call.execute(context);
        if(marker == Return.instance) return Return.instance;
        if(marker == Continue.instance) break;
        if(marker == Break.instance) break main;
      }
      i.value++;
    }
    if(code[0].breakpoint != BPType.NONE) {
      code[0].breakpoint = BPType.NONE;
      super.setBreakpointInsideParent(context, BPType.STEP);
    }
    return null;
  }

  

  @Override
  public void setNextBreakpoint(Context context, BPType type) {
    if(code.length == 0) {
      super.setNextBreakpoint(context, type);
    } else {
      code[0].setBreakpoint(BPType.STEP_INTO);
    }
  }
  
  @Override
  public void setChildBreakpoint(Context context, Function func, BPType type) {
    setCodeBreakpoint(context, code, func, type, true);
  }
  
  

  @Override
  public String toString() {
    String varName = index.name;
    return "for(" + varName + " = " + range.params[0].toString() + "; "
        + varName + " < " + range.params[1].toString() + "; " + varName
        + "++) {\n" + codeToString(code) + tabString + "}";
  }
}
