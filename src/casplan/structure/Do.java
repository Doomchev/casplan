package casplan.structure;

import casplan.object.Context;
import casplan.object.Function;

public class Do extends Function {
  public Function[] code;

  @Override
  public Function execute(Context context) {
    while(true) {
      Function marker = executeCode(context, code, this);
      if(marker == Break.instance) return null;
    }
  }
}
