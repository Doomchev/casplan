package casplan.object;

public class Range extends Function {
  @Override
  public int getPriority() {
    return 4;
  }
  
  
  
  @Override
  public Range toRange() {
    return this;
  }
}
