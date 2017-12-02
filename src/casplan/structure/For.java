package casplan.structure;

import casplan.object.*;

public class For extends Function {
  public Function init, increment;
  public CasObject condition;
  public Function[] code;

  @Override
  public Function execute(Context context) {
    init.execute(context);
    main: while(condition.toBoolean(context)) {
      for(Function call : code) {
        if(call.breakpoint != BPType.NONE) call.stop(context);
        Function marker = call.execute(context);
        if(marker == Return.instance) return Return.instance;
        if(marker == Continue.instance) break;
        if(marker == Break.instance) break main;
      }
      increment.execute(context);
    }
    return null;
  }
  
  
  
  @Override
  public void setChildBreakpoint(Context context, Function func, BPType type) {
    setCodeBreakpoint(context, code, func, type, true);
  }
}
