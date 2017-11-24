package casplan.structure;

import casplan.object.CasObject;
import casplan.object.Context;
import casplan.object.Function;
import casplan.object.Parameter;

public class ForIn extends Function {
  public Parameter value, index;
  public CasObject object;
  public Function[] code;

  public ForIn(Parameter valueVar, Parameter indexVar, CasObject object
      , Function[] code) {
    this.value = valueVar;
    this.index = indexVar;
    this.object = object;
    this.code = code;
  }

  @Override
  public Function execute(Context context) {
    return object.toValue(context).iterate(context, this);
  }
}
