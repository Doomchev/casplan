package external.draw2d;

import casplan.object.CasObject;
import casplan.object.Context;
import casplan.object.Function;

public class DrawLine extends Function {
  @Override
  public Function execute(Context context, CasObject[] params) {
    graphics.drawLine(params[0].toInteger(context), params[1].toInteger(context)
        , params[2].toInteger(context), params[3].toInteger(context));
    return null;
  }
}
