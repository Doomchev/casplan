package external.draw2d;

import static casplan.Base.graphics;
import casplan.object.CasObject;
import casplan.object.Context;
import casplan.object.Function;

public class DrawBox extends Function {
  @Override
  public Function execute(Context context, CasObject[] params) {
    graphics.fillRect(params[0].toInteger(context), params[1].toInteger(context)
        , params[2].toInteger(context), params[3].toInteger(context));
    return null;
  }

}
