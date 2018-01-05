package casplan.vm.integer;

import casplan.vm.Command;

public class IntSubtract extends Command {
  @Override
  public void execute() {
    intStack[intStackPos - 1] -= intStack[intStackPos];
    intStackPos--;
  }
}
