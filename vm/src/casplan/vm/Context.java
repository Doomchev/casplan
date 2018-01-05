package casplan.vm;

public class Context {
  public int boolStackPos, intStackPos, stringStackPos;
  public int[] intVars;
  public Command nextCommand;
  public Context parentContext;
  
  public Context() {
    parentContext = Base.currentContext;
    nextCommand = Base.currentCommand;
  }
}
