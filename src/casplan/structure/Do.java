package casplan.structure;

import casplan.object.Context;
import casplan.object.Function;

public class Do extends Function {
  Function[] code;

  public Do(Function[] code) {
    this.code = code;
  }
  
  @Override
  public Function execute(Context context) {
    while(true) {
      Function marker = executeCode(context, code);
      if(marker == Break.instance) return null;
    }
  }
}
