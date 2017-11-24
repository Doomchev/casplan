package casplan.object;

public class Context {
  public Context(Context prevContext, CasObject parent, int paramsQuantity) {
    this.prevContext = prevContext;
    this.parent = parent;
    this.params = new CasObject[paramsQuantity];
  }
  
  public CasObject parent;
  public CasObject returnedValue;
  public CasObject[] params;
  public CasObject functionObject;
  public Function functionCall;
  public Context prevContext;
}
