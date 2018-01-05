package casplan.vm.integer;

import casplan.vm.Command;

public class IntParamInit extends Command {
  int value;

  public IntParamInit(int value) {
    this.value = value;
  }
  
  @Override
  public void execute() {
    currentContext.intStackPos = intStackPos - value + 1;
  }
}
