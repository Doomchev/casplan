package casplan.structure;

import casplan.object.CasObject;
import casplan.object.Context;
import casplan.object.Function;

public class For extends Function {
  Function init, increment;
  CasObject condition;
  public Function[] code;

  public For(Function init, CasObject condition, Function increment
      , Function[] code) {
    this.init = init;
    this.increment = increment;
    this.code = code;
    this.condition = condition;
  }

  @Override
  public Function execute(Context context) {
    init.execute(context);
    while(condition.toBoolean(context)) {
      for(Function call : code) {
        Function marker = call.execute(context);
        if(marker == Return.instance) return Return.instance;
        if(marker == Continue.instance) break;
        if(marker == Break.instance) return null;
      }
      increment.execute(context);
    }
    return null;
  }
}
