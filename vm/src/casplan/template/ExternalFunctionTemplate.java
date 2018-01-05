package casplan.template;

public class ExternalFunctionTemplate extends FunctionTemplate {
  public String address;

  public ExternalFunctionTemplate(String address) {
    this.address = address;
  }

  @Override
  public String export() {
    return target.exportExternalFunction(this);
  }
}
