package casplan.template;

public class ParameterTemplate extends ObjectTemplate {
  public int index;
  public String name;
  public boolean isThis;
  public ObjectTemplate defaultValue;

  public ParameterTemplate(int index, String name, boolean isThis
      , ObjectTemplate defaultValue) {
    this.name = name;
    this.isThis = isThis;
    this.defaultValue = defaultValue;
  }
  
  @Override
  public String export() {
    return name;
  }
}
