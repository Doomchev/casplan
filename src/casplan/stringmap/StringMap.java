package casplan.stringmap;

import casplan.object.CasObject;
import casplan.object.Context;
import casplan.object.Function;
import casplan.structure.Break;
import casplan.structure.Continue;
import casplan.structure.ForIn;
import casplan.structure.Return;
import casplan.value.CasString;
import java.util.HashMap;

public class StringMap extends CasObject {
  public HashMap<String, CasObject> entries = new HashMap<>();

  
  @Override
  public Function iterate(Context context, ForIn loop) {
    for(HashMap.Entry<String, CasObject> entry : entries.entrySet()) {
      if(loop.index != null) {
        loop.index.setValue(context, new CasString(entry.getKey()), loop);
      }
      if(loop.value != null) loop.value.setValue(context, entry.getValue(), loop);
      for(Function call : loop.code) {
        if(call.breakpoint != Function.BPType.NONE) call.stop(context);
        Function marker = call.execute(context);
        if(marker == Return.instance) return Return.instance;
        if(marker == Continue.instance) break;
        if(marker == Break.instance) return null;
      }
    }
    return null;
  }
}
