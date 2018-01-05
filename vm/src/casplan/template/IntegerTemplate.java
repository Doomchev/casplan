package casplan.template;

public class IntegerTemplate extends ObjectTemplate {
  public int value;

  public IntegerTemplate(int value) {
    this.value = value;
  }
  
  @Override
  public String export() {
    return target.exportInteger(value);
  }
}
