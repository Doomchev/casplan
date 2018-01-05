package casplan.template;

public class StringTemplate extends ObjectTemplate {
  String value;

  public StringTemplate(String value) {
    this.value = value;
  }
  
  @Override
  public String export() {
    return target.exportString(value);
  }
}
