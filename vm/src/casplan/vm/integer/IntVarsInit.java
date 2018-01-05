package casplan.vm.integer;

import casplan.vm.Command;

public class IntVarsInit extends Command {
  int quantity;

  public IntVarsInit(int quantity) {
    this.quantity = quantity;
  }
  
  @Override
  public void execute() {
    currentContext.intVars = new int[quantity];
  }
}
